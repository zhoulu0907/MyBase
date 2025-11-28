import type { EntityListItem } from '@/pages/CreateApp/pages/DataFactory/utils/interface';
import { Form, Grid, Input, Message, Modal, Select, Space } from '@arco-design/web-react';
import * as ruleService from '@onebase/app';
import React, { useCallback, useEffect, useState } from 'react';
import { useAppStore } from '@/store/store_app';
import { validationTypeMap, ruleTip, validationTypeList, VALIDATION_TYPES, REGEX_LIST } from './rule.ts';
import styles from '../modal.module.less';

interface RuleFormValues {
  // validationType: string;
  validationType: string;
  formatValidationType?: string;
  rgName: string;
  fieldId?: string;
  childEntityId?: string;
  popPrompt: string;
  popType: string;
  id?: string;
  regex?: string;
  minLength?: string;
  maxLength?: string;
  minValue?: string;
  maxValue?: string;
}

interface CreateRuleModalProps {
  visible: boolean;
  setVisible: (visible: boolean) => void;
  entity: EntityListItem;
  successCallback: () => void;
  ruleType: string;
  editRule: Partial<RuleFormValues> | null;
}

const REGEX_OPTIONS = REGEX_LIST.map((item) => {
  return {
    label: (
      <>
        <span>{item.label}</span>
        <span className={styles.regexText}>{item.value}</span>
      </>
    ),
    value: item.value
  };
});

const CreateOtherRule: React.FC<CreateRuleModalProps> = ({
  visible,
  setVisible,
  successCallback,
  entity,
  ruleType,
  editRule
}) => {
  const { curAppId } = useAppStore();
  const [form] = Form.useForm<RuleFormValues>();
  const [loading, setLoading] = useState(false);
  const [fieldOptions, setFieldOptions] = useState<{ label: string; value: string }[]>([]);
  const [childEntityOptions, setChildEntityOptions] = useState<{ label: string; value: string }[]>([]);

  const numberValidator = (value: string | undefined, cb: (error?: React.ReactNode) => void) => {
    if (value && !/^\d+(.\d{1,4})?$/.test(value)) {
      cb('请输入数字');
    } else {
      cb();
    }
  };

  // 监听校验类型变化，控制格式校验类型字段的显示
  const handleValidationTypeChange = (value: string) => {
    if (value !== 'format') {
      form.setFieldValue('formatValidationType', undefined);
    }
  };

  const handleGetRuleById = useCallback(
    async (id: string) => {
      const ruleHandlers = {
        [VALIDATION_TYPES.REQUIRED]: ruleService.getRequiredRuleById,
        [VALIDATION_TYPES.UNIQUE]: ruleService.getUniqueRuleById,
        [VALIDATION_TYPES.LENGTH]: ruleService.getLengthRuleById,
        [VALIDATION_TYPES.RANGE]: ruleService.getRangeRuleById,
        [VALIDATION_TYPES.FORMAT]: ruleService.getFormatRuleById,
        [VALIDATION_TYPES.CHILD_NOT_EMPTY]: ruleService.getChildNotEmptyRuleById
      };
      const handler = ruleHandlers[ruleType as keyof typeof ruleHandlers];
      if (handler) {
        const res = await handler(id);
        if (res) {
          form.setFieldsValue({ ...res, popPrompt: res.promptMessage, regex: res.formatValue });
        }
      }
    },
    [form, ruleType]
  );

  const handleCreateRule = async (values: RuleFormValues) => {
    const params = {
      ...values,
      entityId: entity.id,
      appId: curAppId
    };

    let res;

    const ruleHandlers = {
      [VALIDATION_TYPES.REQUIRED]: ruleService.createRequiredRule,
      [VALIDATION_TYPES.UNIQUE]: ruleService.createUniqueRule,
      [VALIDATION_TYPES.LENGTH]: ruleService.createLengthRule,
      [VALIDATION_TYPES.RANGE]: ruleService.createRangeRule,
      [VALIDATION_TYPES.FORMAT]: ruleService.createFormatRule,
      [VALIDATION_TYPES.CHILD_NOT_EMPTY]: ruleService.createChildNotEmptyRule
    };

    const handler = ruleHandlers[ruleType as keyof typeof ruleHandlers];
    if (handler) {
      res = await handler(params);
    }

    if (res) {
      Message.success('创建规则成功');
      form.resetFields();
      setVisible(false);
      successCallback();
    } else {
      console.error(res.msg || '创建失败');
    }
  };

  const handleUpdateRule = async (values: RuleFormValues) => {
    const params = {
      ...values,
      isEnabled: 0, // 启用
      id: editRule?.id,
      entityId: entity.id,
      appId: curAppId
    };

    const ruleHandlers = {
      [VALIDATION_TYPES.REQUIRED]: ruleService.updateRequiredRule,
      [VALIDATION_TYPES.UNIQUE]: ruleService.updateUniqueRule,
      [VALIDATION_TYPES.LENGTH]: ruleService.updateLengthRule,
      [VALIDATION_TYPES.RANGE]: ruleService.updateRangeRule,
      [VALIDATION_TYPES.FORMAT]: ruleService.updateFormatRule,
      [VALIDATION_TYPES.CHILD_NOT_EMPTY]: ruleService.updateChildNotEmptyRule
    };

    try {
      const handler = ruleHandlers[ruleType as keyof typeof ruleHandlers];
      if (handler) {
        const res = await handler(params);
        if (res) {
          Message.success('更新规则成功');
          form.resetFields();
          setVisible(false);
          successCallback();
        } else {
          console.error(res.msg || '更新失败');
        }
      }
    } catch (error) {
      console.error('更新规则失败:', error);
    }
  };

  // 提交表单
  const handleFinish = async () => {
    try {
      const values = await form.validate();
      setLoading(true);

      console.log('规则表单数据:', values, form.getFieldsValue());

      if (editRule) {
        handleUpdateRule(values);
      } else {
        handleCreateRule(values);
      }
    } catch (error) {
      console.error('提交表单失败:', error);
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
  const loadFieldOptions = useCallback(async () => {
    try {
      const res = await ruleService.getEntityFieldsWithChildren(entity.id);

      const parentFields = res.parentFields.map((field: { displayName: string; fieldId: string }) => ({
        label: field.displayName,
        value: field.fieldId
      }));

      const childEntities = res.childEntities.map((item: { childEntityName: string; childEntityId: string }) => ({
        label: item.childEntityName,
        value: item.childEntityId
      }));
      setChildEntityOptions(childEntities); // 子表字段选项
      setFieldOptions(parentFields); // 父表字段选项
    } catch (error) {
      console.error('加载字段选项失败:', error);
      throw error;
    }
  }, [entity.id]);

  // 初始化表单数据
  useEffect(() => {
    if (!visible) {
      return;
    }

    form.setFieldValue('validationType', ruleType);

    const initFormData = async () => {
      await loadFieldOptions();

      if (editRule?.id) {
        await handleGetRuleById(editRule.id);
      }
    };

    initFormData();
  }, [visible, editRule?.id, form, ruleType]);

  return (
    <Modal
      className={styles.createRuleModal}
      title={`${editRule ? '编辑' : '新建'}数据规则-${validationTypeMap[ruleType]}`}
      visible={visible}
      onOk={handleFinish}
      onCancel={handleCancel}
      okText={`${editRule ? '确定' : '创建'}`}
      cancelText="取消"
      confirmLoading={loading}
      style={{ width: 600 }}
    >
      <Form form={form} layout="vertical">
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

        {ruleType !== VALIDATION_TYPES.CHILD_NOT_EMPTY && (
          <Form.Item label="校验数据项" field="fieldId" rules={[{ required: true, message: '请选择校验数据项' }]}>
            <Select
              placeholder="请选择校验数据项"
              options={fieldOptions}
              showSearch
              filterOption={(inputValue, option) =>
                option.props.value.toLowerCase().indexOf(inputValue.toLowerCase()) >= 0 ||
                option.props.children.toLowerCase().indexOf(inputValue.toLowerCase()) >= 0
              }
            />
          </Form.Item>
        )}

        {ruleType === VALIDATION_TYPES.CHILD_NOT_EMPTY && (
          <Form.Item label="校验子表" field="childEntityId" rules={[{ required: true, message: '请选择校验子表' }]}>
            <Select
              options={childEntityOptions}
              placeholder="请选择校验子表"
              showSearch
              filterOption={(inputValue, option) =>
                option.props.value.toLowerCase().indexOf(inputValue.toLowerCase()) >= 0 ||
                option.props.children.toLowerCase().indexOf(inputValue.toLowerCase()) >= 0
              }
            />
          </Form.Item>
        )}

        <Form.Item label="校验类型" field="validationType" hidden>
          <Select onChange={handleValidationTypeChange} placeholder="请选择校验类型" disabled>
            {validationTypeList.map((option) => (
              <Select.Option key={option.value} value={option.value}>
                {option.label}
              </Select.Option>
            ))}
          </Select>
        </Form.Item>

        {ruleType === VALIDATION_TYPES.LENGTH && (
          <Grid.Row gutter={16}>
            <Grid.Col span={12}>
              <Form.Item
                label="最小长度"
                field="minLength"
                rules={[{ required: true, message: '请输入最小长度' }]}
                initialValue="0"
              >
                <Input placeholder="请输入最小长度" />
              </Form.Item>
            </Grid.Col>
            <Grid.Col span={12}>
              <Form.Item
                label="最大长度"
                field="maxLength"
                rules={[{ required: true, message: '请输入最大长度' }]}
                initialValue="800"
              >
                <Input placeholder="请输入最大长度" />
              </Form.Item>
            </Grid.Col>
          </Grid.Row>
        )}

        {ruleType === VALIDATION_TYPES.RANGE && (
          <Form.Item label="范围区间" required>
            <Space align="center" className={styles.rangeSpace}>
              <Form.Item
                field="minValue"
                rules={[{ required: true, message: '请输入范围区间' }, { validator: numberValidator }]}
              >
                <Input placeholder="请输入范围区间" />
              </Form.Item>
              <span>~</span>
              <Form.Item
                field="maxValue"
                rules={[{ required: true, message: '请输入范围区间' }, { validator: numberValidator }]}
              >
                <Input placeholder="请输入范围区间" />
              </Form.Item>
            </Space>
          </Form.Item>
        )}

        {ruleType === VALIDATION_TYPES.FORMAT && (
          <Form.Item label="正则表达式" field="regex" rules={[{ required: true, message: '请输入正则表达式' }]}>
            <Select
              placeholder="请输入正则表达式"
              options={REGEX_OPTIONS}
              allowCreate
              filterOption
              labelInValue
              onChange={(value) => {
                if (typeof value === 'string') {
                  form.setFieldValue('regex', value);
                } else if (value && typeof value === 'object' && 'value' in value) {
                  form.setFieldValue('regex', value.value);
                }
              }}
            />
          </Form.Item>
        )}

        {/* 验证失败提示语 */}
        <Form.Item field="popPrompt" label="验证失败提示语">
          <Input placeholder={ruleTip[ruleType]} maxLength={40} />
        </Form.Item>
      </Form>
    </Modal>
  );
};

export default CreateOtherRule;
