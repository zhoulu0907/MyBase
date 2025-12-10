import { ETLDrawerTab, etlEditorSignal } from '@onebase/common';
import { useSignals } from '@preact/signals-react/runtime';
import { cloneDeep } from 'lodash-es';
import React, { useEffect, useRef, useState } from 'react';
import DataPreview from '../../components/dataPreview';
import { handlePreviewData, type PreviewData } from '../utils';
import UnionConfig from './config';
import styles from './index.module.less';

type UnionNodeConfigProps = { onRegisterSave?: (fn: () => void) => void };

export const UnionNodeConfig: React.FC<UnionNodeConfigProps> = ({ onRegisterSave }) => {
  useSignals();

  const { curDrawerTab, nodeData, curNode, graphData } = etlEditorSignal;
  const saveFnRef = useRef<(() => void) | null>(null);

  const [previewData, setPreviewData] = useState<PreviewData>({
    columns: [],
    data: []
  });

  const [newPayload, setNewPayload] = useState<any>(cloneDeep(nodeData.value[curNode.value.id]));

  useEffect(() => {
    if (curDrawerTab.value == ETLDrawerTab.DATA_PREVIEW) {
      handlePreviewData(
        graphData.value,
        nodeData.value,
        {
          ...curNode.value,
          ...newPayload
        },
        setPreviewData
      );
    }
  }, [curDrawerTab.value]);

  const handleRegisterFromChild = (fn: () => void) => {
    saveFnRef.current = fn;
    onRegisterSave?.(fn);
  };

  return (
    <div className={styles.config}>
      {curDrawerTab.value === ETLDrawerTab.DATA_CONFIG && (
        <UnionConfig onRegisterSave={handleRegisterFromChild} newPayload={newPayload} setNewPayload={setNewPayload} />
      )}
      {curDrawerTab.value === ETLDrawerTab.DATA_PREVIEW && (
        <DataPreview data={previewData.data} columns={previewData.columns} />
      )}
      {curDrawerTab.value === ETLDrawerTab.NODE_REMARK && <div></div>}
    </div>
  );
};
