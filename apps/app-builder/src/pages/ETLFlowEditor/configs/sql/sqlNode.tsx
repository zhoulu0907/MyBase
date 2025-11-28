import { ETLDrawerTab, etlEditorSignal } from '@onebase/common';
import { useSignals } from '@preact/signals-react/runtime';
import React, { useRef } from 'react';
import DataRemark from '../../components/dataRemark';
import SQLConfig from './config';
import DataPreview from './DataPreview';
import styles from './index.module.less';

type SQLNodeConfigProps = { onRegisterSave?: (fn: () => void) => void };

export const SQLNodeConfig: React.FC<SQLNodeConfigProps> = ({ onRegisterSave }) => {
  useSignals();

  const { curDrawerTab } = etlEditorSignal;
  const saveFnRef = useRef<(() => void) | null>(null);

  const handleRegisterFromChild = (fn: () => void) => {
    saveFnRef.current = fn;
    onRegisterSave?.(fn);
  };

  return (
    <div className={styles.config}>
      {curDrawerTab.value === ETLDrawerTab.DATA_CONFIG && <SQLConfig onRegisterSave={handleRegisterFromChild} />}
      {curDrawerTab.value === ETLDrawerTab.DATA_PREVIEW && <DataPreview />}
      {curDrawerTab.value === ETLDrawerTab.NODE_REMARK && <DataRemark />}
    </div>
  );
};
