import React, { useState } from 'react';
import { Form, Input, Button, Message, Radio } from '@arco-design/web-react';
import styles from './index.module.less';
import { DS_RESOURCE_TYPE, DS_RESOURCE_TYPE_LABEL } from '../../utils/constans';

interface EntityFormValues {
  source: string;
  code: string;
  name: string;
  description: string;
}

const entitySources = [
  { label: '新建业务实体', value: 'new' },
  { label: '使用自有数据源中的数据表', value: 'existing' },
];

const CreateEntityPage: React.FC<{ handlePageType: (tab: string) => void }> = ({ handlePageType }) => {
  const [form] = Form.useForm<EntityFormValues>();
  const [dsResource, setDsResource] = useState<string>(DS_RESOURCE_TYPE.INTERNAL); // 数据源来源：内部数据源、外部数据源、外部数据源中引用自有数据源已有资产

  // 提交
  const handleFinish = () => {
    // TODO: 提交表单数据
    Message.success('保存成功');
    handlePageType('check-entity');
  };

  return (
    <div className={styles['create-entity-page']}>
      <h2 className={styles['page-title']}>创建业务实体</h2>
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
            <Radio.Group>
              {entitySources.map(source => (
                <Radio value={source.value} key={source.value}>{source.label}</Radio>
              ))}
            </Radio.Group>
          </Form.Item>
        }
        
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
        
        <Form.Item className={styles['form-item-operation']}>
          <Button type="secondary" onClick={() => handlePageType('check-entity')}>
            取消
          </Button>
          <Button type="primary" onClick={() => handleFinish()}>
            创建
          </Button>
        </Form.Item>
      </Form>
    </div>
  );
};

export default CreateEntityPage;
