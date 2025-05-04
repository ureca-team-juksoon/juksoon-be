# 프로젝트 : 맛집줍
### TEAM : 죽순

---

# 계층 구조 + 설명

## domain
- common
    - `BaseEntity` : 엔티티 공통 속성
- feed
    - controller
        - `FeedController` : 
- refresh
    - controller
        - `RefreshTokenController` : 리프레시 토큰 컨트롤러
    - entity
        - `RefreshToken` : 리프레시 토큰 엔티티
    - repository
        - `RefreshTokenRepository`
    - service
        - `RefreshTokenService`
- user : 유저 도메인
    - controller
        - `UserController` : 유저 컨트롤러
    - entity
        - `UserRole` : 유저 권한 나타내는 Enum
        - `User` : 유저 엔티티
    - service
        - `UserService` : 유저 서비스
    - repository
        - `UserRepository` : 유저 리포지토리
    - dto
        - `UserRoleReq` : 유저 롤 업뎃 요청 DTO
        - `UserRoleRes` : 유저 롤 업뎃 응답 DTO
    
## global
- config
    - `SecurityConfig` : 시큐리티 설정
    - `SwaggerConfig` : 스웨거 설정
- exception
    - handler
        - `GlobalExceptionHandler`
  - `GlobalException(Exception)`
- response
    - `CommonResponse`
    - `CustomHeaderType(Enum)` : 시큐리티 헤더 타입
    - `ResultCode(Enum)` : 기본 결과
- security
    - jwt : jwt를 통한 인증 흐름
        - filter : jwt 필터
            - `JwtAuthenticationFilter` : jwt 인증 필터
        - provider : jwt/refresh-token 제공/파싱 유틸
            - `JwtProvider` : jwt 제공/파싱 유틸
            - `RefreshTokenProvider` : refresh-token 제공/파싱 유틸
        - userdetail : Authentication에 넣어줌
            - `CustomUserDetails` : AuthenticationToken에 넣어주는 클래스
    - oauth : OAuth 소셜 로그인 흐름
        - converter : converter 모음
            - `OAuth2ResponseConverter` : Access Token으로 받아온 유저 정보를 Convert 해준다.
        - handler : OAuth 로그인 성공/실패 핸들러
            - `CustomOAuth2AuthenticationFailureHandler(class)` : 인증 성공 시
        - service : OAuth 로그인 필요 서비스
          - `CustomOAuth2UserService(class)` : 유저 존재 여부 확인 서비스
        - response : Access Token으로 받아온 유저 정보
            - `OAuth2Response(interface)` : 확장성을 고려한 인터페이스(각 scope 주는 방식이 다르기 때문) 
            - `KakaoResponse` : 카카오 사용자 정보 API에서 받아온 정보
            - `Scope(Enum)` : 카카오 스코프의 키들
        - userdetail : Authentication에 넣어줌
            - `CusomOAuth2User` : AuthenticationToken에 넣어주는 클래스
            - `PrincipalKey` : CustomOAuth2User map의 키들
---