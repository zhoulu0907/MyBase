import DynamicForm from '@/components/DynamicForm';
import { type ISchema } from '@formily/react';
import { type FlowConnector } from '@onebase/app';
import React, { useMemo } from 'react';

interface ActionFormConfigProps {
  connector: FlowConnector | null;
  actionKey: string | null;
}

export const ActionFormConfig: React.FC<ActionFormConfigProps> = ({ connector, actionKey }) => {
  // 将 action 配置转换为 Formily schema
  const actionSchema = useMemo<ISchema | null>(() => {
    console.log('connector :', connector);
    console.log('actionKey :', actionKey);
    if (!connector?.config?.properties || !actionKey) {
      return null;
    }

    const actionConfig = connector.config.properties[actionKey];
    if (!actionConfig) {
      return null;
    }
    console.log('actionConfig :', actionConfig);

    const schema: ISchema = actionConfig;

    console.log('schema :', schema);

    return schema;
  }, [connector, actionKey]);

  if (!actionSchema) {
    return <div style={{ color: '#999', padding: '32px 0', textAlign: 'center' }}>请先选择动作</div>;
  }

  return <DynamicForm schema={actionSchema} />;
};
