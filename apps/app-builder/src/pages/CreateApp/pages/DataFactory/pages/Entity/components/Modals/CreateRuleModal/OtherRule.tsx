import type { EntityListItem } from '@/pages/CreateApp/pages/DataFactory/utils/interface';
import { Form, Grid, Input, Message, Modal, Select } from '@arco-design/web-react';
import {
  createLengthRule,
  createRequiredRule,
  createRangeRule,
  createFormatRule,
  createUniqueRule,
  getEntityFieldsWithChildren,
  createChildNotEmptyRule
} from '@onebase/app';
import React, { useState } from 'react';
import { useAppStore } from '@/store/store_app';
import { validationTypeMap, ruleTip, validationTypeOptions } from './rule.ts';
import styles from '../modal.module.less';

interface RuleFormValues {
  // validationType: string;
  validationType: string;
  formatValidationType?: string;
  rgName: string;
  fieldId: string;
  promptMessage: string;
  popType: string;
}

interface CreateRuleModalProps {
  visible: boolean;
  setVisible: (visible: boolean) => void;
  entity: EntityListItem;
  successCallback: () => void;
  ruleType: string;
}

const CreateOtherRule: React.FC<CreateRuleModalProps> = ({
  visible,
  setVisible,
  successCallback,
  entity,
  ruleType
}) => {
  const { curAppId } = useAppStore();
  const [form] = Form.useForm<RuleFormValues>();
  const [loading, setLoading] = useState(false);
  const [fieldOptions, setFieldOptions] = useState<any[]>([]);

  // 监听校验类型变化，控制格式校验类型字段的显示
  const handleValidationTypeChange = (value: string) => {
    if (value !== 'format') {
      form.setFieldValue('formatValidationType', undefined);
    }
  };

  // 提交表单
  const handleFinish = async () => {
    try {
      const values = await form.validate();
      setLoading(true);

      console.log('规则表单数据:', values, form.getFieldsValue());

      const params = {
        ...values,
        entityId: entity.id,
        appId: curAppId
      };

      let res;

      switch (ruleType) {
        case 'required':
          res = await createRequiredRule(params);
          break;
        case 'unique':
          res = await createUniqueRule(params);
          break;
        case 'length':
          res = await createLengthRule(params);
          break;
        case 'range':
          res = await createRangeRule(params);
          break;
        case 'format':
          res = await createFormatRule(params);
          break;
        case 'subtable_empty':
          res = await createChildNotEmptyRule(params);
          break;
      }

      console.log('createRule', res);

      if (res) {
        Message.success('创建规则成功');
        form.resetFields();
        setVisible(false);
        successCallback();
      } else {
        console.error(res.msg || '创建失败');
      }
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
    setFieldOptions(allFields);
  };

  // 初始化表单数据
  React.useEffect(() => {
    if (visible) {
      loadFieldOptions();
      form.setFieldValue('validationType', ruleType);
    }
  }, [visible]);

  return (
    <Modal
      className={styles['create-rule-modal']}
      title={`新建数据规则-${validationTypeMap[ruleType]}`}
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
          field="rgName"
          rules={[
            { required: true, message: '请输入规则名称' },
            { max: 50, message: '规则名称不能超过50个字符' }
          ]}
        >
          <Input placeholder="请输入规则名称" maxLength={50} showWordLimit />
        </Form.Item>

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

        <Form.Item label="校验类型" field="validationType" hidden>
          <Select onChange={handleValidationTypeChange} placeholder="请选择校验类型" disabled>
            {validationTypeOptions.map((option) => (
              <Select.Option key={option.value} value={option.value}>
                {option.label}
              </Select.Option>
            ))}
          </Select>
        </Form.Item>

        {ruleType === 'length' && (
          <Grid.Row gutter={16}>
            <Grid.Col span={12}>
              <Form.Item label="最小长度" field="minLength" rules={[{ required: true, message: '请输入最小长度' }]}>
                <Input placeholder="请输入最小长度" defaultValue={0} />
              </Form.Item>
            </Grid.Col>
            <Grid.Col span={12}>
              <Form.Item label="最大长度" field="maxLength" rules={[{ required: true, message: '请输入最大长度' }]}>
                <Input placeholder="请输入最大长度" defaultValue={8000} />
              </Form.Item>
            </Grid.Col>
          </Grid.Row>
        )}

        {ruleType === 'range' && (
          <Grid.Row gutter={16}>
            <Grid.Col span={10}>
              <Form.Item label="范围区间" field="range" rules={[{ required: true, message: '请输入范围区间' }]}>
                <Input placeholder="请输入范围区间" />
              </Form.Item>
            </Grid.Col>
            <Grid.Col span={4}>~</Grid.Col>
            <Grid.Col span={10}>
              <Form.Item field="range" rules={[{ required: true, message: '请输入范围区间' }]}>
                <Input placeholder="请输入范围区间" />
              </Form.Item>
            </Grid.Col>
          </Grid.Row>
        )}

        {ruleType === 'format' && (
          <Form.Item
            label="正则表达式"
            field="formatValidationType"
            rules={[{ required: true, message: '请输入正则表达式' }]}
          >
            <Input placeholder="请输入正则表达式" />
          </Form.Item>
        )}

        {/* 验证失败提示语 */}
        <Form.Item field="promptMessage" label="验证失败提示语">
          <Input placeholder={ruleTip[ruleType]} maxLength={40} />
        </Form.Item>
      </Form>
    </Modal>
  );
};

export default CreateOtherRule;
