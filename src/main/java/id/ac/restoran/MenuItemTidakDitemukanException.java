package id.ac.restoran;

public final class MenuItemTidakDitemukanException extends RuntimeException {
    public MenuItemTidakDitemukanException(int id) {
        super("Item menu dengan ID " + id + " tidak ditemukan.");
    }
}
