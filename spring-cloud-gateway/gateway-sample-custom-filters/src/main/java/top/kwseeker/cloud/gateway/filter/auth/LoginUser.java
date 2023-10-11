package top.kwseeker.cloud.gateway.filter.auth;

import lombok.Data;

import java.util.List;

@Data
public class LoginUser {

    /**
     * 用户编号
     */
    private Long id;
    ///**
    // * 用户类型
    // */
    //private Integer userType;
    ///**
    // * 租户编号
    // */
    //private Long tenantId;
    ///**
    // * 授权范围
    // */
    //private List<String> scopes;

}
