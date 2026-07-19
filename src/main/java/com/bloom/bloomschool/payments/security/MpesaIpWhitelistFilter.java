package com.bloom.bloomschool.payments.security;

import com.bloom.bloomschool.payments.config.MpesaProperties;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Set;

/**
 * Restricts the public M-Pesa callback endpoints to Safaricom's published source IPs.
 * No-ops (allows everything through) when mpesa.whitelist is empty, so sandbox/local
 * testing isn't blocked by default — set it explicitly for production.
 */
@Component
@RequiredArgsConstructor
public class MpesaIpWhitelistFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(MpesaIpWhitelistFilter.class);

    private static final Set<String> PROTECTED_PATHS = Set.of(
            "/api/payments/mpesa/stk-callback",
            "/api/payments/mpesa/c2b/validation",
            "/api/payments/mpesa/c2b/confirmation"
    );

    private final MpesaProperties mpesaProperties;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        String path = request.getRequestURI();
        Set<String> whitelist = mpesaProperties.getEffectiveWhitelist();

        if (PROTECTED_PATHS.contains(path) && whitelist != null && !whitelist.isEmpty()) {
            String ip = request.getHeader("X-Forwarded-For");
            ip = (ip == null) ? request.getRemoteAddr() : ip.split(",")[0].trim();

            if (!whitelist.contains(ip)) {
                log.warn("Blocked M-Pesa callback from non-whitelisted IP {} to {}", ip, path);
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                return;
            }
        }

        chain.doFilter(request, response);
    }
}
