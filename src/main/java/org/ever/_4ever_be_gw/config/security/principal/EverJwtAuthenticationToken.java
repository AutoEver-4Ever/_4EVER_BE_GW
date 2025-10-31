package org.ever._4ever_be_gw.config.security.principal;

import java.util.Collection;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

/**
 * Ever 서비스용 사용자 Principal 을 담는 JwtAuthenticationToken 확장 클래스.
 *
 * <p>
 * - 기본 {@link JwtAuthenticationToken} 이 보유한 정보(JWT, 권한) 외에,
 *   애플리케이션 도메인의 사용자 정보인 {@link EverUserPrincipal} 을 함께 보관합니다.
 * - 인증 객체의 이름(name)은 {@link EverUserPrincipal#getLoginEmail()} 값이 있으면 이를 사용하고,
 *   없을 경우 JWT 의 subject(= {@link Jwt#getSubject()}) 값을 사용합니다.
 * </p>
 */
public class EverJwtAuthenticationToken extends JwtAuthenticationToken {

    /**
     * 애플리케이션 도메인의 인증된 사용자 정보.
     */
    private final EverUserPrincipal principal;

    /**
     * EverJwtAuthenticationToken 생성자.
     *
     * @param jwt          인증에 사용된 {@link Jwt}
     * @param authorities  사용자의 권한 목록
     * @param principal    애플리케이션 도메인의 사용자 Principal (null 가능)
     *
     * <p>
     * 스프링 시큐리티의 {@code Authentication#getName()} 에 해당하는 이름 값은
     * {@code principal} 이 존재하면 {@link EverUserPrincipal#getLoginEmail()} 을 사용하고,
     * 존재하지 않으면 JWT 의 subject 값을 사용하도록 부모 생성자에 전달합니다.
     * </p>
     */
    public EverJwtAuthenticationToken(
        Jwt jwt,
        Collection<? extends GrantedAuthority> authorities,
        EverUserPrincipal principal
    ) {
        super(jwt, authorities, principal != null ? principal.getLoginEmail() : jwt.getSubject());
        this.principal = principal;
    }

    /**
     * 인증된 사용자의 애플리케이션 도메인 Principal 을 반환합니다.
     *
     * @return {@link EverUserPrincipal}
     */
    @Override
    public EverUserPrincipal getPrincipal() {
        return principal;
    }
}
