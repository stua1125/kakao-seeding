package org.antop.kakao.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.antop.kakao.constants.Codes;
import org.antop.kakao.constants.Header;
import org.antop.kakao.dto.PickupDto;
import org.antop.kakao.dto.SeedingDto;
import org.antop.kakao.jpa.entity.Pickup;
import org.antop.kakao.jpa.entity.Seeding;
import org.antop.kakao.service.SeedingService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/api/v1", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
public class SeedingApiController {
    private final SeedingService seedingService;

    @PostMapping
    ResponseEntity<ApiResponse> sprinkle(
            @RequestHeader(Header.ROOM_ID) String roomId,
            @RequestHeader(Header.USER_ID) long userId,
            @RequestBody SprinkleRequest request,
            UriComponentsBuilder b) {
        log.debug("roomId={}, userId={}, body={}", roomId, userId, request);

        Seeding seeding = seedingService.sprinkle(roomId, userId, request.getAmount(), request.getCount());

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(b.path("/{token}").buildAndExpand(seeding.getToken()).toUri());

        ApiResponse response = ApiResponse.of(Codes.S0000, seeding.getToken());
        return new ResponseEntity<>(response, headers, HttpStatus.CREATED);
    }

    @PutMapping("/{token:[a-zA-Z]{3}}")
    ApiResponse pickup(
            @RequestHeader(Header.ROOM_ID) String roomId,
            @RequestHeader(Header.USER_ID) long userId,
            @PathVariable("token") String token) {
        log.debug("roomId={}, userId={}, token={}", roomId, userId, token);
        long amount = seedingService.pickup(roomId, userId, token);
        log.debug("amount = {}", amount);
        return ApiResponse.of(Codes.S0000, amount);
    }

    @GetMapping(value = "/{token:[a-zA-Z]{3}}")
    ApiResponse inquire(
            @RequestHeader(Header.ROOM_ID) String roomId,
            @RequestHeader(Header.USER_ID) long userId,
            @PathVariable("token") String token) {
        log.debug("roomId={}, userId={}, token={}", roomId, userId, token);
        Seeding seeding = seedingService.inquiry(userId, token);

        SeedingDto dto = new SeedingDto(
                seeding.getCreatedAt(),
                seeding.getAmount(),
                seeding.getPickups().stream().filter(Pickup::isReceived).mapToLong(Pickup::getAmount).sum(),
                seeding.getPickups().stream()
                        .filter(Pickup::isReceived)
                        .map(it -> new PickupDto(it.getUserId(), it.getAmount()))
                        .collect(Collectors.toList())
        );
        return ApiResponse.of(Codes.S0000, dto);
    }

}
