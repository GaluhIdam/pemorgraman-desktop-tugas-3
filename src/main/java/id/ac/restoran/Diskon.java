package id.ac.restoran;

public final class Diskon extends MenuItem {
    private final double diskon;

    public Diskon(int id, String nama, double diskon) {
        super(id, nama, 0, "Diskon");
        if (!Double.isFinite(diskon) || diskon < 0 || diskon > 100) {
            throw new IllegalArgumentException("Diskon harus antara 0 dan 100 persen.");
        }
        this.diskon = diskon;
    }

    public double getDiskon() { return diskon; }

    @Override
    public void tampilMenu() {
        System.out.printf("%-4d %-25s %-13s %-16s %s%n",
                getId(), getNama(), "-", getKategori(), FormatUtil.persen(diskon));
    }
}
