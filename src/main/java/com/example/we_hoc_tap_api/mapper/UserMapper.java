package com.example.we_hoc_tap_api.mapper;

import com.example.we_hoc_tap_api.dto.reponse.UserResponse;
import com.example.we_hoc_tap_api.dto.request.UserRequest;
import com.example.we_hoc_tap_api.entity.UserEntity;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    @Autowired
    private ModelMapper modelMapper;

    public UserResponse toDto(UserEntity entity) {
        return modelMapper.map(entity, UserResponse.class);
    }

    public UserEntity toEntity(UserRequest userRequest) {
        return modelMapper.map(userRequest, UserEntity.class);
    }
}
