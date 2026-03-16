import type { FormInstance } from '@arco-design/web-react';
import React from 'react';

export interface ActionItem {
  key: string;
  title?: string;
  description?: string;
}

interface ActionListProps {
  items: ActionItem[];
  form: FormInstance;
  onSelect?: (item: ActionItem) => void;
}

export const ActionList: React.FC<ActionListProps> = ({ items, form, onSelect }) => {
  const selectedActionName = form.getFieldValue('actionName');

  const handleSelect = (item: ActionItem) => {
    form.setFieldValue('actionName', item.key);
    onSelect?.(item);
  };

  if (!items || items.length === 0) {
    return <div style={{ color: '#999', padding: '32px 0', textAlign: 'center' }}>该连接器暂无可用动作</div>;
  }

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: 16 }}>
      {items.map((item) => {
        const isSelected = selectedActionName === item.key;
        return (
          <div
            key={item.key}
            role="button"
            tabIndex={0}
            style={{
              border: isSelected ? '2px solid #165dff' : '1px solid #e5e6eb',
              borderRadius: 8,
              padding: 16,
              cursor: 'pointer',
              display: 'flex',
              flexDirection: 'column',
              gap: 8,
              backgroundColor: isSelected ? '#f2f3ff' : 'transparent',
              transition: 'all 0.2s'
            }}
            onClick={() => handleSelect(item)}
            onKeyDown={(e) => {
              if (e.key === 'Enter' || e.key === ' ') {
                e.preventDefault();
                handleSelect(item);
              }
            }}
          >
            <div style={{ fontWeight: 500 }}>{item.title ?? item.key}</div>
            {item.description && <div style={{ color: '#999', fontSize: 12 }}>{item.description}</div>}
          </div>
        );
      })}
    </div>
  );
};
