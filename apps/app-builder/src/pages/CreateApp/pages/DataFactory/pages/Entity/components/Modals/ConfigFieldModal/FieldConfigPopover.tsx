import React from 'react';
import { PicklistConfig, MultiPicklistConfig, AutoCodeConfig } from './FieldTypeConfig';
import FieldConstraint from './FieldConstraint';
import styles from '../modal.module.less';

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
      {fieldType === 'PICKLIST' && (
        <PicklistConfig
          onConfirm={(options) => onConfirm('PICKLIST', fieldId, options)}
          initialOptions={field?.options}
          onCancel={() => onCancel('PICKLIST')}
        />
      )}

      {fieldType === 'MULTI_PICKLIST' && (
        <MultiPicklistConfig
          onConfirm={(options) => onConfirm('MULTI_PICKLIST', fieldId, options)}
          initialOptions={field?.options}
          onCancel={() => onCancel('MULTI_PICKLIST')}
        />
      )}

      {fieldType === 'AUTO_CODE' && (
        <AutoCodeConfig
          onConfirm={(rules) => onConfirm('AUTO_CODE', fieldId, rules)}
          initialRules={field?.autoCodeRules}
          onCancel={() => onCancel('AUTO_CODE')}
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
