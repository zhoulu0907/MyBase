import TableIcon from '@/assets/images/etl/table.svg';
import { Button, Input } from '@arco-design/web-react';
import { ETLDrawerTab, etlEditorSignal } from '@onebase/common';
import { useSignals } from '@preact/signals-react/runtime';
import React, { useState } from 'react';
import DatasourceModal from './components/datasourceModal';
import styles from './index.module.less';

const { TextArea } = Input;

export const InputNodeConfig: React.FC = () => {
  useSignals();

  const { curDrawerTab, nodeData, curNode } = etlEditorSignal;

  const [isModalVisible, setIsModalVisible] = useState(false);

  return (
    <div className={styles.config}>
      {curDrawerTab.value === ETLDrawerTab.DATA_PREVIEW && (
        <div className={styles.dataPreview}>
          <div className={styles.dataSource}>
            <div className={styles.dataSourceHeader}>
              <div className={styles.dataSourceHeaderLeft}>输入源</div>
              <div className={styles.dataSourceHeaderRight}>
                <Button type="text" onClick={() => setIsModalVisible(true)}>
                  更改输入源
                </Button>
              </div>
            </div>
            <div className={styles.dataSourceContent}>
              <div className={styles.dataSourceName}>{nodeData.value[curNode.value.id]?.config?.datasourceId}</div>
              <div className={styles.selectedFields}>已选字段</div>

              <div className={styles.dataSourceContentItems}>
                {nodeData.value[curNode.value.id]?.config?.fields.map((field: any) => (
                  <div key={field.fieldId} className={styles.dataSourceContentItem}>
                    <img src={TableIcon} alt="column" />
                    {field.fieldName}
                  </div>
                ))}
              </div>
            </div>
          </div>
          <div className={styles.dataPreviewContent}></div>
        </div>
      )}
      {curDrawerTab.value === ETLDrawerTab.NODE_REMARK && (
        <TextArea placeholder="请输入节点备注" autoSize={{ minRows: 3, maxRows: 6 }} allowClear />
      )}

      <DatasourceModal
        isModalVisible={isModalVisible}
        onClose={() => setIsModalVisible(false)}
        onOk={() => setIsModalVisible(false)}
      />
    </div>
  );
};
