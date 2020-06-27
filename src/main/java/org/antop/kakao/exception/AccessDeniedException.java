package org.antop.kakao.exception;

public class AccessDeniedException extends RuntimeException {
    public AccessDeniedException() {
        super("뿌린 사람 자신만 조회할 수 있습니다.");
    }
}
