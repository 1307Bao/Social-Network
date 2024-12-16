package com.example.Social_Network.Controller;

import com.example.Social_Network.DTO.Request.*;
import com.example.Social_Network.DTO.Response.AuthenticationResponse;
import com.example.Social_Network.DTO.Response.LoginResponse;
import com.example.Social_Network.DTO.Response.RegisterResponse;
import com.example.Social_Network.Exception.AppRuntimeException;
import com.example.Social_Network.Service.AuthenticationService;
import com.nimbusds.jose.JOSEException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationController {

    AuthenticationService authenticationService;

    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@RequestBody LoginRequest request) throws AppRuntimeException {
        LoginResponse result = authenticationService.login(request);
        return ApiResponse.<LoginResponse>builder().result(result).build();
    }

    @PostMapping("/register")
    public ApiResponse<RegisterResponse> register(@RequestBody RegisterRequest request) throws AppRuntimeException {
        RegisterResponse result = authenticationService.register(request);
        return ApiResponse.<RegisterResponse>builder().result(result).build();
    }

    @PostMapping("/introspect")
    public ApiResponse<AuthenticationResponse> introspect(@RequestBody IntrospectRequest request) {
        AuthenticationResponse result = authenticationService.introspect(request);

        return ApiResponse.<AuthenticationResponse>builder().result(result).build();
    }

    @PostMapping("/logout")
    public void logout(@RequestBody LogoutRequest request) throws AppRuntimeException, ParseException, JOSEException {
        authenticationService.logout(request);
    }

}
