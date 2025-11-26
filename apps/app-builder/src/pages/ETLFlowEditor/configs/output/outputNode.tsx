import { Button, Grid, Message, Select } from '@arco-design/web-react';
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
import DataRemark from '../../components/dataRemark';
import FieldModal, { type FieldMapping } from './components/fieldModal';
import styles from './index.module.less';

const Row = Grid.Row;
const Col = Grid.Col;

export const OutputNodeConfig: React.FC = () => {
  useSignals();

  const { curDrawerTab, setNodeData, curNode, nodeData } = etlEditorSignal;

  const [isModalVisible, setIsModalVisible] = useState(false);

  const [datasourceOptions, setDatasourceOptions] = useState<ETLDatasourceOption[]>([]);
  const [datasourceType, setDatasourceType] = useState<string>('external');
  const [selectDatasourceUUID, setSelectDatasourceUUID] = useState<string>(
    nodeData.value[curNode.value.id]?.config?.datasourceUUID || ''
  );
  const [selectTableUUID, setSelectTableUUID] = useState<string>(
    nodeData.value[curNode.value.id]?.config?.tableUUID || ''
  );
  const [tableOptions, setTableOptions] = useState<ETLTable[]>([]);
  const [targetColumns, setTargetColumns] = useState<ELTColumn[]>([]);
  const [fieldMappings, setFieldMappings] = useState<FieldMapping[]>(
    nodeData.value[curNode.value.id]?.config?.fields || []
  );

  useEffect(() => {
    handleListAppETLDatasource();
  }, []);

  useEffect(() => {
    if (selectDatasourceUUID) {
      handleListETLTables(selectDatasourceUUID);
    }
  }, [selectDatasourceUUID]);

  useEffect(() => {
    if (selectTableUUID) {
      handleListETLTableColumns(selectTableUUID);
    }
  }, [selectTableUUID]);

  const handleDatasourceTypeOnChange = (value: string) => {
    setDatasourceType(value);
    setSelectDatasourceUUID('');
    setTableOptions([]);
    setTargetColumns([]);
    setFieldMappings([]);

    const payload = nodeData.value[curNode.value.id];
    payload.config = {
      datasourceType: value,
      datasourceUUID: '',
      tableUUID: '',
      fields: []
    };
    payload.output = {
      verified: false
    };
    setNodeData(curNode.value.id, payload);
  };

  const handleDatasourceUUIDOnChange = (value: string) => {
    setSelectDatasourceUUID(value);

    setTargetColumns([]);
    setFieldMappings([]);
    const payload = nodeData.value[curNode.value.id];
    payload.config = {
      ...payload.config,
      datasourceUUID: value,
      tableUUID: '',
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
    console.log('res: ', res);
    setDatasourceOptions(res);
  };

  const handleListETLTables = async (datasourceUuid: string) => {
    const res = await listETLTables({
      uuid: datasourceUuid,
      writable: 1
    });
    setTableOptions(res);
  };

  const openFieldModal = () => {
    setIsModalVisible(true);
  };

  const handleSelectTableOnChange = async (tableUuid: string) => {
    setSelectTableUUID(tableUuid);
    handleListETLTableColumns(tableUuid);

    setFieldMappings([]);

    const payload = nodeData.value[curNode.value.id];
    payload.config = {
      ...payload.config,
      tableUUID: tableUuid,
      fields: []
    };
    payload.output = {
      verified: false
    };

    setNodeData(curNode.value.id, payload);
  };

  const handleListETLTableColumns = async (tableUuid: string) => {
    const res = await listETLTableColumns({
      tableUuid: tableUuid
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
                value={nodeData.value[curNode.value.id]?.config?.datasourceUUID}
                options={datasourceOptions.map((option) => ({ label: option.name, value: option.uuid }))}
                onChange={handleDatasourceUUIDOnChange}
              />
            </Col>
          </Row>

          <Row style={{ marginTop: '16px' }}>同步表单</Row>
          <Row gutter={24}>
            <Col span={12}>
              <Select
                style={{ width: '200px' }}
                value={nodeData.value[curNode.value.id]?.config?.tableUUID}
                options={tableOptions.map((option) => ({ label: option.name, value: option.uuid }))}
                onChange={handleSelectTableOnChange}
              />
            </Col>
            <Col span={12}>
              <Button
                style={{ width: '200px' }}
                type="secondary"
                onClick={openFieldModal}
                disabled={!selectDatasourceUUID || !targetColumns.length}
              >
                设置同步字段
              </Button>
            </Col>
          </Row>
        </div>
      )}

      {curDrawerTab.value === ETLDrawerTab.DATA_PREVIEW && <div className={styles.dataPreview}></div>}

      {curDrawerTab.value === ETLDrawerTab.NODE_REMARK && <DataRemark />}

      <FieldModal
        targetColumns={targetColumns.map((option: ELTColumn) => ({
          fieldFqn: option.fieldFqn,
          fieldName: option.fieldName,
          fieldType: option.fieldType,
          displayName: option.displayName
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
