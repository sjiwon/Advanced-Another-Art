# Advanced Another Art

> 기존 프로젝트 도메인 재설계 및 리팩토링을 위한 레포지토리<br>
> [Origin Project 바로가기](https://github.com/yumyeonghan/Another_Art)


## 주요 포인트
- Vue.js 재구현
- 백엔드 도메인 구조 다시 설계
- 테스트 코드

### 토큰 전략
> Client - Server간의 관계를 <code>Stateless</code>하게 가져가기 위해서 이전에 사용하던 세션 전략 대신 토큰 전략을 채택

- 사용자는 로그인 후 <code>Access Token & Refresh Token</code>을 발급받는다
- 이 후 모든 요청에 대해서 사용자는 Access Token을 <code>Authorization 헤더</code>에 포함해서 요청을 보내야 한다
- 사용자가 보유한 Refresh Token이 유효하다면 Refresh Token을 통해서 Access Token을 재발급 받을 수 있다
    - <code>RTR (Refresh Token Rotation)</code>전략을 활용해서 Refresh Token으로 Access Token을 발급받는 순간 Refresh Token도 다시 재발급한다
- 로그아웃 시 Access Token & Refresh Token은 만료된다

### [API 명세 바로가기](https://sjiwon.notion.site/Advanced-Another-Art-API-993210355b844b3c9b892f23eb058e7b)
- 각각의 API에 대한 <code>요청 및 응답</code>은 Spring Rest Docs를 통해서 확인 가능