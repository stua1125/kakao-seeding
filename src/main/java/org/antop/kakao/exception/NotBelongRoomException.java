package org.antop.kakao.exception;

public class NotBelongRoomException extends ValidationException {

    public NotBelongRoomException() {
        super("해당 대화방에 속해 있지 않습니다.");
    }

}
