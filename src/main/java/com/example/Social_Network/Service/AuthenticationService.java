package com.example.Social_Network.Service;

import com.example.Social_Network.DTO.Request.IntrospectRequest;
import com.example.Social_Network.DTO.Request.LoginRequest;
import com.example.Social_Network.DTO.Request.LogoutRequest;
import com.example.Social_Network.DTO.Request.RegisterRequest;
import com.example.Social_Network.DTO.Response.AuthenticationResponse;
import com.example.Social_Network.DTO.Response.LoginResponse;
import com.example.Social_Network.DTO.Response.RegisterResponse;
import com.example.Social_Network.Entity.InvalidatedToken;
import com.example.Social_Network.Entity.User;
import com.example.Social_Network.Exception.AppRuntimeException;
import com.example.Social_Network.Exception.ErrorCode;
import com.example.Social_Network.Repository.InvalidatedTokenRepository;
import com.example.Social_Network.Repository.UserRepository;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationService {

    @NonFinal
    @Value("${jwt.signer-key}")
    String SIGNER_KEY;

    @NonFinal
    @Value("${jwt.using-time}")
    int USING_TIME;

    UserRepository userRepository;
    PasswordEncoder passwordEncoder;
    InvalidatedTokenRepository invalidatedTokenRepository;

    public LoginResponse login(LoginRequest request) throws AppRuntimeException {
        User user = userRepository.findByEmail(request.getEmail()).orElseThrow(
                () -> new AppRuntimeException(ErrorCode.USER_NOT_EXIST)
        );

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new AppRuntimeException(ErrorCode.WRONG_PASSWORD);
        }

        return LoginResponse.builder().token(generateToken(user)).build();
    }

    public RegisterResponse register(RegisterRequest request) throws AppRuntimeException {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new AppRuntimeException(ErrorCode.USER_EXISTED);
        }

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new AppRuntimeException(ErrorCode.USER_EXISTED);
        }

        String password = passwordEncoder.encode(request.getPassword());

        User user = User.builder().email(request.getEmail())
                .fullname(request.getFullname())
                .username(request.getUsername())
                .password(password)
                .build();

        userRepository.save(user);
        return RegisterResponse.builder().token(generateToken(user)).build();
    }

    public AuthenticationResponse introspect(IntrospectRequest request) {
        var token = request.getToken();
        boolean isValid = true;
        try {
            verifyToken(token);
        } catch (AppRuntimeException | ParseException | JOSEException e) {
            isValid = false;
        }

        return AuthenticationResponse.builder().valid(isValid).build();
    }

    public void logout(LogoutRequest request) throws AppRuntimeException, ParseException, JOSEException {
        SignedJWT signedJWT = verifyToken(request.getToken());
        InvalidatedToken invalidatedToken = InvalidatedToken.builder()
                .id(signedJWT.getJWTClaimsSet().getJWTID())
                .expiryTime(signedJWT.getJWTClaimsSet().getExpirationTime())
                .build();

        invalidatedTokenRepository.save(invalidatedToken);
    }

    private String generateToken(User user) {
        JWSHeader header =  new JWSHeader(JWSAlgorithm.HS512);

        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .subject(user.getUser_id())
                .issuer("Social-Network-Project.com")
                .claim("roles", "USER")
                .expirationTime(new Date(
                        Instant.now().plus(USING_TIME, ChronoUnit.HOURS).toEpochMilli()
                ))
                .issueTime(new Date())
                .jwtID(UUID.randomUUID().toString())
                .build();

        Payload payload = new Payload(claimsSet.toJSONObject());
        JWSObject jwsObject = new JWSObject(header, payload);

        try {
            jwsObject.sign(new MACSigner(SIGNER_KEY.getBytes()));
            return jwsObject.serialize();
        } catch (JOSEException e) {
            throw new RuntimeException(e);
        }
    }

    public SignedJWT verifyToken(String token) throws JOSEException, ParseException, AppRuntimeException {
        JWSVerifier verifier = new MACVerifier(SIGNER_KEY.getBytes());
        SignedJWT signedJWT = SignedJWT.parse(token);

        Date expiryTime = signedJWT.getJWTClaimsSet().getExpirationTime();
        var verified = signedJWT.verify(verifier);

        if (!(verified && expiryTime.after(new Date()))) {
            throw new AppRuntimeException(ErrorCode.DATE_EXPIRED);
        }

        if (invalidatedTokenRepository.existsById(signedJWT.getJWTClaimsSet().getJWTID())) {
            log.error(token);
            throw new AppRuntimeException(ErrorCode.UNAUTHENTICATED);
        }

        return signedJWT;
    }
}
