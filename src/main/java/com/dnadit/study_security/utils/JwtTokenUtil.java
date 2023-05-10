package com.dnadit.study_security.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;

public class JwtTokenUtil {
    public static String createToken(String userName, String key, long expireTimeMs) {
        Claims claims = Jwts.claims(); // 일종의 map
        claims.put("userName", userName);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis())) // 만든시간
                .setExpiration(new Date(System.currentTimeMillis() + expireTimeMs)) // expireTimeMs는 만료시간
                .signWith(SignatureAlgorithm.HS256, key) // HS256알고리즘을 이용하여 key를 암호화
                .compact()
                ;

    }
}
