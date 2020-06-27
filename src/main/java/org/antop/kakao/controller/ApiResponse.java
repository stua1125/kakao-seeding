package org.antop.kakao.controller;

import lombok.*;
import org.antop.kakao.constants.Codes;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@RequiredArgsConstructor
public class ApiResponse {
    @NonNull
    private String code;
    @NonNull
    private String message;
    @Setter
    private Object body;

    public static ApiResponse of(String code, String message) {
        return new ApiResponse(code, message);
    }

    public static ApiResponse of(Codes code) {
        return new ApiResponse(code.code, code.description);
    }

    public static ApiResponse of(Codes code, Object body) {
        ApiResponse response = of(code);
        response.setBody(body);
        return response;
    }

}
