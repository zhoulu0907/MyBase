import React, { useEffect } from 'react';
import { Modal, Radio, InputNumber, Checkbox, Select, Form, Tooltip } from '@arco-design/web-react';
import { IconQuestionCircle } from '@arco-design/web-react/icon';
import styles from './index.module.less';
import type { AutoNumberRule, AutoNumberRuleResponce } from './types';

interface AutoCodeConfigModalProps {
  visible: boolean;
  onVisibleChange: (visible: boolean) => void;
  onConfirm: (config: AutoNumberRule) => void;
  initialConfig?: AutoNumberRuleResponce;
}

const AutoCodeConfigModal: React.FC<AutoCodeConfigModalProps> = ({
  visible,
  onVisibleChange,
  onConfirm,
  initialConfig
}) => {
  const [form] = Form.useForm();
  const numberMode = Form.useWatch(['numberMode'], form)?.numberMode;

  const initialValues = {
    isEnabled: 1,
    numberMode: 'FIXED_DIGITS',
    digitWidth: 4,
    overflowContinue: 1,
    initialValue: 1,
    resetCycle: 'NONE',
    rules: []
  };

  const handleConfirm = () => {
    form.validate().then((values) => {
      onConfirm(values);
      onVisibleChange(false);
    });
  };

  const handleCancel = () => {
    form.resetFields();
    onVisibleChange(false);
  };

  // 当弹窗显示时，设置表单初始值
  useEffect(() => {
    console.log('useEffect', visible, initialConfig, form);
    if (visible && initialConfig) {
      const values = {
        initialValue: initialConfig.startValue,
        numberMode: initialConfig.mode || 'FIXED_DIGITS',
        digitWidth: initialConfig.digitWidth || 4,
        overflowContinue: initialConfig.overflowContinue || 1,
        resetCycle: initialConfig.resetCycle || 'NONE',
        nextRecordStartValue: initialConfig?.nextRecordStartValue || 1
      };
      form.setFieldsValue(values);
    } else if (visible) {
      // 设置默认值
      form.setFieldsValue(initialValues);
    }
  }, [visible, initialConfig, form]);

  return (
    <Modal
      title="编号设置"
      visible={visible}
      onOk={handleConfirm}
      onCancel={handleCancel}
      okText="确定"
      cancelText="取消"
      className={styles.autoNumberConfigModal}
      style={{ width: 500 }}
    >
      <Form form={form} layout="horizontal" className={styles.autoNumberConfigForm} labelAlign="left">
        <Form.Item label="编号方式" field="numberMode" rules={[{ required: true, message: '请选择编号方式' }]}>
          <Radio.Group>
            <Radio value="NATURAL">自然数编号</Radio>
            <Radio value="FIXED_DIGITS">指定位数编号</Radio>
          </Radio.Group>
        </Form.Item>

        {numberMode === 'FIXED_DIGITS' && (
          <>
            <Form.Item
              label="位数"
              field="digitWidth"
              rules={[
                { required: true, message: '请输入位数' },
                { type: 'number', min: 2, max: 5, message: '位数必须在2-5之间' }
              ]}
            >
              <InputNumber min={2} max={5} style={{ width: 120 }} placeholder="请输入位数" />
            </Form.Item>

            <Form.Item label="" field="overflowContinue" className={styles.checkboxWithHelp}>
              <Checkbox>编号超出位数后继续递增</Checkbox>
              <IconQuestionCircle className={styles.helpIcon} />
            </Form.Item>
          </>
        )}

        <Form.Item
          label={
            <span>
              开始值
              <IconQuestionCircle className={styles.helpIcon} />
            </span>
          }
          field="initialValue"
          rules={[
            { required: true, message: '请输入开始值' },
            { type: 'number', min: 1, message: '开始值必须大于0' }
          ]}
        >
          <InputNumber min={1} style={{ width: 120 }} placeholder="请输入开始值" />
        </Form.Item>

        <Form.Item label="" field="nextRecordStartValue" className={styles.checkboxWithHelp}>
          <Checkbox>下一条记录以修改后的开始值编号</Checkbox>
          <Tooltip content="请设置下一条记录的编号，设置的编号不得小于开始值，未设置时默认使用初始值。">
            <IconQuestionCircle className={styles.helpIcon} />
          </Tooltip>
        </Form.Item>

        <Form.Item label="周期重置" field="resetCycle" rules={[{ required: true, message: '请选择周期重置方式' }]}>
          <Select placeholder="请选择周期重置方式">
            <Select.Option value="NONE">不自动重置</Select.Option>
            <Select.Option value="DAILY">每日重置</Select.Option>
            <Select.Option value="MONTHLY">每月重置</Select.Option>
            <Select.Option value="YEARLY">每年重置</Select.Option>
          </Select>
        </Form.Item>
      </Form>
    </Modal>
  );
};

export default AutoCodeConfigModal;
