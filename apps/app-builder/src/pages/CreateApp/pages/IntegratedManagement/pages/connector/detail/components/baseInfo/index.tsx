import { Button, Input, Message } from '@arco-design/web-react';
import { updateConnectInstance, type ConnectInstance, type UpdateConnectInstanceReq } from '@onebase/app';
import React, { useState } from 'react';
import styles from './index.module.less';

interface ConnectorBaseInfoProps {
  baseInfo: ConnectInstance;
  isCreateMode?: boolean;
  createName?: string;
  onCreateNameChange?: (name: string) => void;
  onCreate?: (data: { description: string }) => void;
}

const ConnectorBaseInfo: React.FC<ConnectorBaseInfoProps> = ({
  baseInfo,
  isCreateMode = false,
  createName = '',
  onCreateNameChange,
  onCreate
}) => {
  const [description, setDescription] = useState(baseInfo.description);

  const handleDescriptionOnChange = (value: string) => {
    setDescription(value);
  };

  const updateBaseInfo = async () => {
    const res = await updateConnectInstance({
      id: baseInfo?.id,
      connectorName: baseInfo?.connectorName,
      description: description
    } as UpdateConnectInstanceReq);

    console.log('res :', res);
    if (res) {
      Message.success('更新成功');
    }
  };

  const handleSave = () => {
    // 创建模式
    if (isCreateMode && onCreate) {
      if (!createName.trim()) {
        Message.warning('请输入实例名称');
        return;
      }
      onCreate({ description });
    }
  };

  // 展示基本信息，可根据数据结构调整
  return (
    <div className={styles.baseInfo}>
      <div className={styles.title}>基本信息</div>

      <div className={styles.content}>
        <div className={styles.contentItem}>连接器类型: {baseInfo?.typeCode || '未知类型'}</div>

        <div className={styles.contentItem}>连接器版本: {baseInfo?.version || '1.0.0'}</div>

        <div className={styles.description}>
          <div>描述</div>
          <Input.TextArea
            value={description}
            onChange={(value) => handleDescriptionOnChange(value)}
            placeholder="请填写基础信息"
            maxLength={200}
            showWordLimit
            style={{ width: '100%' }}
          />
        </div>
        <div className={styles.footer}>
          {isCreateMode ? (
            <div style={{ display: 'flex', justifyContent: 'flex-end', gap: 12 }}>
              <Button onClick={() => window.history.back()}>取消</Button>
              <Button type="primary" onClick={handleSave}>
                下一步
              </Button>
            </div>
          ) : (
            <Button type="primary" onClick={updateBaseInfo}>
              保存
            </Button>
          )}
        </div>
      </div>
    </div>
  );
};

export default ConnectorBaseInfo;
