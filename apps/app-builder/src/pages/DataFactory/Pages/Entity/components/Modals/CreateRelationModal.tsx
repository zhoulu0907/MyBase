import { Form, Message, Modal, Select } from '@arco-design/web-react';
import React, { useEffect, useState } from 'react';
import type { EdgeData, EntityField, EntityNode } from '../../../../utils/interface';
import styles from './modal.module.less';

interface RelationFormValues {
  leftEntity: string;
  leftField: string;
  relationType: string;
  rightEntity: string;
  rightField: string;
}

interface EntityOption {
  label: string;
  value: string;
}

interface FieldOption {
  label: string;
  value: string;
}

// 关联关系类型选项
const relationTypes = [
  { label: '一对一', value: 'ONE_TO_ONE' },
  { label: '一对多', value: 'ONE_TO_MANY' },
  { label: '多对一', value: 'MANY_TO_ONE' },
  { label: '多对多', value: 'MANY_TO_MANY' }
];

const CreateRelationModal: React.FC<{
  visible: boolean;
  setVisible: (visible: boolean) => void;
  successCallback: () => void;
  updateRelationOptions: boolean;
  setUpdateRelationOptions: (updateRelationOptions: boolean) => void;
}> = ({ visible, setVisible, successCallback, updateRelationOptions, setUpdateRelationOptions }) => {
  const [form] = Form.useForm<RelationFormValues>();
  const [leftEntityOptions, setLeftEntityOptions] = useState<EntityOption[]>([]);
  const [rightEntityOptions, setRightEntityOptions] = useState<EntityOption[]>([]);
  const [leftFieldOptions, setLeftFieldOptions] = useState<FieldOption[]>([]);
  const [rightFieldOptions, setRightFieldOptions] = useState<FieldOption[]>([]);

  // 初始化实体选项
  useEffect(() => {
    if (updateRelationOptions) {
      // 从localStorage获取实体数据
      const { nodes } = JSON.parse(
        localStorage.getItem('entityFormValues') || JSON.stringify({ nodes: [], edges: [] })
      );
      const entityOptions = nodes?.length
        ? nodes.map((node: EntityNode) => ({
            label: node.title, // 使用title作为label
            value: node.id
          }))
        : [];
      const fieldOptions = nodes?.fields?.length
        ? nodes.fields.map((field: EntityField) => ({
            label: field.name,
            value: field.id
          }))
        : [];
      setLeftFieldOptions(fieldOptions);
      setRightFieldOptions(fieldOptions);

      setLeftEntityOptions(entityOptions);
      setRightEntityOptions(entityOptions);
    }

    setUpdateRelationOptions(false);
  }, [updateRelationOptions]);

  // 当左实体改变时，更新左字段选项
  const handleLeftEntityChange = (value: string) => {
    // 这里应该根据选择的实体获取对应的字段
    const { nodes } = JSON.parse(localStorage.getItem('entityFormValues') || JSON.stringify({ nodes: [] }));
    const entity = nodes.find((node: EntityNode) => node.id === value);
    const fieldOptions = entity?.fields.map((field: EntityField) => ({
      label: field.name,
      value: field.id
    }));
    setLeftFieldOptions(fieldOptions);
    form.setFieldValue('leftField', '');
  };

  // 当右实体改变时，更新右字段选项
  const handleRightEntityChange = (value: string) => {
    // 这里应该根据选择的实体获取对应的字段
    const { nodes } = JSON.parse(localStorage.getItem('entityFormValues') || JSON.stringify({ nodes: [] }));
    const entity = nodes.find((node: EntityNode) => node.id === value);
    const fieldOptions = entity?.fields.map((field: EntityField) => ({
      label: field.name,
      value: field.id
    }));
    setRightFieldOptions(fieldOptions);
    form.setFieldValue('rightField', '');
  };

  // 提交
  const handleFinish = () => {
    form.validate().then((values) => {
      // TODO: 提交关联关系数据
      console.log('关联关系数据:', values);
      const { nodes, edges } = JSON.parse(
        localStorage.getItem('entityFormValues') || JSON.stringify({ nodes: [], edges: [] })
      );
      const newEdges = edges || [];
      const edge: EdgeData = {
        source: { cell: values.leftEntity, port: values.leftField },
        target: { cell: values.rightEntity, port: values.rightField }
        // label: values.relationType,
      };
      newEdges.push(edge);
      localStorage.setItem('entityFormValues', JSON.stringify({ nodes, edges: newEdges }));
      form.resetFields();
      Message.success('关联关系创建成功');
      setVisible(false);
      successCallback();
    });
  };

  return (
    <Modal
      className={styles['create-relation-modal']}
      title="添加关联关系"
      visible={visible}
      onOk={handleFinish}
      onCancel={() => setVisible(false)}
      okText="确认"
      cancelText="取消"
    >
      <Form form={form} layout="vertical" onSubmit={handleFinish} className={styles['relation-form']}>
        <div className={styles['relation-form-container']}>
          {/* 左关联表 */}
          <Form.Item
            label="左关联表"
            field="leftEntity"
            required
            rules={[{ required: true, message: '请选择左关联表' }]}
          >
            <Select placeholder="请选择业务实体" options={leftEntityOptions} onChange={handleLeftEntityChange} />
          </Form.Item>

          <Form.Item label="请选择字段" field="leftField" rules={[{ required: true, message: '请选择字段' }]}>
            <Select placeholder="请选择字段" options={leftFieldOptions} />
          </Form.Item>

          {/* 关联关系类型 */}
          <Form.Item
            label="关联关系"
            field="relationType"
            required
            rules={[{ required: true, message: '请选择关联关系' }]}
          >
            <Select placeholder="请选择关联关系" options={relationTypes} />
          </Form.Item>

          {/* 右关联表 */}
          <Form.Item
            label="右关联表"
            field="rightEntity"
            required
            rules={[{ required: true, message: '请选择右关联表' }]}
          >
            <Select placeholder="请选择业务实体" options={rightEntityOptions} onChange={handleRightEntityChange} />
          </Form.Item>

          <Form.Item label="请选择字段" field="rightField" rules={[{ required: true, message: '请选择字段' }]}>
            <Select placeholder="请选择字段" options={rightFieldOptions} />
          </Form.Item>
        </div>
      </Form>
    </Modal>
  );
};

export default CreateRelationModal;
