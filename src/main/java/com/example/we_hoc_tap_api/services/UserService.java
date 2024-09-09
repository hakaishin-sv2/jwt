package com.example.we_hoc_tap_api.services;

import com.example.we_hoc_tap_api.dto.reponse.ApiResponse;
import com.example.we_hoc_tap_api.dto.reponse.UserResponse;
import com.example.we_hoc_tap_api.dto.request.UserRequest;
import com.example.we_hoc_tap_api.entity.UserEntity;
import com.example.we_hoc_tap_api.exception.AppException;
import com.example.we_hoc_tap_api.exception.ErrorCode;
import com.example.we_hoc_tap_api.mapper.UserMapper;
import com.example.we_hoc_tap_api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {
    @Autowired
    UserRepository userRepository;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private PasswordEncoder passwordEncoder;
    public UserResponse createUser(UserRequest userRequest) {
        // Kiểm tra xem username đã tồn tại chưa
        Optional<UserEntity> existingUser = userRepository.findByUsername(userRequest.getUsername());
        if (existingUser.isPresent()) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }
        UserEntity userEntity = userMapper.toEntity(userRequest);
        userEntity.setPassword(passwordEncoder.encode(userRequest.getPassword()));
        UserEntity savedUserEntity = userRepository.save(userEntity);
        return userMapper.toDto(savedUserEntity);
    }
    // Lấy thông tin người dùng theo ID
    public UserResponse getUserById(Long id) {
        UserEntity userEntity = userRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        return userMapper.toDto(userEntity);
    }

    // không cần truyển id
    public UserResponse getMyInfor() {
        var context =  SecurityContextHolder.getContext();
        String username = context.getAuthentication().getName();
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        // Tạo đối tượng UserResponse
        UserResponse userResponse = new UserResponse();
        userResponse = userMapper.toDto(user);
        return userResponse;
    }
    // Lấy danh sách người dùng
    public ApiResponse getAllUsers() {
        List<UserResponse> userResponses = userRepository.findAll().stream()
                .map(user -> userMapper.toDto(user))
                .collect(Collectors.toList());

        return ApiResponse.builder()
                .message("Successfully retrieved list of users")
                .data(userResponses)
                .build();
    }

    // Cập nhật thông tin người dùng
    public UserResponse updateUser(Long id, UserRequest userRequest) {
        // Kiểm tra xem người dùng có tồn tại hay không
        if (userRepository.existsById(id)) {
            UserEntity existingUserEntity = userRepository.findById(id)
                    .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

            // Cập nhật các trường của người dùng hiện tại
            existingUserEntity.setUsername(userRequest.getUsername());
            existingUserEntity.setFullname(userRequest.getFullname());
            existingUserEntity.setPassword(userRequest.getPassword()); // Xem xét mã hóa mật khẩu trước khi lưu
            existingUserEntity.setStatus(userRequest.getStatus());

            // Lưu các thay đổi vào cơ sở dữ liệu
            UserEntity updatedUserEntity = userRepository.save(existingUserEntity);

            // Chuyển đổi đối tượng đã cập nhật thành DTO và trả về
            return userMapper.toDto(updatedUserEntity);
        } else {
            // Trả về null hoặc ném ngoại lệ nếu người dùng không tồn tại
            return null;
        }
    }
    // Xóa người dùng theo ID
    public boolean deleteUser(Long id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return true;
        } else {
            throw new AppException(ErrorCode.USER_NOT_EXISTED);
        }
    }
}
