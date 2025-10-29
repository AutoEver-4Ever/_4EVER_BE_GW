package org.ever._4ever_be_gw.config.security.principal;

import java.io.Serial;
import java.io.Serializable;
import java.security.Principal;
import java.time.Instant;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * Gateway에서 JWT 기반 인증 결과로 사용하는 사용자 컨텍스트.
 */
@Getter
@EqualsAndHashCode
@ToString
public final class EverUserPrincipal implements Principal, Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private final String userId;
    private final String loginEmail;
    private final String userRole;
    private final String userType;
    private final Set<String> authorities;
    private final Instant issuedAt;
    private final Instant expiresAt;

    @Builder
    private EverUserPrincipal(
        String userId,
        String loginEmail,
        String userRole,
        String userType,
        Set<String> authorities,
        Instant issuedAt,
        Instant expiresAt
    ) {
        this.userId = userId;
        this.loginEmail = Objects.requireNonNull(loginEmail, "loginEmail must not be null");
        this.userRole = userRole;
        this.userType = userType;
        Set<String> safeAuthorities = authorities != null
            ? new LinkedHashSet<>(authorities)
            : new LinkedHashSet<>();
        this.authorities = Collections.unmodifiableSet(safeAuthorities);
        this.issuedAt = issuedAt;
        this.expiresAt = expiresAt;
    }

    @Override
    public String getName() {
        return loginEmail;
    }
}
