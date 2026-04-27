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
import { useFlowPageEditorSignal } from '@onebase/ui-kit';
import '@flowgram.ai/free-layout-editor/index.css';
import './styles/index.css';
import { nodeRegistries } from './nodes';
import { initialData } from './initial-data';
import { useEditorProps } from './hooks';
import { DemoTools } from './components/tools';
import { SidebarProvider, SidebarRenderer } from './components/sidebar';
import LeftNavBar from './components/left-nav-bar/index';
import { getDataById, save } from '@onebase/app';
import { useLocation } from 'react-router-dom';
import type { WorkflowJSON } from './editorType';
import { useFlowEditorStor } from '@/store/index';
import { debounce } from 'lodash-es';
import { useRefresh } from '@flowgram.ai/fixed-layout-editor';
const sourceNodeIDMap = new Map();

const Port3Ids: any = {
  approver_input: 'approver2',
  approver_output: 'approver1',
  cc_input: 'copyc2',
  cc_output: 'copyc1',
  executor_input: 'executor2',
  executor_output: 'executor1'
};

export const Editor = () => {
  const refresh = useRefresh();
  const { currentFlowId, setEditorRef, flowData, setFlowData, initFlowData, configData, setConfigData } =
    useFlowEditorStor();
  const { setFlowId } = useFlowPageEditorSignal;
  const ref = useRef<FreeLayoutPluginContext | null>(null);
  const [isLoading, setIsLoading] = useState(false);
  const location = useLocation();
  const searchParams = new URLSearchParams(location.search);
  const pageSetId = searchParams.get('pageSetId') || '';
  const getFlowData = async (currentFlowId: string) => {
    try {
      let currentJsonData = {};
      const res = await getDataById({ id: currentFlowId });
      if (res.globalConfig) {
        setConfigData(res.globalConfig);
      }
      if (!res.bpmDefJson) {
        currentJsonData = initialData;
      } else {
        const bpmDefJson = JSON.parse(res.bpmDefJson);
        currentJsonData = normalizeNodes(bpmDefJson);
      }
      if (res.businessUuid) {
        setFlowData(res);
      }
      ref?.current?.document.clear();
      setIsLoading(true);
      ref?.current?.document.fromJSON(currentJsonData);
      setTimeout(() => {
        ref?.current?.document.fitView();
      }, 10);
    } catch (e) {
      console.error('fail to get flow data:', e);
    }
  };
  const initEditor = () => {
    ref?.current?.document.clear();
    ref?.current?.document.fromJSON(initialData);
  };

  useEffect(() => {
    if (currentFlowId) {
      getFlowData(currentFlowId);
    } else {
      initFlowData();
      initEditor();
    }
  }, [currentFlowId]);

  const normalizeNodes = (obj: WorkflowJSON | undefined) => {
    // 审批人、抄送人、执行人 三个节点的端口连线变成了左右端口，需要改成上下端口
    const node3PortObx: any = {};
    const newNodes = obj?.nodes.map((node) => {
      if (node.type === 'approver' || node.type === 'cc' || node.type === 'executor') {
        node3PortObx[node.id] = node.type;
      }
      if (node.id.includes('branch') && node.meta) {
        node.meta.defaultPorts = [
          { type: 'input', portID: 'input-top', location: 'top' },
          { type: 'output', portID: 'output-bottom', location: 'bottom' }
        ];
      }
      if ('name' in node) {
        return { ...node, data: { ...(node.data || {}), name: node.name } };
      } else if (node.data && 'name' in node.data) {
        return { ...node, name: node.data.name };
      }
      return node;
    });
    obj?.edges.forEach((item) => {
      if (item.targetNodeID.includes('branch')) {
        item.targetPortID = 'input-top';
      }
      if (item.sourceNodeID.includes('branch')) {
        item.sourcePortID = 'output-bottom';
      }
      // targetPortID
      const inType: number = node3PortObx[item.targetNodeID];
      if (inType) {
        item.targetPortID = Port3Ids[`${inType}_input`];
      }
      //sourcePortID
      const outType: number = node3PortObx[item.sourceNodeID];
      if (outType) {
        item.sourcePortID = Port3Ids[`${outType}_output`];
      }
      if (item?.type) {
        sourceNodeIDMap.set(item.sourceNodeID + item.targetNodeID, item.type);
      } else {
        item.type = sourceNodeIDMap.get(item.sourceNodeID + item.targetNodeID) || 'PASS';
      }
    });
    return { ...obj, nodes: newNodes };
  };
  const editorProps = useEditorProps({ nodes: [], edges: [] }, nodeRegistries);

  const onSave = async () => {
    const data = ref?.current?.document.toJSON();
    const currentJsonData = normalizeNodes(data);
    const { id, flowCode, flowName, bpmVersion, bpmVersionAlias, bpmVersionStatus, businessUuid } = flowData;
    const params = {
      id: id || '',
      flowCode: flowCode || '',
      flowName: flowName || '',
      bpmVersion: bpmVersion || '',
      bpmVersionAlias: bpmVersionAlias || '',
      bpmVersionStatus: bpmVersionStatus || '',
      businessUuid: businessUuid || '',
      bpmDefJson: JSON.stringify(currentJsonData),
      globalConfig: configData
    };
    save(params).then((res: any) => {
      setFlowId(res);
    });
  };

  useEffect(() => {
    setEditorRef(ref.current);
  }, [ref.current]);
  useEffect(() => {
    const toDispose = ref?.current?.document.onContentChange(
      debounce((e) => {
        refresh();
      }, 200)
    );
    return () => toDispose?.dispose();
  }, []);

  // useEffect(() => {
  //   const dispose = ref?.current?.document.linesManager.onAvailableLinesChange((e) => {
  //     // 监听条件边线，第一条设为默认分支
  //     if (e.type === 'ADD_LINE') {
  //       const lineJson = e.toJSON();
  //       if (lineJson.sourceNodeID.startsWith('conditional_branch')) {
  //         const allLines = ref?.current?.document.linesManager.getAllLines();
  //         console.log(e, lineJson, allLines, '这里监听线条的新增和删除');
  //       }
  //     }
  //   });
  //   return () => dispose?.dispose();
  // }, []);

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
