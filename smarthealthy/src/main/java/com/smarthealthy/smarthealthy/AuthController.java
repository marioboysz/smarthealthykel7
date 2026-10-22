package com.smarthealthy.smarthealthy;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

@Controller
public class AuthController {
    private final UserRepository users;

    public AuthController(UserRepository users) { this.users = users; }

    @GetMapping("/login")
    public String loginPage() { return "login"; }

    @PostMapping("/login")
    public String login(@RequestParam String email, @RequestParam String password, HttpSession s, Model m) {
        User u = users.findByEmail(email.trim().toLowerCase()).orElse(null);
        if (u == null || !check(password, u.getPassword())) {
            m.addAttribute("error", "Email atau password salah.");
            return "login";
        }
        s.setAttribute("uid", u.getId());
        s.setAttribute("uname", u.getNama());
        s.setAttribute("role", u.getRole());
        return "ADMIN".equals(u.getRole()) ? "redirect:/admin" : "redirect:/dashboard";
    }

    @GetMapping("/register")
    public String registerPage() { return "register"; }

    @PostMapping("/register")
    public String register(@RequestParam String nama, @RequestParam String email, @RequestParam String password, Model m) {
        String e = email.trim().toLowerCase();
        if (nama.isBlank() || password.length() < 6) {
            m.addAttribute("error", "Nama wajib diisi dan password minimal 6 karakter.");
            return "register";
        }
        if (users.findByEmail(e).isPresent()) {
            m.addAttribute("error", "Email sudah terdaftar, silakan masuk.");
            return "register";
        }
        users.save(new User(nama.trim(), e, hash(password)));
        return "redirect:/login?daftar";
    }

    @GetMapping("/logout")
    public String logout(HttpSession s) { s.invalidate(); return "redirect:/"; }

    // password disimpan sebagai salt:hash (SHA-256)
    static String hash(String p) {
        byte[] salt = new byte[16];
        new SecureRandom().nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt) + ":" + digest(salt, p);
    }
    private static String digest(byte[] salt, String p) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt);
            return Base64.getEncoder().encodeToString(md.digest(p.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) { throw new IllegalStateException(e); }
    }
    private static boolean check(String p, String stored) {
        String[] a = stored.split(":");
        return digest(Base64.getDecoder().decode(a[0]), p).equals(a[1]);
    }
}
