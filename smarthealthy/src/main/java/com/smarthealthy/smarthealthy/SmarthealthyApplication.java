package com.smarthealthy.smarthealthy;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.List;

@SpringBootApplication
public class SmarthealthyApplication {

	public static void main(String[] args) {
		SpringApplication.run(SmarthealthyApplication.class, args);
	}

	// Data awal: akun admin + daftar jenis kegiatan bawaan
	@Bean
	CommandLineRunner seed(UserRepository users, KegiatanRepository kegiatan,
						   @Value("${ADMIN_PASSWORD:admin123}") String adminPassword) {
		return args -> {
			if (users.findByEmail("admin@clamfy.com").isEmpty()) {
				users.save(new User("Administrator", "admin@clamfy.com", AuthController.hash(adminPassword), "ADMIN"));
			}
			if (kegiatan.count() == 0) {
				kegiatan.saveAll(List.of(
						new Kegiatan("Jalan Kaki", "AKTIVITAS", "Menit"),
						new Kegiatan("Olahraga / Workout", "AKTIVITAS", "Menit"),
						new Kegiatan("Membaca Buku", "AKTIVITAS", "Menit"),
						new Kegiatan("Meditasi", "AKTIVITAS", "Menit"),
						new Kegiatan("Minum Air", "AIR", "Gelas"),
						new Kegiatan("Nutrisi / Makan", "NUTRISI", "Kkal"),
						new Kegiatan("Tidur", "TIDUR", "Jam")));
			}
		};
	}
}
