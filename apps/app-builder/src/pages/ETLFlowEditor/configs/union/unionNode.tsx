import { ETLDrawerTab, etlEditorSignal } from '@onebase/common';
import { useSignals } from '@preact/signals-react/runtime';
import React, { useRef, useState } from 'react';
import UnionConfig from './config';
import styles from './index.module.less';

type UnionNodeConfigProps = { onRegisterSave?: (fn: () => void) => void };

export const UnionNodeConfig: React.FC<UnionNodeConfigProps> = ({ onRegisterSave }) => {
  useSignals();

  const { curDrawerTab, nodeData, curNode } = etlEditorSignal;
  const saveFnRef = useRef<(() => void) | null>(null);

  const [isModalVisible, setIsModalVisible] = useState(false);
  const [remark, setRemark] = useState<string>(nodeData.value[curNode.value.id]?.description || '');

  const handleRegisterFromChild = (fn: () => void) => {
    saveFnRef.current = fn;
    onRegisterSave?.(fn);
  };

  return (
    <div className={styles.config}>
      {curDrawerTab.value === ETLDrawerTab.DATA_CONFIG && <UnionConfig onRegisterSave={handleRegisterFromChild} />}
      {curDrawerTab.value === ETLDrawerTab.DATA_PREVIEW && <div></div>}
      {curDrawerTab.value === ETLDrawerTab.NODE_REMARK && <div></div>}
    </div>
  );
};
