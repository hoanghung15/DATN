package com.example.datnbe.mapper;

import com.example.datnbe.base.entity.User;
import com.example.datnbe.dto.request.UserCreationRequest;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CommonMapper {
    User toUser(UserCreationRequest request);
}
