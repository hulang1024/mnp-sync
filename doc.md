# 部署
1. 将mnp-sync.jar、config.properties拷贝到主机上。
2. 安装redis。
3. 编辑config.properties，配置redis服务器等相关信息。

## 配置说明
属性名|含义
-|-
redis_server.ip   |  数据同步redis服务器的IP
redis_server.port |  数据同步redis服务器的端口
redis_cli_path    |  本地redis-cli的路径
file_api.base_url |  携号转网服务数据拉取HTTP API的URL


# 用法

## 命令行用法
```shell
java -jar mnp-sync.jar [可选参数...]
````
如果提示编码错误，需带上一个参数：
```shell
java -Dfile.encoding=UTF8 -jar mnp-sync.jar [可选参数...]
````
可选参数
* 可选的参数为空  
  处理昨日数据。

* pull  
  * pull all  
    先清空临时目录，然后拉取下载历史所有数据（tim文件）。 
  * pull [日期]  
    先清空临时目录，然后拉取下载指定日期数据，日期可以是20200117，也可以是月份如202001。

* merge  
  读取临时目录中所有tim文件，然后合并生成zip，删除tim文件。

* sync   
  读取临时目录中所有zip文件，然后为每个zip文件转换生成一个redis命令文件，最后发送给redis服务器。

## 文件介绍
* /temp  
  程序生成的临时目录。从服务器下载的文件，合并之后的文件，和生成的redis命令文本文件都会放在此处。
* /logs  
  日志。


# 定时任务
每天上午6点处理昨日数据。  
使用crontab实现: ` crontab -e`, 增加如下代码：
```shell
0 6 * * * /pathto/java -Dfile.encoding=UTF8 -jar /pathto/mnp-sync.jar
```
注意java和mnp-sync.jar的位置写绝对路径。