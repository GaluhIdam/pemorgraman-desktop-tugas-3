package id.ac.restoran;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

public final class StrukFileService {
    private static final DateTimeFormatter FORMAT_NAMA = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss");
    private final Path folder;

    public StrukFileService(Path folder) {
        this.folder = folder.toAbsolutePath().normalize();
    }

    public Path simpan(Pesanan pesanan) throws IOException {
        if (pesanan == null || pesanan.kosong()) {
            throw new IllegalStateException("Tidak ada pesanan untuk disimpan.");
        }
        Files.createDirectories(folder);
        String nama = "struk-" + pesanan.getWaktuPesanan().format(FORMAT_NAMA)
                + "-" + pesanan.getIdPesanan() + ".txt";
        Path tujuan = folder.resolve(nama);
        Files.writeString(tujuan, pesanan.buatStruk(), StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        return tujuan;
    }

    public List<Path> daftarStruk() throws IOException {
        if (Files.notExists(folder)) {
            return List.of();
        }
        try (Stream<Path> isi = Files.list(folder)) {
            return isi.filter(Files::isRegularFile)
                    .filter(path -> path.getFileName().toString().matches("struk-.*\\.txt"))
                    .sorted(Comparator.comparing((Path p) -> p.getFileName().toString()).reversed())
                    .toList();
        }
    }

    public String baca(Path berkas) throws IOException {
        Path normal = berkas.toAbsolutePath().normalize();
        if (!folder.equals(normal.getParent()) || !normal.getFileName().toString().matches("struk-.*\\.txt")) {
            throw new IOException("Berkas struk di luar direktori yang diizinkan.");
        }
        return Files.readString(normal, StandardCharsets.UTF_8);
    }
}
