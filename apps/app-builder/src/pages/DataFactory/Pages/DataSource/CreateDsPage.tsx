import React, { useState, useMemo } from 'react';
import { Form, Input, Select, Button, Message } from '@arco-design/web-react';
import styles from './index.module.less';

const Option = Select.Option;

interface DataSourceFormValues {
  type: string;
  name: string;
  host: string;
  port: string;
  username: string;
  password: string;
  dbName: string;
}

const dbTypes = [
  { label: 'PostgreSQL', value: 'PostgreSQL', urlPrefix: '' },
];

const CreateDataSource: React.FC<{ handlePageType: (tab: string) => void }> = ({ handlePageType }) => {
  const [form] = Form.useForm<DataSourceFormValues>();
  const [testing, setTesting] = useState(false);

  // 拼接 URL
  const url = useMemo(() => {
    const values = form.getFieldsValue();
    const typeObj = dbTypes.find(t => t.value === values.type) || dbTypes[0];
    if (!values.host || !values.port || !values.dbName) return '';
    return `${typeObj.urlPrefix}${values.host}:${values.port}/${values.dbName}`;
  }, [form]);

  // 监听表单变化，强制刷新 URL
  const handleFormChange = () => {
    form.setFieldsValue({});
  };

  // 连接测试
  const handleTest = async () => {
    try {
      await form.validate();
      setTesting(true);
      // TODO: 调用后端接口测试连接
      setTimeout(() => {
        setTesting(false);
        Message.success('连接成功');
      }, 1000);
    } catch {
      // 校验失败
    }
  };

  // 提交
  const handleFinish = () => {
    // TODO: 提交表单数据
    Message.success('保存成功');
    handlePageType('check-ds');
  };

  return (
    <Form
      form={form}
      layout="vertical"
      onValuesChange={handleFormChange}
      onSubmit={handleFinish}
      className={styles.dataSourceForm}
    >
      <Form.Item
        label="数据源类型"
        field="type"
        rules={[{ required: true, message: '请选择数据源类型' }]}
        initialValue={dbTypes[0].value}
      >
        <Select>
          {dbTypes.map(t => (
            <Option value={t.value} key={t.value}>{t.label}</Option>
          ))}
        </Select>
      </Form.Item>
      <Form.Item
        label="名称"
        field="name"
        rules={[{ required: true, message: '请输入名称' }]}
      >
        <Input maxLength={200} placeholder="自定义数据源" />
      </Form.Item>
      <Form.Item label="数据库地址" required style={{ marginBottom: 0 }}>
        <Form.Item
          field="host"
          rules={[{ required: true, message: '请输入数据库地址' }]}
          className={styles.dataSourceFormHost}
        >
          <Input maxLength={200} placeholder="数据库地址" />
        </Form.Item>
        <Form.Item
          field="port"
          rules={[{ required: true, message: '请输入端口' }]}
          className={styles.dataSourceFormPort}
          initialValue="3306"
        >
          <Input maxLength={10} placeholder="端口" />
        </Form.Item>
      </Form.Item>
      <Form.Item label="账号" field="username" rules={[{ required: true, message: '请输入账号' }]}> 
        <Input maxLength={30} placeholder="账号" />
      </Form.Item>
      <Form.Item label="密码" field="password" rules={[{ required: true, message: '请输入密码' }]}> 
        <Input.Password maxLength={30} placeholder="密码" />
      </Form.Item>
      <Form.Item label="数据库名称" field="dbName" rules={[{ required: true, message: '请输入数据库名称' }]}> 
        <Input maxLength={30} placeholder="数据库名称" />
      </Form.Item>
      <Form.Item label="URL">
        <Input value={url} readOnly />
      </Form.Item>
      <Form.Item>
        <Button type="primary" onClick={handleTest} loading={testing} style={{ marginRight: 16 }}>
          连接测试
        </Button>
        <Button type="primary" htmlType="submit">
          确认
        </Button>
      </Form.Item>
    </Form>
  );
};

export default CreateDataSource;
