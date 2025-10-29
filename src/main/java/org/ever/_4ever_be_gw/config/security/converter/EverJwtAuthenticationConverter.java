package org.ever._4ever_be_gw.config.security.converter;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.ever._4ever_be_gw.config.security.principal.EverJwtAuthenticationToken;
import org.ever._4ever_be_gw.config.security.principal.EverUserPrincipal;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

public class EverJwtAuthenticationConverter implements Converter<Jwt, EverJwtAuthenticationToken> {

    @Override
    public EverJwtAuthenticationToken convert(Jwt jwt) {
        EverUserPrincipal principal = buildPrincipal(jwt);
        Collection<GrantedAuthority> authorities = convertAuthorities(jwt, principal);
        return new EverJwtAuthenticationToken(jwt, authorities, principal);
    }

    private EverUserPrincipal buildPrincipal(Jwt jwt) {
        String loginEmail = firstNonBlank(
            jwt.getClaimAsString("login_email"),
            jwt.getSubject()
        );

        Set<String> authorities = new LinkedHashSet<>(getAuthorityClaims(jwt));

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
            .authorities(authorities)
            .issuedAt(jwt.getIssuedAt())
            .expiresAt(jwt.getExpiresAt())
            .build();
    }

    private Collection<GrantedAuthority> convertAuthorities(Jwt jwt, EverUserPrincipal principal) {
        Set<String> rawAuthorities = new LinkedHashSet<>(getAuthorityClaims(jwt));
        if (rawAuthorities.isEmpty() && StringUtils.hasText(principal.getUserRole())) {
            rawAuthorities.add(principal.getUserRole());
        }

        return rawAuthorities.stream()
            .filter(StringUtils::hasText)
            .map(SimpleGrantedAuthority::new)
            .collect(Collectors.toUnmodifiableSet());
    }

    private List<String> getAuthorityClaims(Jwt jwt) {
        List<String> authorities = jwt.getClaimAsStringList("authorities");
        if (CollectionUtils.isEmpty(authorities)) {
            return List.of();
        }
        return authorities;
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
