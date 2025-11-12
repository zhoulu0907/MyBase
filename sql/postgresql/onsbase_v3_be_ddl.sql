/*
 Navicat Premium Data Transfer

 Source Server         : ob3.0pg
 Source Server Type    : PostgreSQL
 Source Server Version : 100005 (100005)
 Source Host           : 10.0.104.38:5432
 Source Catalog        : onebase_cloud_v3
 Source Schema         : public

 Target Server Type    : PostgreSQL
 Target Server Version : 100005 (100005)
 File Encoding         : 65001

 Date: 05/08/2025 13:12:04
*/


-- ----------------------------
-- Sequence structure for field_type_mapping_id_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."field_type_mapping_id_seq";
CREATE SEQUENCE "public"."field_type_mapping_id_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 1
CACHE 1;
ALTER SEQUENCE "public"."field_type_mapping_id_seq" OWNER TO "postgres";

-- ----------------------------
-- Sequence structure for infra_api_access_log_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."infra_api_access_log_seq";
CREATE SEQUENCE "public"."infra_api_access_log_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 1
CACHE 1;
ALTER SEQUENCE "public"."infra_api_access_log_seq" OWNER TO "postgres";

-- ----------------------------
-- Sequence structure for infra_api_error_log_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."infra_api_error_log_seq";
CREATE SEQUENCE "public"."infra_api_error_log_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 1
CACHE 1;
ALTER SEQUENCE "public"."infra_api_error_log_seq" OWNER TO "postgres";

-- ----------------------------
-- Sequence structure for infra_config_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."infra_config_seq";
CREATE SEQUENCE "public"."infra_config_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 13
CACHE 1;
ALTER SEQUENCE "public"."infra_config_seq" OWNER TO "postgres";

-- ----------------------------
-- Sequence structure for infra_data_source_config_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."infra_data_source_config_seq";
CREATE SEQUENCE "public"."infra_data_source_config_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 1
CACHE 1;
ALTER SEQUENCE "public"."infra_data_source_config_seq" OWNER TO "postgres";

-- ----------------------------
-- Sequence structure for infra_file_config_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."infra_file_config_seq";
CREATE SEQUENCE "public"."infra_file_config_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 23
CACHE 1;
ALTER SEQUENCE "public"."infra_file_config_seq" OWNER TO "postgres";

-- ----------------------------
-- Sequence structure for infra_file_content_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."infra_file_content_seq";
CREATE SEQUENCE "public"."infra_file_content_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 1
CACHE 1;
ALTER SEQUENCE "public"."infra_file_content_seq" OWNER TO "postgres";

-- ----------------------------
-- Sequence structure for infra_file_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."infra_file_seq";
CREATE SEQUENCE "public"."infra_file_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 1
CACHE 1;
ALTER SEQUENCE "public"."infra_file_seq" OWNER TO "postgres";

-- ----------------------------
-- Sequence structure for infra_job_log_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."infra_job_log_seq";
CREATE SEQUENCE "public"."infra_job_log_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 1
CACHE 1;
ALTER SEQUENCE "public"."infra_job_log_seq" OWNER TO "postgres";

-- ----------------------------
-- Sequence structure for infra_job_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."infra_job_seq";
CREATE SEQUENCE "public"."infra_job_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 28
CACHE 1;
ALTER SEQUENCE "public"."infra_job_seq" OWNER TO "postgres";

-- ----------------------------
-- Sequence structure for metadata_system_fields_id_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."metadata_system_fields_id_seq";
CREATE SEQUENCE "public"."metadata_system_fields_id_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 1
CACHE 1;
ALTER SEQUENCE "public"."metadata_system_fields_id_seq" OWNER TO "postgres";

-- ----------------------------
-- Sequence structure for system_dept_id_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."system_dept_id_seq";
CREATE SEQUENCE "public"."system_dept_id_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 1
CACHE 1;
ALTER SEQUENCE "public"."system_dept_id_seq" OWNER TO "postgres";

-- ----------------------------
-- Sequence structure for system_dept_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."system_dept_seq";
CREATE SEQUENCE "public"."system_dept_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 114
CACHE 1;
ALTER SEQUENCE "public"."system_dept_seq" OWNER TO "postgres";

-- ----------------------------
-- Sequence structure for system_dict_data_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."system_dict_data_seq";
CREATE SEQUENCE "public"."system_dict_data_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 1537
CACHE 1;
ALTER SEQUENCE "public"."system_dict_data_seq" OWNER TO "postgres";

-- ----------------------------
-- Sequence structure for system_dict_type_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."system_dict_type_seq";
CREATE SEQUENCE "public"."system_dict_type_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 620
CACHE 1;
ALTER SEQUENCE "public"."system_dict_type_seq" OWNER TO "postgres";

-- ----------------------------
-- Sequence structure for system_login_log_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."system_login_log_seq";
CREATE SEQUENCE "public"."system_login_log_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 1
CACHE 1;
ALTER SEQUENCE "public"."system_login_log_seq" OWNER TO "postgres";

-- ----------------------------
-- Sequence structure for system_mail_account_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."system_mail_account_seq";
CREATE SEQUENCE "public"."system_mail_account_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 5
CACHE 1;
ALTER SEQUENCE "public"."system_mail_account_seq" OWNER TO "postgres";

-- ----------------------------
-- Sequence structure for system_mail_log_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."system_mail_log_seq";
CREATE SEQUENCE "public"."system_mail_log_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 1
CACHE 1;
ALTER SEQUENCE "public"."system_mail_log_seq" OWNER TO "postgres";

-- ----------------------------
-- Sequence structure for system_mail_template_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."system_mail_template_seq";
CREATE SEQUENCE "public"."system_mail_template_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 16
CACHE 1;
ALTER SEQUENCE "public"."system_mail_template_seq" OWNER TO "postgres";

-- ----------------------------
-- Sequence structure for system_menu_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."system_menu_seq";
CREATE SEQUENCE "public"."system_menu_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 2758
CACHE 1;
ALTER SEQUENCE "public"."system_menu_seq" OWNER TO "postgres";

-- ----------------------------
-- Sequence structure for system_notice_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."system_notice_seq";
CREATE SEQUENCE "public"."system_notice_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 5
CACHE 1;
ALTER SEQUENCE "public"."system_notice_seq" OWNER TO "postgres";

-- ----------------------------
-- Sequence structure for system_notify_message_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."system_notify_message_seq";
CREATE SEQUENCE "public"."system_notify_message_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 11
CACHE 1;
ALTER SEQUENCE "public"."system_notify_message_seq" OWNER TO "postgres";

-- ----------------------------
-- Sequence structure for system_notify_template_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."system_notify_template_seq";
CREATE SEQUENCE "public"."system_notify_template_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 1
CACHE 1;
ALTER SEQUENCE "public"."system_notify_template_seq" OWNER TO "postgres";

-- ----------------------------
-- Sequence structure for system_oauth2_access_token_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."system_oauth2_access_token_seq";
CREATE SEQUENCE "public"."system_oauth2_access_token_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 1
CACHE 1;
ALTER SEQUENCE "public"."system_oauth2_access_token_seq" OWNER TO "postgres";

-- ----------------------------
-- Sequence structure for system_oauth2_approve_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."system_oauth2_approve_seq";
CREATE SEQUENCE "public"."system_oauth2_approve_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 1
CACHE 1;
ALTER SEQUENCE "public"."system_oauth2_approve_seq" OWNER TO "postgres";

-- ----------------------------
-- Sequence structure for system_oauth2_client_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."system_oauth2_client_seq";
CREATE SEQUENCE "public"."system_oauth2_client_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 43
CACHE 1;
ALTER SEQUENCE "public"."system_oauth2_client_seq" OWNER TO "postgres";

-- ----------------------------
-- Sequence structure for system_oauth2_code_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."system_oauth2_code_seq";
CREATE SEQUENCE "public"."system_oauth2_code_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 1
CACHE 1;
ALTER SEQUENCE "public"."system_oauth2_code_seq" OWNER TO "postgres";

-- ----------------------------
-- Sequence structure for system_oauth2_refresh_token_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."system_oauth2_refresh_token_seq";
CREATE SEQUENCE "public"."system_oauth2_refresh_token_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 1
CACHE 1;
ALTER SEQUENCE "public"."system_oauth2_refresh_token_seq" OWNER TO "postgres";

-- ----------------------------
-- Sequence structure for system_operate_log_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."system_operate_log_seq";
CREATE SEQUENCE "public"."system_operate_log_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 1
CACHE 1;
ALTER SEQUENCE "public"."system_operate_log_seq" OWNER TO "postgres";

-- ----------------------------
-- Sequence structure for system_post_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."system_post_seq";
CREATE SEQUENCE "public"."system_post_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 6
CACHE 1;
ALTER SEQUENCE "public"."system_post_seq" OWNER TO "postgres";

-- ----------------------------
-- Sequence structure for system_role_menu_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."system_role_menu_seq";
CREATE SEQUENCE "public"."system_role_menu_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 5779
CACHE 1;
ALTER SEQUENCE "public"."system_role_menu_seq" OWNER TO "postgres";

-- ----------------------------
-- Sequence structure for system_role_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."system_role_seq";
CREATE SEQUENCE "public"."system_role_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 112
CACHE 1;
ALTER SEQUENCE "public"."system_role_seq" OWNER TO "postgres";

-- ----------------------------
-- Sequence structure for system_sms_channel_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."system_sms_channel_seq";
CREATE SEQUENCE "public"."system_sms_channel_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 7
CACHE 1;
ALTER SEQUENCE "public"."system_sms_channel_seq" OWNER TO "postgres";

-- ----------------------------
-- Sequence structure for system_sms_code_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."system_sms_code_seq";
CREATE SEQUENCE "public"."system_sms_code_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 1
CACHE 1;
ALTER SEQUENCE "public"."system_sms_code_seq" OWNER TO "postgres";

-- ----------------------------
-- Sequence structure for system_sms_log_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."system_sms_log_seq";
CREATE SEQUENCE "public"."system_sms_log_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 1
CACHE 1;
ALTER SEQUENCE "public"."system_sms_log_seq" OWNER TO "postgres";

-- ----------------------------
-- Sequence structure for system_sms_template_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."system_sms_template_seq";
CREATE SEQUENCE "public"."system_sms_template_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 17
CACHE 1;
ALTER SEQUENCE "public"."system_sms_template_seq" OWNER TO "postgres";

-- ----------------------------
-- Sequence structure for system_social_client_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."system_social_client_seq";
CREATE SEQUENCE "public"."system_social_client_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 44
CACHE 1;
ALTER SEQUENCE "public"."system_social_client_seq" OWNER TO "postgres";

-- ----------------------------
-- Sequence structure for system_social_user_bind_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."system_social_user_bind_seq";
CREATE SEQUENCE "public"."system_social_user_bind_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 1
CACHE 1;
ALTER SEQUENCE "public"."system_social_user_bind_seq" OWNER TO "postgres";

-- ----------------------------
-- Sequence structure for system_social_user_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."system_social_user_seq";
CREATE SEQUENCE "public"."system_social_user_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 1
CACHE 1;
ALTER SEQUENCE "public"."system_social_user_seq" OWNER TO "postgres";

-- ----------------------------
-- Sequence structure for system_tenant_package_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."system_tenant_package_seq";
CREATE SEQUENCE "public"."system_tenant_package_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 112
CACHE 1;
ALTER SEQUENCE "public"."system_tenant_package_seq" OWNER TO "postgres";

-- ----------------------------
-- Sequence structure for system_tenant_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."system_tenant_seq";
CREATE SEQUENCE "public"."system_tenant_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 123
CACHE 1;
ALTER SEQUENCE "public"."system_tenant_seq" OWNER TO "postgres";

-- ----------------------------
-- Sequence structure for system_user_post_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."system_user_post_seq";
CREATE SEQUENCE "public"."system_user_post_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 125
CACHE 1;
ALTER SEQUENCE "public"."system_user_post_seq" OWNER TO "postgres";

-- ----------------------------
-- Sequence structure for system_user_role_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."system_user_role_seq";
CREATE SEQUENCE "public"."system_user_role_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 39
CACHE 1;
ALTER SEQUENCE "public"."system_user_role_seq" OWNER TO "postgres";

-- ----------------------------
-- Sequence structure for system_users_id_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."system_users_id_seq";
CREATE SEQUENCE "public"."system_users_id_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 1
CACHE 1;
ALTER SEQUENCE "public"."system_users_id_seq" OWNER TO "postgres";

-- ----------------------------
-- Sequence structure for system_users_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."system_users_seq";
CREATE SEQUENCE "public"."system_users_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 132
CACHE 1;
ALTER SEQUENCE "public"."system_users_seq" OWNER TO "postgres";

-- ----------------------------
-- Sequence structure for onebase_demo01_contact_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."onebase_demo01_contact_seq";
CREATE SEQUENCE "public"."onebase_demo01_contact_seq"
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 2
CACHE 1;
ALTER SEQUENCE "public"."onebase_demo01_contact_seq" OWNER TO "postgres";

-- ----------------------------
-- Sequence structure for onebase_demo02_category_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."onebase_demo02_category_seq";
CREATE SEQUENCE "public"."onebase_demo02_category_seq"
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 7
CACHE 1;
ALTER SEQUENCE "public"."onebase_demo02_category_seq" OWNER TO "postgres";

-- ----------------------------
-- Sequence structure for onebase_demo03_course_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."onebase_demo03_course_seq";
CREATE SEQUENCE "public"."onebase_demo03_course_seq"
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 14
CACHE 1;
ALTER SEQUENCE "public"."onebase_demo03_course_seq" OWNER TO "postgres";

-- ----------------------------
-- Sequence structure for onebase_demo03_grade_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."onebase_demo03_grade_seq";
CREATE SEQUENCE "public"."onebase_demo03_grade_seq"
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 10
CACHE 1;
ALTER SEQUENCE "public"."onebase_demo03_grade_seq" OWNER TO "postgres";

-- ----------------------------
-- Sequence structure for onebase_demo03_student_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."onebase_demo03_student_seq";
CREATE SEQUENCE "public"."onebase_demo03_student_seq"
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 10
CACHE 1;
ALTER SEQUENCE "public"."onebase_demo03_student_seq" OWNER TO "postgres";

-- ----------------------------
-- Table structure for app_application
-- ----------------------------
DROP TABLE IF EXISTS "public"."app_application";
CREATE TABLE "public"."app_application" (
  "id" int8 NOT NULL,
  "app_name" varchar(128) COLLATE "pg_catalog"."default" NOT NULL,
  "app_code" varchar(256) COLLATE "pg_catalog"."default" NOT NULL,
  "app_mode" varchar(64) COLLATE "pg_catalog"."default" NOT NULL,
  "theme_color" varchar(64) COLLATE "pg_catalog"."default" NOT NULL,
  "icon_name" varchar(256) COLLATE "pg_catalog"."default" DEFAULT NULL::character varying,
  "icon_color" varchar(32) COLLATE "pg_catalog"."default" DEFAULT NULL::character varying,
  "version_number" varchar(64) COLLATE "pg_catalog"."default" NOT NULL,
  "datasource_id" int8 NOT NULL DEFAULT 0,
  "status" int2 NOT NULL,
  "description" varchar(1024) COLLATE "pg_catalog"."default" DEFAULT NULL::character varying,
  "lock_version" int8 NOT NULL DEFAULT 0,
  "creator" int8 NOT NULL DEFAULT 0,
  "create_time" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "updater" int8 NOT NULL DEFAULT 0,
  "update_time" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "deleted" int8 NOT NULL DEFAULT 0,
  "tenant_id" int8 NOT NULL DEFAULT 0
)
;
ALTER TABLE "public"."app_application" OWNER TO "postgres";
COMMENT ON COLUMN "public"."app_application"."id" IS '主键ID';
COMMENT ON COLUMN "public"."app_application"."app_name" IS '应用名称';
COMMENT ON COLUMN "public"."app_application"."app_code" IS '应用编码';
COMMENT ON COLUMN "public"."app_application"."app_mode" IS '应用模式';
COMMENT ON COLUMN "public"."app_application"."theme_color" IS '主题颜色';
COMMENT ON COLUMN "public"."app_application"."icon_name" IS '应用图标';
COMMENT ON COLUMN "public"."app_application"."icon_color" IS '图标颜色';
COMMENT ON COLUMN "public"."app_application"."version_number" IS '当前版本';
COMMENT ON COLUMN "public"."app_application"."datasource_id" IS '数据源ID';
COMMENT ON COLUMN "public"."app_application"."status" IS '状态（编辑、发布）';
COMMENT ON COLUMN "public"."app_application"."description" IS '描述';
COMMENT ON TABLE "public"."app_application" IS '应用主表';

-- ----------------------------
-- Table structure for app_application_tag
-- ----------------------------
DROP TABLE IF EXISTS "public"."app_application_tag";
CREATE TABLE "public"."app_application_tag" (
  "id" int8 NOT NULL,
  "application_id" int8 NOT NULL,
  "tag_id" int8 NOT NULL,
  "lock_version" int8 NOT NULL DEFAULT 0,
  "creator" int8 NOT NULL DEFAULT 0,
  "create_time" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "updater" int8 NOT NULL DEFAULT 0,
  "update_time" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "deleted" int8 NOT NULL DEFAULT 0,
  "tenant_id" int8 NOT NULL DEFAULT 0
)
;
ALTER TABLE "public"."app_application_tag" OWNER TO "postgres";
COMMENT ON COLUMN "public"."app_application_tag"."id" IS '主键Id';
COMMENT ON COLUMN "public"."app_application_tag"."application_id" IS '应用Id';
COMMENT ON COLUMN "public"."app_application_tag"."tag_id" IS '标签Id';
COMMENT ON TABLE "public"."app_application_tag" IS '应用标签管理表';

-- ----------------------------
-- Table structure for app_auth_data_filter
-- ----------------------------
DROP TABLE IF EXISTS "public"."app_auth_data_filter";
CREATE TABLE "public"."app_auth_data_filter" (
  "id" int8 NOT NULL,
  "application_id" int8 NOT NULL DEFAULT 0,
  "group_id" int8 NOT NULL DEFAULT 0,
  "field_name" varchar(100) COLLATE "pg_catalog"."default" NOT NULL,
  "field_operator" varchar(20) COLLATE "pg_catalog"."default" NOT NULL,
  "field_value" varchar(100) COLLATE "pg_catalog"."default" NOT NULL,
  "lock_version" int8 NOT NULL DEFAULT 0,
  "creator" int8 NOT NULL DEFAULT 0,
  "create_time" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "updater" int8 NOT NULL DEFAULT 0,
  "update_time" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "is_deleted" int8 NOT NULL DEFAULT 0,
  "tenant_id" int8 NOT NULL DEFAULT 0
)
;
ALTER TABLE "public"."app_auth_data_filter" OWNER TO "postgres";
COMMENT ON COLUMN "public"."app_auth_data_filter"."id" IS '主键Id';
COMMENT ON COLUMN "public"."app_auth_data_filter"."application_id" IS '应用Id';
COMMENT ON COLUMN "public"."app_auth_data_filter"."group_id" IS '数据权限组Id';
COMMENT ON COLUMN "public"."app_auth_data_filter"."field_name" IS '字段名称';
COMMENT ON COLUMN "public"."app_auth_data_filter"."field_operator" IS '比较操作符号';
COMMENT ON COLUMN "public"."app_auth_data_filter"."field_value" IS '字段值';
COMMENT ON TABLE "public"."app_auth_data_filter" IS '数据权限配置-数据过滤条件';

-- ----------------------------
-- Table structure for app_auth_data_group
-- ----------------------------
DROP TABLE IF EXISTS "public"."app_auth_data_group";
CREATE TABLE "public"."app_auth_data_group" (
  "id" int8 NOT NULL,
  "application_id" int8 NOT NULL,
  "group_name" varchar(100) COLLATE "pg_catalog"."default" NOT NULL,
  "description" varchar(256) COLLATE "pg_catalog"."default" DEFAULT NULL::character varying,
  "entity_id" int8 NOT NULL DEFAULT 0,
  "scope_field_id" int8 NOT NULL,
  "scope_level" varchar(32) COLLATE "pg_catalog"."default" NOT NULL,
  "is_can_operate" int2 NOT NULL DEFAULT 0,
  "lock_version" int8 NOT NULL DEFAULT 0,
  "creator" int8 NOT NULL DEFAULT 0,
  "create_time" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "updater" int8 NOT NULL DEFAULT 0,
  "update_time" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "is_deleted" int8 NOT NULL DEFAULT 0,
  "tenant_id" int8 NOT NULL DEFAULT 0
)
;
ALTER TABLE "public"."app_auth_data_group" OWNER TO "postgres";
COMMENT ON COLUMN "public"."app_auth_data_group"."id" IS '主键Id';
COMMENT ON COLUMN "public"."app_auth_data_group"."application_id" IS '应用Id';
COMMENT ON COLUMN "public"."app_auth_data_group"."group_name" IS '组名称';
COMMENT ON COLUMN "public"."app_auth_data_group"."description" IS '描述';
COMMENT ON COLUMN "public"."app_auth_data_group"."entity_id" IS '关联业务实体Id';
COMMENT ON COLUMN "public"."app_auth_data_group"."is_can_operate" IS '是否可以操作';

-- ----------------------------
-- Table structure for app_auth_entity
-- ----------------------------
DROP TABLE IF EXISTS "public"."app_auth_entity";
CREATE TABLE "public"."app_auth_entity" (
  "id" int8 NOT NULL,
  "application_id" int8 NOT NULL DEFAULT 0,
  "menu_id" int8 NOT NULL DEFAULT 0,
  "entity_id" int8 NOT NULL DEFAULT 0,
  "is_allowed" int2 NOT NULL DEFAULT 0,
  "lock_version" int8 NOT NULL DEFAULT 0,
  "creator" int8 NOT NULL DEFAULT 0,
  "create_time" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "updater" int8 NOT NULL DEFAULT 0,
  "update_time" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "is_deleted" int8 NOT NULL DEFAULT 0,
  "tenant_id" int8 NOT NULL DEFAULT 0
)
;
ALTER TABLE "public"."app_auth_entity" OWNER TO "postgres";
COMMENT ON COLUMN "public"."app_auth_entity"."id" IS '主键Id';
COMMENT ON COLUMN "public"."app_auth_entity"."application_id" IS '应用Id';
COMMENT ON COLUMN "public"."app_auth_entity"."menu_id" IS '菜单Id';
COMMENT ON COLUMN "public"."app_auth_entity"."entity_id" IS '实体Id';
COMMENT ON COLUMN "public"."app_auth_entity"."is_allowed" IS '是否可访问';
COMMENT ON TABLE "public"."app_auth_entity" IS '应用功能权限-视图权限';

-- ----------------------------
-- Table structure for app_auth_feature
-- ----------------------------
DROP TABLE IF EXISTS "public"."app_auth_feature";
CREATE TABLE "public"."app_auth_feature" (
  "id" int8 NOT NULL,
  "application_id" int8 NOT NULL DEFAULT 0,
  "menu_id" int8 NOT NULL DEFAULT 0,
  "is_page_access" int2 NOT NULL DEFAULT 0,
  "is_all_entity_access" int2 NOT NULL DEFAULT 0,
  "lock_version" int8 NOT NULL DEFAULT 0,
  "creator" int8 NOT NULL DEFAULT 0,
  "create_time" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "updater" int8 NOT NULL DEFAULT 0,
  "update_time" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "is_deleted" int8 NOT NULL DEFAULT 0,
  "tenant_id" int8 NOT NULL DEFAULT 0
)
;
ALTER TABLE "public"."app_auth_feature" OWNER TO "postgres";
COMMENT ON COLUMN "public"."app_auth_feature"."id" IS '主键Id';
COMMENT ON COLUMN "public"."app_auth_feature"."application_id" IS '应用Id';
COMMENT ON COLUMN "public"."app_auth_feature"."menu_id" IS '菜单Id';
COMMENT ON COLUMN "public"."app_auth_feature"."is_page_access" IS '页面是否可访问';
COMMENT ON COLUMN "public"."app_auth_feature"."is_all_entity_access" IS '关联的所有视图是否可访问';
COMMENT ON TABLE "public"."app_auth_feature" IS '应用功能权限';

-- ----------------------------
-- Table structure for app_auth_field
-- ----------------------------
DROP TABLE IF EXISTS "public"."app_auth_field";
CREATE TABLE "public"."app_auth_field" (
  "id" int8 NOT NULL,
  "application_id" int8 NOT NULL DEFAULT 0,
  "menu_id" int8 NOT NULL DEFAULT 0,
  "field_name" varchar(100) COLLATE "pg_catalog"."default" NOT NULL,
  "is_can_read" int2 DEFAULT 0,
  "is_can_edit" int2 DEFAULT 0,
  "is_can_download" int2 DEFAULT 0,
  "lock_version" int8 NOT NULL DEFAULT 0,
  "creator" int8 NOT NULL DEFAULT 0,
  "create_time" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "updater" int8 NOT NULL DEFAULT 0,
  "update_time" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "is_deleted" int8 NOT NULL DEFAULT 0,
  "tenant_id" int8 NOT NULL DEFAULT 0
)
;
ALTER TABLE "public"."app_auth_field" OWNER TO "postgres";
COMMENT ON COLUMN "public"."app_auth_field"."id" IS '主键Id';
COMMENT ON COLUMN "public"."app_auth_field"."application_id" IS '应用Id';
COMMENT ON COLUMN "public"."app_auth_field"."menu_id" IS '菜单Id';
COMMENT ON COLUMN "public"."app_auth_field"."field_name" IS '字段名称';
COMMENT ON COLUMN "public"."app_auth_field"."is_can_read" IS '是否可阅读';
COMMENT ON COLUMN "public"."app_auth_field"."is_can_edit" IS '是否可编辑';
COMMENT ON COLUMN "public"."app_auth_field"."is_can_download" IS '是否可下载';
COMMENT ON TABLE "public"."app_auth_field" IS '应用字段权限';

-- ----------------------------
-- Table structure for app_auth_operation
-- ----------------------------
DROP TABLE IF EXISTS "public"."app_auth_operation";
CREATE TABLE "public"."app_auth_operation" (
  "id" int8 NOT NULL,
  "application_id" int8 NOT NULL DEFAULT 0,
  "menu_id" int8 NOT NULL DEFAULT 0,
  "operation_type" varchar(64) COLLATE "pg_catalog"."default" NOT NULL,
  "is_allowed" int2 NOT NULL DEFAULT 0,
  "lock_version" int8 NOT NULL DEFAULT 0,
  "creator" int8 NOT NULL DEFAULT 0,
  "create_time" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "updater" int8 NOT NULL DEFAULT 0,
  "update_time" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "is_deleted" int8 NOT NULL DEFAULT 0,
  "tenant_id" int8 NOT NULL DEFAULT 0
)
;
ALTER TABLE "public"."app_auth_operation" OWNER TO "postgres";
COMMENT ON COLUMN "public"."app_auth_operation"."id" IS '主键Id';
COMMENT ON COLUMN "public"."app_auth_operation"."application_id" IS '应用Id';
COMMENT ON COLUMN "public"."app_auth_operation"."menu_id" IS '菜单Id';
COMMENT ON COLUMN "public"."app_auth_operation"."operation_type" IS '操作名称';
COMMENT ON COLUMN "public"."app_auth_operation"."is_allowed" IS '是否允许';
COMMENT ON TABLE "public"."app_auth_operation" IS '应该操作权限';

-- ----------------------------
-- Table structure for app_auth_role
-- ----------------------------
DROP TABLE IF EXISTS "public"."app_auth_role";
CREATE TABLE "public"."app_auth_role" (
  "id" int8 NOT NULL,
  "application_id" int8 NOT NULL DEFAULT 0,
  "role_code" varchar(64) COLLATE "pg_catalog"."default" NOT NULL DEFAULT NULL::character varying,
  "role_name" varchar(64) COLLATE "pg_catalog"."default" NOT NULL DEFAULT NULL::character varying,
  "description" varchar(256) COLLATE "pg_catalog"."default" DEFAULT NULL::character varying,
  "lock_version" int8 NOT NULL DEFAULT 0,
  "creator" int8 NOT NULL DEFAULT 0,
  "create_time" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "updater" int8 NOT NULL DEFAULT 0,
  "update_time" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "is_deleted" int8 NOT NULL DEFAULT 0,
  "tenant_id" int8 NOT NULL DEFAULT 0
)
;
ALTER TABLE "public"."app_auth_role" OWNER TO "postgres";
COMMENT ON COLUMN "public"."app_auth_role"."id" IS '主键Id';
COMMENT ON COLUMN "public"."app_auth_role"."application_id" IS '应用Id';
COMMENT ON COLUMN "public"."app_auth_role"."role_code" IS '角色编码';
COMMENT ON COLUMN "public"."app_auth_role"."role_name" IS '角色名称';
COMMENT ON COLUMN "public"."app_auth_role"."description" IS '描述';
COMMENT ON TABLE "public"."app_auth_role" IS '应用角色';

-- ----------------------------
-- Table structure for app_auth_user_role
-- ----------------------------
DROP TABLE IF EXISTS "public"."app_auth_user_role";
CREATE TABLE "public"."app_auth_user_role" (
  "id" int8 NOT NULL,
  "application_id" int8 NOT NULL DEFAULT 0,
  "user_id" int8 NOT NULL DEFAULT 0,
  "role_id" int8 NOT NULL DEFAULT 0,
  "lock_version" int8 NOT NULL DEFAULT 0,
  "creator" int8 NOT NULL DEFAULT 0,
  "create_time" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "updater" int8 NOT NULL DEFAULT 0,
  "update_time" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "is_deleted" int8 NOT NULL DEFAULT 0,
  "tenant_id" int8 NOT NULL DEFAULT 0
)
;
ALTER TABLE "public"."app_auth_user_role" OWNER TO "postgres";
COMMENT ON COLUMN "public"."app_auth_user_role"."id" IS '主键Id';
COMMENT ON COLUMN "public"."app_auth_user_role"."application_id" IS '应用Id';
COMMENT ON COLUMN "public"."app_auth_user_role"."user_id" IS '用户Id';
COMMENT ON COLUMN "public"."app_auth_user_role"."role_id" IS '角色Id';
COMMENT ON TABLE "public"."app_auth_user_role" IS '应用角色关联表';

-- ----------------------------
-- Table structure for app_menu
-- ----------------------------
DROP TABLE IF EXISTS "public"."app_menu";
CREATE TABLE "public"."app_menu" (
  "id" int8 NOT NULL,
  "application_id" int8 NOT NULL,
  "parent_uuid" varchar(64) COLLATE "pg_catalog"."default" NOT NULL,
  "menu_uuid" varchar(64) COLLATE "pg_catalog"."default" NOT NULL,
  "menu_sort" int4 NOT NULL DEFAULT 0,
  "menu_type" int2 NOT NULL,
  "menu_name" varchar(64) COLLATE "pg_catalog"."default" NOT NULL,
  "menu_icon" varchar(64) COLLATE "pg_catalog"."default" NOT NULL,
  "action_target" varchar(256) COLLATE "pg_catalog"."default",
  "is_visible" int2 NOT NULL DEFAULT 1,
  "lock_version" int8 NOT NULL DEFAULT 0,
  "creator" int8 NOT NULL DEFAULT 0,
  "create_time" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "updater" int8 NOT NULL DEFAULT 0,
  "update_time" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "deleted" int8 NOT NULL DEFAULT 0,
  "tenant_id" int8 NOT NULL DEFAULT 0
)
;
ALTER TABLE "public"."app_menu" OWNER TO "postgres";
COMMENT ON COLUMN "public"."app_menu"."id" IS '主键Id';
COMMENT ON COLUMN "public"."app_menu"."application_id" IS '应用Id';
COMMENT ON COLUMN "public"."app_menu"."parent_uuid" IS '父节点Id';
COMMENT ON COLUMN "public"."app_menu"."menu_uuid" IS '菜单uuid';
COMMENT ON COLUMN "public"."app_menu"."menu_sort" IS '菜单排序';
COMMENT ON COLUMN "public"."app_menu"."menu_type" IS '菜单类型';
COMMENT ON COLUMN "public"."app_menu"."menu_name" IS '菜单名称';
COMMENT ON COLUMN "public"."app_menu"."menu_icon" IS '菜单图标';
COMMENT ON COLUMN "public"."app_menu"."action_target" IS '菜单动作';
COMMENT ON COLUMN "public"."app_menu"."is_visible" IS '是否可见';

-- ----------------------------
-- Table structure for app_resource
-- ----------------------------
DROP TABLE IF EXISTS "public"."app_resource";
CREATE TABLE "public"."app_resource" (
  "id" int8 NOT NULL,
  "application_id" int8 NOT NULL,
  "protocol_type" varchar(64) COLLATE "pg_catalog"."default" NOT NULL,
  "res_key" varchar(64) COLLATE "pg_catalog"."default" NOT NULL,
  "res_data" text COLLATE "pg_catalog"."default" NOT NULL,
  "lock_version" int8 NOT NULL DEFAULT 0,
  "creator" int8 NOT NULL DEFAULT 0,
  "create_time" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "updater" int8 NOT NULL DEFAULT 0,
  "update_time" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "deleted" int8 NOT NULL DEFAULT 0,
  "tenant_id" int8 NOT NULL DEFAULT 0
)
;
ALTER TABLE "public"."app_resource" OWNER TO "postgres";
COMMENT ON COLUMN "public"."app_resource"."id" IS '主键ID';
COMMENT ON COLUMN "public"."app_resource"."application_id" IS '应用ID';
COMMENT ON COLUMN "public"."app_resource"."protocol_type" IS '协议类型';
COMMENT ON COLUMN "public"."app_resource"."res_key" IS '资源key';
COMMENT ON COLUMN "public"."app_resource"."res_data" IS '资源数据';

-- ----------------------------
-- Table structure for app_resource_component
-- ----------------------------
DROP TABLE IF EXISTS "public"."app_resource_component";
CREATE TABLE "public"."app_resource_component" (
  "component_code" varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
  "page_id" int8 NOT NULL,
  "in_table" bool NOT NULL,
  "component_type" varchar(64) COLLATE "pg_catalog"."default" NOT NULL,
  "label" varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
  "width" int4 NOT NULL,
  "hidden" bool NOT NULL,
  "read_only" varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
  "required" bool NOT NULL,
  "config" text COLLATE "pg_catalog"."default" NOT NULL,
  "edit_data" text COLLATE "pg_catalog"."default" NOT NULL,
  "tenant_id" int8,
  "id" int8,
  "create_time" timestamp(6),
  "update_time" timestamp(6),
  "creator" int8,
  "updater" int8,
  "deleted" bool,
  "lock_version" int8
)
;
ALTER TABLE "public"."app_resource_component" OWNER TO "postgres";

-- ----------------------------
-- Table structure for app_resource_page
-- ----------------------------
DROP TABLE IF EXISTS "public"."app_resource_page";
CREATE TABLE "public"."app_resource_page" (
  "page_code" varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
  "page_name" varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
  "title" varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
  "layout" varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
  "width" varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
  "margin" varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
  "background_color" varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
  "main_metadata" varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
  "bpm_enabled" bool NOT NULL,
  "router_path" varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
  "router_name" varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
  "router_meta_auth_required" bool NOT NULL,
  "router_meta_title" varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
  "tenant_id" int8,
  "id" int8,
  "create_time" timestamp(6),
  "update_time" timestamp(6),
  "creator" int8,
  "updater" int8,
  "deleted" bool,
  "lock_version" int8
)
;
ALTER TABLE "public"."app_resource_page" OWNER TO "postgres";

-- ----------------------------
-- Table structure for app_resource_page_metadata
-- ----------------------------
DROP TABLE IF EXISTS "public"."app_resource_page_metadata";
CREATE TABLE "public"."app_resource_page_metadata" (
  "page_id" int8 NOT NULL,
  "metadata" varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
  "tenant_id" int8,
  "id" int8,
  "create_time" timestamp(6),
  "update_time" timestamp(6),
  "creator" int8,
  "updater" int8,
  "deleted" bool,
  "lock_version" int8
)
;
ALTER TABLE "public"."app_resource_page_metadata" OWNER TO "postgres";

-- ----------------------------
-- Table structure for app_resource_page_ref_router
-- ----------------------------
DROP TABLE IF EXISTS "public"."app_resource_page_ref_router";
CREATE TABLE "public"."app_resource_page_ref_router" (
  "page_ref" int8 NOT NULL,
  "router_name" varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
  "router_type" varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
  "tenant_id" int8,
  "id" int8,
  "create_time" timestamp(6),
  "update_time" timestamp(6),
  "creator" int8,
  "updater" int8,
  "deleted" bool,
  "lock_version" int8
)
;
ALTER TABLE "public"."app_resource_page_ref_router" OWNER TO "postgres";

-- ----------------------------
-- Table structure for app_resource_pageset
-- ----------------------------
DROP TABLE IF EXISTS "public"."app_resource_pageset";
CREATE TABLE "public"."app_resource_pageset" (
  "pageset_code" varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
  "menu_id" int8 NOT NULL,
  "pageset_name" varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
  "display_name" varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
  "description" text COLLATE "pg_catalog"."default",
  "tenant_id" int8,
  "id" int8,
  "create_time" timestamp(6),
  "update_time" timestamp(6),
  "creator" int8,
  "updater" int8,
  "deleted" bool,
  "lock_version" int8
)
;
ALTER TABLE "public"."app_resource_pageset" OWNER TO "postgres";

-- ----------------------------
-- Table structure for app_resource_pageset_label
-- ----------------------------
DROP TABLE IF EXISTS "public"."app_resource_pageset_label";
CREATE TABLE "public"."app_resource_pageset_label" (
  "pageset_code" varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
  "label_name" varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
  "label_value" varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
  "tenant_id" int8,
  "id" int8,
  "create_time" timestamp(6),
  "update_time" timestamp(6),
  "creator" int8,
  "updater" int8,
  "deleted" bool,
  "lock_version" int8
)
;
ALTER TABLE "public"."app_resource_pageset_label" OWNER TO "postgres";

-- ----------------------------
-- Table structure for app_resource_pageset_page
-- ----------------------------
DROP TABLE IF EXISTS "public"."app_resource_pageset_page";
CREATE TABLE "public"."app_resource_pageset_page" (
  "pageset_ref" varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
  "pageset_type" varchar(64) COLLATE "pg_catalog"."default" NOT NULL,
  "page_ref" varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
  "is_default" bool NOT NULL,
  "default_seq" int4 NOT NULL,
  "tenant_id" int8,
  "id" int8,
  "create_time" timestamp(6),
  "update_time" timestamp(6),
  "creator" int8,
  "updater" int8,
  "deleted" bool,
  "lock_version" int8
)
;
ALTER TABLE "public"."app_resource_pageset_page" OWNER TO "postgres";

-- ----------------------------
-- Table structure for app_tag
-- ----------------------------
DROP TABLE IF EXISTS "public"."app_tag";
CREATE TABLE "public"."app_tag" (
  "id" int8 NOT NULL,
  "tag_name" varchar(128) COLLATE "pg_catalog"."default" NOT NULL,
  "lock_version" int8 NOT NULL DEFAULT 0,
  "creator" int8 NOT NULL DEFAULT 0,
  "create_time" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "updater" int8 NOT NULL DEFAULT 0,
  "update_time" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "deleted" int8 NOT NULL DEFAULT 0,
  "tenant_id" int8 NOT NULL DEFAULT 0
)
;
ALTER TABLE "public"."app_tag" OWNER TO "postgres";

-- ----------------------------
-- Table structure for app_version
-- ----------------------------
DROP TABLE IF EXISTS "public"."app_version";
CREATE TABLE "public"."app_version" (
  "id" int8 NOT NULL,
  "application_id" int8 NOT NULL,
  "version_name" varchar(128) COLLATE "pg_catalog"."default" NOT NULL,
  "version_number" varchar(64) COLLATE "pg_catalog"."default" NOT NULL,
  "lock_version" int8 NOT NULL DEFAULT 0,
  "creator" int8 NOT NULL DEFAULT 0,
  "create_time" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "updater" int8 NOT NULL DEFAULT 0,
  "update_time" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "deleted" int8 NOT NULL DEFAULT 0,
  "tenant_id" int8 NOT NULL DEFAULT 0
)
;
ALTER TABLE "public"."app_version" OWNER TO "postgres";
COMMENT ON COLUMN "public"."app_version"."id" IS '主键ID';
COMMENT ON COLUMN "public"."app_version"."application_id" IS '应用ID';
COMMENT ON COLUMN "public"."app_version"."version_name" IS '版本名称';
COMMENT ON COLUMN "public"."app_version"."version_number" IS '版本编号';

-- ----------------------------
-- Table structure for app_version_menu
-- ----------------------------
DROP TABLE IF EXISTS "public"."app_version_menu";
CREATE TABLE "public"."app_version_menu" (
  "id" int8 NOT NULL,
  "application_id" int8 NOT NULL,
  "version_id" int8 NOT NULL,
  "parent_id" varchar(64) COLLATE "pg_catalog"."default" NOT NULL,
  "menu_uuid" varchar(64) COLLATE "pg_catalog"."default" NOT NULL,
  "menu_sort" int4 NOT NULL DEFAULT 0,
  "menu_type" varchar(64) COLLATE "pg_catalog"."default" NOT NULL,
  "menu_name" varchar(64) COLLATE "pg_catalog"."default" NOT NULL,
  "menu_icon" varchar(64) COLLATE "pg_catalog"."default" NOT NULL,
  "lock_version" int8 NOT NULL DEFAULT 0,
  "creator" int8 NOT NULL DEFAULT 0,
  "create_time" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "updater" int8 NOT NULL DEFAULT 0,
  "update_time" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "deleted" int8 NOT NULL DEFAULT 0,
  "tenant_id" int8 NOT NULL DEFAULT 0
)
;
ALTER TABLE "public"."app_version_menu" OWNER TO "postgres";
COMMENT ON COLUMN "public"."app_version_menu"."id" IS '主键Id';
COMMENT ON COLUMN "public"."app_version_menu"."application_id" IS '应用Id';
COMMENT ON COLUMN "public"."app_version_menu"."version_id" IS '版本Id';
COMMENT ON COLUMN "public"."app_version_menu"."parent_id" IS '菜单父节点Id';
COMMENT ON COLUMN "public"."app_version_menu"."menu_uuid" IS '菜单uuid';
COMMENT ON COLUMN "public"."app_version_menu"."menu_sort" IS '菜单排序';
COMMENT ON COLUMN "public"."app_version_menu"."menu_type" IS '菜单类型';
COMMENT ON COLUMN "public"."app_version_menu"."menu_name" IS '菜单名称';
COMMENT ON COLUMN "public"."app_version_menu"."menu_icon" IS '菜单图标';

-- ----------------------------
-- Table structure for app_version_resource
-- ----------------------------
DROP TABLE IF EXISTS "public"."app_version_resource";
CREATE TABLE "public"."app_version_resource" (
  "id" int8 NOT NULL,
  "application_id" int8 NOT NULL,
  "version_id" int8 NOT NULL,
  "protocol_type" varchar(64) COLLATE "pg_catalog"."default" NOT NULL,
  "res_key" varchar(64) COLLATE "pg_catalog"."default" NOT NULL,
  "res_data" text COLLATE "pg_catalog"."default" NOT NULL,
  "lock_version" int8 NOT NULL DEFAULT 0,
  "creator" int8 NOT NULL DEFAULT 0,
  "create_time" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "updater" int8 NOT NULL DEFAULT 0,
  "update_time" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "deleted" int8 NOT NULL DEFAULT 0,
  "tenant_id" int8 NOT NULL DEFAULT 0
)
;
ALTER TABLE "public"."app_version_resource" OWNER TO "postgres";
COMMENT ON COLUMN "public"."app_version_resource"."id" IS '主键ID';
COMMENT ON COLUMN "public"."app_version_resource"."application_id" IS '应用ID';
COMMENT ON COLUMN "public"."app_version_resource"."version_id" IS '版本ID';
COMMENT ON COLUMN "public"."app_version_resource"."protocol_type" IS '协议类型';
COMMENT ON COLUMN "public"."app_version_resource"."res_key" IS '资源key';
COMMENT ON COLUMN "public"."app_version_resource"."res_data" IS '资源数据';

-- ----------------------------
-- Table structure for dual
-- ----------------------------
DROP TABLE IF EXISTS "public"."dual";
CREATE TABLE "public"."dual" (
  "id" int2
)
;
ALTER TABLE "public"."dual" OWNER TO "postgres";
COMMENT ON TABLE "public"."dual" IS '数据库连接的表';

-- ----------------------------
-- Table structure for infra_api_access_log
-- ----------------------------
DROP TABLE IF EXISTS "public"."infra_api_access_log";
CREATE TABLE "public"."infra_api_access_log" (
  "id" int8 NOT NULL,
  "trace_id" varchar(64) COLLATE "pg_catalog"."default" NOT NULL DEFAULT ''::character varying,
  "user_id" int8 NOT NULL DEFAULT 0,
  "user_type" int2 NOT NULL DEFAULT 0,
  "application_name" varchar(50) COLLATE "pg_catalog"."default" NOT NULL,
  "request_method" varchar(16) COLLATE "pg_catalog"."default" NOT NULL DEFAULT ''::character varying,
  "request_url" varchar(255) COLLATE "pg_catalog"."default" NOT NULL DEFAULT ''::character varying,
  "request_params" text COLLATE "pg_catalog"."default",
  "response_body" text COLLATE "pg_catalog"."default",
  "user_ip" varchar(50) COLLATE "pg_catalog"."default" NOT NULL,
  "user_agent" varchar(512) COLLATE "pg_catalog"."default" NOT NULL,
  "operate_module" varchar(50) COLLATE "pg_catalog"."default" DEFAULT NULL::character varying,
  "operate_name" varchar(50) COLLATE "pg_catalog"."default" DEFAULT NULL::character varying,
  "operate_type" int2 DEFAULT 0,
  "begin_time" timestamp(6) NOT NULL,
  "end_time" timestamp(6) NOT NULL,
  "duration" int4 NOT NULL,
  "result_code" int4 NOT NULL DEFAULT 0,
  "result_msg" varchar(512) COLLATE "pg_catalog"."default" DEFAULT ''::character varying,
  "creator" int8 DEFAULT 0,
  "create_time" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "updater" int8 DEFAULT 0,
  "update_time" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "deleted" int2 NOT NULL DEFAULT 0,
  "tenant_id" int8 NOT NULL
)
;
ALTER TABLE "public"."infra_api_access_log" OWNER TO "postgres";
COMMENT ON COLUMN "public"."infra_api_access_log"."id" IS '日志主键';
COMMENT ON COLUMN "public"."infra_api_access_log"."trace_id" IS '链路追踪编号';
COMMENT ON COLUMN "public"."infra_api_access_log"."user_id" IS '用户编号';
COMMENT ON COLUMN "public"."infra_api_access_log"."user_type" IS '用户类型';
COMMENT ON COLUMN "public"."infra_api_access_log"."application_name" IS '应用名';
COMMENT ON COLUMN "public"."infra_api_access_log"."request_method" IS '请求方法名';
COMMENT ON COLUMN "public"."infra_api_access_log"."request_url" IS '请求地址';
COMMENT ON COLUMN "public"."infra_api_access_log"."request_params" IS '请求参数';
COMMENT ON COLUMN "public"."infra_api_access_log"."response_body" IS '响应结果';
COMMENT ON COLUMN "public"."infra_api_access_log"."user_ip" IS '用户 IP';
COMMENT ON COLUMN "public"."infra_api_access_log"."user_agent" IS '浏览器 UA';
COMMENT ON COLUMN "public"."infra_api_access_log"."operate_module" IS '操作模块';
COMMENT ON COLUMN "public"."infra_api_access_log"."operate_name" IS '操作名';
COMMENT ON COLUMN "public"."infra_api_access_log"."operate_type" IS '操作分类';
COMMENT ON COLUMN "public"."infra_api_access_log"."begin_time" IS '开始请求时间';
COMMENT ON COLUMN "public"."infra_api_access_log"."end_time" IS '结束请求时间';
COMMENT ON COLUMN "public"."infra_api_access_log"."duration" IS '执行时长';
COMMENT ON COLUMN "public"."infra_api_access_log"."result_code" IS '结果码';
COMMENT ON COLUMN "public"."infra_api_access_log"."result_msg" IS '结果提示';
COMMENT ON COLUMN "public"."infra_api_access_log"."creator" IS '创建者';
COMMENT ON COLUMN "public"."infra_api_access_log"."create_time" IS '创建时间';
COMMENT ON COLUMN "public"."infra_api_access_log"."updater" IS '更新者';
COMMENT ON COLUMN "public"."infra_api_access_log"."update_time" IS '更新时间';
COMMENT ON COLUMN "public"."infra_api_access_log"."deleted" IS '是否删除';
COMMENT ON COLUMN "public"."infra_api_access_log"."tenant_id" IS '租户编号';
COMMENT ON TABLE "public"."infra_api_access_log" IS 'API 访问日志表';

-- ----------------------------
-- Table structure for infra_api_error_log
-- ----------------------------
DROP TABLE IF EXISTS "public"."infra_api_error_log";
CREATE TABLE "public"."infra_api_error_log" (
  "id" int8 NOT NULL,
  "trace_id" varchar(64) COLLATE "pg_catalog"."default" NOT NULL,
  "user_id" int4 NOT NULL DEFAULT 0,
  "user_type" int2 NOT NULL DEFAULT 0,
  "application_name" varchar(50) COLLATE "pg_catalog"."default" NOT NULL,
  "request_method" varchar(16) COLLATE "pg_catalog"."default" NOT NULL,
  "request_url" varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
  "request_params" varchar(8000) COLLATE "pg_catalog"."default" NOT NULL,
  "user_ip" varchar(50) COLLATE "pg_catalog"."default" NOT NULL,
  "user_agent" varchar(512) COLLATE "pg_catalog"."default" NOT NULL,
  "exception_time" timestamp(6) NOT NULL,
  "exception_name" varchar(128) COLLATE "pg_catalog"."default" NOT NULL DEFAULT ''::character varying,
  "exception_message" text COLLATE "pg_catalog"."default" NOT NULL,
  "exception_root_cause_message" text COLLATE "pg_catalog"."default" NOT NULL,
  "exception_stack_trace" text COLLATE "pg_catalog"."default" NOT NULL,
  "exception_class_name" varchar(512) COLLATE "pg_catalog"."default" NOT NULL,
  "exception_file_name" varchar(512) COLLATE "pg_catalog"."default" NOT NULL,
  "exception_method_name" varchar(512) COLLATE "pg_catalog"."default" NOT NULL,
  "exception_line_number" int4 NOT NULL,
  "process_status" int2 NOT NULL,
  "process_time" timestamp(6),
  "process_user_id" int4 DEFAULT 0,
  "creator" int8 DEFAULT 0,
  "create_time" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "updater" int8 DEFAULT 0,
  "update_time" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "deleted" int2 NOT NULL DEFAULT 0,
  "tenant_id" int8 NOT NULL DEFAULT 0
)
;
ALTER TABLE "public"."infra_api_error_log" OWNER TO "postgres";
COMMENT ON COLUMN "public"."infra_api_error_log"."id" IS '编号';
COMMENT ON COLUMN "public"."infra_api_error_log"."trace_id" IS '链路追踪编号
     *
     * 一般来说，通过链路追踪编号，可以将访问日志，错误日志，链路追踪日志，logger 打印日志等，结合在一起，从而进行排错。';
COMMENT ON COLUMN "public"."infra_api_error_log"."user_id" IS '用户编号';
COMMENT ON COLUMN "public"."infra_api_error_log"."user_type" IS '用户类型';
COMMENT ON COLUMN "public"."infra_api_error_log"."application_name" IS '应用名
     *
     * 目前读取 spring.application.name';
COMMENT ON COLUMN "public"."infra_api_error_log"."request_method" IS '请求方法名';
COMMENT ON COLUMN "public"."infra_api_error_log"."request_url" IS '请求地址';
COMMENT ON COLUMN "public"."infra_api_error_log"."request_params" IS '请求参数';
COMMENT ON COLUMN "public"."infra_api_error_log"."user_ip" IS '用户 IP';
COMMENT ON COLUMN "public"."infra_api_error_log"."user_agent" IS '浏览器 UA';
COMMENT ON COLUMN "public"."infra_api_error_log"."exception_time" IS '异常发生时间';
COMMENT ON COLUMN "public"."infra_api_error_log"."exception_name" IS '异常名
     *
     * {@link Throwable#getClass()} 的类全名';
COMMENT ON COLUMN "public"."infra_api_error_log"."exception_message" IS '异常导致的消息
     *
     * {@link cn.iocoder.common.framework.util.ExceptionUtil#getMessage(Throwable)}';
COMMENT ON COLUMN "public"."infra_api_error_log"."exception_root_cause_message" IS '异常导致的根消息
     *
     * {@link cn.iocoder.common.framework.util.ExceptionUtil#getRootCauseMessage(Throwable)}';
COMMENT ON COLUMN "public"."infra_api_error_log"."exception_stack_trace" IS '异常的栈轨迹
     *
     * {@link cn.iocoder.common.framework.util.ExceptionUtil#getServiceException(Exception)}';
COMMENT ON COLUMN "public"."infra_api_error_log"."exception_class_name" IS '异常发生的类全名
     *
     * {@link StackTraceElement#getClassName()}';
COMMENT ON COLUMN "public"."infra_api_error_log"."exception_file_name" IS '异常发生的类文件
     *
     * {@link StackTraceElement#getFileName()}';
COMMENT ON COLUMN "public"."infra_api_error_log"."exception_method_name" IS '异常发生的方法名
     *
     * {@link StackTraceElement#getMethodName()}';
COMMENT ON COLUMN "public"."infra_api_error_log"."exception_line_number" IS '异常发生的方法所在行
     *
     * {@link StackTraceElement#getLineNumber()}';
COMMENT ON COLUMN "public"."infra_api_error_log"."process_status" IS '处理状态';
COMMENT ON COLUMN "public"."infra_api_error_log"."process_time" IS '处理时间';
COMMENT ON COLUMN "public"."infra_api_error_log"."process_user_id" IS '处理用户编号';
COMMENT ON COLUMN "public"."infra_api_error_log"."creator" IS '创建者';
COMMENT ON COLUMN "public"."infra_api_error_log"."create_time" IS '创建时间';
COMMENT ON COLUMN "public"."infra_api_error_log"."updater" IS '更新者';
COMMENT ON COLUMN "public"."infra_api_error_log"."update_time" IS '更新时间';
COMMENT ON COLUMN "public"."infra_api_error_log"."deleted" IS '是否删除';
COMMENT ON COLUMN "public"."infra_api_error_log"."tenant_id" IS '租户编号';
COMMENT ON TABLE "public"."infra_api_error_log" IS '系统异常日志';

-- ----------------------------
-- Table structure for infra_config
-- ----------------------------
DROP TABLE IF EXISTS "public"."infra_config";
CREATE TABLE "public"."infra_config" (
  "id" int4 NOT NULL,
  "category" varchar(50) COLLATE "pg_catalog"."default" NOT NULL,
  "type" int2 NOT NULL,
  "name" varchar(100) COLLATE "pg_catalog"."default" NOT NULL DEFAULT ''::character varying,
  "config_key" varchar(100) COLLATE "pg_catalog"."default" NOT NULL DEFAULT ''::character varying,
  "value" varchar(500) COLLATE "pg_catalog"."default" NOT NULL DEFAULT ''::character varying,
  "visible" bool NOT NULL,
  "remark" varchar(500) COLLATE "pg_catalog"."default" DEFAULT NULL::character varying,
  "creator" int8 DEFAULT 0,
  "create_time" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "updater" int8 DEFAULT 0,
  "update_time" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "deleted" int2 NOT NULL DEFAULT 0
)
;
ALTER TABLE "public"."infra_config" OWNER TO "postgres";
COMMENT ON COLUMN "public"."infra_config"."id" IS '参数主键';
COMMENT ON COLUMN "public"."infra_config"."category" IS '参数分组';
COMMENT ON COLUMN "public"."infra_config"."type" IS '参数类型';
COMMENT ON COLUMN "public"."infra_config"."name" IS '参数名称';
COMMENT ON COLUMN "public"."infra_config"."config_key" IS '参数键名';
COMMENT ON COLUMN "public"."infra_config"."value" IS '参数键值';
COMMENT ON COLUMN "public"."infra_config"."visible" IS '是否可见';
COMMENT ON COLUMN "public"."infra_config"."remark" IS '备注';
COMMENT ON COLUMN "public"."infra_config"."creator" IS '创建者';
COMMENT ON COLUMN "public"."infra_config"."create_time" IS '创建时间';
COMMENT ON COLUMN "public"."infra_config"."updater" IS '更新者';
COMMENT ON COLUMN "public"."infra_config"."update_time" IS '更新时间';
COMMENT ON COLUMN "public"."infra_config"."deleted" IS '是否删除';
COMMENT ON TABLE "public"."infra_config" IS '参数配置表';

-- ----------------------------
-- Table structure for infra_data_source_config
-- ----------------------------
DROP TABLE IF EXISTS "public"."infra_data_source_config";
CREATE TABLE "public"."infra_data_source_config" (
  "id" int8 NOT NULL,
  "name" varchar(100) COLLATE "pg_catalog"."default" NOT NULL DEFAULT ''::character varying,
  "url" varchar(1024) COLLATE "pg_catalog"."default" NOT NULL,
  "username" varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
  "password" varchar(255) COLLATE "pg_catalog"."default" NOT NULL DEFAULT ''::character varying,
  "creator" int8 DEFAULT 0,
  "create_time" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "updater" int8 DEFAULT 0,
  "update_time" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "deleted" int2 NOT NULL DEFAULT 0
)
;
ALTER TABLE "public"."infra_data_source_config" OWNER TO "postgres";
COMMENT ON COLUMN "public"."infra_data_source_config"."id" IS '主键编号';
COMMENT ON COLUMN "public"."infra_data_source_config"."name" IS '参数名称';
COMMENT ON COLUMN "public"."infra_data_source_config"."url" IS '数据源连接';
COMMENT ON COLUMN "public"."infra_data_source_config"."username" IS '用户名';
COMMENT ON COLUMN "public"."infra_data_source_config"."password" IS '密码';
COMMENT ON COLUMN "public"."infra_data_source_config"."creator" IS '创建者';
COMMENT ON COLUMN "public"."infra_data_source_config"."create_time" IS '创建时间';
COMMENT ON COLUMN "public"."infra_data_source_config"."updater" IS '更新者';
COMMENT ON COLUMN "public"."infra_data_source_config"."update_time" IS '更新时间';
COMMENT ON COLUMN "public"."infra_data_source_config"."deleted" IS '是否删除';
COMMENT ON TABLE "public"."infra_data_source_config" IS '数据源配置表';

-- ----------------------------
-- Table structure for infra_file
-- ----------------------------
DROP TABLE IF EXISTS "public"."infra_file";
CREATE TABLE "public"."infra_file" (
  "id" int8 NOT NULL,
  "config_id" int8,
  "name" varchar(256) COLLATE "pg_catalog"."default" DEFAULT NULL::character varying,
  "path" varchar(512) COLLATE "pg_catalog"."default" NOT NULL,
  "url" varchar(1024) COLLATE "pg_catalog"."default" NOT NULL,
  "type" varchar(128) COLLATE "pg_catalog"."default" DEFAULT NULL::character varying,
  "size" int4 NOT NULL,
  "creator" int8 DEFAULT 0,
  "create_time" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "updater" int8 DEFAULT 0,
  "update_time" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "deleted" int2 NOT NULL DEFAULT 0
)
;
ALTER TABLE "public"."infra_file" OWNER TO "postgres";
COMMENT ON COLUMN "public"."infra_file"."id" IS '文件编号';
COMMENT ON COLUMN "public"."infra_file"."config_id" IS '配置编号';
COMMENT ON COLUMN "public"."infra_file"."name" IS '文件名';
COMMENT ON COLUMN "public"."infra_file"."path" IS '文件路径';
COMMENT ON COLUMN "public"."infra_file"."url" IS '文件 URL';
COMMENT ON COLUMN "public"."infra_file"."type" IS '文件类型';
COMMENT ON COLUMN "public"."infra_file"."size" IS '文件大小';
COMMENT ON COLUMN "public"."infra_file"."creator" IS '创建者';
COMMENT ON COLUMN "public"."infra_file"."create_time" IS '创建时间';
COMMENT ON COLUMN "public"."infra_file"."updater" IS '更新者';
COMMENT ON COLUMN "public"."infra_file"."update_time" IS '更新时间';
COMMENT ON COLUMN "public"."infra_file"."deleted" IS '是否删除';
COMMENT ON TABLE "public"."infra_file" IS '文件表';

-- ----------------------------
-- Table structure for infra_file_config
-- ----------------------------
DROP TABLE IF EXISTS "public"."infra_file_config";
CREATE TABLE "public"."infra_file_config" (
  "id" int8 NOT NULL,
  "name" varchar(63) COLLATE "pg_catalog"."default" NOT NULL,
  "storage" int2 NOT NULL,
  "remark" varchar(255) COLLATE "pg_catalog"."default" DEFAULT NULL::character varying,
  "master" bool NOT NULL,
  "config" varchar(4096) COLLATE "pg_catalog"."default" NOT NULL,
  "creator" int8 DEFAULT 0,
  "create_time" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "updater" int8 DEFAULT 0,
  "update_time" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "deleted" int2 NOT NULL DEFAULT 0
)
;
ALTER TABLE "public"."infra_file_config" OWNER TO "postgres";
COMMENT ON COLUMN "public"."infra_file_config"."id" IS '编号';
COMMENT ON COLUMN "public"."infra_file_config"."name" IS '配置名';
COMMENT ON COLUMN "public"."infra_file_config"."storage" IS '存储器';
COMMENT ON COLUMN "public"."infra_file_config"."remark" IS '备注';
COMMENT ON COLUMN "public"."infra_file_config"."master" IS '是否为主配置';
COMMENT ON COLUMN "public"."infra_file_config"."config" IS '存储配置';
COMMENT ON COLUMN "public"."infra_file_config"."creator" IS '创建者';
COMMENT ON COLUMN "public"."infra_file_config"."create_time" IS '创建时间';
COMMENT ON COLUMN "public"."infra_file_config"."updater" IS '更新者';
COMMENT ON COLUMN "public"."infra_file_config"."update_time" IS '更新时间';
COMMENT ON COLUMN "public"."infra_file_config"."deleted" IS '是否删除';
COMMENT ON TABLE "public"."infra_file_config" IS '文件配置表';

-- ----------------------------
-- Table structure for infra_file_content
-- ----------------------------
DROP TABLE IF EXISTS "public"."infra_file_content";
CREATE TABLE "public"."infra_file_content" (
  "id" int8 NOT NULL,
  "config_id" int8 NOT NULL,
  "path" varchar(512) COLLATE "pg_catalog"."default" NOT NULL,
  "content" bytea NOT NULL,
  "creator" int8 DEFAULT 0,
  "create_time" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "updater" int8 DEFAULT 0,
  "update_time" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "deleted" int2 NOT NULL DEFAULT 0
)
;
ALTER TABLE "public"."infra_file_content" OWNER TO "postgres";
COMMENT ON COLUMN "public"."infra_file_content"."id" IS '编号';
COMMENT ON COLUMN "public"."infra_file_content"."config_id" IS '配置编号';
COMMENT ON COLUMN "public"."infra_file_content"."path" IS '文件路径';
COMMENT ON COLUMN "public"."infra_file_content"."content" IS '文件内容';
COMMENT ON COLUMN "public"."infra_file_content"."creator" IS '创建者';
COMMENT ON COLUMN "public"."infra_file_content"."create_time" IS '创建时间';
COMMENT ON COLUMN "public"."infra_file_content"."updater" IS '更新者';
COMMENT ON COLUMN "public"."infra_file_content"."update_time" IS '更新时间';
COMMENT ON COLUMN "public"."infra_file_content"."deleted" IS '是否删除';
COMMENT ON TABLE "public"."infra_file_content" IS '文件表';

-- ----------------------------
-- Table structure for infra_job
-- ----------------------------
DROP TABLE IF EXISTS "public"."infra_job";
CREATE TABLE "public"."infra_job" (
  "id" int8 NOT NULL,
  "name" varchar(32) COLLATE "pg_catalog"."default" NOT NULL,
  "status" int2 NOT NULL,
  "handler_name" varchar(64) COLLATE "pg_catalog"."default" NOT NULL,
  "handler_param" varchar(255) COLLATE "pg_catalog"."default" DEFAULT NULL::character varying,
  "cron_expression" varchar(32) COLLATE "pg_catalog"."default" NOT NULL,
  "retry_count" int4 NOT NULL DEFAULT 0,
  "retry_interval" int4 NOT NULL DEFAULT 0,
  "monitor_timeout" int4 NOT NULL DEFAULT 0,
  "creator" int8 DEFAULT 0,
  "create_time" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "updater" int8 DEFAULT 0,
  "update_time" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "deleted" int2 NOT NULL DEFAULT 0
)
;
ALTER TABLE "public"."infra_job" OWNER TO "postgres";
COMMENT ON COLUMN "public"."infra_job"."id" IS '任务编号';
COMMENT ON COLUMN "public"."infra_job"."name" IS '任务名称';
COMMENT ON COLUMN "public"."infra_job"."status" IS '任务状态';
COMMENT ON COLUMN "public"."infra_job"."handler_name" IS '处理器的名字';
COMMENT ON COLUMN "public"."infra_job"."handler_param" IS '处理器的参数';
COMMENT ON COLUMN "public"."infra_job"."cron_expression" IS 'CRON 表达式';
COMMENT ON COLUMN "public"."infra_job"."retry_count" IS '重试次数';
COMMENT ON COLUMN "public"."infra_job"."retry_interval" IS '重试间隔';
COMMENT ON COLUMN "public"."infra_job"."monitor_timeout" IS '监控超时时间';
COMMENT ON COLUMN "public"."infra_job"."creator" IS '创建者';
COMMENT ON COLUMN "public"."infra_job"."create_time" IS '创建时间';
COMMENT ON COLUMN "public"."infra_job"."updater" IS '更新者';
COMMENT ON COLUMN "public"."infra_job"."update_time" IS '更新时间';
COMMENT ON COLUMN "public"."infra_job"."deleted" IS '是否删除';
COMMENT ON TABLE "public"."infra_job" IS '定时任务表';

-- ----------------------------
-- Table structure for infra_job_log
-- ----------------------------
DROP TABLE IF EXISTS "public"."infra_job_log";
CREATE TABLE "public"."infra_job_log" (
  "id" int8 NOT NULL,
  "job_id" int8 NOT NULL,
  "handler_name" varchar(64) COLLATE "pg_catalog"."default" NOT NULL,
  "handler_param" varchar(255) COLLATE "pg_catalog"."default" DEFAULT NULL::character varying,
  "execute_index" int2 NOT NULL DEFAULT 1,
  "begin_time" timestamp(6) NOT NULL,
  "end_time" timestamp(6),
  "duration" int4,
  "status" int2 NOT NULL,
  "result" varchar(4000) COLLATE "pg_catalog"."default" DEFAULT ''::character varying,
  "creator" int8 DEFAULT 0,
  "create_time" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "updater" int8 DEFAULT 0,
  "update_time" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "deleted" int2 NOT NULL DEFAULT 0
)
;
ALTER TABLE "public"."infra_job_log" OWNER TO "postgres";
COMMENT ON COLUMN "public"."infra_job_log"."id" IS '日志编号';
COMMENT ON COLUMN "public"."infra_job_log"."job_id" IS '任务编号';
COMMENT ON COLUMN "public"."infra_job_log"."handler_name" IS '处理器的名字';
COMMENT ON COLUMN "public"."infra_job_log"."handler_param" IS '处理器的参数';
COMMENT ON COLUMN "public"."infra_job_log"."execute_index" IS '第几次执行';
COMMENT ON COLUMN "public"."infra_job_log"."begin_time" IS '开始执行时间';
COMMENT ON COLUMN "public"."infra_job_log"."end_time" IS '结束执行时间';
COMMENT ON COLUMN "public"."infra_job_log"."duration" IS '执行时长';
COMMENT ON COLUMN "public"."infra_job_log"."status" IS '任务状态';
COMMENT ON COLUMN "public"."infra_job_log"."result" IS '结果数据';
COMMENT ON COLUMN "public"."infra_job_log"."creator" IS '创建者';
COMMENT ON COLUMN "public"."infra_job_log"."create_time" IS '创建时间';
COMMENT ON COLUMN "public"."infra_job_log"."updater" IS '更新者';
COMMENT ON COLUMN "public"."infra_job_log"."update_time" IS '更新时间';
COMMENT ON COLUMN "public"."infra_job_log"."deleted" IS '是否删除';
COMMENT ON TABLE "public"."infra_job_log" IS '定时任务日志表';

-- ----------------------------
-- Table structure for metadata_business_entity
-- ----------------------------
DROP TABLE IF EXISTS "public"."metadata_business_entity";
CREATE TABLE "public"."metadata_business_entity" (
  "id" int8 NOT NULL,
  "display_name" varchar(64) COLLATE "pg_catalog"."default" NOT NULL,
  "code" varchar(32) COLLATE "pg_catalog"."default" NOT NULL,
  "entity_type" int4 NOT NULL DEFAULT 1,
  "description" varchar(512) COLLATE "pg_catalog"."default",
  "datasource_id" int8 NOT NULL,
  "table_name" varchar(128) COLLATE "pg_catalog"."default",
  "run_mode" int4 NOT NULL DEFAULT 0,
  "app_id" int8 NOT NULL,
  "deleted" int4 NOT NULL DEFAULT 0,
  "creator" int8 NOT NULL,
  "create_time" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "updater" int8 NOT NULL,
  "update_time" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "lock_version" int4 NOT NULL DEFAULT 0,
  "tenant_id" int8 NOT NULL,
  "display_config" text COLLATE "pg_catalog"."default"
)
;
ALTER TABLE "public"."metadata_business_entity" OWNER TO "postgres";
COMMENT ON COLUMN "public"."metadata_business_entity"."id" IS '主键ID';
COMMENT ON COLUMN "public"."metadata_business_entity"."display_name" IS '实体名称';
COMMENT ON COLUMN "public"."metadata_business_entity"."code" IS '实体编码';
COMMENT ON COLUMN "public"."metadata_business_entity"."entity_type" IS '实体类型(1:自建表 2:复用已有表)';
COMMENT ON COLUMN "public"."metadata_business_entity"."description" IS '实体描述';
COMMENT ON COLUMN "public"."metadata_business_entity"."datasource_id" IS '数据源ID';
COMMENT ON COLUMN "public"."metadata_business_entity"."table_name" IS '对应数据表名';
COMMENT ON COLUMN "public"."metadata_business_entity"."run_mode" IS '运行模式：0 编辑态，1 运行态';
COMMENT ON COLUMN "public"."metadata_business_entity"."app_id" IS '应用ID';
COMMENT ON COLUMN "public"."metadata_business_entity"."deleted" IS '软删除标识';
COMMENT ON COLUMN "public"."metadata_business_entity"."creator" IS '创建人ID';
COMMENT ON COLUMN "public"."metadata_business_entity"."create_time" IS '创建时间';
COMMENT ON COLUMN "public"."metadata_business_entity"."updater" IS '更新人ID';
COMMENT ON COLUMN "public"."metadata_business_entity"."update_time" IS '更新时间';
COMMENT ON COLUMN "public"."metadata_business_entity"."lock_version" IS '版本锁标识';
COMMENT ON COLUMN "public"."metadata_business_entity"."tenant_id" IS '租户ID';
COMMENT ON COLUMN "public"."metadata_business_entity"."display_config" IS '前端显示配置json';
COMMENT ON TABLE "public"."metadata_business_entity" IS '业务实体表';

-- ----------------------------
-- Table structure for metadata_datasource
-- ----------------------------
DROP TABLE IF EXISTS "public"."metadata_datasource";
CREATE TABLE "public"."metadata_datasource" (
  "id" int8 NOT NULL,
  "datasource_name" varchar(256) COLLATE "pg_catalog"."default" NOT NULL,
  "code" varchar(128) COLLATE "pg_catalog"."default" NOT NULL,
  "datasource_type" varchar(64) COLLATE "pg_catalog"."default" NOT NULL,
  "config" text COLLATE "pg_catalog"."default" NOT NULL,
  "description" text COLLATE "pg_catalog"."default",
  "run_mode" int4 NOT NULL DEFAULT 0,
  "app_id" int8 NOT NULL,
  "deleted" int4 NOT NULL DEFAULT 0,
  "creator" int8 NOT NULL,
  "create_time" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "updater" int8 NOT NULL,
  "update_time" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "lock_version" int4 NOT NULL DEFAULT 0,
  "tenant_id" int8 NOT NULL,
  "datasource_origin" int4 NOT NULL DEFAULT 0
)
;
ALTER TABLE "public"."metadata_datasource" OWNER TO "postgres";
COMMENT ON COLUMN "public"."metadata_datasource"."id" IS '主键ID';
COMMENT ON COLUMN "public"."metadata_datasource"."datasource_name" IS '数据源名称';
COMMENT ON COLUMN "public"."metadata_datasource"."code" IS '数据源编码';
COMMENT ON COLUMN "public"."metadata_datasource"."datasource_type" IS '数据源类型(POSTGRESQL,MYSQL,KINGBASE,TDENGINE,CLICKHOUSE等)';
COMMENT ON COLUMN "public"."metadata_datasource"."config" IS '数据源配置信息(JSON格式存储所有连接参数)';
COMMENT ON COLUMN "public"."metadata_datasource"."description" IS '描述';
COMMENT ON COLUMN "public"."metadata_datasource"."run_mode" IS '运行模式：0 编辑态，1 运行态';
COMMENT ON COLUMN "public"."metadata_datasource"."app_id" IS '应用ID';
COMMENT ON COLUMN "public"."metadata_datasource"."deleted" IS '软删除标识';
COMMENT ON COLUMN "public"."metadata_datasource"."creator" IS '创建人ID';
COMMENT ON COLUMN "public"."metadata_datasource"."create_time" IS '创建时间';
COMMENT ON COLUMN "public"."metadata_datasource"."updater" IS '更新人ID';
COMMENT ON COLUMN "public"."metadata_datasource"."update_time" IS '更新时间';
COMMENT ON COLUMN "public"."metadata_datasource"."lock_version" IS '版本锁标识';
COMMENT ON COLUMN "public"."metadata_datasource"."tenant_id" IS '租户ID';
COMMENT ON COLUMN "public"."metadata_datasource"."datasource_origin" IS '数据源来源 0.系统默认，1.自有数据源，2 外部数据源';
COMMENT ON TABLE "public"."metadata_datasource" IS '数据源表';

-- ----------------------------
-- Table structure for metadata_entity_field
-- ----------------------------
DROP TABLE IF EXISTS "public"."metadata_entity_field";
CREATE TABLE "public"."metadata_entity_field" (
  "id" int8 NOT NULL,
  "entity_id" int8 NOT NULL,
  "field_name" varchar(128) COLLATE "pg_catalog"."default" NOT NULL,
  "display_name" varchar(128) COLLATE "pg_catalog"."default" NOT NULL,
  "field_type" varchar(64) COLLATE "pg_catalog"."default" NOT NULL,
  "data_length" int4,
  "decimal_places" int4,
  "default_value" text COLLATE "pg_catalog"."default",
  "description" varchar(256) COLLATE "pg_catalog"."default",
  "is_system_field" bool NOT NULL DEFAULT false,
  "is_primary_key" bool NOT NULL DEFAULT false,
  "is_required" bool NOT NULL DEFAULT false,
  "is_unique" bool NOT NULL DEFAULT false,
  "allow_null" bool NOT NULL DEFAULT true,
  "sort_order" int4 NOT NULL DEFAULT 0,
  "validation_rules" text COLLATE "pg_catalog"."default",
  "run_mode" int4 NOT NULL DEFAULT 0,
  "app_id" int8 NOT NULL,
  "deleted" int4 NOT NULL DEFAULT 0,
  "creator" int8 NOT NULL,
  "create_time" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "updater" int8 NOT NULL,
  "update_time" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "lock_version" int4 NOT NULL DEFAULT 0,
  "tenant_id" int8 NOT NULL,
  "status" int4
)
;
ALTER TABLE "public"."metadata_entity_field" OWNER TO "postgres";
COMMENT ON COLUMN "public"."metadata_entity_field"."id" IS '主键ID';
COMMENT ON COLUMN "public"."metadata_entity_field"."entity_id" IS '实体ID';
COMMENT ON COLUMN "public"."metadata_entity_field"."field_name" IS '字段名称';
COMMENT ON COLUMN "public"."metadata_entity_field"."display_name" IS '显示名称';
COMMENT ON COLUMN "public"."metadata_entity_field"."field_type" IS '字段类型';
COMMENT ON COLUMN "public"."metadata_entity_field"."data_length" IS '数据长度';
COMMENT ON COLUMN "public"."metadata_entity_field"."decimal_places" IS '小数位数';
COMMENT ON COLUMN "public"."metadata_entity_field"."default_value" IS '默认值';
COMMENT ON COLUMN "public"."metadata_entity_field"."description" IS '字段描述';
COMMENT ON COLUMN "public"."metadata_entity_field"."is_system_field" IS '是否系统字段';
COMMENT ON COLUMN "public"."metadata_entity_field"."is_primary_key" IS '是否主键';
COMMENT ON COLUMN "public"."metadata_entity_field"."is_required" IS '是否必填';
COMMENT ON COLUMN "public"."metadata_entity_field"."is_unique" IS '是否唯一';
COMMENT ON COLUMN "public"."metadata_entity_field"."allow_null" IS '是否允许空值';
COMMENT ON COLUMN "public"."metadata_entity_field"."sort_order" IS '排序';
COMMENT ON COLUMN "public"."metadata_entity_field"."validation_rules" IS '校验规则表达式';
COMMENT ON COLUMN "public"."metadata_entity_field"."run_mode" IS '运行模式：0 编辑态，1 运行态';
COMMENT ON COLUMN "public"."metadata_entity_field"."app_id" IS '应用ID';
COMMENT ON COLUMN "public"."metadata_entity_field"."deleted" IS '软删除标识';
COMMENT ON COLUMN "public"."metadata_entity_field"."creator" IS '创建人ID';
COMMENT ON COLUMN "public"."metadata_entity_field"."create_time" IS '创建时间';
COMMENT ON COLUMN "public"."metadata_entity_field"."updater" IS '更新人ID';
COMMENT ON COLUMN "public"."metadata_entity_field"."update_time" IS '更新时间';
COMMENT ON COLUMN "public"."metadata_entity_field"."lock_version" IS '版本锁标识';
COMMENT ON COLUMN "public"."metadata_entity_field"."tenant_id" IS '租户ID';
COMMENT ON COLUMN "public"."metadata_entity_field"."status" IS '字段状态 0：开启，1：关闭';
COMMENT ON TABLE "public"."metadata_entity_field" IS '实体字段表';

-- ----------------------------
-- Table structure for metadata_entity_relationship
-- ----------------------------
DROP TABLE IF EXISTS "public"."metadata_entity_relationship";
CREATE TABLE "public"."metadata_entity_relationship" (
  "id" int8 NOT NULL,
  "relation_name" varchar(128) COLLATE "pg_catalog"."default" NOT NULL,
  "source_entity_id" int8 NOT NULL,
  "target_entity_id" int8 NOT NULL,
  "relationship_type" varchar(32) COLLATE "pg_catalog"."default" NOT NULL,
  "source_field_id" varchar(128) COLLATE "pg_catalog"."default" NOT NULL,
  "target_field_id" varchar(128) COLLATE "pg_catalog"."default" NOT NULL,
  "cascade_type" varchar(32) COLLATE "pg_catalog"."default" DEFAULT 'READ'::character varying,
  "description" varchar(256) COLLATE "pg_catalog"."default",
  "run_mode" int4 NOT NULL DEFAULT 0,
  "app_id" int8 NOT NULL,
  "deleted" int4 NOT NULL DEFAULT 0,
  "creator" int8 NOT NULL,
  "create_time" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "updater" int8 NOT NULL,
  "update_time" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "lock_version" int4 NOT NULL DEFAULT 0,
  "tenant_id" int8 NOT NULL
)
;
ALTER TABLE "public"."metadata_entity_relationship" OWNER TO "postgres";
COMMENT ON COLUMN "public"."metadata_entity_relationship"."id" IS '主键ID';
COMMENT ON COLUMN "public"."metadata_entity_relationship"."relation_name" IS '关系名称';
COMMENT ON COLUMN "public"."metadata_entity_relationship"."source_entity_id" IS '源实体ID';
COMMENT ON COLUMN "public"."metadata_entity_relationship"."target_entity_id" IS '目标实体ID';
COMMENT ON COLUMN "public"."metadata_entity_relationship"."relationship_type" IS '关系类型(ONE_TO_ONE,ONE_TO_MANY,MANY_TO_ONE,MANY_TO_MANY)';
COMMENT ON COLUMN "public"."metadata_entity_relationship"."source_field_id" IS '源字段id';
COMMENT ON COLUMN "public"."metadata_entity_relationship"."target_field_id" IS '目标字段id';
COMMENT ON COLUMN "public"."metadata_entity_relationship"."cascade_type" IS '级联操作类型(read,all,delete,none)';
COMMENT ON COLUMN "public"."metadata_entity_relationship"."description" IS '关系描述';
COMMENT ON COLUMN "public"."metadata_entity_relationship"."run_mode" IS '运行模式：0 编辑态，1 运行态';
COMMENT ON COLUMN "public"."metadata_entity_relationship"."app_id" IS '应用ID';
COMMENT ON COLUMN "public"."metadata_entity_relationship"."deleted" IS '软删除标识';
COMMENT ON COLUMN "public"."metadata_entity_relationship"."creator" IS '创建人ID';
COMMENT ON COLUMN "public"."metadata_entity_relationship"."create_time" IS '创建时间';
COMMENT ON COLUMN "public"."metadata_entity_relationship"."updater" IS '更新人ID';
COMMENT ON COLUMN "public"."metadata_entity_relationship"."update_time" IS '更新时间';
COMMENT ON COLUMN "public"."metadata_entity_relationship"."lock_version" IS '版本锁标识';
COMMENT ON COLUMN "public"."metadata_entity_relationship"."tenant_id" IS '租户ID';
COMMENT ON TABLE "public"."metadata_entity_relationship" IS '实体关系表';

-- ----------------------------
-- Table structure for metadata_field_type_mapping
-- ----------------------------
DROP TABLE IF EXISTS "public"."metadata_field_type_mapping";
CREATE TABLE "public"."metadata_field_type_mapping" (
  "id" int8 NOT NULL DEFAULT nextval('field_type_mapping_id_seq'::regclass),
  "business_field_type" varchar(50) COLLATE "pg_catalog"."default" NOT NULL,
  "business_meaning" text COLLATE "pg_catalog"."default" NOT NULL,
  "database_type" varchar(20) COLLATE "pg_catalog"."default" NOT NULL,
  "database_field" varchar(100) COLLATE "pg_catalog"."default" NOT NULL,
  "is_default" int4 DEFAULT 0,
  "default_length" int4 DEFAULT 255,
  "default_decimal_places" int4 DEFAULT 0,
  "created_at" timestamp(6) DEFAULT CURRENT_TIMESTAMP,
  "updated_at" timestamp(6) DEFAULT CURRENT_TIMESTAMP
)
;
ALTER TABLE "public"."metadata_field_type_mapping" OWNER TO "postgres";
COMMENT ON COLUMN "public"."metadata_field_type_mapping"."id" IS '主键ID';
COMMENT ON COLUMN "public"."metadata_field_type_mapping"."business_field_type" IS '业务字段类型';
COMMENT ON COLUMN "public"."metadata_field_type_mapping"."business_meaning" IS '业务含义';
COMMENT ON COLUMN "public"."metadata_field_type_mapping"."database_type" IS '数据库类型';
COMMENT ON COLUMN "public"."metadata_field_type_mapping"."database_field" IS '数据库中对应的字段';
COMMENT ON COLUMN "public"."metadata_field_type_mapping"."is_default" IS '默认还是备选：0-备选，1-默认';
COMMENT ON COLUMN "public"."metadata_field_type_mapping"."default_length" IS '默认长度';
COMMENT ON COLUMN "public"."metadata_field_type_mapping"."default_decimal_places" IS '默认小数点后长度';
COMMENT ON COLUMN "public"."metadata_field_type_mapping"."created_at" IS '创建时间';
COMMENT ON COLUMN "public"."metadata_field_type_mapping"."updated_at" IS '更新时间';
COMMENT ON TABLE "public"."metadata_field_type_mapping" IS '字段类型映射表';

-- ----------------------------
-- Table structure for metadata_system_fields
-- ----------------------------
DROP TABLE IF EXISTS "public"."metadata_system_fields";
CREATE TABLE "public"."metadata_system_fields" (
  "id" int8 NOT NULL DEFAULT nextval('metadata_system_fields_id_seq'::regclass),
  "field_name" varchar(50) COLLATE "pg_catalog"."default" NOT NULL,
  "field_type" varchar(20) COLLATE "pg_catalog"."default" NOT NULL,
  "is_snowflake_id" int4 NOT NULL DEFAULT 0,
  "is_required" int4 NOT NULL DEFAULT 0,
  "default_value" varchar(100) COLLATE "pg_catalog"."default",
  "description" text COLLATE "pg_catalog"."default",
  "is_enabled" int4 NOT NULL DEFAULT 1
)
;
ALTER TABLE "public"."metadata_system_fields" OWNER TO "postgres";
COMMENT ON COLUMN "public"."metadata_system_fields"."id" IS '主键ID';
COMMENT ON COLUMN "public"."metadata_system_fields"."field_name" IS '字段名';
COMMENT ON COLUMN "public"."metadata_system_fields"."field_type" IS '字段类型';
COMMENT ON COLUMN "public"."metadata_system_fields"."is_snowflake_id" IS '是否为雪花ID(0:否,1:是)';
COMMENT ON COLUMN "public"."metadata_system_fields"."is_required" IS '是否必填(0:否,1:是)';
COMMENT ON COLUMN "public"."metadata_system_fields"."default_value" IS '默认值';
COMMENT ON COLUMN "public"."metadata_system_fields"."description" IS '字段说明';
COMMENT ON COLUMN "public"."metadata_system_fields"."is_enabled" IS '是否启用(0:否,1:是)';
COMMENT ON TABLE "public"."metadata_system_fields" IS '元数据系统字段维护表';

-- ----------------------------
-- Table structure for metadata_validation_rule
-- ----------------------------
DROP TABLE IF EXISTS "public"."metadata_validation_rule";
CREATE TABLE "public"."metadata_validation_rule" (
  "id" int8 NOT NULL,
  "validation_name" varchar(128) COLLATE "pg_catalog"."default" NOT NULL,
  "validation_code" varchar(128) COLLATE "pg_catalog"."default" NOT NULL,
  "entity_id" int8,
  "field_id" int8,
  "validation_condition" varchar(64) COLLATE "pg_catalog"."default" NOT NULL,
  "validation_type" varchar(64) COLLATE "pg_catalog"."default" NOT NULL,
  "validation_target_object" varchar(64) COLLATE "pg_catalog"."default" NOT NULL,
  "validation_expression" text COLLATE "pg_catalog"."default",
  "error_message" varchar(512) COLLATE "pg_catalog"."default" NOT NULL,
  "validation_timing" varchar(64) COLLATE "pg_catalog"."default" NOT NULL,
  "sort_order" int4 NOT NULL DEFAULT 0,
  "run_mode" int4 NOT NULL DEFAULT 0,
  "app_id" int8 NOT NULL,
  "deleted" int4 NOT NULL DEFAULT 0,
  "creator" int8 NOT NULL,
  "create_time" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "updater" int8 NOT NULL,
  "update_time" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "lock_version" int4 NOT NULL DEFAULT 0,
  "tenant_id" int8 NOT NULL
)
;
ALTER TABLE "public"."metadata_validation_rule" OWNER TO "postgres";
COMMENT ON COLUMN "public"."metadata_validation_rule"."id" IS '主键ID';
COMMENT ON COLUMN "public"."metadata_validation_rule"."validation_name" IS '规则名称';
COMMENT ON COLUMN "public"."metadata_validation_rule"."validation_code" IS '规则编码';
COMMENT ON COLUMN "public"."metadata_validation_rule"."entity_id" IS '关联实体ID';
COMMENT ON COLUMN "public"."metadata_validation_rule"."field_id" IS '关联字段ID';
COMMENT ON COLUMN "public"."metadata_validation_rule"."validation_condition" IS '校验条件(=,<,>,<=,>=,LIKE,IN,NOT IN,BETWEEN,NOT BETWEEN,IS NULL,IS NOT NULL,etc)';
COMMENT ON COLUMN "public"."metadata_validation_rule"."validation_type" IS '校验类型(字段，变量等)';
COMMENT ON COLUMN "public"."metadata_validation_rule"."validation_target_object" IS '校验比较对象';
COMMENT ON COLUMN "public"."metadata_validation_rule"."validation_expression" IS '校验表达式';
COMMENT ON COLUMN "public"."metadata_validation_rule"."error_message" IS '错误提示信息';
COMMENT ON COLUMN "public"."metadata_validation_rule"."validation_timing" IS '校验时机(更新时,新增时)';
COMMENT ON COLUMN "public"."metadata_validation_rule"."sort_order" IS '执行顺序';
COMMENT ON COLUMN "public"."metadata_validation_rule"."run_mode" IS '运行模式：0 编辑态，1 运行态';
COMMENT ON COLUMN "public"."metadata_validation_rule"."app_id" IS '应用ID';
COMMENT ON COLUMN "public"."metadata_validation_rule"."deleted" IS '软删除标识';
COMMENT ON COLUMN "public"."metadata_validation_rule"."creator" IS '创建人ID';
COMMENT ON COLUMN "public"."metadata_validation_rule"."create_time" IS '创建时间';
COMMENT ON COLUMN "public"."metadata_validation_rule"."updater" IS '更新人ID';
COMMENT ON COLUMN "public"."metadata_validation_rule"."update_time" IS '更新时间';
COMMENT ON COLUMN "public"."metadata_validation_rule"."lock_version" IS '版本锁标识';
COMMENT ON COLUMN "public"."metadata_validation_rule"."tenant_id" IS '租户ID';
COMMENT ON TABLE "public"."metadata_validation_rule" IS '校验规则表';

-- ----------------------------
-- Table structure for system_dept
-- ----------------------------
DROP TABLE IF EXISTS "public"."system_dept";
CREATE TABLE "public"."system_dept" (
  "id" int8 NOT NULL,
  "name" varchar(30) COLLATE "pg_catalog"."default" NOT NULL DEFAULT ''::character varying,
  "parent_id" int8 NOT NULL DEFAULT 0,
  "sort" int4 NOT NULL DEFAULT 0,
  "leader_user_id" int8,
  "phone" varchar(11) COLLATE "pg_catalog"."default" DEFAULT NULL::character varying,
  "email" varchar(50) COLLATE "pg_catalog"."default" DEFAULT NULL::character varying,
  "status" int2 NOT NULL,
  "creator" int8 DEFAULT 0,
  "create_time" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "updater" int8 DEFAULT 0,
  "update_time" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "tenant_id" int8 NOT NULL,
  "deleted" int2 DEFAULT 0
)
;
ALTER TABLE "public"."system_dept" OWNER TO "postgres";
COMMENT ON COLUMN "public"."system_dept"."id" IS '部门id';
COMMENT ON COLUMN "public"."system_dept"."name" IS '部门名称';
COMMENT ON COLUMN "public"."system_dept"."parent_id" IS '父部门id';
COMMENT ON COLUMN "public"."system_dept"."sort" IS '显示顺序';
COMMENT ON COLUMN "public"."system_dept"."leader_user_id" IS '负责人';
COMMENT ON COLUMN "public"."system_dept"."phone" IS '联系电话';
COMMENT ON COLUMN "public"."system_dept"."email" IS '邮箱';
COMMENT ON COLUMN "public"."system_dept"."status" IS '部门状态（0正常 1停用）';
COMMENT ON COLUMN "public"."system_dept"."creator" IS '创建者';
COMMENT ON COLUMN "public"."system_dept"."create_time" IS '创建时间';
COMMENT ON COLUMN "public"."system_dept"."updater" IS '更新者';
COMMENT ON COLUMN "public"."system_dept"."update_time" IS '更新时间';
COMMENT ON COLUMN "public"."system_dept"."tenant_id" IS '租户编号';
COMMENT ON TABLE "public"."system_dept" IS '部门表';

-- ----------------------------
-- Table structure for system_dict_data
-- ----------------------------
DROP TABLE IF EXISTS "public"."system_dict_data";
CREATE TABLE "public"."system_dict_data" (
  "id" int8 NOT NULL,
  "sort" int4 NOT NULL DEFAULT 0,
  "label" varchar(100) COLLATE "pg_catalog"."default" NOT NULL DEFAULT ''::character varying,
  "value" varchar(100) COLLATE "pg_catalog"."default" NOT NULL DEFAULT ''::character varying,
  "dict_type" varchar(100) COLLATE "pg_catalog"."default" NOT NULL DEFAULT ''::character varying,
  "status" int2 NOT NULL DEFAULT 0,
  "color_type" varchar(100) COLLATE "pg_catalog"."default" DEFAULT ''::character varying,
  "css_class" varchar(100) COLLATE "pg_catalog"."default" DEFAULT ''::character varying,
  "remark" varchar(500) COLLATE "pg_catalog"."default" DEFAULT NULL::character varying,
  "creator" int8 DEFAULT 0,
  "create_time" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "updater" int8 DEFAULT 0,
  "update_time" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "deleted" int2 NOT NULL DEFAULT 0
)
;
ALTER TABLE "public"."system_dict_data" OWNER TO "postgres";
COMMENT ON COLUMN "public"."system_dict_data"."id" IS '字典编码';
COMMENT ON COLUMN "public"."system_dict_data"."sort" IS '字典排序';
COMMENT ON COLUMN "public"."system_dict_data"."label" IS '字典标签';
COMMENT ON COLUMN "public"."system_dict_data"."value" IS '字典键值';
COMMENT ON COLUMN "public"."system_dict_data"."dict_type" IS '字典类型';
COMMENT ON COLUMN "public"."system_dict_data"."status" IS '状态（0正常 1停用）';
COMMENT ON COLUMN "public"."system_dict_data"."color_type" IS '颜色类型';
COMMENT ON COLUMN "public"."system_dict_data"."css_class" IS 'css 样式';
COMMENT ON COLUMN "public"."system_dict_data"."remark" IS '备注';
COMMENT ON COLUMN "public"."system_dict_data"."creator" IS '创建者';
COMMENT ON COLUMN "public"."system_dict_data"."create_time" IS '创建时间';
COMMENT ON COLUMN "public"."system_dict_data"."updater" IS '更新者';
COMMENT ON COLUMN "public"."system_dict_data"."update_time" IS '更新时间';
COMMENT ON COLUMN "public"."system_dict_data"."deleted" IS '是否删除';
COMMENT ON TABLE "public"."system_dict_data" IS '字典数据表';

-- ----------------------------
-- Table structure for system_dict_type
-- ----------------------------
DROP TABLE IF EXISTS "public"."system_dict_type";
CREATE TABLE "public"."system_dict_type" (
  "id" int8 NOT NULL,
  "name" varchar(100) COLLATE "pg_catalog"."default" NOT NULL DEFAULT ''::character varying,
  "type" varchar(100) COLLATE "pg_catalog"."default" NOT NULL DEFAULT ''::character varying,
  "status" int2 NOT NULL DEFAULT 0,
  "remark" varchar(500) COLLATE "pg_catalog"."default" DEFAULT NULL::character varying,
  "creator" int8 DEFAULT 0,
  "create_time" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "updater" int8 DEFAULT 0,
  "update_time" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "deleted" int2 NOT NULL DEFAULT 0,
  "deleted_time" timestamp(6)
)
;
ALTER TABLE "public"."system_dict_type" OWNER TO "postgres";
COMMENT ON COLUMN "public"."system_dict_type"."id" IS '字典主键';
COMMENT ON COLUMN "public"."system_dict_type"."name" IS '字典名称';
COMMENT ON COLUMN "public"."system_dict_type"."type" IS '字典类型';
COMMENT ON COLUMN "public"."system_dict_type"."status" IS '状态（0正常 1停用）';
COMMENT ON COLUMN "public"."system_dict_type"."remark" IS '备注';
COMMENT ON COLUMN "public"."system_dict_type"."creator" IS '创建者';
COMMENT ON COLUMN "public"."system_dict_type"."create_time" IS '创建时间';
COMMENT ON COLUMN "public"."system_dict_type"."updater" IS '更新者';
COMMENT ON COLUMN "public"."system_dict_type"."update_time" IS '更新时间';
COMMENT ON COLUMN "public"."system_dict_type"."deleted" IS '是否删除';
COMMENT ON COLUMN "public"."system_dict_type"."deleted_time" IS '删除时间';
COMMENT ON TABLE "public"."system_dict_type" IS '字典类型表';

-- ----------------------------
-- Table structure for system_license
-- ----------------------------
DROP TABLE IF EXISTS "public"."system_license";
CREATE TABLE "public"."system_license" (
  "id" int8 NOT NULL,
  "enterprise_name" varchar(128) COLLATE "pg_catalog"."default" NOT NULL,
  "enterprise_code" varchar(128) COLLATE "pg_catalog"."default" NOT NULL,
  "enterprise_address" varchar(1024) COLLATE "pg_catalog"."default",
  "platform_type" varchar(64) COLLATE "pg_catalog"."default",
  "tenant_limit" int4 NOT NULL,
  "user_limit" int4 NOT NULL,
  "expire_time" timestamp(6) NOT NULL,
  "status" varchar(16) COLLATE "pg_catalog"."default" NOT NULL DEFAULT 'enable'::character varying,
  "is_trial" bool NOT NULL DEFAULT false,
  "license_file" text COLLATE "pg_catalog"."default" NOT NULL,
  "creator" int8 NOT NULL,
  "updater" int8 NOT NULL,
  "create_time" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "update_time" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "deleted" int4 NOT NULL DEFAULT 0
)
;
ALTER TABLE "public"."system_license" OWNER TO "postgres";
COMMENT ON COLUMN "public"."system_license"."id" IS '主键';
COMMENT ON COLUMN "public"."system_license"."enterprise_name" IS '企业名称';
COMMENT ON COLUMN "public"."system_license"."enterprise_code" IS '企业编号';
COMMENT ON COLUMN "public"."system_license"."enterprise_address" IS '企业地址';
COMMENT ON COLUMN "public"."system_license"."platform_type" IS '平台类型';
COMMENT ON COLUMN "public"."system_license"."tenant_limit" IS '租户数量限制';
COMMENT ON COLUMN "public"."system_license"."user_limit" IS '用户数量限制';
COMMENT ON COLUMN "public"."system_license"."expire_time" IS '到期时间';
COMMENT ON COLUMN "public"."system_license"."status" IS '状态：enable,disable';
COMMENT ON COLUMN "public"."system_license"."is_trial" IS '是否为试用License';
COMMENT ON COLUMN "public"."system_license"."license_file" IS 'License文件';
COMMENT ON COLUMN "public"."system_license"."creator" IS '创建者ID';
COMMENT ON COLUMN "public"."system_license"."updater" IS '更新人ID';
COMMENT ON COLUMN "public"."system_license"."create_time" IS '创建时间';
COMMENT ON COLUMN "public"."system_license"."update_time" IS '更新时间';
COMMENT ON COLUMN "public"."system_license"."deleted" IS '软删标识：非0即删除';
COMMENT ON TABLE "public"."system_license" IS '平台License信息';

-- ----------------------------
-- Table structure for system_login_log
-- ----------------------------
DROP TABLE IF EXISTS "public"."system_login_log";
CREATE TABLE "public"."system_login_log" (
  "id" int8 NOT NULL,
  "log_type" int8 NOT NULL,
  "trace_id" varchar(64) COLLATE "pg_catalog"."default" NOT NULL DEFAULT ''::character varying,
  "user_id" int8 NOT NULL DEFAULT 0,
  "user_type" int2 NOT NULL DEFAULT 0,
  "username" varchar(50) COLLATE "pg_catalog"."default" NOT NULL DEFAULT ''::character varying,
  "result" int2 NOT NULL,
  "user_ip" varchar(50) COLLATE "pg_catalog"."default" NOT NULL,
  "user_agent" varchar(512) COLLATE "pg_catalog"."default" NOT NULL,
  "creator" int8 DEFAULT 0,
  "create_time" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "updater" int8 DEFAULT 0,
  "update_time" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "deleted" int2 NOT NULL DEFAULT 0,
  "tenant_id" int8 NOT NULL
)
;
ALTER TABLE "public"."system_login_log" OWNER TO "postgres";
COMMENT ON COLUMN "public"."system_login_log"."id" IS '访问ID';
COMMENT ON COLUMN "public"."system_login_log"."log_type" IS '日志类型';
COMMENT ON COLUMN "public"."system_login_log"."trace_id" IS '链路追踪编号';
COMMENT ON COLUMN "public"."system_login_log"."user_id" IS '用户编号';
COMMENT ON COLUMN "public"."system_login_log"."user_type" IS '用户类型';
COMMENT ON COLUMN "public"."system_login_log"."username" IS '用户账号';
COMMENT ON COLUMN "public"."system_login_log"."result" IS '登陆结果';
COMMENT ON COLUMN "public"."system_login_log"."user_ip" IS '用户 IP';
COMMENT ON COLUMN "public"."system_login_log"."user_agent" IS '浏览器 UA';
COMMENT ON COLUMN "public"."system_login_log"."creator" IS '创建者';
COMMENT ON COLUMN "public"."system_login_log"."create_time" IS '创建时间';
COMMENT ON COLUMN "public"."system_login_log"."updater" IS '更新者';
COMMENT ON COLUMN "public"."system_login_log"."update_time" IS '更新时间';
COMMENT ON COLUMN "public"."system_login_log"."deleted" IS '是否删除';
COMMENT ON COLUMN "public"."system_login_log"."tenant_id" IS '租户编号';
COMMENT ON TABLE "public"."system_login_log" IS '系统访问记录';

-- ----------------------------
-- Table structure for system_mail_account
-- ----------------------------
DROP TABLE IF EXISTS "public"."system_mail_account";
CREATE TABLE "public"."system_mail_account" (
  "id" int8 NOT NULL,
  "mail" varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
  "username" varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
  "password" varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
  "host" varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
  "port" int4 NOT NULL,
  "ssl_enable" bool NOT NULL DEFAULT false,
  "starttls_enable" bool NOT NULL DEFAULT false,
  "creator" int8 DEFAULT 0,
  "create_time" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "updater" int8 DEFAULT 0,
  "update_time" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "deleted" int2 NOT NULL DEFAULT 0
)
;
ALTER TABLE "public"."system_mail_account" OWNER TO "postgres";
COMMENT ON COLUMN "public"."system_mail_account"."id" IS '主键';
COMMENT ON COLUMN "public"."system_mail_account"."mail" IS '邮箱';
COMMENT ON COLUMN "public"."system_mail_account"."username" IS '用户名';
COMMENT ON COLUMN "public"."system_mail_account"."password" IS '密码';
COMMENT ON COLUMN "public"."system_mail_account"."host" IS 'SMTP 服务器域名';
COMMENT ON COLUMN "public"."system_mail_account"."port" IS 'SMTP 服务器端口';
COMMENT ON COLUMN "public"."system_mail_account"."ssl_enable" IS '是否开启 SSL';
COMMENT ON COLUMN "public"."system_mail_account"."starttls_enable" IS '是否开启 STARTTLS';
COMMENT ON COLUMN "public"."system_mail_account"."creator" IS '创建者';
COMMENT ON COLUMN "public"."system_mail_account"."create_time" IS '创建时间';
COMMENT ON COLUMN "public"."system_mail_account"."updater" IS '更新者';
COMMENT ON COLUMN "public"."system_mail_account"."update_time" IS '更新时间';
COMMENT ON COLUMN "public"."system_mail_account"."deleted" IS '是否删除';
COMMENT ON TABLE "public"."system_mail_account" IS '邮箱账号表';

-- ----------------------------
-- Table structure for system_mail_log
-- ----------------------------
DROP TABLE IF EXISTS "public"."system_mail_log";
CREATE TABLE "public"."system_mail_log" (
  "id" int8 NOT NULL,
  "user_id" int8,
  "user_type" int2,
  "to_mail" varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
  "account_id" int8 NOT NULL,
  "from_mail" varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
  "template_id" int8 NOT NULL,
  "template_code" varchar(63) COLLATE "pg_catalog"."default" NOT NULL,
  "template_nickname" varchar(255) COLLATE "pg_catalog"."default" DEFAULT NULL::character varying,
  "template_title" varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
  "template_content" varchar(10240) COLLATE "pg_catalog"."default" NOT NULL,
  "template_params" varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
  "send_status" int2 NOT NULL DEFAULT 0,
  "send_time" timestamp(6),
  "send_message_id" varchar(255) COLLATE "pg_catalog"."default" DEFAULT NULL::character varying,
  "send_exception" varchar(4096) COLLATE "pg_catalog"."default" DEFAULT NULL::character varying,
  "creator" int8 DEFAULT 0,
  "create_time" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "updater" int8 DEFAULT 0,
  "update_time" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "deleted" int2 NOT NULL DEFAULT 0
)
;
ALTER TABLE "public"."system_mail_log" OWNER TO "postgres";
COMMENT ON COLUMN "public"."system_mail_log"."id" IS '编号';
COMMENT ON COLUMN "public"."system_mail_log"."user_id" IS '用户编号';
COMMENT ON COLUMN "public"."system_mail_log"."user_type" IS '用户类型';
COMMENT ON COLUMN "public"."system_mail_log"."to_mail" IS '接收邮箱地址';
COMMENT ON COLUMN "public"."system_mail_log"."account_id" IS '邮箱账号编号';
COMMENT ON COLUMN "public"."system_mail_log"."from_mail" IS '发送邮箱地址';
COMMENT ON COLUMN "public"."system_mail_log"."template_id" IS '模板编号';
COMMENT ON COLUMN "public"."system_mail_log"."template_code" IS '模板编码';
COMMENT ON COLUMN "public"."system_mail_log"."template_nickname" IS '模版发送人名称';
COMMENT ON COLUMN "public"."system_mail_log"."template_title" IS '邮件标题';
COMMENT ON COLUMN "public"."system_mail_log"."template_content" IS '邮件内容';
COMMENT ON COLUMN "public"."system_mail_log"."template_params" IS '邮件参数';
COMMENT ON COLUMN "public"."system_mail_log"."send_status" IS '发送状态';
COMMENT ON COLUMN "public"."system_mail_log"."send_time" IS '发送时间';
COMMENT ON COLUMN "public"."system_mail_log"."send_message_id" IS '发送返回的消息 ID';
COMMENT ON COLUMN "public"."system_mail_log"."send_exception" IS '发送异常';
COMMENT ON COLUMN "public"."system_mail_log"."creator" IS '创建者';
COMMENT ON COLUMN "public"."system_mail_log"."create_time" IS '创建时间';
COMMENT ON COLUMN "public"."system_mail_log"."updater" IS '更新者';
COMMENT ON COLUMN "public"."system_mail_log"."update_time" IS '更新时间';
COMMENT ON COLUMN "public"."system_mail_log"."deleted" IS '是否删除';
COMMENT ON TABLE "public"."system_mail_log" IS '邮件日志表';

-- ----------------------------
-- Table structure for system_mail_template
-- ----------------------------
DROP TABLE IF EXISTS "public"."system_mail_template";
CREATE TABLE "public"."system_mail_template" (
  "id" int8 NOT NULL,
  "name" varchar(63) COLLATE "pg_catalog"."default" NOT NULL,
  "code" varchar(63) COLLATE "pg_catalog"."default" NOT NULL,
  "account_id" int8 NOT NULL,
  "nickname" varchar(255) COLLATE "pg_catalog"."default" DEFAULT NULL::character varying,
  "title" varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
  "content" varchar(10240) COLLATE "pg_catalog"."default" NOT NULL,
  "params" varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
  "status" int2 NOT NULL,
  "remark" varchar(255) COLLATE "pg_catalog"."default" DEFAULT NULL::character varying,
  "creator" int8 DEFAULT 0,
  "create_time" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "updater" int8 DEFAULT 0,
  "update_time" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "deleted" int2 NOT NULL DEFAULT 0
)
;
ALTER TABLE "public"."system_mail_template" OWNER TO "postgres";
COMMENT ON COLUMN "public"."system_mail_template"."id" IS '编号';
COMMENT ON COLUMN "public"."system_mail_template"."name" IS '模板名称';
COMMENT ON COLUMN "public"."system_mail_template"."code" IS '模板编码';
COMMENT ON COLUMN "public"."system_mail_template"."account_id" IS '发送的邮箱账号编号';
COMMENT ON COLUMN "public"."system_mail_template"."nickname" IS '发送人名称';
COMMENT ON COLUMN "public"."system_mail_template"."title" IS '模板标题';
COMMENT ON COLUMN "public"."system_mail_template"."content" IS '模板内容';
COMMENT ON COLUMN "public"."system_mail_template"."params" IS '参数数组';
COMMENT ON COLUMN "public"."system_mail_template"."status" IS '开启状态';
COMMENT ON COLUMN "public"."system_mail_template"."remark" IS '备注';
COMMENT ON COLUMN "public"."system_mail_template"."creator" IS '创建者';
COMMENT ON COLUMN "public"."system_mail_template"."create_time" IS '创建时间';
COMMENT ON COLUMN "public"."system_mail_template"."updater" IS '更新者';
COMMENT ON COLUMN "public"."system_mail_template"."update_time" IS '更新时间';
COMMENT ON COLUMN "public"."system_mail_template"."deleted" IS '是否删除';
COMMENT ON TABLE "public"."system_mail_template" IS '邮件模版表';

-- ----------------------------
-- Table structure for system_menu
-- ----------------------------
DROP TABLE IF EXISTS "public"."system_menu";
CREATE TABLE "public"."system_menu" (
  "id" int8 NOT NULL,
  "name" varchar(50) COLLATE "pg_catalog"."default" NOT NULL,
  "permission" varchar(100) COLLATE "pg_catalog"."default" NOT NULL DEFAULT ''::character varying,
  "type" int2 NOT NULL,
  "sort" int4 NOT NULL DEFAULT 0,
  "parent_id" int8 NOT NULL DEFAULT 0,
  "path" varchar(200) COLLATE "pg_catalog"."default" DEFAULT ''::character varying,
  "icon" varchar(100) COLLATE "pg_catalog"."default" DEFAULT '#'::character varying,
  "component" varchar(255) COLLATE "pg_catalog"."default" DEFAULT NULL::character varying,
  "component_name" varchar(255) COLLATE "pg_catalog"."default" DEFAULT NULL::character varying,
  "status" int2 NOT NULL DEFAULT 0,
  "visible" bool NOT NULL DEFAULT true,
  "keep_alive" bool NOT NULL DEFAULT true,
  "always_show" bool NOT NULL DEFAULT true,
  "creator" int8 DEFAULT 0,
  "create_time" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "updater" int8 DEFAULT 0,
  "update_time" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "deleted" int2 NOT NULL DEFAULT 0
)
;
ALTER TABLE "public"."system_menu" OWNER TO "postgres";
COMMENT ON COLUMN "public"."system_menu"."id" IS '菜单ID';
COMMENT ON COLUMN "public"."system_menu"."name" IS '菜单名称';
COMMENT ON COLUMN "public"."system_menu"."permission" IS '权限标识';
COMMENT ON COLUMN "public"."system_menu"."type" IS '菜单类型';
COMMENT ON COLUMN "public"."system_menu"."sort" IS '显示顺序';
COMMENT ON COLUMN "public"."system_menu"."parent_id" IS '父菜单ID';
COMMENT ON COLUMN "public"."system_menu"."path" IS '路由地址';
COMMENT ON COLUMN "public"."system_menu"."icon" IS '菜单图标';
COMMENT ON COLUMN "public"."system_menu"."component" IS '组件路径';
COMMENT ON COLUMN "public"."system_menu"."component_name" IS '组件名';
COMMENT ON COLUMN "public"."system_menu"."status" IS '菜单状态';
COMMENT ON COLUMN "public"."system_menu"."visible" IS '是否可见';
COMMENT ON COLUMN "public"."system_menu"."keep_alive" IS '是否缓存';
COMMENT ON COLUMN "public"."system_menu"."always_show" IS '是否总是显示';
COMMENT ON COLUMN "public"."system_menu"."creator" IS '创建者';
COMMENT ON COLUMN "public"."system_menu"."create_time" IS '创建时间';
COMMENT ON COLUMN "public"."system_menu"."updater" IS '更新者';
COMMENT ON COLUMN "public"."system_menu"."update_time" IS '更新时间';
COMMENT ON COLUMN "public"."system_menu"."deleted" IS '是否删除';
COMMENT ON TABLE "public"."system_menu" IS '菜单权限表';

-- ----------------------------
-- Table structure for system_notice
-- ----------------------------
DROP TABLE IF EXISTS "public"."system_notice";
CREATE TABLE "public"."system_notice" (
  "id" int8 NOT NULL,
  "title" varchar(50) COLLATE "pg_catalog"."default" NOT NULL,
  "content" text COLLATE "pg_catalog"."default" NOT NULL,
  "type" int2 NOT NULL,
  "status" int2 NOT NULL DEFAULT 0,
  "creator" int8 DEFAULT 0,
  "create_time" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "updater" int8 DEFAULT 0,
  "update_time" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "deleted" int2 NOT NULL DEFAULT 0,
  "tenant_id" int8 NOT NULL
)
;
ALTER TABLE "public"."system_notice" OWNER TO "postgres";
COMMENT ON COLUMN "public"."system_notice"."id" IS '公告ID';
COMMENT ON COLUMN "public"."system_notice"."title" IS '公告标题';
COMMENT ON COLUMN "public"."system_notice"."content" IS '公告内容';
COMMENT ON COLUMN "public"."system_notice"."type" IS '公告类型（1通知 2公告）';
COMMENT ON COLUMN "public"."system_notice"."status" IS '公告状态（0正常 1关闭）';
COMMENT ON COLUMN "public"."system_notice"."creator" IS '创建者';
COMMENT ON COLUMN "public"."system_notice"."create_time" IS '创建时间';
COMMENT ON COLUMN "public"."system_notice"."updater" IS '更新者';
COMMENT ON COLUMN "public"."system_notice"."update_time" IS '更新时间';
COMMENT ON COLUMN "public"."system_notice"."deleted" IS '是否删除';
COMMENT ON COLUMN "public"."system_notice"."tenant_id" IS '租户编号';
COMMENT ON TABLE "public"."system_notice" IS '通知公告表';

-- ----------------------------
-- Table structure for system_notify_message
-- ----------------------------
DROP TABLE IF EXISTS "public"."system_notify_message";
CREATE TABLE "public"."system_notify_message" (
  "id" int8 NOT NULL,
  "user_id" int8 NOT NULL,
  "user_type" int2 NOT NULL,
  "template_id" int8 NOT NULL,
  "template_code" varchar(64) COLLATE "pg_catalog"."default" NOT NULL,
  "template_nickname" varchar(63) COLLATE "pg_catalog"."default" NOT NULL,
  "template_content" varchar(1024) COLLATE "pg_catalog"."default" NOT NULL,
  "template_type" int4 NOT NULL,
  "template_params" varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
  "read_status" bool NOT NULL,
  "read_time" timestamp(6),
  "creator" int8 DEFAULT 0,
  "create_time" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "updater" int8 DEFAULT 0,
  "update_time" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "deleted" int2 NOT NULL DEFAULT 0,
  "tenant_id" int8 NOT NULL DEFAULT 1
)
;
ALTER TABLE "public"."system_notify_message" OWNER TO "postgres";
COMMENT ON COLUMN "public"."system_notify_message"."id" IS '用户ID';
COMMENT ON COLUMN "public"."system_notify_message"."user_id" IS '用户id';
COMMENT ON COLUMN "public"."system_notify_message"."user_type" IS '用户类型';
COMMENT ON COLUMN "public"."system_notify_message"."template_id" IS '模版编号';
COMMENT ON COLUMN "public"."system_notify_message"."template_code" IS '模板编码';
COMMENT ON COLUMN "public"."system_notify_message"."template_nickname" IS '模版发送人名称';
COMMENT ON COLUMN "public"."system_notify_message"."template_content" IS '模版内容';
COMMENT ON COLUMN "public"."system_notify_message"."template_type" IS '模版类型';
COMMENT ON COLUMN "public"."system_notify_message"."template_params" IS '模版参数';
COMMENT ON COLUMN "public"."system_notify_message"."read_status" IS '是否已读';
COMMENT ON COLUMN "public"."system_notify_message"."read_time" IS '阅读时间';
COMMENT ON COLUMN "public"."system_notify_message"."creator" IS '创建者';
COMMENT ON COLUMN "public"."system_notify_message"."create_time" IS '创建时间';
COMMENT ON COLUMN "public"."system_notify_message"."updater" IS '更新者';
COMMENT ON COLUMN "public"."system_notify_message"."update_time" IS '更新时间';
COMMENT ON COLUMN "public"."system_notify_message"."deleted" IS '是否删除';
COMMENT ON COLUMN "public"."system_notify_message"."tenant_id" IS '租户编号';
COMMENT ON TABLE "public"."system_notify_message" IS '站内信消息表';

-- ----------------------------
-- Table structure for system_notify_template
-- ----------------------------
DROP TABLE IF EXISTS "public"."system_notify_template";
CREATE TABLE "public"."system_notify_template" (
  "id" int8 NOT NULL,
  "name" varchar(63) COLLATE "pg_catalog"."default" NOT NULL,
  "code" varchar(64) COLLATE "pg_catalog"."default" NOT NULL,
  "nickname" varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
  "content" varchar(1024) COLLATE "pg_catalog"."default" NOT NULL,
  "type" int2 NOT NULL,
  "params" varchar(255) COLLATE "pg_catalog"."default" DEFAULT NULL::character varying,
  "status" int2 NOT NULL,
  "remark" varchar(255) COLLATE "pg_catalog"."default" DEFAULT NULL::character varying,
  "creator" int8 DEFAULT 0,
  "create_time" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "updater" int8 DEFAULT 0,
  "update_time" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "deleted" int2 NOT NULL DEFAULT 0
)
;
ALTER TABLE "public"."system_notify_template" OWNER TO "postgres";
COMMENT ON COLUMN "public"."system_notify_template"."id" IS '主键';
COMMENT ON COLUMN "public"."system_notify_template"."name" IS '模板名称';
COMMENT ON COLUMN "public"."system_notify_template"."code" IS '模版编码';
COMMENT ON COLUMN "public"."system_notify_template"."nickname" IS '发送人名称';
COMMENT ON COLUMN "public"."system_notify_template"."content" IS '模版内容';
COMMENT ON COLUMN "public"."system_notify_template"."type" IS '类型';
COMMENT ON COLUMN "public"."system_notify_template"."params" IS '参数数组';
COMMENT ON COLUMN "public"."system_notify_template"."status" IS '状态';
COMMENT ON COLUMN "public"."system_notify_template"."remark" IS '备注';
COMMENT ON COLUMN "public"."system_notify_template"."creator" IS '创建者';
COMMENT ON COLUMN "public"."system_notify_template"."create_time" IS '创建时间';
COMMENT ON COLUMN "public"."system_notify_template"."updater" IS '更新者';
COMMENT ON COLUMN "public"."system_notify_template"."update_time" IS '更新时间';
COMMENT ON COLUMN "public"."system_notify_template"."deleted" IS '是否删除';
COMMENT ON TABLE "public"."system_notify_template" IS '站内信模板表';

-- ----------------------------
-- Table structure for system_oauth2_access_token
-- ----------------------------
DROP TABLE IF EXISTS "public"."system_oauth2_access_token";
CREATE TABLE "public"."system_oauth2_access_token" (
  "id" int8 NOT NULL,
  "user_id" int8 NOT NULL,
  "user_type" int2 NOT NULL,
  "user_info" varchar(512) COLLATE "pg_catalog"."default" NOT NULL,
  "access_token" varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
  "refresh_token" varchar(32) COLLATE "pg_catalog"."default" NOT NULL,
  "client_id" varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
  "scopes" varchar(255) COLLATE "pg_catalog"."default" DEFAULT NULL::character varying,
  "expires_time" timestamp(6) NOT NULL,
  "creator" int8 DEFAULT 0,
  "create_time" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "updater" int8 DEFAULT 0,
  "update_time" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "deleted" int2 NOT NULL DEFAULT 0,
  "tenant_id" int8 NOT NULL DEFAULT 0
)
;
ALTER TABLE "public"."system_oauth2_access_token" OWNER TO "postgres";
COMMENT ON COLUMN "public"."system_oauth2_access_token"."id" IS '编号';
COMMENT ON COLUMN "public"."system_oauth2_access_token"."user_id" IS '用户编号';
COMMENT ON COLUMN "public"."system_oauth2_access_token"."user_type" IS '用户类型';
COMMENT ON COLUMN "public"."system_oauth2_access_token"."user_info" IS '用户信息';
COMMENT ON COLUMN "public"."system_oauth2_access_token"."access_token" IS '访问令牌';
COMMENT ON COLUMN "public"."system_oauth2_access_token"."refresh_token" IS '刷新令牌';
COMMENT ON COLUMN "public"."system_oauth2_access_token"."client_id" IS '客户端编号';
COMMENT ON COLUMN "public"."system_oauth2_access_token"."scopes" IS '授权范围';
COMMENT ON COLUMN "public"."system_oauth2_access_token"."expires_time" IS '过期时间';
COMMENT ON COLUMN "public"."system_oauth2_access_token"."creator" IS '创建者';
COMMENT ON COLUMN "public"."system_oauth2_access_token"."create_time" IS '创建时间';
COMMENT ON COLUMN "public"."system_oauth2_access_token"."updater" IS '更新者';
COMMENT ON COLUMN "public"."system_oauth2_access_token"."update_time" IS '更新时间';
COMMENT ON COLUMN "public"."system_oauth2_access_token"."deleted" IS '是否删除';
COMMENT ON COLUMN "public"."system_oauth2_access_token"."tenant_id" IS '租户编号';
COMMENT ON TABLE "public"."system_oauth2_access_token" IS 'OAuth2 访问令牌';

-- ----------------------------
-- Table structure for system_oauth2_approve
-- ----------------------------
DROP TABLE IF EXISTS "public"."system_oauth2_approve";
CREATE TABLE "public"."system_oauth2_approve" (
  "id" int8 NOT NULL,
  "user_id" int8 NOT NULL,
  "user_type" int2 NOT NULL,
  "client_id" varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
  "scope" varchar(255) COLLATE "pg_catalog"."default" NOT NULL DEFAULT ''::character varying,
  "approved" bool NOT NULL DEFAULT false,
  "expires_time" timestamp(6) NOT NULL,
  "creator" int8 DEFAULT 0,
  "create_time" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "updater" int8 DEFAULT 0,
  "update_time" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "deleted" int2 NOT NULL DEFAULT 0,
  "tenant_id" int8 NOT NULL DEFAULT 0
)
;
ALTER TABLE "public"."system_oauth2_approve" OWNER TO "postgres";
COMMENT ON COLUMN "public"."system_oauth2_approve"."id" IS '编号';
COMMENT ON COLUMN "public"."system_oauth2_approve"."user_id" IS '用户编号';
COMMENT ON COLUMN "public"."system_oauth2_approve"."user_type" IS '用户类型';
COMMENT ON COLUMN "public"."system_oauth2_approve"."client_id" IS '客户端编号';
COMMENT ON COLUMN "public"."system_oauth2_approve"."scope" IS '授权范围';
COMMENT ON COLUMN "public"."system_oauth2_approve"."approved" IS '是否接受';
COMMENT ON COLUMN "public"."system_oauth2_approve"."expires_time" IS '过期时间';
COMMENT ON COLUMN "public"."system_oauth2_approve"."creator" IS '创建者';
COMMENT ON COLUMN "public"."system_oauth2_approve"."create_time" IS '创建时间';
COMMENT ON COLUMN "public"."system_oauth2_approve"."updater" IS '更新者';
COMMENT ON COLUMN "public"."system_oauth2_approve"."update_time" IS '更新时间';
COMMENT ON COLUMN "public"."system_oauth2_approve"."deleted" IS '是否删除';
COMMENT ON COLUMN "public"."system_oauth2_approve"."tenant_id" IS '租户编号';
COMMENT ON TABLE "public"."system_oauth2_approve" IS 'OAuth2 批准表';

-- ----------------------------
-- Table structure for system_oauth2_client
-- ----------------------------
DROP TABLE IF EXISTS "public"."system_oauth2_client";
CREATE TABLE "public"."system_oauth2_client" (
  "id" int8 NOT NULL,
  "client_id" varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
  "secret" varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
  "name" varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
  "logo" varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
  "description" varchar(255) COLLATE "pg_catalog"."default" DEFAULT NULL::character varying,
  "status" int2 NOT NULL,
  "access_token_validity_seconds" int4 NOT NULL,
  "refresh_token_validity_seconds" int4 NOT NULL,
  "redirect_uris" varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
  "authorized_grant_types" varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
  "scopes" varchar(255) COLLATE "pg_catalog"."default" DEFAULT NULL::character varying,
  "auto_approve_scopes" varchar(255) COLLATE "pg_catalog"."default" DEFAULT NULL::character varying,
  "authorities" varchar(255) COLLATE "pg_catalog"."default" DEFAULT NULL::character varying,
  "resource_ids" varchar(255) COLLATE "pg_catalog"."default" DEFAULT NULL::character varying,
  "additional_information" varchar(4096) COLLATE "pg_catalog"."default" DEFAULT NULL::character varying,
  "creator" int8 DEFAULT 0,
  "create_time" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "updater" int8 DEFAULT 0,
  "update_time" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "deleted" int2 NOT NULL DEFAULT 0
)
;
ALTER TABLE "public"."system_oauth2_client" OWNER TO "postgres";
COMMENT ON COLUMN "public"."system_oauth2_client"."id" IS '编号';
COMMENT ON COLUMN "public"."system_oauth2_client"."client_id" IS '客户端编号';
COMMENT ON COLUMN "public"."system_oauth2_client"."secret" IS '客户端密钥';
COMMENT ON COLUMN "public"."system_oauth2_client"."name" IS '应用名';
COMMENT ON COLUMN "public"."system_oauth2_client"."logo" IS '应用图标';
COMMENT ON COLUMN "public"."system_oauth2_client"."description" IS '应用描述';
COMMENT ON COLUMN "public"."system_oauth2_client"."status" IS '状态';
COMMENT ON COLUMN "public"."system_oauth2_client"."access_token_validity_seconds" IS '访问令牌的有效期';
COMMENT ON COLUMN "public"."system_oauth2_client"."refresh_token_validity_seconds" IS '刷新令牌的有效期';
COMMENT ON COLUMN "public"."system_oauth2_client"."redirect_uris" IS '可重定向的 URI 地址';
COMMENT ON COLUMN "public"."system_oauth2_client"."authorized_grant_types" IS '授权类型';
COMMENT ON COLUMN "public"."system_oauth2_client"."scopes" IS '授权范围';
COMMENT ON COLUMN "public"."system_oauth2_client"."auto_approve_scopes" IS '自动通过的授权范围';
COMMENT ON COLUMN "public"."system_oauth2_client"."authorities" IS '权限';
COMMENT ON COLUMN "public"."system_oauth2_client"."resource_ids" IS '资源';
COMMENT ON COLUMN "public"."system_oauth2_client"."additional_information" IS '附加信息';
COMMENT ON COLUMN "public"."system_oauth2_client"."creator" IS '创建者';
COMMENT ON COLUMN "public"."system_oauth2_client"."create_time" IS '创建时间';
COMMENT ON COLUMN "public"."system_oauth2_client"."updater" IS '更新者';
COMMENT ON COLUMN "public"."system_oauth2_client"."update_time" IS '更新时间';
COMMENT ON COLUMN "public"."system_oauth2_client"."deleted" IS '是否删除';
COMMENT ON TABLE "public"."system_oauth2_client" IS 'OAuth2 客户端表';

-- ----------------------------
-- Table structure for system_oauth2_code
-- ----------------------------
DROP TABLE IF EXISTS "public"."system_oauth2_code";
CREATE TABLE "public"."system_oauth2_code" (
  "id" int8 NOT NULL,
  "user_id" int8 NOT NULL,
  "user_type" int2 NOT NULL,
  "code" varchar(32) COLLATE "pg_catalog"."default" NOT NULL,
  "client_id" varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
  "scopes" varchar(255) COLLATE "pg_catalog"."default" DEFAULT ''::character varying,
  "expires_time" timestamp(6) NOT NULL,
  "redirect_uri" varchar(255) COLLATE "pg_catalog"."default" DEFAULT NULL::character varying,
  "state" varchar(255) COLLATE "pg_catalog"."default" NOT NULL DEFAULT ''::character varying,
  "creator" int8 DEFAULT 0,
  "create_time" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "updater" int8 DEFAULT 0,
  "update_time" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "deleted" int2 NOT NULL DEFAULT 0,
  "tenant_id" int8 NOT NULL DEFAULT 1
)
;
ALTER TABLE "public"."system_oauth2_code" OWNER TO "postgres";
COMMENT ON COLUMN "public"."system_oauth2_code"."id" IS '编号';
COMMENT ON COLUMN "public"."system_oauth2_code"."user_id" IS '用户编号';
COMMENT ON COLUMN "public"."system_oauth2_code"."user_type" IS '用户类型';
COMMENT ON COLUMN "public"."system_oauth2_code"."code" IS '授权码';
COMMENT ON COLUMN "public"."system_oauth2_code"."client_id" IS '客户端编号';
COMMENT ON COLUMN "public"."system_oauth2_code"."scopes" IS '授权范围';
COMMENT ON COLUMN "public"."system_oauth2_code"."expires_time" IS '过期时间';
COMMENT ON COLUMN "public"."system_oauth2_code"."redirect_uri" IS '可重定向的 URI 地址';
COMMENT ON COLUMN "public"."system_oauth2_code"."state" IS '状态';
COMMENT ON COLUMN "public"."system_oauth2_code"."creator" IS '创建者';
COMMENT ON COLUMN "public"."system_oauth2_code"."create_time" IS '创建时间';
COMMENT ON COLUMN "public"."system_oauth2_code"."updater" IS '更新者';
COMMENT ON COLUMN "public"."system_oauth2_code"."update_time" IS '更新时间';
COMMENT ON COLUMN "public"."system_oauth2_code"."deleted" IS '是否删除';
COMMENT ON COLUMN "public"."system_oauth2_code"."tenant_id" IS '租户编号';
COMMENT ON TABLE "public"."system_oauth2_code" IS 'OAuth2 授权码表';

-- ----------------------------
-- Table structure for system_oauth2_refresh_token
-- ----------------------------
DROP TABLE IF EXISTS "public"."system_oauth2_refresh_token";
CREATE TABLE "public"."system_oauth2_refresh_token" (
  "id" int8 NOT NULL,
  "user_id" int8 NOT NULL,
  "refresh_token" varchar(32) COLLATE "pg_catalog"."default" NOT NULL,
  "user_type" int2 NOT NULL,
  "client_id" varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
  "scopes" varchar(255) COLLATE "pg_catalog"."default" DEFAULT NULL::character varying,
  "expires_time" timestamp(6) NOT NULL,
  "creator" int8,
  "create_time" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "updater" int8,
  "update_time" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "deleted" int2 NOT NULL DEFAULT 0,
  "tenant_id" int8 NOT NULL DEFAULT 0
)
;
ALTER TABLE "public"."system_oauth2_refresh_token" OWNER TO "postgres";
COMMENT ON COLUMN "public"."system_oauth2_refresh_token"."id" IS '编号';
COMMENT ON COLUMN "public"."system_oauth2_refresh_token"."user_id" IS '用户编号';
COMMENT ON COLUMN "public"."system_oauth2_refresh_token"."refresh_token" IS '刷新令牌';
COMMENT ON COLUMN "public"."system_oauth2_refresh_token"."user_type" IS '用户类型';
COMMENT ON COLUMN "public"."system_oauth2_refresh_token"."client_id" IS '客户端编号';
COMMENT ON COLUMN "public"."system_oauth2_refresh_token"."scopes" IS '授权范围';
COMMENT ON COLUMN "public"."system_oauth2_refresh_token"."expires_time" IS '过期时间';
COMMENT ON COLUMN "public"."system_oauth2_refresh_token"."creator" IS '创建者';
COMMENT ON COLUMN "public"."system_oauth2_refresh_token"."create_time" IS '创建时间';
COMMENT ON COLUMN "public"."system_oauth2_refresh_token"."updater" IS '更新者';
COMMENT ON COLUMN "public"."system_oauth2_refresh_token"."update_time" IS '更新时间';
COMMENT ON COLUMN "public"."system_oauth2_refresh_token"."deleted" IS '是否删除';
COMMENT ON COLUMN "public"."system_oauth2_refresh_token"."tenant_id" IS '租户编号';
COMMENT ON TABLE "public"."system_oauth2_refresh_token" IS 'OAuth2 刷新令牌';

-- ----------------------------
-- Table structure for system_operate_log
-- ----------------------------
DROP TABLE IF EXISTS "public"."system_operate_log";
CREATE TABLE "public"."system_operate_log" (
  "id" int8 NOT NULL,
  "trace_id" varchar(64) COLLATE "pg_catalog"."default" NOT NULL DEFAULT ''::character varying,
  "user_id" int8 NOT NULL,
  "user_type" int2 NOT NULL DEFAULT 0,
  "type" varchar(50) COLLATE "pg_catalog"."default" NOT NULL,
  "sub_type" varchar(50) COLLATE "pg_catalog"."default" NOT NULL,
  "biz_id" int8 NOT NULL,
  "action" varchar(2000) COLLATE "pg_catalog"."default" NOT NULL DEFAULT ''::character varying,
  "extra" varchar(2000) COLLATE "pg_catalog"."default" DEFAULT ''::character varying,
  "request_method" varchar(16) COLLATE "pg_catalog"."default" DEFAULT ''::character varying,
  "request_url" varchar(255) COLLATE "pg_catalog"."default" DEFAULT ''::character varying,
  "user_ip" varchar(50) COLLATE "pg_catalog"."default" DEFAULT NULL::character varying,
  "user_agent" varchar(200) COLLATE "pg_catalog"."default" DEFAULT NULL::character varying,
  "creator" int8 DEFAULT 0,
  "create_time" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "updater" int8 DEFAULT 0,
  "update_time" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "deleted" int2 NOT NULL DEFAULT 0,
  "tenant_id" int8 NOT NULL
)
;
ALTER TABLE "public"."system_operate_log" OWNER TO "postgres";
COMMENT ON COLUMN "public"."system_operate_log"."id" IS '日志主键';
COMMENT ON COLUMN "public"."system_operate_log"."trace_id" IS '链路追踪编号';
COMMENT ON COLUMN "public"."system_operate_log"."user_id" IS '用户编号';
COMMENT ON COLUMN "public"."system_operate_log"."user_type" IS '用户类型';
COMMENT ON COLUMN "public"."system_operate_log"."type" IS '操作模块类型';
COMMENT ON COLUMN "public"."system_operate_log"."sub_type" IS '操作名';
COMMENT ON COLUMN "public"."system_operate_log"."biz_id" IS '操作数据模块编号';
COMMENT ON COLUMN "public"."system_operate_log"."action" IS '操作内容';
COMMENT ON COLUMN "public"."system_operate_log"."extra" IS '拓展字段';
COMMENT ON COLUMN "public"."system_operate_log"."request_method" IS '请求方法名';
COMMENT ON COLUMN "public"."system_operate_log"."request_url" IS '请求地址';
COMMENT ON COLUMN "public"."system_operate_log"."user_ip" IS '用户 IP';
COMMENT ON COLUMN "public"."system_operate_log"."user_agent" IS '浏览器 UA';
COMMENT ON COLUMN "public"."system_operate_log"."creator" IS '创建者';
COMMENT ON COLUMN "public"."system_operate_log"."create_time" IS '创建时间';
COMMENT ON COLUMN "public"."system_operate_log"."updater" IS '更新者';
COMMENT ON COLUMN "public"."system_operate_log"."update_time" IS '更新时间';
COMMENT ON COLUMN "public"."system_operate_log"."deleted" IS '是否删除';
COMMENT ON COLUMN "public"."system_operate_log"."tenant_id" IS '租户编号';
COMMENT ON TABLE "public"."system_operate_log" IS '操作日志记录 V2 版本';

-- ----------------------------
-- Table structure for system_post
-- ----------------------------
DROP TABLE IF EXISTS "public"."system_post";
CREATE TABLE "public"."system_post" (
  "id" int8 NOT NULL,
  "code" varchar(64) COLLATE "pg_catalog"."default" NOT NULL,
  "name" varchar(50) COLLATE "pg_catalog"."default" NOT NULL,
  "sort" int4 NOT NULL,
  "status" int2 NOT NULL,
  "remark" varchar(500) COLLATE "pg_catalog"."default" DEFAULT NULL::character varying,
  "creator" int8 DEFAULT 0,
  "create_time" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "updater" int8 DEFAULT 0,
  "update_time" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "deleted" int2 NOT NULL DEFAULT 0,
  "tenant_id" int8 NOT NULL DEFAULT 0
)
;
ALTER TABLE "public"."system_post" OWNER TO "postgres";
COMMENT ON COLUMN "public"."system_post"."id" IS '岗位ID';
COMMENT ON COLUMN "public"."system_post"."code" IS '岗位编码';
COMMENT ON COLUMN "public"."system_post"."name" IS '岗位名称';
COMMENT ON COLUMN "public"."system_post"."sort" IS '显示顺序';
COMMENT ON COLUMN "public"."system_post"."status" IS '状态（0正常 1停用）';
COMMENT ON COLUMN "public"."system_post"."remark" IS '备注';
COMMENT ON COLUMN "public"."system_post"."creator" IS '创建者';
COMMENT ON COLUMN "public"."system_post"."create_time" IS '创建时间';
COMMENT ON COLUMN "public"."system_post"."updater" IS '更新者';
COMMENT ON COLUMN "public"."system_post"."update_time" IS '更新时间';
COMMENT ON COLUMN "public"."system_post"."deleted" IS '是否删除';
COMMENT ON COLUMN "public"."system_post"."tenant_id" IS '租户编号';
COMMENT ON TABLE "public"."system_post" IS '岗位信息表';

-- ----------------------------
-- Table structure for system_role
-- ----------------------------
DROP TABLE IF EXISTS "public"."system_role";
CREATE TABLE "public"."system_role" (
  "id" int8 NOT NULL,
  "name" varchar(30) COLLATE "pg_catalog"."default" NOT NULL,
  "code" varchar(100) COLLATE "pg_catalog"."default" NOT NULL,
  "sort" int4 NOT NULL,
  "data_scope" int2 NOT NULL DEFAULT 1,
  "data_scope_dept_ids" json,
  "status" int2 NOT NULL,
  "type" int2 NOT NULL,
  "remark" varchar(500) COLLATE "pg_catalog"."default" DEFAULT NULL::character varying,
  "creator" int8 DEFAULT 0,
  "create_time" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "updater" int8 DEFAULT 0,
  "update_time" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "deleted" int2 NOT NULL DEFAULT 0,
  "tenant_id" int8 NOT NULL
)
;
ALTER TABLE "public"."system_role" OWNER TO "postgres";
COMMENT ON COLUMN "public"."system_role"."id" IS '角色ID';
COMMENT ON COLUMN "public"."system_role"."name" IS '角色名称';
COMMENT ON COLUMN "public"."system_role"."code" IS '角色权限字符串';
COMMENT ON COLUMN "public"."system_role"."sort" IS '显示顺序';
COMMENT ON COLUMN "public"."system_role"."data_scope" IS '数据范围（1：全部数据权限 2：自定数据权限 3：本部门数据权限 4：本部门及以下数据权限）';
COMMENT ON COLUMN "public"."system_role"."data_scope_dept_ids" IS '数据范围(指定部门数组)';
COMMENT ON COLUMN "public"."system_role"."status" IS '角色状态（0正常 1停用）';
COMMENT ON COLUMN "public"."system_role"."type" IS '角色类型';
COMMENT ON COLUMN "public"."system_role"."remark" IS '备注';
COMMENT ON COLUMN "public"."system_role"."creator" IS '创建者';
COMMENT ON COLUMN "public"."system_role"."create_time" IS '创建时间';
COMMENT ON COLUMN "public"."system_role"."updater" IS '更新者';
COMMENT ON COLUMN "public"."system_role"."update_time" IS '更新时间';
COMMENT ON COLUMN "public"."system_role"."deleted" IS '是否删除';
COMMENT ON COLUMN "public"."system_role"."tenant_id" IS '租户编号';
COMMENT ON TABLE "public"."system_role" IS '角色信息表';

-- ----------------------------
-- Table structure for system_role_menu
-- ----------------------------
DROP TABLE IF EXISTS "public"."system_role_menu";
CREATE TABLE "public"."system_role_menu" (
  "id" int8 NOT NULL DEFAULT nextval('system_role_menu_seq'::regclass),
  "role_id" int8 NOT NULL,
  "menu_id" int8 NOT NULL,
  "creator" varchar(64) COLLATE "pg_catalog"."default" DEFAULT ''::character varying,
  "create_time" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "updater" varchar(64) COLLATE "pg_catalog"."default" DEFAULT ''::character varying,
  "update_time" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "deleted" int2 NOT NULL DEFAULT 0,
  "tenant_id" int8 NOT NULL
)
;
ALTER TABLE "public"."system_role_menu" OWNER TO "postgres";
COMMENT ON COLUMN "public"."system_role_menu"."id" IS '自增编号';
COMMENT ON COLUMN "public"."system_role_menu"."role_id" IS '角色ID';
COMMENT ON COLUMN "public"."system_role_menu"."menu_id" IS '菜单ID';
COMMENT ON COLUMN "public"."system_role_menu"."creator" IS '创建者';
COMMENT ON COLUMN "public"."system_role_menu"."create_time" IS '创建时间';
COMMENT ON COLUMN "public"."system_role_menu"."updater" IS '更新者';
COMMENT ON COLUMN "public"."system_role_menu"."update_time" IS '更新时间';
COMMENT ON COLUMN "public"."system_role_menu"."deleted" IS '是否删除';
COMMENT ON COLUMN "public"."system_role_menu"."tenant_id" IS '租户编号';
COMMENT ON TABLE "public"."system_role_menu" IS '角色和菜单关联表';

-- ----------------------------
-- Table structure for system_sms_channel
-- ----------------------------
DROP TABLE IF EXISTS "public"."system_sms_channel";
CREATE TABLE "public"."system_sms_channel" (
  "id" int8 NOT NULL,
  "signature" varchar(12) COLLATE "pg_catalog"."default" NOT NULL,
  "code" varchar(63) COLLATE "pg_catalog"."default" NOT NULL,
  "status" int2 NOT NULL,
  "remark" varchar(255) COLLATE "pg_catalog"."default" DEFAULT NULL::character varying,
  "api_key" varchar(128) COLLATE "pg_catalog"."default" NOT NULL,
  "api_secret" varchar(128) COLLATE "pg_catalog"."default" DEFAULT NULL::character varying,
  "callback_url" varchar(255) COLLATE "pg_catalog"."default" DEFAULT NULL::character varying,
  "creator" int8 DEFAULT 0,
  "create_time" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "updater" int8 DEFAULT 0,
  "update_time" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "deleted" int2 NOT NULL DEFAULT 0
)
;
ALTER TABLE "public"."system_sms_channel" OWNER TO "postgres";
COMMENT ON COLUMN "public"."system_sms_channel"."id" IS '编号';
COMMENT ON COLUMN "public"."system_sms_channel"."signature" IS '短信签名';
COMMENT ON COLUMN "public"."system_sms_channel"."code" IS '渠道编码';
COMMENT ON COLUMN "public"."system_sms_channel"."status" IS '开启状态';
COMMENT ON COLUMN "public"."system_sms_channel"."remark" IS '备注';
COMMENT ON COLUMN "public"."system_sms_channel"."api_key" IS '短信 API 的账号';
COMMENT ON COLUMN "public"."system_sms_channel"."api_secret" IS '短信 API 的秘钥';
COMMENT ON COLUMN "public"."system_sms_channel"."callback_url" IS '短信发送回调 URL';
COMMENT ON COLUMN "public"."system_sms_channel"."creator" IS '创建者';
COMMENT ON COLUMN "public"."system_sms_channel"."create_time" IS '创建时间';
COMMENT ON COLUMN "public"."system_sms_channel"."updater" IS '更新者';
COMMENT ON COLUMN "public"."system_sms_channel"."update_time" IS '更新时间';
COMMENT ON COLUMN "public"."system_sms_channel"."deleted" IS '是否删除';
COMMENT ON TABLE "public"."system_sms_channel" IS '短信渠道';

-- ----------------------------
-- Table structure for system_sms_code
-- ----------------------------
DROP TABLE IF EXISTS "public"."system_sms_code";
CREATE TABLE "public"."system_sms_code" (
  "id" int8 NOT NULL,
  "mobile" varchar(11) COLLATE "pg_catalog"."default" NOT NULL,
  "code" varchar(6) COLLATE "pg_catalog"."default" NOT NULL,
  "create_ip" varchar(15) COLLATE "pg_catalog"."default" NOT NULL,
  "scene" int2 NOT NULL,
  "today_index" int2 NOT NULL,
  "used" int2 NOT NULL,
  "used_time" timestamp(6),
  "used_ip" varchar(255) COLLATE "pg_catalog"."default" DEFAULT NULL::character varying,
  "creator" int8 DEFAULT 0,
  "create_time" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "updater" int8 DEFAULT 0,
  "update_time" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "deleted" int2 NOT NULL DEFAULT 0,
  "tenant_id" int8 NOT NULL DEFAULT 0
)
;
ALTER TABLE "public"."system_sms_code" OWNER TO "postgres";
COMMENT ON COLUMN "public"."system_sms_code"."id" IS '编号';
COMMENT ON COLUMN "public"."system_sms_code"."mobile" IS '手机号';
COMMENT ON COLUMN "public"."system_sms_code"."code" IS '验证码';
COMMENT ON COLUMN "public"."system_sms_code"."create_ip" IS '创建 IP';
COMMENT ON COLUMN "public"."system_sms_code"."scene" IS '发送场景';
COMMENT ON COLUMN "public"."system_sms_code"."today_index" IS '今日发送的第几条';
COMMENT ON COLUMN "public"."system_sms_code"."used" IS '是否使用';
COMMENT ON COLUMN "public"."system_sms_code"."used_time" IS '使用时间';
COMMENT ON COLUMN "public"."system_sms_code"."used_ip" IS '使用 IP';
COMMENT ON COLUMN "public"."system_sms_code"."creator" IS '创建者';
COMMENT ON COLUMN "public"."system_sms_code"."create_time" IS '创建时间';
COMMENT ON COLUMN "public"."system_sms_code"."updater" IS '更新者';
COMMENT ON COLUMN "public"."system_sms_code"."update_time" IS '更新时间';
COMMENT ON COLUMN "public"."system_sms_code"."deleted" IS '是否删除';
COMMENT ON COLUMN "public"."system_sms_code"."tenant_id" IS '租户编号';
COMMENT ON TABLE "public"."system_sms_code" IS '手机验证码';

-- ----------------------------
-- Table structure for system_sms_log
-- ----------------------------
DROP TABLE IF EXISTS "public"."system_sms_log";
CREATE TABLE "public"."system_sms_log" (
  "id" int8 NOT NULL,
  "channel_id" int8 NOT NULL,
  "channel_code" varchar(63) COLLATE "pg_catalog"."default" NOT NULL,
  "template_id" int8 NOT NULL,
  "template_code" varchar(63) COLLATE "pg_catalog"."default" NOT NULL,
  "template_type" int2 NOT NULL,
  "template_content" varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
  "template_params" varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
  "api_template_id" varchar(63) COLLATE "pg_catalog"."default" NOT NULL,
  "mobile" varchar(11) COLLATE "pg_catalog"."default" NOT NULL,
  "user_id" int8,
  "user_type" int2,
  "send_status" int2 NOT NULL DEFAULT 0,
  "send_time" timestamp(6),
  "api_send_code" varchar(63) COLLATE "pg_catalog"."default" DEFAULT NULL::character varying,
  "api_send_msg" varchar(255) COLLATE "pg_catalog"."default" DEFAULT NULL::character varying,
  "api_request_id" varchar(255) COLLATE "pg_catalog"."default" DEFAULT NULL::character varying,
  "api_serial_no" varchar(255) COLLATE "pg_catalog"."default" DEFAULT NULL::character varying,
  "receive_status" int2 NOT NULL DEFAULT 0,
  "receive_time" timestamp(6),
  "api_receive_code" varchar(63) COLLATE "pg_catalog"."default" DEFAULT NULL::character varying,
  "api_receive_msg" varchar(255) COLLATE "pg_catalog"."default" DEFAULT NULL::character varying,
  "creator" int8 DEFAULT 0,
  "create_time" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "updater" int8 DEFAULT 0,
  "update_time" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "deleted" int2 NOT NULL DEFAULT 0
)
;
ALTER TABLE "public"."system_sms_log" OWNER TO "postgres";
COMMENT ON COLUMN "public"."system_sms_log"."id" IS '编号';
COMMENT ON COLUMN "public"."system_sms_log"."channel_id" IS '短信渠道编号';
COMMENT ON COLUMN "public"."system_sms_log"."channel_code" IS '短信渠道编码';
COMMENT ON COLUMN "public"."system_sms_log"."template_id" IS '模板编号';
COMMENT ON COLUMN "public"."system_sms_log"."template_code" IS '模板编码';
COMMENT ON COLUMN "public"."system_sms_log"."template_type" IS '短信类型';
COMMENT ON COLUMN "public"."system_sms_log"."template_content" IS '短信内容';
COMMENT ON COLUMN "public"."system_sms_log"."template_params" IS '短信参数';
COMMENT ON COLUMN "public"."system_sms_log"."api_template_id" IS '短信 API 的模板编号';
COMMENT ON COLUMN "public"."system_sms_log"."mobile" IS '手机号';
COMMENT ON COLUMN "public"."system_sms_log"."user_id" IS '用户编号';
COMMENT ON COLUMN "public"."system_sms_log"."user_type" IS '用户类型';
COMMENT ON COLUMN "public"."system_sms_log"."send_status" IS '发送状态';
COMMENT ON COLUMN "public"."system_sms_log"."send_time" IS '发送时间';
COMMENT ON COLUMN "public"."system_sms_log"."api_send_code" IS '短信 API 发送结果的编码';
COMMENT ON COLUMN "public"."system_sms_log"."api_send_msg" IS '短信 API 发送失败的提示';
COMMENT ON COLUMN "public"."system_sms_log"."api_request_id" IS '短信 API 发送返回的唯一请求 ID';
COMMENT ON COLUMN "public"."system_sms_log"."api_serial_no" IS '短信 API 发送返回的序号';
COMMENT ON COLUMN "public"."system_sms_log"."receive_status" IS '接收状态';
COMMENT ON COLUMN "public"."system_sms_log"."receive_time" IS '接收时间';
COMMENT ON COLUMN "public"."system_sms_log"."api_receive_code" IS 'API 接收结果的编码';
COMMENT ON COLUMN "public"."system_sms_log"."api_receive_msg" IS 'API 接收结果的说明';
COMMENT ON COLUMN "public"."system_sms_log"."creator" IS '创建者';
COMMENT ON COLUMN "public"."system_sms_log"."create_time" IS '创建时间';
COMMENT ON COLUMN "public"."system_sms_log"."updater" IS '更新者';
COMMENT ON COLUMN "public"."system_sms_log"."update_time" IS '更新时间';
COMMENT ON COLUMN "public"."system_sms_log"."deleted" IS '是否删除';
COMMENT ON TABLE "public"."system_sms_log" IS '短信日志';

-- ----------------------------
-- Table structure for system_sms_template
-- ----------------------------
DROP TABLE IF EXISTS "public"."system_sms_template";
CREATE TABLE "public"."system_sms_template" (
  "id" int8 NOT NULL,
  "type" int2 NOT NULL,
  "status" int2 NOT NULL,
  "code" varchar(63) COLLATE "pg_catalog"."default" NOT NULL,
  "name" varchar(63) COLLATE "pg_catalog"."default" NOT NULL,
  "content" varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
  "params" varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
  "remark" varchar(255) COLLATE "pg_catalog"."default" DEFAULT NULL::character varying,
  "api_template_id" varchar(63) COLLATE "pg_catalog"."default" NOT NULL,
  "channel_id" int8 NOT NULL,
  "channel_code" varchar(63) COLLATE "pg_catalog"."default" NOT NULL,
  "creator" int8 DEFAULT 0,
  "create_time" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "updater" int8 DEFAULT 0,
  "update_time" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "deleted" int2 NOT NULL DEFAULT 0
)
;
ALTER TABLE "public"."system_sms_template" OWNER TO "postgres";
COMMENT ON COLUMN "public"."system_sms_template"."id" IS '编号';
COMMENT ON COLUMN "public"."system_sms_template"."type" IS '模板类型';
COMMENT ON COLUMN "public"."system_sms_template"."status" IS '开启状态';
COMMENT ON COLUMN "public"."system_sms_template"."code" IS '模板编码';
COMMENT ON COLUMN "public"."system_sms_template"."name" IS '模板名称';
COMMENT ON COLUMN "public"."system_sms_template"."content" IS '模板内容';
COMMENT ON COLUMN "public"."system_sms_template"."params" IS '参数数组';
COMMENT ON COLUMN "public"."system_sms_template"."remark" IS '备注';
COMMENT ON COLUMN "public"."system_sms_template"."api_template_id" IS '短信 API 的模板编号';
COMMENT ON COLUMN "public"."system_sms_template"."channel_id" IS '短信渠道编号';
COMMENT ON COLUMN "public"."system_sms_template"."channel_code" IS '短信渠道编码';
COMMENT ON COLUMN "public"."system_sms_template"."creator" IS '创建者';
COMMENT ON COLUMN "public"."system_sms_template"."create_time" IS '创建时间';
COMMENT ON COLUMN "public"."system_sms_template"."updater" IS '更新者';
COMMENT ON COLUMN "public"."system_sms_template"."update_time" IS '更新时间';
COMMENT ON COLUMN "public"."system_sms_template"."deleted" IS '是否删除';
COMMENT ON TABLE "public"."system_sms_template" IS '短信模板';

-- ----------------------------
-- Table structure for system_social_client
-- ----------------------------
DROP TABLE IF EXISTS "public"."system_social_client";
CREATE TABLE "public"."system_social_client" (
  "id" int8 NOT NULL,
  "name" varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
  "social_type" int2 NOT NULL,
  "user_type" int2 NOT NULL,
  "client_id" varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
  "client_secret" varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
  "agent_id" varchar(255) COLLATE "pg_catalog"."default" DEFAULT NULL::character varying,
  "status" int2 NOT NULL,
  "creator" int8 DEFAULT 0,
  "create_time" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "updater" int8 DEFAULT 0,
  "update_time" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "deleted" int2 NOT NULL DEFAULT 0,
  "tenant_id" int8 NOT NULL DEFAULT 0
)
;
ALTER TABLE "public"."system_social_client" OWNER TO "postgres";
COMMENT ON COLUMN "public"."system_social_client"."id" IS '编号';
COMMENT ON COLUMN "public"."system_social_client"."name" IS '应用名';
COMMENT ON COLUMN "public"."system_social_client"."social_type" IS '社交平台的类型';
COMMENT ON COLUMN "public"."system_social_client"."user_type" IS '用户类型';
COMMENT ON COLUMN "public"."system_social_client"."client_id" IS '客户端编号';
COMMENT ON COLUMN "public"."system_social_client"."client_secret" IS '客户端密钥';
COMMENT ON COLUMN "public"."system_social_client"."agent_id" IS '代理编号';
COMMENT ON COLUMN "public"."system_social_client"."status" IS '状态';
COMMENT ON COLUMN "public"."system_social_client"."creator" IS '创建者';
COMMENT ON COLUMN "public"."system_social_client"."create_time" IS '创建时间';
COMMENT ON COLUMN "public"."system_social_client"."updater" IS '更新者';
COMMENT ON COLUMN "public"."system_social_client"."update_time" IS '更新时间';
COMMENT ON COLUMN "public"."system_social_client"."deleted" IS '是否删除';
COMMENT ON COLUMN "public"."system_social_client"."tenant_id" IS '租户编号';
COMMENT ON TABLE "public"."system_social_client" IS '社交客户端表';

-- ----------------------------
-- Table structure for system_social_user
-- ----------------------------
DROP TABLE IF EXISTS "public"."system_social_user";
CREATE TABLE "public"."system_social_user" (
  "id" int8 NOT NULL,
  "type" int2 NOT NULL,
  "openid" varchar(32) COLLATE "pg_catalog"."default" NOT NULL,
  "token" varchar(256) COLLATE "pg_catalog"."default" DEFAULT NULL::character varying,
  "raw_token_info" varchar(1024) COLLATE "pg_catalog"."default" NOT NULL,
  "nickname" varchar(32) COLLATE "pg_catalog"."default" NOT NULL,
  "avatar" varchar(255) COLLATE "pg_catalog"."default" DEFAULT NULL::character varying,
  "raw_user_info" varchar(1024) COLLATE "pg_catalog"."default" NOT NULL,
  "code" varchar(256) COLLATE "pg_catalog"."default" NOT NULL,
  "state" varchar(256) COLLATE "pg_catalog"."default" DEFAULT NULL::character varying,
  "creator" int8 DEFAULT 0,
  "create_time" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "updater" int8 DEFAULT 0,
  "update_time" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "deleted" int2 NOT NULL DEFAULT 0,
  "tenant_id" int8 NOT NULL DEFAULT 0
)
;
ALTER TABLE "public"."system_social_user" OWNER TO "postgres";
COMMENT ON COLUMN "public"."system_social_user"."id" IS '主键(自增策略)';
COMMENT ON COLUMN "public"."system_social_user"."type" IS '社交平台的类型';
COMMENT ON COLUMN "public"."system_social_user"."openid" IS '社交 openid';
COMMENT ON COLUMN "public"."system_social_user"."token" IS '社交 token';
COMMENT ON COLUMN "public"."system_social_user"."raw_token_info" IS '原始 Token 数据，一般是 JSON 格式';
COMMENT ON COLUMN "public"."system_social_user"."nickname" IS '用户昵称';
COMMENT ON COLUMN "public"."system_social_user"."avatar" IS '用户头像';
COMMENT ON COLUMN "public"."system_social_user"."raw_user_info" IS '原始用户数据，一般是 JSON 格式';
COMMENT ON COLUMN "public"."system_social_user"."code" IS '最后一次的认证 code';
COMMENT ON COLUMN "public"."system_social_user"."state" IS '最后一次的认证 state';
COMMENT ON COLUMN "public"."system_social_user"."creator" IS '创建者';
COMMENT ON COLUMN "public"."system_social_user"."create_time" IS '创建时间';
COMMENT ON COLUMN "public"."system_social_user"."updater" IS '更新者';
COMMENT ON COLUMN "public"."system_social_user"."update_time" IS '更新时间';
COMMENT ON COLUMN "public"."system_social_user"."deleted" IS '是否删除';
COMMENT ON COLUMN "public"."system_social_user"."tenant_id" IS '租户编号';
COMMENT ON TABLE "public"."system_social_user" IS '社交用户表';

-- ----------------------------
-- Table structure for system_social_user_bind
-- ----------------------------
DROP TABLE IF EXISTS "public"."system_social_user_bind";
CREATE TABLE "public"."system_social_user_bind" (
  "id" int8 NOT NULL,
  "user_id" int8 NOT NULL,
  "user_type" int2 NOT NULL,
  "social_type" int2 NOT NULL,
  "social_user_id" int8 NOT NULL,
  "creator" int8 DEFAULT 0,
  "create_time" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "updater" int8 DEFAULT 0,
  "update_time" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "deleted" int2 NOT NULL DEFAULT 0,
  "tenant_id" int8 NOT NULL DEFAULT 0
)
;
ALTER TABLE "public"."system_social_user_bind" OWNER TO "postgres";
COMMENT ON COLUMN "public"."system_social_user_bind"."id" IS '主键(自增策略)';
COMMENT ON COLUMN "public"."system_social_user_bind"."user_id" IS '用户编号';
COMMENT ON COLUMN "public"."system_social_user_bind"."user_type" IS '用户类型';
COMMENT ON COLUMN "public"."system_social_user_bind"."social_type" IS '社交平台的类型';
COMMENT ON COLUMN "public"."system_social_user_bind"."social_user_id" IS '社交用户的编号';
COMMENT ON COLUMN "public"."system_social_user_bind"."creator" IS '创建者';
COMMENT ON COLUMN "public"."system_social_user_bind"."create_time" IS '创建时间';
COMMENT ON COLUMN "public"."system_social_user_bind"."updater" IS '更新者';
COMMENT ON COLUMN "public"."system_social_user_bind"."update_time" IS '更新时间';
COMMENT ON COLUMN "public"."system_social_user_bind"."deleted" IS '是否删除';
COMMENT ON COLUMN "public"."system_social_user_bind"."tenant_id" IS '租户编号';
COMMENT ON TABLE "public"."system_social_user_bind" IS '社交绑定表';

-- ----------------------------
-- Table structure for system_tenant
-- ----------------------------
DROP TABLE IF EXISTS "public"."system_tenant";
CREATE TABLE "public"."system_tenant" (
  "id" int8 NOT NULL,
  "name" varchar(64) COLLATE "pg_catalog"."default" NOT NULL,
  "contact_user_id" int8,
  "contact_name" varchar(64) COLLATE "pg_catalog"."default" NOT NULL,
  "contact_mobile" varchar(64) COLLATE "pg_catalog"."default" DEFAULT NULL::character varying,
  "status" int2 NOT NULL DEFAULT 0,
  "website" varchar(256) COLLATE "pg_catalog"."default" DEFAULT ''::character varying,
  "package_id" int8 NOT NULL,
  "expire_time" timestamp(6) NOT NULL,
  "account_count" int4 NOT NULL,
  "creator" int8 NOT NULL,
  "create_time" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "updater" int8 NOT NULL,
  "update_time" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "deleted" int2 NOT NULL DEFAULT 0,
  "allocate_person_count" int4
)
;
ALTER TABLE "public"."system_tenant" OWNER TO "postgres";
COMMENT ON COLUMN "public"."system_tenant"."id" IS '租户编号';
COMMENT ON COLUMN "public"."system_tenant"."name" IS '租户名';
COMMENT ON COLUMN "public"."system_tenant"."contact_user_id" IS '联系人的用户编号';
COMMENT ON COLUMN "public"."system_tenant"."contact_name" IS '联系人';
COMMENT ON COLUMN "public"."system_tenant"."contact_mobile" IS '联系手机';
COMMENT ON COLUMN "public"."system_tenant"."status" IS '租户状态（0正常 1停用）';
COMMENT ON COLUMN "public"."system_tenant"."website" IS '绑定域名';
COMMENT ON COLUMN "public"."system_tenant"."package_id" IS '租户套餐编号';
COMMENT ON COLUMN "public"."system_tenant"."expire_time" IS '过期时间';
COMMENT ON COLUMN "public"."system_tenant"."account_count" IS '账号数量';
COMMENT ON COLUMN "public"."system_tenant"."creator" IS '创建者';
COMMENT ON COLUMN "public"."system_tenant"."create_time" IS '创建时间';
COMMENT ON COLUMN "public"."system_tenant"."updater" IS '更新者';
COMMENT ON COLUMN "public"."system_tenant"."update_time" IS '更新时间';
COMMENT ON COLUMN "public"."system_tenant"."deleted" IS '是否删除';
COMMENT ON COLUMN "public"."system_tenant"."allocate_person_count" IS '分配人员数量';
COMMENT ON TABLE "public"."system_tenant" IS '租户表';

-- ----------------------------
-- Table structure for system_tenant_package
-- ----------------------------
DROP TABLE IF EXISTS "public"."system_tenant_package";
CREATE TABLE "public"."system_tenant_package" (
  "id" int8 NOT NULL,
  "name" varchar(30) COLLATE "pg_catalog"."default" NOT NULL,
  "status" int2 NOT NULL DEFAULT 0,
  "remark" varchar(256) COLLATE "pg_catalog"."default" DEFAULT ''::character varying,
  "creator" int8 NOT NULL,
  "create_time" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "updater" int8,
  "update_time" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "deleted" int2 NOT NULL DEFAULT 0,
  "menu_ids" json
)
;
ALTER TABLE "public"."system_tenant_package" OWNER TO "postgres";
COMMENT ON COLUMN "public"."system_tenant_package"."id" IS '套餐编号';
COMMENT ON COLUMN "public"."system_tenant_package"."name" IS '套餐名';
COMMENT ON COLUMN "public"."system_tenant_package"."status" IS '租户状态（0正常 1停用）';
COMMENT ON COLUMN "public"."system_tenant_package"."remark" IS '备注';
COMMENT ON COLUMN "public"."system_tenant_package"."creator" IS '创建者';
COMMENT ON COLUMN "public"."system_tenant_package"."create_time" IS '创建时间';
COMMENT ON COLUMN "public"."system_tenant_package"."updater" IS '更新者';
COMMENT ON COLUMN "public"."system_tenant_package"."update_time" IS '更新时间';
COMMENT ON COLUMN "public"."system_tenant_package"."deleted" IS '是否删除';
COMMENT ON COLUMN "public"."system_tenant_package"."menu_ids" IS '关联的菜单IDS';
COMMENT ON TABLE "public"."system_tenant_package" IS '租户套餐表';

-- ----------------------------
-- Table structure for system_user_post
-- ----------------------------
DROP TABLE IF EXISTS "public"."system_user_post";
CREATE TABLE "public"."system_user_post" (
  "id" int8 NOT NULL DEFAULT nextval('system_user_post_seq'::regclass),
  "user_id" int8 NOT NULL DEFAULT 0,
  "post_id" int8 NOT NULL DEFAULT 0,
  "creator" varchar(64) COLLATE "pg_catalog"."default" DEFAULT ''::character varying,
  "create_time" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "updater" varchar(64) COLLATE "pg_catalog"."default" DEFAULT ''::character varying,
  "update_time" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "deleted" int2 NOT NULL DEFAULT 0,
  "tenant_id" int8 NOT NULL
)
;
ALTER TABLE "public"."system_user_post" OWNER TO "postgres";
COMMENT ON COLUMN "public"."system_user_post"."id" IS 'id';
COMMENT ON COLUMN "public"."system_user_post"."user_id" IS '用户ID';
COMMENT ON COLUMN "public"."system_user_post"."post_id" IS '岗位ID';
COMMENT ON COLUMN "public"."system_user_post"."creator" IS '创建者';
COMMENT ON COLUMN "public"."system_user_post"."create_time" IS '创建时间';
COMMENT ON COLUMN "public"."system_user_post"."updater" IS '更新者';
COMMENT ON COLUMN "public"."system_user_post"."update_time" IS '更新时间';
COMMENT ON COLUMN "public"."system_user_post"."deleted" IS '是否删除';
COMMENT ON COLUMN "public"."system_user_post"."tenant_id" IS '租户编号';
COMMENT ON TABLE "public"."system_user_post" IS '用户岗位表';

-- ----------------------------
-- Table structure for system_user_role
-- ----------------------------
DROP TABLE IF EXISTS "public"."system_user_role";
CREATE TABLE "public"."system_user_role" (
  "id" int8 NOT NULL DEFAULT nextval('system_user_role_seq'::regclass),
  "user_id" int8 NOT NULL,
  "role_id" int8 NOT NULL,
  "creator" int8,
  "create_time" timestamp(6) DEFAULT CURRENT_TIMESTAMP,
  "updater" int8,
  "update_time" timestamp(6) DEFAULT CURRENT_TIMESTAMP,
  "deleted" int2 NOT NULL DEFAULT 0,
  "tenant_id" int8 NOT NULL
)
;
ALTER TABLE "public"."system_user_role" OWNER TO "postgres";
COMMENT ON COLUMN "public"."system_user_role"."id" IS '自增编号';
COMMENT ON COLUMN "public"."system_user_role"."user_id" IS '用户ID';
COMMENT ON COLUMN "public"."system_user_role"."role_id" IS '角色ID';
COMMENT ON COLUMN "public"."system_user_role"."creator" IS '创建者';
COMMENT ON COLUMN "public"."system_user_role"."create_time" IS '创建时间';
COMMENT ON COLUMN "public"."system_user_role"."updater" IS '更新者';
COMMENT ON COLUMN "public"."system_user_role"."update_time" IS '更新时间';
COMMENT ON COLUMN "public"."system_user_role"."deleted" IS '是否删除';
COMMENT ON COLUMN "public"."system_user_role"."tenant_id" IS '租户编号';
COMMENT ON TABLE "public"."system_user_role" IS '用户和角色关联表';

-- ----------------------------
-- Table structure for system_users
-- ----------------------------
DROP TABLE IF EXISTS "public"."system_users";
CREATE TABLE "public"."system_users" (
  "id" int8 NOT NULL,
  "username" varchar(64) COLLATE "pg_catalog"."default" NOT NULL,
  "password" varchar(128) COLLATE "pg_catalog"."default" NOT NULL DEFAULT ''::character varying,
  "nickname" varchar(64) COLLATE "pg_catalog"."default" NOT NULL,
  "remark" varchar(512) COLLATE "pg_catalog"."default" DEFAULT NULL::character varying,
  "dept_id" int8,
  "post_ids" json,
  "email" varchar(64) COLLATE "pg_catalog"."default" DEFAULT ''::character varying,
  "mobile" varchar(11) COLLATE "pg_catalog"."default" DEFAULT ''::character varying,
  "sex" int2 DEFAULT 0,
  "avatar" varchar(512) COLLATE "pg_catalog"."default" DEFAULT ''::character varying,
  "status" int2 NOT NULL DEFAULT 0,
  "login_ip" varchar(64) COLLATE "pg_catalog"."default" DEFAULT ''::character varying,
  "login_date" timestamp(6),
  "creator" int8 DEFAULT 0,
  "create_time" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "updater" int8 DEFAULT 0,
  "update_time" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "deleted" int2 NOT NULL DEFAULT 0,
  "tenant_id" int8 NOT NULL,
  "user_type" int2
)
;
ALTER TABLE "public"."system_users" OWNER TO "postgres";
COMMENT ON COLUMN "public"."system_users"."id" IS '用户ID';
COMMENT ON COLUMN "public"."system_users"."username" IS '用户账号';
COMMENT ON COLUMN "public"."system_users"."password" IS '密码';
COMMENT ON COLUMN "public"."system_users"."nickname" IS '用户昵称';
COMMENT ON COLUMN "public"."system_users"."remark" IS '备注';
COMMENT ON COLUMN "public"."system_users"."dept_id" IS '部门ID';
COMMENT ON COLUMN "public"."system_users"."post_ids" IS '岗位编号数组';
COMMENT ON COLUMN "public"."system_users"."email" IS '用户邮箱';
COMMENT ON COLUMN "public"."system_users"."mobile" IS '手机号码';
COMMENT ON COLUMN "public"."system_users"."sex" IS '用户性别';
COMMENT ON COLUMN "public"."system_users"."avatar" IS '头像地址';
COMMENT ON COLUMN "public"."system_users"."status" IS '帐号状态（0正常 1停用）';
COMMENT ON COLUMN "public"."system_users"."login_ip" IS '最后登录IP';
COMMENT ON COLUMN "public"."system_users"."login_date" IS '最后登录时间';
COMMENT ON COLUMN "public"."system_users"."creator" IS '创建者';
COMMENT ON COLUMN "public"."system_users"."create_time" IS '创建时间';
COMMENT ON COLUMN "public"."system_users"."updater" IS '更新者';
COMMENT ON COLUMN "public"."system_users"."update_time" IS '更新时间';
COMMENT ON COLUMN "public"."system_users"."deleted" IS '是否删除';
COMMENT ON COLUMN "public"."system_users"."tenant_id" IS '租户编号';
COMMENT ON COLUMN "public"."system_users"."user_type" IS '用户类型';
COMMENT ON TABLE "public"."system_users" IS '用户信息表';

-- ----------------------------
-- Table structure for onebase_demo01_contact
-- ----------------------------
DROP TABLE IF EXISTS "public"."onebase_demo01_contact";
CREATE TABLE "public"."onebase_demo01_contact" (
  "id" int8 NOT NULL,
  "name" varchar(100) COLLATE "pg_catalog"."default" NOT NULL DEFAULT ''::character varying,
  "sex" int2 NOT NULL,
  "birthday" timestamp(6) NOT NULL,
  "description" varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
  "avatar" varchar(512) COLLATE "pg_catalog"."default" DEFAULT NULL::character varying,
  "creator" int8 DEFAULT 0,
  "create_time" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "updater" int8 DEFAULT 0,
  "update_time" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "deleted" int2 NOT NULL DEFAULT 0,
  "tenant_id" int8 NOT NULL DEFAULT 0
)
;
ALTER TABLE "public"."onebase_demo01_contact" OWNER TO "postgres";
COMMENT ON COLUMN "public"."onebase_demo01_contact"."id" IS '编号';
COMMENT ON COLUMN "public"."onebase_demo01_contact"."name" IS '名字';
COMMENT ON COLUMN "public"."onebase_demo01_contact"."sex" IS '性别';
COMMENT ON COLUMN "public"."onebase_demo01_contact"."birthday" IS '出生年';
COMMENT ON COLUMN "public"."onebase_demo01_contact"."description" IS '简介';
COMMENT ON COLUMN "public"."onebase_demo01_contact"."avatar" IS '头像';
COMMENT ON COLUMN "public"."onebase_demo01_contact"."creator" IS '创建者';
COMMENT ON COLUMN "public"."onebase_demo01_contact"."create_time" IS '创建时间';
COMMENT ON COLUMN "public"."onebase_demo01_contact"."updater" IS '更新者';
COMMENT ON COLUMN "public"."onebase_demo01_contact"."update_time" IS '更新时间';
COMMENT ON COLUMN "public"."onebase_demo01_contact"."deleted" IS '是否删除';
COMMENT ON COLUMN "public"."onebase_demo01_contact"."tenant_id" IS '租户编号';
COMMENT ON TABLE "public"."onebase_demo01_contact" IS '示例联系人表';

-- ----------------------------
-- Table structure for onebase_demo02_category
-- ----------------------------
DROP TABLE IF EXISTS "public"."onebase_demo02_category";
CREATE TABLE "public"."onebase_demo02_category" (
  "id" int8 NOT NULL,
  "name" varchar(100) COLLATE "pg_catalog"."default" NOT NULL DEFAULT ''::character varying,
  "parent_id" int8 NOT NULL,
  "creator" int8 DEFAULT 0,
  "create_time" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "updater" int8 DEFAULT 0,
  "update_time" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "deleted" int2 NOT NULL DEFAULT 0,
  "tenant_id" int8 NOT NULL DEFAULT 0
)
;
ALTER TABLE "public"."onebase_demo02_category" OWNER TO "postgres";
COMMENT ON COLUMN "public"."onebase_demo02_category"."id" IS '编号';
COMMENT ON COLUMN "public"."onebase_demo02_category"."name" IS '名字';
COMMENT ON COLUMN "public"."onebase_demo02_category"."parent_id" IS '父级编号';
COMMENT ON COLUMN "public"."onebase_demo02_category"."creator" IS '创建者';
COMMENT ON COLUMN "public"."onebase_demo02_category"."create_time" IS '创建时间';
COMMENT ON COLUMN "public"."onebase_demo02_category"."updater" IS '更新者';
COMMENT ON COLUMN "public"."onebase_demo02_category"."update_time" IS '更新时间';
COMMENT ON COLUMN "public"."onebase_demo02_category"."deleted" IS '是否删除';
COMMENT ON COLUMN "public"."onebase_demo02_category"."tenant_id" IS '租户编号';
COMMENT ON TABLE "public"."onebase_demo02_category" IS '示例分类表';

-- ----------------------------
-- Table structure for onebase_demo03_course
-- ----------------------------
DROP TABLE IF EXISTS "public"."onebase_demo03_course";
CREATE TABLE "public"."onebase_demo03_course" (
  "id" int8 NOT NULL,
  "student_id" int8 NOT NULL,
  "name" varchar(100) COLLATE "pg_catalog"."default" NOT NULL DEFAULT ''::character varying,
  "score" int2 NOT NULL,
  "creator" int8 DEFAULT 0,
  "create_time" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "updater" int8 DEFAULT 0,
  "update_time" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "deleted" int2 NOT NULL DEFAULT 0,
  "tenant_id" int8 NOT NULL DEFAULT 0
)
;
ALTER TABLE "public"."onebase_demo03_course" OWNER TO "postgres";
COMMENT ON COLUMN "public"."onebase_demo03_course"."id" IS '编号';
COMMENT ON COLUMN "public"."onebase_demo03_course"."student_id" IS '学生编号';
COMMENT ON COLUMN "public"."onebase_demo03_course"."name" IS '名字';
COMMENT ON COLUMN "public"."onebase_demo03_course"."score" IS '分数';
COMMENT ON COLUMN "public"."onebase_demo03_course"."creator" IS '创建者';
COMMENT ON COLUMN "public"."onebase_demo03_course"."create_time" IS '创建时间';
COMMENT ON COLUMN "public"."onebase_demo03_course"."updater" IS '更新者';
COMMENT ON COLUMN "public"."onebase_demo03_course"."update_time" IS '更新时间';
COMMENT ON COLUMN "public"."onebase_demo03_course"."deleted" IS '是否删除';
COMMENT ON COLUMN "public"."onebase_demo03_course"."tenant_id" IS '租户编号';
COMMENT ON TABLE "public"."onebase_demo03_course" IS '学生课程表';

-- ----------------------------
-- Table structure for onebase_demo03_grade
-- ----------------------------
DROP TABLE IF EXISTS "public"."onebase_demo03_grade";
CREATE TABLE "public"."onebase_demo03_grade" (
  "id" int8 NOT NULL,
  "student_id" int8 NOT NULL,
  "name" varchar(100) COLLATE "pg_catalog"."default" NOT NULL DEFAULT ''::character varying,
  "teacher" varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
  "creator" int8 DEFAULT 0,
  "create_time" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "updater" int8 DEFAULT 0,
  "update_time" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "deleted" int2 NOT NULL DEFAULT 0,
  "tenant_id" int8 NOT NULL DEFAULT 0
)
;
ALTER TABLE "public"."onebase_demo03_grade" OWNER TO "postgres";
COMMENT ON COLUMN "public"."onebase_demo03_grade"."id" IS '编号';
COMMENT ON COLUMN "public"."onebase_demo03_grade"."student_id" IS '学生编号';
COMMENT ON COLUMN "public"."onebase_demo03_grade"."name" IS '名字';
COMMENT ON COLUMN "public"."onebase_demo03_grade"."teacher" IS '班主任';
COMMENT ON COLUMN "public"."onebase_demo03_grade"."creator" IS '创建者';
COMMENT ON COLUMN "public"."onebase_demo03_grade"."create_time" IS '创建时间';
COMMENT ON COLUMN "public"."onebase_demo03_grade"."updater" IS '更新者';
COMMENT ON COLUMN "public"."onebase_demo03_grade"."update_time" IS '更新时间';
COMMENT ON COLUMN "public"."onebase_demo03_grade"."deleted" IS '是否删除';
COMMENT ON COLUMN "public"."onebase_demo03_grade"."tenant_id" IS '租户编号';
COMMENT ON TABLE "public"."onebase_demo03_grade" IS '学生班级表';

-- ----------------------------
-- Table structure for onebase_demo03_student
-- ----------------------------
DROP TABLE IF EXISTS "public"."onebase_demo03_student";
CREATE TABLE "public"."onebase_demo03_student" (
  "id" int8 NOT NULL,
  "name" varchar(100) COLLATE "pg_catalog"."default" NOT NULL DEFAULT ''::character varying,
  "sex" int2 NOT NULL,
  "birthday" timestamp(6) NOT NULL,
  "description" varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
  "creator" int8 DEFAULT 0,
  "create_time" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "updater" int8 DEFAULT 0,
  "update_time" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "deleted" int2 NOT NULL DEFAULT 0,
  "tenant_id" int8 NOT NULL DEFAULT 0
)
;
ALTER TABLE "public"."onebase_demo03_student" OWNER TO "postgres";
COMMENT ON COLUMN "public"."onebase_demo03_student"."id" IS '编号';
COMMENT ON COLUMN "public"."onebase_demo03_student"."name" IS '名字';
COMMENT ON COLUMN "public"."onebase_demo03_student"."sex" IS '性别';
COMMENT ON COLUMN "public"."onebase_demo03_student"."birthday" IS '出生日期';
COMMENT ON COLUMN "public"."onebase_demo03_student"."description" IS '简介';
COMMENT ON COLUMN "public"."onebase_demo03_student"."creator" IS '创建者';
COMMENT ON COLUMN "public"."onebase_demo03_student"."create_time" IS '创建时间';
COMMENT ON COLUMN "public"."onebase_demo03_student"."updater" IS '更新者';
COMMENT ON COLUMN "public"."onebase_demo03_student"."update_time" IS '更新时间';
COMMENT ON COLUMN "public"."onebase_demo03_student"."deleted" IS '是否删除';
COMMENT ON COLUMN "public"."onebase_demo03_student"."tenant_id" IS '租户编号';
COMMENT ON TABLE "public"."onebase_demo03_student" IS '学生表';

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
ALTER SEQUENCE "public"."field_type_mapping_id_seq"
OWNED BY "public"."metadata_field_type_mapping"."id";
SELECT setval('"public"."field_type_mapping_id_seq"', 131, true);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
SELECT setval('"public"."infra_api_access_log_seq"', 1, false);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
SELECT setval('"public"."infra_api_error_log_seq"', 1312, true);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
SELECT setval('"public"."infra_config_seq"', 13, false);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
SELECT setval('"public"."infra_data_source_config_seq"', 1, false);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
SELECT setval('"public"."infra_file_config_seq"', 23, false);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
SELECT setval('"public"."infra_file_content_seq"', 1, false);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
SELECT setval('"public"."infra_file_seq"', 1, false);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
SELECT setval('"public"."infra_job_log_seq"', 1, false);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
SELECT setval('"public"."infra_job_seq"', 28, false);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
ALTER SEQUENCE "public"."metadata_system_fields_id_seq"
OWNED BY "public"."metadata_system_fields"."id";
SELECT setval('"public"."metadata_system_fields_id_seq"', 9, true);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
ALTER SEQUENCE "public"."system_dept_id_seq"
OWNED BY "public"."system_dept"."id";
SELECT setval('"public"."system_dept_id_seq"', 124, true);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
SELECT setval('"public"."system_dept_seq"', 124, false);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
SELECT setval('"public"."system_dict_data_seq"', 1614, true);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
SELECT setval('"public"."system_dict_type_seq"', 621, true);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
SELECT setval('"public"."system_login_log_seq"', 147, true);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
SELECT setval('"public"."system_mail_account_seq"', 5, false);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
SELECT setval('"public"."system_mail_log_seq"', 1, false);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
SELECT setval('"public"."system_mail_template_seq"', 16, false);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
SELECT setval('"public"."system_menu_seq"', 2808, true);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
SELECT setval('"public"."system_notice_seq"', 5, false);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
SELECT setval('"public"."system_notify_message_seq"', 11, false);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
SELECT setval('"public"."system_notify_template_seq"', 1, false);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
SELECT setval('"public"."system_oauth2_access_token_seq"', 199, true);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
SELECT setval('"public"."system_oauth2_approve_seq"', 1, false);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
SELECT setval('"public"."system_oauth2_client_seq"', 43, false);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
SELECT setval('"public"."system_oauth2_code_seq"', 1, false);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
SELECT setval('"public"."system_oauth2_refresh_token_seq"', 25, false);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
SELECT setval('"public"."system_operate_log_seq"', 28, true);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
SELECT setval('"public"."system_post_seq"', 4097, true);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
SELECT setval('"public"."system_role_menu_seq"', 5974, true);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
SELECT setval('"public"."system_role_seq"', 125, true);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
SELECT setval('"public"."system_sms_channel_seq"', 7, false);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
SELECT setval('"public"."system_sms_code_seq"', 1, false);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
SELECT setval('"public"."system_sms_log_seq"', 1, false);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
SELECT setval('"public"."system_sms_template_seq"', 17, false);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
SELECT setval('"public"."system_social_client_seq"', 44, false);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
SELECT setval('"public"."system_social_user_bind_seq"', 1, false);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
SELECT setval('"public"."system_social_user_seq"', 1, false);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
SELECT setval('"public"."system_tenant_package_seq"', 112, true);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
SELECT setval('"public"."system_tenant_seq"', 136, true);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
SELECT setval('"public"."system_user_post_seq"', 155, true);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
SELECT setval('"public"."system_user_role_seq"', 46, true);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
ALTER SEQUENCE "public"."system_users_id_seq"
OWNED BY "public"."system_users"."id";
SELECT setval('"public"."system_users_id_seq"', 140, true);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
SELECT setval('"public"."system_users_seq"', 136, true);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
SELECT setval('"public"."onebase_demo01_contact_seq"', 2, false);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
SELECT setval('"public"."onebase_demo02_category_seq"', 7, false);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
SELECT setval('"public"."onebase_demo03_course_seq"', 14, false);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
SELECT setval('"public"."onebase_demo03_grade_seq"', 10, false);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
SELECT setval('"public"."onebase_demo03_student_seq"', 10, false);

-- ----------------------------
-- Primary Key structure for table app_application
-- ----------------------------
ALTER TABLE "public"."app_application" ADD CONSTRAINT "pk_app_application" PRIMARY KEY ("id");

-- ----------------------------
-- Uniques structure for table app_application_tag
-- ----------------------------
ALTER TABLE "public"."app_application_tag" ADD CONSTRAINT "uk_app_application_tag" UNIQUE ("application_id", "tag_id");

-- ----------------------------
-- Primary Key structure for table app_application_tag
-- ----------------------------
ALTER TABLE "public"."app_application_tag" ADD CONSTRAINT "pk_app_application_tag" PRIMARY KEY ("id");

-- ----------------------------
-- Uniques structure for table app_auth_data_filter
-- ----------------------------
ALTER TABLE "public"."app_auth_data_filter" ADD CONSTRAINT "uk_app_auth_data_filter" UNIQUE ("application_id", "group_id", "field_name");

-- ----------------------------
-- Primary Key structure for table app_auth_data_filter
-- ----------------------------
ALTER TABLE "public"."app_auth_data_filter" ADD CONSTRAINT "pk_app_auth_data_filter" PRIMARY KEY ("id");

-- ----------------------------
-- Uniques structure for table app_auth_data_group
-- ----------------------------
ALTER TABLE "public"."app_auth_data_group" ADD CONSTRAINT "uk_app_auth_data_group" UNIQUE ("application_id", "group_name", "is_deleted");

-- ----------------------------
-- Primary Key structure for table app_auth_data_group
-- ----------------------------
ALTER TABLE "public"."app_auth_data_group" ADD CONSTRAINT "pk_app_auth_data_group" PRIMARY KEY ("id");

-- ----------------------------
-- Uniques structure for table app_auth_entity
-- ----------------------------
ALTER TABLE "public"."app_auth_entity" ADD CONSTRAINT "uk_app_auth_entity" UNIQUE ("application_id", "menu_id", "entity_id");

-- ----------------------------
-- Primary Key structure for table app_auth_entity
-- ----------------------------
ALTER TABLE "public"."app_auth_entity" ADD CONSTRAINT "pk_app_auth_entity" PRIMARY KEY ("id");

-- ----------------------------
-- Uniques structure for table app_auth_feature
-- ----------------------------
ALTER TABLE "public"."app_auth_feature" ADD CONSTRAINT "uk_app_auth_feature" UNIQUE ("application_id", "menu_id");

-- ----------------------------
-- Primary Key structure for table app_auth_feature
-- ----------------------------
ALTER TABLE "public"."app_auth_feature" ADD CONSTRAINT "pk_app_auth_feature" PRIMARY KEY ("id");

-- ----------------------------
-- Uniques structure for table app_auth_field
-- ----------------------------
ALTER TABLE "public"."app_auth_field" ADD CONSTRAINT "uk_app_auth_field" UNIQUE ("application_id", "menu_id", "field_name");

-- ----------------------------
-- Primary Key structure for table app_auth_field
-- ----------------------------
ALTER TABLE "public"."app_auth_field" ADD CONSTRAINT "pk_app_auth_field" PRIMARY KEY ("id");

-- ----------------------------
-- Uniques structure for table app_auth_operation
-- ----------------------------
ALTER TABLE "public"."app_auth_operation" ADD CONSTRAINT "uk_app_auth_operation" UNIQUE ("application_id", "menu_id", "operation_type", "is_deleted");

-- ----------------------------
-- Primary Key structure for table app_auth_operation
-- ----------------------------
ALTER TABLE "public"."app_auth_operation" ADD CONSTRAINT "pk_app_auth_operation" PRIMARY KEY ("id");

-- ----------------------------
-- Uniques structure for table app_auth_role
-- ----------------------------
ALTER TABLE "public"."app_auth_role" ADD CONSTRAINT "uk_app_auth_role" UNIQUE ("application_id", "role_code");

-- ----------------------------
-- Primary Key structure for table app_auth_role
-- ----------------------------
ALTER TABLE "public"."app_auth_role" ADD CONSTRAINT "pk_app_auth_role" PRIMARY KEY ("id");

-- ----------------------------
-- Uniques structure for table app_auth_user_role
-- ----------------------------
ALTER TABLE "public"."app_auth_user_role" ADD CONSTRAINT "uk_app_auth_user_role" UNIQUE ("application_id", "user_id", "role_id");

-- ----------------------------
-- Primary Key structure for table app_auth_user_role
-- ----------------------------
ALTER TABLE "public"."app_auth_user_role" ADD CONSTRAINT "pk_app_auth_user_role" PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table app_menu
-- ----------------------------
ALTER TABLE "public"."app_menu" ADD CONSTRAINT "pk_id" PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table app_resource
-- ----------------------------
ALTER TABLE "public"."app_resource" ADD CONSTRAINT "pk_app_application_resource" PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table app_tag
-- ----------------------------
ALTER TABLE "public"."app_tag" ADD CONSTRAINT "app_tag_pk" PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table app_version
-- ----------------------------
ALTER TABLE "public"."app_version" ADD CONSTRAINT "pk_app_application_version" PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table app_version_menu
-- ----------------------------
ALTER TABLE "public"."app_version_menu" ADD CONSTRAINT "pk_app_app_application_version_menu" PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table app_version_resource
-- ----------------------------
ALTER TABLE "public"."app_version_resource" ADD CONSTRAINT "pk_app_application_version_resource" PRIMARY KEY ("id");

-- ----------------------------
-- Indexes structure for table infra_api_access_log
-- ----------------------------
CREATE INDEX "idx_infra_api_access_log_01" ON "public"."infra_api_access_log" USING btree (
  "create_time" "pg_catalog"."timestamp_ops" ASC NULLS LAST
);

-- ----------------------------
-- Primary Key structure for table infra_api_access_log
-- ----------------------------
ALTER TABLE "public"."infra_api_access_log" ADD CONSTRAINT "pk_infra_api_access_log" PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table infra_api_error_log
-- ----------------------------
ALTER TABLE "public"."infra_api_error_log" ADD CONSTRAINT "pk_infra_api_error_log" PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table infra_config
-- ----------------------------
ALTER TABLE "public"."infra_config" ADD CONSTRAINT "pk_infra_config" PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table infra_data_source_config
-- ----------------------------
ALTER TABLE "public"."infra_data_source_config" ADD CONSTRAINT "pk_infra_data_source_config" PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table infra_file
-- ----------------------------
ALTER TABLE "public"."infra_file" ADD CONSTRAINT "pk_infra_file" PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table infra_file_config
-- ----------------------------
ALTER TABLE "public"."infra_file_config" ADD CONSTRAINT "pk_infra_file_config" PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table infra_file_content
-- ----------------------------
ALTER TABLE "public"."infra_file_content" ADD CONSTRAINT "pk_infra_file_content" PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table infra_job
-- ----------------------------
ALTER TABLE "public"."infra_job" ADD CONSTRAINT "pk_infra_job" PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table infra_job_log
-- ----------------------------
ALTER TABLE "public"."infra_job_log" ADD CONSTRAINT "pk_infra_job_log" PRIMARY KEY ("id");

-- ----------------------------
-- Indexes structure for table metadata_business_entity
-- ----------------------------
CREATE INDEX "idx_entity_datasource" ON "public"."metadata_business_entity" USING btree (
  "datasource_id" "pg_catalog"."int8_ops" ASC NULLS LAST,
  "deleted" "pg_catalog"."int4_ops" ASC NULLS LAST
);
CREATE INDEX "idx_entity_tenant_app" ON "public"."metadata_business_entity" USING btree (
  "app_id" "pg_catalog"."int8_ops" ASC NULLS LAST,
  "tenant_id" "pg_catalog"."int8_ops" ASC NULLS LAST,
  "deleted" "pg_catalog"."int4_ops" ASC NULLS LAST
);
CREATE UNIQUE INDEX "uk_entity_code" ON "public"."metadata_business_entity" USING btree (
  "code" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST,
  "app_id" "pg_catalog"."int8_ops" ASC NULLS LAST,
  "tenant_id" "pg_catalog"."int8_ops" ASC NULLS LAST
) WHERE deleted = 0;

-- ----------------------------
-- Primary Key structure for table metadata_business_entity
-- ----------------------------
ALTER TABLE "public"."metadata_business_entity" ADD CONSTRAINT "metadata_business_entity_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Indexes structure for table metadata_datasource
-- ----------------------------
CREATE INDEX "idx_datasource_tenant_app" ON "public"."metadata_datasource" USING btree (
  "app_id" "pg_catalog"."int8_ops" ASC NULLS LAST,
  "tenant_id" "pg_catalog"."int8_ops" ASC NULLS LAST,
  "deleted" "pg_catalog"."int4_ops" ASC NULLS LAST
);
CREATE INDEX "idx_datasource_type" ON "public"."metadata_datasource" USING btree (
  "datasource_type" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST,
  "deleted" "pg_catalog"."int4_ops" ASC NULLS LAST
);
CREATE UNIQUE INDEX "uk_datasource_code" ON "public"."metadata_datasource" USING btree (
  "code" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST,
  "app_id" "pg_catalog"."int8_ops" ASC NULLS LAST,
  "tenant_id" "pg_catalog"."int8_ops" ASC NULLS LAST
) WHERE deleted = 0;

-- ----------------------------
-- Primary Key structure for table metadata_datasource
-- ----------------------------
ALTER TABLE "public"."metadata_datasource" ADD CONSTRAINT "metadata_datasource_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Indexes structure for table metadata_entity_field
-- ----------------------------
CREATE INDEX "idx_field_entity" ON "public"."metadata_entity_field" USING btree (
  "entity_id" "pg_catalog"."int8_ops" ASC NULLS LAST,
  "deleted" "pg_catalog"."int4_ops" ASC NULLS LAST
);
CREATE INDEX "idx_field_tenant_app" ON "public"."metadata_entity_field" USING btree (
  "tenant_id" "pg_catalog"."int8_ops" ASC NULLS LAST,
  "app_id" "pg_catalog"."int8_ops" ASC NULLS LAST,
  "deleted" "pg_catalog"."int4_ops" ASC NULLS LAST
);
CREATE UNIQUE INDEX "uk_field_name" ON "public"."metadata_entity_field" USING btree (
  "entity_id" "pg_catalog"."int8_ops" ASC NULLS LAST,
  "field_name" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
) WHERE deleted = 0;

-- ----------------------------
-- Primary Key structure for table metadata_entity_field
-- ----------------------------
ALTER TABLE "public"."metadata_entity_field" ADD CONSTRAINT "metadata_entity_field_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Indexes structure for table metadata_entity_relationship
-- ----------------------------
CREATE INDEX "idx_relationship_source" ON "public"."metadata_entity_relationship" USING btree (
  "source_entity_id" "pg_catalog"."int8_ops" ASC NULLS LAST,
  "deleted" "pg_catalog"."int4_ops" ASC NULLS LAST
);
CREATE INDEX "idx_relationship_target" ON "public"."metadata_entity_relationship" USING btree (
  "target_entity_id" "pg_catalog"."int8_ops" ASC NULLS LAST,
  "deleted" "pg_catalog"."int4_ops" ASC NULLS LAST
);
CREATE INDEX "idx_relationship_tenant_app" ON "public"."metadata_entity_relationship" USING btree (
  "app_id" "pg_catalog"."int8_ops" ASC NULLS LAST,
  "tenant_id" "pg_catalog"."int8_ops" ASC NULLS LAST,
  "deleted" "pg_catalog"."int4_ops" ASC NULLS LAST
);

-- ----------------------------
-- Primary Key structure for table metadata_entity_relationship
-- ----------------------------
ALTER TABLE "public"."metadata_entity_relationship" ADD CONSTRAINT "metadata_entity_relationship_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table metadata_field_type_mapping
-- ----------------------------
ALTER TABLE "public"."metadata_field_type_mapping" ADD CONSTRAINT "field_type_mapping_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table metadata_system_fields
-- ----------------------------
ALTER TABLE "public"."metadata_system_fields" ADD CONSTRAINT "metadata_system_fields_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Indexes structure for table metadata_validation_rule
-- ----------------------------
CREATE INDEX "idx_validation_entity" ON "public"."metadata_validation_rule" USING btree (
  "entity_id" "pg_catalog"."int8_ops" ASC NULLS LAST,
  "deleted" "pg_catalog"."int4_ops" ASC NULLS LAST
);
CREATE INDEX "idx_validation_tenant_app" ON "public"."metadata_validation_rule" USING btree (
  "app_id" "pg_catalog"."int8_ops" ASC NULLS LAST,
  "tenant_id" "pg_catalog"."int8_ops" ASC NULLS LAST,
  "deleted" "pg_catalog"."int4_ops" ASC NULLS LAST
);
CREATE UNIQUE INDEX "uk_validation_code" ON "public"."metadata_validation_rule" USING btree (
  "validation_code" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST,
  "app_id" "pg_catalog"."int8_ops" ASC NULLS LAST,
  "tenant_id" "pg_catalog"."int8_ops" ASC NULLS LAST
) WHERE deleted = 0;

-- ----------------------------
-- Primary Key structure for table metadata_validation_rule
-- ----------------------------
ALTER TABLE "public"."metadata_validation_rule" ADD CONSTRAINT "metadata_validation_rule_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table system_dept
-- ----------------------------
ALTER TABLE "public"."system_dept" ADD CONSTRAINT "pk_system_dept" PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table system_dict_data
-- ----------------------------
ALTER TABLE "public"."system_dict_data" ADD CONSTRAINT "pk_system_dict_data" PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table system_dict_type
-- ----------------------------
ALTER TABLE "public"."system_dict_type" ADD CONSTRAINT "pk_system_dict_type" PRIMARY KEY ("id");

-- ----------------------------
-- Indexes structure for table system_license
-- ----------------------------
CREATE INDEX "idx_system_license_enterprise_name" ON "public"."system_license" USING btree (
  "enterprise_name" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
);
CREATE INDEX "idx_system_license_expire_time" ON "public"."system_license" USING btree (
  "expire_time" "pg_catalog"."timestamp_ops" ASC NULLS LAST
);

-- ----------------------------
-- Primary Key structure for table system_license
-- ----------------------------
ALTER TABLE "public"."system_license" ADD CONSTRAINT "system_license_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table system_login_log
-- ----------------------------
ALTER TABLE "public"."system_login_log" ADD CONSTRAINT "pk_system_login_log" PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table system_mail_account
-- ----------------------------
ALTER TABLE "public"."system_mail_account" ADD CONSTRAINT "pk_system_mail_account" PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table system_mail_log
-- ----------------------------
ALTER TABLE "public"."system_mail_log" ADD CONSTRAINT "pk_system_mail_log" PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table system_mail_template
-- ----------------------------
ALTER TABLE "public"."system_mail_template" ADD CONSTRAINT "pk_system_mail_template" PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table system_menu
-- ----------------------------
ALTER TABLE "public"."system_menu" ADD CONSTRAINT "pk_system_menu" PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table system_notice
-- ----------------------------
ALTER TABLE "public"."system_notice" ADD CONSTRAINT "pk_system_notice" PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table system_notify_message
-- ----------------------------
ALTER TABLE "public"."system_notify_message" ADD CONSTRAINT "pk_system_notify_message" PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table system_notify_template
-- ----------------------------
ALTER TABLE "public"."system_notify_template" ADD CONSTRAINT "pk_system_notify_template" PRIMARY KEY ("id");

-- ----------------------------
-- Indexes structure for table system_oauth2_access_token
-- ----------------------------
CREATE INDEX "idx_system_oauth2_access_token_01" ON "public"."system_oauth2_access_token" USING btree (
  "access_token" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
);
CREATE INDEX "idx_system_oauth2_access_token_02" ON "public"."system_oauth2_access_token" USING btree (
  "refresh_token" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
);

-- ----------------------------
-- Primary Key structure for table system_oauth2_access_token
-- ----------------------------
ALTER TABLE "public"."system_oauth2_access_token" ADD CONSTRAINT "pk_system_oauth2_access_token" PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table system_oauth2_approve
-- ----------------------------
ALTER TABLE "public"."system_oauth2_approve" ADD CONSTRAINT "pk_system_oauth2_approve" PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table system_oauth2_client
-- ----------------------------
ALTER TABLE "public"."system_oauth2_client" ADD CONSTRAINT "pk_system_oauth2_client" PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table system_oauth2_code
-- ----------------------------
ALTER TABLE "public"."system_oauth2_code" ADD CONSTRAINT "pk_system_oauth2_code" PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table system_oauth2_refresh_token
-- ----------------------------
ALTER TABLE "public"."system_oauth2_refresh_token" ADD CONSTRAINT "pk_system_oauth2_refresh_token" PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table system_operate_log
-- ----------------------------
ALTER TABLE "public"."system_operate_log" ADD CONSTRAINT "pk_system_operate_log" PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table system_post
-- ----------------------------
ALTER TABLE "public"."system_post" ADD CONSTRAINT "pk_system_post" PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table system_role
-- ----------------------------
ALTER TABLE "public"."system_role" ADD CONSTRAINT "pk_system_role" PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table system_role_menu
-- ----------------------------
ALTER TABLE "public"."system_role_menu" ADD CONSTRAINT "pk_system_role_menu" PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table system_sms_channel
-- ----------------------------
ALTER TABLE "public"."system_sms_channel" ADD CONSTRAINT "pk_system_sms_channel" PRIMARY KEY ("id");

-- ----------------------------
-- Indexes structure for table system_sms_code
-- ----------------------------
CREATE INDEX "idx_system_sms_code_01" ON "public"."system_sms_code" USING btree (
  "mobile" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
);

-- ----------------------------
-- Primary Key structure for table system_sms_code
-- ----------------------------
ALTER TABLE "public"."system_sms_code" ADD CONSTRAINT "pk_system_sms_code" PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table system_sms_log
-- ----------------------------
ALTER TABLE "public"."system_sms_log" ADD CONSTRAINT "pk_system_sms_log" PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table system_sms_template
-- ----------------------------
ALTER TABLE "public"."system_sms_template" ADD CONSTRAINT "pk_system_sms_template" PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table system_social_client
-- ----------------------------
ALTER TABLE "public"."system_social_client" ADD CONSTRAINT "pk_system_social_client" PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table system_social_user
-- ----------------------------
ALTER TABLE "public"."system_social_user" ADD CONSTRAINT "pk_system_social_user" PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table system_social_user_bind
-- ----------------------------
ALTER TABLE "public"."system_social_user_bind" ADD CONSTRAINT "pk_system_social_user_bind" PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table system_tenant
-- ----------------------------
ALTER TABLE "public"."system_tenant" ADD CONSTRAINT "pk_system_tenant" PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table system_tenant_package
-- ----------------------------
ALTER TABLE "public"."system_tenant_package" ADD CONSTRAINT "pk_system_tenant_package" PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table system_user_post
-- ----------------------------
ALTER TABLE "public"."system_user_post" ADD CONSTRAINT "pk_system_user_post" PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table system_user_role
-- ----------------------------
ALTER TABLE "public"."system_user_role" ADD CONSTRAINT "pk_system_user_role" PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table system_users
-- ----------------------------
ALTER TABLE "public"."system_users" ADD CONSTRAINT "pk_system_users" PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table onebase_demo01_contact
-- ----------------------------
ALTER TABLE "public"."onebase_demo01_contact" ADD CONSTRAINT "pk_onebase_demo01_contact" PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table onebase_demo02_category
-- ----------------------------
ALTER TABLE "public"."onebase_demo02_category" ADD CONSTRAINT "pk_onebase_demo02_category" PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table onebase_demo03_course
-- ----------------------------
ALTER TABLE "public"."onebase_demo03_course" ADD CONSTRAINT "pk_onebase_demo03_course" PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table onebase_demo03_grade
-- ----------------------------
ALTER TABLE "public"."onebase_demo03_grade" ADD CONSTRAINT "pk_onebase_demo03_grade" PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table onebase_demo03_student
-- ----------------------------
ALTER TABLE "public"."onebase_demo03_student" ADD CONSTRAINT "pk_onebase_demo03_student" PRIMARY KEY ("id");
