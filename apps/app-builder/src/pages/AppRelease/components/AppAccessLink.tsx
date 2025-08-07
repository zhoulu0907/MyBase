import React from "react";
import { Card, Input, Button, Space, Typography } from "@arco-design/web-react";
import { IconCopy, IconEye, IconQrcode } from "@arco-design/web-react/icon";
import styles from "./AppAccessLink.module.less";

interface AppAccessLinkProps {
  accessUrl: string;
  onUrlChange: (url: string) => void;
}

const AppAccessLink: React.FC<AppAccessLinkProps> = ({
  accessUrl,
  onUrlChange,
}) => {
  const handleCopy = () => {
    navigator.clipboard.writeText(accessUrl);
  };

  const handleView = () => {
    // TODO: 访问应用
    console.log("访问应用");
  };

  const handleQRCode = () => {
    // TODO: 实现二维码功能
    console.log("显示二维码");
  };

  return (
    <Card className={styles.appAccessLink}>
      <Typography.Title heading={5} className={styles.title}>
        应用访问链接
      </Typography.Title>

      <div className={styles.linkContainer}>
        <Input
          value={accessUrl}
          onChange={onUrlChange}
          className={styles.urlInput}
          readOnly
        />
        <Space className={styles.actionButtons}>
          <Button
            type="text"
            icon={<IconCopy />}
            onClick={handleCopy}
            className={styles.actionButton}
          />
          <Button
            type="text"
            icon={<IconEye />}
            onClick={handleView}
            className={styles.actionButton}
          />
          <Button
            type="text"
            icon={<IconQrcode />}
            onClick={handleQRCode}
            className={styles.actionButton}
          />
        </Space>
      </div>
    </Card>
  );
};

export default AppAccessLink;
