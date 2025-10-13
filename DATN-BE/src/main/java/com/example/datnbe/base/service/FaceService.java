package com.example.datnbe.base.service;

import com.example.datnbe.dto.response.ApiResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FaceService {
    @NonFinal
    @Value("${server-deploy.server-python}")
    String flaskURL;

    public ApiResponse getIdByFace(MultipartFile file) throws IOException {
        try {
            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            ByteArrayResource fileAsResource = new ByteArrayResource(file.getBytes()) {
                @Override
                public String getFilename() {
                    return file.getOriginalFilename();
                }
            };

            var body = new org.springframework.util.LinkedMultiValueMap<String, Object>();
            body.add("file", fileAsResource);

            HttpEntity<MultiValueMap<String, Object>> requestEntity =
                    new HttpEntity<>(body, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(flaskURL, requestEntity, String.class);
            return ApiResponse.builder()
                    .code(200)
                    .message("Điểm danh thành công")
                    .result(response.getBody())
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.builder()
                    .code(400)
                    .message("Lỗi hệ thống nhận diện " + e.getMessage())
                    .build();

        }
    }
}
