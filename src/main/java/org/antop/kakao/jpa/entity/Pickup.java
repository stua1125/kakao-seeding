package org.antop.kakao.jpa.entity;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@ToString
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@RequiredArgsConstructor
@Table(name = "tb_pickup")
public class Pickup implements Serializable {
    /**
     * 고유 아이디
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pickup_id")
    private Long id;

    /**
     * 뿌리기 엔티티
     */
    @NonNull
    @ManyToOne(targetEntity = Seeding.class, fetch = FetchType.EAGER)
    @JoinColumn(name = "token")
    private Seeding seeding;

    /**
     * 순번
     */
    @NonNull
    @Column(name = "seq", nullable = false)
    private Integer seq;

    /**
     * 받은 금액
     */
    @NonNull
    @Column(name = "amount", nullable = false)
    private Long amount;

    /**
     * 받은 사용자 식별값
     */
    @Column(name = "user_id")
    private Long userId;

    /**
     * 받은 일시
     */
    @Column(name = "pickup_at")
    private LocalDateTime pickupAt;

    /**
     * 받는다.
     *
     * @param userId 사용자 식별값
     */
    public void pickup(long userId) {
        this.userId = userId;
        pickupAt = LocalDateTime.now();
    }

    /**
     * 받아갔니?
     *
     * @return {@code} 받았어
     */
    public boolean isReceived() {
        return Objects.nonNull(userId);
    }

    /**
     * 아직 안 받아갔니?
     *
     * @return {@code true} 아직 안 받아갔어
     */
    public boolean isNotReceived() {
        return !isReceived();
    }

}
