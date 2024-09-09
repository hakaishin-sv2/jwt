package com.example.we_hoc_tap_api.api;


import com.example.we_hoc_tap_api.dto.reponse.ApiResponse;
import com.example.we_hoc_tap_api.dto.reponse.AuthenticationResponse;
import com.example.we_hoc_tap_api.dto.reponse.IntrospectResponse;
import com.example.we_hoc_tap_api.dto.request.AuthenticationRequest;
import com.example.we_hoc_tap_api.dto.request.IntrospectRequest;
import com.example.we_hoc_tap_api.exception.AppException;
import com.example.we_hoc_tap_api.exception.ErrorCode;
import com.example.we_hoc_tap_api.services.AuthenticationService;
import com.nimbusds.jose.JOSEException;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    @Autowired
    private AuthenticationService authenticationService;

    // Đăng nhập
    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(@RequestBody AuthenticationRequest request) {
        AuthenticationResponse response = authenticationService.authenticate(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/introspect")
    public ResponseEntity<IntrospectResponse> authenticate(@RequestBody IntrospectRequest request) throws ParseException, JOSEException {
        IntrospectResponse result = authenticationService.introspect(request);
        return ResponseEntity.ok(result);
    }




}
