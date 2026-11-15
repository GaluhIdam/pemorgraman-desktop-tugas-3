package id.ac.restoran;

public abstract class MenuItem {
    private final int id;
    private final String nama;
    private final double harga;
    private final String kategori;

    protected MenuItem(int id, String nama, double harga, String kategori) {
        if (id <= 0) {
            throw new IllegalArgumentException("ID item harus lebih dari nol.");
        }
        if (nama == null || nama.isBlank()) {
            throw new IllegalArgumentException("Nama item tidak boleh kosong.");
        }
        if (!Double.isFinite(harga) || harga < 0) {
            throw new IllegalArgumentException("Harga harus berupa angka yang valid dan tidak negatif.");
        }
        if (kategori == null || kategori.isBlank()) {
            throw new IllegalArgumentException("Kategori tidak boleh kosong.");
        }
        this.id = id;
        this.nama = nama.trim();
        this.harga = harga;
        this.kategori = kategori.trim();
    }

    public int getId() { return id; }
    public String getNama() { return nama; }
    public double getHarga() { return harga; }
    public String getKategori() { return kategori; }

    /** Polymorphism: setiap subkelas menampilkan informasinya sendiri. */
    public abstract void tampilMenu();
}
