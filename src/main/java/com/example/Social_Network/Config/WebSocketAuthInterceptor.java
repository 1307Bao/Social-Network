package com.example.Social_Network.Config;

import com.example.Social_Network.DTO.Request.IntrospectRequest;
import com.example.Social_Network.Exception.AppRuntimeException;
import com.example.Social_Network.Service.AuthenticationService;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jwt.SignedJWT;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class WebSocketAuthInterceptor implements HandshakeInterceptor {

    private final AuthenticationService authenticationService;

    public WebSocketAuthInterceptor(@Lazy AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        String query = request.getURI().getQuery(); // Lấy query string
        log.info("Received query string: " + query);

        if (query != null && query.contains("access_token=")) {
            String token = query.split("access_token=")[1].split("&")[0]; // Tách token ra từ query string
            log.info("Extracted token: " + token);

            boolean isValid = authenticationService.introspect(IntrospectRequest.builder().token(token).build()).isValid();

            if (isValid) {
                log.info("Token is valid");
                attributes.put("userId", getUserId(token)); // Lưu userId vào attributes
                return true;
            } else {
                log.warn("Invalid token");
            }
        }

        return false;
    }




    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {

    }

    public String getUserId(String token) throws AppRuntimeException, ParseException, JOSEException {
        SignedJWT signedJWT = authenticationService.verifyToken(token);
        return signedJWT.getJWTClaimsSet().getSubject();
    }
}
