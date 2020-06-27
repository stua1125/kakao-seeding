package org.antop.kakao;

import org.antop.kakao.constants.Codes;
import org.antop.kakao.jpa.entity.Seeding;
import org.antop.kakao.jpa.repository.SeedingRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class PickupApiTest extends SeedingTests {
    @MockBean
    private SeedingRepository repository;

    @Test
    @DisplayName("받기 요청 하면 금액을 응답값으로 내려준다")
    void test001() throws Exception {
        Seeding seeding = stub();

        when(repository.findByToken(anyString())).thenReturn(seeding);

        pickup("ASD", "Z", 90)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is("0000")))
                .andExpect(jsonPath("$.body").value(seeding.getPickups().get(1).getAmount()))
        ;
    }

    @Test
    @DisplayName("뿌리기 당 한 사용자는 한번만 받을 수 있습니다.")
    void test002() throws Exception {
        when(repository.findByToken(anyString())).thenReturn(stub());

        pickup("ASD", "Z", 20) // 20 사용자는 이미 받음
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code", is(Codes.E0002.code)))
                .andExpect(jsonPath("$.message", notNullValue()))
        ;
    }

    @Test
    @DisplayName("자신이 뿌리기한 건은 자신이 받을 수 없습니다.")
    void test003() throws Exception {
        when(repository.findByToken(anyString())).thenReturn(stub());

        pickup("ASD", "Z", 10)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code", is(Codes.E0002.code)))
                .andExpect(jsonPath("$.message", notNullValue()))
        ;
    }

    @Test
    @DisplayName("뿌린이가 호출된 대화방과 동일한 대화방에 속한 사용자만이 받을 수 있습니다.")
    void test004() throws Exception {
        when(repository.findByToken(anyString())).thenReturn(stub());

        pickup("ASD", "Y", 60) // "Y"는 다른 대화방
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code", is(Codes.E0002.code)))
                .andExpect(jsonPath("$.message", notNullValue()))
        ;
    }

    @Test
    @DisplayName("뿌린 건은 10분간만 유효합니다.")
    void test005() throws Exception {
        Seeding seeding = Mockito.mock(Seeding.class);
        when(seeding.isExpired(anyInt())).thenReturn(true); // 만료를 응답함
        when(repository.findByToken(anyString())).thenReturn(seeding);

        pickup("ASD", "Z", 60)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code", is(Codes.E0002.code)))
                .andExpect(jsonPath("$.message", notNullValue()))
        ;
    }

}
