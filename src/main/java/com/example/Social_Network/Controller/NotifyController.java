package com.example.Social_Network.Controller;

import com.example.Social_Network.DTO.Request.ApiResponse;
import com.example.Social_Network.DTO.Response.NotifyResponse;
import com.example.Social_Network.Service.NotificationService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/notifications")
public class NotifyController {
    NotificationService notificationService;

    @GetMapping("/")
    ApiResponse<List<NotifyResponse>> getAllNotifications(@RequestParam int limit, @RequestParam int offset) {
        List<NotifyResponse> result = notificationService.getAllNotification(limit, offset);

        return ApiResponse.<List<NotifyResponse>>builder().result(result).build();
    }
}
