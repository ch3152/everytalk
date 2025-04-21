package com.example.talkdemo.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final Logger log = LoggerFactory.getLogger(JwtAuthFilter.class);

    public JwtAuthFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String path = request.getRequestURI();
        log.info("🔍 [JWT 필터 진입] 요청 URI: {}", path);

        // ✅ 필터 예외 경로 확인
        if (
            path.startsWith("/api/users") ||
            path.equals("/api/chat/enter-random") ||
            path.startsWith("/ws") ||
            path.startsWith("/app")
        ) {
            log.info("✅ [필터 예외 처리] JWT 검사 생략: {}", path);
            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader("Authorization");
        log.info("🪪 [Authorization 헤더] {}", authHeader);

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            try {
                String token = authHeader.substring(7);
                log.info("🔐 [토큰 추출 성공] {}", token);

                String username = jwtUtil.validateAndGetUsername(token);
                log.info("✅ [토큰 유효] 사용자: {}", username);

                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(username, null, Collections.emptyList());
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            } catch (Exception e) {
                log.error("⛔ [JWT 검증 실패] {}", e.getMessage());
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.getWriter().write("Invalid JWT token");
                return;
            }
        } else {
            log.warn("❌ [Authorization 헤더 없음 또는 형식 오류] URI: {}", path);
        }

        filterChain.doFilter(request, response);
    }
}
