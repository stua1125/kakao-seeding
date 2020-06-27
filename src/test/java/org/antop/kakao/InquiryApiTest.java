package org.antop.kakao;

import org.antop.kakao.constants.Codes;
import org.antop.kakao.jpa.repository.SeedingRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class InquiryApiTest extends SeedingTests {
    @MockBean
    private SeedingRepository repository;

    @Test
    @DisplayName("token에 해당하는 뿌리기 건의 현재 상태를 응답값으로 내려줍니다.")
    void test001() throws Exception {
        when(repository.findByTokenAndCreatedAtGreaterThan(anyString(), any(LocalDateTime.class))).thenReturn(stub());

        inquiry("ASD", "Z", 10) // 뿌린 본인
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(Codes.S0000.code)))
                .andExpect(jsonPath("$.body.pickupAmount", is(330)))
                .andExpect(jsonPath("$.body.pickups.length()", is(1)))
                .andExpect(jsonPath("$.body.pickups[0].userId", is(20)))
                .andExpect(jsonPath("$.body.pickups[0].amount", is(330)))
        ;
    }

    @Test
    @DisplayName("뿌린 사람 자신만 조회를 할 수 있습니다.")
    void test002() throws Exception {
        when(repository.findByTokenAndCreatedAtGreaterThan(anyString(), any(LocalDateTime.class))).thenReturn(stub());

        inquiry("ASD", "Z", 90) // 뿌린 본인이 아님
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code", is(Codes.E0003.code)))
        ;
    }

    @Test
    @DisplayName("유효하지 않은 token에 대해서는 조회 실패응답이 내려가야 합니다.")
    void test003() throws Exception {
        when(repository.findByTokenAndCreatedAtGreaterThan(anyString(), any(LocalDateTime.class))).thenReturn(stub());

        pickup("ZZZ", "Z", 10) // "ZZZ" = 존재하지 않는 토큰
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code", is(Codes.E0004.code)))
                .andExpect(jsonPath("$.message", notNullValue()))
        ;
    }

    @Test
    @DisplayName("뿌린 건에 대한 조회는 7일동안 할 수 있습니다.")
    void test004() throws Exception {
        // 조회 데이터 없음
        when(repository.findByTokenAndCreatedAtGreaterThan(anyString(), any(LocalDateTime.class))).thenReturn(null);

        pickup("ASD", "Y", 60)
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code", is(Codes.E0004.code)))
                .andExpect(jsonPath("$.body", nullValue()))
        ;
    }

}
