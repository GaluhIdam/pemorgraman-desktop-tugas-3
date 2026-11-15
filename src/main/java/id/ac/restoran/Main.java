package id.ac.restoran;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Scanner;

public final class Main {
    private final Scanner scanner = new Scanner(System.in);
    private final MenuFileService menuFileService = new MenuFileService();
    private final StrukFileService strukFileService = new StrukFileService(Path.of("data", "struk"));
    private final Path fileMenu = Path.of("data", "menu.txt");
    private Menu menu;
    private Pesanan pesanan;

    public static void main(String[] args) {
        new Main().jalankan();
    }

    private void jalankan() {
        inisialisasi();
        System.out.println("\nSelamat datang di Aplikasi Manajemen Restoran (Tugas Praktik 3).");
        boolean berjalan = true;
        while (berjalan) {
            tampilMenuUtama();
            int pilihan = bacaInt("Pilih menu utama: ");
            try {
                switch (pilihan) {
                    case 1 -> tambahItemMenu();
                    case 2 -> menu.tampilkanSemua();
                    case 3 -> buatAtauUbahPesanan();
                    case 4 -> tampilkanPerhitungan();
                    case 5 -> tampilkanStruk();
                    case 6 -> simpanStruk();
                    case 7 -> muatStruk();
                    case 8 -> simpanMenu();
                    case 9 -> muatMenu();
                    case 10 -> pesananBaru();
                    case 0 -> {
                        System.out.println("Terima kasih. Program selesai.");
                        berjalan = false;
                    }
                    default -> System.out.println("Pilihan tidak tersedia. Masukkan angka 0-10.");
                }
            } catch (MenuItemTidakDitemukanException | IllegalArgumentException | IllegalStateException e) {
                System.out.println("[PERINGATAN] " + e.getMessage());
            } catch (IOException e) {
                System.out.println("[GAGAL FILE] " + e.getMessage());
            }
        }
    }

    private void inisialisasi() {
        if (Files.exists(fileMenu)) {
            try {
                menu = menuFileService.muat(fileMenu);
                System.out.println("Daftar menu berhasil dimuat dari " + fileMenu);
            } catch (IOException e) {
                System.out.println("[PERINGATAN] Tidak bisa membaca file menu: " + e.getMessage());
                System.out.println("Data pada file TIDAK diubah. Gunakan opsi 9 setelah file diperbaiki.");
                menu = new Menu();
            }
        } else {
            menu = Menu.dataContoh();
            try {
                menuFileService.simpan(menu, fileMenu);
                System.out.println("Menu awal dibuat pada " + fileMenu);
            } catch (IOException e) {
                System.out.println("[PERINGATAN] Menu contoh aktif, tetapi gagal disimpan: " + e.getMessage());
            }
        }
    }

    private void tampilMenuUtama() {
        System.out.println("\n========== MENU UTAMA ==========");
        System.out.println("1. Tambah item menu / promo diskon");
        System.out.println("2. Tampilkan daftar menu restoran");
        System.out.println("3. Buat / tambah / ubah pesanan");
        System.out.println("4. Hitung total biaya pesanan");
        System.out.println("5. Tampilkan struk pesanan aktif");
        System.out.println("6. Simpan struk ke file teks");
        System.out.println("7. Buka struk tersimpan dari file");
        System.out.println("8. Simpan daftar menu ke file teks");
        System.out.println("9. Muat ulang daftar menu dari file");
        System.out.println("10. Buat pesanan baru (reset)");
        System.out.println("0. Keluar");
        System.out.println("================================");
    }

    private void tambahItemMenu() throws IOException {
        System.out.println("\nTAMBAH ITEM: 1=Makanan, 2=Minuman, 3=Diskon, 0=Batal");
        int jenis = bacaInt("Pilih jenis: ");
        if (jenis == 0) return;
        if (jenis < 1 || jenis > 3) {
            System.out.println("Jenis item tidak tersedia.");
            return;
        }
        String nama = bacaTeks("Nama item / promo: ");
        int id = menu.idBerikutnya();
        MenuItem item;
        if (jenis == 1) {
            double harga = bacaDouble("Harga makanan (Rp): ", 0.01, Double.MAX_VALUE);
            String jenisMakanan = bacaTeks("Jenis makanan (contoh: Makanan utama): ");
            item = new Makanan(id, nama, harga, jenisMakanan);
        } else if (jenis == 2) {
            double harga = bacaDouble("Harga minuman (Rp): ", 0.01, Double.MAX_VALUE);
            String jenisMinuman = bacaTeks("Jenis minuman (contoh: Dingin): ");
            item = new Minuman(id, nama, harga, jenisMinuman);
        } else {
            double diskon = bacaDouble("Persentase diskon (0-100): ", 0, 100);
            item = new Diskon(id, nama, diskon);
        }
        menu.tambahItem(item);
        System.out.println("Berhasil menambahkan " + item.getNama() + " (ID " + id + ").");
        menuFileService.simpan(menu, fileMenu);
        System.out.println("Menu otomatis tersimpan di " + fileMenu);
    }

    private void buatAtauUbahPesanan() {
        if (menu.getDaftarItem().stream().noneMatch(item -> !(item instanceof Diskon))) {
            System.out.println("Belum ada makanan / minuman yang dapat dipesan.");
            return;
        }
        if (pesanan == null) {
            pesanan = new Pesanan();
            System.out.println("Pesanan baru dibuat (ID " + pesanan.getIdPesanan() + ").");
        }
        boolean mengisi = true;
        while (mengisi) {
            menu.tampilkanSemua();
            System.out.println("Masukkan ID makanan/minuman untuk memesan.");
            System.out.println("-1 = Pilih promo diskon, -2 = Hapus diskon, -3 = Hapus item pesanan, 0 = Selesai");
            int pilihan = bacaInt("Pilihan / ID: ");
            try {
                switch (pilihan) {
                    case 0 -> mengisi = false;
                    case -1 -> pilihDiskon();
                    case -2 -> {
                        pesanan.terapkanDiskon(null);
                        System.out.println("Diskon dihapus dari pesanan.");
                    }
                    case -3 -> {
                        int idDihapus = bacaInt("ID item yang akan dihapus dari pesanan: ");
                        pesanan.hapusItem(idDihapus);
                        System.out.println("Item berhasil dihapus dari pesanan.");
                    }
                    default -> {
                        MenuItem item = menu.cariBerdasarkanId(pilihan);
                        if (item instanceof Diskon) {
                            System.out.println("Item tersebut promo. Gunakan -1 untuk menerapkan diskon.");
                            break;
                        }
                        int jumlah = bacaInt("Jumlah pesanan: ");
                        pesanan.tambahItem(item, jumlah);
                        System.out.println("Ditambahkan: " + jumlah + " x " + item.getNama());
                    }
                }
            } catch (MenuItemTidakDitemukanException | IllegalArgumentException e) {
                System.out.println("[PERINGATAN] " + e.getMessage());
            }
        }
        if (pesanan.kosong()) {
            System.out.println("Pesanan belum memiliki item.");
        } else {
            tampilkanPerhitungan();
        }
    }

    private void pilihDiskon() {
        System.out.println("\nPROMO YANG TERSEDIA:");
        boolean ada = false;
        for (MenuItem item : menu.getDaftarItem()) {
            if (item instanceof Diskon) {
                item.tampilMenu();
                ada = true;
            }
        }
        if (!ada) {
            System.out.println("Belum ada promo diskon.");
            return;
        }
        int id = bacaInt("Pilih ID promo: ");
        MenuItem item = menu.cariBerdasarkanId(id);
        if (!(item instanceof Diskon diskon)) {
            throw new IllegalArgumentException("ID tersebut bukan diskon.");
        }
        pesanan.terapkanDiskon(diskon);
        System.out.println("Promo " + diskon.getNama() + " berhasil diterapkan.");
    }

    private void tampilkanPerhitungan() {
        if (pesanan == null || pesanan.kosong()) {
            System.out.println("Belum ada pesanan yang dapat dihitung.");
            return;
        }
        System.out.println("\n----- RINGKASAN BIAYA -----");
        System.out.println("Subtotal : " + FormatUtil.rupiah(pesanan.hitungSubtotal()));
        System.out.println("Diskon   : " + (pesanan.getDiskon() == null ? "Tidak ada"
                : pesanan.getDiskon().getNama() + " (" + FormatUtil.persen(pesanan.getDiskon().getDiskon()) + ")"));
        System.out.println("Potongan : -" + FormatUtil.rupiah(pesanan.hitungPotonganDiskon()));
        System.out.println("TOTAL    : " + FormatUtil.rupiah(pesanan.hitungTotal()));
        System.out.println("---------------------------");
    }

    private void tampilkanStruk() {
        if (pesanan == null || pesanan.kosong()) {
            System.out.println("Belum ada pesanan yang dapat dibuatkan struk.");
            return;
        }
        System.out.println("\n" + pesanan.buatStruk());
    }

    private void simpanStruk() throws IOException {
        if (pesanan == null || pesanan.kosong()) {
            System.out.println("Belum ada pesanan yang dapat disimpan.");
            return;
        }
        Path tersimpan = strukFileService.simpan(pesanan);
        System.out.println("Struk tersimpan: " + tersimpan);
    }

    private void muatStruk() throws IOException {
        List<Path> files = strukFileService.daftarStruk();
        if (files.isEmpty()) {
            System.out.println("Belum ada struk tersimpan dalam folder data/struk.");
            return;
        }
        System.out.println("\nSTRUK TERSIMPAN:");
        for (int i = 0; i < files.size(); i++) {
            System.out.println((i + 1) + ". " + files.get(i).getFileName());
        }
        int pilih = bacaInt("Nomor struk (0=Batal): ");
        if (pilih == 0) return;
        if (pilih < 1 || pilih > files.size()) {
            System.out.println("Nomor struk tidak tersedia.");
            return;
        }
        System.out.println("\n" + strukFileService.baca(files.get(pilih - 1)));
    }

    private void simpanMenu() throws IOException {
        menuFileService.simpan(menu, fileMenu);
        System.out.println("Daftar menu berhasil disimpan ke " + fileMenu);
    }

    private void muatMenu() throws IOException {
        Menu dimuat = menuFileService.muat(fileMenu);
        menu = dimuat;
        pesanan = null;
        System.out.println("Daftar menu berhasil dimuat ulang (" + menu.getDaftarItem().size()
                + " item). Pesanan aktif direset agar tetap konsisten.");
    }

    private void pesananBaru() {
        pesanan = new Pesanan();
        System.out.println("Pesanan baru siap digunakan (ID " + pesanan.getIdPesanan() + ").");
    }

    private String bacaTeks(String pesan) {
        while (true) {
            System.out.print(pesan);
            String nilai = scanner.nextLine().trim();
            if (!nilai.isBlank()) return nilai;
            System.out.println("Input tidak boleh kosong.");
        }
    }

    private int bacaInt(String pesan) {
        while (true) {
            System.out.print(pesan);
            String nilai = scanner.nextLine().trim();
            try {
                return Integer.parseInt(nilai);
            } catch (NumberFormatException e) {
                System.out.println("Input harus berupa bilangan bulat yang valid.");
            }
        }
    }

    private double bacaDouble(String pesan, double minimal, double maksimal) {
        while (true) {
            System.out.print(pesan);
            String nilai = scanner.nextLine().trim().replace(',', '.');
            try {
                double hasil = Double.parseDouble(nilai);
                if (Double.isFinite(hasil) && hasil >= minimal && hasil <= maksimal) {
                    return hasil;
                }
            } catch (NumberFormatException ignored) {

            }
            System.out.printf("Masukkan angka valid antara %s dan %s.%n", minimal, maksimal);
        }
    }
}
