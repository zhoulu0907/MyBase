import { Card, Descriptions, Space, Typography } from '@arco-design/web-react';
// import { IconInfoCircle } from '@arco-design/web-react/icon';
import React from 'react';
// import { useTranslation } from 'react-i18next';
import styles from './index.module.less';

// const { Title, Text } = Typography;

const Administrator: React.FC = () => {
  // const { t } = useTranslation();

  // // 模拟平台信息数据
  // const platformData = {
  //   name: 'ONE BASE Platform',
  //   version: '1.0.0',
  //   description: '企业级管理平台，提供用户管理、内容管理、系统设置等功能',
  //   environment: 'Production',
  //   lastUpdate: '2024-01-15 10:30:00',
  //   status: '运行中',
  //   serverInfo: {
  //     os: 'Linux Ubuntu 20.04',
  //     nodeVersion: 'v18.17.0',
  //     database: 'PostgreSQL 14.0',
  //     redis: 'Redis 6.2.0'
  //   }
  // };

  return (
    <div className={styles.administrator}>
      <Space direction="vertical" size="large" style={{ width: '100%' }}>
        <div>administrator</div>
      </Space>
    </div>
  );
};

export default Administrator;