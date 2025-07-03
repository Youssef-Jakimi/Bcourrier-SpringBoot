//package com.courrier.Bcourrier.Security;
//
//import com.courrier.Bcourrier.Entities.Employe;
//import com.courrier.Bcourrier.Repositories.EmployeRepository;
//import com.courrier.Bcourrier.config.JwtUtil;
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.stereotype.Component;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import java.io.IOException;
//import java.util.Objects;
//
//@Component
//public class DelegueFilter extends OncePerRequestFilter {
//
//    @Autowired
//    private JwtUtil jwtUtil;
//
//    @Autowired
//    private EmployeRepository employeRepository;
//
//    @Override
//    protected void doFilterInternal(HttpServletRequest request,
//                                    HttpServletResponse response,
//                                    FilterChain filterChain)
//            throws ServletException, IOException {
//
//        String authHeader = request.getHeader("Authorization");
//
//        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
//            response.sendError(HttpStatus.FORBIDDEN.value(), "Access denied: JWT token is required");
//            return;
//        }
//
//        String jwtToken = authHeader.substring(7);
//        String login = jwtUtil.extractLogin(jwtToken);
//
//        if (login != null) {
//            Employe employe = employeRepository.findByLogin(login)
//                    .orElse(null);
//
//            if (employe == null || !(Objects.equals(employe.getRole().getNom(), "DELEGUE"))) {
//                response.sendError(HttpStatus.FORBIDDEN.value(), "Access denied: not an AdminBC user");
//                return;
//            }
//        }
//
//        filterChain.doFilter(request, response);
//    }
//
//    @Override
//    protected boolean shouldNotFilter(HttpServletRequest request) {
//        return !request.getRequestURI().startsWith("/api/delegue");
//    }
//}