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
import { useLocation, useParams } from 'react-router-dom';
import type { WorkflowJSON, FlowData } from './editorType';
import { getAppIdByPageSetId } from '@onebase/app';
const sourceNodeIDMap = new Map();
export const Editor = () => {
  //  const { appId } = useParams();
  const ref = useRef<FreeLayoutPluginContext | null>(null);
  const [isLoading, setIsLoading] = useState(false);
  const location = useLocation();
  const searchParams = new URLSearchParams(location.search);
  const pageSetId = searchParams.get('pageSetId') || '';

  const [flowData, setFlowData] = useState<FlowData>({});
  // 数据请求
  const getFlowData = async () => {
    try {
      // 85239398472974337
      const res = await getByBusinessId({ businessId: pageSetId });

      let useJsonData = {};
      // 如果没有数据就用初始化数据
      if (!res.bpmDefJson) {
        useJsonData = initialData;
      } else {
        // 否则用查询数据
        const bpmDefJson = JSON.parse(res.bpmDefJson);
        useJsonData = normalizeNodes(bpmDefJson);
      }

      // 保存流程数据
      if (res.businessId) {
        setFlowData(res);
      }
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
    // 处理连线数据的type
    obj?.edges.forEach((item) => {
      if (item?.type) {
        sourceNodeIDMap.set(item.sourceNodeID + item.targetNodeID, item.type);
      } else {
        item.type = sourceNodeIDMap.get(item.sourceNodeID + item.targetNodeID) || 'PASS';
      }
    });
    // 处理节点数据
    const newNodes = obj?.nodes.map((node) => {
      if ('name' in node) {
        return { ...node, data: { ...(node.data || {}), name: node.name } };
      } else if (node.data && 'name' in node.data) {
        return { ...node, name: node.data.name };
      }
      return node;
    });
    return { ...obj, nodes: newNodes };
  };
  const editorProps = useEditorProps({ nodes: [], edges: [] }, nodeRegistries);

  // 保存数据
  const onSave = async () => {
    // 查找appid
    const appId = await getAppIdByPageSetId({ pageSetId });
    // 处理数据
    const data = ref?.current?.document.toJSON();
    const useJsonData = normalizeNodes(data);
    
    const { id, flowCode, flowName, version, versionAlias, versionStatus, businessId } = flowData;
    const params = {
      id: id || '',
      flowCode: flowCode || '',
      flowName: flowName || '',
      version: version || '',
      versionAlias: versionAlias || '',
      versionStatus: versionStatus || '',
      businessId: businessId || '',
      appId,
      bpmDefJson: JSON.stringify(useJsonData)
    };
    save(params).then((res: any) => {
      console.log(res);
    });
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
