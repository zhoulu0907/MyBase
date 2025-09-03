import React, { useEffect } from 'react';
import { Modal, Radio, InputNumber, Checkbox, Select, Form, Tooltip } from '@arco-design/web-react';
import { IconQuestionCircle } from '@arco-design/web-react/icon';
import styles from './index.module.less';
import type { AutoCodeRule } from './FieldTypeConfig';

interface AutoCodeConfigModalProps {
  visible: boolean;
  onVisibleChange: (visible: boolean) => void;
  onConfirm: (config: AutoCodeRule['config']) => void;
  initialConfig?: AutoCodeRule['config'];
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
    numberMode: 'FIXED_DIGITS',
    digitWidth: 4,
    continueIncrement: true,
    initialValue: 1,
    nextRecordStartValue: false,
    resetCycle: 'NONE'
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
    if (visible && initialConfig) {
      form.setFieldsValue(initialConfig);
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
      className={styles['auto-number-config-modal']}
      style={{ width: 500 }}
    >
      <Form
        form={form}
        layout="horizontal"
        className={styles['auto-number-config-form']}
        initialValues={initialValues}
        labelAlign="left"
      >
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

            <Form.Item label="" field="overflowContinue" className={styles['checkbox-with-help']}>
              <Checkbox>编号超出位数后继续递增</Checkbox>
              <IconQuestionCircle className={styles['help-icon']} />
            </Form.Item>
          </>
        )}

        <Form.Item
          label={
            <span>
              开始值
              <IconQuestionCircle className={styles['help-icon']} />
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

        <Form.Item label="" field="nextRecordStartValue" className={styles['checkbox-with-help']}>
          <Checkbox>下一条记录以修改后的开始值编号</Checkbox>
          <Tooltip content="请设置下一条记录的编号，设置的编号不得小于开始值，未设置时默认使用初始值。">
            <IconQuestionCircle className={styles['help-icon']} />
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
