package org.antop.kakao.feature;

public interface TokenGenerator {

    /**
     * 3자리 뿌리기 토큰 생성
     *
     * @return 생성된 토큰값
     */
    default String generate() {
        return generate(3);
    }

    /**
     * 뿌리기 토큰 생성
     *
     * @param length 생성 길이
     * @return 생성된 토큰값
     */
    String generate(int length);

}
