package com.example.datnbe.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChannelDTO {
    String id;
    String name;
    String description;
    boolean privateChannel;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
    String groupId;
}
