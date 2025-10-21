package com.example.datnbe.base.controller;

import com.example.datnbe.base.service.ChannelServiceImpl;
import com.example.datnbe.dto.request.ChannelCreateRequest;
import com.example.datnbe.dto.response.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/channel")
@Tag(name = "[CHANNEL]")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ChannelController {
    ChannelServiceImpl channelService;

    @PostMapping
    public ApiResponse createChannl(@RequestBody ChannelCreateRequest request) {
        return channelService.createNewChannel(request);
    }
}
