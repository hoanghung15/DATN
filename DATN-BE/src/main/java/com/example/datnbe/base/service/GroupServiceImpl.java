package com.example.datnbe.base.service;

import com.example.datnbe.Enum.GroupStatus;
import com.example.datnbe.Enum.MemberRole;
import com.example.datnbe.Enum.MemberStatus;
import com.example.datnbe.base.custom.CustomUserDetails;
import com.example.datnbe.base.entity.Group;
import com.example.datnbe.base.entity.GroupMember;
import com.example.datnbe.base.entity.User;
import com.example.datnbe.base.repository.GroupMemberRepository;
import com.example.datnbe.base.repository.GroupRepository;
import com.example.datnbe.base.repository.UserRepository;
import com.example.datnbe.dto.GroupDTO;
import com.example.datnbe.dto.request.GroupCreateRequest;
import com.example.datnbe.dto.request.InviteGroupRequest;
import com.example.datnbe.dto.response.ApiResponse;
import com.example.datnbe.mapper.CommonMapper;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class GroupServiceImpl implements GroupService {

    GroupRepository groupRepository;
    GroupMemberRepository groupMemberRepository;
    CommonMapper commonMapper;
    UserRepository userRepository;
    KafkaTemplate<String, String> kafkaTemplate;
    ChannelServiceImpl channelService;

    @Transactional
    @Override
    public ApiResponse joinNewGroup(String groupCode) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();
        User user = userRepository.findByUsername(username);

        Group group = groupRepository.findByCode(groupCode).orElseThrow(() -> new RuntimeException("Group not found"));
        boolean checkExist = groupMemberRepository.existsByUser_IdAndGroup_Id(user.getId(), group.getId());
        if (checkExist) {
            throw new RuntimeException("Exist info in database");
        }
        GroupMember groupMember = new GroupMember();

        groupMember.setGroup(group);
        groupMember.setUser(user);
        groupMember.setRole(MemberRole.MEMBER);
        groupMember.setStatus(MemberStatus.ACTIVE);

        groupMemberRepository.save(groupMember);

        return ApiResponse.builder()
                .code(HttpStatus.OK.value())
                .message("Successfully joined group")
                .build();
    }

    @Transactional
    @Override
    public ApiResponse createNewGroup(GroupCreateRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        boolean isPrivate = request.isPrivateGroup();

        String username = customUserDetails.getUsername();
        User user = userRepository.findByUsername(username);

        Group group = commonMapper.toGroup(request);
        group.setOwner(user);
        group.setCode(generateCodeJoin(6));
        group.setIcon_color(generateIconColor());
        group.setText_color(generateTextColor(generateIconColor()));
        group.set_private(isPrivate);
        group.setStatus(GroupStatus.ACTIVE);

        GroupMember groupMember = new GroupMember();
        groupMember.setGroup(group);
        groupMember.setUser(user);
        groupMember.setRole(MemberRole.OWNER);
        groupMember.setStatus(MemberStatus.ACTIVE);

        groupRepository.save(group);
        groupMemberRepository.save(groupMember);

        GroupDTO groupDTO = commonMapper.toGroupDTO(group);
        groupDTO.setOwner_id(user.getId());

        channelService.createGeneralChannel(group);

        return ApiResponse.builder()
                .code(200)
                .message("Group created successfully")
                .result(groupDTO)
                .build();
    }

    @Transactional
    @Override
    public ApiResponse inviteToGroup(InviteGroupRequest request) {
        String groupCode = request.getGroupCode();
        String emailAddress = request.getEmailAddress();
        Group group = groupRepository.findByCode(groupCode).orElseThrow(() -> new RuntimeException("Group not found"));
        User user = userRepository.findByEmail((emailAddress));
        if (user == null) {
            throw new RuntimeException("User not found");
        }
        boolean checkExist = groupMemberRepository.existsByUser_IdAndGroup_Id(user.getId(), group.getId());
        if (checkExist) {
            throw new RuntimeException("Member already exists in this group");
        }
        GroupMember groupMember = new GroupMember();
        groupMember.setGroup(group);
        groupMember.setUser(user);
        groupMember.setRole(MemberRole.MEMBER);
        groupMember.setStatus(MemberStatus.ACTIVE);
        kafkaTemplate.send("notification-join-group-topic", "Đây là thư mời");
        groupMemberRepository.save(groupMember);

        return ApiResponse.builder()
                .code(HttpStatus.CREATED.value())
                .message("Member joined successfully")
                .build();
    }

    @Override
    public String generateIconColor() {
        List<String> bgColors = List.of(
                "#F44336",
                "#2196F3",
                "#4CAF50",
                "#FF9800",
                "#9C27B0"
        );
        Random random = new Random();
        return bgColors.get(random.nextInt(bgColors.size()));
    }

    @Override
    public String generateTextColor(String backgroundColor) {
        Map<String, String> colorMap = Map.of(
                "#F44336", "#FFFFFF",
                "#2196F3", "#FFFFFF",
                "#4CAF50", "#FFFFFF",
                "#FF9800", "#000000",
                "#9C27B0", "#FFFFFF"
        );
        return colorMap.getOrDefault(backgroundColor, "#FFFFFF");
    }

    @Override
    public String generateCodeJoin(int size) {
        String chars = "AaBbCcDdEeFfGgHhIiJjKkLlMmNnOoPpQqRrSsTtUyVvWwXxYyZz0123456789";
        StringBuilder code = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < size; i++) {
            code.append(chars.charAt(random.nextInt(chars.length())));
        }
        return code.toString();
    }

}
