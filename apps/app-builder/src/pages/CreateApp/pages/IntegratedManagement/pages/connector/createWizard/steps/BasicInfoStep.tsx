import { useEffect } from 'react';
import { Input, Message } from '@arco-design/web-react';
import { useNavigate, useParams } from 'react-router-dom';
import { getHashQueryParam } from '@onebase/common';
import { useConnectorWizardStore } from '../store';
import styles from '../index.module.less';

const BasicInfoStep: React.FC = () => {
  const navigate = useNavigate();
  const { tenantId } = useParams();
  const { connectorType, formData, updateFormData, nextStep, fetchSchemas, ui, schemas } = useConnectorWizardStore();

  // 组件挂载时确保 schemas 已加载
  useEffect(() => {
    if (connectorType.nodeCode && !ui.error && !schemas.conn_config) {
      // 如果 schemas 还未加载，则调用接口
      fetchSchemas(connectorType.nodeCode);
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [connectorType.nodeCode]);

  const handleCancel = () => {
    const appId = getHashQueryParam('appId');
    navigate(`/onebase/${tenantId}/home/create-app/integrated-management/connector-instances?appId=${appId}`);
  };

  const handleNext = () => {
    // 校验
    if (!formData.basicInfo.connectorName.trim()) {
      Message.error('请输入实例名称');
      return;
    }
    if (formData.basicInfo.connectorName.length > 30) {
      Message.error('实例名称不能超过30个字符');
      return;
    }
    if (formData.basicInfo.description.length > 200) {
      Message.error('描述不能超过200个字符');
      return;
    }

    nextStep();
  };

  return (
    <div className={styles.stepContainer}>
      <div className={styles.baseInfo}>
        <div className={styles.baseInfoTitle}>基本信息</div>

        <div className={styles.baseInfoContent}>
          {/* 实例名称 */}
          <div className={styles.baseInfoItem}>
            <div className={styles.baseInfoLabel}>实例名称</div>
            <div className={styles.baseInfoValue}>
              <Input
                placeholder={`${connectorType.nodeName}+实例`}
                value={formData.basicInfo.connectorName}
                onChange={(val) =>
                  updateFormData({
                    basicInfo: { ...formData.basicInfo, connectorName: val }
                  })
                }
                maxLength={30}
                showWordLimit
              />
            </div>
          </div>

          {/* 连接器类型 */}
          <div className={styles.baseInfoItem}>
            <div className={styles.baseInfoLabel}>连接器类型</div>
            <div className={styles.baseInfoValue}>
              <div className={styles.readOnlyText}>{connectorType.nodeName}</div>
            </div>
          </div>

          {/* 版本 */}
          <div className={styles.baseInfoItem}>
            <div className={styles.baseInfoLabel}>版本</div>
            <div className={styles.baseInfoValue}>
              <div className={styles.readOnlyText}>{connectorType.version}</div>
            </div>
          </div>

          {/* 实例描述 */}
          <div className={styles.baseInfoItem}>
            <div className={styles.baseInfoLabel}>实例描述</div>
            <div className={styles.baseInfoValue}>
              <Input.TextArea
                placeholder="请输入实例描述"
                value={formData.basicInfo.description}
                onChange={(val) =>
                  updateFormData({
                    basicInfo: { ...formData.basicInfo, description: val }
                  })
                }
                maxLength={200}
                showWordLimit
                autoSize={{ minRows: 3, maxRows: 6 }}
                style={{ width: '100%' }}
              />
            </div>
          </div>
        </div>
      </div>

      <div className={styles.stepFooter}>
        <button
          type="button"
          onClick={handleCancel}
          style={{
            padding: '8px 24px',
            backgroundColor: '#fff',
            color: '#4e5969',
            border: '1px solid #e5e5e5',
            borderRadius: '4px',
            cursor: 'pointer',
            marginRight: '12px'
          }}
        >
          取消
        </button>
        <button
          type="button"
          onClick={handleNext}
          style={{
            padding: '8px 24px',
            backgroundColor: 'rgb(var(--primary-6))',
            color: '#fff',
            border: 'none',
            borderRadius: '4px',
            cursor: 'pointer'
          }}
        >
          下一步
        </button>
      </div>
    </div>
  );
};

export default BasicInfoStep;
