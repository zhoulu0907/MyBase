import { Avatar, Divider, Spin, Typography, Message } from '@arco-design/web-react';
import { IconEye, IconEyeInvisible } from '@arco-design/web-react/icon';
import type { TenantInfo } from '@onebase/platform-center';
import { getTenantInfo, updateTenant } from '@onebase/platform-center';
import React, { useEffect, useState } from 'react';
import dayjs from 'dayjs';
import styles from './index.module.less';
import PlaceholderPanel from '@/components/PlaceholderPanel';
import { hasPermission } from '@/utils/permission';
import { TENANT_INFO_PERMISSION as ACTIONS } from '@/constants/permission';

const { Title, Text } = Typography;
const TenantPage: React.FC = () => {
  const [tenantInfo, setTenantInfo] = useState<TenantInfo | null>(null);
  const [loading, setLoading] = useState(true);
  const [secretVisible, setSecretVisible] = useState(false);
  const [tenantName, setTenantName] = useState('');

  const fetchTenantInfo = async () => {
    try {
      setLoading(true);
      const res = await getTenantInfo();
      setTenantInfo(res);
      setTenantName(res.name);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchTenantInfo();
  }, []);

  const handleNameChange = async (newName: string) => {
    if (!tenantInfo) return;
    
    if (!newName.trim()) {
      Message.error('租户名称不能为空');
      return;
    }

    try {
      await updateTenant({
        id: tenantInfo.id,
        name: newName
      });
      
      setTenantInfo({
        ...tenantInfo,
        name: newName
      });
      
      Message.success('租户名称更新成功');
    } catch (error) {}
  };

  const secretIconStyle = { cursor: 'pointer', marginLeft: '8px', color: 'rgb(78, 89, 105)' };

  const gotoLink = (link: string | null) => {
    if (!link) {
      return;
    }
    () => window.open(link, '_blank')
  }

  // 显示加载状态
  if (loading) {
    return (
      <div className={styles.tenantPage}>
        <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '300px' }}>
          <Spin tip="加载中..." />
        </div>
      </div>
    );
  }

  // 数据加载完成后但没有租户信息
  if (!tenantInfo) {
    return (
      <div className={styles.tenantPage}>
        <div style={{ textAlign: 'center', padding: '40px' }}>
          <p>无法加载租户信息</p>
        </div>
      </div>
    );
  }

  return (
    <PlaceholderPanel hasPermission={hasPermission(ACTIONS.QUERY)} isLoading={loading}>
      <div className={styles.tenantPage}>
        <div className={styles.tenantPageMain}>
          <div className={`${styles.infoCard} ${styles.infoCardPrimary}`}>
            <div className={styles.infoCardPrimaryLeft}>
              <div className={styles.avatarSection}>
                <Avatar size={56} style={{ backgroundColor: '#009E9E', borderRadius: '8px' }}>
                  <span>{tenantInfo.name?.slice(0,2)}</span>
                </Avatar>
              </div>
              {/* 名称 & ID */}
              <div className={styles.section}>
                <Title
                  heading={5}
                  style={{ margin: 0 }}
                  editable={ hasPermission(ACTIONS.UPDATE) ? { onChange: setTenantName,onEnd: handleNameChange } : false }>
                  {tenantName}
                </Title>
                <Text copyable>ID：{tenantInfo.id}</Text>
              </div>
              <div className={styles.section} style={{marginLeft: '2rem'}}>
                <div className={styles.sectionBody}>
                  <div className={styles.descriptionItem}>
                    <Text type="secondary">创建人：</Text>
                    <Text>{tenantInfo.contactName}</Text>
                  </div>
                  <div className={styles.descriptionItem}>
                    <Text type="secondary">创建时间：</Text>
                    <Text>{dayjs(tenantInfo.createTime).format('YYYY-MM-DD HH:mm:ss')}</Text>
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
                    onClick={() => { gotoLink(tenantInfo.website) }}
                    style={{ cursor: 'pointer' }}
                  >
                    {tenantInfo.website || '-'}
                  </Text>
                </div>
                <div className={styles.descriptionItem}>
                  <Text type="secondary">移动端：</Text>
                  <Text 
                    type="primary" 
                    copyable 
                    onClick={() => { gotoLink(tenantInfo.websiteH5) }}
                    style={{ cursor: 'pointer' }}
                  >
                    {tenantInfo.websiteH5 || '-'}
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
                  <Text copyable>{tenantInfo.tenantKey || '-'}</Text>
                </div>
                <div className={styles.descriptionItem}>
                  <Text type="secondary">tenantSecret：</Text>
                  <span className={styles.secretText || '-'}>
                    {secretVisible ? tenantInfo.tenantSecret || '-': '•'.repeat(tenantInfo.tenantSecret?.length || 8)}
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
    </PlaceholderPanel>
  );
};

export default TenantPage;