package chat.service;

import chat.config.JwtUtil;
import chat.enitity.UserDetail;
import chat.repository.UserDetailRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;


import java.time.LocalDate;
import java.util.Optional;

@Component
public class DailyLoginInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserDetailRepository userDetailRepository;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            String username = jwtUtil.extractUsername(token);

            if (username != null && jwtUtil.validateToken(token)) {
                Optional<UserDetail> userOpt = userDetailRepository.findByName(username);

                if (userOpt.isPresent()) {
                    UserDetail user = userOpt.get();
                    LocalDate today = LocalDate.now();

                    // Check if user already got the daily login reward
                    if (!today.equals(user.getDate())) {
                        user.setDate(today);
                        user.addCoins(50);
                        userDetailRepository.save(user);
                    }
                }
            }
        }
        return true; // Continue request processing
    }
}
