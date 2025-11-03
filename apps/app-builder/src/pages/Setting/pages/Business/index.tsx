import { Button, Dropdown, Input, Menu, Space, Tag, Modal, Message } from "@arco-design/web-react";
import { IconMore } from "@arco-design/web-react/icon";
import styles from "./index.module.less";
import StatusTag from "@/components/StatusTag";
import { Outlet, useMatch, useNavigate } from "react-router-dom";
import { CommonTable } from "./components/table/commonTable";
import { TopHeader } from "./components/topHeader";
import { useEffect, useMemo, useState } from "react";

interface businessTableItem {
    key: number,
    logo: string,
    name: string,
    id: string,
    industry: string,
    apps: string,
    admin: string,
    status: number,
    createTime: string
}
interface IBusinessPageProps {

}

const BusinessPage: React.FC<IBusinessPageProps> = () => {
    const navigate = useNavigate();
    const [editable, setEditable] = useState<boolean>(false);
    const [tableData, setTableData] = useState<businessTableItem[]>([]);
    const [searchInputValue, setSearchInputValue] = useState<string>("");
    const isCreatePage = useMatch('onebase/setting/business/create-business');

    const handleEdit = (name: string, activeTab: string) => {
        setEditable(true);
        const encodedName = encodeURIComponent(name);
        navigate(`${encodedName}/${activeTab === "basic" ? "基本信息" : "授权应用"}`);
    }

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
            render: (_: any, record: any) => (
                <Space size={4}>
                    <Button size="small" type="text" onClick={handleEdit.bind(null, record.name,"basic")}>编辑</Button>
                    <Button size="small" type="text" onClick={handleEdit.bind(null, record.name ,"authorized")}>应用授权</Button>
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
    const initialBusinessManagementData = [1, 2, 3, 4, 5].map((_, index) => ({
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

    useEffect(() => {
        setTableData(initialBusinessManagementData)
    }, [])

    const displayBusinessData = useMemo(() => {
        if (!searchInputValue.trim()) {
        return tableData;
        }
        // 有搜索值时，过滤原始数据
        const lowerKey = searchInputValue.toLowerCase();
        return tableData.filter(item => 
        item.name.toLowerCase().includes(lowerKey)
        );
    }, [tableData, searchInputValue]); 

    //创建企业
    const handleCreateBusiness = () => {
        navigate("create-business");
    }

    // 禁用用户，需确认
    const handleDisabled = (record: any) => {
        Modal.confirm({
            title: `禁用企业(${record.name})? `,
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
                    <Input placeholder="请输入企业名称" />
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
    const actionMenu = (record: any) => (
        <Menu>
            <Menu.Item key="disable" onClick={() => handleDisabled(record)}>禁用</Menu.Item>
            <Menu.Item key="delete" onClick={() => handleDelete(record)}>删除</Menu.Item>
        </Menu>
    );

    const renderContent = () => {
        if(editable) {
            return <Outlet />
        }
        if(isCreatePage) {
            return <Outlet />
        }
        return (
            <div className={styles.businessManagement}>
                {/* 头部渲染 */}
                <TopHeader title="创建企业" onAdd={handleCreateBusiness} setSearchInputValue={setSearchInputValue}/>
                <CommonTable 
                    data={displayBusinessData}
                    columns={businessManageColumns}
                    pageination={{
                        sizeCanChange: true,
                        showTotal: true,
                        total: displayBusinessData.length,
                        pageSize: 10,
                        current: 1,
                        pageSizeChangeResetCurrent: true
                    }}
                />
            </div> 
        )
    }

    return (
        <div>
            {renderContent()}
        </div>
    )
}

export default BusinessPage;