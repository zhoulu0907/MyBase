import { useAppStore } from '@/store/store_app';
import { useResourceStore } from '@/store/store_resource';
import { Form, Message, Modal, Select, Grid } from '@arco-design/web-react';
import { createRelation, getEntityFields, getEntityList } from '@onebase/app';
import React, { useEffect, useState } from 'react';
import { RELATIONSHIP_OPTIONS } from '@/pages/CreateApp/pages/DataFactory/utils/types';
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

const CreateRelationModal: React.FC<{
  visible: boolean;
  setVisible: (visible: boolean) => void;
  successCallback: () => void;
  updateRelationOptions: boolean;
  setUpdateRelationOptions: (updateRelationOptions: boolean) => void;
  entityId: string;
}> = ({ visible, setVisible, successCallback, entityId }) => {
  const { curAppId } = useAppStore();
  const { curDataSourceId } = useResourceStore();
  const [form] = Form.useForm<RelationFormValues>();
  const [leftEntityOptions, setLeftEntityOptions] = useState<EntityOption[]>([]);
  const [rightEntityOptions, setRightEntityOptions] = useState<EntityOption[]>([]);
  const [leftFieldOptions, setLeftFieldOptions] = useState<FieldOption[]>([]);
  const [rightFieldOptions, setRightFieldOptions] = useState<FieldOption[]>([]);

  // 初始化实体选项
  useEffect(() => {
    if (visible && curDataSourceId) {
      loadEntities();
    }
  }, [visible]);

  const loadEntities = async () => {
    const res = await getEntityList(curDataSourceId);
    if (res.length > 0) {
      const entityOptions = res.map((entity: any) => ({
        label: entity.displayName,
        value: entity.id
      }));
      setLeftEntityOptions(entityOptions);
      setRightEntityOptions(entityOptions.filter((item: EntityOption) => item.value !== entityId));
      form.setFieldValue('sourceEntityId', entityId);
      handleEntityChange(entityId, 'left');
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
        label: field.displayName,
        value: field.id
      }));
      if (type === 'left') {
        form.setFieldValue('sourceFieldId', '');
        setLeftFieldOptions(fieldOptions);
      } else {
        form.setFieldValue('targetFieldId', '');
        setRightFieldOptions(fieldOptions);
      }
    }
  };

  // 提交
  const handleFinish = () => {
    form.validate().then(async (values) => {
      console.log('关联关系数据:', values);

      const params = {
        ...values,
        relationName: values.relationshipType,
        applicationId: curAppId
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
      className={styles.createRelationModal}
      title="添加关联关系"
      visible={visible}
      onOk={handleFinish}
      onCancel={() => setVisible(false)}
      okText="确认"
      cancelText="取消"
    >
      <Form form={form} onSubmit={handleFinish} layout="vertical" className={styles.relationForm}>
        <div className={styles.relationFormContainer}>
          {/* 本表 */}
          <Grid.Row gutter={16}>
            <Grid.Col span={12}>
              <Form.Item
                label="本表"
                field="sourceEntityId"
                required
                rules={[{ required: true, message: '请选择本表' }]}
              >
                <Select
                  placeholder="请选择数据资产"
                  options={leftEntityOptions}
                  onChange={(values) => handleEntityChange(values, 'left')}
                  disabled
                />
              </Form.Item>
            </Grid.Col>
            <Grid.Col span={12} className={styles.formItemBottom}>
              <Form.Item field="sourceFieldId" rules={[{ required: true, message: '请选择字段' }]}>
                <Select placeholder="请选择字段" options={leftFieldOptions} />
              </Form.Item>
            </Grid.Col>
          </Grid.Row>

          {/* 关联关系类型 */}
          <Form.Item
            label="关联关系"
            field="relationshipType"
            required
            rules={[{ required: true, message: '请选择关联关系' }]}
          >
            <Select placeholder="请选择关联关系" options={RELATIONSHIP_OPTIONS} />
          </Form.Item>

          {/* 关联表 */}
          <Grid.Row gutter={16}>
            <Grid.Col span={12}>
              <Form.Item
                label="关联表"
                field="targetEntityId"
                required
                rules={[{ required: true, message: '请选择关联表' }]}
              >
                <Select
                  placeholder="请选择数据资产"
                  options={rightEntityOptions}
                  onChange={(values) => handleEntityChange(values, 'right')}
                />
              </Form.Item>
            </Grid.Col>
            <Grid.Col span={12} className={styles.formItemBottom}>
              <Form.Item field="targetFieldId" rules={[{ required: true, message: '请选择字段' }]}>
                <Select placeholder="请选择字段" options={rightFieldOptions} />
              </Form.Item>
            </Grid.Col>
          </Grid.Row>
        </div>
      </Form>
    </Modal>
  );
};

export default CreateRelationModal;
