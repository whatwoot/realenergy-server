## 项目结构
### sp-common 系统-基础支撑包
### sp-web-common 系统-web应用支撑包
### sp-job-admin 任务-暂时未使用
### mybatis-plus-generator 代码生成器
### xxx-server应用包
### nginx ng的配置

### rsa加密密钥
#### 生成pkcs1格式的密钥
```
openssl genrsa -out private_pkcs1.pem 4096
```
#### 生成pkcs8格式的密钥
```
openssl pkcs8 -topk8 -inform PEM -in private_pkcs1.pem -outform PEM -nocrypt -out private_pkcs8.pem
```

### 分支 
main分支为生产版本
dev分支为最新开发

###
release 版本  

###



#### 单项目打包
mvn clean install -pl {项目名} -am  
eg: mvn clean install -pl follow-job -am




