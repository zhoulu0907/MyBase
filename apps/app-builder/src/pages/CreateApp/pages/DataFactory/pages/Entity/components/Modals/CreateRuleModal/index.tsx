import type { EntityListItem, EntityNode } from '@/pages/CreateApp/pages/DataFactory/utils/interface';
import { Button, Form, Input, Message, Modal, Radio, Select, Space } from '@arco-design/web-react';
import { IconDelete, IconPlus } from '@arco-design/web-react/icon';
import React, { useState } from 'react';
import { getEntityFieldsWithChildren } from '@onebase/app';
import styles from '../modal.module.less';
import { useAppStore } from '@/store';

interface ConditionRow {
  field: string;
  operator: string;
  valueType: string;
  value: string;
}

interface ConditionGroup {
  id: string;
  conditions: ConditionRow[];
  logic: 'AND' | 'OR';
}

interface RuleFormValues {
  validationType: string;
  formatValidationType?: string;
  validationName: string;
  validationDataItem: string;
  conditionGroups: ConditionGroup[];
  failureMessage: string;
}

interface CreateRuleModalProps {
  visible: boolean;
  setVisible: (visible: boolean) => void;
  entity: EntityListItem;
  successCallback: () => void;
}

const CreateRuleModal: React.FC<CreateRuleModalProps> = ({ visible, setVisible, successCallback, entity }) => {
  const { curAppId } = useAppStore();
  const [form] = Form.useForm<RuleFormValues>();
  const [loading, setLoading] = useState(false);
  const [leftFieldOptions, setLeftFieldOptions] = useState<any[]>([]);
  const [rightFieldOptions, setRightFieldOptions] = useState<any[]>([]);

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
    { label: 'URL格式', value: 'url' },
    { label: '日期格式', value: 'date' }
  ];

  // 操作符选项
  const operatorOptions = [
    { label: '等于', value: 'equals' },
    { label: '不等于', value: 'not_equals' },
    { label: '大于', value: 'greater_than' },
    { label: '大于等于', value: 'greater_equal' },
    { label: '小于', value: 'less_than' },
    { label: '小于等于', value: 'less_equal' },
    { label: '包含', value: 'contains' },
    { label: '不包含', value: 'not_contains' },
    { label: '为空', value: 'is_null' },
    { label: '不为空', value: 'is_not_null' }
  ];

  // 值类型选项
  const valueTypeOptions = [
    { label: '自定义', value: 'custom' },
    { label: '字段值', value: 'field' },
    { label: '固定值', value: 'fixed' },
    { label: '当前用户', value: 'current_user' },
    { label: '当前时间', value: 'current_time' }
  ];

  // 生成唯一ID
  const generateId = () => `id_${Date.now()}_${Math.random().toString(36).substr(2, 9)}`;

  // 创建默认条件行
  const createDefaultConditionRow = (): ConditionRow => ({
    field: '',
    operator: 'equals',
    valueType: 'custom',
    value: ''
  });

  // 创建默认条件组
  const createDefaultConditionGroup = (): ConditionGroup => ({
    id: generateId(),
    conditions: [createDefaultConditionRow()],
    logic: 'AND'
  });

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

  // 添加AND条件（在同一组内添加行）
  const addAndCondition = (groupIndex: number) => {
    const currentGroups = form.getFieldValue('conditionGroups') || [];
    const newGroups = [...currentGroups];
    newGroups[groupIndex].conditions.push(createDefaultConditionRow());
    form.setFieldValue('conditionGroups', newGroups);
  };

  // 添加OR条件（添加新的条件组）
  const addOrCondition = () => {
    const currentGroups = form.getFieldValue('conditionGroups') || [];
    const newGroups = [...currentGroups, createDefaultConditionGroup()];
    form.setFieldValue('conditionGroups', newGroups);
  };

  // 删除条件行
  const removeConditionRow = (groupIndex: number, conditionIndex: number) => {
    const currentGroups = form.getFieldValue('conditionGroups') || [];
    const newGroups = [...currentGroups];

    // 确保至少保留一行条件
    if (newGroups[groupIndex].conditions.length > 1) {
      newGroups[groupIndex].conditions.splice(conditionIndex, 1);
      form.setFieldValue('conditionGroups', newGroups);
    } else {
      Message.warning('至少需要保留一个条件');
    }
  };

  // 删除条件组
  const removeConditionGroup = (groupIndex: number) => {
    const currentGroups = form.getFieldValue('conditionGroups') || [];

    // 确保至少保留一个条件组
    if (currentGroups.length > 1) {
      const newGroups = currentGroups.filter((_, index) => index !== groupIndex);
      form.setFieldValue('conditionGroups', newGroups);
    } else {
      Message.warning('至少需要保留一个条件组');
    }
  };

  // 更新条件行
  const updateConditionRow = (groupIndex: number, conditionIndex: number, field: keyof ConditionRow, value: string) => {
    const currentGroups = form.getFieldValue('conditionGroups') || [];
    const newGroups = [...currentGroups];
    newGroups[groupIndex].conditions[conditionIndex][field] = value;
    form.setFieldValue('conditionGroups', newGroups);
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
      //   appId: curAppId
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

  // 加载字段选项
  const loadFieldOptions = async () => {
    const res = await getEntityFieldsWithChildren(entity.id);
    console.log('字段选项:', res);
    let allFields: object[] = [];
    let parentFields: object[] = [];
    let childFields: object[] = [];

    if (res?.parentFields?.length > 0) {
      parentFields = res.parentFields;
    }
    if (res?.childEntities?.length > 0) {
      res.childEntities.forEach((item: { childFields: object[] }) => {
        if (item?.childFields?.length > 0) {
          childFields = childFields.concat(item.childFields);
        }
      });
    }
    allFields = [...parentFields, ...childFields];
    setLeftFieldOptions(allFields);
    setRightFieldOptions(allFields);
  };

  // 初始化表单数据
  React.useEffect(() => {
    if (visible) {
      // 设置默认的条件组
      form.setFieldValue('conditionGroups', [createDefaultConditionGroup()]);
      loadFieldOptions();
    }
  }, [visible, form]);

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
      style={{ width: 800 }}
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

        {/* 条件设置 */}
        <Form.Item noStyle shouldUpdate>
          {(values) => {
            if (isConditionSettingVisible()) {
              const conditionGroups = values.conditionGroups || [];

              return (
                <Form.Item label="条件设置" field="conditionGroups">
                  <div className={styles['condition-setting-container']}>
                    {conditionGroups.map((group, groupIndex) => (
                      <div key={group.id} className={styles['condition-group']}>
                        {groupIndex > 0 && (
                          <div className={styles['condition-logic-divider']}>
                            <span className={styles['logic-label']}>或者</span>
                          </div>
                        )}

                        <div className={styles['condition-group-content']}>
                          {group.conditions.map((condition, conditionIndex) => (
                            <div key={conditionIndex} className={styles['condition-row']}>
                              <Space size="small" align="start">
                                {/* 字段选择 */}
                                <Select
                                  placeholder="请选择字段"
                                  value={condition.field}
                                  onChange={(value) => updateConditionRow(groupIndex, conditionIndex, 'field', value)}
                                  style={{ width: 200 }}
                                  options={leftFieldOptions}
                                />

                                {/* 操作符 */}
                                <Select
                                  placeholder="请选择操作符"
                                  value={condition.operator}
                                  onChange={(value) =>
                                    updateConditionRow(groupIndex, conditionIndex, 'operator', value)
                                  }
                                  style={{ width: 120 }}
                                  options={operatorOptions}
                                />

                                {/* 值类型 */}
                                <Select
                                  placeholder="请选择值类型"
                                  value={condition.valueType}
                                  onChange={(value) =>
                                    updateConditionRow(groupIndex, conditionIndex, 'valueType', value)
                                  }
                                  style={{ width: 100 }}
                                  options={valueTypeOptions}
                                />

                                {/* 值输入 */}
                                <Input
                                  placeholder="请输入值"
                                  value={condition.value}
                                  onChange={(value) => updateConditionRow(groupIndex, conditionIndex, 'value', value)}
                                  style={{ width: 150 }}
                                />

                                {/* 删除按钮 */}
                                <Button
                                  type="text"
                                  status="danger"
                                  size="mini"
                                  icon={<IconDelete />}
                                  onClick={() => removeConditionRow(groupIndex, conditionIndex)}
                                  className={styles['delete-condition-btn']}
                                />
                              </Space>
                            </div>
                          ))}

                          {/* 添加AND条件按钮 */}
                          <div className={styles['add-condition-buttons']}>
                            <Button
                              type="dashed"
                              size="small"
                              icon={<IconPlus />}
                              onClick={() => addAndCondition(groupIndex)}
                              className={styles['add-and-btn']}
                            >
                              并且
                            </Button>
                          </div>
                        </div>
                      </div>
                    ))}

                    {/* 添加OR条件按钮 */}
                    <div className={styles['add-or-button-container']}>
                      <Button
                        type="dashed"
                        size="small"
                        icon={<IconPlus />}
                        onClick={addOrCondition}
                        className={styles['add-or-btn']}
                      >
                        或者
                      </Button>
                    </div>
                  </div>
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
