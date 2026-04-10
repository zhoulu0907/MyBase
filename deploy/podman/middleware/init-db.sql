-- OneBase 数据库初始化脚本
-- 创建所需的数据库

-- 创建业务数据库
CREATE DATABASE onebase_business;

-- 创建 DolphinScheduler 数据库
CREATE DATABASE dolphinscheduler;

-- 授予用户权限
GRANT ALL PRIVILEGES ON DATABASE onebase_business TO onebase;
GRANT ALL PRIVILEGES ON DATABASE dolphinscheduler TO onebase;

-- 切换到 onebase_cloud_v3 数据库
\c onebase_cloud_v3

-- 创建必要的扩展
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS "pg_trgm";

-- 切换到 onebase_business 数据库
\c onebase_business

-- 创建必要的扩展
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS "pg_trgm";