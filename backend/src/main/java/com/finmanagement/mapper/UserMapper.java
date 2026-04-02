package com.finmanagement.mapper;

import com.finmanagement.dto.user.UserResponse;
import com.finmanagement.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "createdAt", source = "createdAt")
    UserResponse toResponse(User user);

    List<UserResponse> toResponseList(List<User> users);
}
