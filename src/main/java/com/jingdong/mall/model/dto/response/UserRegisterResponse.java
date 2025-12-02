// model/dto/response/UserRegisterResponse.java
package com.jingdong.mall.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
/*
* 返回体，不解释
* */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRegisterResponse {
    private String token;
    private UserInfo userInfo;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserInfo {
        private Long id;
        private String userName;
        private String avatar;
    }
}