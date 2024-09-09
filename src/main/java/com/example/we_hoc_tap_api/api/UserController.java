package com.example.we_hoc_tap_api.api;

import com.example.we_hoc_tap_api.dto.reponse.ApiResponse;
import com.example.we_hoc_tap_api.dto.reponse.UserResponse;
import com.example.we_hoc_tap_api.dto.request.UserRequest;
import com.example.we_hoc_tap_api.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    // Tạo người dùng mới
    @PostMapping
    public ResponseEntity<UserResponse> createUser(@RequestBody UserRequest userRequest) {
        UserResponse createdUser = userService.createUser(userRequest);
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    // Lấy danh sách người dùng
    @GetMapping("/list")
    public ResponseEntity<ApiResponse> listUser() {
        var authen =SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        ApiResponse apiResponse = userService.getAllUsers();
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }
    @Autowired
    private JwtDecoder jwtDecoder;

    // Nếu user đang login token hiện tại à username người đang login vào mới tar về
    @PostAuthorize("returnObject.id == authentication.principal.claims['id']")
    @GetMapping("/infor-user")
    public ResponseEntity<Map<String, Object>> getUserInfo(@RequestHeader("Authorization") String authorizationHeader) {
        // Xử lý token từ header Authorization
        String token = authorizationHeader.replace("Bearer ", "");

        try {
            // Giải mã token
            Jwt jwt = jwtDecoder.decode(token);

            // Lấy thông tin từ payload
            String id = jwt.getClaimAsString("id");
            String scope = jwt.getClaimAsString("scope");

            // Tạo response
            Map<String, Object> response = new HashMap<>();
            response.put("id", id);
            response.put("scope", scope);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(401).body(Map.of("error", "Invalid token"));
        }
    }
    // Lấy thông tin người dùng theo ID
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
//        UserDTO userDTO = userService.getUserById(id);
//        if (userDTO != null) {
//            return new ResponseEntity<>(userDTO, HttpStatus.OK);
//        } else {
//            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
//        }
        return  null;
    }

    // Cập nhật thông tin người dùng
    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(@PathVariable Long id, @RequestBody UserRequest userRequest) {
//        UserDTO updatedUser = userService.updateUser(id, userRequest);
//        if (updatedUser != null) {
//            return new ResponseEntity<>(updatedUser, HttpStatus.OK);
//        } else {
//            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
//        }
        return null;
    }

    // Xóa người dùng theo ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
//        boolean isDeleted = userService.deleteUser(id);
//        if (isDeleted) {
//            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
//        } else {
//            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
//        }
        return null;
    }
}
