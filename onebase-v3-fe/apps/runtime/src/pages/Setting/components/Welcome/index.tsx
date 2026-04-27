import { useI18n } from '@/hooks/useI18n';
import { Card, Space, Typography } from '@arco-design/web-react';
import { IconHome, IconInfoCircle } from '@arco-design/web-react/icon';
import React from 'react';
import styles from './index.module.less';

const { Title, Text } = Typography;

const Welcome: React.FC = () => {
  const { t } = useI18n();

  return (
    <div className={styles.welcome}>
      <Space direction="vertical" size="large" style={{ width: '100%' }}>
        {/* 欢迎标题 */}
        <div className={styles.welcomeHeader}>
          <Title heading={2} className={styles.welcomeTitle}>
            <IconHome className={styles.titleIcon} />
            {t('welcome.title')}
          </Title>
          <Text type="secondary" className={styles.welcomeSubtitle}>
            {t('welcome.subtitle')}
          </Text>
        </div>

        {/* 快速访问卡片 */}
        <div className={styles.quickAccess}>
          <Title heading={4} className={styles.sectionTitle}>
            {t('welcome.quickAccess')}
          </Title>
          <div className={styles.cardGrid}>
            <Card
              className={styles.quickCard}
              hoverable
              onClick={() => (window.location.href = '/onebase/platform-info')}
            >
              <div className={styles.cardContent}>
                <IconInfoCircle className={styles.cardIcon} />
                <Title heading={5}>{t('welcome.platformInfo')}</Title>
                <Text type="secondary">{t('welcome.platformInfoDesc')}</Text>
              </div>
            </Card>
          </div>
        </div>

        {/* 系统状态概览 */}
        <Card title={t('welcome.systemOverview')} className={styles.overviewCard}>
          <div className={styles.overviewGrid}>
            <div className={styles.overviewItem}>
              <div className={styles.overviewLabel}>{t('welcome.totalUsers')}</div>
              <div className={styles.overviewValue}>1,234</div>
            </div>
            <div className={styles.overviewItem}>
              <div className={styles.overviewLabel}>{t('welcome.activeUsers')}</div>
              <div className={styles.overviewValue}>567</div>
            </div>
            <div className={styles.overviewItem}>
              <div className={styles.overviewLabel}>{t('welcome.totalArticles')}</div>
              <div className={styles.overviewValue}>89</div>
            </div>
            <div className={styles.overviewItem}>
              <div className={styles.overviewLabel}>{t('welcome.systemStatus')}</div>
              <div className={styles.overviewValue}>
                <span className={styles.statusRunning}>{t('welcome.running')}</span>
              </div>
            </div>
          </div>
        </Card>
      </Space>
    </div>
  );
};

export default Welcome;
