package org.ever._4ever_be_gw.config.security.principal;

import java.util.Collection;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

public class EverJwtAuthenticationToken extends JwtAuthenticationToken {

    private final EverUserPrincipal principal;

    public EverJwtAuthenticationToken(
        Jwt jwt,
        Collection<? extends GrantedAuthority> authorities,
        EverUserPrincipal principal
    ) {
        super(jwt, authorities, principal != null ? principal.getLoginEmail() : jwt.getSubject());
        this.principal = principal;
    }

    @Override
    public EverUserPrincipal getPrincipal() {
        return principal;
    }
}
