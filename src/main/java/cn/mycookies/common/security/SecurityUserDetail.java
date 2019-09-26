package cn.mycookies.common.security;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 用户信息，供SpringSecurity使用
 *
 * @author liqiang
 * @datetime 2019-08-07 16:55:25
 * Copyright © SSY All Rights Reserved.
 */
@Data
@AllArgsConstructor
@Builder
public class SecurityUserDetail implements UserDetails {
    private Long id;

    /**
     * 用户名
     */
    private String userName;

    /**
     * 用户密码
     */
    private String password;

    /**
     * 角色信息，多个角色用，分割
     */
    private String role;

    public SecurityUserDetail() {
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();
        if (StringUtils.isNotEmpty(role)) {
            String[] roleSplits = role.split(",");
            for (String roleItem : roleSplits) {
                authorities.add(new SimpleGrantedAuthority(roleItem));
            }
        }
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return userName;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public String getUserName() {
        return userName;
    }

}
