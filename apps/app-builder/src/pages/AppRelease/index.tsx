import React, { useState } from 'react';
import { Space } from '@arco-design/web-react';
import styles from './index.module.less';
import AppStatusHeader from './components/AppStatusHeader';
import AppAccessLink from './components/AppAccessLink';
import VersionManagement from './components/VersionManagement';

const AppReleasePage: React.FC = () => {
  const [isPublished, setIsPublished] = useState(true);
  const [currentVersion] = useState('V2.0.7');
  const [accessUrl, setAccessUrl] = useState('https://uikfrc0i4r.feishu.cn/wiki/S56JwDq901205EknLDEcbfERnJh');

  const handlePublishToggle = () => {
    setIsPublished(!isPublished);
  };

  return (
    <div className={styles.appReleasePage}>
      <Space direction="vertical" size={16} style={{ width: '100%' }}>
        {/* 应用状态头部 */}
        <AppStatusHeader 
          isPublished={isPublished}
          currentVersion={currentVersion}
          onPublishToggle={handlePublishToggle}
        />
        
        {/* 应用访问链接 */}
        <AppAccessLink 
          accessUrl={accessUrl}
          onUrlChange={setAccessUrl}
        />
        
        {/* 版本管理 */}
        <VersionManagement />
      </Space>
    </div>
  );
};

export default AppReleasePage;