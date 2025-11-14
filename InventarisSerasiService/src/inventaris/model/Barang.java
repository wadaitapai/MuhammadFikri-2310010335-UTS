package inventaris.model;

public class Barang {
     private String kode;
    private String nama;
    private int jumlah;
    private double harga;

    public Barang(String kode, String nama, int jumlah, double harga) {
        this.kode = kode;
        this.nama = nama;
        this.jumlah = jumlah;
        this.harga = harga;
}
// Getter Accessor
    public String getKode() { return kode; }
    public String getNama() { return nama; }
    public int getJumlah() { return jumlah; }
    public double getHarga() { return harga; }

    // Setter Mutator
    public void setNama(String nama) { this.nama = nama; }
    public void setJumlah(int jumlah) { this.jumlah = jumlah; }
    public void setHarga(double harga) { this.harga = harga; }

    @Override
    public String toString() {
        return kode + " - " + nama + " (" + jumlah + ")";
    }
}