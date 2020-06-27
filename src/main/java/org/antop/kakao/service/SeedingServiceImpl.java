package org.antop.kakao.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.antop.kakao.exception.*;
import org.antop.kakao.feature.TokenGenerator;
import org.antop.kakao.jpa.entity.Pickup;
import org.antop.kakao.jpa.entity.Seeding;
import org.antop.kakao.jpa.repository.PickupRepository;
import org.antop.kakao.jpa.repository.SeedingRepository;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class SeedingServiceImpl implements SeedingService {
    private final TokenGenerator tokenGenerator;
    private final SeedingRepository seedingRepository;
    private final PickupRepository pickupRepository;

    @Transactional
    @Override
    public Seeding sprinkle(String roomId, long userId, long amount, int count) {
        String token = tokenGenerator.generate();
        Seeding seeding = new Seeding(token, roomId, userId, amount, count);
        seedingRepository.save(seeding);
        long[] divide = divide(seeding.getAmount(), seeding.getCount());
        for (int i = 0; i < divide.length; i++) {
            Pickup pickup = new Pickup(seeding, i + 1, divide[i]);
            pickupRepository.save(pickup);
        }
        return seeding;
    }

    @Transactional
    @Override
    public long pickup(String roomId, long userId, String token) {
        Seeding seeding = seedingRepository.findByToken(token);

        if (Objects.isNull(seeding)) { // 조회된 뿌리기 없음
            throw new SeedingNotFoundException();
        }
        if (seeding.isExpired(10)) { // 만료됨 (종료됨)
            throw new SeedingCompletedException();
        }
        if (!seeding.getRoomId().equals(roomId)) { // 다른 대화방 사용자
            throw new NotBelongRoomException();
        }
        if (seeding.getUserId() == userId) { // 뿌린 사용자가 주울려고 함
            throw new SelfPickupException();
        }
        if (seeding.getPickups().stream().noneMatch(Pickup::isNotReceived)) { // 모두 주워감
            throw new SeedingCompletedException();
        }

        if (seeding.getPickups().stream()
                .filter(Pickup::isReceived)
                .anyMatch(it -> it.getUserId() == userId)) { // 한번만 받을 수 있다.
            throw new DuplicatedPickupException();
        }

        List<Pickup> notPickedUpYet = seeding.getPickups().stream() // 아직 주워가지 않은 건만 필터
                .filter(Pickup::isNotReceived)
                .collect(Collectors.toList());
        // 첫번째 받기
        Pickup pickup = notPickedUpYet.get(0);
        // 주웠다!
        pickup.pickup(userId);
        // 주운 금액 리턴
        return pickup.getAmount();
    }

    @Transactional(readOnly = true)
    @Override
    public Seeding inquiry(long userId, String token) {
        LocalDateTime createdAt = LocalDateTime.of(LocalDate.now(), LocalTime.MAX).minusDays(7);

        Seeding seeding = seedingRepository.findByTokenAndCreatedAtGreaterThan(token, createdAt);
        log.debug("seeding = {}", seeding);
        if (Objects.isNull(seeding)) {
            throw new SeedingNotFoundException();
        }
        if (seeding.getUserId() != userId) {
            throw new AccessDeniedException();
        }

        return seeding;
    }

    /**
     * 금액을 나눈다.
     */
    private long[] divide(long amount, int count) {
        long[] array = new long[count];
        long max = RandomUtils.nextLong(amount / count, amount / count * 2);
        for (int i = 0; i < count - 1; i++) {
            array[i] = RandomUtils.nextLong(1, Math.min(max, amount));
            amount -= array[i];
        }
        array[count - 1] = amount;
        return array;
    }
}
