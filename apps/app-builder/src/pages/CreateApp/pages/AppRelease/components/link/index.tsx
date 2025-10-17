import { Button, Card, Input, Space, Typography } from '@arco-design/web-react';
import { IconCopy, IconEye, IconQrcode } from '@arco-design/web-react/icon';
import React from 'react';
import styles from './index.module.less';

interface AppAccessLinkProps {
  accessUrl: string;
}

const AppAccessLink: React.FC<AppAccessLinkProps> = ({ accessUrl }) => {
  const handleCopy = () => {
    navigator.clipboard.writeText(accessUrl);
  };

  const handleView = () => {
    // TODO: 访问应用
    console.log('访问应用');
  };

  const handleQRCode = () => {
    // TODO: 实现二维码功能
    console.log('显示二维码');
  };

  return (
    <Card className={styles.appAccessLink}>
      <Typography.Title heading={5} className={styles.title} style={{ marginTop: '0' }}>
        应用访问链接
      </Typography.Title>

      <div className={styles.linkContainer}>
        <Input value={accessUrl} className={styles.urlInput} readOnly placeholder="请先发布应用后，再获取访问链接" />
        <Space className={styles.actionButtons}>
          <Button type="text" icon={<IconCopy />} onClick={handleCopy} className={styles.actionButton} />
          <Button type="text" icon={<IconEye />} onClick={handleView} className={styles.actionButton} />
          <Button type="text" icon={<IconQrcode />} onClick={handleQRCode} className={styles.actionButton} />
        </Space>
      </div>
    </Card>
  );
};

export default AppAccessLink;
