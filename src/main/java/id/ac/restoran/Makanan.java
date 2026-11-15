package id.ac.restoran;

public final class Makanan extends MenuItem {
    private final String jenisMakanan;

    public Makanan(int id, String nama, double harga, String jenisMakanan) {
        super(id, nama, harga, "Makanan");
        if (harga <= 0) {
            throw new IllegalArgumentException("Harga makanan harus lebih dari nol.");
        }
        if (jenisMakanan == null || jenisMakanan.isBlank()) {
            throw new IllegalArgumentException("Jenis makanan tidak boleh kosong.");
        }
        this.jenisMakanan = jenisMakanan.trim();
    }

    public String getJenisMakanan() { return jenisMakanan; }

    @Override
    public void tampilMenu() {
        System.out.printf("%-4d %-25s %-13s %-16s %s%n",
                getId(), getNama(), FormatUtil.rupiah(getHarga()), getKategori(), jenisMakanan);
    }
}
