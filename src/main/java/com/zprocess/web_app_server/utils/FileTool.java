package com.zprocess.web_app_server.utils;

import com.zprocess.web_app_server.vo.FormVo;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


public class FileTool {

    //文件存储到本地
    private static final String filePath = "D:\\temp\\";

    public static String saveFile(FormVo formVo,Boolean isEdit) throws IOException {
            String uuid = "";
            if(isEdit){
                uuid = formVo.getUuid();
            }else {
                uuid = CommonTool.getUuid();
            }

            File dirName = new File(filePath);
            if(!dirName.exists()){
                dirName.mkdir();
            }
            String fileUrl = filePath+uuid+".txt";
            File writeName = new File(fileUrl); // 相对路径，如果没有则要建立一个新的output.txt文件
            if(!writeName.exists()) {
                writeName.createNewFile(); // 创建新文件,有同名的文件的话直接覆盖
            }
            FileWriter writer = new FileWriter(writeName);
            BufferedWriter out = new BufferedWriter(writer);
            out.write(formVo.getText());
            out.flush(); // 把缓存区内容压入文件
            out.close();
            return uuid;
    }
    public static String getFilePath(){
        return filePath;
    }
}
