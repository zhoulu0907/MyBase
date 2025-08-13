import { resouceId } from '@/pages/CreateApp/pages/DataFactory/utils/const';
import { Form, Message, Modal, Select } from '@arco-design/web-react';
import { useAppStore } from '@/store';
import { createRelation, getEntityFields, getEntityList } from '@onebase/app';
import React, { useEffect, useState } from 'react';
import styles from '../modal.module.less';

interface RelationFormValues {
  sourceEntityId: string;
  sourceFieldId: string;
  relationshipType: string;
  targetEntityId: string;
  targetFieldId: string;
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
}> = ({ visible, setVisible, successCallback }) => {
  const { curAppId } = useAppStore();
  const [form] = Form.useForm<RelationFormValues>();
  const [leftEntityOptions, setLeftEntityOptions] = useState<EntityOption[]>([]);
  const [rightEntityOptions, setRightEntityOptions] = useState<EntityOption[]>([]);
  const [leftFieldOptions, setLeftFieldOptions] = useState<FieldOption[]>([]);
  const [rightFieldOptions, setRightFieldOptions] = useState<FieldOption[]>([]);

  // 初始化实体选项
  useEffect(() => {
    loadEntities();
  }, []);

  const loadEntities = async () => {
    const res = await getEntityList(resouceId);
    if (res.length > 0) {
      const entityOptions = res.map((entity: any) => ({
        label: entity.displayName,
        value: entity.id
      }));
      setLeftEntityOptions(entityOptions);
      setRightEntityOptions(entityOptions);
    }
  };

  // 当实体改变时，更新字段选项
  const handleEntityChange = async (value: string, type: string) => {
    // 这里应该根据选择的实体获取对应的字段
    // const { nodes } = JSON.parse(localStorage.getItem('entityFormValues') || JSON.stringify({ nodes: [] }));
    // const entity = nodes.find((node: EntityNode) => node.id === value);
    // const fieldOptions = entity?.fields.map((field: EntityField) => ({
    //   label: field.name,
    //   value: field.id
    // }));

    const res = await getEntityFields({
      entityId: value
    });
    console.log('getEntityFields', res);
    if (res.length > 0) {
      const fieldOptions = res.map((field: any) => ({
        label: field.fieldName,
        value: field.id
      }));
      type === 'left' ? setLeftFieldOptions(fieldOptions) : setRightFieldOptions(fieldOptions);
    }
    form.setFieldValue('sourceFieldId', '');
  };

  // 提交
  const handleFinish = () => {
    form.validate().then(async (values) => {
      // TODO: 提交关联关系数据
      console.log('关联关系数据:', values);
      // const { nodes, edges } = JSON.parse(
      //   localStorage.getItem('entityFormValues') || JSON.stringify({ nodes: [], edges: [] })
      // );
      // const newEdges = edges || [];
      // const edge: EdgeData = {
      //   source: { cell: values.sourceEntityId, port: values.sourceFieldId },
      //   target: { cell: values.targetEntityId, port: values.targetFieldId }
      //   // label: values.relationshipType,
      // };
      // newEdges.push(edge);
      // localStorage.setItem('entityFormValues', JSON.stringify({ nodes, edges: newEdges }));

      const params = {
        ...values,
        relationName: '1',
        appId: curAppId
      };

      const res = await createRelation(params);
      console.log('createRelation', res);

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
            field="sourceEntityId"
            required
            rules={[{ required: true, message: '请选择左关联表' }]}
          >
            <Select
              placeholder="请选择业务实体"
              options={leftEntityOptions}
              onChange={(values) => handleEntityChange(values, 'left')}
            />
          </Form.Item>

          <Form.Item label="请选择字段" field="sourceFieldId" rules={[{ required: true, message: '请选择字段' }]}>
            <Select placeholder="请选择字段" options={leftFieldOptions} />
          </Form.Item>

          {/* 关联关系类型 */}
          <Form.Item
            label="关联关系"
            field="relationshipType"
            required
            rules={[{ required: true, message: '请选择关联关系' }]}
          >
            <Select placeholder="请选择关联关系" options={relationTypes} />
          </Form.Item>

          {/* 右关联表 */}
          <Form.Item
            label="右关联表"
            field="targetEntityId"
            required
            rules={[{ required: true, message: '请选择右关联表' }]}
          >
            <Select
              placeholder="请选择业务实体"
              options={rightEntityOptions}
              onChange={(values) => handleEntityChange(values, 'right')}
            />
          </Form.Item>

          <Form.Item label="请选择字段" field="targetFieldId" rules={[{ required: true, message: '请选择字段' }]}>
            <Select placeholder="请选择字段" options={rightFieldOptions} />
          </Form.Item>
        </div>
      </Form>
    </Modal>
  );
};

export default CreateRelationModal;
