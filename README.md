# 카카오페이 뿌리기 기능 구현하기

## 요구 사항

* 뿌리기, 받기, 조회 기능을 수행하는 REST API 를 구현합니다.
    * 요청한 사용자의 식별값은 숫자 형태이며 "X-USER-ID" 라는 HTTP Header로 전달됩니다. → `org.antop.kakao.XHeaderTest#test001`
    * 요청한 사용자가 속한 대화방의 식별값은 문자 형태이며 "X-ROOM-ID" 라는 HTTP Header로 전달됩니다.  → `org.antop.kakao.XHeaderTest#test002`
    * 모든 사용자는 뿌리기에 충분한 잔액을 보유하고 있다고 가정하여 별도로 잔액에 관련된 체크는 하지 않습니다.
    * 작성하신 어플리케이션이 다수의 서버에 다수의 인스턴스로 동작하더라도 기능에 문제가 없도록 설계되어야 합니다.
    * 각 기능 및 제약사항에 대한 단위테스트를 반드시 작성합니다.

## 상세 구현 요건 및 제약사항

### 1. 뿌리기 API → `POST` /api/v1

* 다음 조건을 만족하는 뿌리기 API를 만들어 주세요.
    * 뿌릴 금액, 뿌릴 인원을 요청값으로 받습니다.
    * 뿌리기 요청건에 대한 고유 token을 발급하고 응답값으로 내려줍니다. → `org.antop.kakao.SeedingApiTest#test001`
    * 뿌릴 금액을 인원수에 맞게 분배하여 저장합니다. (분배 로직은 자유롭게 구현해 주세요.)
    * token은 3자리 문자열로 구성되며 예측이 불가능해야 합니다.

### 2. 받기 API → `PUT` /api/v1/{token}

* 다음 조건을 만족하는 받기 API를 만들어 주세요.
    * 뿌리기 시 발급된 token을 요청값으로 받습니다.
    * token에 해당하는 뿌리기 건 중 아직 누구에게도 할당되지 않은 분배건 하나를 API를 호출한 사용자에게 할당하고, 그 금액을 응답값으로 내려줍니다. → `org.antop.kakao.PickupApiTest#test001`
    * 뿌리기 당 한 사용자는 한번만 받을 수 있습니다. → `org.antop.kakao.PickupApiTest#test002`
    * 자신이 뿌리기한 건은 자신이 받을 수 없습니다. →  `org.antop.kakao.PickupApiTest#test003`
    * 뿌린이가 호출된 대화방과 동일한 대화방에 속한 사용자만이 받을 수 있습니다. → `org.antop.kakao.PickupApiTest#test004`
    * 뿌린 건은 10분간만 유효합니다. 뿌린지 10분이 지난 요청에 대해서는 받기 실패 응답이 내려가야 합니다. → `org.antop.kakao.PickupApiTest#test005`

### 3. 조회 API → `GET` /api/v1/{token}

* 다음 조건을 만족하는 조회 API를 만들어 주세요.
    * 뿌리기 시 발급된 token을 요청값으로 받습니다.
    * token에 해당하는 뿌리기 건의 현재 상태를 응답값으로 내려줍니다. 현재 상태는 다음의 정보를 포함합니다. → `org.antop.kakao.InquiryApiTest#test001`
    * 뿌린 시각, 뿌린 금액, 받기 완료된 금액, 받기 완료된 정보 ([받은 금액, 받은 사용자 아이디] 리스트)  →  `org.antop.kakao.InquiryApiTest#test001`
    * 뿌린 사람 자신만 조회를 할 수 있습니다. 다른사람의 뿌리기건이나 유효하지 않은 token에 대해서는 조회 실패 응답이 내려가야 합니다. → `org.antop.kakao.InquiryApiTest#test002`, `org.antop.kakao.InquiryApiTest#test003`
    * 뿌린 건에 대한 조회는 7일 동안 할 수 있습니다. → `org.antop.kakao.InquiryApiTest#test004`

## 구현

### 계획

![](https://i.imgur.com/bPJt63L.png)

1. `Redis`(또는 `Hazelcast`)를 이용한 글로벌 캐싱
2. `Lock`을 이용한 동시성 해결
3. 캐시와 데이터베이스 상태 동기

### 현실

* ~~Redi를 이용한 글로벌 캐싱~~
* ~~Lock을 이용한 동시성 해결~~
* ~~캐시와 데이터베이스 데이터의 상태 동기~~
* 데이터베이스에 등록/수정

### 데이터베이스

`MariaDB` 사용

```mysql
create database kakaopay;
create user 'kakaopay'@'%' identified by 'gH4nRAxHG9NrhNAP';
grant all privileges on kakaopay.* to 'kakaopay'@'%';
flush privileges;
```

테이블 생성

![](https://i.imgur.com/4MxsZ0M.png)

```mysql
create table tb_seeding
(
    token        char(3)      not null primary key,
    room_id      varchar(255) not null,
    user_id      bigint       not null,
    amount       bigint       not null,
    people_count int          not null
);

create table tb_pickup
(
    pickup_id  bigint   not null auto_increment primary key,
    token      char(3)  not null,
    seq        int      not null,
    amount     bigint   not null,
    pickup_at  datetime null,
    user_id    bigint   null,
    constraint tb_pickup_uindex unique (token, seq),
    constraint tb_pickup_ibfk_1 foreign key (token) references tb_seeding (token)
);
```

## 테스트

![](https://i.imgur.com/qUVF5zv.png)

## API 명세서

### 요청 공통

해더

| 항목         | 값 (예)          | 설명            |
| ------------ | ---------------- | --------------- |
| Content-Type | application/json | JSON` 으로 요청 |
| X-ROOM-ID    | A                | 대화방 식별값   |
| X-USER-ID    | 12               | 사용자 식별값   |

### 응답 공통

HTTP 응답코드

| 응답코드 | 설명                  |
| -------- | --------------------- |
| `200 OK` | **정상 응답**         |
| `400`    | 잘못된 요청           |
| `404`    | 리소스를 찾을 수 없음 |
| `500`    | 시스템 에러           |

해더

| 항목         | 값               | 설명             |
| ------------ | ---------------- | ---------------- |
| Content-Type | application/json | `JSON` 으로 응답 |

내용

| 이름    |  타입  | 필수 | 설명             |
| ------- | :----: | :---: | ---------------- |
| code    | string |  ○   | 응답 코드        |
| message | string |  ○   | 응답 메세지      |
| body    | string |  ×   | API 별 응답 내용 |

응답 예

```json
{
  "code": "0000",
  "message": "정상 처리",
  "body": null
}
```

### 뿌리기 API

#### 요청

| 항목 | 값             |
| ---- | -------------- |
| URL  | `POST` /api/v1 |

항목

| 이름       |  타입  | 필수 | 설명                                                         |
| ---------- | :----: | :---: | ------------------------------------------------------------ |
| amount     | long |  ○   | 뿌릴 금액                                           |
| count      | int  |  ○   | 뿌릴 인원                                           |

요청 예

```json
{
  "amount": 1000,
  "count": 4
}
```

#### 응답

응답 내용

| 이름 |  타입  | 필수 | 설명        |
| ---- | :----: | :---: | ----------- |
| body   | string |  ○   | 뿌리기 요청건에 대한 고유 token |

응답 예시

```json
{
  "code": "0000",
  "message": "정상 처리",
  "body": "fCz"
}
```

### 줍기(받기) API

#### 요청

| 항목 | 값             | 설명 |
| ---- | -------------- | --- |
| URL  | `PUT` /api/v1/{token} | `{token}` = 뿌리기 토큰 |

#### 응답

응답 내용

| 이름 |  타입  | 필수 | 설명        |
| ---- | :----: | :---: | ----------- |
| body   | long |  ○   | 받은 금액 |

응답 예시

```json
{
  "code": "0000",
  "message": "정상 처리",
  "body": 269
}
```

### 조회 API

#### 요청

| 항목 | 값             | 설명 |
| ---- | -------------- | --- |
| URL  | `GET` /api/v1/{token} | `{token}` = 뿌리기 토큰 |

#### 응답

응답 내용

| 이름 |  타입  | 필수 | 설명        |
| ---- | :----: | :---: | ----------- |
| body.datetime | string | ○ | 뿌린 시각 |
| body.amount | long | ○ | 뿌린 금액 |
| body.pickupAmount | long | ○ | 받기 완료된 금액 |
| body.pickups[].amount | long | ○ | 받은 금액 |
| body.pickups[].userId | long | ○ | 받은 사용자 식별값 |

응답 예시

```json
{
  "code": "0000",
  "message": "정상 처리",
  "body": {
    "datetime": "20200627143825",
    "amount": 1000,
    "pickupAmount": 330,
    "pickups": [
      {
        "userId": 20,
        "amount": 330
      }
    ]
  }
}
```

