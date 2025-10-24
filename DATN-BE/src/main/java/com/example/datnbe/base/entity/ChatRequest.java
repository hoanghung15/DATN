package com.example.datnbe.base.entity;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class ChatRequest {
    private List<Map<String, String>> history;
}
