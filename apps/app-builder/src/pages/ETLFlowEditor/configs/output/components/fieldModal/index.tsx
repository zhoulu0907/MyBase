import { Button, Grid, Modal, Select } from '@arco-design/web-react';
import type { ELTColumn } from '@onebase/app';
import { etlEditorSignal } from '@onebase/common';
import { useSignals } from '@preact/signals-react/runtime';
import React, { useEffect, useState } from 'react';
import { getSourceNodeIdsByTarget } from '../../../utils';
import styles from './index.module.less';

const Row = Grid.Row;
const Col = Grid.Col;

export interface FieldMapping {
  targetFieldName: string;
  sourceFieldFqn: string;
  sourceFieldName: string;
  sourceFieldType: string;
}

interface FieldModalProps {
  // 控制弹窗是否显示
  isModalVisible: boolean;
  // 关闭弹窗的回调
  onClose: () => void;
  onOk?: (validFields: FieldMapping[]) => void;

  targetColumns: ELTColumn[];
  initialMappings?: FieldMapping[];
}

const FieldModal: React.FC<FieldModalProps> = ({ isModalVisible, onClose, onOk, targetColumns, initialMappings }) => {
  useSignals();

  const { curNode, nodeData, graphData } = etlEditorSignal;
  const [fieldMappings, setFieldMappings] = useState<FieldMapping[]>([]);
  const [outputColumns, setOutputColumns] = useState<ELTColumn[]>([]);

  useEffect(() => {
    if (!isModalVisible) {
      return;
    }

    // 根据 curNode.value.id 从 graphData 中找到对应的 sourceNodeID
    let sourceNodeIds = getSourceNodeIdsByTarget(graphData.value, curNode.value.id);

    if (sourceNodeIds && sourceNodeIds.length > 0) {
      const sourceNodeData = nodeData.value[sourceNodeIds[0]];
      console.log('sourceNodeData: ', sourceNodeData);
      setOutputColumns(
        sourceNodeData?.output?.fields?.map((field: any) => ({
          fieldFqn: field.fieldFqn,
          fieldName: field.fieldName,
          fieldType: field.fieldType
        })) ?? []
      );
    }

    if (initialMappings && initialMappings.length > 0) {
      setFieldMappings(
        initialMappings.map((item) => ({
          ...item
        }))
      );
      return;
    }

    setFieldMappings([
      {
        targetFieldName: '',
        sourceFieldFqn: '',
        sourceFieldName: '',
        sourceFieldType: ''
      }
    ]);
  }, [isModalVisible, initialMappings]);

  const handleAddField = () => {
    setFieldMappings((prev) => [
      ...prev,
      {
        targetFieldName: '',
        sourceFieldFqn: '',
        sourceFieldName: '',
        sourceFieldType: ''
      }
    ]);
  };

  const handleRemoveField = (index: number) => {
    setFieldMappings((prev) => {
      if (prev.length === 1) {
        return [
          {
            targetFieldName: '',
            sourceFieldFqn: '',
            sourceFieldName: '',
            sourceFieldType: ''
          }
        ];
      }

      return prev.filter((_, idx) => idx !== index);
    });
  };

  const handleChangeOutputColumn = (sourceFieldFqn: string, index: number) => {
    const selectedColumn = outputColumns.find((column) => column.fieldFqn === sourceFieldFqn);

    setFieldMappings((prev) =>
      prev.map((field, idx) =>
        idx === index
          ? {
              ...field,
              sourceFieldFqn,
              sourceFieldName: selectedColumn?.fieldName ?? '',
              sourceFieldType: selectedColumn?.fieldType ?? ''
            }
          : field
      )
    );
  };

  const handleChangeTargetColumn = (targetFieldName: string, index: number) => {
    setFieldMappings((prev) =>
      prev.map((field, idx) =>
        idx === index
          ? {
              ...field,
              targetFieldName
            }
          : field
      )
    );
  };

  const handleOk = () => {
    const validFields = fieldMappings.filter((field) => field.sourceFieldFqn && field.targetFieldName);

    onOk?.(validFields);
  };

  const handleCancel = () => {
    onClose?.();
  };

  const getAvailableOutputOptions = (currentIndex: number) => {
    const selectedIds = new Set(
      fieldMappings
        .filter((_, idx) => idx !== currentIndex)
        .map((mapping) => mapping.sourceFieldFqn)
        .filter((id) => !!id)
    );
    return outputColumns
      .filter(
        (column) => fieldMappings[currentIndex]?.sourceFieldFqn === column.fieldFqn || !selectedIds.has(column.fieldFqn)
      )
      .map((column) => ({
        label: column.fieldName,
        value: column.fieldFqn
      }));
  };

  const getAvailableTargetOptions = (currentIndex: number) => {
    const targetType = fieldMappings[currentIndex]?.sourceFieldType;

    const selectedIds = new Set(
      fieldMappings
        .filter((_, idx) => idx !== currentIndex)
        .map((mapping) => mapping.targetFieldName)
        .filter((id) => !!id)
    );

    // TODO(mickey): targetType和fieldType匹配需要和后端商量后重构

    const avaliableTargetOptions = targetColumns
      //   .filter((column) => (!targetType || column.fieldType === targetType) && !selectedIds.has(column.fieldFqn))
      .filter((column) => !selectedIds.has(column.fieldFqn))
      .map((column) => ({
        label: column.fieldName,
        value: column.fieldFqn
      }));

    return avaliableTargetOptions;
  };

  return (
    <Modal visible={isModalVisible} title="字段对应关系" onOk={handleOk} onCancel={handleCancel} style={{ width: 700 }}>
      <div className={styles.fieldModal}>
        <div className={styles.fieldModalContent}>
          <Row align="center" style={{ marginTop: '16px' }}>
            <Col span={10}>
              <div className={styles.fieldModalTitle}>输出表字段</div>
            </Col>
            <Col span={5} style={{ textAlign: 'center' }}></Col>
            <Col span={9}>
              <div className={styles.fieldModalTitle}>同步表字段</div>
            </Col>
          </Row>
          {fieldMappings.map((field, index) => (
            <Row key={`field-${index}`} align="center" style={{ marginTop: '16px' }} gutter={12}>
              <Col span={10}>
                <Select
                  placeholder="请选择字段"
                  value={field.sourceFieldFqn}
                  options={getAvailableOutputOptions(index)}
                  onChange={(value) => handleChangeOutputColumn(value as string, index)}
                />
              </Col>
              <Col span={4} style={{ textAlign: 'center' }}>
                =
              </Col>
              <Col span={8}>
                <Select
                  placeholder="请选择字段"
                  value={field.targetFieldName}
                  options={getAvailableTargetOptions(index)}
                  onChange={(value) => handleChangeTargetColumn(value as string, index)}
                />
              </Col>
              <Col span={2} style={{ textAlign: 'right' }}>
                <Button type="text" status="danger" onClick={() => handleRemoveField(index)}>
                  删除
                </Button>
              </Col>
            </Row>
          ))}
          <Row style={{ marginTop: '16px' }}>
            <Col span={24}>
              <Button
                type="outline"
                long
                onClick={handleAddField}
                disabled={fieldMappings.length >= outputColumns.length}
              >
                新增字段映射
              </Button>
            </Col>
          </Row>
        </div>
      </div>
    </Modal>
  );
};

export default FieldModal;
