package com.smarthealthy.smarthealthy;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface KegiatanRepository extends JpaRepository<Kegiatan, Long> {
    Optional<Kegiatan> findByNama(String nama);
    boolean existsByNama(String nama);
}
