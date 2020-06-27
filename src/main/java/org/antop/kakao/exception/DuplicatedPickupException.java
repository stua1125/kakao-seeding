package org.antop.kakao.exception;

public class DuplicatedPickupException extends ValidationException {

    public DuplicatedPickupException() {
        super("뿌리기 당 한 사용자는 한번만 받을 수 있습니다.");
    }

}
