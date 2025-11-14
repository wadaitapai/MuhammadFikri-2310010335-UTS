package inventaris.model;

import java.util.ArrayList;

public class ManajerBarang {
    private ArrayList<Barang> listBarang = new ArrayList<>();

    public void tambah(Barang b) {
        listBarang.add(b);
    }

    public void hapus(int index) {
        listBarang.remove(index);
    }

    public void update(int index, Barang b) {
        listBarang.set(index, b);
    }

    public ArrayList<Barang> getAll() {
        return listBarang;
    }
}

