-- V2026.01.26.01__create_flow_node_action_ref_table.sql

CREATE TABLE IF NOT EXISTS flow_node_action_ref (
    id BIGINT PRIMARY KEY,
    node_id BIGINT NOT NULL,
    connector_id BIGINT NOT NULL,
    action_id VARCHAR(64) NOT NULL,
    action_version INT DEFAULT 1,
    flow_version VARCHAR(32),
    creator BIGINT,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updater BIGINT,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted BIGINT DEFAULT 0
);

COMMENT ON TABLE flow_node_action_ref IS '逻辑流节点对连接器动作的引用关系';
COMMENT ON COLUMN flow_node_action_ref.node_id IS '逻辑流节点ID';
COMMENT ON COLUMN flow_node_action_ref.connector_id IS '连接器实例ID';
COMMENT ON COLUMN flow_node_action_ref.action_id IS '动作ID';
COMMENT ON COLUMN flow_node_action_ref.action_version IS '引用的动作版本';

CREATE INDEX IF NOT EXISTS idx_connector_action ON flow_node_action_ref(connector_id, action_id);
CREATE INDEX IF NOT EXISTS idx_node ON flow_node_action_ref(node_id);
CREATE INDEX IF NOT EXISTS idx_action_ref ON flow_node_action_ref(action_id);
