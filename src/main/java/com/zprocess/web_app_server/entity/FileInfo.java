package com.zprocess.web_app_server.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("file_info")
public class FileInfo {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String fileName;
    private String uuid;
    private String md5;
    private Date createTime;
    private Date updateTime;
    private String createBy;
    private String updateBy;
}
