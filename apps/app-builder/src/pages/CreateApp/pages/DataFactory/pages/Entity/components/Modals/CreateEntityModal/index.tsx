import { DS_RESOURCE_TYPE, useGraphEntitytore } from '@onebase/ui-kit';
import { useAppStore } from '@/store/store_app';
import { useResourceStore } from '@/store/store_resource';
import { Form, Input, Message, Modal, Radio, Select } from '@arco-design/web-react';
import { createEntity } from '@onebase/app';
import React, { useState } from 'react';
import styles from '../modal.module.less';
import { createEntityRules } from '@/pages/CreateApp/pages/DataFactory/utils/rules';
import { getNewNodePosition } from '../../ERchart/utils/nodePositionCalculator';

interface EntityFormValues {
  source: string;
  code: string;
  tableName: string;
  displayName: string;
  description: string;
  dsResource: string;
  dsTable: string;
}

// 实体类型(1:自建表，2:复用已有表)
const entitySources = [
  { label: '新建数据资产', value: '1' },
  { label: '引用自有数据源中已有资产', value: '2' }
];

const dsOptions: { label: string; value: string }[] = [];
const dsTables: { label: string; value: string }[] = [];

const CreateEntityModal: React.FC<{
  visible: boolean;
  setVisible: (visible: boolean) => void;
  successCallback: () => void;
}> = ({ visible, setVisible, successCallback }) => {
  const { curDataSourceId } = useResourceStore();
  const { curAppId } = useAppStore();
  const { newNodes, setNewNodes } = useGraphEntitytore();
  const [form] = Form.useForm<EntityFormValues>();
  const [dsResource, setDsResource] = useState<string>(DS_RESOURCE_TYPE.EXTERNAL); // 数据源来源：内部数据源、外部数据源、外部数据源中引用自有数据源已有资产

  // 提交
  const handleFinish = () => {
    // 检查数据源ID是否存在
    if (!curDataSourceId) {
      Message.error('数据源ID未获取到，请刷新页面重试');
      return;
    }

    form.validate().then(async (values) => {
      // 获取新节点位置
      const { x, y } = getNewNodePosition();

      const params = {
        displayName: values.displayName,
        tableName: values.tableName,
        code: values?.code,
        entityType: 1, // 实体类型 1:自建表，2:复用已有表
        description: values.description,
        datasourceId: curDataSourceId,
        appId: curAppId,
        displayConfig: JSON.stringify({
          x,
          y
        })
      };

      const res = await createEntity(params);
      console.log('createEntity', res);

      if (res) {
        setNewNodes([...newNodes, res.id]);
        form.resetFields();
        Message.success('保存成功');
        successCallback();
        setVisible(false);
      }
    });
  };

  const handleSourceChange = (value: string) => {
    setDsResource(value);
    form.setFieldValue('source', value);
    form.setFieldValue('dsResource', '');
    form.setFieldValue('dsTable', '');
  };

  const handleCancel = () => {
    form.resetFields();
    setVisible(false);
  };

  return (
    <Modal
      className={styles.createEntityModal}
      title="创建数据资产"
      visible={visible}
      onOk={handleFinish}
      onCancel={handleCancel}
      okText="创建"
      cancelText="取消"
    >
      <Form form={form} layout="vertical" onSubmit={handleFinish}>
        {dsResource !== DS_RESOURCE_TYPE.INTERNAL && (
          <Form.Item
            label="数据资产来源于"
            field="source"
            rules={[{ required: true, message: '请选择数据资产来源' }]}
            initialValue={entitySources[0].value}
          >
            <Radio.Group onChange={handleSourceChange}>
              {entitySources.map((source) => (
                <Radio value={source.value} key={source.value}>
                  {source.label}
                </Radio>
              ))}
            </Radio.Group>
          </Form.Item>
        )}

        {form.getFieldValue('source') === entitySources[1].value && (
          <>
            <Form.Item label="外部数据源" field="dsResource" rules={[{ required: true, message: '请选择外部数据源' }]}>
              <Select placeholder="请选择自有数据源" options={dsOptions} />
            </Form.Item>

            <Form.Item label="数据表" field="dsTable" rules={[{ required: true, message: '请选择数据表' }]}>
              <Select placeholder="请选择自有数据源中的数据表" options={dsTables} />
            </Form.Item>
          </>
        )}

        <Form.Item label="数据资产名称" field="tableName" rules={[...createEntityRules.tableName]}>
          <Input maxLength={40} placeholder="由小写字母、数字、下划线组成，须以字母开头，不超过40个字符" />
        </Form.Item>

        <Form.Item label="数据资产展示名称" field="displayName" rules={[...createEntityRules.displayName]}>
          <Input maxLength={50} placeholder="请输入数据资产展示名称，不超过50个字符" />
        </Form.Item>

        <Form.Item label="数据资产描述" field="description" rules={[...createEntityRules.description]}>
          <Input.TextArea placeholder="请输入描述（选填）" rows={4} maxLength={500} showWordLimit />
        </Form.Item>
      </Form>
    </Modal>
  );
};

export default CreateEntityModal;
