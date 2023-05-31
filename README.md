# Advanced Another Art

> 기존 프로젝트 도메인 재설계 및 리팩토링을 위한 레포지토리<br>
> [Origin Project 바로가기](https://github.com/yumyeonghan/Another_Art)

- 비용 문제로 인해 Naver Cloud Platform FE & BE Server는 비활성화

<br>

## 🌙 소개
#### AI 객체탐지 기반 작품 검색과 작품 경매 & 구매가 가능한 웹 애플리케이션

<br>

## 🖥 서비스 화면

|![1  메인페이지](https://github.com/sjiwon/readme/assets/51479381/1e5c63e3-4805-42fe-8d07-97c45e9e7307)|![2  회원가입   로그인](https://github.com/sjiwon/readme/assets/51479381/3c66fb1f-8df3-4b78-9cf0-c5e9beeb3f92)|![3  작품 등록](https://github.com/sjiwon/readme/assets/51479381/30896c91-6d45-44e5-83ba-51d455a493d9)|![4  작품 찜](https://github.com/sjiwon/readme/assets/51479381/bace72b2-a1f5-4fb5-9e37-feb50d9ebf17)|
|:---:|:---:|:---:|:---:|
|1. 메인페이지|2. 회원가입 & 로그인|3. 작품 등록|4. 작품 찜|
|![5  경매 작품 입찰](https://github.com/sjiwon/readme/assets/51479381/c1aedaad-eb25-4985-b0ac-d72a7fb5a657)|![6  경매작품, 일반작품 구매](https://github.com/sjiwon/readme/assets/51479381/d9665284-e96b-4a6f-9bfa-f456b70658a8)|![7  마이페이지](https://github.com/sjiwon/readme/assets/51479381/09201038-3221-4a71-bb8d-88e77aa9139c)|![8  AI 작품 검색](https://github.com/sjiwon/readme/assets/51479381/00c5182f-a9b3-4232-88fa-0a3804ea7b36)|
|5. 경매작품 입찰|6. 경매/일반 작품 구매|7. 마이페이지|8. AI 작품 검색|

### [API 명세서 바로가기](https://sjiwon.notion.site/Another-Art-API-d3cadad3af10416aa3c1f08e52a7ccd1)

<br>

## 🔥 챌린지

### 경매 작품 입찰에 대한 동시성 이슈
- [Pessimistic Lock -> Redis Distributed Lock(Redisson Client) 적용](https://github.com/sjiwon/Advanced-Another-Art/issues/2)
- 입찰 로직
    - [Facade Layer](https://github.com/sjiwon/Advanced-Another-Art/blob/main/backend/src/main/java/com/sjiwon/anotherart/auction/facade/BidFacade.java)
    - [Service Layer](https://github.com/sjiwon/Advanced-Another-Art/blob/main/backend/src/main/java/com/sjiwon/anotherart/auction/service/BidService.java)
    - [Domain - Auction](https://github.com/sjiwon/Advanced-Another-Art/blob/main/backend/src/main/java/com/sjiwon/anotherart/auction/domain/Auction.java)
    - [Domain - Bidders](https://github.com/sjiwon/Advanced-Another-Art/blob/main/backend/src/main/java/com/sjiwon/anotherart/auction/domain/Bidders.java)

<br>

## ✍️ 유의사항
> git clone 후 테스트 코드를 실행할 시 <code>Docker</code>가 설치되고 활성화되어 있어야 합니다. 

<br>

## 🛠 Tech Stacks

### Frontend
![FE 스택](https://github.com/sjiwon/readme/assets/51479381/2b01e0f6-c66b-4a04-8d31-eda550f403f3)


### Backend
![BE 스택](https://github.com/sjiwon/readme/assets/51479381/81be8580-743e-4d4f-990a-c199a89a5990)


### Infra
![Infra 스택](https://github.com/sjiwon/readme/assets/51479381/78bb4e63-08bf-436d-8849-21e9ac89463b)


<br>

## ⚙️ Infrastructure
![사용자 요청 흐름도](https://github.com/sjiwon/readme/assets/51479381/c6993787-eda2-4d2c-99bb-5b92385c0ada)

<br>

## 🔀 CI/CD Pipeline
### Frontend CI/CD
![FE CI-CD](https://github.com/sjiwon/readme/assets/51479381/475f97a9-5534-4517-9425-9f74c239d98e)

### Backend CI/CD
![BE CI-CD](https://github.com/sjiwon/readme/assets/51479381/991d2983-25a0-44a5-bcb5-e3e2f5f9d658)



