// model/dto/response/NoticeListResponse.java
package com.jingdong.mall.model.dto.response;

import lombok.Data;
import java.util.List;

@Data
public class NoticeListResponse {
    private List<NoticeResponse> list;
    private Long total;
    private Integer page;
    private Integer pageSize;
}