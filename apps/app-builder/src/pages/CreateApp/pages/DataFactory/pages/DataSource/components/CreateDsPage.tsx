import { useAppStore } from '@/store/store_app';
import { Button, Form, Grid, Input, Message, Select } from '@arco-design/web-react';
import {
  createDatasource,
  testDatasourceConnection,
  type DatasourceSaveReqVO,
  type DatasourceTestConnectionReqVO
} from '@onebase/app';
import React, { useEffect, useState } from 'react';
import styles from '../index.module.less';

const Option = Select.Option;

interface DataSourceFormValues {
  datasourceType: string;
  datasourceName: string;
  host: string;
  port: string;
  username: string;
  password: string;
  code: string;
  url: string;
  database: string;
}

const dbTypes = [{ label: 'PostgreSQL', value: 'PostgreSQL', urlPrefix: 'jdbc:postgresql://' }];

const CreateDataSource: React.FC<{ handlePageType: (tab: string) => void }> = ({ handlePageType }) => {
  const { curAppId } = useAppStore();
  const [form] = Form.useForm<DataSourceFormValues>();
  const [testing, setTesting] = useState(false);
  const [submitting, setSubmitting] = useState(false);
  const isInternal = false;
  const [formValues, setFormValues] = useState<Partial<DataSourceFormValues>>({});

  // 拼接 URL
  // const url = useMemo(() => {
  //   console.log('url', formValues);
  //   const values = formValues;
  //   const typeObj = dbTypes.find(t => t.value === values.datasourceType) || dbTypes[0];
  //   if (!values.host || !values.port || !values.database) return '';
  //   console.log('url 111', `${typeObj.urlPrefix}${values.host}:${values.port}/${values.database}`);
  //   return `${typeObj.urlPrefix}${values.host}:${values.port}/${values.database}`;
  // }, [formValues.datasourceType, formValues.host, formValues.port, formValues.database]);

  // 监听表单变化
  const handleFormChange = (changedValues: Partial<DataSourceFormValues>, allValues: Partial<DataSourceFormValues>) => {
    const relevantFields = ['host', 'port', 'database', 'datasourceType'];
    const hasRelevantChanges = Object.keys(changedValues).some((key) => relevantFields.includes(key));

    if (hasRelevantChanges) {
      setFormValues(allValues);
    }
  };

  useEffect(() => {
    // 当 formValues 变化时，更新 URL 字段
    const typeObj = dbTypes.find((t) => t.value === formValues.datasourceType) || dbTypes[0];
    if (formValues.host && formValues.port && formValues.database) {
      const url = `${typeObj.urlPrefix}${formValues.host}:${formValues.port}/${formValues.database}`;
      if (url) {
        form.setFieldsValue({ url });
      }
    }
  }, [formValues]);

  const rules = {
    code: [{ required: true, message: '请输入数据源编码' }],
    datasourceName: [{ required: true, message: '请输入数据源名称' }],
    datasourceType: [{ required: true, message: '请选择数据库类型' }],
    host: [{ required: true, message: '请输入数据库地址' }],
    port: [{ required: true, message: '请输入端口' }],
    username: [{ required: true, message: '请输入账号' }],
    password: [{ required: true, message: '请输入密码' }],
    database: [{ required: true, message: '请输入数据库名称' }]
  };

  // 连接测试
  const handleTest = async () => {
    try {
      const values = await form.validate();
      setTesting(true);

      // 构建测试连接参数
      const testParams: DatasourceTestConnectionReqVO = {
        datasourceType: values.datasourceType,
        config: {
          host: values.host,
          port: parseInt(values.port),
          database: values.database,
          username: values.username,
          password: values.password,
          url: values.url || `jdbc:mysql://${values.host}:${values.port}/${values.database}`
        }
      };

      const res = await testDatasourceConnection(testParams);
      console.log('handleTest res', res);
      if (res?.success) {
        Message.success(`连接成功，耗时 ${res.duration}ms`);
      } else {
        Message.error(`连接失败：${res.message}`);
      }
    } catch (error) {
      if (error instanceof Error) {
        Message.error(error.message);
      } else {
        Message.error('连接测试失败，请检查表单数据');
      }
    } finally {
      setTesting(false);
    }
  };

  // 提交
  const handleFinish = async () => {
    try {
      const values = await form.validate();
      setSubmitting(true);

      // 构建创建数据源参数
      const createParams: DatasourceSaveReqVO = {
        datasourceName: values.datasourceName,
        code: values.code,
        datasourceType: values.datasourceType,
        config: {
          host: values.host,
          port: parseInt(values.port),
          database: values.database,
          username: values.username,
          password: values.password,
          url: values.url || `jdbc:mysql://${values.host}:${values.port}/${values.database}`
        },
        description: `${values.datasourceType} 数据源`,
        applicationId: curAppId
      };

      const res = await createDatasource(createParams);

      console.log('createDatasource res', res);

      if (res) {
        Message.success('数据源创建成功');
        handlePageType('check-ds');
      } else {
        Message.error(res.msg || '创建失败');
      }
    } catch (error) {
      if (error instanceof Error) {
        Message.error(error.message);
      } else {
        Message.error('创建失败，请检查表单数据');
      }
    } finally {
      form.resetFields();
      setSubmitting(false);
    }
  };

  const handleCancel = () => {
    handlePageType('check-ds');
  };

  return (
    <>
      {!isInternal && (
        <Form form={form} layout="vertical" onValuesChange={handleFormChange} className={styles.dataSourceForm}>
          <Form.Item label="数据源编码" field="code" rules={rules.code}>
            <Input
              maxLength={200}
              placeholder="请输入数据源编码，由字母、数字、下划线组合，须以字母开头，不超过40个字符"
            />
          </Form.Item>
          <Form.Item label="数据源名称" field="datasourceName" rules={rules.datasourceName}>
            <Input maxLength={200} placeholder="请输入数据源名称，不超过200个字符" />
          </Form.Item>
          <Form.Item
            label="数据库类型"
            field="datasourceType"
            rules={rules.datasourceType}
            initialValue={dbTypes[0].value}
          >
            <Select>
              {dbTypes.map((t) => (
                <Option value={t.value} key={t.value}>
                  {t.label}
                </Option>
              ))}
            </Select>
          </Form.Item>
          <Grid.Row gutter={24}>
            <Grid.Col span={12}>
              <Form.Item label="数据库地址" field="host" required rules={rules.host}>
                <Input maxLength={200} placeholder="数据库地址" />
              </Form.Item>
            </Grid.Col>
            <Grid.Col span={12}>
              <Form.Item label="端口" field="port" rules={rules.port} initialValue="3306">
                <Input maxLength={10} placeholder="端口" />
              </Form.Item>
            </Grid.Col>
          </Grid.Row>
          <Grid.Row gutter={24}>
            <Grid.Col span={12}>
              <Form.Item label="账号" field="username" rules={rules.username}>
                <Input maxLength={30} placeholder="账号" />
              </Form.Item>
            </Grid.Col>
            <Grid.Col span={12}>
              <Form.Item label="密码" field="password" rules={rules.password}>
                <Input.Password maxLength={30} placeholder="密码" />
              </Form.Item>
            </Grid.Col>
          </Grid.Row>
          <Form.Item label="数据库名称" field="database" rules={[{ required: true, message: '请输入数据库名称' }]}>
            <Input maxLength={30} placeholder="数据库名称" />
          </Form.Item>
          <Form.Item label="URL">
            <Form.Item field="url" className={styles.dataSourceFormUrl}>
              <Input readOnly />
            </Form.Item>
            <Form.Item className={styles.dataSourceFormTestButton}>
              <Button type="secondary" onClick={handleTest} loading={testing}>
                连接测试
              </Button>
            </Form.Item>
          </Form.Item>
          <Form.Item className={styles.dataSourceFormButton}>
            <Button type="secondary" onClick={handleCancel} loading={testing} style={{ marginRight: 16 }}>
              取消
            </Button>
            <Button type="primary" onClick={handleFinish} loading={submitting}>
              创建
            </Button>
          </Form.Item>
        </Form>
      )}

      {isInternal && (
        <Form form={form} layout="vertical" onValuesChange={handleFormChange} className={styles.dataSourceForm}>
          <Form.Item label="数据源编码" field="code" rules={rules.code}>
            <Input
              maxLength={200}
              placeholder="请输入数据源编码，由字母、数字、下划线组合，须以字母开头，不超过40个字符"
            />
          </Form.Item>
          <Form.Item label="数据源名称" field="datasourceName" rules={rules.datasourceName}>
            <Input maxLength={200} placeholder="请输入数据源名称，不超过200个字符" />
          </Form.Item>
          <Form.Item className={styles.dataSourceFormButton}>
            <Button type="secondary" onClick={handleCancel} loading={testing} style={{ marginRight: 16 }}>
              取消
            </Button>
            <Button type="primary" htmlType="submit" onClick={handleFinish} loading={submitting}>
              创建
            </Button>
          </Form.Item>
        </Form>
      )}
    </>
  );
};

export default CreateDataSource;
