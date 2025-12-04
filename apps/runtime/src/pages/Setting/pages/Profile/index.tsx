import PlaceholderPanel from '@/components/PlaceholderPanel';
import { Avatar, Button, Grid, Image, Space, Spin, Tag, Typography } from '@arco-design/web-react';
import { CORP_INFO_PERMISSION as ACTIONS, hasPermission } from '@onebase/common';
import type { PostSimpleRespVO } from '@onebase/platform-center';
import { getDictDataByType, getLoginedUser } from '@onebase/platform-center';
import React, { useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import styles from './index.module.less';

const { Title, Text } = Typography;
const { Col, Row } = Grid;

const ProfilePage: React.FC = () => {
  const nav = useNavigate();
  const { tenantId } = useParams();
  const [loading, setLoading] = useState(true);

  const [userInfo, setUserInfo] = useState<any>(null);

  useEffect(() => {
    fetchUserInfo();
  }, []);

  const fetchUserInfo = async () => {
    try {
      setLoading(true);
      const res = await getLoginedUser(true);
      setUserInfo(res);
      if (res?.id) {
        await getDictDataByType(res.id);
      }
    } finally {
      setLoading(false);
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
  if (!userInfo) {
    return (
      <div className={styles.tenantPage}>
        <div style={{ textAlign: 'center', padding: '40px' }}>
          <p>无法加载个人中心信息</p>
        </div>
      </div>
    );
  }

  const handleGoEditPage = () => {
    nav(`/onebase/${tenantId}/setting/profile/edit`);
  };

  const getStatus = (status: number) => {
    if (status === 0) {
      return '禁用';
    } else {
      return '正常';
    }
  };
  const defaultNickName = userInfo?.nickname?.charAt(0) || 'U';

  return (
    <div className={styles.tenantPage}>
      <div className={styles.userInfo}>
        <Row justify="space-between" align="center" style={{ marginBottom: 24 }}>
          {/* 左侧头像与姓名 */}
          <Col flex="auto">
            <Space align="center">
              <Avatar
                size={80}
                shape="circle"
                className={userInfo.avatar ? styles.currentAvatar : styles.defaultAvatar}
                style={{ border: '1px solid #f0f0f0', overflow: 'hidden', marginRight: '16px' }}
              >
                {userInfo.avatar ? (
                  <Image
                    width={80}
                    height={80}
                    src={userInfo.avatar}
                    style={{
                      objectFit: 'cover',
                      borderRadius: '50%', // 强制圆形裁剪
                      display: 'block' // 避免 inline 元素影响
                    }}
                  />
                ) : (
                  defaultNickName
                )}
              </Avatar>
              <div>
                <div className={styles.userTop}>
                  <Title className={styles.username} heading={6}>
                    {userInfo.nickname}
                  </Title>
                  {userInfo?.posts?.map((post: PostSimpleRespVO) => (
                    <Tag className={styles.userTag} color="cyan" size="small" key={post.id}>
                      {post.name}
                    </Tag>
                  ))}
                </div>
              </div>
            </Space>
          </Col>

          {/* 右上角编辑按钮 */}
          <Col flex="none">
            <Button type="secondary" onClick={handleGoEditPage}>
              编辑
            </Button>
          </Col>
        </Row>

        {/* 下方详细信息区 */}
        <Row gutter={[0, 12]} align="center" style={{ paddingLeft: 105 }}>
          {/* 第一行 */}
          <Col span={8}>
            <Row gutter={8}>
              <Col flex="80px">
                <Text type="secondary">账号</Text>
              </Col>
              <Col flex="auto">
                <Text>{userInfo.username}</Text>
              </Col>
            </Row>
          </Col>

          <Col span={8}>
            <Row gutter={8}>
              <Col flex="80px">
                <Text type="secondary">手机号</Text>
              </Col>
              <Col flex="auto">
                <Text>{userInfo.mobile || '-'}</Text>
              </Col>
            </Row>
          </Col>

          <Col span={8}>
            <Row gutter={8}>
              <Col flex="80px">
                <Text type="secondary">账号状态</Text>
              </Col>
              <Col flex="auto">
                <Text>{getStatus(userInfo.status)}</Text>
              </Col>
            </Row>
          </Col>

          {/* 第二行 */}
          <Col span={8}>
            <Row gutter={8}>
              <Col flex="80px">
                <Text type="secondary">所属部门</Text>
              </Col>
              <Col flex="auto">
                <Text>{userInfo?.dept?.name || '-'}</Text>
              </Col>
            </Row>
          </Col>

          <Col span={8}>
            <Row gutter={8}>
              <Col flex="80px">
                <Text type="secondary">邮箱</Text>
              </Col>
              <Col flex="auto">
                <Text>{userInfo.email || '-'}</Text>
              </Col>
            </Row>
          </Col>

          <Col span={8}>
            <Row gutter={8}>
              <Col flex="80px">
                <Text type="secondary">OneID</Text>
              </Col>
              <Col flex="auto">
                <Text>{userInfo.id || '-'}</Text>
              </Col>
            </Row>
          </Col>
        </Row>
      </div>

      <PlaceholderPanel
        hasPermission={hasPermission(ACTIONS.QUERY)}
        isLoading={loading}
        style={{ display: 'flex', flex: 1, overflow: 'hidden' }}
        spinStyle={{ display: 'flex', flex: 1, overflow: 'hidden' }}
      ></PlaceholderPanel>
    </div>
  );
};

export default ProfilePage;
