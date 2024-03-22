# Advanced Another Art `Backend` (Refactoring)

> [Origin Project 바로가기](https://github.com/yumyeonghan/Another_Art) <br>
> - 백엔드 위주 리팩토링

## 목차

1. [소개](#-소개)
2. [서비스 화면](#-서비스-화면)
3. [주요 챌린지](#-주요-챌린지)
4. [기술 스택](#-기술-스택)
5. [CI/CD Pipeline](#-cicd-pipeline)
6. [백엔드 요청 흐름도](#-백엔드-요청-흐름도)
7. [모니터링 구조도](#-모니터링-구조도)

<br>

## 🌙 소개
#### AI 객체탐지 기반 작품 조회 + 작품 경매 및 구매 플랫폼

<br>

## 🖥 서비스 화면

|![1  메인페이지](https://github.com/sjiwon/Advanced-Another-Art/assets/51479381/30cffd21-660e-489b-9ed1-8f337c84bd08)|![2  회원가입   로그인](https://github.com/sjiwon/Advanced-Another-Art/assets/51479381/4c423d76-23dc-4ea9-94d0-b8e14e30557b)|![3  작품 등록](https://github.com/sjiwon/Advanced-Another-Art/assets/51479381/3f81414c-7279-43b8-8e54-cb3e9f754bb2)|![4  작품 찜](https://github.com/sjiwon/Advanced-Another-Art/assets/51479381/851396db-7794-4225-b380-fb1f2805f254)|
|:---:|:---:|:---:|:---:|
|1. 메인페이지|2. 회원가입 & 로그인|3. 작품 등록|4. 작품 찜|
|![5  경매 작품 입찰](https://github.com/sjiwon/Advanced-Another-Art/assets/51479381/5e793ca9-0649-4c7c-9f3d-72b1a0467cb4)|![6  경매작품, 일반작품 구매](https://github.com/sjiwon/Advanced-Another-Art/assets/51479381/1d720286-f5a4-4a58-8231-a73afe97f6b0)|![7  마이페이지](https://github.com/sjiwon/Advanced-Another-Art/assets/51479381/ddf6371c-c334-405b-915d-0f92eded536e)|![8  AI 작품 검색](https://github.com/sjiwon/Advanced-Another-Art/assets/51479381/73b88b75-863a-45eb-b0ad-940c8b37477d)|
|5. 경매작품 입찰|6. 경매/일반 작품 구매|7. 마이페이지|8. AI 작품 검색|

<br>

## 🔥 주요 챌린지

- [이전 API의 보안 문제를 개선하기 위해서 Redis를 활용한 메일 인증 프로세스 적용기](https://sjiwon-dev.tistory.com/31)
- [비동기를 활용한 인증 메일 전송이 포함된 로직 성능 개선](https://sjiwon-dev.tistory.com/32)
- 경매 입찰 & 작품 구매 동시성 처리
  - [DB Lock 적용기](https://sjiwon-dev.tistory.com/33)
  - [MySQL Named Lock 적용기](https://sjiwon-dev.tistory.com/34)
  - [Redis Lock 적용기](https://sjiwon-dev.tistory.com/35)

<br>

## 🛠 기술 스택

### Backend

![Tech Stack - Backend](https://github.com/sjiwon/Advanced-Another-Art/assets/51479381/24d8acf2-86f8-412a-845d-32827e6eb2b6)

### Infra

![Tech Stack - Infra](https://github.com/sjiwon/Advanced-Another-Art/assets/51479381/4576e3b2-7a1f-4212-9b85-af39374fc1f6)

<br>

## 🚀 CI/CD Pipeline

![CI-CD Pipeline](https://github.com/sjiwon/Advanced-Another-Art/assets/51479381/6e2907f5-4457-405a-9138-6949667866dc)

<br>

## 🛒 백엔드 요청 흐름도

> Backend 위주의 리팩토링이기 때문에 Backend 요청 흐름만 명시
> - 아래 흐름도에서 DNS Resolving 흐름은 생략

### 작품 이미지 업로드/요청 흐름도

![작품 이미지 업로드, 요청 흐름도](https://github.com/sjiwon/Advanced-Another-Art/assets/51479381/dfc0e5a8-494d-404c-a22d-41d00010e04d)

### 서버 API 호출 흐름도

![서버 API 호출 흐름도](https://github.com/sjiwon/Advanced-Another-Art/assets/51479381/f7401d1d-54b6-4f25-8eae-ed27d1e2573c)

<br>

## 💻 모니터링 구조도

![모니터링 구조도](https://github.com/sjiwon/Advanced-Another-Art/assets/51479381/a8b400bf-38b0-4dfc-9d8d-25c169ad28e0)

<br>

