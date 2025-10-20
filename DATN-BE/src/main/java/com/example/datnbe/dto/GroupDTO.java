package com.example.datnbe.dto;

import com.example.datnbe.Enum.GroupStatus;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GroupDTO {
    String id;
    String code;
    String name;
    String description;
    String icon_color;
    String text_color;
    String thumbnail_url;
    String owner_id;

    LocalDateTime createdAt;
    LocalDateTime updatedAt;
    GroupStatus status;

    boolean is_private;
}
