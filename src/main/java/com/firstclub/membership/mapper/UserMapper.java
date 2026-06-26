package com.firstclub.membership.mapper;

import com.firstclub.membership.dto.UserResponse;
import com.firstclub.membership.entity.User;

public final class UserMapper {

    private UserMapper() {
    }

    public static UserResponse toResponse(User user) {
        return new UserResponse(user.getId(), user.getName(), user.getEmail(), user.getCohort());
    }
}
