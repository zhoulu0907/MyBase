import React, { useState, useEffect } from 'react';
import { Button, Form, Select, Modal, Space, Input } from '@arco-design/web-react';
import { IconDelete, IconPlus } from '@arco-design/web-react/icon';
import styles from '../styles/index.module.less';

const Option = Select.Option;

interface Props {
  onChange: (value: any) => void;
  item: any;
  value: any;
  sdk?: any;
}

const DynamicSelectDataSourceConfig = ({ onChange, item, value, sdk }: Props) => {
  const config = value || {};
  const [ruleModalVisible, setRuleModalVisible] = useState(false);
  
  // State for entities and fields fetched from SDK
  const [entities, setEntities] = useState<any[]>([]);
  const [currentFields, setCurrentFields] = useState<any[]>([]);

  // Mapping rules: [{ sourceField: '', targetField: '' }]
  const [rules, setRules] = useState<any[]>(config.fillRuleSetting || []);

  // Fetch entities on mount
  useEffect(() => {
    if (sdk && sdk.context && sdk.context.entity && sdk.context.entity.getEntities) {
      const fetchedEntities = sdk.context.entity.getEntities();
      setEntities(fetchedEntities || []);
    }
  }, [sdk]);

  // Fetch fields when entityUuid changes or on mount if already selected
  useEffect(() => {
    if (config.entityUuid && sdk && sdk.context && sdk.context.entity && sdk.context.entity.getFields) {
      const fetchedFields = sdk.context.entity.getFields(config.entityUuid);
      setCurrentFields(fetchedFields || []);
    } else {
      setCurrentFields([]);
    }
  }, [config.entityUuid, sdk]);

  const handleSourceChange = (entityUuid: string) => {
    const entity = entities.find(e => e.entityUuid === entityUuid);
    if (!entity) return;

    const newConfig = {
      ...config,
      entityUuid,
      entityName: entity.entityName,
      tableName: entity.tableName,
      // Reset dependent configs
      displayFields: [],
      fillRuleSetting: []
    };
    onChange(newConfig);
    setRules([]);
  };

  const handleRulesChange = (newRules: any[]) => {
    setRules(newRules);
    onChange({ ...config, fillRuleSetting: newRules });
  };

  return (
    <div className={styles.dataSourceContainer}>
      <Form.Item className={styles.formItem} label="数据源">
        <Select
          placeholder="请选择数据源"
          value={config.entityUuid}
          onChange={handleSourceChange}
        >
          {entities.map(e => (
            <Option key={e.entityUuid} value={e.entityUuid}>{e.entityName}</Option>
          ))}
        </Select>
      </Form.Item>

      {config.entityUuid && (
        <>
          <Form.Item className={styles.formItem} label="回显字段">
             <Select
                placeholder="选择回显字段"
                value={config.displayFields?.[0]?.value}
                onChange={(val) => {
                   const field = currentFields.find(f => f.fieldName === val);
                   onChange({
                     ...config,
                     displayFields: field ? [{ label: field.displayName, value: field.fieldName }] : []
                   });
                }}
             >
                {currentFields.map(f => (
                  <Option key={f.fieldName} value={f.fieldName}>{f.displayName}</Option>
                ))}
             </Select>
          </Form.Item>

          <Form.Item className={styles.formItem} label="填充到表单字段">
            <Button long onClick={() => setRuleModalVisible(true)}>
              {rules.length > 0 ? `已设置 ${rules.length} 条规则` : '设置填充规则'}
            </Button>
          </Form.Item>
        </>
      )}

      <Modal
        title="填充规则设置"
        visible={ruleModalVisible}
        onOk={() => setRuleModalVisible(false)}
        onCancel={() => setRuleModalVisible(false)}
        style={{ width: 600 }}
      >
        <div style={{ marginBottom: 16 }}>
           <Button type="primary" size="small" onClick={() => handleRulesChange([...rules, { source: '', target: '' }])}>
             <IconPlus /> 添加规则
           </Button>
        </div>
        {rules.map((rule, index) => (
          <Space key={index} style={{ display: 'flex', marginBottom: 8 }} align="center">
            <span>源字段:</span>
            <Select
              style={{ width: 150 }}
              placeholder="选择源字段"
              value={rule.source}
              onChange={(val) => {
                const newRules = [...rules];
                newRules[index].source = val;
                handleRulesChange(newRules);
              }}
            >
               {currentFields.map(f => (
                 <Option key={f.fieldName} value={f.fieldName}>{f.displayName}</Option>
               ))}
            </Select>
            <span>映射到:</span>
            <Input
              style={{ width: 150 }}
              placeholder="目标字段Key"
              value={rule.target}
              onChange={(val) => {
                const newRules = [...rules];
                newRules[index].target = val;
                handleRulesChange(newRules);
              }}
            />
            <Button 
              icon={<IconDelete />} 
              status="danger" 
              shape="circle" 
              size="small"
              onClick={() => {
                const newRules = rules.filter((_, i) => i !== index);
                handleRulesChange(newRules);
              }}
            />
          </Space>
        ))}
        {rules.length === 0 && <div style={{ textAlign: 'center', color: '#999' }}>暂无规则</div>}
      </Modal>
    </div>
  );
};

export default DynamicSelectDataSourceConfig;
