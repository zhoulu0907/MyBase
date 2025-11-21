import { ETLDrawerTab, etlEditorSignal } from '@onebase/common';
import { useSignals } from '@preact/signals-react/runtime';
import React from 'react';
import DataRemark from '../../components/dataRemark';
import SQLConfig from './config';
import DataPreview from './DataPreview';
import styles from './index.module.less';

export const SQLNodeConfig: React.FC = () => {
  useSignals();

  const { curDrawerTab } = etlEditorSignal;

  return (
    <div className={styles.config}>
      {curDrawerTab.value === ETLDrawerTab.DATA_CONFIG && <SQLConfig />}
      {curDrawerTab.value === ETLDrawerTab.DATA_PREVIEW && <DataPreview />}
      {curDrawerTab.value === ETLDrawerTab.NODE_REMARK && <DataRemark />}
    </div>
  );
};
