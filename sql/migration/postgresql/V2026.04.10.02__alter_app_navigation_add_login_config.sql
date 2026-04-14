-- 为 app_navigation 表添加 app_third_user_enable 字段
ALTER TABLE "public"."app_navigation"
ADD COLUMN IF NOT EXISTS "app_third_user_enable" varchar(16) COLLATE "pg_catalog"."default" NOT NULL DEFAULT '1';

-- 添加字段注释
COMMENT ON COLUMN "public"."app_navigation"."app_third_user_enable" IS '第三方用户启用标识';

-- 为 app_navigation 表添加 app_user_register_show 字段
ALTER TABLE "public"."app_navigation"
ADD COLUMN IF NOT EXISTS "app_user_register_show" varchar(16) COLLATE "pg_catalog"."default" NOT NULL DEFAULT '1';

-- 添加字段注释
COMMENT ON COLUMN "public"."app_navigation"."app_user_register_show" IS '显示注册入口 0-不显示 1-显示';

-- 为 app_navigation 表添加 app_user_forgetpwd_show 字段
ALTER TABLE "public"."app_navigation"
ADD COLUMN IF NOT EXISTS "app_user_forgetpwd_show" varchar(16) COLLATE "pg_catalog"."default" NOT NULL DEFAULT '1';

-- 添加字段注释
COMMENT ON COLUMN "public"."app_navigation"."app_user_forgetpwd_show" IS '显示忘记密码入口 0-不显示 1-显示';

-- 为 app_navigation 表添加 app_login_main_pic 字段
ALTER TABLE "public"."app_navigation"
ADD COLUMN IF NOT EXISTS "app_login_main_pic" varchar(32) COLLATE "pg_catalog"."default";

-- 添加字段注释
COMMENT ON COLUMN "public"."app_navigation"."app_login_main_pic" IS '登录页主图，文件ID';
