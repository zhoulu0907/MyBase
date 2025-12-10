import type { EntityListItem } from '@/pages/CreateApp/pages/DataFactory/utils/interface';
import { Form, Input, Message, Modal } from '@arco-design/web-react';
// import { IconDelete, IconPlus } from '@arco-design/web-react/icon';
import type { ConditionRow, EntityFieldValidationTypes } from '@onebase/app';
import {
  createCustomRule,
  getCustomRuleById,
  getEntityFieldsWithChildren,
  getFieldCheckTypeApi,
  updateCustomRule
} from '@onebase/app';
import React, { useState } from 'react';
// import { formatValidationTypeOptions, operatorOptions, valueTypeOptions } from './rule.ts';
import ConditionEditor from './ConditionEditor.tsx';
import styles from '../modal.module.less';
import { VALIDATION_TYPES } from './rule.ts';

interface RuleFormValues {
  id?: string;
  validationType: string;
  formatValidationType?: string;
  rgName: string;
  validationDataItem: string;
  valueRules: ConditionRow[][];
  popPrompt: string;
  popType: string;
  filterCondition?: ConditionRow[][];
}

interface CreateRuleModalProps {
  visible: boolean;
  setVisible: (visible: boolean) => void;
  entity: EntityListItem;
  successCallback: () => void;
  editRule: Partial<RuleFormValues> | null;
}

const CreateCustomRule: React.FC<CreateRuleModalProps> = ({
  visible,
  setVisible,
  successCallback,
  entity,
  editRule
}) => {
  const [form] = Form.useForm<RuleFormValues>();
  const [loading, setLoading] = useState(false);
  const [allOptions, setAllOptions] = useState<any[]>([]);
  const [parentOptions, setParentOptions] = useState<any[]>([]);
  const [filterFieldCheckType, setFilterFieldCheckType] = useState<EntityFieldValidationTypes[]>([]);
  // 创建默认条件行
  const createDefaultConditionRow = (): ConditionRow => ({
    fieldId: '',
    operator: 'equals',
    valueType: 'custom',
    fieldValue: '',
    logicOperator: 'AND'
  });

  // 创建默认条件组
  const createDefaultConditionGroup = (): ConditionRow[] => [createDefaultConditionRow()];

  // 根据ID获取规则
  const handleGetRuleById = async (id: string) => {
    try {
      const res = await getCustomRuleById(id);
      console.log('getRuleById', res);
      if (res) {
        form.setFieldsValue(res);

        const conditions = res?.valueRules.map((item) => {
          return {
            conditions: item.map((item) => ({
              fieldId: item.fieldId,
              op: item.operator,
              operatorType: item.valueType,
              value: item.fieldValue
            }))
          };
        });
        form.setFieldValue('filterCondition', conditions);
      }
    } catch (error) {
      console.error('获取规则失败:', error);
    }
  };

  // 提交表单
  const handleFinish = async () => {
    try {
      const values = await form.validate();
      setLoading(true);

      console.log('规则表单数据:', values);

      const params = {
        popPrompt: values.popPrompt,
        popType: values.popType,
        rgName: values.rgName,
        valueRules: values.filterCondition.map((item) =>
          item.conditions.map((item) => ({
            fieldId: item.fieldId,
            operator: item.op,
            valueType: item.operatorType,
            fieldValue: item.value
          }))
        ),
        entityId: entity.id,
        validationType: VALIDATION_TYPES.SELF_DEFINED
      };

      let res;

      if (editRule) {
        params.id = editRule.id;
        res = await updateCustomRule(params);
      } else {
        res = await createCustomRule(params);
      }

      if (res) {
        Message.success(`${editRule ? '编辑' : '创建'}规则成功`);
        form.resetFields();
        setVisible(false);
        successCallback();
      } else {
        console.error(res.msg || `${editRule ? '编辑' : '创建'}失败`);
      }
    } catch (error) {
      console.error(`${editRule ? '编辑' : '创建'}规则失败:`, error);
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
    // 处理主表字段
    const parentFields = [
      {
        key: res.entityId,
        title: res.entityName,
        children: res?.parentFields.map((item) => {
          return {
            key: item.fieldId,
            title: item.displayName,
            fieldType: item.fieldType
          };
        })
      }
    ];

    // 处理子表字段
    const rawChildEntities = res?.childEntities || [];
    const uniqueChildEntities = Array.from(
      new Map(rawChildEntities.map((entity) => [entity.childEntityId, entity])).values()
    );

    const childFields = uniqueChildEntities.map((entity) => {
      return {
        title: entity.childEntityName,
        key: entity.childEntityId,
        children: entity.childFields.map((item) => ({
          title: item.displayName,
          key: item.fieldId,
          fieldType: item.fieldType
        }))
      };
    });
    setAllOptions([...parentFields, ...childFields]);
    setParentOptions(parentFields);

    getFieldCheckType(res?.parentFields?.map((item) => item.fieldId));
  };

  // 批量获取字段可选校验类型
  const getFieldCheckType = async (fieldIds: string[]) => {
    const res = await getFieldCheckTypeApi(fieldIds);
    setFilterFieldCheckType(res);
  };

  // 初始化表单数据
  React.useEffect(() => {
    if (visible) {
      // 设置默认的条件组
      form.setFieldValue('valueRules', [createDefaultConditionGroup()]);
      loadFieldOptions();
      if (editRule) {
        handleGetRuleById(editRule?.id || '');
      }
    }
  }, [visible, editRule]);

  return (
    <Modal
      className={styles.createRuleModal}
      title={`${editRule ? '编辑' : '添加'}规则`}
      visible={visible}
      onOk={handleFinish}
      onCancel={handleCancel}
      okText={`${editRule ? '确定' : '创建'}`}
      cancelText="取消"
      confirmLoading={loading}
      style={{ width: 610 }}
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

        <Form.Item field="filterCondition" rules={[{ required: true, message: '请添加条件' }]}>
          <ConditionEditor
            nodeId={entity.id}
            label="条件设置"
            required={true}
            form={form}
            fields={parentOptions}
            entityFieldValidationTypes={filterFieldCheckType}
            variableOptions={allOptions}
          />
        </Form.Item>

        <Form.Item
          label="弹窗提示"
          field="popPrompt"
          rules={[
            { required: true, message: '请输入弹窗提示语' },
            { max: 200, message: '弹窗提示语不能超过200个字符' }
          ]}
        >
          <Input placeholder="请输入校验不通过后的弹窗提示语" maxLength={200} />
        </Form.Item>
      </Form>
    </Modal>
  );
};

export default CreateCustomRule;
