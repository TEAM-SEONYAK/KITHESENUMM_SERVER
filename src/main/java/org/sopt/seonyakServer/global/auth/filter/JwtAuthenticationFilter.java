package org.sopt.seonyakServer.global.auth.filter;

import static org.sopt.seonyakServer.global.auth.jwt.JwtValidationType.VALID_JWT;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sopt.seonyakServer.global.auth.MemberAuthentication;
import org.sopt.seonyakServer.global.auth.jwt.JwtTokenProvider;
import org.sopt.seonyakServer.global.exception.enums.ErrorType;
import org.sopt.seonyakServer.global.exception.model.CustomException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    // 각 HTTP 요청에 대해 토큰이 유효한지 확인하고, 유효하다면 해당 사용자를 인증 설정하는 필터링 로직
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        try {
            final String token = getJwtFromRequest(request);

            if (jwtTokenProvider.validateToken(token) == VALID_JWT) {
                Long memberId = jwtTokenProvider.getMemberIdFromJwt(token);

                // authentication 객체 생성 -> principal에 유저정보를 담는다.
                MemberAuthentication authentication = new MemberAuthentication(memberId.toString(), null, null);
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception exception) {
            // SecurityConfig에서 permitAll을 적용해도, Spring Security의 필터 체인을 거치므로
            // 여기서 바로 Exception throw를 하게 되면 permitAll과 상관 없이 ExceptionTranslationFilter로 처리가 넘어간다.
            // 따라서 예외를 직접 throw로 던져주는 것이 아닌, 발생시키기만 하고 다음 필터 호출로 이어지게끔 해야 하고, (doFilter)
            // 이렇게 하면 API의 permitAll 적용 여부에 따라 ExceptionTranslationFilter를 거칠지 판단하게 된다.
            log.error("JwtAuthentication Authentication Exception Occurs! - {}", exception.getMessage());

            if (exception instanceof ExpiredJwtException) {
                throw new CustomException(ErrorType.EXPIRED_JWT_TOKEN);
            }
        }
        // 다음 필터로 요청 전달 (호출)
        filterChain.doFilter(request, response);
    }

    // Authorization 헤더에서 JWT 토큰을 추출
    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");

        if (!StringUtils.hasText(bearerToken)) {
            throw new CustomException(ErrorType.UN_LOGIN_ERROR);
        } else if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring("Bearer ".length());
        } else if (StringUtils.hasText(bearerToken) && !bearerToken.startsWith("Bearer ")) {
            throw new CustomException(ErrorType.BEARER_LOST_ERROR);
        }

        return null;
    }
}
