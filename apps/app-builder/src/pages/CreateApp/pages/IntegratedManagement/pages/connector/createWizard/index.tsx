import { useEffect } from 'react';
import { Steps } from '@arco-design/web-react';
import { IconArrowLeft } from '@arco-design/web-react/icon';
import { useNavigate, useParams } from 'react-router-dom';
import { getHashQueryParam } from '@onebase/common';
import { getConnectorTypeInfo, createConnectInstance } from '@onebase/app';
import { Message } from '@arco-design/web-react';
import { useConnectorWizardStore } from './store';
import BasicInfoStep from './steps/BasicInfoStep';
import DynamicConfigStep from './steps/DynamicConfigStep';
import RelatedFlowsStep from './steps/RelatedFlowsStep';
import RequestLogsStep from './steps/RequestLogsStep';
import styles from './index.module.less';

const ConnectorCreateWizard: React.FC = () => {
  const navigate = useNavigate();
  const { tenantId } = useParams();
  const { currentStep, fetchSchemas, reset, formData } = useConnectorWizardStore();

  useEffect(() => {
    // 初始化：从 URL 获取连接器类型信息
    const connectorTypeParam = getHashQueryParam('connectorType');
    const connectorNameParam = getHashQueryParam('connectorName');

    if (connectorTypeParam) {
      // 获取连接器类型详细信息（包含 schemas）
      getConnectorTypeInfo(connectorTypeParam)
        .then((res: any) => {
          const typeInfo = {
            nodeCode: connectorTypeParam,
            nodeName: res?.nodeName || connectorNameParam || '未知类型',
            version: res?.version || '1.0.0',
          };

          // 更新 store
          useConnectorWizardStore.getState().updateConnectorType(typeInfo);

          // 设置默认实例名称
          useConnectorWizardStore.getState().updateFormData({
            basicInfo: {
              connectorName: `${typeInfo.nodeName}实例`,
              description: '',
            },
          });

          // 获取动态 schemas
          fetchSchemas(connectorTypeParam);
        })
        .catch((error: unknown) => {
          console.error('获取连接器类型信息失败:', error);
          Message.error('获取连接器类型信息失败');
        });
    }

    // 清理函数
    return () => {
      reset();
    };
  }, []);

  const handleSubmit = async () => {
    const appId = getHashQueryParam('appId');
    if (!appId) {
      Message.error('应用ID获取失败');
      return;
    }

    try {
      const { connectorType, formData } = useConnectorWizardStore.getState();

      // 构建动态 payload
      const payload = {
        applicationId: appId,
        connectorName: formData.basicInfo.connectorName,
        typeCode: connectorType.nodeCode,
        description: formData.basicInfo.description,
        config: {
          basicInfo: formData.basicInfo,
          conn_config: formData.conn_config,
          action_config: formData.action_config,
        },
      };

      const res = await createConnectInstance(payload);
      if (res) {
        Message.success('创建成功');
        reset();
        navigate(`/onebase/${tenantId}/home/create-app/integrated-management/connector-detail?appId=${appId}&id=${res.id}`);
      }
    } catch (error) {
      console.error('创建失败:', error);
      Message.error('创建失败');
    }
  };

  const steps = [
    { title: '基本信息', description: '配置实例名称和描述' },
    { title: '环境配置', description: '配置连接环境（动态）' },
    { title: '动作配置', description: '配置连接器动作（动态）' },
    { title: '关联逻辑流', description: '查看关联的逻辑流' },
    { title: '请求日志', description: '查看请求记录' },
  ];

  const renderStep = () => {
    switch (currentStep) {
      case 0:
        return <BasicInfoStep />;
      case 1:
        return <DynamicConfigStep schemaType="conn_config" stepIndex={1} title="环境配置" />;
      case 2:
        return <DynamicConfigStep schemaType="action_config" stepIndex={2} title="动作配置" />;
      case 3:
        return <RelatedFlowsStep />;
      case 4:
        return <RequestLogsStep onSubmit={handleSubmit} />;
      default:
        return <BasicInfoStep />;
    }
  };

  const handleBack = () => {
    const appId = getHashQueryParam('appId');
    navigate(`/onebase/${tenantId}/home/create-app/integrated-management/connector-instances?appId=${appId}`);
  };

  return (
    <div className={styles.wizardContainer}>
      {/* 左侧侧边栏 - 垂直步骤条 */}
      <div className={styles.wizardSidebar}>
        <Steps current={currentStep} direction="vertical">
          {steps.map((step, index) => (
            <Steps.Step key={index} title={step.title} description={step.description} />
          ))}
        </Steps>
      </div>

      {/* 右侧内容区 */}
      <div className={styles.wizardMain}>
        {/* 顶部 Header */}
        <div className={styles.wizardHeader}>
          <div className={styles.headerBack} onClick={handleBack}>
            <IconArrowLeft style={{ marginRight: '8px' }} />
            <span>返回</span>
          </div>
          <div className={styles.headerTitle}>
            连接器实例-{formData.basicInfo.connectorName || '实例'}
          </div>
        </div>

        {/* 表单内容区 */}
        <div className={styles.wizardContent}>{renderStep()}</div>
      </div>
    </div>
  );
};

export default ConnectorCreateWizard;
