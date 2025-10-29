import React, { useMemo } from 'react';
import { ENTITY_FIELD_TYPE } from '@onebase/ui-kit';
import FieldConstraint from './FieldConstraint';
import { AutoCodeConfig, MultiPicklistConfig, PicklistConfig } from './FieldTypeConfig';
import styles from './index.module.less';
import type { FieldConfigPopoverProps } from './types';

const FieldConfigPopover: React.FC<FieldConfigPopoverProps> = React.memo(
  ({ fieldType, fieldId, field, onConfirm, onCancel, fields }) => {
    // 根据字段类型渲染对应的配置组件
    const configComponent = useMemo(() => {
      console.log('renderConfigComponent called for:', fieldType);
      switch (fieldType) {
        case ENTITY_FIELD_TYPE.SELECT.VALUE:
          return (
            <PicklistConfig
              onConfirm={(options, dictTypeId) =>
                onConfirm(ENTITY_FIELD_TYPE.SELECT.VALUE, fieldId, options, dictTypeId)
              }
              initialOptions={field?.options}
              onCancel={() => onCancel(ENTITY_FIELD_TYPE.SELECT.VALUE)}
            />
          );

        case ENTITY_FIELD_TYPE.MULTI_SELECT.VALUE:
          return (
            <MultiPicklistConfig
              onConfirm={(options, dictTypeId) =>
                onConfirm(ENTITY_FIELD_TYPE.MULTI_SELECT.VALUE, fieldId, options, dictTypeId)
              }
              initialOptions={field?.options}
              onCancel={() => onCancel(ENTITY_FIELD_TYPE.MULTI_SELECT.VALUE)}
            />
          );

        case ENTITY_FIELD_TYPE.AUTO_CODE.VALUE:
          return (
            <AutoCodeConfig
              onConfirm={(rules) => onConfirm(ENTITY_FIELD_TYPE.AUTO_CODE.VALUE, fieldId, rules)}
              initialConfig={field?.autoNumberConfig}
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
