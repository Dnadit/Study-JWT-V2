package com.dnadit.study_security.service;

import com.dnadit.study_security.domain.User;
import com.dnadit.study_security.exception.AppException;
import com.dnadit.study_security.exception.ErrorCode;
import com.dnadit.study_security.repository.UserRepository;
import com.dnadit.study_security.utils.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder;

    @Value("${jwt.token.secret}")
    private String key;
    private Long expiredTimeMs = 1000 * 60 * 60l; // 1시간

    public String join(String userName, String password) {

        // userName 중복 check
        userRepository.findByUserName(userName)
                .ifPresent(user -> {
                    throw new AppException(ErrorCode.USERNAME_DUPLICATED, userName + "는 이미 있습니다.");
                });

        // 저장
        User user = User.builder()
                .userName(userName)
                .password(encoder.encode(password))
                .build();
        userRepository.save(user);

        return "SUCCESS";
    }

    public String login(String userName, String password) {
        // userName 없음
        User selectedUser = userRepository.findByUserName(userName)
                .orElseThrow(() -> new AppException(ErrorCode.USERNAME_NOT_FOUND, userName + "이 없습니다."));

        // password 틀림
        if(!encoder.matches(password, selectedUser.getPassword())) { // matches parameter 자리 지켜줘야 함!
            throw new AppException(ErrorCode.INVALID_PASSWORD, "패스워드를 잘 못 입력 하였습니다.");
        }

        String token = JwtTokenUtil.createToken(selectedUser.getUserName(), key, expiredTimeMs);

        // 앞에서 Exception안났으면 토큰 발행
        return token;
    }
}
