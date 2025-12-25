package com.nicecommerce.security;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Firebase Authentication Filter
 * 
 * Intercepts requests and validates Firebase ID tokens.
 * Extracts user information and sets authentication in security context.
 * 
 * @author NiceCommerce Team
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class FirebaseAuthenticationFilter extends OncePerRequestFilter {

    private final FirebaseAuth firebaseAuth;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            String idToken = getTokenFromRequest(request);

            if (StringUtils.hasText(idToken)) {
                FirebaseToken decodedToken = firebaseAuth.verifyIdToken(idToken);
                setAuthentication(decodedToken, request);
            }
        } catch (FirebaseAuthException e) {
            log.error("Firebase token verification failed: {}", e.getMessage());
            // Continue filter chain - let Spring Security handle unauthorized requests
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Extract Firebase ID token from request header
     */
    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    /**
     * Set authentication in security context
     */
    private void setAuthentication(FirebaseToken token, HttpServletRequest request) {
        String uid = token.getUid();
        String email = (String) token.getClaims().get("email");
        
        // Extract role from custom claims
        Map<String, Object> claims = token.getClaims();
        String role = "ROLE_USER";
        if (claims.containsKey("role")) {
            role = "ROLE_" + claims.get("role").toString();
        }

        List<SimpleGrantedAuthority> authorities = Collections.singletonList(
                new SimpleGrantedAuthority(role)
        );

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        email != null ? email : uid,
                        null,
                        authorities
                );
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}

