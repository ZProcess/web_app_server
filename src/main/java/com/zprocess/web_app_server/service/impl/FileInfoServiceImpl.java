package com.zprocess.web_app_server.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zprocess.web_app_server.dao.FileInfoDao;
import com.zprocess.web_app_server.entity.FileInfo;
import com.zprocess.web_app_server.service.FileInfoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class FileInfoServiceImpl extends ServiceImpl<FileInfoDao, FileInfo> implements FileInfoService {
}
