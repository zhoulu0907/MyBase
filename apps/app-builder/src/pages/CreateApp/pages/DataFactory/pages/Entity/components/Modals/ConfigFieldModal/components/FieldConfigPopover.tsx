import React, { useMemo } from 'react';
import { ENTITY_FIELD_TYPE } from '@onebase/ui-kit';
import FieldConstraint from './FieldConstraint';
import { AutoCodeRuleConfig } from './AutoCodeRuleConfig';
import { MultiPicklistConfig, PicklistConfig } from './PicklistConfig';
import type { FieldConfigPopoverProps } from '../types';
import styles from '../index.module.less';

const FieldConfigPopover: React.FC<FieldConfigPopoverProps> = React.memo(
  ({ fieldType, fieldId, field, onConfirm, onCancel, fields, gotoDictPage }) => {
    // 根据字段类型渲染对应的配置组件
    const configComponent = useMemo(() => {
      switch (fieldType) {
        case ENTITY_FIELD_TYPE.SELECT.VALUE:
          return (
            <PicklistConfig
              onConfirm={(options, dictTypeId) => {
                onConfirm(ENTITY_FIELD_TYPE.SELECT.VALUE, fieldId, options, dictTypeId);
              }}
              initialOptions={field?.options}
              initialDictTypeId={field?.dictTypeId}
              onCancel={() => onCancel(ENTITY_FIELD_TYPE.SELECT.VALUE)}
              gotoDictPage={gotoDictPage}
            />
          );

        case ENTITY_FIELD_TYPE.MULTI_SELECT.VALUE:
          return (
            <MultiPicklistConfig
              onConfirm={(options, dictTypeId) => {
                onConfirm(ENTITY_FIELD_TYPE.MULTI_SELECT.VALUE, fieldId, options, dictTypeId);
              }}
              initialOptions={field?.options}
              initialDictTypeId={field?.dictTypeId}
              onCancel={() => onCancel(ENTITY_FIELD_TYPE.MULTI_SELECT.VALUE)}
              gotoDictPage={gotoDictPage}
            />
          );

        case ENTITY_FIELD_TYPE.AUTO_CODE.VALUE:
          return (
            <AutoCodeRuleConfig
              onConfirm={(rules) => onConfirm(ENTITY_FIELD_TYPE.AUTO_CODE.VALUE, fieldId, rules)}
              initialConfig={field?.autoNumber || field?.autoNumberConfig}
              onCancel={() => onCancel(ENTITY_FIELD_TYPE.AUTO_CODE.VALUE)}
              fields={fields}
            />
          );

        case 'CONSTRAINTS':
          return (
            <FieldConstraint
              onConfirm={(constraintConfig) => onConfirm('CONSTRAINTS', fieldId, constraintConfig)}
              initialConfig={field?.constraints}
              onCancel={() => onCancel('CONSTRAINTS')}
            />
          );

        default:
          return null;
      }
    }, [fieldType, fieldId, field, onConfirm, onCancel, fields]);

    return <div className={styles.fieldConfigPopover}>{configComponent}</div>;
  }
);

FieldConfigPopover.displayName = 'FieldConfigPopover';

export default FieldConfigPopover;
