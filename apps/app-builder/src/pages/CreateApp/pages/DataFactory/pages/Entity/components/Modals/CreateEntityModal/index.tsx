import { DS_RESOURCE_TYPE } from '@/pages/CreateApp/pages/DataFactory/utils/const';
import { useAppStore } from '@/store/store_app';
import { useResourceStore } from '@/store/store_resource';
import { Form, Input, Message, Modal, Radio, Select } from '@arco-design/web-react';
import { createEntity } from '@onebase/app';
import React, { useState } from 'react';
import styles from '../modal.module.less';
interface EntityFormValues {
  source: string;
  code: string;
  name: string;
  description: string;
  dsResource: string;
  dsTable: string;
}

// 实体类型(1:自建表，2:复用已有表)
const entitySources = [
  { label: '新建业务实体', value: 1 },
  { label: '使用自有数据源中的数据表', value: 2 }
];

const dsOptions: { label: string; value: string }[] = [];
const dsTables: { label: string; value: string }[] = [];

const CreateEntityModal: React.FC<{
  visible: boolean;
  setVisible: (visible: boolean) => void;
  successCallback: () => void;
  entityListLength: number;
}> = ({ visible, setVisible, successCallback, entityListLength }) => {
  const { curDataSourceId } = useResourceStore();
  const { curAppId } = useAppStore();
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
      const params = {
        displayName: values.name,
        code: values.code,
        entityType: 1, // 实体类型 1:自建表，2:复用已有表
        description: values.description,
        datasourceId: curDataSourceId,
        appId: curAppId,
        displayConfig: JSON.stringify({
          x: entityListLength * 300,
          y: 0
        })
      };

      const res = await createEntity(params);
      console.log('createEntity', res);

      form.resetFields();
      Message.success('保存成功');
      successCallback();
      setVisible(false);
    });
  };

  const handleSourceChange = (value: string) => {
    setDsResource(value);
    form.setFieldValue('source', value);
    form.setFieldValue('dsResource', '');
    form.setFieldValue('dsTable', '');
  };

  return (
    <Modal
      className={styles['create-entity-modal']}
      title="创建业务实体"
      visible={visible}
      onOk={handleFinish}
      onCancel={() => setVisible(false)}
      okText="创建"
      cancelText="取消"
    >
      <Form form={form} layout="vertical" onSubmit={handleFinish} className={styles['entity-form']}>
        {dsResource !== DS_RESOURCE_TYPE.INTERNAL && (
          <Form.Item
            label="业务实体来源于"
            field="source"
            rules={[{ required: true, message: '请选择业务实体来源' }]}
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

        {form.getFieldValue('source') === entitySources[1].value.toString() && (
          <>
            <Form.Item label="外部数据源" field="dsResource" rules={[{ required: true, message: '请选择外部数据源' }]}>
              <Select placeholder="请选择自有数据源，可选已接入的外部数据源" options={dsOptions} />
            </Form.Item>

            <Form.Item label="数据表" field="dsTable" rules={[{ required: true, message: '请选择数据表' }]}>
              <Select placeholder="请选择自有数据源中的数据表" options={dsTables} />
            </Form.Item>
          </>
        )}

        <Form.Item
          label="业务实体编码"
          field="code"
          rules={[
            { required: true, message: '请输入业务实体编码' },
            { max: 40, message: '业务实体编码不能超过40个字符' }
          ]}
        >
          <Input
            maxLength={40}
            placeholder="请输入业务实体编码，由字母、数字、下划线组合，须以字母开头，不超过40个字符"
          />
        </Form.Item>

        <Form.Item
          label="业务实体名称"
          field="name"
          rules={[
            { required: true, message: '请输入业务实体名称' },
            { max: 50, message: '业务实体名称不能超过50个字符' }
          ]}
        >
          <Input maxLength={50} placeholder="请输入实体名称，不超过50个字符" />
        </Form.Item>

        <Form.Item label="业务实体描述" field="description">
          <Input.TextArea placeholder="请输入描述（选填）" rows={4} maxLength={500} showWordLimit />
        </Form.Item>
      </Form>
    </Modal>
  );
};

export default CreateEntityModal;
