package org.antop.kakao.feature;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Component;

@Component
public class TokenGeneratorImpl implements TokenGenerator {
    @Override
    public String generate(int length) {
        return RandomStringUtils.randomAlphabetic(length);
    }
}
