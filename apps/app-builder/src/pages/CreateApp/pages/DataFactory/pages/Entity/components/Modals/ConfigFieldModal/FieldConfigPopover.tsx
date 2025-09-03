import React from 'react';
import { ENTITY_FIELD_TYPE_LABEL } from '@onebase/ui-kit';
import { PicklistConfig, MultiPicklistConfig, AutoNumberConfig } from './FieldTypeConfig';
import FieldConstraint from './FieldConstraint';
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
    <div className={styles['field-config-popover']}>
      {/* 根据字段类型渲染对应的配置组件 */}
      {fieldType === ENTITY_FIELD_TYPE_LABEL.PICKLIST && (
        <PicklistConfig
          onConfirm={(options) => onConfirm(ENTITY_FIELD_TYPE_LABEL.PICKLIST, fieldId, options)}
          initialOptions={field?.options}
          onCancel={() => onCancel(ENTITY_FIELD_TYPE_LABEL.PICKLIST)}
        />
      )}

      {fieldType === ENTITY_FIELD_TYPE_LABEL.MULTI_PICKLIST && (
        <MultiPicklistConfig
          onConfirm={(options) => onConfirm(ENTITY_FIELD_TYPE_LABEL.MULTI_PICKLIST, fieldId, options)}
          initialOptions={field?.options}
          onCancel={() => onCancel(ENTITY_FIELD_TYPE_LABEL.MULTI_PICKLIST)}
        />
      )}

      {fieldType === ENTITY_FIELD_TYPE_LABEL.AUTO_NUMBER && (
        <AutoNumberConfig
          onConfirm={(rules) => onConfirm(ENTITY_FIELD_TYPE_LABEL.AUTO_NUMBER, fieldId, rules)}
          initialRules={field?.autoNumberRules}
          onCancel={() => onCancel(ENTITY_FIELD_TYPE_LABEL.AUTO_NUMBER)}
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
