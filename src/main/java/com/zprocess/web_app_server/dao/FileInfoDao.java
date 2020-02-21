package com.zprocess.web_app_server.dao;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zprocess.web_app_server.entity.FileInfo;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface FileInfoDao extends BaseMapper<FileInfo> {
}
