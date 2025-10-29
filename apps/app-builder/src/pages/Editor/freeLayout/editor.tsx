/**
 * Copyright (c) 2025 Bytedance Ltd. and/or its affiliates
 * SPDX-License-Identifier: MIT
 */
import { useRef, useEffect, useState } from 'react';
import {
  EditorRenderer,
  FreeLayoutEditorProvider,
  type FreeLayoutPluginContext
} from '@flowgram.ai/free-layout-editor';
import { Button } from '@douyinfe/semi-ui';
import '@flowgram.ai/free-layout-editor/index.css';
import './styles/index.css';
import { nodeRegistries } from './nodes';
import { initialData } from './initial-data';
import { useEditorProps } from './hooks';
import { DemoTools } from './components/tools';
import { SidebarProvider, SidebarRenderer } from './components/sidebar';
import LeftNavBar from './components/left-nav-bar/index';
import { GlobalConfigProvider } from './components/globalConfig/components/globalConfigProvider';
import { getByBusinessId, save } from '../../../../../../packages/app/src/services/index';
// import type { WorkflowJSON } from '@flowgram.ai/free-layout-editor';
import type { WorkflowJSON } from './editorType';
const sourceNodeIDMap = new Map();
export const Editor = () => {
  const ref = useRef<FreeLayoutPluginContext | null>(null);
  const [isLoading, setIsLoading] = useState(false);

  // 数据请求
  const getFlowData = async () => {
    try {
      const res = await getByBusinessId({ businessId: '85239398472974337' });
      let useJsonData = {};
      console.log(res);
      if (!res.bpmDefJson) {
        useJsonData = initialData;
      } else {
        const bpmDefJson = JSON.parse(res.bpmDefJson);
        useJsonData = normalizeNodes(bpmDefJson);
      }

      console.log(useJsonData);

      setIsLoading(true);
      ref?.current?.document.fromJSON(useJsonData);

      setTimeout(() => {
        // 加载后触发画布的 fitview 让节点自动居中
        ref?.current?.document.fitView();
      }, 10);
    } catch (e) {
      console.error('fail to get flow data:', e);
    }
  };
  useEffect(() => {
    getFlowData();
  }, []);

  // 格式化数据
  const normalizeNodes = (obj: WorkflowJSON | undefined) => {
    console.log(obj);

    obj?.edges.forEach((item) => {
      if (item?.type) {
        sourceNodeIDMap.set(item.sourceNodeID + item.targetNodeID, item.type);
      } else {
        item.type = sourceNodeIDMap.get(item.sourceNodeID + item.targetNodeID);
      }
    });
    const newNodes = obj?.nodes.map((node) => {
      if ('name' in node) {
        return { ...node, data: { ...(node.data || {}), name: node.name } };
      } else if (node.data && 'name' in node.data) {
        // const name = node.data.name;
        // delete node.data.name;
        // if (Object.keys(node.data).length === 0) {
        //   delete node.data;
        // }
        return { ...node, name: node.data.name };
      }
      return node;
    });
    return { ...obj, nodes: newNodes };
  };
  const editorProps = useEditorProps({ nodes: [], edges: [] }, nodeRegistries);

  // 保存数据
  const onSave = () => {
    console.log(sourceNodeIDMap);

    const data = ref?.current?.document.toJSON();
    const useJsonData = normalizeNodes(data);
    const params = {
      xid: '1431276185660297216',
      flowCode: '1024001',
      flowName: '串行-简单',
      version: 'V1',
      versionAlias: '流程版本V1',
      versionStatus: 'designing',
      businessId: '85239398472974337',
      bpmDefJson: JSON.stringify(useJsonData)
    };
    save(params).then((res: any) => {
      console.log(res);
    });
    console.log(useJsonData, '保存数据');
  };
  return (
    <div className="doc-free-feature-overview">
      {/* <Button onClick={() => onSave()}>保存</Button> */}
      {
        <FreeLayoutEditorProvider {...editorProps} ref={ref}>
          <GlobalConfigProvider>
            <SidebarProvider>
              <div className="demo-container">
                <EditorRenderer className="demo-editor" />
              </div>
              <DemoTools onSave={onSave} />
              <LeftNavBar></LeftNavBar>
              <SidebarRenderer />
            </SidebarProvider>
          </GlobalConfigProvider>
        </FreeLayoutEditorProvider>
      }
    </div>
  );
};
