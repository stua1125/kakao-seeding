package org.antop.kakao.exception;

public class SelfPickupException extends ValidationException {

    public SelfPickupException() {
        super("자신이 뿌리기한 건은 자신이 받을 수 없습니다.");
    }
}
