package org.antop.kakao.jpa.entity;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
@EqualsAndHashCode
public class PickupId {
    private final long seeding;
    private final long seq;
}
