package chat.config;

import chat.repository.LoginRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final JwtUtil jwtUtil;
    private final LoginRepository loginRepository;

    public JwtAuthenticationFilter(JwtUtil jwtUtil, LoginRepository loginRepository) {
        this.jwtUtil = jwtUtil;
        this.loginRepository = loginRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        try {
            String path = request.getRequestURI();
            
            // Skip authentication for non-protected endpoints
            if (!path.startsWith("/community") || 
                path.contains("/login") || 
                path.contains("/createUser") || 
                path.contains("/checkUsername")) {
                filterChain.doFilter(request, response);
                return;
            }

            final String authHeader = request.getHeader("Authorization");
            logger.debug("Auth header: {}", authHeader);

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                logger.warn("No valid authorization header found. Please include 'Authorization: Bearer your_token' header");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Missing or invalid Authorization header. Please include 'Authorization: Bearer your_token'");
                return;
            }

            final String jwt = authHeader.substring(7);
            final String username = jwtUtil.extractUsername(jwt);
            logger.debug("Extracted username: {}", username);

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                if (loginRepository.findByUsername(username) != null && jwtUtil.validateToken(jwt)) {
                    UserDetails userDetails = org.springframework.security.core.userdetails.User
                        .withUsername(username)
                        .password("") // Password not needed here as we're authenticating via JWT
                        .authorities(new ArrayList<>())
                        .build();

                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                    
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    logger.debug("Authentication successful for user: {}", username);
                    filterChain.doFilter(request, response);
                } else {
                    logger.warn("Token validation failed for user: {}", username);
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.getWriter().write("Invalid or expired token");
                }
            } else {
                filterChain.doFilter(request, response);
            }
        } catch (Exception e) {
            logger.error("Error processing JWT token: {}", e.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Error processing token: " + e.getMessage());
        }
    }
} 