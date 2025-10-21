package com.example.datnbe.base.service;

import com.example.datnbe.base.entity.Group;
import com.example.datnbe.dto.request.ChannelCreateRequest;
import com.example.datnbe.dto.response.ApiResponse;

public interface ChannelService {
    ApiResponse createNewChannel(ChannelCreateRequest request);

    public void createGeneralChannel(Group group);

}
