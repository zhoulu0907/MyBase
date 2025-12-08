import { ETLDrawerTab, etlEditorSignal } from '@onebase/common';
import { useSignals } from '@preact/signals-react/runtime';
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

  const handleRegisterFromChild = (fn: () => void) => {
    saveFnRef.current = fn;
    onRegisterSave?.(fn);
  };

  const [previewData, setPreviewData] = useState<PreviewData>({
    columns: [],
    data: []
  });

  useEffect(() => {
    if (curDrawerTab.value == ETLDrawerTab.DATA_PREVIEW) {
      handlePreviewData(graphData.value, nodeData.value, curNode.value, setPreviewData);
    }
  }, [curDrawerTab.value]);

  return (
    <div className={styles.config}>
      {curDrawerTab.value === ETLDrawerTab.DATA_CONFIG && <SQLConfig onRegisterSave={handleRegisterFromChild} />}
      {curDrawerTab.value === ETLDrawerTab.DATA_PREVIEW && (
        <DataPreview data={previewData.data} columns={previewData.columns} />
      )}
      {curDrawerTab.value === ETLDrawerTab.NODE_REMARK && <DataRemark />}
    </div>
  );
};
