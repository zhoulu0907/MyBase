import { Drawer } from '@arco-design/web-react';
import { etlEditorSignal, ETLNodeType } from '@onebase/common';
import { useSignals } from '@preact/signals-react/runtime';
import React, { useRef } from 'react';
import { InputNodeConfig, JoinNodeConfig, OutputNodeConfig, SQLNodeConfig, UnionNodeConfig } from '../../configs';
import DrawerHeader from './components/drawerHeader';

interface DrawerInitPageProps {
  refWrapper: React.RefObject<HTMLDivElement>;
}

const NodeConfigPage: React.FC<DrawerInitPageProps> = ({ refWrapper }) => {
  useSignals();

  const { curNode, nodeData, setCurNode, clearCurNode } = etlEditorSignal;
  const configRef = useRef<(() => void) | null>(null);

  const handleOk = (title: string) => {
    // 直接调用当前 config 的保存方法
    try {
      configRef.current?.();
      etlEditorSignal.nodeData.value = {
        ...nodeData.value,
        [curNode.value.id]: {
          ...(nodeData.value[curNode.value.id] || {}),
          title,
        },
      };
      // 保存成功后再关 Drawer
      etlEditorSignal.clearCurNode();
      configRef.current = null;
    } catch (e) {}
  };

  const handleRegisterSave = (fn: () => void) => {
    configRef.current = fn;
  };

  return (
    <Drawer
      height={600}
      title={<DrawerHeader onOk={handleOk} />}
      getPopupContainer={() => refWrapper && refWrapper?.current!}
      visible={etlEditorSignal.curNode.value.id !== undefined && etlEditorSignal.curNode.value.id !== ''}
      footer={null}
      placement={'bottom'}
      style={{ zIndex: 100 }}
      onCancel={() => {
        etlEditorSignal.clearCurNode();
        configRef.current = null;
      }}
    >
      {curNode.value.flowNodeType === ETLNodeType.INPUT_NODE && <InputNodeConfig onRegisterSave={handleRegisterSave} />}
      {curNode.value.flowNodeType === ETLNodeType.OUTPUT_NODE && (
        <OutputNodeConfig onRegisterSave={handleRegisterSave} />
      )}
      {curNode.value.flowNodeType === ETLNodeType.UNION_NODE && <UnionNodeConfig onRegisterSave={handleRegisterSave} />}
      {curNode.value.flowNodeType === ETLNodeType.JOIN_NODE && <JoinNodeConfig onRegisterSave={handleRegisterSave} />}
      {curNode.value.flowNodeType === ETLNodeType.SQL_NODE && <SQLNodeConfig onRegisterSave={handleRegisterSave} />}
    </Drawer>
  );
};

export default NodeConfigPage;
