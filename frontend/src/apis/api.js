export const API_PATH = {
  MEMBER: {
    LOGIN: `/api/login`, // 로그인
    LOGOUT: `/api/logout`, // 로그아웃
    DUPLICATE_LOGIN_ID: `/api/members/duplicate/login-id`, // 로그인 아이디 중복체크
    DUPLICATE_EMAIL: `/api/members/duplicate/email`, // 이메일 중복체크
    DUPLICATE_NICKNAME: `/api/members/duplicate/nickname`, // 닉네임 중복체크
    DUPLICATE_PHONE: `/api/members/duplicate/phone`, // 전화번호 중복체크
    SIGN_UP: `/api/members`, // 회원가입
    UPDATE_NICKNAME: `/api/members/me/nickname`, // 닉네임 수정
    UPDATE_ADDRESS: `/api/members/me/address`, // 주소 수정
    UPDATE_PASSWORD: `/api/members/me/password`, // 비밀번호 수정
    AUTH_FOR_RETRIEVE_LOGIN_ID: `/api/members/retrieve-login-id`, // 아이디 찾기 인증
    RETRIEVE_LOGIN_ID: `/api/members/retrieve-login-id/confirm`, // 아이디 찾기 인증번호 확인 및 제공
    AUTH_FOR_RESET_PASSWORD: `/api/members/reset-password/auth`, // 비밀번호 재설정 인증
    CONFIRM_AUTH_CODE_RESET_PASSWORD: `/api/members/reset-password/auth/confirm`, // 비밀번호 재설정 인증번호 확인
    RESET_PASSWORD: `/api/members/reset-password`, // 비밀번호 재설정
    GET_INFORMATION: `/api/members/me`, // 사용자 기본 정보 조회
    GET_POINT_RECORDS: `/api/members/me/points`, // 포인트 활용 내역 조회
    GET_WINNING_AUCTION_ARTS: `/api/members/me/arts/winning-auction`, // 낙찰된 경매 작품 조회
    GET_SOLD_ARTS: `/api/members/me/arts/sold`, // 판매한 작품 조회
    GET_PURCHASE_ARTS: `/api/members/me/arts/purchase`, // 구매한 작품 조회
    REISSUE_TOKEN: `/api/token/reissue`, // 토큰 재발급
    CHARGE_POINT: `/api/points/charge`, // 포인트 충전
    REFUND_POINT: `/api/points/refund`, // 포인트 환불
  },
  ART: {
    DUPLICATE_NAME: `/api/arts/duplicate/name`, // 작품명 중복체크
    REGISTER: `/api/arts`, // 작품 등록
    UPDATE: `/api/arts/{artId}`, // 작품 수정
    DELETE: `/api/arts/{artId}`, // 작품 삭제
    GET_SINGLE_ART: `/api/arts/{artId}`, // 작품 단건 조회
    GET_ACTIVE_AUCTION_ARTS: `/api/arts/{artId}/active-auction`, // 현재 진행중인 경매 작품 리스트 조회
    GET_AUCTION_ARTS_BY_KEYWORD: `/api/arts/{artId}/auction/keyword`, // 키워드 기반 경매 작품 리스트 조회
    GET_GENERAL_ARTS_BY_KEYWORD: `/api/arts/{artId}/general/keyword`, // 키워드 기반 일반 작품 리스트 조회
    GET_AUCTION_ARTS_BY_HASHTAG: `/api/arts/{artId}/auction/hashtag`, // 해시태그(객체 탐지) 기반 경매 작품 리스트 조회
    GET_GENERAL_ARTS_BY_HASHTAG: `/api/arts/{artId}/general/hashtag`, // 해시태그(객체 탐지) 기반 일반 작품 리스트 조회
    LIKE_MARKING: `/api/arts/{artId}/like`, // 작품 좋아요 등록
    LIKE_CANCELLATION: `/api/arts/{artId}/like`, // 작품 좋아요 취소
    PURCHASE: `/api/arts/{artId}/purchase`, // 작품 구매
  },
  AUCTION: {
    BID: `/api/auctions/{auctionId}/bid` // 경매 작품 입찰
  },
};
