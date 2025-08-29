import React from 'react';
import { ENTITY_FIELD_TYPE_LABEL, ENTITY_FIELD_TYPE } from '@/pages/CreateApp/pages/DataFactory/utils/const';
import { PicklistConfig, MultiPicklistConfig, AutoCodeConfig } from './FieldTypeConfig';
import FieldConstraint from './FieldConstraint';
import styles from './index.module.less';

interface FieldConfigPopoverProps {
  fieldType: string;
  fieldId: string;
  field: any;
  onConfirm: (fieldType: string, fieldId: string, configData: any) => void;
  onCancel: (fieldType: string) => void;
}

const FieldConfigPopover: React.FC<FieldConfigPopoverProps> = ({ fieldType, fieldId, field, onConfirm, onCancel }) => {

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

      {fieldType === ENTITY_FIELD_TYPE_LABEL.AUTO_CODE && (
        <AutoCodeConfig
          onConfirm={(rules) => onConfirm(ENTITY_FIELD_TYPE_LABEL.AUTO_CODE, fieldId, rules)}
          initialRules={field?.autoCodeRules}
          onCancel={() => onCancel(ENTITY_FIELD_TYPE_LABEL.AUTO_CODE)}
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
