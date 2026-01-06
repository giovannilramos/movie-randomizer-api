package br.com.giovanniramos.movierandomizer.configs.security.filters;

import br.com.giovanniramos.movierandomizer.configs.security.service.TokenService;
import br.com.giovanniramos.movierandomizer.configs.security.service.UserDetailsServiceImpl;
import io.micrometer.common.lang.NonNull;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import static java.util.Objects.nonNull;

@Component
@RequiredArgsConstructor
public class JwtAuthorizationFilter extends OncePerRequestFilter {
    private final UserDetailsServiceImpl userDetailsService;
    private final TokenService tokenService;

    @Override
    @SneakyThrows
    protected void doFilterInternal(@NonNull final HttpServletRequest request,
                                    @NonNull final HttpServletResponse response,
                                    @NonNull final FilterChain filterChain) {
        try {
            final var username = tokenService.getSubject(request);
            if (nonNull(username)) {
                final var userDetails = userDetailsService.loadUserByUsername(username);
                SecurityContextHolder.getContext().setAuthentication(
                        new UsernamePasswordAuthenticationToken(username, null, userDetails.getAuthorities()));
            }

            filterChain.doFilter(request, response);
        } catch (final Exception _) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }
}
