package org.antop.kakao.jpa.repository;

import org.antop.kakao.jpa.entity.Seeding;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface SeedingRepository extends JpaRepository<Seeding, String> {

    Seeding findByToken(String token);

    Seeding findByTokenAndCreatedAtGreaterThan(String token, LocalDateTime createdAt);

}
