import React, { useEffect, useState } from 'react';
import { Avatar, Spin, Typography, Message, Grid } from '@arco-design/web-react';
import type { TenantInfo } from '@onebase/platform-center';
import { getTenantInfo, updateTenant } from '@onebase/platform-center';
import PlaceholderPanel from '@/components/PlaceholderPanel';
import { hasPermission } from '@/utils/permission';
import { TENANT_INFO_PERMISSION as ACTIONS } from '@/constants/permission';
import styles from './index.module.less';

const { Col, Row } = Grid;
const { Title, Text } = Typography;

const SpaceInfo: React.FC = () => {
  const [tenantInfo, setTenantInfo] = useState<TenantInfo | null>(null);
  const [loading, setLoading] = useState(true);
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
    } catch (error) {
      console.error('更新租户信息失败', error);
    }
  };

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
            <div className={styles.blockHeader}>基本信息</div>

            <div className={styles.baseInfo}>
              <div className={styles.infoCardPrimaryLeft}>
                <div className={styles.avatarSection}>
                  <Avatar shape="square" style={{ width: 160, height: 80, backgroundColor: '#F7F8FA', borderRadius: 12 }}>
                    <span className={styles.avatarText}>{tenantInfo.name?.slice(0, 6)}</span>
                  </Avatar>
                </div>
                {/* 名称 & ID */}
                <div className={styles.section}>
                  <Title
                    heading={5}
                    style={{ margin: 0, fontSize: 16, color: '#272E3B' }}
                    editable={
                      hasPermission(ACTIONS.UPDATE) ? { onChange: setTenantName, onEnd: handleNameChange } : false
                    }
                  >
                    {tenantName}
                  </Title>
                  <div className={styles.enterpriseId}>企业ID：<Text copyable>{tenantInfo.id}</Text></div>
                </div>
              </div>

              {/* 统计信息 */}
              <div className={styles.statsSection}>
                <div className={styles.statCard}>
                  <div className={styles.statLabel}>企业数(个)</div>
                  <div className={styles.statValue}>
                    {tenantInfo.accountCount}
                  </div>
                </div>
                <div className={styles.statCard}>
                  <div className={styles.statLabel}>应用数量(个)</div>
                  <div className={styles.statValue}>{tenantInfo.appCount}</div>
                </div>
              </div>
            </div>
          </div>

          <div className={styles.infoCard}>
            {/* 访问地址 */}
            <div className={styles.blockHeader}>更多信息</div>

            <div className={styles.bottomSection}>
              <Row gutter={24} style={{ marginBottom: 28 }}>
                <Col span={12}>
                  <div style={{ display: 'flex' }}>
                    <span className={styles.infoKey}>企业联系人</span>
                    <span>王少青</span>
                  </div>
                </Col>
                <Col span={12}>
                  <div style={{ display: 'flex' }}>
                    <span className={styles.infoKey}>联系人手机号</span>
                    <span>137 0193 5734</span>
                  </div>
                </Col>
              </Row>

              <Row gutter={24} style={{ marginBottom: 28 }}>
                <Col span={12}>
                  <div style={{ display: 'flex' }}>
                    <span className={styles.infoKey}>联系人邮箱</span>
                    <span>wangshaoqing@cmsr.chinamobile.com</span>
                  </div>
                </Col>
                <Col span={12}>
                  <div style={{ display: 'flex' }}>
                    <span className={styles.infoKey}>用户上限</span>
                    <span>2000</span>
                  </div>
                </Col>
              </Row>

              <Row gutter={24} style={{ marginBottom: 28 }}>
                <Col span={12}>
                  <div style={{ display: 'flex' }}>
                    <span className={styles.infoKey}>行业类型</span>
                    <span>工业</span>
                  </div>
                </Col>
                <Col span={12}>
                  <div style={{ display: 'flex' }}>
                    <span className={styles.infoKey}>企业地址</span>
                    <span>海南省三沙市中沙群岛的岛礁及其海域中沙岛礁 8 幢 64 室</span>
                  </div>
                </Col>
              </Row>
            </div>
          </div>
        </div>
      </div>
    </PlaceholderPanel>
  );
};

export default SpaceInfo;
