package org.antop.kakao.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@ToString
@Getter
@RequiredArgsConstructor
public class PickupDto {
    private final long userId;
    private final long amount;
}
