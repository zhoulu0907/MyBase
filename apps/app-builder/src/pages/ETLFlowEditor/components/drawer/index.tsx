import { Drawer } from '@arco-design/web-react';
import { etlEditorSignal, ETLNodeType } from '@onebase/common';
import { useSignals } from '@preact/signals-react/runtime';
import React, { createContext, useContext, useEffect, useMemo, useRef, useState } from 'react';
import { InputNodeConfig, JoinNodeConfig, OutputNodeConfig, SQLNodeConfig, UnionNodeConfig } from '../../configs';
import DrawerHeader from './components/drawerHeader';

interface DrawerInitPageProps {
  refWrapper: React.RefObject<HTMLDivElement>;
}

// 创建 Context 用于传递备注值
interface RemarkContextType {
  remark: string;
  setRemark: (value: string) => void;
}

const RemarkContext = createContext<RemarkContextType | null>(null);

export const useRemarkContext = () => {
  const context = useContext(RemarkContext);
  if (!context) {
    throw new Error('useRemarkContext must be used within RemarkContext.Provider');
  }
  return context;
};

const NodeConfigPage: React.FC<DrawerInitPageProps> = ({ refWrapper }) => {
  useSignals();

  const { curNode, nodeData } = etlEditorSignal;
  const configRef = useRef<(() => void) | null>(null);
  // 维护备注的临时状态，只有在点击确定时才保存
  const [remark, setRemark] = useState<string>('');

  // 当节点变化时，同步备注状态
  useEffect(() => {
    if (curNode.value.id) {
      const currentDescription = nodeData.value[curNode.value.id]?.description || '';
      setRemark(currentDescription);
    }
  }, [curNode.value.id]);

  const handleOk = async (title: string) => {

    // 直接调用当前 config 的保存方法
    try {
      await configRef.current?.();
      // 保存后，确保 description 被正确保留
      // 因为 configRef.current 可能会调用 setNodeData，完全替换节点数据
      etlEditorSignal.nodeData.value = {
        ...nodeData.value,
        [curNode.value.id]: {
          ...(nodeData.value[curNode.value.id] || {}),
          title,
          description: remark
        }
      };

      // 保存成功后再关 Drawer
      etlEditorSignal.clearCurNode();
      configRef.current = null;
    } catch (e) {
        console.error('error: ', e);
    }
  };

  const handleRegisterSave = (fn: () => void) => {
    configRef.current = fn;
  };

  // 使用 useMemo 优化 Context value，避免每次渲染都创建新对象
  // setRemark 是 useState 返回的稳定函数，不需要包含在依赖中
  const remarkContextValue = useMemo(() => ({ remark, setRemark }), [remark]);

  return (
    <RemarkContext.Provider value={remarkContextValue}>
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
    </RemarkContext.Provider>
  );
};

export default NodeConfigPage;
