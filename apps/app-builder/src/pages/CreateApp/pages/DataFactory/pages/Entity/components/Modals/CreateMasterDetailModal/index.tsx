import { resouceId } from '@/pages/CreateApp/pages/DataFactory/utils/const';
import type { EntityNode } from '@/pages/CreateApp/pages/DataFactory/utils/interface';
import { Form, Grid, Input, Message, Modal, Radio, Select } from '@arco-design/web-react';
import { getEntityList, getEntityFields, createMasterChild } from '@onebase/app';
import React, { useEffect, useState } from 'react';
import styles from '../modal.module.less';

interface MasterDetailFormValues {
  parentEntityId: string;
  parentFieldId: string;
  childTableType: 'existing' | 'new';
  childEntityId?: string;
  childFieldId?: string;
  childTableCode?: string;
  childTableName?: string;
  childTableDescription?: string;
}

interface EntityOption {
  label: string;
  value: string;
}

interface FieldOption {
  label: string;
  value: string;
}

const CreateMasterDetailModal: React.FC<{
  visible: boolean;
  setVisible: (visible: boolean) => void;
  entity: EntityNode;
  successCallback: () => void;
}> = ({ visible, setVisible, successCallback }) => {
  const [form] = Form.useForm<MasterDetailFormValues>();
  const [loading, setLoading] = useState(false);
  const [entityOptions, setEntityOptions] = useState<EntityOption[]>([]);
  const [fieldOptions, setFieldOptions] = useState<FieldOption[]>([]);
  const [childFieldOptions, setChildFieldOptions] = useState<FieldOption[]>([]);

  // 初始化实体选项
  useEffect(() => {
    if (visible) {
      loadEntities();
    }
  }, [visible]);

  const loadEntities = async () => {
    try {
      const res = await getEntityList(resouceId);
      if (res.length > 0) {
        const entityOptions = res.map((entityItem: { displayName: string; id: string }) => ({
          label: entityItem.displayName,
          value: entityItem.id
        }));
        setEntityOptions(entityOptions);
      }
    } catch (error) {
      console.error('加载实体列表失败:', error);
      Message.error('加载实体列表失败');
    }
  };

  // 当主表改变时，更新字段选项
  const handleMasterEntityChange = async (value: string) => {
    try {
      const res = await getEntityFields({ entityId: value });
      if (res.length > 0) {
        const fieldOptions = res.map((field: { fieldName: string; id: string }) => ({
          label: field.fieldName,
          value: field.id
        }));
        setFieldOptions(fieldOptions);
      }
      // 清空字段选择
      form.setFieldValue('parentFieldId', '');
    } catch (error) {
      console.error('加载字段列表失败:', error);
      Message.error('加载字段列表失败');
    }
  };

  // 当子表改变时，更新子表字段选项
  const handleChildEntityChange = async (value: string) => {
    try {
      const res = await getEntityFields({ entityId: value });
      if (res.length > 0) {
        const fieldOptions = res.map((field: { fieldName: string; id: string }) => ({
          label: field.fieldName,
          value: field.id
        }));
        setChildFieldOptions(fieldOptions);
      }
      // 清空子表字段选择
      form.setFieldValue('childFieldId', '');
    } catch (error) {
      console.error('加载子表字段列表失败:', error);
      Message.error('加载子表字段列表失败');
    }
  };

  // 提交
  const handleFinish = () => {
    form.validate().then(async (values) => {
      try {
        setLoading(true);
        console.log('主子关系数据:', values);

        // 根据子表类型准备数据
        const requestData = {
          parentEntityId: values.parentEntityId,
          parentFieldId: values.parentFieldId,
          appId: resouceId,
          datasourceId: resouceId
        };

        if (values.childTableType === 'existing') {
          // 选择已有子表
          Object.assign(requestData, {
            childEntityId: values.childEntityId,
            childFieldId: values.childFieldId,
            childTableCode: '',
            childTableName: '',
            childTableDescription: ''
          });
        } else {
          // 新建子表
          Object.assign(requestData, {
            childEntityId: '',
            childFieldId: '',
            childTableCode: values.childTableCode,
            childTableName: values.childTableName,
            childTableDescription: values.childTableDescription || ''
          });
        }

        const res = await createMasterChild(requestData);
        console.log('创建主子关系结果:', res);
        if (res) {
          Message.success('创建成功');
          form.resetFields();
          setVisible(false);
          successCallback();
        }
      } catch (error) {
        console.error('创建主子关系失败:', error);
      } finally {
        setLoading(false);
      }
    });
  };

  return (
    <Modal
      className={styles['create-master-detail-modal']}
      title="添加主子关系"
      visible={visible}
      onOk={handleFinish}
      onCancel={() => setVisible(false)}
      okText="创建"
      cancelText="取消"
      confirmLoading={loading}
    >
      <Form form={form} layout="vertical" className={styles['master-detail-form']}>
        {/* 主表选择 */}
        <Form.Item label="主表" required>
          <Grid.Row gutter={16}>
            <Grid.Col span={12}>
              <Form.Item field="parentEntityId" rules={[{ required: true, message: '请选择主表' }]}>
                <Select
                  placeholder="请选择业务实体"
                  options={entityOptions}
                  onChange={handleMasterEntityChange}
                  style={{ width: '100%' }}
                />
              </Form.Item>
            </Grid.Col>
            <Grid.Col span={12}>
              <Form.Item field="parentFieldId" rules={[{ required: true, message: '请选择主表字段' }]}>
                <Select placeholder="请选择字段" options={fieldOptions} style={{ width: '100%' }} />
              </Form.Item>
            </Grid.Col>
          </Grid.Row>
        </Form.Item>

        {/* 子表类型选择 */}
        <Form.Item label="子表选择" field="childTableType" initialValue="existing">
          <Radio.Group>
            <Radio value="existing">选择已有子表</Radio>
            <Radio value="new">新建子表</Radio>
          </Radio.Group>
        </Form.Item>

        {/* 子表配置 */}
        <Form.Item noStyle shouldUpdate>
          {(values) => {
            if (values.childTableType === 'existing') {
              return (
                <>
                  <Form.Item label="子表" field="childEntityId" rules={[{ required: true, message: '请选择子表' }]}>
                    <Select
                      placeholder="请选择子表"
                      options={entityOptions}
                      style={{ width: '100%' }}
                      onChange={handleChildEntityChange}
                    />
                  </Form.Item>
                </>
              );
            } else {
              return (
                <>
                  <Form.Item
                    label="子表编码"
                    field="childTableCode"
                    rules={[
                      { required: true, message: '请输入子表编码' },
                      { max: 40, message: '子表编码不能超过40个字符' },
                      {
                        validator: (value, cb) => {
                          if (value && !/^[a-zA-Z][a-zA-Z0-9_]*$/.test(value)) {
                            cb('子表编码由字母、数字、下划线组合，须以字母开头');
                          }
                          cb();
                        }
                      }
                    ]}
                  >
                    <Input
                      maxLength={40}
                      placeholder="请输入业务实体编码,由字母、数字、下划线组合,须以字母开头,不超过40个字符"
                    />
                  </Form.Item>

                  <Form.Item
                    label="子表名称"
                    field="childTableName"
                    rules={[
                      { required: true, message: '请输入子表名称' },
                      { max: 50, message: '子表名称不能超过50个字符' }
                    ]}
                  >
                    <Input maxLength={50} placeholder="请输入实体名称,不超过50个字符" />
                  </Form.Item>

                  <Form.Item label="子表描述" field="childTableDescription">
                    <Input.TextArea placeholder="请输入描述(选填)" rows={4} maxLength={500} showWordLimit />
                  </Form.Item>
                </>
              );
            }
          }}
        </Form.Item>
      </Form>
    </Modal>
  );
};

export default CreateMasterDetailModal;
