import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import dayjs from 'dayjs';
import { Avatar, Typography, Grid, Space, Tag, Button, Tabs, Pagination, Table, Card } from '@arco-design/web-react';
// import type { TenantInfo } from '@onebase/platform-center';
// import { getTenantInfo } from '@onebase/platform-center';
import PlaceholderPanel from '@/components/PlaceholderPanel';
import { hasPermission } from '@/utils/permission';
import { TENANT_INFO_PERMISSION as ACTIONS } from '@/constants/permission';
import StatusTag from '@/components/StatusTag';
import { appIconMap } from '@onebase/ui-kit';
import DynamicIcon from '@/components/DynamicIcon';
import styles from './index.module.less';

const TabPane = Tabs.TabPane;
const { Title, Text } = Typography;
const { Col, Row } = Grid;

const tableData = [{
  logo: '',
  name: 'name',
  id: '1',
  type: '333',
  admin: 'admin',
  status: 1,
  createTime: '1762154032380'
},
{
  logo: '',
  name: 'name',
  id: '2',
  type: '333',
  admin: 'admin',
  status: 1,
  createTime: '1762154032380'
},
{
  logo: '',
  name: 'name',
  id: '3',
  type: '333',
  admin: 'admin',
  status: 1,
  createTime: '1762154032380'
}];

const appData = [
  {
    name: '智慧工厂应用',
    desc: '覆盖报案、查勘、定损、核赔到支付的全流程数字化管理通过智能识别与流程自动化，提高理赔效率与准确性，优化客户体验',
  },
  {
    name: '智慧工厂应用',
    desc: '覆盖报案、查勘、定损、核赔到支付的全流程数字化管理通过智能识别与流程自动化，提高理赔效率与准确性，优化客户体验',
  },
  {
    name: '智慧工厂应用',
    desc: '覆盖报案、查勘、定损、核赔到支付的全流程数字化管理通过智能识别与流程自动化，提高理赔效率与准确性，优化客户体验',
  }
];

const TenantPage: React.FC = () => {
  const nav = useNavigate();
  // const [tenantInfo, setTenantInfo] = useState<TenantInfo | null>(null);
  const [loading, setLoading] = useState(true);

  const [page, setPage] = useState(1);
  const [pageSize, setPageSize] = useState(10);
  const [total, setTotal] = useState(0);
  const [data, setData] = useState<any[]>(tableData);
  const [editingUser, setEditingUser] = useState<any | undefined>();

  // const fetchTenantInfo = async () => {
  //   try {
  //     setLoading(true);
  //     const res = await getTenantInfo();
  //     setTenantInfo(res);
  //   } finally {
  //     setLoading(false);
  //   }
  // };

  useEffect(() => {
    // fetchTenantInfo();
  }, []);

  // 显示加载状态
  // if (loading) {
  //   return (
  //     <div className={styles.tenantPage}>
  //       <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '300px' }}>
  //         <Spin tip="加载中..." />
  //       </div>
  //     </div>
  //   );
  // }

  // 数据加载完成后但没有租户信息
  // if (!tenantInfo) {
  //   return (
  //     <div className={styles.tenantPage}>
  //       <div style={{ textAlign: 'center', padding: '40px' }}>
  //         <p>无法加载租户信息</p>
  //       </div>
  //     </div>
  //   );
  // }

  // 生成完整的工作台和移动端链接
  // const fullWebsite = generateFullUrl(tenantInfo.website);
  // const fullWebsiteH5 = generateFullUrl(tenantInfo.websiteH5);

  const handleEdit = (record: any) => {
    setEditingUser(record);
  };

  const getColumns = (handleEdit: (record: any) => void) => {
    return [
      {
        title: '企业LOGO',
        dataIndex: 'logo',
        width: 100,
        render: (_: any) => (
          <img />
        )
      },
      {
        title: '企业名称',
        dataIndex: 'name',
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
        dataIndex: 'type',
        width: 100,
        placeholder: '-',
        render: (val: string, a) => (
          <Tag color="cyan">
            {val}
          </Tag>
        )
      },
      {
        title: '管理员',
        dataIndex: 'admin',
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
    nav(`/onebase/setting/tenant/edit?id=${123}`);
  };

  return (
    <div className={styles.tenantPage}>
      <div className={styles.userInfo}>
        <Row justify="space-between" align="center" style={{ marginBottom: 24 }}>
          {/* 左侧头像与姓名 */}
          <Col flex="auto">
            <Space align="center">
              <Avatar
                className={styles.avatar}
                size={80}
                shape="circle"
                style={{ border: '1px solid #f0f0f0' }}
                image="https://cdn.example.com/avatar-wsq.jpg"
              />
              <div>
                <div className={styles.userTop}>
                  <Title className={styles.username} heading={6}>
                    王少青
                  </Title>
                  <Tag className={styles.userTag} color="arcoblue" size="small">
                    主管
                  </Tag>
                </div>
                <Text className={styles.userRole} type="secondary">角色：产品设计</Text>
              </div>
            </Space>
          </Col>

          {/* 右上角编辑按钮 */}
          <Col flex="none">
            <Button type="secondary" onClick={handleGoEditPage}>编辑</Button>
          </Col>
        </Row>

        {/* 下方详细信息区 */}
        <Row gutter={[0, 12]} align='center' style={{ paddingLeft: 100 }}>
          {/* 第一行 */}
          <Col span={8}>
            <Row gutter={8}>
              <Col flex="80px">
                <Text type="secondary">账号</Text>
              </Col>
              <Col flex="auto">
                <Text>wangshaoqing</Text>
              </Col>
            </Row>
          </Col>

          <Col span={8}>
            <Row gutter={8}>
              <Col flex="80px">
                <Text type="secondary">手机号</Text>
              </Col>
              <Col flex="auto">
                <Text>137 0193 5734</Text>
              </Col>
            </Row>
          </Col>

          <Col span={8}>
            <Row gutter={8}>
              <Col flex="80px">
                <Text type="secondary">账号状态</Text>
              </Col>
              <Col flex="auto">
                <Text>正常</Text>
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
                <Text>湖北交通行业空间 / 科创中心</Text>
              </Col>
            </Row>
          </Col>

          <Col span={8}>
            <Row gutter={8}>
              <Col flex="80px">
                <Text type="secondary">邮箱</Text>
              </Col>
              <Col flex="auto">
                <Text>wangshaoqing@cmsr.chinamobile.com</Text>
              </Col>
            </Row>
          </Col>

          <Col span={8}>
            <Row gutter={8}>
              <Col flex="80px">
                <Text type="secondary">OneID</Text>
              </Col>
              <Col flex="auto">
                <Text>123566424512</Text>
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
        <Tabs className={styles.createTabs}>
          <TabPane key='tab1' title='我创建的企业'>
            <Table
              rowKey="id"
              hover
              columns={getColumns(handleEdit)}
              data={data}
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
          <TabPane key='tab2' title='我创建的应用'>
            <div style={{ overflow: 'auto' }}>
              <Space direction="vertical" size={16} style={{ width: '100%', height: '100%', overflow: 'auto' }}>
                {appData.map((item, index) => (
                  <Card
                    key={index}
                    bordered={false}
                    style={{
                      borderRadius: 16,
                      boxShadow: '0 1px 3px rgba(0,0,0,0.05)',
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
                      <Col flex="auto">
                        <Space direction="vertical" size={4}>
                          <Title heading={6} style={{ margin: 0 }}>
                            {item.name}
                          </Title>
                          <Text type="secondary" style={{ lineHeight: 1.6 }}>
                            {item.desc}
                          </Text>
                        </Space>
                      </Col>
                    </Row>
                  </Card>
                ))}
              </Space>
            </div>

          </TabPane>
        </Tabs>
      </PlaceholderPanel>

    </div>
  );
};

export default TenantPage;
