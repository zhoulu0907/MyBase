import React from 'react';
import { Button, Input, Switch, Form, Message } from '@arco-design/web-react';
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
  const [form] = Form.useForm();

  const initialValues: FieldConstraintConfig = {
    lengthEnabled: FIELD_CONSTRAINT_LENGTH_ENABLED.DISABLE,
    minLength: 0,
    maxLength: 800,
    lengthPrompt: '',
    regexEnabled: FIELD_CONSTRAINT_REGEX_ENABLED.DISABLE,
    regexPattern: '',
    regexPrompt: '',
    ...initialConfig
  };

  const handleConfirm = async () => {
    try {
      const values = (await form.validate()) as FieldConstraintConfig;
      const normalized: FieldConstraintConfig = {
        lengthEnabled: values.lengthEnabled,
        minLength: Number(values.minLength) || 0,
        maxLength: Number(values.maxLength) || 0,
        lengthPrompt: values.lengthPrompt || '',
        regexEnabled: values.regexEnabled,
        regexPattern: values.regexPattern || '',
        regexPrompt: values.regexPrompt || ''
      };

      if (
        normalized.lengthEnabled === FIELD_CONSTRAINT_LENGTH_ENABLED.ENABLE &&
        normalized.maxLength < normalized.minLength
      ) {
        Message.error('最大长度应不小于最小长度');
        return;
      }

      onConfirm(normalized);
    } catch {
      // 校验未通过
    }
  };

  return (
    <div className={styles.fieldConstraintConfig}>
      <h4>字段约束</h4>

      <Form form={form} initialValues={initialValues} layout="vertical">
        {/* 长度范围配置 */}
        <div className={styles.constraintSection}>
          <div className={styles.constraintHeader}>
            <span>长度范围</span>
            <Form.Item
              field="lengthEnabled"
              triggerPropName="checked"
              normalize={(checked) =>
                checked ? FIELD_CONSTRAINT_LENGTH_ENABLED.ENABLE : FIELD_CONSTRAINT_LENGTH_ENABLED.DISABLE
              }
              formatter={(v) => v === FIELD_CONSTRAINT_LENGTH_ENABLED.ENABLE}
              style={{ marginBottom: 0 }}
            >
              <Switch size="small" />
            </Form.Item>
          </div>

          <Form.Item shouldUpdate noStyle>
            {(values: FieldConstraintConfig) =>
              values.lengthEnabled === FIELD_CONSTRAINT_LENGTH_ENABLED.ENABLE && (
                <div className={styles.constraintContent}>
                  <div className={styles.constraintRow}>
                    <label className={styles.requiredLabel}>*最小长度</label>
                    <Form.Item
                      field="minLength"
                      rules={[{ required: true, message: '请填写最小长度' }]}
                      style={{ marginBottom: 0 }}
                    >
                      <Input type="number" placeholder="0" style={{ width: '120px' }} />
                    </Form.Item>
                  </div>

                  <div className={styles.constraintRow}>
                    <label className={styles.requiredLabel}>*最大长度</label>
                    <Form.Item
                      field="maxLength"
                      rules={[{ required: true, message: '请填写最大长度' }]}
                      style={{ marginBottom: 0 }}
                    >
                      <Input type="number" placeholder="800" style={{ width: '120px' }} />
                    </Form.Item>
                  </div>

                  <div className={styles.constraintRow}>
                    <label>提示信息</label>
                    <Form.Item field="lengthPrompt" style={{ marginBottom: 0 }}>
                      <Input placeholder="请输入提示信息" style={{ width: '200px' }} />
                    </Form.Item>
                  </div>
                </div>
              )
            }
          </Form.Item>
        </div>

        {/* 正则校验配置 */}
        <div className={styles.constraintSection}>
          <div className={styles.constraintHeader}>
            <span>正则校验</span>
            <Form.Item
              field="regexEnabled"
              triggerPropName="checked"
              normalize={(checked) =>
                checked ? FIELD_CONSTRAINT_REGEX_ENABLED.ENABLE : FIELD_CONSTRAINT_REGEX_ENABLED.DISABLE
              }
              formatter={(v) => v === FIELD_CONSTRAINT_REGEX_ENABLED.ENABLE}
              style={{ marginBottom: 0 }}
            >
              <Switch size="small" />
            </Form.Item>
          </div>

          <Form.Item shouldUpdate noStyle>
            {(values: FieldConstraintConfig) =>
              values.regexEnabled === FIELD_CONSTRAINT_REGEX_ENABLED.ENABLE && (
                <div className={styles.constraintContent}>
                  <div className={styles.constraintRow}>
                    <label className={styles.requiredLabel}>*正则校验</label>
                    <Form.Item
                      field="regexPattern"
                      rules={[{ required: true, message: '请输入正则表达式' }]}
                      style={{ marginBottom: 0 }}
                    >
                      <Input placeholder="请输入正则表达式" style={{ width: '200px' }} />
                    </Form.Item>
                  </div>

                  <div className={styles.constraintRow}>
                    <label>提示信息</label>
                    <Form.Item field="regexPrompt" style={{ marginBottom: 0 }}>
                      <Input placeholder="请输入提示信息" style={{ width: '200px' }} />
                    </Form.Item>
                  </div>
                </div>
              )
            }
          </Form.Item>
        </div>

        {/* 操作按钮 */}
        <div className={styles.fieldConstraintFooter}>
          <Button type="outline" size="small" onClick={onCancel}>
            取消
          </Button>
          <Button type="primary" size="small" onClick={handleConfirm}>
            确定
          </Button>
        </div>
      </Form>
    </div>
  );
};

export default FieldConstraint;
