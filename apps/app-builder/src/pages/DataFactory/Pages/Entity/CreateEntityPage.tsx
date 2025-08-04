import React, { useState } from 'react';
import { Form, Input, Select, Message, Radio, Modal } from '@arco-design/web-react';
import styles from './index.module.less';
import { DS_RESOURCE_TYPE } from '../../utils/constans';

interface EntityFormValues {
  source: string;
  code: string;
  name: string;
  description: string;
  dsResource: string;
  dsTable: string;
}

const entitySources = [
  { label: '新建业务实体', value: 'new' },
  { label: '使用自有数据源中的数据表', value: 'existing' },
];

const dsOptions: { label: string, value: string }[] = [];
const dsTables: { label: string, value: string }[] = [];

const CreateEntityPage: React.FC<{ visible: boolean, setVisible: (visible: boolean) => void, handlePageType: (tab: string) => void, setRefreshEntityList: (refresh: boolean) => void }> = ({ visible, setVisible, handlePageType, setRefreshEntityList }) => {
  const [form] = Form.useForm<EntityFormValues>();
  const [dsResource, setDsResource] = useState<string>(DS_RESOURCE_TYPE.EXTERNAL); // 数据源来源：内部数据源、外部数据源、外部数据源中引用自有数据源已有资产
  // 提交
  const handleFinish = () => {
    // TODO: 提交表单数据
    form.validate().then(values => {
      const { nodes } = JSON.parse(localStorage.getItem('entityFormValues') || JSON.stringify({nodes: []}));

      nodes.push({
        ...values,
        id: values.code,
        title: values.name,
        x: nodes.length * 300,
        y: 0,
        fields: [
          {id: 'ID', name: 'ID', type: '自增ID', isSystem: true},
        ],
      }); 
      localStorage.setItem('entityFormValues', JSON.stringify({ nodes }));
      console.log(values);
      Message.success('保存成功');
      setVisible(false);
      setRefreshEntityList(true);
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
      title='创建业务实体' visible={visible} 
      onOk={handleFinish} 
      onCancel={() => setVisible(false)} 
      okText='创建'
      cancelText='取消'
    >
      <Form
        form={form}
        layout="vertical"
        onSubmit={handleFinish}
        className={styles['entity-form']}
      >
        { dsResource !== DS_RESOURCE_TYPE.INTERNAL && 
          <Form.Item
            label="业务实体来源于"
            field="source"
            rules={[{ required: true, message: '请选择业务实体来源' }]}
            initialValue={entitySources[0].value}
          >
            <Radio.Group onChange={handleSourceChange}>
              {entitySources.map(source => (
                <Radio value={source.value} key={source.value}>{source.label}</Radio>
              ))}
            </Radio.Group>
          </Form.Item>
        }

        {form.getFieldValue('source') === entitySources[1].value && (
          <>
            <Form.Item
              label="外部数据源"
              field="dsResource"
              rules={[{ required: true, message: '请选择外部数据源' }]}
            >
              <Select placeholder="请选择自有数据源，可选已接入的外部数据源" options={dsOptions} />
            </Form.Item>

            <Form.Item
              label="数据表"
              field="dsTable"
              rules={[{ required: true, message: '请选择数据表' }]}
              >
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
          <Input maxLength={40} placeholder="请输入业务实体编码，由字母、数字、下划线组合，须以字母开头，不超过40个字符" />
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
        
        <Form.Item
          label="业务实体描述"
          field="description"
        >
          <Input.TextArea 
            placeholder="请输入描述（选填）"
            rows={4}
            maxLength={500}
            showWordLimit
          />
        </Form.Item>
      
      </Form>
    </Modal>
  );
};

export default CreateEntityPage;
