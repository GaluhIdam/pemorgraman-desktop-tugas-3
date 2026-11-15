package id.ac.restoran;

public final class Minuman extends MenuItem {
    private final String jenisMinuman;

    public Minuman(int id, String nama, double harga, String jenisMinuman) {
        super(id, nama, harga, "Minuman");
        if (harga <= 0) {
            throw new IllegalArgumentException("Harga minuman harus lebih dari nol.");
        }
        if (jenisMinuman == null || jenisMinuman.isBlank()) {
            throw new IllegalArgumentException("Jenis minuman tidak boleh kosong.");
        }
        this.jenisMinuman = jenisMinuman.trim();
    }

    public String getJenisMinuman() { return jenisMinuman; }

    @Override
    public void tampilMenu() {
        System.out.printf("%-4d %-25s %-13s %-16s %s%n",
                getId(), getNama(), FormatUtil.rupiah(getHarga()), getKategori(), jenisMinuman);
    }
}
