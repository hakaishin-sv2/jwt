package com.example.we_hoc_tap_api.services;


import com.example.we_hoc_tap_api.dto.reponse.AuthenticationResponse;
import com.example.we_hoc_tap_api.dto.reponse.IntrospectResponse;
import com.example.we_hoc_tap_api.dto.request.AuthenticationRequest;
import com.example.we_hoc_tap_api.dto.request.IntrospectRequest;
import com.example.we_hoc_tap_api.entity.UserEntity;
import com.example.we_hoc_tap_api.exception.AppException;
import com.example.we_hoc_tap_api.exception.ErrorCode;
import com.example.we_hoc_tap_api.repository.UserRepository;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j

public class AuthenticationService {

    @Autowired
    UserRepository userRepository;
    @Autowired
    PasswordEncoder passwordEncoder;
    @NonFinal
    @Value("${jwt.signerKey}")
    protected String SIGNER_KEY;

    private static final long VALID_DURATION = TimeUnit.HOURS.toSeconds(1); // Token có hiệu lực trong 1 giờ
    private static final long REFRESHABLE_DURATION = TimeUnit.HOURS.toSeconds(1);;

    public IntrospectResponse introspect(IntrospectRequest request) throws JOSEException, ParseException {
        var token = request.getToken();
        SignedJWT signedJWT = SignedJWT.parse(token);
        JWSVerifier verifier = new MACVerifier(SIGNER_KEY.getBytes());
        var verified = signedJWT.verify(verifier);
        Date expirationTime = signedJWT.getJWTClaimsSet().getExpirationTime();
        boolean valid = verified && expirationTime.after(new Date());
        return IntrospectResponse.builder()
                .valid(valid)
                .build();
    }


    public AuthenticationResponse authenticate(AuthenticationRequest request) {

            UserEntity userEntity = userRepository.findByUsername(request.getUsername())
                    .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

            boolean authenticated = passwordEncoder.matches(request.getPassword(), userEntity.getPassword());

            if (!authenticated) {
                throw new AppException(ErrorCode.UNAUTHENTICATED);
            }

            // Tạo token JWT
            String token = generateToken(userEntity);

            return AuthenticationResponse.builder()
                    .token(token)
                    .authenticated(true)
                    .build();


    }




    // Tạo token từ thông tin người dùng
    public String generateToken(UserEntity user) {
        try {
            JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);

            // Tạo các claims cho token
            JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                    .subject(user.getUsername()) // Sử dụng username của người dùng
                    .issuer("devteria.com")
                    .issueTime(new Date())
                    .expirationTime(new Date(
                            Instant.now().plus(VALID_DURATION, ChronoUnit.SECONDS).toEpochMilli()))
                    .claim("scope", "ADMIN")
                    .claim("id", user.getId())
                    .build();

            // Tạo payload và JWSObject
            Payload payload = new Payload(jwtClaimsSet.toJSONObject());
            JWSObject jwsObject = new JWSObject(header, payload);

            // Ký token với SIGNER_KEY và trả về token dưới dạng chuỗi
            jwsObject.sign(new MACSigner(SIGNER_KEY.getBytes()));
            return jwsObject.serialize();
        } catch (JOSEException e) {
            log.error("Cannot create token", e);
            throw new RuntimeException("Error creating JWT token", e);
        }
    }

//    private String buildScope(UserEntity user) {
//        // Xây dựng scope từ thông tin người dùng
//        return user.getRoles().toString(); // Ví dụ: chuyển đổi danh sách vai trò thành chuỗi
//    }


//    private SignedJWT verifyToken(String token, boolean isRefresh) throws JOSEException, ParseException {
//        JWSVerifier verifier = new MACVerifier(SIGNER_KEY.getBytes());
//
//        SignedJWT signedJWT = SignedJWT.parse(token);
//
//        Date expiryTime = (isRefresh)
//                ? new Date(signedJWT
//                .getJWTClaimsSet()
//                .getIssueTime()
//                .toInstant()
//                .plus(REFRESHABLE_DURATION, ChronoUnit.SECONDS)
//                .toEpochMilli())
//                : signedJWT.getJWTClaimsSet().getExpirationTime();
//
//        var verified = signedJWT.verify(verifier);
//
//        if (!(verified && expiryTime.after(new Date()))) throw new AppException(ErrorCode.UNAUTHENTICATED);
//
//        if (invalidatedTokenRepository.existsById(signedJWT.getJWTClaimsSet().getJWTID()))
//            throw new AppException(ErrorCode.UNAUTHENTICATED);
//
//        return signedJWT;
//    }

//    private String buildScope(User user) {
//        StringJoiner stringJoiner = new StringJoiner(" ");
//
//        if (!CollectionUtils.isEmpty(user.getRoles()))
//            user.getRoles().forEach(role -> {
//                stringJoiner.add("ROLE_" + role.getName());
//                if (!CollectionUtils.isEmpty(role.getPermissions()))
//                    role.getPermissions().forEach(permission -> stringJoiner.add(permission.getName()));
//            });
//
//        return stringJoiner.toString();
//    }
}
