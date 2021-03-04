package com.miniproject.yeolgongdabang.user;

import com.miniproject.yeolgongdabang.user.dto.UserJoinRequestDto;
import com.miniproject.yeolgongdabang.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
public class UserApi {

    private final UserService userService;

    @PostMapping("/join")
    public void join(@RequestBody @Valid UserJoinRequestDto request) {
        userService.join(request.getPhone());
    }
}