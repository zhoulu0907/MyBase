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