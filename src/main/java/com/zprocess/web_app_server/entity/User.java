package com.zprocess.web_app_server.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("user")
public class User {

    @TableId(type = IdType.AUTO)
    private Integer id;
    @TableField("user_name")
    private String username;    //用户名
    @TableField("pass_word")
    private String password;    //用户密码

}
