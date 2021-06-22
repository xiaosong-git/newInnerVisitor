package com.xiaosong.model.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author cwf
 * @date 2021/6/21 11:02
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserVo implements Serializable{
    private static final long serialVersionUID = -5232225373036499697L;
    private String token;

    private String username;
    private Long userRole;
    private Long userId;
    private String tel;
    //** 是否初始值
    @Builder.Default
    private String initialization="F";
    //是否验证码登入
    @Builder.Default
    private boolean codeLogin=false;


}
