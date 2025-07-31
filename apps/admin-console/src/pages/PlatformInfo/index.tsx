import { Card, Descriptions, Space, Typography } from '@arco-design/web-react';
import { IconInfoCircle } from '@arco-design/web-react/icon';
import React from 'react';
import { useTranslation } from 'react-i18next';
import styles from './index.module.less';

const { Title, Text } = Typography;

const PlatformInfo: React.FC = () => {
  const { t } = useTranslation();

  // 模拟平台信息数据
  const platformData = {
    name: 'ONE BASE Platform',
    version: '1.0.0',
    description: '企业级管理平台，提供用户管理、内容管理、系统设置等功能',
    environment: 'Production',
    lastUpdate: '2024-01-15 10:30:00',
    status: '运行中',
    serverInfo: {
      os: 'Linux Ubuntu 20.04',
      nodeVersion: 'v18.17.0',
      database: 'PostgreSQL 14.0',
      redis: 'Redis 6.2.0'
    }
  };

  return (
    <div className={styles.platformInfo}>
      <Space direction="vertical" size="large" style={{ width: '100%' }}>
        {/* 页面标题 */}
        <div className={styles.pageHeader}>
          <Title heading={3} className={styles.pageTitle}>
            <IconInfoCircle className={styles.titleIcon} />
            {t('platformInfo.title')}
          </Title>
          <Text type="secondary">{t('platformInfo.subtitle')}</Text>
        </div>

        {/* 平台基本信息 */}
        <Card title={t('platformInfo.basicInfo')} className={styles.infoCard}>
          <Descriptions
            column={2}
            data={[
              {
                label: t('platformInfo.name'),
                value: platformData.name,
              },
              {
                label: t('platformInfo.version'),
                value: platformData.version,
              },
              {
                label: t('platformInfo.environment'),
                value: platformData.environment,
              },
              {
                label: t('platformInfo.status'),
                value: (
                  <span className={styles.statusRunning}>
                    {platformData.status}
                  </span>
                ),
              },
              {
                label: t('platformInfo.lastUpdate'),
                value: platformData.lastUpdate,
              },
              {
                label: t('platformInfo.description'),
                value: platformData.description,
                span: 2,
              },
            ]}
          />
        </Card>

        {/* 服务器信息 */}
        <Card title={t('platformInfo.serverInfo')} className={styles.infoCard}>
          <Descriptions
            column={2}
            data={[
              {
                label: t('platformInfo.operatingSystem'),
                value: platformData.serverInfo.os,
              },
              {
                label: t('platformInfo.nodeVersion'),
                value: platformData.serverInfo.nodeVersion,
              },
              {
                label: t('platformInfo.database'),
                value: platformData.serverInfo.database,
              },
              {
                label: t('platformInfo.redis'),
                value: platformData.serverInfo.redis,
              },
            ]}
          />
        </Card>

        {/* 系统状态 */}
        <Card title={t('platformInfo.systemStatus')} className={styles.infoCard}>
          <div className={styles.statusGrid}>
            <div className={styles.statusItem}>
              <div className={styles.statusLabel}>{t('platformInfo.cpuUsage')}</div>
              <div className={styles.statusValue}>23%</div>
            </div>
            <div className={styles.statusItem}>
              <div className={styles.statusLabel}>{t('platformInfo.memoryUsage')}</div>
              <div className={styles.statusValue}>45%</div>
            </div>
            <div className={styles.statusItem}>
              <div className={styles.statusLabel}>{t('platformInfo.diskUsage')}</div>
              <div className={styles.statusValue}>67%</div>
            </div>
            <div className={styles.statusItem}>
              <div className={styles.statusLabel}>{t('platformInfo.activeUsers')}</div>
              <div className={styles.statusValue}>128</div>
            </div>
          </div>
        </Card>
      </Space>
    </div>
  );
};

export default PlatformInfo;