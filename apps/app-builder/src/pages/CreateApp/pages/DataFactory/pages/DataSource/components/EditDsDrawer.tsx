import { useAppStore } from '@/store/store_app';
import { Button, Drawer, Form, Input, Message, Select } from '@arco-design/web-react';
import {
  testDatasourceConnection,
  updateDatasource,
  type DatasourceSaveReqVO,
  type DatasourceTestConnectionReqVO
} from '@onebase/app';
import React, { useEffect, useMemo, useState } from 'react';
import styles from '../index.module.less';

const Option = Select.Option;

interface DataSourceFormValues {
  type: string;
  name: string;
  host: string;
  port: string;
  username: string;
  password: string;
  database: string;
  url: string;
}

interface EditDsDrawerProps {
  visible: boolean;
  onClose: () => void;
  dataSource?: DatasourceSaveReqVO;
  onSuccess: () => void;
}

const dbTypes = [{ label: 'MySQL', value: 'MySQL', urlPrefix: 'jdbc:mysql://' }];

const EditDsDrawer: React.FC<EditDsDrawerProps> = ({ visible, onClose, dataSource, onSuccess }) => {
  const { curAppId } = useAppStore();
  const [form] = Form.useForm<DataSourceFormValues>();
  const [testing, setTesting] = useState(false);
  const [submitting, setSubmitting] = useState(false);

  // 拼接 URL
  const url = useMemo(() => {
    const values = form.getFieldsValue();
    const typeObj = dbTypes.find((t) => t.value === values.type) || dbTypes[0];
    if (!values.host || !values.port || !values.database) return '';
    return `${typeObj.urlPrefix}${values.host}:${values.port}/${values.database}`;
  }, [form]);

  // 初始化表单数据
  useEffect(() => {
    if (visible && dataSource) {
      const config = dataSource.config || {};
      form.setFieldsValue({
        type: dataSource.datasourceType,
        name: dataSource.datasourceName,
        host: config.host || '',
        port: config.port?.toString() || '3306',
        username: config.username || '',
        password: config.password || '',
        database: config.database || '',
        url: config.url || ''
      });
    }
  }, [visible, dataSource, form]);

  // 监听表单变化，强制刷新 URL
  const handleFormChange = () => {
    form.setFieldsValue({});
  };

  // 连接测试
  const handleTest = async () => {
    try {
      const values = await form.validate();
      setTesting(true);

      // 构建测试连接参数
      const testParams: DatasourceTestConnectionReqVO = {
        datasourceType: values.type,
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

      if (res?.success) {
        Message.success(`连接成功，耗时 ${res.duration}ms`);
      } else {
        Message.error(res.msg || '连接测试失败');
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

  // 提交更新
  const handleSubmit = async () => {
    try {
      const values = await form.validate();
      setSubmitting(true);

      if (!dataSource) {
        Message.error('数据源信息不存在');
        return;
      }

      // 构建更新数据源参数
      const updateParams: DatasourceSaveReqVO = {
        id: dataSource.id,
        datasourceName: values.name,
        code: dataSource.code, // 保持原有编码
        datasourceType: values.type,
        config: {
          host: values.host,
          port: parseInt(values.port),
          database: values.database,
          username: values.username,
          password: values.password,
          url: values.url || `jdbc:mysql://${values.host}:${values.port}/${values.database}`
        },
        // description: dataSource.description,
        // runMode: dataSource.runMode,
        applicationId: curAppId
        // lockVersion: dataSource.lockVersion,
      };

      const res = await updateDatasource(updateParams);
      console.log('edit res', res);
      if (res) {
        Message.success('数据源更新成功');
        onSuccess();
        onClose();
      } else {
        Message.error(res?.msg || '更新失败');
      }
    } catch (error) {
      if (error instanceof Error) {
        Message.error(error.message);
      } else {
        Message.error('更新失败，请检查表单数据');
      }
    } finally {
      setSubmitting(false);
    }
  };

  // 关闭抽屉时重置表单
  const handleClose = () => {
    form.resetFields();
    onClose();
  };

  return (
    <Drawer
      title="编辑数据源"
      width={600}
      visible={visible}
      onCancel={handleClose}
      className={styles.dataSourceForm}
      footer={
        <div style={{ textAlign: 'right' }}>
          <Button onClick={handleClose} style={{ marginRight: 8 }}>
            取消
          </Button>
          <Button type="primary" onClick={handleTest} loading={testing} style={{ marginRight: 8 }}>
            连接测试
          </Button>
          <Button type="primary" onClick={handleSubmit} loading={submitting}>
            确定
          </Button>
        </div>
      }
    >
      <Form form={form} layout="vertical" onValuesChange={handleFormChange} className={styles.dataSourceForm}>
        <Form.Item label="数据源类型" field="type" rules={[{ required: true, message: '请选择数据源类型' }]}>
          <Select>
            {dbTypes.map((t) => (
              <Option value={t.value} key={t.value}>
                {t.label}
              </Option>
            ))}
          </Select>
        </Form.Item>
        <Form.Item label="名称" field="name" rules={[{ required: true, message: '请输入名称' }]}>
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
        <Form.Item label="数据库名称" field="database" rules={[{ required: true, message: '请输入数据库名称' }]}>
          <Input maxLength={30} placeholder="数据库名称" />
        </Form.Item>
        <Form.Item label="URL">
          <Input value={url} readOnly />
        </Form.Item>
      </Form>
    </Drawer>
  );
};

export default EditDsDrawer;
