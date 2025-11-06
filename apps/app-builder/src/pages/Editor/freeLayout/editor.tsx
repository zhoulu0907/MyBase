/**
 * Copyright (c) 2025 Bytedance Ltd. and/or its affiliates
 * SPDX-License-Identifier: MIT
 */
import { useRef, useEffect, useState, useContext } from 'react';
import {
  EditorRenderer,
  FreeLayoutEditorProvider,
  type FreeLayoutPluginContext
} from '@flowgram.ai/free-layout-editor';
import { useFlowPageEditorSignal } from '@onebase/ui-kit';
import { useFlowEditorStor } from '@/store/index';
import '@flowgram.ai/free-layout-editor/index.css';
import './styles/index.css';
import { nodeRegistries } from './nodes';
import { initialData } from './initial-data';
import { useEditorProps } from './hooks';
import { DemoTools } from './components/tools';
import { SidebarProvider, SidebarRenderer } from './components/sidebar';
import LeftNavBar from './components/left-nav-bar/index';
import { getDataById, save } from '../../../../../../packages/app/src/services/index';
import { useLocation } from 'react-router-dom';
import type { WorkflowJSON, FlowData } from './editorType';
import { getAppIdByPageSetId } from '@onebase/app';
import { GlobalConfigContext } from './context/globalConfigContext';
const sourceNodeIDMap = new Map();
export const Editor = () => {
  const { configData, setConfigData } = useContext(GlobalConfigContext);
  const { setFlowId } = useFlowPageEditorSignal;
  const ref = useRef<FreeLayoutPluginContext | null>(null);
  const [isLoading, setIsLoading] = useState(false);
  const location = useLocation();
  const searchParams = new URLSearchParams(location.search);
  const pageSetId = searchParams.get('pageSetId') || '';
  const [flowData, setFlowData] = useState<FlowData>({});
  const { currentFlowId } = useFlowEditorStor();

  const getFlowData = async (currentFlowId: string) => {
    try {
      const res = await getDataById({ id: currentFlowId });
      if (res.globalConfig) {
        setConfigData(res.globalConfig);
      }
      console.log(ref?.current);
      
      let useJsonData = {};
      if (!res.bpmDefJson) {
        useJsonData = initialData;
      } else {
        const bpmDefJson = JSON.parse(res.bpmDefJson);
        useJsonData = normalizeNodes(bpmDefJson);
      }
      if (res.businessId) {
        setFlowData(res);
      }
      ref?.current?.document.clear();
      setIsLoading(true);
      ref?.current?.document.fromJSON(useJsonData);
      setTimeout(() => {
        ref?.current?.document.fitView();
      }, 10);
    } catch (e) {
      console.error('fail to get flow data:', e);
    }
  };
  useEffect(() => {
    currentFlowId && getFlowData(currentFlowId);
  }, [currentFlowId]);

  const normalizeNodes = (obj: WorkflowJSON | undefined) => {
    obj?.edges.forEach((item) => {
      if (item?.type) {
        sourceNodeIDMap.set(item.sourceNodeID + item.targetNodeID, item.type);
      } else {
        item.type = sourceNodeIDMap.get(item.sourceNodeID + item.targetNodeID) || 'PASS';
      }
    });
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

  const onSave = async () => {
    const appId = await getAppIdByPageSetId({ pageSetId });
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
      businessId: businessId || pageSetId,
      appId,
      bpmDefJson: JSON.stringify(useJsonData),
      globalConfig: configData
    };
    save(params).then((res: any) => {
      setFlowId(res);
    });
  };
  return (
    <div className="doc-free-feature-overview">
      {
        <FreeLayoutEditorProvider {...editorProps} ref={ref}>
          <SidebarProvider>
            <div className="demo-container">
              <EditorRenderer className="demo-editor" />
            </div>
            <DemoTools onSave={onSave} />
            <LeftNavBar></LeftNavBar>
            <SidebarRenderer />
          </SidebarProvider>
        </FreeLayoutEditorProvider>
      }
    </div>
  );
};
