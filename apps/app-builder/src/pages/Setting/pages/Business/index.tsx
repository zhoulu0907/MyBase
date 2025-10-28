import { Button, Dropdown, Input, Link, Menu, Select, Space, Table, Tag, Modal, Message } from "@arco-design/web-react";
import { IconCopy, IconMore, IconPlus } from "@arco-design/web-react/icon";
import styles from "./index.module.less";
import StatusTag from "@/components/StatusTag";
import { StatusEnum } from "@onebase/app";

const BusinessPage: React.FC = () => {
    const businessManageColumns = [
    {
        title: '企业LOGO',
        dataIndex: 'logo',
        render: () => (
        <span>logo</span>
        )
    },
    {
        title: '企业名称',
        dataIndex: 'name',
    },
    {
        title: '企业ID',
        dataIndex: 'id',
    },
    {
        title: '行业类型',
        dataIndex: 'industry',
        render: (industry: string) => (
        <Tag color="cyan" size="small">{industry}</Tag>
        )
    },
    {
        title: '授权应用',
        dataIndex: 'apps',
        render: (apps: string) => (
        <div>{apps}</div>
        )
    },
    {
        title: '管理员',
        dataIndex: 'admin',
    },
    {
        title: '状态',
        dataIndex: 'status',
        render: (status: string) => <StatusTag status={status} />
    },
    {
        title: '创建时间',
        dataIndex: 'createTime',
    },
    {
        title: '操作',
        render: (_:any, record: any) => (
        <Space size={4}>
            <Button size="small" type="text">编辑</Button>
            <Button size="small" type="text">应用授权</Button>
            <Dropdown
            trigger="click" 
            droplist={actionMenu(record)}
            position="bl"
            >
            <Button size="small" type="text" icon={<IconMore />} />
            </Dropdown>
        </Space>
        )
    }
    ];
    // 模拟数据
    const businessManagementData = [1,2,3,4,5].map((_, index) => ({
    key: index,
    logo: '',
    name: '玩贝斯软件公司',
    id: 'com_onebase',
    industry: '大交通',
    apps: "1111",
    admin: '风月',
    status: 1,
    createTime: '2025-03-29 12:46:21'
    }));


  // 禁用用户，需确认
  const handleDisabled = (record: any) => {
    Modal.confirm({
      title: `禁用企业(${record.name})？`,
      content: '禁用后企业用户无法登录，再次启用时企业可恢复正常使用',
      okButtonProps: {
       status: 'danger',
      },
      onOk: async () => {
        // await updateUserStatus(record.id, newStatus);
        Message.success("禁用成功");
        // getUserList();
      }
    });
  };

  // 删除
  const handleDelete = (record: any) => {
    Modal.confirm({
      title: `确认要删除企业(${record.name})吗？`,
      okButtonProps: {
       status: 'danger',
      },
      content: <div className={styles.deleteModal}>
        <div className={styles.title}>删除企业，该企业下的数据将被永久删除，请谨慎操作。</div>
        <div>
            <div className={styles.subTitle}>如确定删除，请输入企业名称：玩贝斯软件公司</div>
            <Input placeholder="请输入企业名称"/>
        </div>
      </div>,
      onOk: async () => {
        // await deleteUser(record.id);
        Message.success('删除成功');
        // onRefreshDept();
        // getUserList();
      }
    });
  };

    // 操作列下拉菜单
    const actionMenu = (record:any) => (
    <Menu>
        <Menu.Item key="disable" onClick={() => handleDisabled(record)}>禁用</Menu.Item>
        <Menu.Item key="delete" onClick={() => handleDelete(record)}>删除</Menu.Item>
    </Menu>
    );

    return (
        <div className={styles.businessManagement}>
            <div className={styles.topHeader}>
                {/*顶部左侧 新建企业*/}
                <div className={styles.createBusiness}>
                    <Button type="primary" icon={<IconPlus />}>创建企业</Button>
                    <div className={styles.linkContent}>
                        <span>企业用户登录地址:</span>
                        <Link href='#' className={styles.link}>www.onebase.com/enterprise </Link>
                        <IconCopy className={styles.copyIcon} fontSize={18}/>
                    </div>
                </div>
                {/* 顶部右侧 搜索*/}
                <div className={styles.searchContent}>
                    <Select
                        className={styles.selectStatus}
                        defaultValue="all" 
                        options={[{ label: '全部状态', value: 'all' }]}
                    />
                    <Input.Search allowClear placeholder='输入企业名称' className={styles.searchInput}/>
                </div>
            </div>
            {/* table */}
            <Table 
            border={false} 
            columns={businessManageColumns} 
            data={businessManagementData} 
            pagination={{
                sizeCanChange: true,
                showTotal: true,
                total: 100,
                pageSize: 10,
                current: 1,
                pageSizeChangeResetCurrent: true
            }}
           />
        </div>
    )
}

export default BusinessPage;