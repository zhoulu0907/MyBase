import React, { useEffect, useState } from 'react';
import { Avatar, Typography, Space, Divider, Empty } from '@arco-design/web-react';
import { IconUser, IconLoading } from '@arco-design/web-react/icon';
import styles from './index.module.less';
import { getTenantInfo } from '@onebase/platform-center/src/services/tenant';
import type { TenantInfo } from '@onebase/platform-center/src/types/tenant';

const { Title, Text } = Typography;
const TenantPage:React.FC = () =>{
const [tenantInfo, setTenantInfo] = useState<TenantInfo | null>(null);
  
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

  // 显示加载状态
  if (!tenantInfo) {
    return <Empty className={styles.tenantPage} icon={<IconLoading />} description='加载中...'></Empty>;
  }
  return (
    <div className={styles.tenantPage}>
      <div className={styles.tenantPageMain}>
        <div className={styles.basicInfoWrapper}>
          <div className={styles.avatarSection}>
            <Avatar size={72} style={{ backgroundColor: '#009E9E', borderRadius: '8px' }}>
              <IconUser />
            </Avatar>
          </div>
          {/* 租户信息 */}
          <div className={styles.infoSection}>
            <Title heading={5} style={{ margin: 0 }}>{tenantInfo.name}</Title>
            <Space className={styles.infoLine}>
              <Text copyable>ID：{tenantInfo.id}</Text>
              <div className={styles.infoBlock}>创建人:{tenantInfo.creator}</div>
              <div className={styles.infoBlock}>创建时间：{tenantInfo.createTime}</div>
            </Space>
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
        <Divider />

        {/* 访问地址 */}
        <div className={styles.section}>
          <div className={styles.sectionTitle}>访问地址</div>
          <div className={styles.sectionBody}>
            <div className={styles.descriptionItem}>
              <Text type="secondary">工作台：</Text>
              <Text copyable>{tenantInfo.workbenchUrl}</Text>
            </div>
            <div className={styles.descriptionItem}>
              <Text type="secondary">移动端：</Text>
              <Text copyable>{tenantInfo.mobileUrl}</Text>
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
              <Text copyable>{tenantInfo.mobileUrl}</Text>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}

export default TenantPage;