import { Button, Input } from '@arco-design/web-react';
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
      {curDrawerTab.value === ETLDrawerTab.DATA_PREVIEW && (
        <div className={styles.dataPreview}>
          <div className={styles.dataSource}>
            <div className={styles.dataSourceHeader}>
              <div className={styles.dataSourceHeaderLeft}>输入源</div>
              <div className={styles.dataSourceHeaderRight}>
                <Button type="text">更改输入源</Button>
              </div>
            </div>
            <div className={styles.dataSourceContent}>
              <div className={styles.dataSourceName}>数据源名称</div>
              <div className={styles.selectedFields}>已选字段</div>
            </div>
          </div>
          <div className={styles.dataPreviewContent}></div>
        </div>
      )}
      {curDrawerTab.value === ETLDrawerTab.NODE_REMARK && (
        <TextArea placeholder="请输入节点备注" autoSize={{ minRows: 3, maxRows: 6 }} allowClear />
      )}
    </div>
  );
};
