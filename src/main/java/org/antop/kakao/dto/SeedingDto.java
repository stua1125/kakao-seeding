package org.antop.kakao.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@ToString
@RequiredArgsConstructor
public class SeedingDto {
    /**
     * 뿌린 시각
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyyMMddHHmmss")
    private final LocalDateTime datetime;
    /**
     * 뿌린 금액
     */
    private final long amount;
    /**
     * 받기 완료된 금액
     */
    private final long pickupAmount;
    /**
     * 받기 완료된 정보
     */
    private final List<PickupDto> pickups;
}
