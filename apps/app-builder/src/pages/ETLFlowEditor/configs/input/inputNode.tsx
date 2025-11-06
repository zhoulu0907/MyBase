import { Input } from '@arco-design/web-react';
import { ETLDrawerTab, etlEditorSignal } from '@onebase/common';
import { useSignals } from '@preact/signals-react/runtime';
import React from 'react';
import styles from './index.module.less';

const { TextArea } = Input;

export const InputNodeConfig: React.FC = () => {
  useSignals();

  const { curDrawerTab } = etlEditorSignal;

  return (
    <div className={styles.config}>
      {curDrawerTab.value === ETLDrawerTab.NODE_REMARK && (
        <TextArea placeholder="请输入节点备注" autoSize={{ minRows: 3, maxRows: 6 }} allowClear />
      )}
    </div>
  );
};
