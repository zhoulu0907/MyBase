import kingbaseIcon from '@/assets/images/etl/kingbase.png';
import mockIcon from '@/assets/images/etl/mock.png';
import mysqlIcon from '@/assets/images/etl/mysql.png';
import oracleIcon from '@/assets/images/etl/oracle.png';
import postgresqlIcon from '@/assets/images/etl/postgresql.png';
import { Button, Checkbox, Form, Grid, Input, Message, Modal, Radio, Select, Steps } from '@arco-design/web-react';
import { createETLDataSource, updateETLDatasource } from '@onebase/app';
import { pingETLDataSource } from '@onebase/app/src/services';
import { getHashQueryParam, getPublicKey, sm2Encrypt } from '@onebase/common';
import React, { useEffect, useState } from 'react';
import styles from './index.module.less';

const Row = Grid.Row;
const Col = Grid.Col;
const Step = Steps.Step;

interface InitialData {
  id?: string;
  datasourceName: string;
  datasourceType: string;
  config: {
    host?: string;
    port?: number;
    database?: string;
    jdbcUrl?: string;
    username: string;
    password: string;
    connectMode?: string;
  };
  declaration?: string;
  readonly?: number;
}

interface CreateExternalModalProps {
  // 控制弹窗是否显示
  visible: boolean;
  // 关闭弹窗的回调
  onClose: () => void;
  // 新建外部数据源后的回调
  onCreate: (datasourceUuid: string) => void;
  // 编辑时的初始数据
  initialData?: InitialData;
}

const CreateExternalModal: React.FC<CreateExternalModalProps> = ({ visible, onClose, onCreate, initialData }) => {
  const [form] = Form.useForm();

  const [loading, setLoading] = useState(false);
  const [currentStep, setCurrentStep] = useState(1);

  const [supportedDatasourceList, setSupportedDataSource] = useState<{ datasourceType: string; displayName: string }[]>(
    []
  );
  const [selectedDataSourceType, setSelectedDataSourceType] = useState('');

  const [testConnectionSuccess, setTestConnectionSuccess] = useState(false);

  const connectMode = Form.useWatch('connectMode', form);

  const isEditMode = !!initialData;

  useEffect(() => {
    if (visible) {
      handleGetSupportedDataSource();
      if (initialData) {
        // 编辑模式：直接跳到第二步，设置数据源类型和表单数据
        setSelectedDataSourceType(initialData.datasourceType);
        setCurrentStep(2);
        // 回显表单数据
        const connectModeValue = initialData.config.connectMode || 'default';
        form.setFieldsValue({
          datasourceName: initialData.datasourceName,
          datasourceType: initialData.datasourceType,
          connectMode: connectModeValue,
          host: initialData.config.host || '',
          port: initialData.config.port ? initialData.config.port.toString() : '',
          database: initialData.config.database || '',
          jdbcUrl: initialData.config.jdbcUrl || '',
          username: initialData.config.username || '',
          password: initialData.config.password || '',
          declaration: initialData.declaration || '',
          readonly: initialData.readonly === 1
        });
        // 编辑模式下默认通过连接测试（因为数据已存在）
        setTestConnectionSuccess(true);
      } else {
        // 新建模式：重置状态
        setSelectedDataSourceType('');
        setCurrentStep(1);
        form.resetFields();
        setTestConnectionSuccess(false);
      }
    }
  }, [visible, initialData]);

  useEffect(() => {
    if (currentStep == 1 && !isEditMode) {
      form.resetFields();
    }
    if (currentStep == 2 && selectedDataSourceType != '' && !isEditMode) {
      form.setFieldValue('datasourceType', selectedDataSourceType);
    }
  }, [selectedDataSourceType, currentStep, isEditMode]);

  const showIcon = (datasourceType: string) => {
    if (datasourceType === 'PostgreSQL') {
      return <img className={styles.datasourceIcon} src={postgresqlIcon} alt="PostgreSQL" />;
    }

    if (datasourceType === 'MySQL') {
      return <img className={styles.datasourceIcon} src={mysqlIcon} alt="MySQL" />;
    }
    if (datasourceType === 'KingBase') {
      return <img className={styles.datasourceIcon} src={kingbaseIcon} alt="KingBase" />;
    }
    if (datasourceType === 'ORACLE') {
      return <img className={styles.datasourceIcon} src={oracleIcon} alt="ORACLE" />;
    }
    return <img className={styles.datasourceIcon} src={mockIcon} alt="未知数据源" />;
  };

  const handleOk = async () => {
    if (selectedDataSourceType == '') {
      Message.error('请选择数据源');
      return;
    }

    if (selectedDataSourceType && currentStep == 1) {
      setCurrentStep(2);

      form.setFieldValue('connectMode', 'default');
      return;
    }

    if (currentStep == 2) {
      const curAppId = getHashQueryParam('appId');
      if (curAppId == '') {
        Message.error('应用ID不存在');
        return;
      }

      form.validate().then(async (values) => {
        console.log('values', values);
        if (isEditMode && initialData) {
          // 编辑模式：使用更新接口
          if (values.password) {
            values.password = await sm2Encrypt(getPublicKey(), values.password);
          }

          const res = await updateETLDatasource({
            id: initialData.id!,
            datasourceName: values.datasourceName,
            datasourceType: values.datasourceType,
            applicationId: curAppId!,
            config: {
              host: values.host,
              port: values.port ? parseInt(values.port) : 0,
              jdbcUrl: values.jdbcUrl,
              database: values.database,
              username: values.username,
              password: values.password,
              connectMode: values.connectMode
            },
            declaration: values.declaration,
            readonly: values.readonly ? 1 : 0,
            withCollect: 1
          });
          Message.success('数据源更新成功');
          onCreate(res);
        } else {
          // 新建模式：使用创建接口

          if (values.password) {
            values.password = await sm2Encrypt(getPublicKey(), values.password);
          }

          const res = await createETLDataSource({
            datasourceName: values.datasourceName,
            datasourceType: values.datasourceType,
            applicationId: curAppId!,
            config: {
              host: values.host,
              port: values.port ? parseInt(values.port) : 0,
              jdbcUrl: values.jdbcUrl,
              database: values.database,
              username: values.username,
              password: values.password,
              connectMode: values.connectMode
            },
            declaration: values.declaration,
            readonly: values.readonly ? 1 : 0,
            withCollect: 1
          });
          Message.success('数据源创建成功');
          onCreate(res);
        }

        onClose();
      });
    }
  };

  const handleCancel = () => {
    onClose();
  };

  const handleGetSupportedDataSource = async () => {
    const res = [
      {
        datasourceType: 'PostgreSQL',
        displayName: 'PostgreSQL'
      },
      {
        datasourceType: 'MySQL',
        displayName: 'MySQL'
      },
      {
        datasourceType: 'KingBase',
        displayName: '人大金仓'
      },
      {
        datasourceType: 'ORACLE',
        displayName: 'Oracle'
      }
    ];
    setSupportedDataSource(res);
  };

  const handleTestConnection = async () => {
    form.validate().then(async (values) => {
      if (values.password) {
        values.password = await sm2Encrypt(getPublicKey(), values.password);
      }
      const res = await pingETLDataSource({
        id: initialData?.id,
        datasourceType: values.datasourceType,
        config: {
          host: values.host,
          port: parseInt(values.port),
          database: values.database,
          jdbcUrl: values.jdbcUrl,
          username: values.username,
          password: values.password,
          connectMode: values.connectMode
        }
      });

      if (res) {
        Message.success('连接测试成功');
      } else {
        Message.warning('连接测试失败');
      }

      setTestConnectionSuccess(res);
    });
  };

  return (
    <Modal
      visible={visible}
      title={isEditMode ? '编辑外部数据源' : '新建外部数据源'}
      onOk={handleOk}
      onCancel={handleCancel}
      confirmLoading={loading}
      style={{ width: 800, maxHeight: 950 }}
      unmountOnExit
      footer={
        <div className={styles.createExternalModalFooter}>
          <div>
            {currentStep == 2 && (
              <Button type="default" onClick={handleTestConnection}>
                连接测试
              </Button>
            )}
          </div>
          <div className={styles.createExternalModalFooterButtons}>
            <Button type="default" onClick={currentStep == 2 && !isEditMode ? () => setCurrentStep(1) : handleCancel}>
              {currentStep == 2 && !isEditMode ? '上一步' : '取消'}
            </Button>
            <Button
              type="primary"
              onClick={handleOk}
              disabled={
                (currentStep == 2 && !testConnectionSuccess) || (currentStep == 1 && selectedDataSourceType == '')
              }
            >
              {currentStep == 2 ? '完成' : '下一步'}
            </Button>
          </div>
        </div>
      }
    >
      <div className={styles.createExternalModal}>
        <div className={styles.createExternalModalHeader}>
          <Steps type="dot" current={currentStep}>
            <Step title="选择数据源" />
            <Step title="配置连接" />
          </Steps>
        </div>
        {currentStep == 1 && !isEditMode && (
          <div className={styles.createExternalModalContent}>
            <Input.Search placeholder="搜索数据源" />
            {supportedDatasourceList.map((item: any) => {
              return (
                <div
                  className={styles.datasourceItem}
                  key={item.datasourceType}
                  onClick={() => setSelectedDataSourceType(item.datasourceType)}
                  style={{
                    border:
                      selectedDataSourceType == item.datasourceType
                        ? '2px solid rgb(var(--primary-6))'
                        : '2px solid transparent'
                  }}
                >
                  {showIcon(item.datasourceType)}
                  <div className={styles.datasourceName}>{item.displayName}</div>
                </div>
              );
            })}
          </div>
        )}

        {currentStep == 2 && (
          <div className={styles.createExternalModalContent}>
            <Form form={form} layout="vertical">
              <Row>
                <Form.Item label="数据源名称" field="datasourceName" required>
                  <Input placeholder="请输入数据源名称" />
                </Form.Item>
              </Row>
              <Row>
                <Form.Item label="数据源类型" field="datasourceType" required>
                  <Select placeholder="请选择数据源类型" disabled>
                    <Select.Option value="PostgreSQL">PostgreSQL</Select.Option>
                    <Select.Option value="KingBase">人大金仓</Select.Option>
                    <Select.Option value="ORACLE">ORACLE</Select.Option>
                    <Select.Option value="MySQL">MySQL</Select.Option>
                  </Select>
                </Form.Item>
              </Row>
              <Row gutter={8}>
                <Col span={12}>
                  <Form.Item field="readonly" triggerPropName="checked">
                    <Checkbox>只读数据源</Checkbox>
                  </Form.Item>
                </Col>
                <Col span={12}>
                  <Form.Item label="连接模式" field={'connectMode'} required>
                    <Radio.Group>
                      <Radio value="default">默认</Radio>
                      <Radio value="profession">专业</Radio>
                    </Radio.Group>
                  </Form.Item>
                </Col>
              </Row>
              {connectMode == 'default' && (
                <>
                  <Row gutter={8}>
                    <Col span={12}>
                      <Form.Item label="主机地址" field={'host'} required>
                        <Input placeholder="请输入主机地址" />
                      </Form.Item>
                    </Col>
                    <Col span={12}>
                      <Form.Item label="端口" field={'port'} required>
                        <Input type="number" placeholder="请输入端口号" />
                      </Form.Item>
                    </Col>
                  </Row>
                  <Row>
                    <Form.Item label="数据库名" field={'database'} required>
                      <Input placeholder="请输入数据库名" />
                    </Form.Item>
                  </Row>
                </>
              )}

              {connectMode == 'profession' && (
                <Row>
                  <Form.Item label="JDBC URL" field={'jdbcUrl'} required>
                    <Input placeholder="请输入JDBC URL" />
                  </Form.Item>
                </Row>
              )}
              <Row gutter={8}>
                <Col span={12}>
                  <Form.Item label="用户名" field={'username'} required>
                    <Input placeholder="请输入用户名" />
                  </Form.Item>
                </Col>
                <Col span={12}>
                  <Form.Item label="密码" field={'password'} required>
                    <Input.Password placeholder="请输入密码" />
                  </Form.Item>
                </Col>
              </Row>

              <Row>
                <Form.Item label="描述" field="declaration">
                  <Input.TextArea placeholder="请输入描述" />
                </Form.Item>
              </Row>
            </Form>
          </div>
        )}
      </div>
    </Modal>
  );
};

export default CreateExternalModal;
