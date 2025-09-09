import React, { useState } from 'react';
import { Button, Input, Switch } from '@arco-design/web-react';
import styles from './index.module.less';
import { FIELD_CONSTRAINT_LENGTH_ENABLED, FIELD_CONSTRAINT_REGEX_ENABLED } from '@onebase/ui-kit';

// 字段约束配置接口
interface FieldConstraintConfig {
  lengthEnabled: number;
  minLength: number;
  maxLength: number;
  lengthPrompt: string;
  regexEnabled: number;
  regexPattern: string;
  regexPrompt: string;
}

interface FieldConstraintProps {
  onConfirm: (config: FieldConstraintConfig) => void;
  onCancel: () => void;
  initialConfig?: FieldConstraintConfig;
}

export const FieldConstraint: React.FC<FieldConstraintProps> = ({ onConfirm, onCancel, initialConfig }) => {
  const [config, setConfig] = useState<FieldConstraintConfig>({
    lengthEnabled: FIELD_CONSTRAINT_LENGTH_ENABLED.DISABLE,
    minLength: 0,
    maxLength: 800,
    lengthPrompt: '',
    regexEnabled: FIELD_CONSTRAINT_REGEX_ENABLED.DISABLE,
    regexPattern: '',
    regexPrompt: '',
    ...initialConfig
  });

  // 更新长度范围配置
  const updateLengthConfig = (
    field: keyof Pick<FieldConstraintConfig, 'lengthEnabled' | 'minLength' | 'maxLength' | 'lengthPrompt'>,
    value: string | number | boolean
  ) => {
    if (field === 'lengthEnabled') {
      setConfig((prev) => ({
        ...prev,
        [field]: value ? FIELD_CONSTRAINT_LENGTH_ENABLED.ENABLE : FIELD_CONSTRAINT_LENGTH_ENABLED.DISABLE
      }));
    } else if (field === 'minLength' || field === 'maxLength') {
      setConfig((prev) => ({
        ...prev,
        [field]: typeof value === 'number' ? value : parseInt(String(value)) || 0
      }));
    } else if (field === 'lengthPrompt') {
      setConfig((prev) => ({
        ...prev,
        [field]: String(value)
      }));
    }
  };

  // 更新正则校验配置
  const updateRegexConfig = (
    field: keyof Pick<FieldConstraintConfig, 'regexEnabled' | 'regexPattern' | 'regexPrompt'>,
    value: string | boolean
  ) => {
    setConfig((prev) => ({
      ...prev,
      [field]:
        field === 'regexEnabled'
          ? value
            ? FIELD_CONSTRAINT_REGEX_ENABLED.ENABLE
            : FIELD_CONSTRAINT_REGEX_ENABLED.DISABLE
          : value
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
            checked={config.lengthEnabled === FIELD_CONSTRAINT_LENGTH_ENABLED.ENABLE}
            onChange={(checked) => updateLengthConfig('lengthEnabled', checked)}
            size="small"
          />
        </div>

        {config.lengthEnabled === FIELD_CONSTRAINT_LENGTH_ENABLED.ENABLE && (
          <div className={styles['constraint-content']}>
            <div className={styles['constraint-row']}>
              <label className={styles['required-label']}>*最小长度</label>
              <Input
                type="number"
                value={config.minLength}
                onChange={(value) => updateLengthConfig('minLength', parseInt(value) || 0)}
                placeholder="0"
                style={{ width: '120px' }}
              />
            </div>

            <div className={styles['constraint-row']}>
              <label className={styles['required-label']}>*最大长度</label>
              <Input
                type="number"
                value={config.maxLength}
                onChange={(value) => updateLengthConfig('maxLength', parseInt(value) || 8000)}
                placeholder="800"
                style={{ width: '120px' }}
              />
            </div>

            <div className={styles['constraint-row']}>
              <label>提示信息</label>
              <Input
                value={config.lengthPrompt}
                onChange={(value) => updateLengthConfig('lengthPrompt', value)}
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
            checked={config.regexEnabled === FIELD_CONSTRAINT_REGEX_ENABLED.ENABLE}
            onChange={(checked) => updateRegexConfig('regexEnabled', checked)}
            size="small"
          />
        </div>

        {config.regexEnabled === FIELD_CONSTRAINT_REGEX_ENABLED.ENABLE && (
          <div className={styles['constraint-content']}>
            <div className={styles['constraint-row']}>
              <label className={styles['required-label']}>*正则校验</label>
              <Input
                value={config.regexPattern}
                onChange={(value) => updateRegexConfig('regexPattern', value)}
                placeholder="请输入正则表达式"
                style={{ width: '200px' }}
              />
            </div>

            <div className={styles['constraint-row']}>
              <label>提示信息</label>
              <Input
                value={config.regexPrompt}
                onChange={(value) => updateRegexConfig('regexPrompt', value)}
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
