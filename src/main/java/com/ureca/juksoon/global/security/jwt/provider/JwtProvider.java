package com.ureca.juksoon.global.security.jwt.provider;

import com.ureca.juksoon.domain.user.entity.UserRole;
import com.ureca.juksoon.global.security.oauth.userdetail.PrincipalKey;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import java.util.Base64;
import java.util.Date;
import javax.crypto.SecretKey;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * JWT 토큰만을 다루는 클래스이다.
 * JWT 생성, claims getter 등 JWT에 대한 메서드들이 있다.
 * JWT는 Bearer를 빼준 순수 String 토큰만 다룬다.
 * 런타임 시점 모든 필드들이 초기화 즉, 싱글톤 관리 @Component
 */

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtProvider {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.access-token-validity-ms}")
    private long jwtTokenValidityMs;
    private SecretKey key;

    @PostConstruct
    public void init(){
        byte[] keyBytes = Base64.getDecoder().decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    /*
      토큰을 생성하는 메서드
      claim에는 role, provider, providerId, userId를 넣었다.
      아래 get메서드들로 claim들을 가져올 수 있다.
     */
    public String generateJwtToken(Long userId, UserRole userRole) {
        Date now = new Date();
        Date exp = new Date(now.getTime() + jwtTokenValidityMs);    //현재시간 + 유효시간 = 만료되는 시간

        return Jwts.builder()
                .claim(PrincipalKey.USER_ID.getKey(), userId)
                .claim(PrincipalKey.USER_ROLE.getKey(), userRole.getUserRole())
                .expiration(exp)
                .signWith(this.key)
                .compact();
    }

    // userid를 반환하는 메서드
    public Long getUserId(String jwt){
        return getClaims(jwt).get(PrincipalKey.USER_ID.getKey(), Long.class);
    }

    //userRole을 반환하는 메서드
    public UserRole getRole(String jwt){
        return UserRole.valueOf(getClaims(jwt).get(PrincipalKey.USER_ROLE.getKey(), String.class));
    }

    /*
    서명된 JWT 토큰을 Base64로 디코딩, key로 검증 후 JWT의 Claims를 Claims 객체 형태로 돌려준다.
    만약, setSigningKey에서 다른 Secret Key로 변조되었다면, SignatureException을 반환한다.(이는 JwtAuthenticationFilter에서 처리)
    이 메서드를 통해 여러 claim들 getter를 써줄 수 있겠다.
     */
    private Claims getClaims(String jwt) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(jwt)
                .getPayload();
    }
}