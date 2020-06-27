package org.antop.kakao.exception;

public class SeedingCompletedException extends ValidationException {

    public SeedingCompletedException() {
        super("종료된 뿌리기 입니다.");
    }

}
