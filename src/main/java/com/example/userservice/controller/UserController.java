package com.example.userservice.controller;

import com.example.userservice.core.BaseResponse;
import com.example.userservice.model.request.PostUserListRequestBody;
import com.example.userservice.service.KafkaService;
import com.example.userservice.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.concurrent.ExecutionException;


@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final KafkaService kafkaService;

    @PostMapping("/list")
    private BaseResponse<?> postList(@RequestBody PostUserListRequestBody requestBody, HttpServletRequest servletRequest) {
        return new BaseResponse<>(userService.postUserList(requestBody));
    }
    @GetMapping("/export")
    public ResponseEntity<?> export() throws IOException {
        return userService.export();
    }
    @GetMapping("/export-multi-thread")
    public ResponseEntity<?> exportMultiThread(@RequestParam("batchSize") int batchSize, @RequestParam("threadCount") int threadCount) throws IOException, InterruptedException, ExecutionException {
        return userService.exportMultiThread(batchSize, threadCount);
    }
    @GetMapping("/insert")
    public ResponseEntity<?> insert(@RequestParam("records") int records, @RequestParam("threadCount") int threadCount) {
        return ResponseEntity.ok(userService.insertMultiThreadProducts(records,threadCount));
    }
    @GetMapping("/create-message")
    public ResponseEntity<?> createMessage() {
        return ResponseEntity.ok(kafkaService.createMessage());
    }
}
