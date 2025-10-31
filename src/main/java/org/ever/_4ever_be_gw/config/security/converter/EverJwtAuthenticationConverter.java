package org.ever._4ever_be_gw.config.security.converter;

import java.util.Collection;
import java.util.List;
import org.ever._4ever_be_gw.config.security.principal.EverJwtAuthenticationToken;
import org.ever._4ever_be_gw.config.security.principal.EverUserPrincipal;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.util.StringUtils;

public class EverJwtAuthenticationConverter implements Converter<Jwt, EverJwtAuthenticationToken> {

    @Override
    public EverJwtAuthenticationToken convert(Jwt jwt) {
        EverUserPrincipal principal = buildPrincipal(jwt);
        Collection<GrantedAuthority> authorities = convertAuthorities(principal);
        return new EverJwtAuthenticationToken(jwt, authorities, principal);
    }

    private EverUserPrincipal buildPrincipal(Jwt jwt) {
        String loginEmail = firstNonBlank(
            jwt.getClaimAsString("login_email"),
            jwt.getSubject()
        );

        return EverUserPrincipal.builder()
            .userId(firstNonBlank(
                jwt.getClaimAsString("user_id"),
                jwt.getClaimAsString("userId")
            ))
            .loginEmail(loginEmail)
            .userRole(firstNonBlank(
                jwt.getClaimAsString("user_role"),
                jwt.getClaimAsString("role")
            ))
            .userType(firstNonBlank(
                jwt.getClaimAsString("user_type"),
                jwt.getClaimAsString("userType")
            ))
            .issuedAt(jwt.getIssuedAt())
            .expiresAt(jwt.getExpiresAt())
            .build();
    }

    private Collection<GrantedAuthority> convertAuthorities(EverUserPrincipal principal) {
        if (!StringUtils.hasText(principal.getUserRole())) {
            return List.of();
        }
        return List.of(new SimpleGrantedAuthority(principal.getUserRole()));
    }

    private String firstNonBlank(String... values) {
        if (values == null) {
            return null;
        }
        for (String value : values) {
            if (StringUtils.hasText(value)) {
                return value;
            }
        }
        return null;
    }
}
