package com.smarthealthy.smarthealthy;

import jakarta.persistence.*;

@Entity
@Table(name = "app_user")
public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nama;
    @Column(unique = true)
    private String email;
    private String password;
    private String role; // USER atau ADMIN

    public User() {}
    public User(String nama, String email, String password) { this(nama, email, password, "USER"); }
    public User(String nama, String email, String password, String role) {
        this.nama = nama; this.email = email; this.password = password; this.role = role;
    }
    public Long getId() { return id; }
    public String getNama() { return nama; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public void setNama(String v) { this.nama = v; }
    public void setEmail(String v) { this.email = v; }
    public void setPassword(String v) { this.password = v; }
    public void setRole(String v) { this.role = v; }
    public String getRole() { return role == null ? "USER" : role; }
}
