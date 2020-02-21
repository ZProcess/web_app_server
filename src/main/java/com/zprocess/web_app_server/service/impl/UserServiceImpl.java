package com.zprocess.web_app_server.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zprocess.web_app_server.dao.UserDao;
import com.zprocess.web_app_server.entity.User;
import com.zprocess.web_app_server.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserServiceImpl extends ServiceImpl<UserDao, User> implements UserService {
}
