package inventaris.model;

import java.util.ArrayList;

public class ManajerBarang {

    private ArrayList<Barang> daftar = new ArrayList<>();

    public void tambah(Barang b) {
        daftar.add(b);
    }

    public void update(int index, Barang b) {
        if (index >= 0 && index < daftar.size()) {
            daftar.set(index, b);
        }
    }

    public void hapus(int index) {
        if (index >= 0 && index < daftar.size()) {
            daftar.remove(index);
        }
    }

    public ArrayList<Barang> getAll() {
        return daftar;
    }
}
