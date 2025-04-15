package me.radek203.authservice.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import me.radek203.authservice.exception.ResourceInvalidException;
import me.radek203.authservice.exception.ResourceNotFoundException;
import me.radek203.authservice.mapper.UserMapper;
import me.radek203.authservice.service.UserService;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@AllArgsConstructor
public class JWTAuthenticationFilter extends OncePerRequestFilter {

    private final JWTTokenUtils jwtService;
    private final UserService userService;

    /**
     * Extracts the JWT token from the request header.
     *
     * @param request the HTTP request
     * @return the JWT token if present, null otherwise
     */
    private static String getTokenFromRequest(final HttpServletRequest request) {
        final String bearerToken = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }

        return null;
    }

    /**
     * Filters the incoming request to check for a valid JWT token.
     * If a valid token is found, it sets the authentication in the security context.
     *
     * @param request     the HTTP request
     * @param response    the HTTP response
     * @param filterChain the filter chain
     * @throws ServletException if an error occurs during filtering
     * @throws IOException      if an I/O error occurs
     */
    @Override
    protected final void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response, final FilterChain filterChain) throws ServletException, IOException {
        try {
            final String token = getTokenFromRequest(request);

            if (StringUtils.hasText(token)) {
                final String username = jwtService.getUsername(token);
                final UserDetails userDetails = UserMapper.mapUserToUserDetails(userService.getUserByUsername(username));

                if (jwtService.isTokenValid(token, userDetails.getUsername())) {
                    final UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        } catch (final ResourceInvalidException | ResourceNotFoundException e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        filterChain.doFilter(request, response);
    }
}
