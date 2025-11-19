import { Drawer } from '@arco-design/web-react';
import { etlEditorSignal, ETLNodeType } from '@onebase/common';
import { useSignals } from '@preact/signals-react/runtime';
import React from 'react';
import { InputNodeConfig, OutputNodeConfig, UnionNodeConfig } from '../../configs';
import DrawerHeader from './components/drawerHeader';

interface DrawerInitPageProps {
  refWrapper: React.RefObject<HTMLDivElement>;
}

const NodeConfigPage: React.FC<DrawerInitPageProps> = ({ refWrapper }) => {
  useSignals();

  const { curNode, setCurNode, clearCurNode } = etlEditorSignal;

  return (
    <Drawer
      height={600}
      title={<DrawerHeader />}
      getPopupContainer={() => refWrapper && refWrapper?.current!}
      visible={etlEditorSignal.curNode.value.id !== undefined && etlEditorSignal.curNode.value.id !== ''}
      footer={null}
      placement={'bottom'}
      style={{ zIndex: 100 }}
      onOk={() => {
        etlEditorSignal.clearCurNode();
      }}
      onCancel={() => {
        etlEditorSignal.clearCurNode();
      }}
    >
      {curNode.value.flowNodeType === ETLNodeType.INPUT_NODE && <InputNodeConfig />}
      {curNode.value.flowNodeType === ETLNodeType.OUTPUT_NODE && <OutputNodeConfig />}
      {curNode.value.flowNodeType === ETLNodeType.UNION_NODE && <UnionNodeConfig />}
    </Drawer>
  );
};

export default NodeConfigPage;
