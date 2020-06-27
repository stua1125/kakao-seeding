package org.antop.kakao.jpa.entity;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@EqualsAndHashCode
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@RequiredArgsConstructor
@Entity
@Table(name = "tb_seeding")
public class Seeding implements Serializable {

    /**
     * 토큰
     */
    @NonNull
    @Id
    @Column(name = "token", length = 3, nullable = false)
    private String token;

    /**
     * 대화방 식별값
     */
    @NonNull
    @Column(name = "room_id", nullable = false)
    private String roomId;

    /**
     * 사용자 식별값
     */
    @NonNull
    @Column(name = "user_id", nullable = false)
    private Long userId;

    /**
     * 뿌린 금액
     */
    @NonNull
    @Column(name = "amount", nullable = false)
    private Long amount;

    /**
     * 받을 인원수
     */
    @NonNull
    @Column(name = "people_count", nullable = false)
    private int count;

    /**
     * 등록일시 (뿌린일시)
     */
    @Column(name = "created_at")
    @Setter
    private LocalDateTime createdAt = LocalDateTime.now();

    /**
     * 이미 배분된 받을 엔티티들
     */
    @ToString.Exclude
    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "token")
    @OrderBy("seq asc")
    private List<Pickup> pickups;

    public List<Pickup> getPickups() {
        if (Objects.isNull(pickups)) {
            pickups = new ArrayList<>();
        }
        return pickups;
    }

    /**
     * 만료 여부
     *
     * @param minutes 분
     * @return {@code true} 지정된 분보다 오래됨
     */
    public boolean isExpired(int minutes) {
        return createdAt.isBefore(LocalDateTime.now().minusMinutes(minutes));
    }
}
