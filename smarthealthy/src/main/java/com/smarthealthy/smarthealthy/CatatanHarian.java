package com.smarthealthy.smarthealthy;

import jakarta.persistence.*;
import java.time.LocalDate;

/** Riwayat catatan harian: id, kategori, detail kegiatan, nilai, satuan, tanggal, keterangan. */
@Entity
@Table(name = "catatan_harian")
public class CatatanHarian {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long userId;
    private String kategori;    // AKTIVITAS, AIR, NUTRISI, TIDUR
    private String detail;      // Jalan Kaki, Meditasi, Minum Air, ...
    private double nilai;
    private String satuan;      // Menit, Gelas, Kkal, Jam
    private LocalDate tanggal;
    private String keterangan;  // catatan bebas (opsional)

    public CatatanHarian() {}
    public CatatanHarian(Long userId, String kategori, String detail, double nilai, String satuan, LocalDate tanggal, String keterangan) {
        this.userId = userId; this.kategori = kategori; this.detail = detail; this.nilai = nilai;
        this.satuan = satuan; this.tanggal = tanggal; this.keterangan = keterangan;
    }

    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public String getKategori() { return kategori; }
    public String getDetail() { return detail; }
    public double getNilai() { return nilai; }
    public String getSatuan() { return satuan; }
    public LocalDate getTanggal() { return tanggal; }
    public String getKeterangan() { return keterangan; }

    public static String fmt(double v) {
        return v == Math.rint(v) ? String.valueOf((long) v) : String.valueOf(Math.round(v * 10) / 10.0);
    }
    public String getNilaiTeks() { return fmt(nilai) + " " + satuan; }
    public String getIkon() {
        return switch (kategori) { case "AKTIVITAS" -> "🏃"; case "AIR" -> "💧"; case "NUTRISI" -> "🥗"; default -> "🌙"; };
    }
}
