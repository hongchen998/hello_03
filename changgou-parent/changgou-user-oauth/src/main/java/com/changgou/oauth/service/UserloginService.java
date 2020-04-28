package com.changgou.oauth.service;

import com.changgou.oauth.util.AuthToken;

/**
 * @author hongchen
 * @date 2020/4/16 0:15
 */
public interface UserloginService {
    /*/**
     * @author hongchen
     * @Description
     * @Date 0:35 2020/4/16
     * @Param  * @param username
     * @param password
     * @param clientId
     * @param clientSecret
     * @param grant_type
     * @return java.util.Map<java.lang.String,java.lang.Object>
     **/
    AuthToken login(String username, String password, String clientId, String clientSecret, String grant_type);
}
