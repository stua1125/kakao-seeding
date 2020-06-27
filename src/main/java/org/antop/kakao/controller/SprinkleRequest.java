package org.antop.kakao.controller;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SprinkleRequest {
    /**
     * 뿌릴 금액
     */
    private long amount;
    /**
     * 뿌릴 인원수
     */
    private int count;
}
