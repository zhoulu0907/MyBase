import FieldIcon from '@/assets/images/etl/field.svg';
import { Button } from '@arco-design/web-react';
import { listETLTables, previewETLDatasource, type ETLTable } from '@onebase/app';
import { ETLDrawerTab, etlEditorSignal } from '@onebase/common';
import { useSignals } from '@preact/signals-react/runtime';
import React, { useEffect, useState } from 'react';
import DataPreview from '../../components/dataPreview';
import DataRemark from '../../components/dataRemark';
import DatasourceModal from './components/datasourceModal';
import styles from './index.module.less';

export const InputNodeConfig: React.FC = () => {
  useSignals();

  const { curDrawerTab, nodeData, curNode } = etlEditorSignal;

  const [isModalVisible, setIsModalVisible] = useState(false);
  const [tables, setTables] = useState<ETLTable[]>([]);
  const [previewData, setPreviewData] = useState<{
    columns: any[];
    data: any[];
  }>({
    columns: [],
    data: []
  });

  useEffect(() => {
    if (nodeData.value[curNode.value.id]?.config?.datasourceId && nodeData.value[curNode.value.id]?.config?.tableId) {
      handleListETLTables(nodeData.value[curNode.value.id]?.config?.datasourceId);
    }

    handlePreviewData();
  }, [nodeData.value[curNode.value.id]?.config?.tableId]);

  const handleListETLTables = async (datasourceId: string) => {
    const res = await listETLTables({ id: datasourceId });
    setTables(res);
  };

  const handlePreviewData = async () => {
    const datasourceId = nodeData.value[curNode.value.id]?.config?.datasourceId;
    const tableId = nodeData.value[curNode.value.id]?.config?.tableId;

    if (!datasourceId || !tableId) {
      return;
    }
    const res = await previewETLDatasource({
      datasourceId: datasourceId,
      tableId: tableId
    });

    console.log('res: ', res);
    if (res) {
      setPreviewData(res);
    }
  };

  return (
    <div className={styles.config}>
      {curDrawerTab.value === ETLDrawerTab.DATA_CONFIG && (
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
            {nodeData.value[curNode.value.id]?.config?.tableId && (
              <div className={styles.dataSourceContent}>
                <div className={styles.dataSourceName}>
                  {
                    tables.find((table: ETLTable) => table.id === nodeData.value[curNode.value.id]?.config?.tableId)
                      ?.name
                  }
                </div>
                <div className={styles.selectedFields}>已选字段</div>

                <div className={styles.dataSourceContentItems}>
                  {nodeData.value[curNode.value.id]?.config?.fields.map((field: any) => (
                    <div key={field.fieldName} className={styles.dataSourceContentItem}>
                      <img src={FieldIcon} alt="column" />
                      {field.fieldName}
                    </div>
                  ))}
                </div>
              </div>
            )}
          </div>
          {/* <div className={styles.dataPreviewContent}>
            {<DataPreview data={previewData.data} columns={previewData.columns} />}
          </div> */}
        </div>
      )}
      {curDrawerTab.value === ETLDrawerTab.DATA_PREVIEW && (
        <DataPreview data={previewData.data} columns={previewData.columns} />
      )}
      {curDrawerTab.value === ETLDrawerTab.NODE_REMARK && <DataRemark />}

      <DatasourceModal
        isModalVisible={isModalVisible}
        onClose={() => setIsModalVisible(false)}
        onOk={() => setIsModalVisible(false)}
      />
    </div>
  );
};
