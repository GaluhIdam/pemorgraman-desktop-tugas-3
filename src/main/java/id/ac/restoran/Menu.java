package id.ac.restoran;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public final class Menu {
    private final ArrayList<MenuItem> daftarItem = new ArrayList<>();

    public void tambahItem(MenuItem item) {
        Objects.requireNonNull(item, "Item tidak boleh null.");
        if (daftarItem.stream().anyMatch(existing -> existing.getId() == item.getId())) {
            throw new IllegalArgumentException("ID " + item.getId() + " sudah digunakan.");
        }
        daftarItem.add(item);
    }

    public MenuItem cariBerdasarkanId(int id) {
        for (MenuItem item : daftarItem) {
            if (item.getId() == id) {
                return item;
            }
        }
        throw new MenuItemTidakDitemukanException(id);
    }

    public int idBerikutnya() {
        return daftarItem.stream().mapToInt(MenuItem::getId).max().orElse(0) + 1;
    }

    public List<MenuItem> getDaftarItem() {
        return Collections.unmodifiableList(daftarItem);
    }

    public void tampilkanSemua() {
        System.out.println("\n======================== MENU RESTORAN ========================");
        System.out.printf("%-4s %-25s %-13s %-16s %s%n", "ID", "NAMA", "HARGA", "KATEGORI", "JENIS / PROMO");
        System.out.println("----------------------------------------------------------------");
        if (daftarItem.isEmpty()) {
            System.out.println("Menu masih kosong.");
        }
        for (MenuItem item : daftarItem) {
            item.tampilMenu(); // Runtime polymorphism
        }
        System.out.println("================================================================");
    }

    public static Menu dataContoh() {
        Menu menu = new Menu();
        menu.tambahItem(new Makanan(1, "Nasi Goreng Spesial", 28000, "Makanan utama"));
        menu.tambahItem(new Makanan(2, "Ayam Bakar", 32000, "Makanan utama"));
        menu.tambahItem(new Makanan(3, "Pisang Goreng", 15000, "Camilan"));
        menu.tambahItem(new Minuman(4, "Es Teh Manis", 8000, "Dingin"));
        menu.tambahItem(new Minuman(5, "Jus Alpukat", 18000, "Jus buah"));
        menu.tambahItem(new Minuman(6, "Kopi Hitam", 12000, "Panas"));
        menu.tambahItem(new Diskon(7, "Promo Pelajar", 10));
        menu.tambahItem(new Diskon(8, "Promo Akhir Pekan", 15));
        return menu;
    }
}
