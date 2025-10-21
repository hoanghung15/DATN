package com.example.datnbe.mapper;

import com.example.datnbe.base.entity.Channel;
import com.example.datnbe.base.entity.Group;
import com.example.datnbe.base.entity.User;
import com.example.datnbe.dto.ChannelDTO;
import com.example.datnbe.dto.GroupDTO;
import com.example.datnbe.dto.request.ChannelCreateRequest;
import com.example.datnbe.dto.request.GroupCreateRequest;
import com.example.datnbe.dto.request.UserCreationRequest;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CommonMapper {
    User toUser(UserCreationRequest request);

    Group toGroup(GroupCreateRequest request);

    GroupDTO toGroupDTO(Group group);

    Channel toChannel(ChannelCreateRequest request);

    ChannelDTO toChannelDTO(Channel channel);
}
