import { Avatar, Divider, Empty, Typography } from '@arco-design/web-react';
import { IconLoading, IconEye, IconEyeInvisible } from '@arco-design/web-react/icon';
import type { TenantInfo } from '@onebase/platform-center';
import { getTenantInfo } from '@onebase/platform-center';
import React, { useEffect, useState } from 'react';
import styles from './index.module.less';

const { Title, Text } = Typography;
const TenantPage: React.FC = () => {
  const [tenantInfo, setTenantInfo] = useState<TenantInfo | null>(null);
  const [secretVisible, setSecretVisible] = useState(false);

  useEffect(() => {
    const fetchTenantInfo = async () => {
      try {
        const res = await getTenantInfo();
        setTenantInfo(res);
      } catch (error) {
        // TODO： 联调后移除mock数据
        setTenantInfo({
          id: 1,
          name: '租户名',
          creator: 'admin',
          contactName: '张三',
          contactMobile: '13800138000',
          status: 1,
          domain: 'example.com',
          password: 'password',
          accountCount: 100,
          createTime: '2024-06-01 12:12:12',
          expireTime: '2024-06-30',
          appCount: 10,
          workbenchUrl: 'http://workbench.example.com',
          mobileUrl: 'http://mobile.example.com'
        });
      }
    };

    fetchTenantInfo();
  }, []);

  const secretIconStyle = { cursor: 'pointer', marginLeft: '8px', color: 'rgb(78, 89, 105)' };

  // 显示加载状态
  if (!tenantInfo) {
    return <Empty className={styles.tenantPage} icon={<IconLoading />} description="加载中..."></Empty>;
  }
  return (
    <div className={styles.tenantPage}>
      <div className={styles.tenantPageMain}>
        <div className={`${styles.infoCard} ${styles.infoCardPrimary}`}>
          <div className={styles.infoCardPrimaryLeft}>
            <div className={styles.avatarSection}>
              <Avatar size={56} style={{ backgroundColor: '#009E9E', borderRadius: '8px' }}>
                <span>{tenantInfo.creator?.slice(0,2)}</span>
              </Avatar>
            </div>
            {/* 名称 & ID */}
            <div className={styles.section}>
              <Title heading={5} style={{ margin: 0 }}>
                {tenantInfo.name}
              </Title>
              <Text copyable>ID：{tenantInfo.id}</Text>
            </div>
            <div className={styles.section} style={{marginLeft: '2rem'}}>
              <div className={styles.sectionBody}>
                <div className={styles.descriptionItem}>
                  <Text type="secondary">创建人：</Text>
                  <Text>{tenantInfo.creator}</Text>
                </div>
                <div className={styles.descriptionItem}>
                  <Text type="secondary">创建时间：</Text>
                  <Text>{tenantInfo.createTime}</Text>
                </div>
              </div>
            </div>
          </div>
          {/* 统计信息 */}
          <div className={styles.statsSection}>
            <div className={styles.statCard}>
              <div className={styles.statLabel}>用户人数</div>
              <div className={styles.statValue}>{tenantInfo.accountCount}</div>
            </div>
            <div className={styles.statCard}>
              <div className={styles.statLabel}>应用数量</div>
              <div className={styles.statValue}>{tenantInfo.appCount}</div>
            </div>
          </div>
        </div>
       
        <div className={styles.infoCard}>
          {/* 访问地址 */}
          <div className={styles.section}>
            <div className={styles.sectionTitle}>访问地址</div>
            <div className={styles.sectionBody}>
              <div className={styles.descriptionItem}>
                <Text type="secondary">工作台：</Text>
                <Text 
                  type="primary" 
                  copyable 
                  onClick={() => window.open(tenantInfo.workbenchUrl, '_blank')}
                  style={{ cursor: 'pointer' }}
                >
                  {tenantInfo.workbenchUrl}
                </Text>
              </div>
              <div className={styles.descriptionItem}>
                <Text type="secondary">移动端：</Text>
                <Text 
                  type="primary" 
                  copyable 
                  onClick={() => window.open(tenantInfo.mobileUrl, '_blank')}
                  style={{ cursor: 'pointer' }}
                >
                  {tenantInfo.mobileUrl}
                </Text>
              </div>
            </div>
          </div>
          <Divider />

          {/* 租户凭证 */}
          <div className={styles.section}>
            <div className={styles.sectionTitle}>租户凭证</div>
            <div className={styles.sectionBody}>
              <div className={styles.descriptionItem}>
                <Text type="secondary">tenantKey：</Text>
                <Text copyable>{tenantInfo.workbenchUrl}</Text>
              </div>
              <div className={styles.descriptionItem}>
                <Text type="secondary">tenantSecret：</Text>
                <span className={styles.secretText}>
                  {secretVisible ? tenantInfo.mobileUrl : '•'.repeat(tenantInfo.mobileUrl?.length || 8)}
                </span>
                <span onClick={() => setSecretVisible(!secretVisible)}>
                  {secretVisible ? <IconEye style={secretIconStyle} /> : <IconEyeInvisible style={secretIconStyle} />}
                </span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default TenantPage;