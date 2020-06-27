package org.antop.kakao.jpa.repository;

import org.antop.kakao.jpa.entity.Pickup;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PickupRepository extends JpaRepository<Pickup, Long> {
}
