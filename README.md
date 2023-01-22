# Advanced Another Art

> 기존 프로젝트 도메인 재설계 및 리팩토링을 위한 레포지토리<br>
> [Origin Project 바로가기](https://github.com/yumyeonghan/Another_Art)


## 주요 포인트
- Vue.js 재구현
- 백엔드 도메인 구조 다시 설계
- 테스트 코드

## 토큰 전략
> Client - Server간의 관계를 <code>Stateless</code>하게 가져가기 위해서 이전에 사용하던 세션 전략 대신 토큰 전략을 채택

- 사용자는 로그인 후 <code>Access Token & Refresh Token</code>을 발급받는다
  - Refresh Token은 <code>Cookie</code>를 통해서 발급
    - <code>httpOnly, secure, max-age 설정</code>을 추가한 Cookie
- 이 후 모든 요청에 대해서 사용자는 Access Token을 <code>Authorization 헤더</code>에 포함해서 요청을 보내야 한다
- 사용자는 Refresh Token이 유효하다면 Refresh Token을 통해서 Access Token을 재발급 받을 수 있다
    - <code>RTR (Refresh Token Rotation)</code>전략을 활용해서 Refresh Token으로 Access Token을 발급받는 순간 Refresh Token도 다시 재발급한다
- 로그아웃 시 Access Token & Refresh Token은 만료된다
    - Redis 블랙리스트 활용 예정

### Refresh Token
- Refresh Token은 2가지 보관 전략을 고려
    1. DB
    2. Redis
- 이 중에서 일단 Redis로 Refresh Token을 관리하는 전략을 생각하였다
    - Redis는 데이터가 HDD나 SSD가 아닌 <code>RAM</code>에 저장되기 때문에 액세스 시간이 굉장히 빠르다
        - 이러한 이점으로 인해 Redis를 보통 캐싱 용도로 많이 활용한다
    - 기본적으로 Redis는 <code>TTL (Time To Live)</code>을 지정할 수 있기 때문에 Refresh Token에 대한 관리가 수월하다