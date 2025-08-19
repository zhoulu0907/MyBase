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
  const hasConfig = field?.fieldConfig;

  return (
    <div className={styles['field-config-popover']}>
      <div style={{ marginBottom: '16px' }}>
        {hasConfig ? (
          <div>
            <p style={{ color: '#52c41a', marginBottom: 8 }}>✓ 已配置</p>
            {fieldType === 'AUTO_CODE' && field.fieldConfig?.autoCodeRules && (
              <p style={{ fontSize: '12px', color: '#666' }}>规则数量: {field.fieldConfig.autoCodeRules.length}</p>
            )}
            {(fieldType === 'PICKLIST' || fieldType === 'MULTI_PICKLIST') && field.fieldConfig?.options && (
              <p style={{ fontSize: '12px', color: '#666' }}>选项数量: {field.fieldConfig.options.length}</p>
            )}
            {fieldType === 'CONSTRAINTS' && field.fieldConfig?.constraints && (
              <div>
                <p style={{ fontSize: '12px', color: '#666' }}>
                  长度范围: {field.fieldConfig.constraints.lengthRange.enabled ? '已启用' : '未启用'}
                </p>
                <p style={{ fontSize: '12px', color: '#666' }}>
                  正则校验: {field.fieldConfig.constraints.regexValidation.enabled ? '已启用' : '未启用'}
                </p>
              </div>
            )}
          </div>
        ) : (
          <p style={{ color: '#ff4d4f' }}>⚠ 未配置</p>
        )}
      </div>

      {/* 根据字段类型渲染对应的配置组件 */}
      {fieldType === 'PICKLIST' && (
        <PicklistConfig
          onConfirm={(options) => onConfirm('PICKLIST', fieldId, options)}
          initialOptions={field?.fieldConfig?.options}
          onCancel={() => onCancel('PICKLIST')}
        />
      )}

      {fieldType === 'MULTI_PICKLIST' && (
        <MultiPicklistConfig
          onConfirm={(options) => onConfirm('MULTI_PICKLIST', fieldId, options)}
          initialOptions={field?.fieldConfig?.options}
          onCancel={() => onCancel('MULTI_PICKLIST')}
        />
      )}

      {fieldType === 'AUTO_CODE' && (
        <AutoCodeConfig
          onConfirm={(rules) => onConfirm('AUTO_CODE', fieldId, rules)}
          initialRules={field?.fieldConfig?.autoCodeRules}
          onCancel={() => onCancel('AUTO_CODE')}
        />
      )}

      {fieldType === 'CONSTRAINTS' && (
        <FieldConstraint
          onConfirm={(constraintConfig) => onConfirm('CONSTRAINTS', fieldId, constraintConfig)}
          initialConfig={field?.fieldConfig?.constraints}
          onCancel={() => onCancel('CONSTRAINTS')}
        />
      )}
    </div>
  );
};

export default FieldConfigPopover;
