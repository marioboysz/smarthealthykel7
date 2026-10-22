package com.smarthealthy.smarthealthy;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Set;

@Controller
public class AdminController {
    private static final Set<String> KATEGORI = Set.of("AKTIVITAS", "AIR", "NUTRISI", "TIDUR");
    private final UserRepository users;
    private final CatatanRepository logs;
    private final KegiatanRepository kegiatan;

    public AdminController(UserRepository users, CatatanRepository logs, KegiatanRepository kegiatan) {
        this.users = users; this.logs = logs; this.kegiatan = kegiatan;
    }

    public record Row(Long id, String nama, String email, String role, long jumlah) {}

    private boolean isAdmin(HttpSession s) { return "ADMIN".equals(s.getAttribute("role")); }

    @GetMapping("/admin")
    public String admin(HttpSession s, Model m) {
        if (!isAdmin(s)) return "redirect:/login";
        List<Row> rows = users.findAll().stream()
                .map(u -> new Row(u.getId(), u.getNama(), u.getEmail(), u.getRole(), logs.countByUserId(u.getId())))
                .toList();
        m.addAttribute("rows", rows);
        m.addAttribute("totalUser", rows.stream().filter(r -> !"ADMIN".equals(r.role())).count());
        m.addAttribute("totalLog", logs.count());
        m.addAttribute("kegiatan", kegiatan.findAll());
        m.addAttribute("nama", s.getAttribute("uname"));
        return "admin";
    }

    // ---------- kelola pengguna ----------
    @PostMapping("/admin/user/tambah")
    public String tambahUser(HttpSession s, @RequestParam String nama, @RequestParam String email,
                             @RequestParam String password, @RequestParam String role, RedirectAttributes ra) {
        if (!isAdmin(s)) return "redirect:/login";
        String e = email.trim().toLowerCase();
        if (nama.isBlank() || e.isBlank() || password.length() < 6) {
            ra.addFlashAttribute("galat", "Nama, email wajib diisi dan password minimal 6 karakter.");
        } else if (users.findByEmail(e).isPresent()) {
            ra.addFlashAttribute("galat", "Email sudah terdaftar.");
        } else {
            users.save(new User(nama.trim(), e, AuthController.hash(password), "ADMIN".equals(role) ? "ADMIN" : "USER"));
            ra.addFlashAttribute("pesan", "✓ Pengguna " + nama.trim() + " berhasil ditambahkan.");
        }
        return "redirect:/admin";
    }

    @PostMapping("/admin/user/{id}/edit")
    public String editUser(HttpSession s, @PathVariable Long id, @RequestParam String nama, @RequestParam String email,
                           @RequestParam String role, @RequestParam(required = false) String password, RedirectAttributes ra) {
        if (!isAdmin(s)) return "redirect:/login";
        User u = users.findById(id).orElse(null);
        String e = email.trim().toLowerCase();
        if (u == null) return "redirect:/admin";
        if (nama.isBlank() || e.isBlank()) {
            ra.addFlashAttribute("galat", "Nama dan email tidak boleh kosong.");
        } else if (users.findByEmail(e).filter(o -> !o.getId().equals(id)).isPresent()) {
            ra.addFlashAttribute("galat", "Email itu sudah dipakai pengguna lain.");
        } else if (password != null && !password.isBlank() && password.length() < 6) {
            ra.addFlashAttribute("galat", "Password baru minimal 6 karakter.");
        } else {
            u.setNama(nama.trim());
            u.setEmail(e);
            if (!id.equals(s.getAttribute("uid"))) u.setRole("ADMIN".equals(role) ? "ADMIN" : "USER"); // admin tak bisa menurunkan perannya sendiri
            if (password != null && !password.isBlank()) u.setPassword(AuthController.hash(password));
            users.save(u);
            if (id.equals(s.getAttribute("uid"))) s.setAttribute("uname", u.getNama());
            ra.addFlashAttribute("pesan", "✓ Data " + u.getNama() + " diperbarui.");
        }
        return "redirect:/admin";
    }

    @PostMapping("/admin/user/{id}/delete")
    @Transactional
    public String hapusUser(HttpSession s, @PathVariable Long id, RedirectAttributes ra) {
        if (!isAdmin(s)) return "redirect:/login";
        User u = users.findById(id).orElse(null);
        if (u != null && !"ADMIN".equals(u.getRole())) {
            logs.deleteByUserId(u.getId());
            users.delete(u);
            ra.addFlashAttribute("pesan", "Pengguna " + u.getNama() + " dihapus.");
        }
        return "redirect:/admin";
    }

    // ---------- kelola jenis kegiatan ----------
    @PostMapping("/admin/kegiatan/tambah")
    public String tambahKegiatan(HttpSession s, @RequestParam String nama, @RequestParam String kategori,
                                 @RequestParam String satuan, RedirectAttributes ra) {
        if (!isAdmin(s)) return "redirect:/login";
        String n = nama.trim(), st = satuan.trim();
        if (n.isEmpty() || st.isEmpty() || !KATEGORI.contains(kategori)) {
            ra.addFlashAttribute("galat", "Nama kegiatan, kategori, dan satuan wajib diisi.");
        } else if (kegiatan.existsByNama(n)) {
            ra.addFlashAttribute("galat", "Kegiatan \"" + n + "\" sudah ada.");
        } else {
            kegiatan.save(new Kegiatan(n, kategori, st));
            ra.addFlashAttribute("pesan", "✓ Kegiatan \"" + n + "\" ditambahkan dan langsung muncul di form pengguna.");
        }
        return "redirect:/admin";
    }

    @PostMapping("/admin/kegiatan/{id}/hapus")
    public String hapusKegiatan(HttpSession s, @PathVariable Long id, RedirectAttributes ra) {
        if (!isAdmin(s)) return "redirect:/login";
        kegiatan.deleteById(id);
        ra.addFlashAttribute("pesan", "Jenis kegiatan dihapus.");
        return "redirect:/admin";
    }
}
