package com.ecom.filter;

import com.ecom.util.JwtUtil;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {

    @Autowired
    private RouteValidator validator;

    @Autowired
    private JwtUtil jwtUtil;

    public AuthenticationFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            if (exchange.getRequest().getPath().toString().startsWith("/auth/login") ||
                exchange.getRequest().getPath().toString().startsWith("/auth/signup")) {
                return chain.filter(exchange);
            }

            if (validator.isSecured.test(exchange.getRequest())) {
                String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

                if (authHeader != null && authHeader.startsWith("Bearer ")) {
                    String token = authHeader.substring(7);
                    try {
                        Claims claims = (Claims) jwtUtil.extractAllClaims(token);
                        List<String> roles = ((List<?>) claims.get("roles")).stream()
                            .filter(obj -> obj instanceof String)
                            .map(obj -> (String) obj)
                            .collect(Collectors.toList());

                        boolean isAuthorized = roles.stream().anyMatch(role ->
                            (role.equals("ADMIN") && exchange.getRequest().getPath().toString().startsWith("/offer/admin")) ||
                            (role.equals("CLIENT") && exchange.getRequest().getPath().toString().startsWith("/offer/user")) ||
                            (role.equals("SUPERADMIN") && exchange.getRequest().getPath().toString().startsWith("/superadmin"))
                        );

                        if (!isAuthorized) {
                            throw new Exception("Unauthorized access to application");
                        }
                    } catch (Exception e) {
                        ServerHttpResponse response = exchange.getResponse();
                        response.setStatusCode(HttpStatus.UNAUTHORIZED);
                        return response.setComplete();
                    }
                } else {
                    ServerHttpResponse response = exchange.getResponse();
                    response.setStatusCode(HttpStatus.UNAUTHORIZED);
                    return response.setComplete();
                }
            }
            return chain.filter(exchange);
        };
    }



    public static class Config {
        // Config class for properties if needed
    }
}
