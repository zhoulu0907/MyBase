import { ETLDrawerTab, etlEditorSignal } from '@onebase/common';
import { useSignals } from '@preact/signals-react/runtime';
import { cloneDeep } from 'lodash-es';
import React, { useEffect, useRef, useState } from 'react';
import DataPreview from '../../components/dataPreview';
import DataRemark from '../../components/dataRemark';
import { handlePreviewData, type PreviewData } from '../utils';
import SQLConfig from './config';
import styles from './index.module.less';

type SQLNodeConfigProps = { onRegisterSave?: (fn: () => void) => void };

export const SQLNodeConfig: React.FC<SQLNodeConfigProps> = ({ onRegisterSave }) => {
  useSignals();

  const { curDrawerTab, nodeData, curNode, graphData } = etlEditorSignal;
  const saveFnRef = useRef<(() => void) | null>(null);

  const [previewData, setPreviewData] = useState<PreviewData>({
    columns: [],
    data: []
  });
  const [newPayload, setNewPayload] = useState<any>(cloneDeep(nodeData.value[curNode.value.id]));

  const handleRegisterFromChild = (fn: () => void) => {
    saveFnRef.current = fn;
    onRegisterSave?.(fn);
  };

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

  return (
    <div className={styles.config}>
      {curDrawerTab.value === ETLDrawerTab.DATA_CONFIG && (
        <SQLConfig onRegisterSave={handleRegisterFromChild} newPayload={newPayload} setNewPayload={setNewPayload} />
      )}
      {curDrawerTab.value === ETLDrawerTab.DATA_PREVIEW && (
        <DataPreview data={previewData.data} columns={previewData.columns} />
      )}
      {curDrawerTab.value === ETLDrawerTab.NODE_REMARK && <DataRemark />}
    </div>
  );
};
