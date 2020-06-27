package org.antop.kakao.exception;

public class SeedingNotFoundException extends NotFoundException {

    public SeedingNotFoundException() {
        super("뿌리기를 찾을 수 없습니다.");
    }
}
