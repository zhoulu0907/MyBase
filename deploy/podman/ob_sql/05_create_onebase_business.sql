\set ON_ERROR_STOP on

-- 数据库准备脚本
-- 1. 请使用有创建角色/数据库权限的账号执行
-- 2. 默认先连接维护库 pg
-- 3. 会创建 onebase 用户、创建数据库 onebase_business、并统一授予 public schema 下对象权限

\connect pg

DO $$
BEGIN
  IF NOT EXISTS (SELECT 1 FROM pg_roles WHERE rolname = 'onebase') THEN
    CREATE ROLE onebase LOGIN PASSWORD 'onebase';
  ELSE
    ALTER ROLE onebase WITH LOGIN PASSWORD 'onebase';
  END IF;
END
$$;

SELECT format(
  'CREATE DATABASE %I OWNER onebase ENCODING ''UTF8'' TEMPLATE template0',
  'onebase_business'
)
WHERE NOT EXISTS (
  SELECT 1
  FROM pg_database
  WHERE datname = 'onebase_business'
)
\gexec

SELECT format(
  'ALTER DATABASE %I OWNER TO onebase',
  'onebase_business'
)
WHERE EXISTS (
  SELECT 1
  FROM pg_database
  WHERE datname = 'onebase_business'
    AND pg_get_userbyid(datdba) <> 'onebase'
)
\gexec

GRANT ALL PRIVILEGES ON DATABASE onebase_business TO onebase;
GRANT CONNECT, TEMPORARY ON DATABASE onebase_business TO onebase;

\connect onebase_business

GRANT USAGE, CREATE ON SCHEMA public TO onebase;
GRANT SELECT, INSERT, UPDATE, DELETE, TRUNCATE, REFERENCES, TRIGGER ON ALL TABLES IN SCHEMA public TO onebase;
GRANT USAGE, SELECT, UPDATE ON ALL SEQUENCES IN SCHEMA public TO onebase;
GRANT EXECUTE ON ALL FUNCTIONS IN SCHEMA public TO onebase;

ALTER DEFAULT PRIVILEGES IN SCHEMA public
GRANT SELECT, INSERT, UPDATE, DELETE, TRUNCATE, REFERENCES, TRIGGER ON TABLES TO onebase;

ALTER DEFAULT PRIVILEGES IN SCHEMA public
GRANT USAGE, SELECT, UPDATE ON SEQUENCES TO onebase;

ALTER DEFAULT PRIVILEGES IN SCHEMA public
GRANT EXECUTE ON FUNCTIONS TO onebase;
