# web_app_server

**采用的技术：** 
* 后端: springboot、spring-security、mybatis-plus、redis、websocket 、maven 、 git 
* 前端： layui 、jquery、js

**开发工具：**
idea

**环境要求：**  
* 后端：jdk8 、 MYSQL服务 、 Redis服务   
* 前端：浏览器支持websocket

**运行步骤：**

1. 启动本地redis（或修改redis配置，mysql已有远程服务）
2. 用idea打开工程，通过类WebAppServerApplication启动（或maven打包通过jar命令启动），完成后登陆127.0.0.1:8080

**功能说明：**

1. 登陆页中点击保存，成功后会在本地生成文件，同时在数据库中写入文件信息；

2. 历史信息页中会获取所有保存的文件信息，点击文件名进行下载，点击编辑进行文件编辑；

3. 编辑页中会回显文件内容，首个用户进入有60s进行编辑，超过60s不可编辑；如果中途第二个用户进入，此时第二个用户的
   页面为不可编辑状态，只有等待第一个用户超过编辑时间方可编辑；

4. 多用户编辑时，有一方编辑并保存后，其他用户可实时接收到保存后的内容；
  
**演示：**

1. 启动
![Image text](https://github.com/ZProcess/web_app_server/blob/master/img-folder/4.png)



2. 登陆页
![Image text](https://github.com/ZProcess/web_app_server/blob/master/img-folder/1.png)



3. 历史信息页
![Image text](https://github.com/ZProcess/web_app_server/blob/master/img-folder/2.png)




4. 编辑页
![Image text](https://github.com/ZProcess/web_app_server/blob/master/img-folder/3.png)
