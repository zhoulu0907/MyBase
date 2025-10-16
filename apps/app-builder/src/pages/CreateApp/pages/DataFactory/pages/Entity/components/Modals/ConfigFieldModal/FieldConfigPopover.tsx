import { ENTITY_FIELD_TYPE } from '@onebase/ui-kit';
import React from 'react';
import FieldConstraint from './FieldConstraint';
import { AutoCodeConfig, MultiPicklistConfig, PicklistConfig } from './FieldTypeConfig';
import styles from './index.module.less';

interface FieldConfigPopoverProps {
  fieldType: string;
  fieldId: string;
  field: any;
  onConfirm: (fieldType: string, fieldId: string, configData: any) => void;
  onCancel: (fieldType: string) => void;
  fields: any[];
}

const FieldConfigPopover: React.FC<FieldConfigPopoverProps> = ({
  fieldType,
  fieldId,
  field,
  onConfirm,
  onCancel,
  fields
}) => {
  return (
    <div className={styles.fieldConfigPopover}>
      {/* 根据字段类型渲染对应的配置组件 */}
      {fieldType === ENTITY_FIELD_TYPE.SELECT.VALUE && (
        <PicklistConfig
          onConfirm={(options) => onConfirm(ENTITY_FIELD_TYPE.SELECT.VALUE, fieldId, options)}
          initialOptions={field?.options}
          onCancel={() => onCancel(ENTITY_FIELD_TYPE.SELECT.VALUE)}
        />
      )}

      {fieldType === ENTITY_FIELD_TYPE.MULTI_SELECT.VALUE && (
        <MultiPicklistConfig
          onConfirm={(options) => onConfirm(ENTITY_FIELD_TYPE.MULTI_SELECT.VALUE, fieldId, options)}
          initialOptions={field?.options}
          onCancel={() => onCancel(ENTITY_FIELD_TYPE.MULTI_SELECT.VALUE)}
        />
      )}

      {fieldType === ENTITY_FIELD_TYPE.AUTO_CODE.VALUE && (
        <AutoCodeConfig
          onConfirm={(rules) => onConfirm(ENTITY_FIELD_TYPE.AUTO_CODE.VALUE, fieldId, rules)}
          initialConfig={field?.autoNumber}
          onCancel={() => onCancel(ENTITY_FIELD_TYPE.AUTO_CODE.VALUE)}
          fields={fields}
        />
      )}

      {fieldType === 'CONSTRAINTS' && (
        <FieldConstraint
          onConfirm={(constraintConfig) => onConfirm('CONSTRAINTS', fieldId, constraintConfig)}
          initialConfig={field?.constraints}
          onCancel={() => onCancel('CONSTRAINTS')}
        />
      )}
    </div>
  );
};

export default FieldConfigPopover;
