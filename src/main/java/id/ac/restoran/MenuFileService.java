package id.ac.restoran;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

public final class MenuFileService {
    public static final String HEADER = "# FORMAT_MENU_RESTORAN_V1";

    public void simpan(Menu menu, Path tujuan) throws IOException {
        Path path = tujuan.toAbsolutePath().normalize();
        Files.createDirectories(path.getParent());
        List<String> baris = new ArrayList<>();
        baris.add(HEADER);
        baris.add("# tipe|id|nama|harga|kategori|jenis_atau_persentase_diskon");
        for (MenuItem item : menu.getDaftarItem()) {
            String tipe;
            String detail;
            if (item instanceof Makanan makanan) {
                tipe = "MAKANAN";
                detail = makanan.getJenisMakanan();
            } else if (item instanceof Minuman minuman) {
                tipe = "MINUMAN";
                detail = minuman.getJenisMinuman();
            } else if (item instanceof Diskon diskon) {
                tipe = "DISKON";
                detail = Double.toString(diskon.getDiskon());
            } else {
                throw new IOException("Tipe item menu tidak dikenal: " + item.getClass().getName());
            }
            baris.add(String.join("|", tipe, Integer.toString(item.getId()),
                    escape(item.getNama()), Double.toString(item.getHarga()),
                    escape(item.getKategori()), escape(detail)));
        }

        // Simpan ke berkas sementara dahulu agar data lama tidak terpotong jika gagal.
        Path sementara = Files.createTempFile(path.getParent(), "menu-", ".tmp");
        try {
            Files.write(sementara, baris, StandardCharsets.UTF_8);
            try {
                Files.move(sementara, path, StandardCopyOption.ATOMIC_MOVE,
                        StandardCopyOption.REPLACE_EXISTING);
            } catch (AtomicMoveNotSupportedException ignored) {
                Files.move(sementara, path, StandardCopyOption.REPLACE_EXISTING);
            }
        } finally {
            Files.deleteIfExists(sementara);
        }
    }

    public Menu muat(Path sumber) throws IOException {
        List<String> baris = Files.readAllLines(sumber, StandardCharsets.UTF_8);
        if (baris.isEmpty() || !HEADER.equals(baris.get(0).trim())) {
            throw new IOException("Header file menu tidak valid atau tidak didukung.");
        }
        Menu hasil = new Menu();
        for (int i = 1; i < baris.size(); i++) {
            String teks = baris.get(i);
            if (teks.isBlank() || teks.startsWith("#")) {
                continue;
            }
            try {
                List<String> bagian = splitEscaped(teks);
                if (bagian.size() != 6) {
                    throw new IllegalArgumentException("Harus berisi 6 kolom.");
                }
                String tipe = bagian.get(0);
                int id = Integer.parseInt(bagian.get(1));
                String nama = bagian.get(2);
                double harga = Double.parseDouble(bagian.get(3));
                String kategori = bagian.get(4);
                String detail = bagian.get(5);
                MenuItem item;
                switch (tipe) {
                    case "MAKANAN" -> {
                        periksaKategori(kategori, "Makanan");
                        item = new Makanan(id, nama, harga, detail);
                    }
                    case "MINUMAN" -> {
                        periksaKategori(kategori, "Minuman");
                        item = new Minuman(id, nama, harga, detail);
                    }
                    case "DISKON" -> {
                        periksaKategori(kategori, "Diskon");
                        if (harga != 0) {
                            throw new IllegalArgumentException("Harga promo harus nol.");
                        }
                        item = new Diskon(id, nama, Double.parseDouble(detail));
                    }
                    default -> throw new IllegalArgumentException("Tipe item tidak dikenal: " + tipe);
                }
                hasil.tambahItem(item);
            } catch (IllegalArgumentException ex) {
                throw new IOException("Format file menu salah di baris " + (i + 1) + ": " + ex.getMessage(), ex);
            }
        }
        return hasil; // Hanya dikembalikan jika semua baris berhasil dimuat.
    }

    private void periksaKategori(String aktual, String seharusnya) {
        if (!seharusnya.equals(aktual)) {
            throw new IllegalArgumentException("Kategori tidak sesuai tipe " + seharusnya + ".");
        }
    }

    /** Menghindari konflik delimiter dengan escape \|, \\ dan \n. */
    private String escape(String nilai) {
        return nilai.replace("\\", "\\\\").replace("|", "\\|")
                .replace("\n", "\\n").replace("\r", "\\r");
    }

    private List<String> splitEscaped(String baris) {
        List<String> hasil = new ArrayList<>();
        StringBuilder kolom = new StringBuilder();
        boolean escape = false;
        for (char c : baris.toCharArray()) {
            if (escape) {
                switch (c) {
                    case '|', '\\' -> kolom.append(c);
                    case 'n' -> kolom.append('\n');
                    case 'r' -> kolom.append('\r');
                    default -> throw new IllegalArgumentException("Escape tidak dikenal: \\" + c);
                }
                escape = false;
            } else if (c == '\\') {
                escape = true;
            } else if (c == '|') {
                hasil.add(kolom.toString());
                kolom.setLength(0);
            } else {
                kolom.append(c);
            }
        }
        if (escape) {
            throw new IllegalArgumentException("Karakter escape tidak lengkap.");
        }
        hasil.add(kolom.toString());
        return hasil;
    }
}
