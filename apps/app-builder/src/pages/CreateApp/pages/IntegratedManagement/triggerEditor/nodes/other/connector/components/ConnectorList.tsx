import type { FormInstance } from '@arco-design/web-react';
import type { FlowConnector } from '@onebase/app';
import React from 'react';

interface ConnectorListProps {
  connectorList: FlowConnector[];
  form: FormInstance;
  onSelect?: (connector: FlowConnector) => void;
}

export const ConnectorList: React.FC<ConnectorListProps> = ({ connectorList, form, onSelect }) => {
  const handleSelect = (connector: FlowConnector) => {
    form.setFieldValue('connectorId', connector.id);
    form.setFieldValue('connectorUuid', connector.connectorUuid);
    onSelect?.(connector);
  };

  if (!connectorList || connectorList.length === 0) {
    return <div style={{ color: '#999', padding: '32px 0', textAlign: 'center' }}>暂无可用连接，请先创建连接实例</div>;
  }

  return (
    <div style={{ display: 'flex', flexWrap: 'wrap', gap: 16 }}>
      {connectorList.map((connector) => (
        <div
          key={connector.id}
          role="button"
          tabIndex={0}
          style={{
            border: '1px solid #e5e6eb',
            borderRadius: 8,
            padding: 16,
            minWidth: 160,
            cursor: 'pointer',
            display: 'flex',
            flexDirection: 'column',
            gap: 8
          }}
          onClick={() => handleSelect(connector)}
          onKeyDown={(e) => {
            if (e.key === 'Enter' || e.key === ' ') {
              e.preventDefault();
              handleSelect(connector);
            }
          }}
        >
          <div style={{ fontWeight: 500 }}>{connector.connectorName}</div>
          {connector.connectorVersion && (
            <div style={{ color: '#666', fontSize: 12 }}>版本: {connector.connectorVersion}</div>
          )}
          {connector.description && <div style={{ color: '#999', fontSize: 12 }}>{connector.description}</div>}
        </div>
      ))}
    </div>
  );
};
