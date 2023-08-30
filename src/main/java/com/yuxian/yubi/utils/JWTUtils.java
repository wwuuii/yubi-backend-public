package com.yuxian.yubi.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.google.gson.Gson;
import com.yuxian.yubi.enums.ErrorCode;
import com.yuxian.yubi.exception.BusinessException;
import com.yuxian.yubi.model.entity.User;

import java.util.Calendar;
import java.util.HashMap;

public class JWTUtils {
    private static String SIGNATURE = "token!@#$%^7890";

    /**
     * 生成token
     * @param map //传入payload
     * @return 返回token
     */
    public static String getToken(HashMap<String,String> map){
        JWTCreator.Builder builder = JWT.create();
        map.forEach((k,v)->{
            builder.withClaim(k,v);
        });
        Calendar instance = Calendar.getInstance();
        instance.add(Calendar.DATE,3);
        builder.withExpiresAt(instance.getTime());
        return builder.sign(Algorithm.HMAC256(SIGNATURE));
    }

    /**
     * 验证token
     * @param token
     */
    public static DecodedJWT verify(String token){
        return JWT.require(Algorithm.HMAC256(SIGNATURE)).build().verify(token);
    }

    /**
     * 验证token
     * @param token
     */
    public static User verifyAndGetUser(String token){
        try {
            String userJson = verify(token).getClaim("user").asString();
            Gson gson = new Gson();
            User user = gson.fromJson(userJson, User.class);
            return user;
        }catch (Exception e) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
    }

}
