package org.example.expert.domain.user.service;

import lombok.RequiredArgsConstructor;
import org.example.expert.config.auth.PasswordEncoder;
import org.example.expert.domain.user.dto.request.UserChangePasswordRequest;
import org.example.expert.domain.user.dto.response.UserResponse;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.repository.UserRepository;
import org.example.expert.ex.ErrorCode;
import org.example.expert.ex.InvalidRequestException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserResponse getUser(long userId) {
        return userRepository.findById(userId)
                .map(UserResponse::new)
                .orElseThrow(() -> new InvalidRequestException(ErrorCode.USER_NOT_FOUND));
    }

    @Transactional
    public void changePassword(long userId, UserChangePasswordRequest userChangePasswordRequest) {
        //존재하는 유저인지 검증
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new InvalidRequestException(ErrorCode.USER_NOT_FOUND));
        //비밀번호 변경 검증
        user.validatePassword(userChangePasswordRequest.getOldPassword());
        user.changePassword(passwordEncoder.encode(userChangePasswordRequest.getNewPassword()));
    }
}
