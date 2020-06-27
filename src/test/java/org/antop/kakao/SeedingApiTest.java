package org.antop.kakao;

import org.antop.kakao.controller.SprinkleRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;

import static org.hamcrest.Matchers.hasLength;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class SeedingApiTest extends SeedingTests {

    @Test
    @DisplayName("뿌리기 요청건에 대한 고유 token을 발급하고 응답값으로 내려줍니다.")
    void test001() throws Exception {
        SprinkleRequest request = new SprinkleRequest();
        request.setAmount(1000);
        request.setCount(4);

        sprinkle("QWER", 1234, 1000, 4)
                .andExpect(status().isCreated())
                .andExpect(header().exists(HttpHeaders.LOCATION))
                .andExpect(jsonPath("$.code", is("0000")))
                .andExpect(jsonPath("$.body", hasLength(3)))
        ;
    }
}
