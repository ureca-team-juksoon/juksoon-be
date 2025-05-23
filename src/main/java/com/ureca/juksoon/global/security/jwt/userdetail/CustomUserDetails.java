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

    @Override   //AuthorizationFilter에서 사용하는 권한을 검사하는 메서드 : 우리는 사용할 필요 없음.
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
