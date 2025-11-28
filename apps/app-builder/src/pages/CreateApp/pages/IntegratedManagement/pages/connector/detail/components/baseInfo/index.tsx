import { Button, Input, Message } from '@arco-design/web-react';
import { TypeCode, updateConnectInstance, type ConnectInstance, type UpdateConnectInstanceReq } from '@onebase/app';
import React, { useState } from 'react';
import styles from './index.module.less';

interface ConnectorBaseInfoProps {
  baseInfo: ConnectInstance;
}

const ConnectorBaseInfo: React.FC<ConnectorBaseInfoProps> = ({ baseInfo }) => {
  const [description, setDescription] = useState(baseInfo.description);

  const handleDescriptionOnChange = (value: string) => {
    setDescription(value);
  };

  const updateBaseInfo = async () => {
    const res = await updateConnectInstance({
      connectorId: baseInfo?.connectorId,
      connectorName: baseInfo?.connectorName,
      description: description
    } as UpdateConnectInstanceReq);

    console.log('res :', res);
    if (res) {
      Message.success('更新成功');
    }
  };

  // 展示基本信息，可根据数据结构调整
  return (
    <div className={styles.baseInfo}>
      <div className={styles.title}>基本信息</div>

      <div className={styles.content}>
        <div className={styles.contentItem}>
          连接器类型: {baseInfo?.typeCode === TypeCode.SCRIPT ? 'JavaScript脚本' : '未知类型'}
        </div>

        <div className={styles.description}>
          <div>描述</div>
          <div>
            <Input.TextArea
              value={description}
              onChange={(value) => handleDescriptionOnChange(value)}
              placeholder="请填写基础信息"
            />
          </div>
        </div>
        <div className={styles.footer}>
          <Button type="primary" onClick={updateBaseInfo}>
            保存
          </Button>
        </div>
      </div>
    </div>
  );
};

export default ConnectorBaseInfo;
