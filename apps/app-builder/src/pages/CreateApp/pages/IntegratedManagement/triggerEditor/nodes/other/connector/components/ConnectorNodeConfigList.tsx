import type { FormInstance } from '@arco-design/web-react';
import React from 'react';

interface ConnectorNodeConfig {
  nodeCode: string;
  nodeName: string;
  [key: string]: any;
}

interface ConnectorNodeConfigListProps {
  nodeConfigList: ConnectorNodeConfig[];
  form: FormInstance;
  onSelect?: (nodeCode: string) => void;
}

export const ConnectorNodeConfigList: React.FC<ConnectorNodeConfigListProps> = ({ nodeConfigList, form, onSelect }) => {
  const selectedNodeCode = form.getFieldValue('nodeConfigCode');

  const handleSelect = (nodeCode: string) => {
    form.setFieldValue('nodeConfigCode', nodeCode);
    onSelect?.(nodeCode);
  };

  if (!nodeConfigList || nodeConfigList.length === 0) {
    return (
      <div style={{ color: '#999', padding: '32px 0', textAlign: 'center' }}>
        暂无可用连接器，请先在集成管理-连接器中心添加
      </div>
    );
  }

  return (
    <div style={{ display: 'flex', flexWrap: 'wrap', gap: 16 }}>
      {nodeConfigList.map((config) => {
        const isSelected = selectedNodeCode === config.nodeCode;
        return (
          <div
            key={config.nodeCode}
            role="button"
            tabIndex={0}
            style={{
              border: isSelected ? '2px solid #165dff' : '1px solid #e5e6eb',
              borderRadius: 8,
              padding: 16,
              minWidth: 160,
              cursor: 'pointer',
              display: 'flex',
              alignItems: 'center',
              backgroundColor: isSelected ? '#f2f3ff' : 'transparent',
              transition: 'all 0.2s'
            }}
            onClick={() => handleSelect(config.nodeCode)}
          >
            <div>{config.nodeName}</div>
          </div>
        );
      })}
    </div>
  );
};
