//package com.example.we_hoc_tap_api.config;
//
//import com.example.we_hoc_tap_api.dto.request.IntrospectRequest;
//import com.example.we_hoc_tap_api.services.AuthenticationService;
//import com.nimbusds.jose.JOSEException;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.expression.ParseException;
//import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
//import org.springframework.security.oauth2.jwt.Jwt;
//import org.springframework.security.oauth2.jwt.JwtDecoder;
//import org.springframework.security.oauth2.jwt.JwtException;
//import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
//import org.springframework.stereotype.Component;
//
//import javax.crypto.spec.SecretKeySpec;
//import java.util.Objects;
//
//@Component
//public class CustomJwtDecoder implements JwtDecoder {
//    @Value("${jwt.signerKey}")
//    private String signerKey;
//
//    @Autowired
//    private AuthenticationService authenticationService;
//
//    private NimbusJwtDecoder nimbusJwtDecoder = null;
//
//    @Override
//    public Jwt decode(String token) throws JwtException {
//
//        try {
//            var response = authenticationService.introspect(
//                  token);
//
//            if (!response.isValid()) throw new JwtException("Token invalid");
//        } catch (JOSEException | ParseException e) {
//            throw new JwtException(e.getMessage());
//        } catch (java.text.ParseException e) {
//            throw new RuntimeException(e);
//        }
//
//        if (Objects.isNull(nimbusJwtDecoder)) {
//            SecretKeySpec secretKeySpec = new SecretKeySpec(signerKey.getBytes(), "HS512");
//            nimbusJwtDecoder = NimbusJwtDecoder.withSecretKey(secretKeySpec)
//                    .macAlgorithm(MacAlgorithm.HS512)
//                    .build();
//        }
//
//        return nimbusJwtDecoder.decode(token);
//    }
//}