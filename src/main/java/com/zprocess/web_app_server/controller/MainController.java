package com.zprocess.web_app_server.controller;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zprocess.web_app_server.entity.FileInfo;
import com.zprocess.web_app_server.service.FileInfoService;
import com.zprocess.web_app_server.utils.CommonTool;
import com.zprocess.web_app_server.utils.FileTool;
import com.zprocess.web_app_server.utils.RedisTool;
import com.zprocess.web_app_server.utils.SpringUtil;
import com.zprocess.web_app_server.vo.FormVo;
import com.zprocess.web_app_server.vo.PageVo;
import com.zprocess.web_app_server.vo.WebsocketVo;
import io.lettuce.core.RedisURI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


@Controller
public class MainController {

    @Autowired
    private RedisTool redisTool;

    @Autowired
    private FileInfoService fileInfoService;

    @RequestMapping(value = "/")
    public String newPage() {
        return "/newPage";
    }

    @RequestMapping(value = "/save", method = RequestMethod.POST)
    @ResponseBody
    public String save(@RequestBody FormVo formVo) throws Exception {
        if (formVo == null || StringUtils.isEmpty(formVo.getFileName())) {
            return "文件名不能为空";
        }
        String uuid = FileTool.saveFile(formVo,false);
        saveFileToDB(formVo, uuid);
        return "文件保存成功";
    }

    private void saveFileToDB(FormVo formVo, String uuid) {
        FileInfo fileInfo = new FileInfo();
        fileInfo.setUuid(uuid);
        fileInfo.setFileName(formVo.getFileName());
        fileInfo.setMd5(CommonTool.getMd5(formVo));
        fileInfoService.save(fileInfo);
    }

    @RequestMapping(value = "/history")
    public String historyPage() {
        return "/historyPage";
    }

    @RequestMapping(value = "/editPage")
    public String editPage() {
        return "/editPage";
    }


    @RequestMapping(value = "/getHistoryData")
    @ResponseBody
    public Map<String, Object> getHistoryData(@RequestParam Long pageNum, @RequestParam Long pageSize) {
        IPage<FileInfo> page = new Page<>();
        page.setSize(pageSize);
        page.setCurrent(pageNum);
        IPage<FileInfo> result = fileInfoService.page(page);
        Map<String, Object> map = new HashMap<>();
        map.put("code", 0);
        map.put("msg", "");
        map.put("count", result.getTotal());
        map.put("data", result.getRecords());
        return map;
    }

    @RequestMapping(value = "/getFileText")
    @ResponseBody
    public String getFileText(@RequestParam String uuid) throws IOException {
        String uuidFileName = uuid + ".txt";
        String url = FileTool.getFilePath() + uuidFileName;
        File file = new File(url);
        if(file.exists()){
            FileReader reader = new FileReader(file);//定义一个fileReader对象，用来初始化BufferedReader
            BufferedReader bReader = new BufferedReader(reader);//new一个BufferedReader对象，将文件内容读取到缓存
            StringBuilder sb = new StringBuilder();//定义一个字符串缓存，将字符串存放缓存中
            String s = "";
            while ((s =bReader.readLine()) != null) {//逐行读取文件内容，不读取换行符和末尾的空格
                sb.append(s + "\n");//将读取的字符串添加换行符后累加存放在缓存中
            }
            bReader.close();
            return sb.toString();
        }else {
            return "";
        }
    }

    @RequestMapping(value = "/download")
    @ResponseBody
    public void download(@RequestParam String uuid, @RequestParam String fileName,HttpServletResponse response) throws Exception {
        String uuidFileName = uuid + ".txt";
        String url = FileTool.getFilePath() + uuidFileName;
        File file = new File(url);
        if (!file.exists()) {
            throw new Exception("文件不存在");
        } else {

            // 配置文件下载
            response.setHeader("content-type", "application/octet-stream");
            response.setContentType("application/octet-stream");
            // 下载文件能正常显示中文
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName+".txt", "UTF-8"));
            // 实现文件下载
            byte[] buffer = new byte[1024];
            FileInputStream fis = null;
            BufferedInputStream bis = null;
            try {
                fis = new FileInputStream(file);
                bis = new BufferedInputStream(fis);
                OutputStream os = response.getOutputStream();
                int i = bis.read(buffer);
                while (i != -1) {
                    os.write(buffer, 0, i);
                    i = bis.read(buffer);
                }
                System.out.println("Download  successfully!");

            } catch (Exception e) {
                System.out.println("Download  failed!");

            } finally {
                if (bis != null) {
                    try {
                        bis.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (fis != null) {
                    try {
                        fis.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    @RequestMapping(value = "/getFileInfoByUuid",method = RequestMethod.POST)
    @ResponseBody
    public FileInfo getFileInfoByUuid(@RequestBody String uuid) {
        QueryWrapper<FileInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("uuid",uuid);
        return fileInfoService.getOne(queryWrapper);
    }

    @RequestMapping(value = "/websocketTest")
    public String websocketTest() {
        return "/websocketTest";
    }

    @RequestMapping(value = "/updateFile")
    @ResponseBody
    public String updateFile(@RequestBody FormVo formVo) throws Exception {
        if(StringUtils.isEmpty(formVo.getUuid())){
            throw new Exception("uuid 不能为空");
        }
        if(!redisTool.hasKey(formVo.getUuid()) || !formVo.getUserUuid().equals(redisTool.get(formVo.getUuid()))){
            return "已超过更改时间！！！";
        }
        FileTool.saveFile(formVo,true);
        FileInfo fileInfo = getFileInfoByUuid(formVo.getUuid());
        fileInfo.setUpdateTime(new Date());
        fileInfoService.updateById(fileInfo);
        //推送更新后的内容
        WebsocketVo websocketVo = new WebsocketVo();
        websocketVo.setE("3");
        websocketVo.setT(formVo.getText());
        WebSocketServer.sendInfoToOthers(JSON.toJSONString(websocketVo),formVo.getUserUuid());
        return "更新文件成功";
    }
}

