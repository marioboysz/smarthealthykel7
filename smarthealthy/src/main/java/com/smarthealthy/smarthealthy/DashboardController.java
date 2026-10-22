package com.smarthealthy.smarthealthy;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.*;

@Controller
public class DashboardController {

    private static final List<String> KATEGORI = List.of("AKTIVITAS", "AIR", "NUTRISI", "TIDUR");
    private static final Map<String, Double> TARGET = Map.of("AKTIVITAS", 30.0, "AIR", 8.0, "NUTRISI", 2000.0, "TIDUR", 8.0);
    private final CatatanRepository repo;
    private final KegiatanRepository kegiatanRepo;

    public DashboardController(CatatanRepository repo, KegiatanRepository kegiatanRepo) {
        this.repo = repo; this.kegiatanRepo = kegiatanRepo;
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession s, Model m) {
        Long uid = (Long) s.getAttribute("uid");
        if (uid == null) return "redirect:/login";

        LocalDate today = LocalDate.now(), from = today.minusDays(6);
        List<CatatanHarian> week = repo.findByUserIdAndTanggalBetween(uid, from, today);
        Locale id = Locale.forLanguageTag("id-ID");

        StringBuilder sb = new StringBuilder("{\"labels\":[");
        for (int i = 0; i < 7; i++)
            sb.append(i > 0 ? "," : "").append("\"").append(from.plusDays(i).getDayOfWeek().getDisplayName(TextStyle.SHORT, id)).append("\"");
        sb.append("]");

        Map<String, String> todayTotals = new LinkedHashMap<>();
        Map<String, Integer> persen = new LinkedHashMap<>();
        for (String k : KATEGORI) {
            sb.append(",\"").append(k).append("\":[");
            double todaySum = 0;
            for (int i = 0; i < 7; i++) {
                LocalDate d = from.plusDays(i);
                double sum = week.stream().filter(c -> c.getKategori().equals(k) && c.getTanggal().equals(d))
                        .mapToDouble(CatatanHarian::getNilai).sum();
                sb.append(i > 0 ? "," : "").append(sum);
                if (i == 6) todaySum = sum;
            }
            sb.append("]");
            todayTotals.put(k, CatatanHarian.fmt(todaySum));
            persen.put(k, (int) Math.min(100, Math.round(todaySum / TARGET.get(k) * 100)));
        }

        // komposisi aktivitas 7 hari terakhir (untuk donat)
        Map<String, Double> komposisi = new LinkedHashMap<>();
        week.stream().filter(c -> c.getKategori().equals("AKTIVITAS"))
                .forEach(c -> komposisi.merge(c.getDetail(), c.getNilai(), Double::sum));
        sb.append(",\"komposisi\":{");
        int n = 0;
        for (Map.Entry<String, Double> e : komposisi.entrySet())
            sb.append(n++ > 0 ? "," : "").append("\"").append(e.getKey()).append("\":").append(e.getValue());
        sb.append("}}");

        m.addAttribute("nama", s.getAttribute("uname"));
        m.addAttribute("kegiatan", kegiatanRepo.findAll());
        m.addAttribute("today", todayTotals);
        m.addAttribute("persen", persen);
        m.addAttribute("chartJson", sb.toString());
        m.addAttribute("hariAktif", week.stream().map(CatatanHarian::getTanggal).distinct().count());
        m.addAttribute("recent", repo.findTop10ByUserIdOrderByTanggalDescIdDesc(uid));
        return "dashboard";
    }

    /** Menerima form "Catat Sekarang", menyimpan ke database, lalu memuat ulang dashboard. */
    @PostMapping("/catat")
    public String catat(HttpSession s, @RequestParam String detail, @RequestParam double nilai,
                        @RequestParam(required = false) String tanggal,
                        @RequestParam(required = false) String keterangan, RedirectAttributes ra) {
        Long uid = (Long) s.getAttribute("uid");
        if (uid == null) return "redirect:/login";

        Kegiatan kg = kegiatanRepo.findByNama(detail).orElse(null);
        if (kg == null || nilai <= 0 || nilai > 100000) {
            ra.addFlashAttribute("galat", "Data tidak valid, silakan cek isianmu lagi.");
            return "redirect:/dashboard";
        }
        LocalDate today = LocalDate.now(), d;
        try { d = (tanggal == null || tanggal.isBlank()) ? today : LocalDate.parse(tanggal); }
        catch (Exception e) { d = today; }
        if (d.isAfter(today)) d = today;

        String ket = (keterangan == null || keterangan.isBlank()) ? null
                : keterangan.trim().substring(0, Math.min(100, keterangan.trim().length()));
        repo.save(new CatatanHarian(uid, kg.getKategori(), detail, nilai, kg.getSatuan(), d, ket));
        ra.addFlashAttribute("pesan", "✓ " + detail + " " + CatatanHarian.fmt(nilai) + " " + kg.getSatuan() + " berhasil dicatat!");
        return "redirect:/dashboard";
    }

    @PostMapping("/catat/{id}/hapus")
    public String hapus(HttpSession s, @PathVariable Long id, RedirectAttributes ra) {
        Long uid = (Long) s.getAttribute("uid");
        if (uid == null) return "redirect:/login";
        repo.findById(id).filter(c -> c.getUserId().equals(uid)).ifPresent(repo::delete);
        ra.addFlashAttribute("pesan", "Catatan dihapus.");
        return "redirect:/dashboard";
    }
}
