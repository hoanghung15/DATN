package com.example.datnbe.base.service;

import com.example.datnbe.dto.request.GroupCreateRequest;
import com.example.datnbe.dto.request.InviteGroupRequest;
import com.example.datnbe.dto.response.ApiResponse;
import org.apache.kafka.shaded.com.google.protobuf.Api;

public interface GroupService {
    ApiResponse createNewGroup(GroupCreateRequest request);

    ApiResponse joinNewGroup(String groupCode);

    ApiResponse inviteToGroup(InviteGroupRequest request);

    String generateIconColor();

    String generateCodeJoin(int size);

    String generateTextColor(String backgroundColor);
}
