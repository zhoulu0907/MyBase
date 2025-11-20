import { ETLDrawerTab, etlEditorSignal } from '@onebase/common';
import { useSignals } from '@preact/signals-react/runtime';
import React, { useState } from 'react';
import SQLConfig from './config';
import styles from './index.module.less';

export const SQLNodeConfig: React.FC = () => {
  useSignals();

  const { curDrawerTab, nodeData, curNode } = etlEditorSignal;

  const [isModalVisible, setIsModalVisible] = useState(false);
  const [remark, setRemark] = useState<string>(nodeData.value[curNode.value.id]?.description || '');

  return (
    <div className={styles.config}>
      {curDrawerTab.value === ETLDrawerTab.DATA_CONFIG && <SQLConfig />}
      {curDrawerTab.value === ETLDrawerTab.DATA_PREVIEW && <div></div>}
      {curDrawerTab.value === ETLDrawerTab.NODE_REMARK && <div></div>}
    </div>
  );
};
