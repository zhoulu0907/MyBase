import React, { useState } from 'react';
import { Button, Input, Switch } from '@arco-design/web-react';
import styles from '../modal.module.less';

// 字段约束配置接口
interface FieldConstraintConfig {
  lengthRange: {
    enabled: boolean;
    minLength: number;
    maxLength: number;
    hintMessage: string;
  };
  regexValidation: {
    enabled: boolean;
    pattern: string;
    hintMessage: string;
  };
}

interface FieldConstraintProps {
  onConfirm: (config: FieldConstraintConfig) => void;
  onCancel: () => void;
  initialConfig?: FieldConstraintConfig;
}

export const FieldConstraint: React.FC<FieldConstraintProps> = ({ onConfirm, onCancel, initialConfig }) => {
  const [config, setConfig] = useState<FieldConstraintConfig>({
    lengthRange: {
      enabled: false,
      minLength: 0,
      maxLength: 8000,
      hintMessage: ''
    },
    regexValidation: {
      enabled: false,
      pattern: '',
      hintMessage: ''
    },
    ...initialConfig
  });

  // 更新长度范围配置
  const updateLengthRange = (field: keyof FieldConstraintConfig['lengthRange'], value: string | number | boolean) => {
    setConfig((prev) => ({
      ...prev,
      lengthRange: {
        ...prev.lengthRange,
        [field]: value
      }
    }));
  };

  // 更新正则校验配置
  const updateRegexValidation = (field: keyof FieldConstraintConfig['regexValidation'], value: string | boolean) => {
    setConfig((prev) => ({
      ...prev,
      regexValidation: {
        ...prev.regexValidation,
        [field]: value
      }
    }));
  };

  // 处理确认
  const handleConfirm = () => {
    onConfirm(config);
  };

  return (
    <div className={styles['field-constraint-config']}>
      <h4>字段约束</h4>

      {/* 长度范围配置 */}
      <div className={styles['constraint-section']}>
        <div className={styles['constraint-header']}>
          <span>长度范围</span>
          <Switch
            checked={config.lengthRange.enabled}
            onChange={(checked) => updateLengthRange('enabled', checked)}
            size="small"
          />
        </div>

        {config.lengthRange.enabled && (
          <div className={styles['constraint-content']}>
            <div className={styles['constraint-row']}>
              <label className={styles['required-label']}>*最小长度</label>
              <Input
                type="number"
                value={config.lengthRange.minLength}
                onChange={(value) => updateLengthRange('minLength', parseInt(value) || 0)}
                placeholder="0"
                style={{ width: '120px' }}
              />
            </div>

            <div className={styles['constraint-row']}>
              <label className={styles['required-label']}>*最大长度</label>
              <Input
                type="number"
                value={config.lengthRange.maxLength}
                onChange={(value) => updateLengthRange('maxLength', parseInt(value) || 8000)}
                placeholder="8000"
                style={{ width: '120px' }}
              />
            </div>

            <div className={styles['constraint-row']}>
              <label>提示信息</label>
              <Input
                value={config.lengthRange.hintMessage}
                onChange={(value) => updateLengthRange('hintMessage', value)}
                placeholder="请输入提示信息"
                style={{ width: '200px' }}
              />
            </div>
          </div>
        )}
      </div>

      {/* 正则校验配置 */}
      <div className={styles['constraint-section']}>
        <div className={styles['constraint-header']}>
          <span>正则校验</span>
          <Switch
            checked={config.regexValidation.enabled}
            onChange={(checked) => updateRegexValidation('enabled', checked)}
            size="small"
          />
        </div>

        {config.regexValidation.enabled && (
          <div className={styles['constraint-content']}>
            <div className={styles['constraint-row']}>
              <label className={styles['required-label']}>*正则校验</label>
              <Input
                value={config.regexValidation.pattern}
                onChange={(value) => updateRegexValidation('pattern', value)}
                placeholder="请输入正则表达式"
                style={{ width: '200px' }}
              />
            </div>

            <div className={styles['constraint-row']}>
              <label>提示信息</label>
              <Input
                value={config.regexValidation.hintMessage}
                onChange={(value) => updateRegexValidation('hintMessage', value)}
                placeholder="请输入提示信息"
                style={{ width: '200px' }}
              />
            </div>
          </div>
        )}
      </div>

      {/* 操作按钮 */}
      <div className={styles['field-constraint-footer']}>
        <Button type="outline" size="small" onClick={onCancel}>
          取消
        </Button>
        <Button type="primary" size="small" onClick={handleConfirm}>
          确定
        </Button>
      </div>
    </div>
  );
};

export default FieldConstraint;
