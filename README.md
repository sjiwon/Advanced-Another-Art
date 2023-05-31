# Advanced Another Art

> 기존 프로젝트 도메인 재설계 및 리팩토링을 위한 레포지토리<br>
> [Origin Project 바로가기](https://github.com/yumyeonghan/Another_Art)

- 비용 문제로 인해 Naver Cloud Platform FE & BE Server는 비활성화

<br>

## 🌙 소개
#### AI 객체탐지 기반 작품 검색과 작품 경매 & 구매가 가능한 웹 애플리케이션

<br>

## 🖥 서비스 화면

|![1  메인페이지](https://github.com/sjiwon/Advanced-Another-Art/assets/51479381/30cffd21-660e-489b-9ed1-8f337c84bd08)|![2  회원가입   로그인](https://github.com/sjiwon/Advanced-Another-Art/assets/51479381/4c423d76-23dc-4ea9-94d0-b8e14e30557b)|![3  작품 등록](https://github.com/sjiwon/Advanced-Another-Art/assets/51479381/3f81414c-7279-43b8-8e54-cb3e9f754bb2)|![4  작품 찜](https://github.com/sjiwon/Advanced-Another-Art/assets/51479381/851396db-7794-4225-b380-fb1f2805f254)|
|:---:|:---:|:---:|:---:|
|1. 메인페이지|2. 회원가입 & 로그인|3. 작품 등록|4. 작품 찜|
|![5  경매 작품 입찰](https://github.com/sjiwon/Advanced-Another-Art/assets/51479381/5e793ca9-0649-4c7c-9f3d-72b1a0467cb4)|![6  경매작품, 일반작품 구매](https://github.com/sjiwon/Advanced-Another-Art/assets/51479381/1d720286-f5a4-4a58-8231-a73afe97f6b0)|![7  마이페이지](https://github.com/sjiwon/Advanced-Another-Art/assets/51479381/ddf6371c-c334-405b-915d-0f92eded536e)|![8  AI 작품 검색](https://github.com/sjiwon/Advanced-Another-Art/assets/51479381/73b88b75-863a-45eb-b0ad-940c8b37477d)|
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
![FE 스택](https://github.com/sjiwon/Advanced-Another-Art/assets/51479381/f8665ebb-444b-4653-9cac-43b7f68117a3)

### Backend
![BE 스택](https://github.com/sjiwon/Advanced-Another-Art/assets/51479381/9ae3dcc4-208b-491b-abe1-840bda68aee9)

### Infra
![Infra 스택](https://github.com/sjiwon/Advanced-Another-Art/assets/51479381/c0c72ef9-88e2-494d-864e-6a60d9fb1cf2)

<br>

## ⚙️ Infrastructure
![사용자 요청 흐름도](https://github.com/sjiwon/Advanced-Another-Art/assets/51479381/48db5945-b720-4e2c-bdf6-269fdaac8aa2)

<br>

## 🔀 CI/CD Pipeline
### Frontend CI/CD
![FE CI-CD](https://github.com/sjiwon/Advanced-Another-Art/assets/51479381/d112f48f-4dc0-431a-8d11-9113bbb22133)

### Backend CI/CD
![BE CI-CD](https://github.com/sjiwon/Advanced-Another-Art/assets/51479381/7a371722-1a4e-4cf0-931d-1d4d0b77b63c)



