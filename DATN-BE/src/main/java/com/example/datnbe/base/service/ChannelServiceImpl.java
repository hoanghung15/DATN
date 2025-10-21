package com.example.datnbe.base.service;

import com.example.datnbe.base.custom.CustomUserDetails;
import com.example.datnbe.base.entity.Channel;
import com.example.datnbe.base.entity.Group;
import com.example.datnbe.base.entity.User;
import com.example.datnbe.base.repository.ChannelRepository;
import com.example.datnbe.base.repository.GroupRepository;
import com.example.datnbe.base.repository.UserRepository;
import com.example.datnbe.dto.ChannelDTO;
import com.example.datnbe.dto.request.ChannelCreateRequest;
import com.example.datnbe.dto.response.ApiResponse;
import com.example.datnbe.mapper.CommonMapper;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ChannelServiceImpl implements ChannelService {
    UserRepository userRepository;
    GroupRepository groupRepository;
    CommonMapper commonMapper;
    ChannelRepository channelRepository;

    @Transactional
    @Override
    public ApiResponse createNewChannel(ChannelCreateRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        String username = userDetails.getUsername();
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new RuntimeException("User not found");
        }
        Group group = groupRepository.findById(request.getGroupId()).orElseThrow(
                () -> new RuntimeException("Group not found"));
        if (!group.getOwner().equals(user)) {
            throw new RuntimeException("You are not group's owner");
        }
        Channel newChannel = commonMapper.toChannel(request);
        newChannel.setGroup(group);
        channelRepository.saveAndFlush(newChannel);
        ChannelDTO newChannelDTO = commonMapper.toChannelDTO(newChannel);
        newChannelDTO.setGroupId(group.getId());

        return ApiResponse.builder()
                .code(HttpStatus.CREATED.value())
                .message("Created new channel successfully")
                .result(newChannelDTO)
                .build();
    }

    @Transactional
    @Override
    public void createGeneralChannel(Group group) {
        Channel channel = new Channel();
        channel.setName("General");
        channel.setDescription("This is General Channel for every groups");
        channel.setPrivateChannel(false);
        channel.setGroup(group);
        channelRepository.save(channel);
    }
}
