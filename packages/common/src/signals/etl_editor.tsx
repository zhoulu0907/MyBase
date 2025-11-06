import { signal } from '@preact/signals-react';

export const createETLEditorSignal = () => {
  const curNode = signal<any>({});

  const setCurNode = (newNode: any) => {
    curNode.value = newNode;
  };

  const clearCurNode = () => {
    curNode.value = {};
  };

  return {
    curNode,
    setCurNode,
    clearCurNode
  };
};

export const etlEditorSignal = createETLEditorSignal();
