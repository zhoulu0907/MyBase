# OneBase管理后台脚手架
## 核心指令
单体模式打包命令：
Mac:  mvn package -am -pl onebase-server -Dmaven.test.skip=true
Win:  mvn package -am -pl onebase-server '-Dmaven.test.skip=true'

Cloud模式打包命令：
Mac: mvn clean package -Dmaven.test.skip=true
Win: mvn clean package '-Dmaven.test.skip=true'

服务启动：
sh deploy.sh start

## 数据库设置

自增ID设置从最大值开始(以部门system_dept为例)：
SELECT setval('system_dept_id_seq', (SELECT MAX(id) FROM system_dept));
