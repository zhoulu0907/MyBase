\set ON_ERROR_STOP on

-- onebase_ai 初始化脚本
-- 执行前请先运行 00_create_user_db_grants.sql
\connect onebase_ai
SET search_path TO public;
SET standard_conforming_strings = on;

-- ============================================================================
-- 来源文件: onebase_ai_ddl.sql.txt
-- ============================================================================
-- OBAgent 数据库表结构
-- 从 SQLAlchemy 模型自动生成
-- 生成时间: 2026-03-26 15:56:31
-- 请勿手动修改，运行 scripts/generate_ddl.py 重新生成

-- 设置字符集
SET standard_conforming_strings = on;

-- 删除已存在的表（按相反顺序删除，避免依赖问题）
DROP TABLE IF EXISTS generate_report_task CASCADE;
DROP TABLE IF EXISTS generate_agent_config CASCADE;
DROP TABLE IF EXISTS agent CASCADE;
DROP TABLE IF EXISTS miniprogram_template CASCADE;
DROP TABLE IF EXISTS miniprogram_task CASCADE;
DROP TABLE IF EXISTS app_progress CASCADE;
DROP TABLE IF EXISTS document_reference_files CASCADE;
DROP TABLE IF EXISTS document CASCADE;
DROP TABLE IF EXISTS template CASCADE;
DROP TABLE IF EXISTS ob_video_item CASCADE;
DROP TABLE IF EXISTS ob_video_info CASCADE;
DROP TABLE IF EXISTS action_execution_error_logs CASCADE;
DROP TABLE IF EXISTS session_progress CASCADE;
DROP TABLE IF EXISTS copilot_history_message CASCADE;
DROP TABLE IF EXISTS copilot_conversation CASCADE;
DROP TABLE IF EXISTS task_executions CASCADE;
DROP TABLE IF EXISTS action_executions CASCADE;
DROP TABLE IF EXISTS actions CASCADE;
DROP TABLE IF EXISTS use_cases CASCADE;
DROP TABLE IF EXISTS epics CASCADE;
DROP TABLE IF EXISTS session_app_state CASCADE;
DROP TABLE IF EXISTS sessions CASCADE;

-- 开始创建表

-- 表: sessions

CREATE TABLE sessions (
	session_id VARCHAR(36) NOT NULL, 
	user_id VARCHAR(255), 
	app_name VARCHAR(255), 
	status VARCHAR(50) NOT NULL, 
	worker_id VARCHAR(255), 
	created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL, 
	updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL, 
	input_text TEXT, 
	file_ids TEXT, 
	requirement_summary TEXT, 
	analysis_result TEXT, 
	analysis_error TEXT, 
	app_id VARCHAR(255), 
	big_screen_id VARCHAR(50), 
	theme_config TEXT, 
	PRIMARY KEY (session_id)
)

;

-- 表: session_app_state

CREATE TABLE session_app_state (
	session_id VARCHAR(36) NOT NULL, 
	app_id VARCHAR(255), 
	datasource_id VARCHAR(255), 
	entities_json TEXT, 
	created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL, 
	updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL, 
	PRIMARY KEY (session_id)
)

;

-- 表: epics

CREATE TABLE epics (
	epic_id VARCHAR(36) NOT NULL, 
	session_id VARCHAR(36) NOT NULL, 
	epic_name VARCHAR(500) NOT NULL, 
	epic_code VARCHAR(255), 
	business_domain VARCHAR(255), 
	business_goal TEXT, 
	value_indicators TEXT, 
	included_modules TEXT, 
	related_requirement_chapter VARCHAR(255), 
	"order" INTEGER NOT NULL, 
	raw_data TEXT, 
	created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL, 
	updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL, 
	PRIMARY KEY (epic_id)
)

;

-- 表: use_cases

CREATE TABLE use_cases (
	use_case_id VARCHAR(36) NOT NULL, 
	session_id VARCHAR(36) NOT NULL, 
	epic_id VARCHAR(36), 
	use_case_name VARCHAR(500) NOT NULL, 
	use_case_code VARCHAR(255), 
	module VARCHAR(255), 
	trigger_roles TEXT, 
	operation_entry VARCHAR(500), 
	precondition TEXT, 
	object_name VARCHAR(255), 
	action_type VARCHAR(100), 
	trigger_event VARCHAR(100), 
	field_names TEXT, 
	is_required VARCHAR(50), 
	input_method TEXT, 
	default_value TEXT, 
	validation_rules TEXT, 
	system_behavior TEXT, 
	status_flow TEXT, 
	auto_fill_fields TEXT, 
	notification_objects TEXT, 
	notification_channels TEXT, 
	permissions_roles TEXT, 
	permissions_data_scope VARCHAR(255), 
	acceptance_criteria TEXT, 
	"order" INTEGER NOT NULL, 
	raw_data TEXT, 
	created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL, 
	updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL, 
	PRIMARY KEY (use_case_id)
)

;

-- 表: actions

CREATE TABLE actions (
	action_id VARCHAR(36) NOT NULL, 
	session_id VARCHAR(36) NOT NULL, 
	name VARCHAR(500) NOT NULL, 
	code VARCHAR(255) NOT NULL, 
	category VARCHAR(255), 
	description TEXT, 
	input_params TEXT, 
	output_params TEXT, 
	"order" INTEGER NOT NULL, 
	task_id VARCHAR(36), 
	parent_action_ids VARCHAR(1000), 
	raw_data TEXT, 
	created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL, 
	updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL, 
	PRIMARY KEY (action_id)
)

;

-- 表: action_executions

CREATE TABLE action_executions (
	action_id VARCHAR(36) NOT NULL, 
	session_id VARCHAR(36), 
	status VARCHAR(50) NOT NULL, 
	execution_error TEXT, 
	retry_count INTEGER NOT NULL, 
	max_retries INTEGER NOT NULL, 
	started_at TIMESTAMP WITHOUT TIME ZONE, 
	completed_at TIMESTAMP WITHOUT TIME ZONE, 
	created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL, 
	updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL, 
	PRIMARY KEY (action_id)
)

;

-- 表: task_executions

CREATE TABLE task_executions (
	task_id VARCHAR(36) NOT NULL, 
	session_id VARCHAR(36) NOT NULL, 
	task_name VARCHAR(500) NOT NULL, 
	task_code VARCHAR(255) NOT NULL, 
	category VARCHAR(255), 
	description TEXT, 
	status VARCHAR(50) NOT NULL, 
	priority INTEGER NOT NULL, 
	execution_order INTEGER NOT NULL, 
	execution_error TEXT, 
	depends_on TEXT, 
	raw_data TEXT, 
	progress INTEGER NOT NULL, 
	total_actions INTEGER, 
	completed_actions INTEGER NOT NULL, 
	created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL, 
	updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL, 
	started_at TIMESTAMP WITHOUT TIME ZONE, 
	completed_at TIMESTAMP WITHOUT TIME ZONE, 
	PRIMARY KEY (task_id)
)

;

-- 表: copilot_conversation

CREATE TABLE copilot_conversation (
	id VARCHAR(36) NOT NULL, 
	app_id VARCHAR(64) NOT NULL, 
	user_id VARCHAR(255), 
	activated INTEGER NOT NULL, 
	current_agent_id VARCHAR(255), 
	created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL, 
	updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL, 
	PRIMARY KEY (id)
)

;

-- 表: copilot_history_message

CREATE TABLE copilot_history_message (
	id VARCHAR(36) NOT NULL, 
	conversation_id VARCHAR(36) NOT NULL, 
	content TEXT NOT NULL, 
	role VARCHAR(64) NOT NULL, 
	seq INTEGER NOT NULL, 
	completed INTEGER NOT NULL, 
	intent_type VARCHAR(64), 
	created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL, 
	updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL, 
	request_id VARCHAR(255), 
	PRIMARY KEY (id)
)

;

-- 表: session_progress

CREATE TABLE session_progress (
	session_id VARCHAR(36) NOT NULL, 
	stage_id VARCHAR(50) NOT NULL, 
	stage_name VARCHAR(100) NOT NULL, 
	status VARCHAR(50) NOT NULL, 
	progress INTEGER NOT NULL, 
	message TEXT, 
	started_at TIMESTAMP WITHOUT TIME ZONE, 
	completed_at TIMESTAMP WITHOUT TIME ZONE, 
	created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL, 
	updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL, 
	PRIMARY KEY (session_id, stage_id)
)

;

-- 表: action_execution_error_logs

CREATE TABLE action_execution_error_logs (
	log_id VARCHAR(36) NOT NULL, 
	action_id VARCHAR(36) NOT NULL, 
	session_id VARCHAR(36) NOT NULL, 
	action_code VARCHAR(255), 
	retry_count INTEGER NOT NULL, 
	error_message TEXT, 
	error_stacktrace TEXT, 
	failed_at TIMESTAMP WITHOUT TIME ZONE, 
	created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL, 
	PRIMARY KEY (log_id)
)

;

-- 表: ob_video_info

CREATE TABLE ob_video_info (
	id VARCHAR(36) NOT NULL, 
	course_name VARCHAR(255), 
	video_url VARCHAR(255), 
	description TEXT, 
	design_environment VARCHAR(255), 
	data_type VARCHAR(50), 
	data_tag VARCHAR(200), 
	created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL, 
	updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL, 
	PRIMARY KEY (id)
)

;

-- 表: ob_video_item

CREATE TABLE ob_video_item (
	id VARCHAR(36) NOT NULL, 
	video_id VARCHAR(64) NOT NULL, 
	description TEXT, 
	url TEXT, 
	file_id VARCHAR(255), 
	design_environment VARCHAR(255), 
	created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL, 
	updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL, 
	PRIMARY KEY (id)
)

;

-- 表: template

CREATE TABLE template (
	id TEXT NOT NULL, 
	name TEXT NOT NULL, 
	description TEXT, 
	reference_file_id TEXT NOT NULL, 
	reference_file_name TEXT, 
	reference_file_url TEXT, 
	creator_id INTEGER NOT NULL, 
	created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL, 
	updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL, 
	PRIMARY KEY (id)
)

;

-- 表: document

CREATE TABLE document (
	id TEXT NOT NULL, 
	name TEXT NOT NULL, 
	template_id TEXT NOT NULL, 
	source_type TEXT NOT NULL, 
	report_id TEXT, 
	status TEXT, 
	progress TEXT, 
	report_file_url TEXT, 
	reference_file_id TEXT, 
	reference_file_name TEXT, 
	reference_file_url TEXT, 
	associated_app_id TEXT, 
	associated_app_name TEXT, 
	creator_id INTEGER NOT NULL, 
	created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL, 
	updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL, 
	PRIMARY KEY (id)
)

;

-- 表: document_reference_files

CREATE TABLE document_reference_files (
	id TEXT NOT NULL, 
	document_id TEXT NOT NULL, 
	reference_file_id TEXT NOT NULL, 
	reference_file_name TEXT, 
	reference_file_url TEXT, 
	created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL, 
	PRIMARY KEY (id)
)

;

-- 表: app_progress

CREATE TABLE app_progress (
	id SERIAL NOT NULL, 
	task_id TEXT, 
	app_id TEXT NOT NULL, 
	app_name TEXT, 
	generation_status TEXT, 
	current_progress TEXT, 
	create_content TEXT, 
	is_noticed INTEGER, 
	is_click INTEGER, 
	create_time TIMESTAMP WITHOUT TIME ZONE, 
	update_time TIMESTAMP WITHOUT TIME ZONE, 
	current_cookie TEXT, 
	flowchart_status INTEGER, 
	ext_field TEXT, 
	start_generate_time TIMESTAMP WITHOUT TIME ZONE, 
	end_generate_time TIMESTAMP WITHOUT TIME ZONE, 
	generate_duration INTEGER, 
	prd_flag INTEGER, 
	PRIMARY KEY (id)
)

;

-- 表: miniprogram_task

CREATE TABLE miniprogram_task (
	id SERIAL NOT NULL, 
	task_no VARCHAR(64) NOT NULL, 
	miniprogram_name TEXT NOT NULL, 
	app_id TEXT NOT NULL, 
	wx_app_id TEXT NOT NULL, 
	h5_url TEXT NOT NULL, 
	progress INTEGER, 
	status TEXT, 
	user_id TEXT, 
	download_url TEXT, 
	file_size INTEGER, 
	error_message TEXT, 
	remark TEXT, 
	start_time TIMESTAMP WITHOUT TIME ZONE, 
	complete_time TIMESTAMP WITHOUT TIME ZONE, 
	duration INTEGER, 
	create_time TIMESTAMP WITHOUT TIME ZONE NOT NULL, 
	update_time TIMESTAMP WITHOUT TIME ZONE NOT NULL, 
	delete_flag INTEGER NOT NULL, 
	PRIMARY KEY (id), 
	UNIQUE (task_no)
)

;

-- 表: miniprogram_template

CREATE TABLE miniprogram_template (
	id SERIAL NOT NULL, 
	template_id VARCHAR(128) NOT NULL, 
	template_name TEXT NOT NULL, 
	version TEXT NOT NULL, 
	description TEXT, 
	minio_url TEXT, 
	file_size INTEGER, 
	original_filename TEXT, 
	status TEXT, 
	create_time TIMESTAMP WITHOUT TIME ZONE, 
	update_time TIMESTAMP WITHOUT TIME ZONE, 
	create_by TEXT, 
	update_by TEXT, 
	PRIMARY KEY (id), 
	UNIQUE (template_id)
)

;

-- 表: agent

CREATE TABLE agent (
	id SERIAL NOT NULL, 
	agent_name TEXT NOT NULL, 
	agent_type INTEGER NOT NULL, 
	description TEXT, 
	author_id TEXT NOT NULL, 
	created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL, 
	updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL, 
	PRIMARY KEY (id)
)

;

-- 表: generate_agent_config

CREATE TABLE generate_agent_config (
	id SERIAL NOT NULL, 
	agent_id INTEGER NOT NULL, 
	outline TEXT NOT NULL, 
	fields TEXT, 
	file_id TEXT, 
	created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL, 
	updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL, 
	PRIMARY KEY (id)
)

;

-- 表: generate_report_task

CREATE TABLE generate_report_task (
	id SERIAL NOT NULL, 
	agent_id INTEGER NOT NULL, 
	report_id TEXT NOT NULL, 
	task_status TEXT NOT NULL, 
	task_progress INTEGER NOT NULL, 
	file_ids TEXT, 
	fields TEXT, 
	report_content TEXT, 
	err_log TEXT, 
	enable_web_search BOOLEAN NOT NULL, 
	model_id TEXT, 
	created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL, 
	updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL, 
	PRIMARY KEY (id), 
	UNIQUE (report_id)
)

;


-- ============================================================================
-- 来源文件: AI补丁.sql
-- ============================================================================
ALTER TABLE sessions ADD COLUMN IF NOT EXISTS tenant_id VARCHAR(255);
CREATE INDEX IF NOT EXISTS ix_sessions_tenant_id ON sessions(tenant_id);
