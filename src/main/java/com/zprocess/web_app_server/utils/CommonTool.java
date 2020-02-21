package com.zprocess.web_app_server.utils;


import com.zprocess.web_app_server.vo.FormVo;
import org.springframework.util.DigestUtils;

import java.util.UUID;

public class CommonTool {

    public static String getUuid(){
        return  UUID.randomUUID().toString().replaceAll("-", "");
    }

    public static String getMd5(FormVo formVo){
        String fileName = formVo.getFileName().trim();
        String text = formVo.getText();
        String base = fileName+text;
        String md5 = DigestUtils.md5DigestAsHex(base.getBytes());
        return md5;
    }
}
