package inventaris.view;

import inventaris.model.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date; // PERBAIKAN: Import Date untuk mengambil data saat update
import javax.swing.*;
import javax.swing.event.*;

public class InventarisFrame extends javax.swing.JFrame {
    // ================================
    // Data & Model
    // ================================
    ManajerBarang manager = new ManajerBarang();    // Penyimpanan data barang
    DefaultListModel modelList = new DefaultListModel();  // Model untuk JList

    public InventarisFrame() {
        initComponents();
        setTitle("Aplikasi Inventaris Toko Serasi Service");
        
        // ================================
        // Set model list agar dinamis
        // ================================
        listBarang.setModel(modelList);

        // ================================
        // Event ketika item list diklik
        // ================================
        listBarang.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent evt) {
                if (!evt.getValueIsAdjusting()) { // Menghindari dua kali event
                    tampilkanDetail();
                    tampilkanDataKeForm(); // PERBAIKAN: Panggil fungsi untuk mengisi form
                }
            }
        });
        
        btnImport.addActionListener(e -> popupImport());
        btnExport.addActionListener(e -> popupExport());
    }
    
    // PERBAIKAN: Fungsi untuk mengisi form ketika item list diklik
    private void tampilkanDataKeForm() {
        int index = listBarang.getSelectedIndex();
        if (index >= 0) {
            Barang b = manager.getAll().get(index);
            txtKode.setText(b.getKode());
            txtNama.setText(b.getNama());
            txtJumlah.setText(String.valueOf(b.getJumlah()));
            txtHarga.setText(String.valueOf(b.getHarga()));
            
            // Mengubah String Tanggal menjadi Date untuk JDateChooser
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy");
                Date tanggalMasuk = sdf.parse(b.getTanggal());
                dateTanggal.setDate(tanggalMasuk);
            } catch (Exception ex) {
                dateTanggal.setDate(null);
            }
        }
    }
    
    // ================================
    // Menampilkan detail barang
    // ================================
    private void tampilkanDetail() {
        int index = listBarang.getSelectedIndex();
        if (index >= 0) {
            Barang b = manager.getAll().get(index);
            txtDetail.setText(
                "Kode    : " + b.getKode() +
                "\nNama    : " + b.getNama() +
                "\nJumlah : " + b.getJumlah() +
                "\nHarga  : " + b.getHarga()+
                "\nTanggal Masuk : " + b.getTanggal()
            );
        }
    }
    
   // =====================================================
    // CRUD
    // =====================================================

    private void tambahBarang() {
        try {
            String kode = txtKode.getText();
            String nama = txtNama.getText();
            int jumlah = Integer.parseInt(txtJumlah.getText());
            double harga = Double.parseDouble(txtHarga.getText());

            SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy");
            String tanggal = sdf.format(dateTanggal.getDate());

            Barang b = new Barang(kode, nama, jumlah, harga, tanggal);
            manager.tambah(b);
            modelList.addElement(b.toString());

            JOptionPane.showMessageDialog(this, "Barang berhasil ditambahkan!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Input tidak valid! Pastikan semua kolom terisi dengan benar.");
        }
    }

    private void updateBarang() {
        int index = listBarang.getSelectedIndex();
        if (index < 0) {
            JOptionPane.showMessageDialog(this, "Pilih data dulu!");
            return;
        }

        try {
            String kode = txtKode.getText();
            String nama = txtNama.getText();
            int jumlah = Integer.parseInt(txtJumlah.getText());
            double harga = Double.parseDouble(txtHarga.getText());
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy");
            String tanggal = sdf.format(dateTanggal.getDate());
            
            // PERBAIKAN: Buat objek Barang baru dan update manajer
            Barang barangBaru = new Barang(kode, nama, jumlah, harga, tanggal);
            manager.update(index, barangBaru);
            
            refreshList();
            listBarang.setSelectedIndex(index); // Pilih kembali item yang diupdate
            tampilkanDetail(); // Tampilkan detail yang baru

            JOptionPane.showMessageDialog(this, "Barang berhasil diupdate!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Input tidak valid!");
        }
    }

    private void hapusBarang() {
        int index = listBarang.getSelectedIndex();
        if (index < 0) {
            JOptionPane.showMessageDialog(this, "Pilih data dulu!");
            return;
        }

        manager.hapus(index);
        modelList.remove(index);
        txtDetail.setText("");

        JOptionPane.showMessageDialog(this, "Barang dihapus!");
    }
    // ===================================
    // REFRESH LIST
    // ===================================
    private void refreshList() {
        modelList.clear();
        for (Barang b : manager.getAll()) {
            modelList.addElement(b.toString());
        }
    }
// =====================================================
    // EXPORT
    // =====================================================

    private void exportTXT() {
        try {
            FileWriter fw = new FileWriter("data_barang.txt");
            for (Barang b : manager.getAll()) {
                // PERBAIKAN: Menyertakan Tanggal agar konsisten saat import
                fw.write(b.getKode() + ";" + b.getNama() + ";" + b.getJumlah() + ";" + b.getHarga() + ";" + b.getTanggal() + "\n");
            }
            fw.close();
            JOptionPane.showMessageDialog(this, "Export TXT berhasil!");
        } catch (Exception e) {
             JOptionPane.showMessageDialog(this, "Gagal Export TXT!");
        }
    }

    private void exportJSON() {
        try {
            FileWriter fw = new FileWriter("data_barang.json");
            fw.write("[\n");
            for (int i = 0; i < manager.getAll().size(); i++) {
                Barang b = manager.getAll().get(i);
                fw.write("  {\n");
                fw.write("    \"kode\": \"" + b.getKode() + "\",\n");
                fw.write("    \"nama\": \"" + b.getNama() + "\",\n");
                fw.write("    \"jumlah\": " + b.getJumlah() + ",\n");
                fw.write("    \"harga\": " + b.getHarga() + ",\n");
                fw.write("    \"tanggal\": \"" + b.getTanggal() + "\"\n"); // Tambahkan tanggal
                fw.write(i == manager.getAll().size() - 1 ? "  }\n" : "  },\n");
            }
            fw.write("]");
            fw.close();
            JOptionPane.showMessageDialog(this, "Export JSON berhasil!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal Export JSON!");
        }
    }
    // ===================================
    // EXPORT EXCEL (Tab Separated)
    // ===================================
    private void exportExcel() {
        try {
            FileWriter fw = new FileWriter("data_barang.xls");
            fw.write("Kode\tNama\tJumlah\tHarga\tTanggal\n");

            for (Barang b : manager.getAll()) {
                fw.write(b.getKode() + "\t" + b.getNama() + "\t" +
                        b.getJumlah() + "\t" + b.getHarga() + "\t" +
                        b.getTanggal() + "\n");
            }
            fw.close();
            JOptionPane.showMessageDialog(this, "Export Excel berhasil!");
        } catch (Exception e) {
             JOptionPane.showMessageDialog(this, "Gagal Export Excel!");
        }
    }
    // ===================================
    // EXPORT PDF (Sederhana)
    // ===================================
    private void exportPDF() {
        try {
            FileOutputStream fos = new FileOutputStream("data_barang.pdf");

            String content = "DATA BARANG TOKO SERASI SERVICE\n\n";
            for (Barang b : manager.getAll()) {
                content += "Kode : " + b.getKode() + "\n";
                content += "Nama : " + b.getNama() + "\n";
                content += "Jumlah : " + b.getJumlah() + "\n";
                content += "Harga : " + b.getHarga() + "\n";
                content += "Tanggal : " + b.getTanggal() + "\n";
                content += "--------------------------\n";
            }

            fos.write(content.getBytes());
            fos.close();

            JOptionPane.showMessageDialog(this, "Export PDF berhasil!");
        } catch (Exception e) {
             JOptionPane.showMessageDialog(this, "Gagal Export PDF!");
        }
    }

    // =====================================================
    // IMPORT TXT
    // =====================================================

    private void importTXT() {
        try {
            BufferedReader br = new BufferedReader(new FileReader("data_barang.txt"));
            String line;

            manager.getAll().clear();
            modelList.clear();

            // PERBAIKAN: Sekarang membaca 5 elemen, termasuk tanggal (d[4])
            while ((line = br.readLine()) != null) {
                String[] d = line.split(";");
                
                // Pastikan format data minimal 5 elemen
                if (d.length >= 5) {
                    Barang b = new Barang(
                            d[0], d[1],
                            Integer.parseInt(d[2]),
                            Double.parseDouble(d[3]),
                            d[4]
                    );
                    manager.tambah(b);
                }
            }
            br.close();
            refreshList();

            JOptionPane.showMessageDialog(this, "Import TXT OK!");

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal import TXT. Pastikan file data_barang.txt ada dan formatnya benar.");
        }
    }
    // ===================================
    // IMPORT JSON
    // ===================================
    private void importJSON() {
        try {
            BufferedReader br = new BufferedReader(new FileReader("data_barang.json"));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) sb.append(line);
            br.close();

            String json = sb.toString().replace("[", "").replace("]", "").trim();

            manager.getAll().clear();
            modelList.clear();

            String[] items = json.split("\\},");
            for (String item : items) {
                item = item.replace("{", "").replace("}", "").trim();
                String[] x = item.split(",");

                // Parsing nilai dari JSON (index bisa berbeda tergantung format export)
                String kode = x[0].split(":")[1].replace("\"", "").trim();
                String nama = x[1].split(":")[1].replace("\"", "").trim();
                int jumlah = Integer.parseInt(x[2].split(":")[1].trim());
                double harga = Double.parseDouble(x[3].split(":")[1].trim());
                String tanggal = x[4].split(":")[1].replace("\"", "").trim(); // Tambahkan parsing tanggal

                // PERBAIKAN: Membuat dan menambahkan objek Barang ke Manajer
                Barang b = new Barang(kode, nama, jumlah, harga, tanggal);
                manager.tambah(b);
            }
            refreshList();

            JOptionPane.showMessageDialog(this, "Import JSON berhasil!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error import JSON! Pastikan file data_barang.json ada dan formatnya benar.");
        }
    }

    // =====================================================
    // POPUP EXPORT & IMPORT
    // =====================================================

    private void popupExport() {
        String[] opsi = {"TXT", "JSON", "Excel (.xls)", "PDF"};
        String pilih = (String) JOptionPane.showInputDialog(
                this, "Pilih tipe export:", "Export",
                JOptionPane.QUESTION_MESSAGE, null, opsi, opsi[0]
        );

        if (pilih == null) return;

        switch (pilih) {
            case "TXT": exportTXT(); break;
            case "JSON": exportJSON(); break;
            case "Excel (.xls)": exportExcel(); break;
            case "PDF": exportPDF(); break;
        }
    }

    private void popupImport() {
        String[] pilihan = {"TXT", "JSON"};
        String pick = (String) JOptionPane.showInputDialog(
                this, "Pilih format import:",
                "Import Data",
                JOptionPane.QUESTION_MESSAGE,
                null, pilihan, pilihan[0]);

        if (pick == null) return;

        switch (pick) {
            case "TXT": importTXT(); break;
            case "JSON": importJSON(); break;
        }
    }
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        lblKode = new javax.swing.JLabel();
        lblNama = new javax.swing.JLabel();
        lblJumlah = new javax.swing.JLabel();
        lblHarga = new javax.swing.JLabel();
        txtKode = new javax.swing.JTextField();
        txtJumlah = new javax.swing.JTextField();
        txtHarga = new javax.swing.JTextField();
        txtNama = new javax.swing.JTextField();
        btnUpdate = new javax.swing.JButton();
        btnHapus = new javax.swing.JButton();
        btnTambah = new javax.swing.JButton();
        lblJudul = new javax.swing.JLabel();
        dateTanggal = new com.toedter.calendar.JDateChooser();
        lblTanggal = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        listBarang = new javax.swing.JList<>();
        jPanel2 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        txtDetail = new javax.swing.JTextArea();
        btnExport = new javax.swing.JButton();
        btnImport = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 30)); // NOI18N
        jLabel1.setText("APLIKASI INVENTARIS BARANG TOKO \"SERASI\" SERVICE");
        getContentPane().add(jLabel1, java.awt.BorderLayout.CENTER);

        jPanel1.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));

        lblKode.setFont(new java.awt.Font("Verdana", 0, 18)); // NOI18N
        lblKode.setText("Kode Barang    : ");

        lblNama.setFont(new java.awt.Font("Verdana", 0, 18)); // NOI18N
        lblNama.setText("Nama Barang   :");

        lblJumlah.setFont(new java.awt.Font("Verdana", 0, 18)); // NOI18N
        lblJumlah.setText("Jumlah            : ");

        lblHarga.setFont(new java.awt.Font("Verdana", 0, 18)); // NOI18N
        lblHarga.setText("Harga              :");

        txtKode.setFont(new java.awt.Font("Verdana", 0, 18)); // NOI18N

        txtJumlah.setFont(new java.awt.Font("Verdana", 0, 18)); // NOI18N

        txtHarga.setFont(new java.awt.Font("Verdana", 0, 18)); // NOI18N

        txtNama.setFont(new java.awt.Font("Verdana", 0, 18)); // NOI18N

        btnUpdate.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        btnUpdate.setText("Update");
        btnUpdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUpdateActionPerformed(evt);
            }
        });

        btnHapus.setBackground(new java.awt.Color(255, 0, 0));
        btnHapus.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        btnHapus.setText("Hapus");
        btnHapus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnHapusActionPerformed(evt);
            }
        });

        btnTambah.setBackground(new java.awt.Color(0, 204, 0));
        btnTambah.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        btnTambah.setText("Tambah");
        btnTambah.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTambahActionPerformed(evt);
            }
        });

        lblJudul.setBackground(new java.awt.Color(255, 255, 51));
        lblJudul.setFont(new java.awt.Font("Tahoma", 1, 28)); // NOI18N
        lblJudul.setText("APLIKASI INVENTARIS BARANG TOKO \"SERASI\"SERVICE");

        lblTanggal.setFont(new java.awt.Font("Verdana", 0, 18)); // NOI18N
        lblTanggal.setText("Tanggal Masuk : ");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(258, 258, 258)
                        .addComponent(btnTambah, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(72, 72, 72)
                        .addComponent(btnUpdate, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(82, 82, 82)
                        .addComponent(btnHapus, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(61, 61, 61)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblKode)
                            .addComponent(lblNama)
                            .addComponent(lblJumlah)
                            .addComponent(lblHarga)
                            .addComponent(lblTanggal))
                        .addGap(43, 43, 43)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtKode, javax.swing.GroupLayout.DEFAULT_SIZE, 599, Short.MAX_VALUE)
                            .addComponent(txtJumlah)
                            .addComponent(txtHarga)
                            .addComponent(txtNama)
                            .addComponent(dateTanggal, javax.swing.GroupLayout.PREFERRED_SIZE, 210, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(160, 160, 160)
                .addComponent(lblJudul)
                .addGap(0, 371, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(lblJudul)
                        .addGap(24, 24, 24)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblKode)
                            .addComponent(txtKode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(6, 6, 6)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblNama)
                            .addComponent(txtNama, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(9, 9, 9)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblJumlah)
                            .addComponent(txtJumlah, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtHarga, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblHarga))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(dateTanggal, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(lblTanggal))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 39, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnTambah, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnUpdate, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnHapus, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(23, 23, 23))
        );

        getContentPane().add(jPanel1, java.awt.BorderLayout.PAGE_START);

        jPanel6.setLayout(new java.awt.BorderLayout());

        listBarang.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
        listBarang.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        listBarang.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        listBarang.setMinimumSize(new java.awt.Dimension(200, 0));
        listBarang.setName(""); // NOI18N
        listBarang.setPreferredSize(new java.awt.Dimension(200, 400));
        jScrollPane1.setViewportView(listBarang);

        jPanel6.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        jPanel2.setBackground(new java.awt.Color(0, 0, 0));

        jLabel6.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(255, 255, 51));
        jLabel6.setText("Daftar Barang");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(50, 50, 50)
                .addComponent(jLabel6)
                .addContainerGap(144, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel6)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel6.add(jPanel2, java.awt.BorderLayout.PAGE_START);

        getContentPane().add(jPanel6, java.awt.BorderLayout.LINE_START);

        jPanel3.setLayout(new java.awt.BorderLayout());
        getContentPane().add(jPanel3, java.awt.BorderLayout.PAGE_END);

        jPanel4.setBackground(new java.awt.Color(0, 0, 0));

        txtDetail.setEditable(false);
        txtDetail.setColumns(20);
        txtDetail.setRows(5);
        jScrollPane2.setViewportView(txtDetail);

        btnExport.setBackground(new java.awt.Color(51, 255, 255));
        btnExport.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        btnExport.setText("Export");
        btnExport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExportActionPerformed(evt);
            }
        });

        btnImport.setBackground(new java.awt.Color(51, 51, 255));
        btnImport.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        btnImport.setText("Import");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap(732, Short.MAX_VALUE)
                .addComponent(btnExport, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnImport, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(23, 23, 23))
            .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.TRAILING)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 204, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnExport, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnImport, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, Short.MAX_VALUE))
        );

        getContentPane().add(jPanel4, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents
   // BUTTON EVENT
    private void btnTambahActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTambahActionPerformed
         tambahBarang();
    }//GEN-LAST:event_btnTambahActionPerformed

    private void btnUpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpdateActionPerformed
        updateBarang();
    }//GEN-LAST:event_btnUpdateActionPerformed

    private void btnHapusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHapusActionPerformed
        hapusBarang();
    }//GEN-LAST:event_btnHapusActionPerformed

    private void btnExportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExportActionPerformed
        popupExport();
    }//GEN-LAST:event_btnExportActionPerformed
         
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(InventarisFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(InventarisFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(InventarisFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(InventarisFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new InventarisFrame().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnExport;
    private javax.swing.JButton btnHapus;
    private javax.swing.JButton btnImport;
    private javax.swing.JButton btnTambah;
    private javax.swing.JButton btnUpdate;
    private com.toedter.calendar.JDateChooser dateTanggal;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lblHarga;
    private javax.swing.JLabel lblJudul;
    private javax.swing.JLabel lblJumlah;
    private javax.swing.JLabel lblKode;
    private javax.swing.JLabel lblNama;
    private javax.swing.JLabel lblTanggal;
    private javax.swing.JList<String> listBarang;
    private javax.swing.JTextArea txtDetail;
    private javax.swing.JTextField txtHarga;
    private javax.swing.JTextField txtJumlah;
    private javax.swing.JTextField txtKode;
    private javax.swing.JTextField txtNama;
    // End of variables declaration//GEN-END:variables
}
