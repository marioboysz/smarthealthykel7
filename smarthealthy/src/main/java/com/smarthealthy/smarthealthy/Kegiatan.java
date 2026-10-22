package com.smarthealthy.smarthealthy;

import jakarta.persistence.*;

/** Master jenis kegiatan yang dikelola admin dan muncul di dropdown "Catat Sekarang". */
@Entity
@Table(name = "kegiatan")
public class Kegiatan {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String nama;
    private String kategori; // AKTIVITAS, AIR, NUTRISI, TIDUR (menentukan grafik)
    private String satuan;   // Menit, Gelas, Kkal, Jam, ...

    public Kegiatan() {}
    public Kegiatan(String nama, String kategori, String satuan) { this.nama = nama; this.kategori = kategori; this.satuan = satuan; }
    public Long getId() { return id; }
    public String getNama() { return nama; }
    public String getKategori() { return kategori; }
    public String getSatuan() { return satuan; }
    public String getIkon() {
        return switch (kategori) { case "AKTIVITAS" -> "🏃"; case "AIR" -> "💧"; case "NUTRISI" -> "🥗"; default -> "🌙"; };
    }
}
