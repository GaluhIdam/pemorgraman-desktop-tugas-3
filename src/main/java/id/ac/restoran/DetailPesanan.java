package id.ac.restoran;

import java.util.Objects;

public final class DetailPesanan {
    private final MenuItem item;
    private int jumlah;

    public DetailPesanan(MenuItem item, int jumlah) {
        this.item = Objects.requireNonNull(item, "Item tidak boleh null.");
        if (item instanceof Diskon) {
            throw new IllegalArgumentException("Promo diskon tidak dapat menjadi barang pesanan.");
        }
        if (jumlah <= 0) {
            throw new IllegalArgumentException("Jumlah harus lebih dari nol.");
        }
        this.jumlah = jumlah;
    }

    public MenuItem getItem() { return item; }
    public int getJumlah() { return jumlah; }
    public double getTotalHarga() { return item.getHarga() * jumlah; }

    void tambahJumlah(int tambahan) {
        if (tambahan <= 0 || jumlah > Integer.MAX_VALUE - tambahan) {
            throw new IllegalArgumentException("Jumlah item tidak valid atau terlalu besar.");
        }
        jumlah += tambahan;
    }
}
