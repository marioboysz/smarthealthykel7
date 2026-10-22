package com.smarthealthy.smarthealthy;

import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;

public interface CatatanRepository extends JpaRepository<CatatanHarian, Long> {
    List<CatatanHarian> findByUserIdAndTanggalBetween(Long userId, LocalDate from, LocalDate to);
    List<CatatanHarian> findTop10ByUserIdOrderByTanggalDescIdDesc(Long userId);
    long countByUserId(Long userId);
    void deleteByUserId(Long userId);
}
