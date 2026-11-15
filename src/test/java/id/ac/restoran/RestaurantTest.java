package id.ac.restoran;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/** Pengujian otomatis tanpa library eksternal (jalankan dengan -ea). */
public final class RestaurantTest {
    private static int jumlahLulus = 0;

    public static void main(String[] args) throws IOException {
        ujiInheritanceDanPolymorphism();
        ujiPerhitunganPesananDanDiskon();
        ujiValidasiDanException();
        ujiSimpanDanMuatMenu();
        ujiSimpanDanBacaStruk();
        System.out.println("SEMUA PENGUJIAN LULUS: " + jumlahLulus + " skenario.");
    }

    private static void ujiInheritanceDanPolymorphism() {
        MenuItem a = new Makanan(1, "Ayam", 20000, "Utama");
        MenuItem b = new Minuman(2, "Teh", 5000, "Dingin");
        MenuItem c = new Diskon(3, "Promo", 12.5);
        periksa(a instanceof MenuItem && b instanceof MenuItem && c instanceof MenuItem,
                "Semua item harus mewarisi MenuItem");
        a.tampilMenu();
        b.tampilMenu();
        c.tampilMenu();
        lulus("Inheritance dan polymorphism");
    }

    private static void ujiPerhitunganPesananDanDiskon() {
        Pesanan pesanan = new Pesanan();
        Makanan nasi = new Makanan(1, "Nasi", 25000, "Utama");
        Minuman teh = new Minuman(2, "Teh", 10000, "Dingin");
        pesanan.tambahItem(nasi, 2);
        pesanan.tambahItem(teh, 1);
        pesanan.tambahItem(nasi, 1); // Item sama digabung, kuantitas bertambah.
        periksa(pesanan.getDaftarItem().size() == 2, "Baris item ganda seharusnya digabung");
        periksa(pesanan.getDaftarItem().get(0).getJumlah() == 3, "Jumlah nasi seharusnya 3");
        periksaMendekati(pesanan.hitungSubtotal(), 85000, "Subtotal");
        pesanan.terapkanDiskon(new Diskon(3, "Promo", 12.5));
        periksaMendekati(pesanan.hitungPotonganDiskon(), 10625, "Potongan");
        periksaMendekati(pesanan.hitungTotal(), 74375, "Total akhir");
        periksa(pesanan.buatStruk().contains("12,5%"), "Persentase diskon harus terbaca pada struk");
        pesanan.terapkanDiskon(null);
        periksaMendekati(pesanan.hitungTotal(), 85000, "Total tanpa diskon");
        pesanan.hapusItem(2);
        periksaMendekati(pesanan.hitungTotal(), 75000, "Total setelah hapus item");
        lulus("Perhitungan, gabung kuantitas, diskon, hapus item");
    }

    private static void ujiValidasiDanException() {
        Menu menu = new Menu();
        menu.tambahItem(new Makanan(1, "Nasi", 20000, "Utama"));
        harapException(MenuItemTidakDitemukanException.class, () -> menu.cariBerdasarkanId(9));
        harapException(IllegalArgumentException.class,
                () -> menu.tambahItem(new Minuman(1, "Teh", 5000, "Dingin")));
        harapException(IllegalArgumentException.class, () -> new Diskon(2, "Salah", 110));
        harapException(IllegalArgumentException.class, () -> new Makanan(3, "Salah", -50, "Utama"));
        harapException(IllegalArgumentException.class,
                () -> new Pesanan().tambahItem(new Diskon(4, "Promo", 10), 1));
        harapException(IllegalArgumentException.class,
                () -> new Pesanan().tambahItem(new Makanan(5, "Nasi", 10000, "Utama"), 0));
        harapException(UnsupportedOperationException.class,
                () -> menu.getDaftarItem().clear());
        lulus("Exception, validasi input, encapsulation");
    }

    private static void ujiSimpanDanMuatMenu() throws IOException {
        Path folder = Files.createTempDirectory("uji-menu-restoran-");
        try {
            Path file = folder.resolve("menu.txt");
            Menu menu = new Menu();
            menu.tambahItem(new Makanan(1, "Nasi | Ayam", 30000, "Utama\\Spesial"));
            menu.tambahItem(new Minuman(2, "Jus Alpukat", 15000, "Jus buah"));
            menu.tambahItem(new Diskon(3, "Promo", 12.5));
            MenuFileService service = new MenuFileService();
            service.simpan(menu, file);
            Menu dimuat = service.muat(file);
            periksa(dimuat.getDaftarItem().size() == 3, "Jumlah item hasil load");
            periksa(dimuat.cariBerdasarkanId(1).getNama().equals("Nasi | Ayam"), "Karakter delimiter | harus utuh");
            periksa(((Makanan) dimuat.cariBerdasarkanId(1)).getJenisMakanan().equals("Utama\\Spesial"),
                    "Karakter backslash harus utuh");
            periksaMendekati(((Diskon) dimuat.cariBerdasarkanId(3)).getDiskon(), 12.5, "Diskon hasil load");
            Files.writeString(file, "header salah\n", StandardCharsets.UTF_8);
            harapException(IOException.class, () -> service.muat(file));
            Files.write(file, List.of(MenuFileService.HEADER, "MAKANAN|1|Nasi|10000|Makanan|Utama",
                    "MINUMAN|1|Teh|5000|Minuman|Dingin"), StandardCharsets.UTF_8);
            harapException(IOException.class, () -> service.muat(file));
            lulus("Operasi simpan/muat menu, karakter escape, validasi file rusak");
        } finally {
            Files.deleteIfExists(folder.resolve("menu.txt"));
            Files.deleteIfExists(folder);
        }
    }

    private static void ujiSimpanDanBacaStruk() throws IOException {
        Path folder = Files.createTempDirectory("uji-struk-restoran-");
        try {
            StrukFileService service = new StrukFileService(folder);
            Pesanan pesanan = new Pesanan();
            pesanan.tambahItem(new Makanan(1, "Ayam Bakar", 32000, "Utama"), 2);
            pesanan.terapkanDiskon(new Diskon(2, "Promo", 10));
            Path file = service.simpan(pesanan);
            periksa(Files.exists(file), "File struk seharusnya dibuat");
            periksa(service.daftarStruk().size() == 1, "Daftar struk seharusnya berisi satu file");
            String isi = service.baca(file);
            periksa(isi.contains("Ayam Bakar") && isi.contains("TOTAL")
                    && isi.contains("Promo"), "Isi struk harus lengkap");
            harapException(IOException.class, () -> service.baca(folder.resolve("../file-lain.txt")));
            lulus("Simpan, daftar, baca struk, validasi akses file");
        } finally {
            try (var isi = Files.list(folder)) {
                isi.forEach(path -> {
                    try { Files.delete(path); } catch (IOException e) { throw new RuntimeException(e); }
                });
            }
            Files.delete(folder);
        }
    }

    private static void periksa(boolean kondisi, String pesan) {
        if (!kondisi) throw new AssertionError(pesan);
    }

    private static void periksaMendekati(double aktual, double harapan, String pesan) {
        periksa(Math.abs(aktual - harapan) < 0.001,
                pesan + ": diharapkan " + harapan + " tetapi didapat " + aktual);
    }

    private static void harapException(Class<? extends Throwable> jenis, Aksi aksi) {
        try {
            aksi.jalankan();
        } catch (Throwable error) {
            if (jenis.isInstance(error)) return;
            throw new AssertionError("Jenis exception salah: " + error, error);
        }
        throw new AssertionError("Seharusnya ada exception " + jenis.getSimpleName());
    }

    private static void lulus(String skenario) {
        jumlahLulus++;
        System.out.println("[LULUS] " + skenario);
    }

    @FunctionalInterface
    private interface Aksi {
        void jalankan() throws Exception;
    }
}
