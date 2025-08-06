import React from 'react';
import { Card, Button, Typography } from '@arco-design/web-react';
import { IconArrowDown, IconArrowUp } from '@arco-design/web-react/icon';
import styles from './AppStatusHeader.module.less';

interface AppStatusHeaderProps {
  isPublished: boolean;
  currentVersion: string;
  onPublishToggle: () => void;
}

const AppStatusHeader: React.FC<AppStatusHeaderProps> = ({
  isPublished,
  currentVersion,
  onPublishToggle
}) => {
  return (
    <Card className={styles.appStatusHeader}>
      <div className={styles.statusContent}>
        <div className={styles.statusInfo}>
          <div className={styles.statusIcon}>
            <div className={styles.iconWrapper}>
              <IconArrowUp className={styles.icon} />
            </div>
          </div>
          <div className={styles.statusText}>
            <Typography.Title heading={4} className={styles.statusTitle}>
              {isPublished ? '已发布' : '未发布'}
            </Typography.Title>
            <Typography.Text className={styles.statusDescription}>
              {isPublished 
                ? '应用已发布,您可以将下方链接发布给企业成员进行访问'
                : '应用未发布,请点击右侧按钮发布应用'
              }
            </Typography.Text>
          </div>
        </div>
        
        <div className={styles.statusActions}>
          <div className={styles.versionInfo}>
            <Typography.Text className={styles.versionLabel}>
              正式环境版本号: {currentVersion}
            </Typography.Text>
          </div>
          <Button
            type={isPublished ? 'outline' : 'primary'}
            status={isPublished ? 'danger' : 'success'}
            icon={isPublished ? <IconArrowDown /> : <IconArrowUp />}
            onClick={onPublishToggle}
          >
            {isPublished ? '应用下线' : '发布应用'}
          </Button>
        </div>
      </div>
    </Card>
  );
};

export default AppStatusHeader; 