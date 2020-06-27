package org.antop.kakao;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.antop.kakao.constants.Header;
import org.antop.kakao.controller.SprinkleRequest;
import org.antop.kakao.jpa.entity.Pickup;
import org.antop.kakao.jpa.entity.Seeding;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
abstract public class SeedingTests {
    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper mapper;

    protected Seeding stub() {
        Seeding seeding = new Seeding("ASD", "Z", 10L, 1000L, 4);
        Pickup pickup1 = new Pickup(seeding, 1, 330L);
        pickup1.pickup(20); // 20 사용자가 받아감
        seeding.getPickups().add(pickup1);
        seeding.getPickups().add(new Pickup(seeding, 2, 269L));
        seeding.getPickups().add(new Pickup(seeding, 3, 235L));
        seeding.getPickups().add(new Pickup(seeding, 4, 466L));
        return seeding;
    }

    protected ResultActions sprinkle(String roomId, long userId, long amount, int count) throws Exception {
        SprinkleRequest request = new SprinkleRequest();
        request.setAmount(amount);
        request.setCount(count);

        return mockMvc.perform(
                post("/api/v1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(Header.ROOM_ID, roomId)
                        .header(Header.USER_ID, userId)
                        .content(mapper.writeValueAsString(request))
        )
                .andDo(print())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                ;
    }

    protected ResultActions pickup(String token, String roomId, long userId) throws Exception {
        return mockMvc.perform(
                put("/api/v1/" + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(Header.ROOM_ID, roomId)
                        .header(Header.USER_ID, userId)
        )
                .andDo(print())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                ;
    }

    protected ResultActions inquiry(String token, String roomId, long userId) throws Exception {
        return mockMvc.perform(
                get("/api/v1/" + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(Header.ROOM_ID, roomId)
                        .header(Header.USER_ID, userId)
        )
                .andDo(print())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));
    }

}
