package com.example.datnbe.base.controller;

import com.example.datnbe.base.service.GroupServiceImpl;
import com.example.datnbe.dto.request.GroupCreateRequest;
import com.example.datnbe.dto.request.InviteGroupRequest;
import com.example.datnbe.dto.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "[GROUP]")
@RequestMapping("group")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class GroupController {
    GroupServiceImpl groupService;

    @Operation(description = "Create new Group", summary = "Create new Group")
    @PostMapping("create")
    public ApiResponse createGroup(@RequestBody GroupCreateRequest request) {
        return groupService.createNewGroup(request);
    }

    @Operation(description = "Join new Group", summary = "Join new Group")
    @PostMapping("join")
    public ApiResponse joinGroup(@RequestParam String groupCode) {
        return groupService.joinNewGroup(groupCode);
    }

    @PostMapping("/invite")
    public ApiResponse inviteGroup(@RequestBody  InviteGroupRequest request) {
        return groupService.inviteToGroup(request);
    }
}
