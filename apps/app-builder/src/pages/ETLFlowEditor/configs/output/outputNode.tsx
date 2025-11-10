import { Button, Grid, Input, Message, Select } from '@arco-design/web-react';
import {
  listAppETLDatasource,
  listETLTableColumns,
  listETLTables,
  type ELTColumn,
  type ETLDatasourceOption,
  type ETLTable
} from '@onebase/app';
import { ETLDrawerTab, etlEditorSignal, getHashQueryParam } from '@onebase/common';
import { useSignals } from '@preact/signals-react/runtime';
import React, { useEffect, useState } from 'react';
import FieldModal, { type FieldMapping } from './components/fieldModal';
import styles from './index.module.less';

const Row = Grid.Row;
const Col = Grid.Col;

const { TextArea } = Input;

export const OutputNodeConfig: React.FC = () => {
  useSignals();

  const { curDrawerTab, setNodeData, curNode, nodeData } = etlEditorSignal;

  const [isModalVisible, setIsModalVisible] = useState(false);

  const [datasourceOptions, setDatasourceOptions] = useState<ETLDatasourceOption[]>([]);
  const [datasourceType, setDatasourceType] = useState<string>('external');
  const [selectDatasourceId, setSelectDatasourceId] = useState<string>(
    nodeData.value[curNode.value.id]?.config?.datasourceId || ''
  );
  const [selectTableId, setSelectTableId] = useState<string>(nodeData.value[curNode.value.id]?.config?.tableId || '');
  const [tableOptions, setTableOptions] = useState<ETLTable[]>([]);
  const [targetColumns, setTargetColumns] = useState<ELTColumn[]>([]);
  const [fieldMappings, setFieldMappings] = useState<FieldMapping[]>(
    nodeData.value[curNode.value.id]?.config?.fields || []
  );

  useEffect(() => {
    handleListAppETLDatasource();
  }, []);

  useEffect(() => {
    if (selectDatasourceId) {
      handleListETLTables(selectDatasourceId);
    }
  }, [selectDatasourceId]);

  useEffect(() => {
    if (selectTableId) {
      handleListETLTableColumns(selectTableId);
    }
  }, [selectTableId]);

  const handleDatasourceTypeOnChange = (value: string) => {
    setDatasourceType(value);
    setSelectDatasourceId('');
    setTableOptions([]);
    setTargetColumns([]);
    setFieldMappings([]);

    const payload = nodeData.value[curNode.value.id];
    payload.config = {
      datasourceType: value,
      datasourceId: '',
      tableId: '',
      fields: []
    };
    payload.output = {
      verified: false
    };
    setNodeData(curNode.value.id, payload);
  };

  const handleDatasourceIdOnChange = (value: string) => {
    setSelectDatasourceId(value);
    const payload = nodeData.value[curNode.value.id];
    payload.config = {
      ...payload.config,
      datasourceId: value,
      tableId: '',
      fields: []
    };

    payload.output = {
      verified: false
    };

    setNodeData(curNode.value.id, payload);
  };

  const handleListAppETLDatasource = async () => {
    const curAppId = getHashQueryParam('appId');
    if (!curAppId) {
      Message.error('应用ID不存在');
      return;
    }
    const res = await listAppETLDatasource({
      applicationId: curAppId,
      writable: 1
    });
    setDatasourceOptions(res);
  };

  const handleListETLTables = async (datasourceId: string) => {
    const res = await listETLTables({
      id: datasourceId,
      writable: 1
    });
    setTableOptions(res);
  };

  const openFieldModal = () => {
    setIsModalVisible(true);
  };

  const handleSelectTableOnChange = async (tableId: string) => {
    setSelectTableId(tableId);
    handleListETLTableColumns(tableId);

    const payload = nodeData.value[curNode.value.id];
    payload.config = {
      ...payload.config,
      tableId: tableId
    };
    payload.output = {
      verified: false
    };

    setNodeData(curNode.value.id, payload);
  };

  const handleListETLTableColumns = async (tableId: string) => {
    const res = await listETLTableColumns({
      tableId: tableId
    });
    setTargetColumns(res);
  };

  const handleFieldModalOk = (validFields: FieldMapping[]) => {
    console.log('validFields: ', validFields);
    const payload = nodeData.value[curNode.value.id];

    payload.config = {
      ...payload.config,
      fields: validFields
    };

    payload.output = {
      verified: true
    };

    setNodeData(curNode.value.id, payload);
    setFieldMappings(validFields);
  };

  return (
    <div className={styles.config}>
      {curDrawerTab.value === ETLDrawerTab.DATA_CONFIG && (
        <div className={styles.dataConfig}>
          <Row>同步数据源</Row>
          <Row gutter={24}>
            <Col span={12}>
              <Select
                style={{ width: '200px' }}
                value={datasourceType}
                onChange={handleDatasourceTypeOnChange}
                options={[
                  { label: '内部数据源', value: 'internal' },
                  { label: '外部数据源', value: 'external' }
                ]}
              />
            </Col>
            <Col span={12}>
              <Select
                style={{ width: '200px' }}
                placeholder="请选择数据源"
                value={nodeData.value[curNode.value.id]?.config?.datasourceId}
                options={datasourceOptions.map((option) => ({ label: option.name, value: option.id }))}
                onChange={handleDatasourceIdOnChange}
              />
            </Col>
          </Row>

          <Row style={{ marginTop: '16px' }}>同步表单</Row>
          <Row gutter={24}>
            <Col span={12}>
              <Select
                style={{ width: '200px' }}
                value={nodeData.value[curNode.value.id]?.config?.tableId}
                options={tableOptions.map((option) => ({ label: option.name, value: option.id }))}
                onChange={handleSelectTableOnChange}
              />
            </Col>
            <Col span={12}>
              <Button
                style={{ width: '200px' }}
                type="secondary"
                onClick={openFieldModal}
                disabled={!selectDatasourceId || !targetColumns.length}
              >
                设置同步字段
              </Button>
            </Col>
          </Row>
        </div>
      )}

      {curDrawerTab.value === ETLDrawerTab.DATA_PREVIEW && <div className={styles.dataPreview}></div>}

      {curDrawerTab.value === ETLDrawerTab.NODE_REMARK && (
        <TextArea placeholder="请输入节点备注" autoSize={{ minRows: 3, maxRows: 6 }} allowClear />
      )}

      <FieldModal
        targetColumns={targetColumns.map((option: ELTColumn) => ({
          id: option.id,
          name: option.name,
          type: option.type
        }))}
        isModalVisible={isModalVisible}
        initialMappings={fieldMappings}
        onClose={() => setIsModalVisible(false)}
        onOk={(validFields) => {
          handleFieldModalOk(validFields);
          setIsModalVisible(false);
        }}
      />
    </div>
  );
};
