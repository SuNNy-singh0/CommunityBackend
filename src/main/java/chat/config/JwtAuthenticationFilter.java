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
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        String method = request.getMethod(); // Get HTTP method for conditional skipping

        // Log for debugging purposes
        logger.info("shouldNotFilter check for path: {} (Method: {})", path, method);

        // List all paths that DO NOT require JWT authentication.
        // These are typically your static assets (React app files), your main HTML entry point,
        // and public API endpoints that Spring Security will also permit.
        return path.equals("/") || // The root URL (your homepage)
               path.equals("/index.html") || // Explicit index.html access
               path.startsWith("/static/") || // Common static asset folder (e.g., from Vite/CRA build)
               // Comprehensive regex for all common static file extensions
               path.matches(".*\\.(js|css|png|jpg|jpeg|gif|svg|ico|webmanifest|json|map|woff|woff2|ttf|eot)$") || 
               path.startsWith("/images/") || // If you have a dedicated images folder
               path.startsWith("/fonts/") || // If you have a dedicated fonts folder
               path.startsWith("/vite.svg") || // Vite default icon (if still in public)
               path.startsWith("/favicon.ico") || // Your main favicon
               path.startsWith("/apple-touch-icon.png") || // Apple touch icon
               path.startsWith("/site.webmanifest") || // Web manifest file
               path.startsWith("/logo.png") || // Your uploaded logo if directly accessed
               
               // === Your Public API Endpoints (these should mirror your SecurityConfig's permitAll rules) ===
               path.startsWith("/rooms/login") ||
               path.startsWith("/rooms/createUser") ||
               path.startsWith("/rooms/reset-password") ||
               path.startsWith("/rooms/alluser") || // All users list (if public)
               path.startsWith("/event/all") ||
               path.startsWith("/event/create") || // Ensure this endpoint is truly meant to be public without auth
               path.startsWith("/contests/all") ||
               path.startsWith("/community/records") ||
               path.startsWith("/jobs/all") ||
               path.startsWith("/jobs/find-by-skills") ||
               path.startsWith("/jobs/filter-by-date") ||
               path.startsWith("/jobs/") || // Covers individual job pages like /jobs/123 if they are public
               path.startsWith("/mcq/all") ||
               path.startsWith("/mcq/daily") ||
               path.startsWith("/mcq/attempt/status") || // MCQ status check (before attempt)
               path.startsWith("/mcq/upload") || // Ensure this endpoint is truly meant to be public without auth
               path.startsWith("/usercontrol/top-users") ||
               path.startsWith("/usercontrol/user-rank") ||
               path.startsWith("/usercontrol/user-pics") ||
               (path.startsWith("/usercontrol/") && method.equals("GET")) || // Specific GETs under usercontrol if public
               path.contains("/messages") || // General message API (if parts of it are public)
               path.startsWith("/chat") || // All your WebSocket/STOMP endpoints (e.g., /chat/info, /chat/websocket)
               path.startsWith("/ws") || // Any other general WebSocket endpoint
               path.startsWith("/about"); // Your About Us page
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        final String requestURI = request.getRequestURI();
        logger.debug("Processing JWT filter for path: {}", requestURI);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            logger.warn("No valid Authorization header found for protected path: {}. Allowing chain to continue.", requestURI);
            filterChain.doFilter(request, response);
            return; // Exit here. Spring Security's AuthorizationFilter will then reject this.
        }

        try {
            final String jwt = authHeader.substring(7);
            final String username = jwtUtil.extractUsername(jwt);
            logger.debug("Extracted username: {} for path: {}", username, requestURI);

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = (UserDetails) loginRepository.findByUsername(username);

                if (userDetails != null && jwtUtil.validateToken(jwt)) {
                    UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    logger.debug("Authentication successful for user: {} for path: {}", username, requestURI);
                } else {
                    logger.warn("Token validation failed for user: {} or token invalid for path: {}", username, requestURI);
                }
            } else {
                 logger.warn("Username null or user already authenticated for path: {}", requestURI);
            }
        } catch (Exception e) {
            logger.error("Error processing JWT token for path {}: {}", requestURI, e.getMessage());
        }

        filterChain.doFilter(request, response); // Continue to the next filter in the chain
    }

}