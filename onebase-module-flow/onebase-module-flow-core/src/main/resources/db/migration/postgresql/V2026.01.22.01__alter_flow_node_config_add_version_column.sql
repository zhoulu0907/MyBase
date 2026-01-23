-- Add version column to flow_node_config table
-- This column stores the connector type version (e.g., 1.0.0)

ALTER TABLE flow_node_config
ADD COLUMN version VARCHAR(20);

COMMENT ON COLUMN flow_node_config.version IS 'Connector version (e.g., 1.0.0)';
