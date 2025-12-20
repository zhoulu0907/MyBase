import { Button, Grid, Select } from '@arco-design/web-react';
import type { ELTColumn } from '@onebase/app';
import { etlEditorSignal } from '@onebase/common';
import { useSignals } from '@preact/signals-react/runtime';
import React, { useEffect, useRef, useState } from 'react';
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
  // 是否启用（替代原来的 isModalVisible，用于控制是否加载数据）
  enabled: boolean;
  // 字段变化时的回调
  onChange?: (validFields: FieldMapping[]) => void;

  targetColumns: ELTColumn[];
  initialMappings?: FieldMapping[];
}

const FieldModal: React.FC<FieldModalProps> = ({ enabled, onChange, targetColumns, initialMappings }) => {
  useSignals();

  const { curNode, nodeData, graphData } = etlEditorSignal;
  const [fieldMappings, setFieldMappings] = useState<FieldMapping[]>([]);
  const [outputColumns, setOutputColumns] = useState<ELTColumn[]>([]);
  const prevEnabledRef = useRef<boolean>(false);

  // 加载输出字段列表和初始化字段映射（只在 enabled 变为 true 时执行一次）
  useEffect(() => {
    if (!enabled) {
      prevEnabledRef.current = false;
      return;
    }

    // 只在从 false 变为 true 时初始化
    const wasDisabled = !prevEnabledRef.current;
    prevEnabledRef.current = enabled;

    if (!wasDisabled) {
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
    } else {
      // 如果没有找到源节点，清空 outputColumns
      setOutputColumns([]);
    }

    // 初始化字段映射（只在首次启用时使用 initialMappings）
    if (initialMappings && initialMappings.length > 0) {
      setFieldMappings(
        initialMappings.map((item) => ({
          ...item
        }))
      );
    } else {
      setFieldMappings([
        {
          targetFieldName: '',
          sourceFieldFqn: '',
          sourceFieldName: '',
          sourceFieldType: ''
        }
      ]);
    }
  }, [enabled]);

  const handleAddField = () => {
    const newMappings = [
      ...fieldMappings,
      {
        targetFieldName: '',
        sourceFieldFqn: '',
        sourceFieldName: '',
        sourceFieldType: ''
      }
    ];
    setFieldMappings(newMappings);

    // 实时触发 onChange
    const validFields = newMappings.filter((field) => field.sourceFieldFqn && field.targetFieldName);
    onChange?.(validFields);
  };

  const handleRemoveField = (index: number) => {
    let newMappings: FieldMapping[];
    if (fieldMappings.length === 1) {
      newMappings = [
        {
          targetFieldName: '',
          sourceFieldFqn: '',
          sourceFieldName: '',
          sourceFieldType: ''
        }
      ];
    } else {
      newMappings = fieldMappings.filter((_, idx) => idx !== index);
    }
    setFieldMappings(newMappings);

    // 实时触发 onChange
    const validFields = newMappings.filter((field) => field.sourceFieldFqn && field.targetFieldName);
    onChange?.(validFields);
  };

  const handleChangeOutputColumn = (sourceFieldFqn: string, index: number) => {
    const selectedColumn = outputColumns.find((column) => column.fieldFqn === sourceFieldFqn);

    const newMappings = fieldMappings.map((field, idx) =>
      idx === index
        ? {
            ...field,
            sourceFieldFqn,
            sourceFieldName: selectedColumn?.fieldName ?? '',
            sourceFieldType: selectedColumn?.fieldType ?? ''
          }
        : field
    );
    setFieldMappings(newMappings);

    // 实时触发 onChange
    const validFields = newMappings.filter((field) => field.sourceFieldFqn && field.targetFieldName);
    onChange?.(validFields);
  };

  const handleChangeTargetColumn = (targetFieldName: string, index: number) => {
    const newMappings = fieldMappings.map((field, idx) =>
      idx === index
        ? {
            ...field,
            targetFieldName
          }
        : field
    );
    setFieldMappings(newMappings);

    // 实时触发 onChange
    const validFields = newMappings.filter((field) => field.sourceFieldFqn && field.targetFieldName);
    onChange?.(validFields);
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

  if (!enabled) {
    return null;
  }

  return (
    <div className={styles.fieldModal}>
      <div className={styles.fieldModalHeader}>设置输出表和同步表单字段对应关系</div>
      <div className={styles.fieldModalContent}>
        <Row align="center">
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
              disabled={outputColumns.length === 0 || fieldMappings.length >= outputColumns.length}
            >
              新增字段映射
            </Button>
          </Col>
        </Row>
      </div>
    </div>
  );
};

export default FieldModal;
