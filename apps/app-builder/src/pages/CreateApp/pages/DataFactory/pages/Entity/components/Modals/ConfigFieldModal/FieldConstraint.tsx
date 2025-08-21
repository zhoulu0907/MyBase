import React, { useState } from 'react';
import { Button, Input, Switch } from '@arco-design/web-react';
import styles from '../modal.module.less';

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
    lengthEnabled: 1,
    minLength: 0,
    maxLength: 800,
    lengthPrompt: '',
    regexEnabled: 1,
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
        [field]: value ? 0 : 1
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
      [field]: field === 'regexEnabled' ? (value ? 0 : 1) : value
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
            checked={config.lengthEnabled === 0}
            onChange={(checked) => updateLengthConfig('lengthEnabled', checked)}
            size="small"
          />
        </div>

        {config.lengthEnabled === 0 && (
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
            checked={config.regexEnabled === 0}
            onChange={(checked) => updateRegexConfig('regexEnabled', checked)}
            size="small"
          />
        </div>

        {config.regexEnabled === 0 && (
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
