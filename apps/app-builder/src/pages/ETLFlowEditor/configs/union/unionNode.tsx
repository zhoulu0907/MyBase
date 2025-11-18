import { ETLDrawerTab, etlEditorSignal } from '@onebase/common';
import { useSignals } from '@preact/signals-react/runtime';
import React, { useState } from 'react';
import styles from './index.module.less';

export const UnionNodeConfig: React.FC = () => {
  useSignals();

  const { curDrawerTab, nodeData, curNode } = etlEditorSignal;

  const [isModalVisible, setIsModalVisible] = useState(false);
  const [remark, setRemark] = useState<string>(nodeData.value[curNode.value.id]?.description || '');

  return (
    <div className={styles.config}>
      {curDrawerTab.value === ETLDrawerTab.DATA_CONFIG && <div>123123</div>}
      {curDrawerTab.value === ETLDrawerTab.DATA_PREVIEW && <div></div>}
      {curDrawerTab.value === ETLDrawerTab.NODE_REMARK && <div></div>}
    </div>
  );
};
