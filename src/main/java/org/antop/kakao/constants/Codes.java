package org.antop.kakao.constants;

public enum Codes {

    S0000("0000", "정상 처리"),
    E0001("0001", "요청 값 누락"),
    E0002("0002", "잘못된 요청"),
    E0003("0003", "권한 없음"),
    E0004("0004", "찾을 수 없음");

    public final String code;
    public final String description;

    Codes(String code, String description) {
        this.code = code;
        this.description = description;
    }
}
