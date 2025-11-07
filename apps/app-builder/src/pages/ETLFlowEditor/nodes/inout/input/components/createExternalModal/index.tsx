import kingbaseIcon from '@/assets/images/etl/kingbase.png';
import mockIcon from '@/assets/images/etl/mock.png';
import postgresqlIcon from '@/assets/images/etl/postgresql.png';
import { Form, Input, Message, Modal, Steps } from '@arco-design/web-react';
import { getETLSupportedDataSource } from '@onebase/app';
import React, { useEffect, useState } from 'react';
import styles from './index.module.less';

const Step = Steps.Step;

interface CreateExternalModalProps {
  // 控制弹窗是否显示
  visible: boolean;
  // 关闭弹窗的回调
  onClose: () => void;
  // 新建外部数据源后的回调
  onCreate?: (data: { name: string; type: string }) => void;
}

const CreateExternalModal: React.FC<CreateExternalModalProps> = ({ visible, onClose, onCreate }) => {
  const [form] = Form.useForm();
  const [loading, setLoading] = useState(false);
  const [currentStep, setCurrentStep] = useState(1);

  const [supportedDatasourceList, setSupportedDataSource] = useState([]);
  const [selectedDataSourceType, setSelectedDataSourceType] = useState('');

  useEffect(() => {
    if (visible) {
      handleGetSupportedDataSource();
    }
  }, [visible]);

  const showIcon = (datasourceType: string) => {
    if (datasourceType === 'PostgreSQL') {
      return <img className={styles.datasourceIcon} src={postgresqlIcon} alt="PostgreSQL" />;
    }

    // if (datasourceType === 'MySQL'){}
    if (datasourceType === 'KingBase') {
      return <img className={styles.datasourceIcon} src={kingbaseIcon} alt="KingBase" />;
    }
    // if (datasourceType === 'ORACLE'){}
    return <img className={styles.datasourceIcon} src={mockIcon} alt="KingBase" />;
  };

  const handleOk = async () => {
    if (selectedDataSourceType == '') {
      Message.error('请选择数据源');
      return;
    }

    if (selectedDataSourceType && currentStep == 1) {
      setCurrentStep(2);
    }

    if (currentStep == 2) {
      //   onCreate?.(selectedDataSourceType);
      onClose();
    }
  };

  const handleCancel = () => {
    if (currentStep == 2) {
      setCurrentStep(1);
      return;
    }
    onClose();
  };

  const handleGetSupportedDataSource = async () => {
    const res = await getETLSupportedDataSource();
    console.log('getSupportedDataSource', res);
    setSupportedDataSource(res);
  };

  return (
    <Modal
      visible={visible}
      title="新建外部数据源"
      onOk={handleOk}
      onCancel={handleCancel}
      confirmLoading={loading}
      style={{ width: 800, height: 800 }}
      unmountOnExit
      okText="下一步"
      cancelText={currentStep == 2 ? '上一步' : '取消'}
    >
      <div className={styles.createExternalModal}>
        <div className={styles.createExternalModalHeader}>
          <Steps type="dot" current={currentStep}>
            <Step title="选择数据源" />
            <Step title="配置连接" />
          </Steps>
        </div>
        {currentStep == 1 && (
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
            <Form>
              <Form.Item></Form.Item>
            </Form>
          </div>
        )}
      </div>
    </Modal>
  );
};

export default CreateExternalModal;
