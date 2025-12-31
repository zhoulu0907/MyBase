import StatusTag from '@/components/StatusTag';
import {
  Avatar,
  Button,
  Dropdown,
  Image,
  Input,
  Menu,
  Message,
  Modal,
  Space,
  Table,
  Tag
} from '@arco-design/web-react';
import { IconMore } from '@arco-design/web-react/icon';
import { formatTimeYMDHMS } from '@onebase/common';
import {
  deleteCorpApi,
  disabledCorpApi,
  getCorpListApi,
  getDictDataListByType,
  getFileUrlById,
  type corpListParams
} from '@onebase/platform-center';
import { useEffect, useMemo, useRef, useState } from 'react';
import { Outlet, useLocation, useMatch, useNavigate, useParams } from 'react-router-dom';
import { TopHeader } from './components/topHeader';
import styles from './index.module.less';
import { type corpApplicationListProps, type cropItem, type industryTypeOption } from './types/appItem';
import { convertName } from './utils';
import type { ColumnProps } from '@arco-design/web-react/es/Table';
import { displayCorpLogo } from '@/utils';
const AvatarGroup = Avatar.Group;

const BusinessPage: React.FC = () => {
  const { tenantId } = useParams();

  const businessManageColumns = [
    {
      title: '企业LOGO',
      dataIndex: 'corpLogo',
      render: (data: string, record: any) => (
        <>
          {data ? (
            <Image src={getFileUrlById(data)} width={72} height={36} />
          ) : (
            <div className={styles.corpLogo}>{displayCorpLogo(record?.corpName)}</div>
          )}
        </>
      )
    },
    {
      title: '企业名称',
      dataIndex: 'corpName'
    },
    {
      title: '企业编码',
      dataIndex: 'corpCode'
    },
    {
      title: '行业类型',
      dataIndex: 'industryTypeName',
      render: (industry: string) => (
        <Tag color="cyan" size="small">
          {industry}
        </Tag>
      )
    },
    {
      title: '授权应用',
      dataIndex: 'corpApplicationList',
      render: (apps: corpApplicationListProps[]) => <div>{renderAuthorizedAppGroup(apps)}</div>
    },
    {
      title: '管理员',
      dataIndex: 'adminName'
    },
    {
      title: '手机号',
      dataIndex: 'adminMobile'
    },
    {
      title: '状态',
      dataIndex: 'status',
      render: (status: number) => <StatusTag status={status} />
    },
    {
      title: '创建时间',
      dataIndex: 'createTime',
      render: (timeValue: string) => <div>{formatTimeYMDHMS(timeValue)}</div>
    },
    {
      title: '操作',
      render: (_: any, record: any) => (
        <Space size={4}>
          <Button size="small" type="text" onClick={handleEdit.bind(null, record, 'basic')}>
            编辑
          </Button>
          <Button size="small" type="text" onClick={onReviewInfo.bind(null, record, 'basic')}>
            查看
          </Button>
          <Button size="small" type="text" onClick={handleEdit.bind(null, record, 'authorized')}>
            应用授权
          </Button>
          <Dropdown trigger="click" droplist={actionMenu(record)} position="bl">
            <Button size="small" type="text" icon={<IconMore />} />
          </Dropdown>
        </Space>
      )
    }
  ];
  const navigate = useNavigate();
  const location = useLocation();
  const inputRef = useRef(null);
  const isCreatePage = useMatch('onebase/:tenantId/setting/enterprise/create-enterprise');
  const [loading, setLoading] = useState<boolean>(false);
  const [editable, setEditable] = useState<boolean>(false);
  const [displayInfo, setDisplayInfo] = useState<boolean>(false);
  const [searchValue, setSearchValue] = useState<string>('');
  const [tableData, setTableData] = useState<cropItem[]>([]);
  const [currentId, setCurrentId] = useState<string>('');
  const [industryOptions, setIndustryOptions] = useState<industryTypeOption[]>([]);
  const [pagination, setPagination] = useState({
    showTotal: true,
    total: 0,
    pageSize: 10,
    current: 1,
    sizeCanChange: true,
    pageSizeChangeResetCurrent: true
  });

  function renderAuthorizedAppGroup(applicationList: corpApplicationListProps[]) {
    return (
      <>
        {applicationList?.length > 0 ? (
          <div style={{ display: 'flex', alignItems: 'flex-end' }}>
            <AvatarGroup
              size={24}
              maxCount={5}
              maxPopoverTriggerProps={{
                disabled: true
              }}
            >
              {applicationList?.map((item, index) => {
                return (
                  <Avatar key={index} style={{ backgroundColor: item.iconColor }}>
                    {item.iconName}
                  </Avatar>
                );
              })}
            </AvatarGroup>
          </div>
        ) : (
          <></>
        )}
      </>
    );
  }

  const fetchTableDataList = async (pageNo = 1, pageSize = 10, status = null) => {
    setLoading(true);
    const params: corpListParams = {
      pageNo,
      pageSize,
      status
    };
    try {
      const res = await getCorpListApi(params);
      if (res && Array.isArray(res.list)) {
        setTableData(res.list);
        setPagination((prev) => ({ ...prev, current: pageNo, pageSize, total: res.total || 0 }));
      } else {
        console.warn('Invalid response format:', res);
      }
    } catch (error: any) {
      Message.error(error.message);
    } finally {
      setLoading(false);
    }
  };

  const fetchIndustryType = async () => {
    try {
      const res = await getDictDataListByType('industry_type');
      setIndustryOptions(res);
    } catch (error) {
      Message.error('获取行业类型列表失败');
    }
  };

  useEffect(() => {
    if (displayInfo) {
      setDisplayInfo(false);
    }
    fetchTableDataList();
    fetchIndustryType();
  }, []);

  const handlePageChange = (current: number, pageSize: number) => {
    fetchTableDataList(current, pageSize);
  };

  const navigateInfo = (record: cropItem, activeTab: string) => {
    setCurrentId(record.id);
    setDisplayInfo(true); //展示基本信息
    const encodedName = encodeURIComponent(record.corpName);
    navigate(`${encodedName}/${activeTab === 'basic' ? '基本信息' : '授权应用'}`);
  };

  const onReviewInfo = (record: cropItem, activeTab: string) => {
    setEditable(false); //查看是false
    navigateInfo(record, activeTab);
  };

  const handleEdit = (record: cropItem, activeTab: string) => {
    setEditable(true); //编辑是true
    navigateInfo(record, activeTab);
  };

  const handleChangeStatus = (value: number | null) => {
    fetchTableDataList(pagination.current, pagination.pageSize, value as any);
  };

  //创建企业
  const handleCreateBusiness = () => {
    navigate('create-enterprise');
  };

  const renderInput = (name: string) => {
    return (
      <div className={styles.deleteModal}>
        <div className={styles.title}>删除企业，该企业下的数据将被永久删除，请谨慎操作。</div>
        <div>
          <div className={styles.subTitle}>如确定删除，请输入企业名称：{name}</div>
          <Input placeholder="请输入企业名称" ref={inputRef as any} />
        </div>
      </div>
    );
  };

  const handleDisabled = async (record: cropItem) => {
    if (record.status === 0) {
      const params = { id: record.id, status: 1 };
      try {
        await disabledCorpApi(params);
        await fetchTableDataList(pagination.current, pagination.pageSize);
        Message.success(`启用成功`);
      } catch (error) {
        Message.error(`启用失败`);
      }
    } else {
      return Modal.confirm({
        title: `禁用企业(${record.corpName})? `,
        content: '禁用后企业用户无法登录，再次启用时企业可恢复正常使用',
        okButtonProps: {
          status: 'danger'
        },
        onOk: async () => {
          const params = { id: record.id, status: 0 };
          try {
            await disabledCorpApi(params);
            await fetchTableDataList(pagination.current, pagination.pageSize);
            Message.success(`禁用成功`);
          } catch (error) {
            Message.error(`禁用失败`);
          }
        }
      });
    }
  };

  // 删除
  const handleDelete = (record: cropItem) => {
    Modal.confirm({
      title: `确认要删除企业(${record.corpName})吗？`,
      okButtonProps: {
        status: 'danger'
      },
      content: renderInput(record.corpName),
      onOk: async () => {
        const value = (inputRef as any)?.current?.dom?.value;
        if (!value) {
          Message.error('请输入内容');
          return false;
        }
        if (value !== record.corpName) {
          Message.error('输入的企业名称不一致，请重新输入');
          return false;
        }
        try {
          await deleteCorpApi(record.id);
          await fetchTableDataList(pagination.current, pagination.pageSize);
          Message.success('删除成功');
        } catch (error) {
          Message.error('删除失败，请重试');
        }
      }
    });
  };

  const handleSearchChange = (searchValue: string) => {
    setSearchValue(searchValue);
  };

  const displayData = useMemo(() => {
    if (!searchValue.trim()) return tableData;
    const lowerSearch = searchValue.toLowerCase();
    return tableData.filter(
      (item) => item.corpName?.toLowerCase().includes(lowerSearch) || item.corpCode?.toLowerCase().includes(lowerSearch)
    );
  }, [tableData, searchValue]);

  // 操作列下拉菜单
  const actionMenu = (record: cropItem) => (
    <Menu>
      <Menu.Item key="disable" onClick={() => handleDisabled(record)}>
        {convertName(record.status)}
      </Menu.Item>
      <Menu.Item key="delete" onClick={() => handleDelete(record)}>
        删除
      </Menu.Item>
    </Menu>
  );

  const renderContent = () => {
    if (displayInfo) {
      return <Outlet context={{ industryOptions, currentId, editable }} />;
    }
    if (isCreatePage) {
      return <Outlet context={{ industryOptions, currentId }} />;
    }
    return (
      <div className={styles.businessManagement}>
        <TopHeader
          title="创建企业"
          onchange={handleChangeStatus}
          onAdd={handleCreateBusiness}
          setSearchInputValue={handleSearchChange}
        />
        <Table
          loading={loading}
          columns={businessManageColumns as ColumnProps<cropItem>[]}
          data={displayData}
          pagination={{
            ...pagination,
            showTotal: true,
            onChange: handlePageChange
          }}
          rowKey="id"
          border={false}
        />
      </div>
    );
  };

  return <div>{renderContent()}</div>;
};

export default BusinessPage;
