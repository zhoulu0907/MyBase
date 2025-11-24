import { useAppStore } from '@/store/store_app';
import { useResourceStore } from '@/store/store_resource';
import { Form, Input, Message, Modal, Radio, Select } from '@arco-design/web-react';
import { createMasterChild, getEntityList } from '@onebase/app';
import React, { useEffect, useState } from 'react';
import styles from '../modal.module.less';

interface MasterDetailFormValues {
  parentEntityId: string;
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

const CreateMasterDetailModal: React.FC<{
  visible: boolean;
  setVisible: (visible: boolean) => void;
  entityId: string;
  successCallback: (type?: string) => void;
}> = ({ visible, setVisible, successCallback, entityId }) => {
  const { curAppId } = useAppStore();
  const { curDataSourceId } = useResourceStore();
  const [form] = Form.useForm<MasterDetailFormValues>();
  const [loading, setLoading] = useState(false);
  const [entityOptions, setEntityOptions] = useState<EntityOption[]>([]);
  const [childEntityOptions, setChildEntityOptions] = useState<EntityOption[]>([]);

  // 初始化资产选项
  useEffect(() => {
    if (visible && curDataSourceId) {
      loadEntities();
    }
  }, [visible]);

  const loadEntities = async () => {
    try {
      const res = await getEntityList(curDataSourceId);
      if (res.length > 0) {
        const entityOptions = res.map((entityItem: { displayName: string; id: string }) => ({
          label: entityItem.displayName,
          value: entityItem.id
        }));
        setEntityOptions(entityOptions);
        setChildEntityOptions(entityOptions.filter((item: EntityOption) => item.value !== entityId));
        form.setFieldValue('parentEntityId', entityId);
      }
    } catch (error) {
      console.error('加载资产列表失败:', error);
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
          appId: curAppId,
          datasourceId: curDataSourceId
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
          if (values.childTableCode) {
            successCallback('new_master_child');
          } else {
            successCallback();
          }
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
      className={styles.createMasterDetailModal}
      title="添加主子关系"
      visible={visible}
      onOk={handleFinish}
      onCancel={() => setVisible(false)}
      okText="创建"
      cancelText="取消"
      confirmLoading={loading}
    >
      <Form form={form} layout="vertical">
        {/* 主表选择 */}
        <Form.Item label="主表" required>
          <Form.Item field="parentEntityId" rules={[{ required: true, message: '请选择主表' }]}>
            <Select placeholder="请选择业务资产" options={entityOptions} disabled />
          </Form.Item>
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
                    <Select placeholder="请选择子表" options={childEntityOptions} />
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
                      placeholder="请输入业务资产编码,由字母、数字、下划线组合,须以字母开头,不超过40个字符"
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
                    <Input maxLength={50} placeholder="请输入资产名称,不超过50个字符" />
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
