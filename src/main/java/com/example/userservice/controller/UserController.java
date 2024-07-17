package com.example.userservice.controller;

import com.example.userservice.core.BaseResponse;
import com.example.userservice.model.request.PostUserListRequestBody;
import com.example.userservice.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/list")
    private BaseResponse<?> postList(@RequestBody PostUserListRequestBody requestBody, HttpServletRequest servletRequest) {
        return new BaseResponse<>(userService.postUserList(requestBody));
    }
}
