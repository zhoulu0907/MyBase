import type { EntityNode } from '@/pages/CreateApp/pages/DataFactory/utils/interface';
import { Form, Input, Message, Modal, Radio, Select } from '@arco-design/web-react';
import React, { useState } from 'react';
import styles from '../modal.module.less';

interface RuleFormValues {
  validationType: string;
  formatValidationType?: string;
  validationName: string;
  validationDataItem: string;
  conditionSetting: string;
  failureMessage: string;
}

interface CreateRuleModalProps {
  visible: boolean;
  setVisible: (visible: boolean) => void;
  entity: Partial<EntityNode>;
  successCallback: () => void;
}

const CreateRuleModal: React.FC<CreateRuleModalProps> = ({ visible, setVisible, successCallback }) => {
  const [form] = Form.useForm<RuleFormValues>();
  const [loading, setLoading] = useState(false);

  // 校验类型选项
  const validationTypeOptions = [
    { label: '必填校验', value: 'required' },
    { label: '唯一校验', value: 'unique' },
    { label: '长度校验', value: 'length' },
    { label: '范围校验', value: 'range' },
    { label: '格式校验', value: 'format' },
    { label: '子表空行校验', value: 'subtable_empty' }
  ];

  // 格式校验类型选项
  const formatValidationTypeOptions = [
    { label: '正则校验', value: 'pattern' },
    { label: '邮箱格式', value: 'email' },
    { label: '电话格式', value: 'phone' },
    // { label: '身份证格式', value: 'idcard' },
    { label: 'URL格式', value: 'url' },
    { label: '日期格式', value: 'date' }
  ];

  // 校验数据项选项（这里可以根据实际需求动态获取）
  const validationDataItemOptions = [
    { label: '字段值', value: 'field_value' },
    { label: '关联字段值', value: 'related_field_value' },
    { label: '计算字段值', value: 'calculated_field_value' }
  ];

  // 条件设置选项
  const conditionSettingOptions = [
    { label: '等于', value: 'equals' },
    { label: '不等于', value: 'not_equals' },
    { label: '大于', value: 'greater_than' },
    { label: '小于', value: 'less_than' },
    { label: '包含', value: 'contains' },
    { label: '不包含', value: 'not_contains' }
  ];

  // 监听校验类型变化，控制格式校验类型字段的显示
  const handleValidationTypeChange = (value: string) => {
    if (value !== 'format') {
      form.setFieldValue('formatValidationType', undefined);
    }
  };

  // 监听校验类型变化，控制条件设置字段的显示
  const isConditionSettingVisible = () => {
    const validationType = form.getFieldValue('validationType');
    return validationType !== 'subtable_empty';
  };

  // 提交表单
  const handleFinish = async () => {
    try {
      const values = await form.validate();
      setLoading(true);

      console.log('规则表单数据:', values);

      // TODO: 调用创建规则的API
      // const res = await createRule({
      //   ...values,
      //   entityId: entity.entityId,
      //   appId: '1'
      // });

      Message.success('创建规则成功');
      form.resetFields();
      setVisible(false);
      successCallback();
    } catch (error) {
      console.error('创建规则失败:', error);
      Message.error('创建规则失败');
    } finally {
      setLoading(false);
    }
  };

  // 关闭弹窗时重置表单
  const handleCancel = () => {
    form.resetFields();
    setVisible(false);
  };

  return (
    <Modal
      className={styles['create-rule-modal']}
      title="添加规则"
      visible={visible}
      onOk={handleFinish}
      onCancel={handleCancel}
      okText="创建"
      cancelText="取消"
      confirmLoading={loading}
      style={{ width: 600 }}
    >
      <Form form={form} layout="vertical" className={styles['rule-form']}>
        <Form.Item
          label="规则名称"
          field="validationName"
          rules={[
            { required: true, message: '请输入规则名称' },
            { max: 50, message: '规则名称不能超过50个字符' }
          ]}
        >
          <Input placeholder="请输入规则名称" maxLength={50} showWordLimit />
        </Form.Item>
        <Form.Item label="校验类型" field="validationType" rules={[{ required: true, message: '请选择校验类型' }]}>
          <Select onChange={handleValidationTypeChange} placeholder="请选择校验类型">
            {validationTypeOptions.map((option) => (
              <Select.Option key={option.value} value={option.value}>
                {option.label}
              </Select.Option>
            ))}
          </Select>
        </Form.Item>

        {/* 格式校验类型 - 条件显示 */}
        <Form.Item noStyle shouldUpdate>
          {(values) => {
            if (values.validationType === 'format') {
              return (
                <Form.Item
                  label="格式校验类型"
                  field="formatValidationType"
                  rules={[{ required: true, message: '请选择格式校验类型' }]}
                >
                  <Select
                    placeholder="请选择格式校验类型"
                    options={formatValidationTypeOptions}
                    style={{ width: '100%' }}
                  />
                </Form.Item>
              );
            }
            return null;
          }}
        </Form.Item>

        {/* 校验数据项 */}
        <Form.Item
          label="校验数据项"
          field="validationDataItem"
          rules={[{ required: true, message: '请选择校验数据项' }]}
        >
          <Select placeholder="请选择校验数据项" options={validationDataItemOptions} style={{ width: '100%' }} />
        </Form.Item>

        {/* 条件设置 - 条件显示 */}
        <Form.Item noStyle shouldUpdate>
          {(values) => {
            if (isConditionSettingVisible()) {
              return (
                <Form.Item
                  label="条件设置"
                  field="conditionSetting"
                  rules={[{ required: true, message: '请选择条件设置' }]}
                >
                  <Select placeholder="请选择条件设置" options={conditionSettingOptions} style={{ width: '100%' }} />
                </Form.Item>
              );
            }
            return null;
          }}
        </Form.Item>

        {/* 验证失败提示语 */}
        <Form.Item
          label="验证失败提示语"
          field="failureMessage"
          rules={[
            { required: true, message: '请输入验证失败提示语' },
            { max: 200, message: '验证失败提示语不能超过200个字符' }
          ]}
        >
          <Input.TextArea placeholder="请输入验证失败提示语" rows={3} maxLength={200} showWordLimit />
        </Form.Item>
      </Form>
    </Modal>
  );
};

export default CreateRuleModal;
