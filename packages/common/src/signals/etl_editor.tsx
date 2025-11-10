import { signal } from '@preact/signals-react';
import { ETLDrawerTab } from 'src/types';

export const createETLEditorSignal = () => {
  const curNode = signal<any>({});

  const setCurNode = (newNode: any) => {
    curNode.value = newNode;
  };

  const clearCurNode = () => {
    curNode.value = {};
  };

  const nodeData = signal<{ [key: string]: any }>({});

  const setNodeData = (nodeID: string, data: { [key: string]: any }) => {
    nodeData.value = { ...nodeData.value, [nodeID]: data };
  };

  const setAllNodeData = (data: object) => {
    nodeData.value = data;
  };

  const deleteNodeData = (nodeID: string) => {
    const { [nodeID]: _, ...rest } = nodeData.value;
    nodeData.value = rest;
  };

  const clearNodeData = () => {
    nodeData.value = {};
  };

  const curDrawerTab = signal<string>(ETLDrawerTab.DATA_PREVIEW);

  const setCurDrawerTab = (tab: string) => {
    curDrawerTab.value = tab;
  };

  const resetCurDrawerTab = () => {
    curDrawerTab.value = ETLDrawerTab.DATA_PREVIEW;
  };

  const graphData = signal<any>({});
  const setGraphData = (data: any) => {
    graphData.value = data;
  };

  return {
    curNode,
    setCurNode,
    clearCurNode,

    nodeData,
    setNodeData,
    setAllNodeData,
    deleteNodeData,
    clearNodeData,

    curDrawerTab,
    setCurDrawerTab,
    resetCurDrawerTab,

    graphData,
    setGraphData
  };
};

export const etlEditorSignal = createETLEditorSignal();
