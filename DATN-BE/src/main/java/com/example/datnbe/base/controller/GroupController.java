package com.example.datnbe.base.controller;

import com.example.datnbe.base.service.GroupServiceImpl;
import com.example.datnbe.dto.request.GroupCreateRequest;
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
@RequiredArgsConstructor
@Tag(name = "GROUP")
@RequestMapping("group")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class GroupController {
    GroupServiceImpl groupService;

    @PostMapping
    public ApiResponse createGroup(@RequestBody GroupCreateRequest request) {
        return groupService.createNewGroup(request);
    }


}
