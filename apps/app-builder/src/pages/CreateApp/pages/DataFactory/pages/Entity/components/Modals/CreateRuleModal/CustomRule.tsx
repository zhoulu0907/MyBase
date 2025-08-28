import type { EntityListItem } from '@/pages/CreateApp/pages/DataFactory/utils/interface';
import { useAppStore } from '@/store/store_app';
import { Button, Form, Input, Message, Modal, Radio, Select, Space } from '@arco-design/web-react';
import { IconDelete, IconPlus } from '@arco-design/web-react/icon';
import { createRule, getEntityFieldsWithChildren } from '@onebase/app';
import React, { useState } from 'react';
import { operatorOptions, valueTypeOptions, validationTypeOptions, formatValidationTypeOptions } from './rule.ts';
import styles from '../modal.module.less';
import type { ConditionRow } from '@onebase/app';

interface RuleFormValues {
  validationType: string;
  formatValidationType?: string;
  rgName: string;
  validationDataItem: string;
  valueRules: ConditionRow[][];
  popPrompt: string;
  popType: string;
}

interface CreateRuleModalProps {
  visible: boolean;
  setVisible: (visible: boolean) => void;
  entity: EntityListItem;
  successCallback: () => void;
}

const CreateCustomRule: React.FC<CreateRuleModalProps> = ({ visible, setVisible, successCallback, entity }) => {
  const { curAppId } = useAppStore();
  const [form] = Form.useForm<RuleFormValues>();
  const [loading, setLoading] = useState(false);
  const [leftFieldOptions, setLeftFieldOptions] = useState<any[]>([]);
  const [rightFieldOptions, setRightFieldOptions] = useState<any[]>([]);

  // 生成唯一ID
  const generateId = () => `id_${Date.now()}_${Math.random().toString(36).substr(2, 9)}`;

  // 创建默认条件行
  const createDefaultConditionRow = (): ConditionRow => ({
    fieldId: '',
    operator: 'equals',
    valueType: 'custom',
    fieldValue: '',
    logicOperator: 'AND',
    logicType: 'CONDITION'
  });

  // 创建默认条件组
  const createDefaultConditionGroup = (): ConditionRow[] => [createDefaultConditionRow()];

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
    const currentGroups = form.getFieldValue('valueRules') || [];
    const newGroups = [...currentGroups];
    newGroups[groupIndex].push(createDefaultConditionRow());
    form.setFieldValue('valueRules', newGroups);
  };

  // 添加OR条件（添加新的条件组）
  const addOrCondition = () => {
    const currentGroups = form.getFieldValue('valueRules') || [];
    const newGroups = [...currentGroups, createDefaultConditionGroup()];
    form.setFieldValue('valueRules', newGroups);
  };

  // 删除条件行
  const removeConditionRow = (groupIndex: number, conditionIndex: number) => {
    const currentGroups = form.getFieldValue('valueRules') || [];
    const newGroups = [...currentGroups];

    // 确保至少保留一行条件
    if (newGroups[groupIndex].length > 1) {
      newGroups[groupIndex].splice(conditionIndex, 1);
      form.setFieldValue('valueRules', newGroups);
    } else {
      Message.warning('至少需要保留一个条件');
    }
  };

  // 删除条件组
  const removeConditionGroup = (groupIndex: number) => {
    const currentGroups = form.getFieldValue('valueRules') || [];

    // 确保至少保留一个条件组
    if (currentGroups.length > 1) {
      const newGroups = currentGroups.filter((_, index) => index !== groupIndex);
      form.setFieldValue('valueRules', newGroups);
    } else {
      Message.warning('至少需要保留一个条件组');
    }
  };

  // 更新条件行
  const updateConditionRow = (
    groupIndex: number,
    conditionIndex: number,
    fieldId: keyof ConditionRow,
    value: string
  ) => {
    const currentGroups = form.getFieldValue('valueRules') || [];
    const newGroups = [...currentGroups];
    newGroups[groupIndex][conditionIndex][fieldId] = value;
    form.setFieldValue('valueRules', newGroups);
  };

  // 提交表单
  const handleFinish = async () => {
    try {
      const values = await form.validate();
      setLoading(true);

      console.log('规则表单数据:', values, form.getFieldsValue());

      // TODO: 调用创建规则的API
      const res = await createRule({
        ...values,
        entityId: entity.id
        // appId: curAppId
      });

      console.log('createRule', res);

      Message.success('创建规则成功');
      form.resetFields();
      setVisible(false);
      successCallback();
    } catch (error) {
      console.error('创建规则失败:', error);
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
    let allChildFields: object[] = [];

    if (res?.parentFields?.length > 0) {
      parentFields = res.parentFields.map((item: { displayName: string; fieldID: string }) => ({
        label: item.displayName,
        value: item.fieldID,
        isParent: true // 增加主表标识
      }));
    }
    if (res?.childEntities?.length > 0) {
      res.childEntities.forEach((item: { childFields: { displayName: string; fieldID: string }[] }) => {
        if (item?.childFields?.length > 0) {
          const childFields = item?.childFields?.map((item: { displayName: string; fieldID: string }) => ({
            label: item.displayName,
            value: item.fieldID
          }));
          allChildFields = allChildFields.concat(childFields);
        }
      });
    }
    allFields = [...parentFields, ...allChildFields];
    // console.log('allFields', allFields, parentFields, childFields);
    setLeftFieldOptions(allFields);
    setRightFieldOptions(allChildFields);
  };

  // 初始化表单数据
  React.useEffect(() => {
    if (visible) {
      // 设置默认的条件组
      form.setFieldValue('valueRules', [createDefaultConditionGroup()]);
      loadFieldOptions();
    }
  }, [visible]);

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
          field="rgName"
          rules={[
            { required: true, message: '请输入规则名称' },
            { max: 50, message: '规则名称不能超过50个字符' }
          ]}
        >
          <Input placeholder="请输入规则名称" maxLength={50} showWordLimit />
        </Form.Item>

        {/* <Form.Item label="校验类型" field="validationType" rules={[{ required: true, message: '请选择校验类型' }]}>
          <Select onChange={handleValidationTypeChange} placeholder="请选择校验类型">
            {validationTypeOptions.map((option) => (
              <Select.Option key={option.value} value={option.value}>
                {option.label}
              </Select.Option>
            ))}
          </Select>
        </Form.Item> */}

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
              const valueRules = values.valueRules || [];

              return (
                <Form.Item label="条件设置" field="valueRules">
                  <div className={styles['condition-setting-container']}>
                    {valueRules.map((group, groupIndex) => (
                      <div key={groupIndex} className={styles['condition-group']}>
                        {groupIndex > 0 && (
                          <div className={styles['condition-logicOperator-divider']}>
                            <span className={styles['logicOperator-label']}>或者</span>
                          </div>
                        )}

                        <div className={styles['condition-group-content']}>
                          {group.map((condition, conditionIndex) => (
                            <div key={conditionIndex} className={styles['condition-row']}>
                              <Space size="small" align="start">
                                {/* 字段选择 */}
                                <Select
                                  placeholder="请选择字段"
                                  value={condition.fieldId}
                                  onChange={(value) => updateConditionRow(groupIndex, conditionIndex, 'fieldId', value)}
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
                                  // options={
                                  //   fieldOperatorMapping[condition.fieldId]?.map(operator => ({
                                  //     value: operator,
                                  //     label: operatorOptions[operator],
                                  //   })) || []
                                  // }
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
                                {/* 静态值 */}
                                {condition.valueType === 'custom' && (
                                  <Input
                                    placeholder="请输入值"
                                    value={condition.fieldValue}
                                    onChange={(value) =>
                                      updateConditionRow(groupIndex, conditionIndex, 'fieldValue', value)
                                    }
                                    style={{ width: 150 }}
                                  />
                                )}
                                {/* 变量 */}
                                {condition.valueType === 'fieldId' && (
                                  <Select
                                    placeholder="请选择字段"
                                    value={condition.fieldValue}
                                    onChange={(value) =>
                                      updateConditionRow(groupIndex, conditionIndex, 'fieldValue', value)
                                    }
                                    style={{ width: 150 }}
                                    options={rightFieldOptions}
                                  />
                                )}

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
        <Form.Item label="验证失败提示语" field="popType">
          <Radio.Group
            defaultValue={'SHORT'}
            options={[
              { label: '短提示框', value: 'SHORT' },
              { label: '长提示框', value: 'LONG' }
            ]}
          />
        </Form.Item>
        <Form.Item field="popPrompt">
          <Input.TextArea placeholder="请输入验证失败提示语" rows={3} maxLength={200} showWordLimit />
        </Form.Item>
      </Form>
    </Modal>
  );
};

export default CreateCustomRule;
