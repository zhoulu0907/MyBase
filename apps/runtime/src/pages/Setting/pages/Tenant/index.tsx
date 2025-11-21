import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import dayjs from 'dayjs';
import { Avatar, Typography, Grid, Space, Tag, Button, Tabs, Pagination, Table, Card, Spin, Image } from '@arco-design/web-react';
import { getCorpListApi, getLoginedUser, getDictDataByType } from '@onebase/platform-center';
import type { CorpDetailResponse, DictData, RoleSimpleRespVO, PostSimpleRespVO } from '@onebase/platform-center';
import PlaceholderPanel from '@/components/PlaceholderPanel';
import { hasPermission, /* UserPermissionManager */ } from '@/utils/permission';
import { CORP_INFO_PERMISSION as ACTIONS } from '@/constants/permission';
import StatusTag from '@/components/StatusTag';
import { appIconMap } from '@onebase/ui-kit';
import { DynamicIcon } from '@/components/DynamicIcon';
import styles from './index.module.less';
import {
  getApplicationSimple,
  type Application,
  type PageParam
} from '@onebase/app';

const TabPane = Tabs.TabPane;
const { Title, Text } = Typography;
const { Col, Row } = Grid;


const CREATED_TYPE = {
  ENTERPRISE: 'enterprise',
  APPLICATION: 'application',
}

const leftPanelWidth = 240;
const SectionPadding = 32 + 64;
const tabPanelWidth = `calc(100vw - ${leftPanelWidth}px - ${SectionPadding}px`;

type ownerCreateType = 'enterprise' | 'application' | string;

const TenantPage: React.FC = () => {
  const nav = useNavigate();
  const [loading, setLoading] = useState(true);
  const [page, setPage] = useState(1);
  const [pageSize, setPageSize] = useState(10);
  const [total, setTotal] = useState(0);
  const [curTab, setCurTab] = useState<ownerCreateType>(CREATED_TYPE.ENTERPRISE);

  const [userInfo, setUserInfo] = useState<any>(null);
  const [appData, setAppData] = useState<Application[]>([]);
  const [corpData, setCorpData] = useState<CorpDetailResponse[]>([]);
  const [industryDict, setTndustryDict] = useState<DictData[] | null>(null);

  useEffect(() => {
    fetchUserInfo();
  }, []);

  useEffect(() => {
    if (curTab === CREATED_TYPE.ENTERPRISE) {
      getOwnerCorp();
    } else if (curTab === CREATED_TYPE.APPLICATION) {
      getOwnerApplication();
    }
  }, [curTab, page, pageSize]);

  const fetchUserInfo = async () => {
    try {
      setLoading(true);
      const res = await getLoginedUser();
      setUserInfo(res);
      if (res?.id) {
        await fetchIndustryDict(res.id)
      }
    } finally {
      setLoading(false);
    }
  };

  // 我创建的企业
  const getOwnerCorp = async () => {
    const req: PageParam = {
      pageNo: page,
      pageSize: pageSize,
      ownerTag: 1, // 我创建的
    };
    const res = await getCorpListApi(req);
    if (res) {
      setCorpData(res.list);
      setTotal(res.total);
    }
  };

  // 我创建的应用
  const getOwnerApplication = async () => {
    const ownerTag = 1;
    const res = await getApplicationSimple(ownerTag,"");
    setAppData(res.list);
  };

  const fetchIndustryDict = async (id: string) => {
    try {
      const res = await getDictDataByType(id);
      setTndustryDict(res);
    } catch (error) {
      console.error('字典数据列表错误', error);
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

  const getColumns = () => {
    return [
      {
        title: '企业LOGO',
        dataIndex: 'corpLogo',
        width: 100,
        render: (url: any) => (
          <img src={url} style={{ width: 72, height: 36, borderRadius: 5, border: '1px solid #F2F3F5', backgroundColor: '#F7F8FA', objectFit: 'contain' }} />
        )
      },
      {
        title: '企业名称',
        dataIndex: 'corpName',
        width: 235,
        ellipsis: true
      },
      {
        title: '企业ID',
        dataIndex: 'id',
        width: 120,
        ellipsis: true
      },
      {
        title: '行业类型',
        dataIndex: 'industryType',
        width: 100,
        placeholder: '-',
        render: (val: string) => {
          const getIndustryTypeName = industryDict?.find(data => data.id === val)?.label || '-';
          return (
            <Tag color="cyan">
              {getIndustryTypeName}
            </Tag>
          )
        }
      },
      {
        title: '管理员',
        dataIndex: 'adminName',
        width: 90,
        placeholder: '-',
        ellipsis: true
      },
      {
        title: '状态',
        dataIndex: 'status',
        width: 200,
        render: (val: number) => <StatusTag status={val} />
      },
      {
        title: '创建时间',
        dataIndex: 'createTime',
        width: 235,
        render: (val: any) => dayjs(val).format('YYYY-MM-DD HH:mm:ss')
      }
    ];
  };

  const handleGoEditPage = () => {
    nav('/onebase/setting/tenant/edit');
  };

  const getStatus = (status: number) => {
    if(status === 0) {
      return "禁用"
    }else {
      return "正常"
    }
  }

  return (
    <div className={styles.tenantPage}>
      <div className={styles.userInfo}>
        <Row justify="space-between" align="center">
          {/* 左侧头像与姓名 */}
          <Col flex="auto">
            <Space align="center">
              <Avatar
                className={styles.avatar}
                size={80}
                shape="circle"
                style={{ border: '1px solid #f0f0f0', overflow: 'hidden' }}
              >
                <Image width={80} height={80} src={userInfo.avatar} />
              </Avatar>
              <div>
                <div className={styles.userTop}>
                  <Title className={styles.username} heading={6}>
                    {userInfo.nickname}
                  </Title>
                  {
                    userInfo?.posts?.map((post: PostSimpleRespVO) => <Tag className={styles.userTag} color="cyan" size="small" key={post.id}>
                      {post.name}
                    </Tag>)
                  }
                </div>
                <Text className={styles.userRole} type="secondary">角色：{userInfo?.roles?.map((role: RoleSimpleRespVO) => role.name).join('、')}</Text>
              </div>
            </Space>
          </Col>

          {/* 右上角编辑按钮 */}
          <Col flex="none">
            <Button type="secondary" onClick={handleGoEditPage}>编辑</Button>
          </Col>
        </Row>

        {/* 下方详细信息区 */}
        <Row gutter={[0, 12]} align='center' style={{ paddingLeft: 105 }}>
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
      >
        <Tabs className={styles.createTabs} activeTab={curTab} onChange={setCurTab} style={{ maxWidth: tabPanelWidth }}>
          <TabPane key={CREATED_TYPE.ENTERPRISE} title='我创建的企业'>
            <Table
              rowKey="id"
              hover
              columns={getColumns()}
              data={corpData}
              pagination={false}
              scroll={{ y: 510 }}
              border={false}
            />
            {/* 页码 */}
            <div
              style={{
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'flex-end',
                marginTop: 12
              }}
            >
              <Pagination
                size="small"
                current={page}
                pageSize={pageSize}
                total={total}
                onChange={setPage}
                onPageSizeChange={setPageSize}
                showTotal
                showJumper
                sizeOptions={[5, 10, 20]}
              />
            </div>

          </TabPane>
          <TabPane key={CREATED_TYPE.APPLICATION} title='我创建的应用'>
            <Space direction="vertical" size={16}>
              {appData?.map((item, index) => (
                <Card
                  key={index}
                  bordered={false}
                  style={{
                    width: '100%',
                    borderRadius: 16,
                    boxShadow: '0 1px 3px rgba(0,0,0,0.05)'
                  }}
                >
                  <Row align="center" gutter={8}>
                    {/* 左侧图标 */}
                    <Col flex="64px" style={{ textAlign: 'center' }}>
                      <div className={styles.myAppIcon} style={{ backgroundColor: item?.iconColor || '#ccc' }}>
                        <DynamicIcon
                          IconComponent={appIconMap[item?.iconName as keyof typeof appIconMap || 'city-one']}
                          theme="outline"
                          size="32"
                          fill="#F2F3F5"
                        />
                      </div>
                    </Col>

                    {/* 右侧信息 */}
                    <Col flex="1">
                      <Space direction="vertical" size={4}>
                        <Title heading={6} style={{ margin: 0 }}>
                          {item.appName}
                        </Title>
                        <Text type="secondary" style={{ lineHeight: 1.6, overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap' }}>
                          {item.description}
                        </Text>
                      </Space>
                    </Col>
                  </Row>
                </Card>
              ))}
            </Space>

          </TabPane>
        </Tabs>
      </PlaceholderPanel>

    </div>
  );
};

export default TenantPage;
