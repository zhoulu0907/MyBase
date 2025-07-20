-- PostgreSQL 序列和默认值修复脚本
-- 第一步：设置所有表的序列起始值为当前表中的最大ID值 + 1
-- 第二步：为所有表的id字段添加DEFAULT nextval('序列名'::regclass)

-- =========================
-- 第一步：设置序列起始值
-- =========================

-- 基础设施模块相关表
SELECT setval('infra_api_access_log_seq', COALESCE((SELECT MAX(id) FROM infra_api_access_log), 0) + 1, false);
SELECT setval('infra_api_error_log_seq', COALESCE((SELECT MAX(id) FROM infra_api_error_log), 0) + 1, false);
SELECT setval('infra_codegen_column_seq', COALESCE((SELECT MAX(id) FROM infra_codegen_column), 0) + 1, false);
SELECT setval('infra_codegen_table_seq', COALESCE((SELECT MAX(id) FROM infra_codegen_table), 0) + 1, false);
SELECT setval('infra_config_seq', COALESCE((SELECT MAX(id) FROM infra_config), 0) + 1, false);
SELECT setval('infra_data_source_config_seq', COALESCE((SELECT MAX(id) FROM infra_data_source_config), 0) + 1, false);
SELECT setval('infra_file_seq', COALESCE((SELECT MAX(id) FROM infra_file), 0) + 1, false);
SELECT setval('infra_file_config_seq', COALESCE((SELECT MAX(id) FROM infra_file_config), 0) + 1, false);
SELECT setval('infra_file_content_seq', COALESCE((SELECT MAX(id) FROM infra_file_content), 0) + 1, false);
SELECT setval('infra_job_seq', COALESCE((SELECT MAX(id) FROM infra_job), 0) + 1, false);
SELECT setval('infra_job_log_seq', COALESCE((SELECT MAX(id) FROM infra_job_log), 0) + 1, false);

-- 系统核心模块相关表
SELECT setval('system_dept_seq', COALESCE((SELECT MAX(id) FROM system_dept), 0) + 1, false);
SELECT setval('system_dict_data_seq', COALESCE((SELECT MAX(id) FROM system_dict_data), 0) + 1, false);
SELECT setval('system_dict_type_seq', COALESCE((SELECT MAX(id) FROM system_dict_type), 0) + 1, false);
SELECT setval('system_login_log_seq', COALESCE((SELECT MAX(id) FROM system_login_log), 0) + 1, false);
SELECT setval('system_mail_account_seq', COALESCE((SELECT MAX(id) FROM system_mail_account), 0) + 1, false);
SELECT setval('system_mail_log_seq', COALESCE((SELECT MAX(id) FROM system_mail_log), 0) + 1, false);
SELECT setval('system_mail_template_seq', COALESCE((SELECT MAX(id) FROM system_mail_template), 0) + 1, false);
SELECT setval('system_menu_seq', COALESCE((SELECT MAX(id) FROM system_menu), 0) + 1, false);
SELECT setval('system_notice_seq', COALESCE((SELECT MAX(id) FROM system_notice), 0) + 1, false);
SELECT setval('system_notify_message_seq', COALESCE((SELECT MAX(id) FROM system_notify_message), 0) + 1, false);
SELECT setval('system_notify_template_seq', COALESCE((SELECT MAX(id) FROM system_notify_template), 0) + 1, false);

-- OAuth2 相关表
SELECT setval('system_oauth2_access_token_seq', COALESCE((SELECT MAX(id) FROM system_oauth2_access_token), 0) + 1, false);
SELECT setval('system_oauth2_approve_seq', COALESCE((SELECT MAX(id) FROM system_oauth2_approve), 0) + 1, false);
SELECT setval('system_oauth2_client_seq', COALESCE((SELECT MAX(id) FROM system_oauth2_client), 0) + 1, false);
SELECT setval('system_oauth2_code_seq', COALESCE((SELECT MAX(id) FROM system_oauth2_code), 0) + 1, false);
SELECT setval('system_oauth2_refresh_token_seq', COALESCE((SELECT MAX(id) FROM system_oauth2_refresh_token), 0) + 1, false);

-- 操作日志表
SELECT setval('system_operate_log_seq', COALESCE((SELECT MAX(id) FROM system_operate_log), 0) + 1, false);

-- 权限相关表
SELECT setval('system_post_seq', COALESCE((SELECT MAX(id) FROM system_post), 0) + 1, false);
SELECT setval('system_role_seq', COALESCE((SELECT MAX(id) FROM system_role), 0) + 1, false);
SELECT setval('system_role_menu_seq', COALESCE((SELECT MAX(id) FROM system_role_menu), 0) + 1, false);

-- 短信相关表
SELECT setval('system_sms_channel_seq', COALESCE((SELECT MAX(id) FROM system_sms_channel), 0) + 1, false);
SELECT setval('system_sms_code_seq', COALESCE((SELECT MAX(id) FROM system_sms_code), 0) + 1, false);
SELECT setval('system_sms_log_seq', COALESCE((SELECT MAX(id) FROM system_sms_log), 0) + 1, false);
SELECT setval('system_sms_template_seq', COALESCE((SELECT MAX(id) FROM system_sms_template), 0) + 1, false);

-- 社交相关表
SELECT setval('system_social_client_seq', COALESCE((SELECT MAX(id) FROM system_social_client), 0) + 1, false);
SELECT setval('system_social_user_seq', COALESCE((SELECT MAX(id) FROM system_social_user), 0) + 1, false);
SELECT setval('system_social_user_bind_seq', COALESCE((SELECT MAX(id) FROM system_social_user_bind), 0) + 1, false);

-- 租户相关表
SELECT setval('system_tenant_seq', COALESCE((SELECT MAX(id) FROM system_tenant), 0) + 1, false);
SELECT setval('system_tenant_package_seq', COALESCE((SELECT MAX(id) FROM system_tenant_package), 0) + 1, false);

-- 用户相关表
SELECT setval('system_user_post_seq', COALESCE((SELECT MAX(id) FROM system_user_post), 0) + 1, false);
SELECT setval('system_user_role_seq', COALESCE((SELECT MAX(id) FROM system_user_role), 0) + 1, false);
SELECT setval('system_users_seq', COALESCE((SELECT MAX(id) FROM system_users), 0) + 1, false);

-- 演示样例表
SELECT setval('yudao_demo01_contact_seq', COALESCE((SELECT MAX(id) FROM yudao_demo01_contact), 0) + 1, false);
SELECT setval('yudao_demo02_category_seq', COALESCE((SELECT MAX(id) FROM yudao_demo02_category), 0) + 1, false);
SELECT setval('yudao_demo03_course_seq', COALESCE((SELECT MAX(id) FROM yudao_demo03_course), 0) + 1, false);
SELECT setval('yudao_demo03_grade_seq', COALESCE((SELECT MAX(id) FROM yudao_demo03_grade), 0) + 1, false);
SELECT setval('yudao_demo03_student_seq', COALESCE((SELECT MAX(id) FROM yudao_demo03_student), 0) + 1, false);

-- =========================
-- 第二步：为id字段添加默认值
-- =========================

-- 基础设施模块相关表
ALTER TABLE infra_api_access_log ALTER COLUMN id SET DEFAULT nextval('infra_api_access_log_seq'::regclass);
ALTER TABLE infra_api_error_log ALTER COLUMN id SET DEFAULT nextval('infra_api_error_log_seq'::regclass);
ALTER TABLE infra_codegen_column ALTER COLUMN id SET DEFAULT nextval('infra_codegen_column_seq'::regclass);
ALTER TABLE infra_codegen_table ALTER COLUMN id SET DEFAULT nextval('infra_codegen_table_seq'::regclass);
ALTER TABLE infra_config ALTER COLUMN id SET DEFAULT nextval('infra_config_seq'::regclass);
ALTER TABLE infra_data_source_config ALTER COLUMN id SET DEFAULT nextval('infra_data_source_config_seq'::regclass);
ALTER TABLE infra_file ALTER COLUMN id SET DEFAULT nextval('infra_file_seq'::regclass);
ALTER TABLE infra_file_config ALTER COLUMN id SET DEFAULT nextval('infra_file_config_seq'::regclass);
ALTER TABLE infra_file_content ALTER COLUMN id SET DEFAULT nextval('infra_file_content_seq'::regclass);
ALTER TABLE infra_job ALTER COLUMN id SET DEFAULT nextval('infra_job_seq'::regclass);
ALTER TABLE infra_job_log ALTER COLUMN id SET DEFAULT nextval('infra_job_log_seq'::regclass);

-- 系统核心模块相关表（注意：system_dept可能已经有了，所以检查序列名）
-- ALTER TABLE system_dept ALTER COLUMN id SET DEFAULT nextval('system_dept_seq'::regclass); -- 如果已经有了可以注释掉
ALTER TABLE system_dict_data ALTER COLUMN id SET DEFAULT nextval('system_dict_data_seq'::regclass);
ALTER TABLE system_dict_type ALTER COLUMN id SET DEFAULT nextval('system_dict_type_seq'::regclass);
ALTER TABLE system_login_log ALTER COLUMN id SET DEFAULT nextval('system_login_log_seq'::regclass);
ALTER TABLE system_mail_account ALTER COLUMN id SET DEFAULT nextval('system_mail_account_seq'::regclass);
ALTER TABLE system_mail_log ALTER COLUMN id SET DEFAULT nextval('system_mail_log_seq'::regclass);
ALTER TABLE system_mail_template ALTER COLUMN id SET DEFAULT nextval('system_mail_template_seq'::regclass);
ALTER TABLE system_menu ALTER COLUMN id SET DEFAULT nextval('system_menu_seq'::regclass);
ALTER TABLE system_notice ALTER COLUMN id SET DEFAULT nextval('system_notice_seq'::regclass);
ALTER TABLE system_notify_message ALTER COLUMN id SET DEFAULT nextval('system_notify_message_seq'::regclass);
ALTER TABLE system_notify_template ALTER COLUMN id SET DEFAULT nextval('system_notify_template_seq'::regclass);

-- OAuth2 相关表
ALTER TABLE system_oauth2_access_token ALTER COLUMN id SET DEFAULT nextval('system_oauth2_access_token_seq'::regclass);
ALTER TABLE system_oauth2_approve ALTER COLUMN id SET DEFAULT nextval('system_oauth2_approve_seq'::regclass);
ALTER TABLE system_oauth2_client ALTER COLUMN id SET DEFAULT nextval('system_oauth2_client_seq'::regclass);
ALTER TABLE system_oauth2_code ALTER COLUMN id SET DEFAULT nextval('system_oauth2_code_seq'::regclass);
ALTER TABLE system_oauth2_refresh_token ALTER COLUMN id SET DEFAULT nextval('system_oauth2_refresh_token_seq'::regclass);

-- 操作日志表
ALTER TABLE system_operate_log ALTER COLUMN id SET DEFAULT nextval('system_operate_log_seq'::regclass);

-- 权限相关表
ALTER TABLE system_post ALTER COLUMN id SET DEFAULT nextval('system_post_seq'::regclass);
ALTER TABLE system_role ALTER COLUMN id SET DEFAULT nextval('system_role_seq'::regclass);
ALTER TABLE system_role_menu ALTER COLUMN id SET DEFAULT nextval('system_role_menu_seq'::regclass);

-- 短信相关表
ALTER TABLE system_sms_channel ALTER COLUMN id SET DEFAULT nextval('system_sms_channel_seq'::regclass);
ALTER TABLE system_sms_code ALTER COLUMN id SET DEFAULT nextval('system_sms_code_seq'::regclass);
ALTER TABLE system_sms_log ALTER COLUMN id SET DEFAULT nextval('system_sms_log_seq'::regclass);
ALTER TABLE system_sms_template ALTER COLUMN id SET DEFAULT nextval('system_sms_template_seq'::regclass);

-- 社交相关表
ALTER TABLE system_social_client ALTER COLUMN id SET DEFAULT nextval('system_social_client_seq'::regclass);
ALTER TABLE system_social_user ALTER COLUMN id SET DEFAULT nextval('system_social_user_seq'::regclass);
ALTER TABLE system_social_user_bind ALTER COLUMN id SET DEFAULT nextval('system_social_user_bind_seq'::regclass);

-- 租户相关表
ALTER TABLE system_tenant ALTER COLUMN id SET DEFAULT nextval('system_tenant_seq'::regclass);
ALTER TABLE system_tenant_package ALTER COLUMN id SET DEFAULT nextval('system_tenant_package_seq'::regclass);

-- 用户相关表
ALTER TABLE system_user_post ALTER COLUMN id SET DEFAULT nextval('system_user_post_seq'::regclass);
ALTER TABLE system_user_role ALTER COLUMN id SET DEFAULT nextval('system_user_role_seq'::regclass);
ALTER TABLE system_users ALTER COLUMN id SET DEFAULT nextval('system_users_seq'::regclass);

-- 演示样例表
ALTER TABLE yudao_demo01_contact ALTER COLUMN id SET DEFAULT nextval('yudao_demo01_contact_seq'::regclass);
ALTER TABLE yudao_demo02_category ALTER COLUMN id SET DEFAULT nextval('yudao_demo02_category_seq'::regclass);
ALTER TABLE yudao_demo03_course ALTER COLUMN id SET DEFAULT nextval('yudao_demo03_course_seq'::regclass);
ALTER TABLE yudao_demo03_grade ALTER COLUMN id SET DEFAULT nextval('yudao_demo03_grade_seq'::regclass);
ALTER TABLE yudao_demo03_student ALTER COLUMN id SET DEFAULT nextval('yudao_demo03_student_seq'::regclass);

-- 执行完成后，输出提示信息
SELECT 'PostgreSQL序列和默认值设置完成！所有表的id字段现在都会自动生成' AS message; 