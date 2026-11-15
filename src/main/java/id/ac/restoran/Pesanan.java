package id.ac.restoran;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public final class Pesanan {
    private static final DateTimeFormatter TANGGAL_FORMAT = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
    private final String idPesanan = UUID.randomUUID().toString().substring(0, 8).toUpperCase(Locale.ROOT);
    private final LocalDateTime waktuPesanan = LocalDateTime.now();
    private final ArrayList<DetailPesanan> daftarItem = new ArrayList<>();
    private Diskon diskon;

    public String getIdPesanan() { return idPesanan; }
    public LocalDateTime getWaktuPesanan() { return waktuPesanan; }
    public Diskon getDiskon() { return diskon; }
    public List<DetailPesanan> getDaftarItem() {
        return Collections.unmodifiableList(daftarItem);
    }

    public void tambahItem(MenuItem item, int jumlah) {
        if (item == null || item instanceof Diskon) {
            throw new IllegalArgumentException("Hanya makanan atau minuman yang dapat dipesan.");
        }
        if (jumlah <= 0) {
            throw new IllegalArgumentException("Jumlah harus lebih dari nol.");
        }
        for (DetailPesanan detail : daftarItem) {
            if (detail.getItem().getId() == item.getId()) {
                detail.tambahJumlah(jumlah);
                return;
            }
        }
        daftarItem.add(new DetailPesanan(item, jumlah));
    }

    public void hapusItem(int id) {
        if (!daftarItem.removeIf(detail -> detail.getItem().getId() == id)) {
            throw new MenuItemTidakDitemukanException(id);
        }
    }

    /** Hanya satu promo dapat diterapkan ke satu pesanan. Null berarti tanpa promo. */
    public void terapkanDiskon(Diskon diskon) {
        this.diskon = diskon;
    }

    public boolean kosong() {
        return daftarItem.isEmpty();
    }

    public double hitungSubtotal() {
        double subtotal = 0;
        for (DetailPesanan detail : daftarItem) {
            subtotal += detail.getTotalHarga();
        }
        return subtotal;
    }

    public double hitungPotonganDiskon() {
        return diskon == null ? 0 : hitungSubtotal() * diskon.getDiskon() / 100;
    }

    public double hitungTotal() {
        return hitungSubtotal() - hitungPotonganDiskon();
    }

    public String buatStruk() {
        if (kosong()) {
            throw new IllegalStateException("Pesanan masih kosong, struk belum dapat dibuat.");
        }
        StringBuilder struk = new StringBuilder();
        struk.append("====================================================\n");
        struk.append("                  STRUK RESTORAN                    \n");
        struk.append("====================================================\n");
        struk.append("ID Pesanan : ").append(idPesanan).append('\n');
        struk.append("Tanggal    : ").append(waktuPesanan.format(TANGGAL_FORMAT)).append('\n');
        struk.append("----------------------------------------------------\n");
        struk.append(String.format("%-23s %4s %20s%n", "Nama Item", "Qty", "Jumlah"));
        struk.append("----------------------------------------------------\n");
        for (DetailPesanan detail : daftarItem) {
            struk.append(String.format("%-23s %4d %20s%n", detail.getItem().getNama(),
                    detail.getJumlah(), FormatUtil.rupiah(detail.getTotalHarga())));
        }
        struk.append("----------------------------------------------------\n");
        struk.append("Subtotal : ").append(FormatUtil.rupiah(hitungSubtotal())).append('\n');
        if (diskon == null) {
            struk.append("Diskon   : Tidak ada\n");
        } else {
            struk.append("Diskon   : ").append(diskon.getNama())
                    .append(" (").append(FormatUtil.persen(diskon.getDiskon())).append(")\n");
            struk.append("Potongan : -").append(FormatUtil.rupiah(hitungPotonganDiskon())).append('\n');
        }
        struk.append("TOTAL    : ").append(FormatUtil.rupiah(hitungTotal())).append('\n');
        struk.append("====================================================\n");
        struk.append("              Terima kasih!                         \n");
        struk.append("====================================================\n");
        return struk.toString();
    }
}
