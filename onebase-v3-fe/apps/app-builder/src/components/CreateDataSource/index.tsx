import { useEffect, useState, forwardRef, useImperativeHandle } from 'react';
import { Button, Form, Grid, Input, Message, Select, Steps } from '@arco-design/web-react';
import { testDatasourceConnection, type DatasourceSaveReqDTO, type DatasourceTestConnectionReqVO } from '@onebase/app';
import damengImg from '@/assets/images/etl/dameng.png';
import postgresqlImg from '@/assets/images/etl/postgresql.png';
import kingbaseImg from '@/assets/images/etl/kingbase.png';
import styles from './index.module.less';

const Step = Steps.Step;
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

const dbTypes = [
  { label: 'PostgreSQL', value: 'PostgreSQL', urlPrefix: 'jdbc:postgresql://' },
  { label: 'KingBase', value: 'KingBase', urlPrefix: 'jdbc:kingbase8://' },
  { label: 'DM', value: 'DM', urlPrefix: 'jdbc:dm://' }
];

const datasourceList = [
  {
    img: postgresqlImg,
    name: 'PostgreSQL',
    value: 'PostgreSQL'
  },
  {
    img: kingbaseImg,
    name: '人大金仓',
    value: 'KingBase'
  },
  {
    img: damengImg,
    name: '达梦数据库',
    value: 'DM'
  }
];

export type DataSourceHandle = {
  handleGetDatasource: () => Promise<DatasourceSaveReqDTO | undefined>;
};

interface IProps {
  currentStep: number;
  dbTypeSelect: string;
  setDbTypeSelect: (vlaue: string) => void;
  style?: React.CSSProperties;
}

// 创建外部数据源
const CreateDataSource = forwardRef<DataSourceHandle, IProps>((props, ref) => {
  const { currentStep, dbTypeSelect, setDbTypeSelect, style } = props;

  const [form] = Form.useForm<DataSourceFormValues>();
  const [testing, setTesting] = useState<boolean>(false);
  const [formValues, setFormValues] = useState<Partial<DataSourceFormValues>>({});

  useImperativeHandle(ref, () => ({
    async handleGetDatasource(): Promise<DatasourceSaveReqDTO | undefined> {
      try {
        const values = await form.validate();

        const createParams = {
          datasourceName: values.datasourceName,
          code: values.code,
          datasourceType: values.datasourceType,
          config: JSON.stringify({
            host: values.host,
            port: parseInt(values.port),
            database: values.database,
            username: values.username,
            password: values.password,
            url: values.url || `jdbc:mysql://${values.host}:${values.port}/${values.database}`
          }),
          description: `${values.datasourceType} 数据源`,
          datasourceOrigin: 1 // 自有数据源
        };

        return createParams;
      } catch (error) {
        return undefined;
      }
    }
  }));

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
    code: [
      { required: true, message: '请输入数据源编码' },
      {
        match: /^[A-Za-z][A-Za-z0-9_]*$/,
        message: '数据源编码不符合填写要求'
      }
    ],
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

  return (
    <div className={styles.createDatasource} style={style}>
      <Steps current={currentStep} className={styles.steps}>
        <Step title='选择数据源类型' />
        <Step title='配置连接' />
      </Steps>

      {
        currentStep === 1 ? (
          <div className={styles.databaseList}>
            {
              datasourceList.map((data, index) => (
                <div
                  className={`${styles.databaseItem} ${data.value === dbTypeSelect ? styles.activeDatabaseItem : ''}`}
                  onClick={() => setDbTypeSelect(data.value)}
                  key={index}
                >
                  <img src={data.img} alt={data.name} />
                  <div>{data.name}</div>
                </div>
              ))
            }
          </div>
        ) : (
          <Form form={form} layout="vertical" onValuesChange={handleFormChange} className={styles.dataSourceForm}>
            <Form.Item label="数据源编码" field="code" rules={rules.code}>
              <Input
                maxLength={40}
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
              initialValue={dbTypes.find(db => db.value === dbTypeSelect)?.value}
              disabled
            >
              <Select options={dbTypes} />
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
          </Form>
        )
      }
    </div>
  );
});

export default CreateDataSource;
