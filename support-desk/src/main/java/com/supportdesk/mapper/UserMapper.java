package com.supportdesk.mapper;

import com.supportdesk.dto.auth.RegisterRequest;
import com.supportdesk.dto.common.UserResponse;
import com.supportdesk.entity.UserEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserEntity toEntity(RegisterRequest request);

    UserResponse toResponse(UserEntity user);

}
