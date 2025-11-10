import React, { useEffect } from 'react';
import { Modal, Radio, InputNumber, Checkbox, Select, Form, Tooltip } from '@arco-design/web-react';
import { IconQuestionCircle } from '@arco-design/web-react/icon';
import type { AutoNumberRule, AutoNumberRuleResponce } from '../types';
import {
  AUTO_CODE_NUMBER_MODE,
  AUTO_CODE_RESET_CYCLE,
  CONSTANTS,
  AUTO_CODE_NUMBER_DEFAULT_CONFIG
} from '../utils/const';
import styles from '../index.module.less';

interface AutoCodeNumberSettingsModalProps {
  visible: boolean;
  onVisibleChange: (visible: boolean) => void;
  onConfirm: (config: AutoNumberRule) => void;
  initialConfig?: AutoNumberRuleResponce;
}

const AutoCodeNumberSettingsModal: React.FC<AutoCodeNumberSettingsModalProps> = ({
  visible,
  onVisibleChange,
  onConfirm,
  initialConfig
}) => {
  const [form] = Form.useForm();
  const numberMode = Form.useWatch(['numberMode'], form)?.numberMode;

  const initialValues = {
    ...AUTO_CODE_NUMBER_DEFAULT_CONFIG,
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

  useEffect(() => {
    if (visible) {
      // 每次打开弹窗时先重置表单，避免数据残留
      form.resetFields();

      if (initialConfig) {
        const config = initialConfig as AutoNumberRuleResponce & { startValue?: number };
        const values = {
          ...config,
          initialValue: config.startValue || initialConfig.initialValue
        };
        form.setFieldsValue(values);
      } else {
        form.setFieldsValue(initialValues);
      }
    }
  }, [visible, initialConfig]);

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
      getPopupContainer={() => document.body}
      unmountOnExit
    >
      <Form
        id="auto-code-form"
        form={form}
        layout="horizontal"
        className={styles.autoNumberConfigForm}
        labelAlign="left"
      >
        <Form.Item label="编号方式" field="numberMode" rules={[{ required: true, message: '请选择编号方式' }]}>
          <Radio.Group>
            <Radio value={AUTO_CODE_NUMBER_MODE.NATURAL}>自然数编号</Radio>
            <Radio value={AUTO_CODE_NUMBER_MODE.FIXED_DIGITS}>指定位数编号</Radio>
          </Radio.Group>
        </Form.Item>

        {numberMode === AUTO_CODE_NUMBER_MODE.FIXED_DIGITS && (
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

            <Form.Item
              field="overflowContinue"
              className={styles.checkboxWithHelp}
              triggerPropName="checked"
              normalize={(v) => (v ? CONSTANTS.ENABLED : CONSTANTS.DISABLED)}
              formatter={(v) => v === CONSTANTS.ENABLED || v === true}
            >
              <Checkbox>
                编号超出位数后继续递增
                <IconQuestionCircle className={styles.helpIcon} />
              </Checkbox>
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

        <Form.Item
          field="resetOnInitialChange"
          className={styles.checkboxWithHelp}
          triggerPropName="checked"
          normalize={(v) => (v ? CONSTANTS.ENABLED : CONSTANTS.DISABLED)}
          formatter={(v) => v === CONSTANTS.ENABLED || v === true}
        >
          <Checkbox>
            <span>下一条记录以修改后的开始值编号</span>
            <Tooltip content="请设置下一条记录的编号，设置的编号不得小于开始值，未设置时默认使用初始值。">
              <IconQuestionCircle className={styles.helpIcon} />
            </Tooltip>
          </Checkbox>
        </Form.Item>

        <Form.Item label="周期重置" field="resetCycle" rules={[{ required: true, message: '请选择周期重置方式' }]}>
          <Select placeholder="请选择周期重置方式">
            <Select.Option value={AUTO_CODE_RESET_CYCLE.NONE}>不自动重置</Select.Option>
            <Select.Option value={AUTO_CODE_RESET_CYCLE.DAILY}>每日重置</Select.Option>
            <Select.Option value={AUTO_CODE_RESET_CYCLE.MONTHLY}>每月重置</Select.Option>
            <Select.Option value={AUTO_CODE_RESET_CYCLE.YEARLY}>每年重置</Select.Option>
          </Select>
        </Form.Item>
      </Form>
    </Modal>
  );
};

export default AutoCodeNumberSettingsModal;
