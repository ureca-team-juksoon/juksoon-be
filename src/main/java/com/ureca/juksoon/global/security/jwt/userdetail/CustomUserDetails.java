package com.ureca.juksoon.global.security.jwt.userdetail;

import com.ureca.juksoon.domain.user.entity.UserRole;
import com.ureca.juksoon.global.security.oauth.userdetail.PrincipalKey;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@RequiredArgsConstructor
public class CustomUserDetails implements UserDetails {
    private final Map<String, Object> principals;

    /*
        이 메서드는 Security 인증 흐름 상 필요한 것임
        어차피, 한 유저 당 UserRole은 한개이니, 아래의 getUserRole을 사용하자.
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
        authorities.add(new SimpleGrantedAuthority(principals.get(PrincipalKey.USER_ROLE.getKey()).toString()));
        return authorities;
    }

    @Deprecated //패스워드 없어도 어차피 OAuth이다.
    @Override
    public String getPassword() {
        return "";
    }

    @Deprecated //유저네임 없어도 된다.
    @Override
    public String getUsername() {
        return "";
    }

    //User의 pk를 반환
    public Long getUserId(){
        return (Long) principals.get(PrincipalKey.USER_ID.getKey());
    }

    //User의 Role을 UserRole(enum) 형태로 반환
    public UserRole getUserRole(){
        return (UserRole) principals.get(PrincipalKey.USER_ROLE.getKey());
    }
}
