import { Button, Dropdown, Input, Menu, Space, Tag, Modal, Message, Table,Avatar, Image } from "@arco-design/web-react";
import { IconMore } from "@arco-design/web-react/icon";
import styles from "./index.module.less";
import StatusTag from "@/components/StatusTag";
import { Outlet, useLocation, useMatch, useNavigate } from "react-router-dom";
import { TopHeader } from "./components/topHeader";
import { useEffect, useMemo, useRef, useState } from "react";
import { getCorpListApi, disabledCorpApi, deleteCorpApi, getIndustryType, type corpListParams } from "@onebase/platform-center";
import { type corpApplicationListProps, type cropItem, type industryTypeOption} from "./types/appItem";
import { formatTimeYMDHMS } from '@onebase/common';
import { convertName } from "./utils";
const AvatarGroup = Avatar.Group;

const BusinessPage: React.FC = () => {
    const businessManageColumns = [
        {
            title: '企业LOGO',
            dataIndex: 'corpLogo',
            render: (data: string) => (
               <>{data? 
                <Image src={data} width={72} height={36}/>:
                <div className={styles.corpLogo}>中国移动</div> }
               </>
            )
        },
        {
            title: '企业名称',
            dataIndex: 'corpName',
        },
        {
            title: '企业编码',
            dataIndex: 'corpCode',
        },
        {
            title: '行业类型',
            dataIndex: 'industryTypeName',
            render: (industry: string) => (
                <Tag color="cyan" size="small">{industry}</Tag>
            )
        },
        {
            title: '授权应用',
            dataIndex: 'corpApplicationList',
            render: (apps: corpApplicationListProps[]) => (
              <div>{renderAuthorizedAppGroup(apps)}</div>
            )
        },
        {
            title: '管理员',
            dataIndex: 'adminName',
        },
        {
            title: '状态',
            dataIndex: 'status',
            render: (status: number) => <StatusTag status={status} />
        },
        {
            title: '创建时间',
            dataIndex: 'createTime',
            render: (timeValue: string) => (
                <div>{formatTimeYMDHMS(timeValue)}</div>
            )
        },
        {
            title: '操作',
            render: (_: any, record: any) => (
                <Space size={4}>
                    <Button size="small" type="text" onClick={handleEdit.bind(null, record, "basic")}>编辑</Button>
                    <Button size="small" type="text" onClick={handleEdit.bind(null, record, "authorized")}>应用授权</Button>
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
    const navigate = useNavigate();
    const location = useLocation();
    const inputRef = useRef(null);
    const isCreatePage = useMatch('onebase/setting/enterprise/create-enterprise');
    const [loading, setLoading] = useState<boolean>(false);
    const [editable, setEditable] = useState<boolean>(false);
    const [searchValue, setSearchValue] = useState<string>("");
    const [tableData, setTableData] = useState<cropItem[]>([]);
    const [currentId, setCurrentId] = useState<string>("");
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
        return <>
           {applicationList?.length > 0 ? 
            <div style={{display: 'flex', alignItems: 'flex-end'}}>
                    <AvatarGroup size={24}>
                        {applicationList?.map(item => {
                            return <Avatar style={{ backgroundColor: item.iconColor }}>
                                {item.iconName}
                            </Avatar>
                        })}
                    <Avatar>{applicationList.length}</Avatar>
                    </AvatarGroup>
            </div> : <></>
            }
        </>
    }

    const fetchTableDataList = async(pageNo = 1, pageSize = 10) => {
        setLoading(true);
        const params: corpListParams = {
            pageNo,
            pageSize
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
            setLoading(false)
        }
    }

    const fetchIndustryType = async() => {
        try {
            const res = await getIndustryType("industry_type");
            setIndustryOptions(res);
        }catch(error) {
            Message.error("获取行业类型列表失败");
        }
    }

    useEffect(() => {
        if(location.pathname === "/onebase/setting/enterprise") {
            if(editable) {
                setEditable(false);
            }
            fetchTableDataList();
            fetchIndustryType();
        }
    }, [location.pathname])

    const handlePageChange = (current: number, pageSize: number) => {
        fetchTableDataList(current, pageSize);
    };

    const handleEdit = (record:cropItem, activeTab: string) => {
        setEditable(true);
        setCurrentId(record.id);
        const encodedName = encodeURIComponent(record.corpName);
        navigate(`${encodedName}/${activeTab === "basic" ? "基本信息" : "授权应用"}`);
    }

    //创建企业
    const handleCreateBusiness = () => {
        navigate("create-enterprise");
    }

    const renderInput = (name: string) => {
        return (
             <div className={styles.deleteModal}>
                <div className={styles.title}>删除企业，该企业下的数据将被永久删除，请谨慎操作。</div>
                <div>
                    <div className={styles.subTitle}>如确定删除，请输入企业名称：{name}</div>
                    <Input 
                        placeholder="请输入企业名称" 
                        ref={inputRef as any}
                    />
                </div>
            </div>
        )
    }

    const handleDisabled = async(record: cropItem) => {
        if(record.status === 0) {
            const params = {id: record.id, status: 1};
            try {
                await disabledCorpApi(params);
                await fetchTableDataList(pagination.current,pagination.pageSize);
                Message.success(`启用成功`);
            }catch(error) {
                Message.error(`启用失败`);
            }
        }else {
           return  (
            Modal.confirm({
                title: `禁用企业(${record.corpName})? `,
                content: '禁用后企业用户无法登录，再次启用时企业可恢复正常使用',
                okButtonProps: {
                    status: 'danger',
                },
                onOk: async () => {
                    const params = {id: record.id, status: 0};
                    try {
                        await disabledCorpApi(params);
                        await fetchTableDataList(pagination.current,pagination.pageSize);
                        Message.success(`禁用成功`);
                    }catch(error) {
                        Message.error(`禁用失败`);
                    }
                }
            })
           )
        }
    };

    // 删除
    const handleDelete = (record: cropItem) => {
        Modal.confirm({
            title: `确认要删除企业(${record.corpName})吗？`,
            okButtonProps: {
                status: 'danger',
            },
            content:renderInput(record.corpName),
            onOk: async () => {
                const value = (inputRef as any)?.current?.dom?.value;
                if(!value) {
                    Message.error("请输入内容");
                    return false;
                }
                if (value !== record.corpName) {
                    Message.error('输入的企业名称不一致，请重新输入');
                    return false; 
                }
                try {
                    await deleteCorpApi(record.id);
                    await fetchTableDataList(pagination.current,pagination.pageSize);
                    Message.success('删除成功');
                } catch (error) {
                    Message.error('删除失败，请重试');
                }
            }
        });
    };

    const handleSearchChange = (searchValue: string) => {
        setSearchValue(searchValue);
    }

    const displayData = useMemo(()=>{
        if (!searchValue.trim()) return tableData
        const lowerSearch = searchValue.toLowerCase();
        return tableData.filter(item => 
            item.corpName?.toLowerCase().includes(lowerSearch) || 
            item.corpCode?.toLowerCase().includes(lowerSearch)
        )
    },[tableData, searchValue])

    // 操作列下拉菜单
    const actionMenu = (record: cropItem) => (
        <Menu>
            <Menu.Item key="disable" onClick={() => handleDisabled(record)}>{convertName(record.status)}</Menu.Item>
            <Menu.Item key="delete" onClick={() => handleDelete(record)}>删除</Menu.Item>
        </Menu>
    );

    const renderContent = () => {
        if (editable) {
            return <Outlet context={{industryOptions, currentId}}/>
        }
        if (isCreatePage) {
            return <Outlet context={{industryOptions, currentId }}/>
        }
        return (
            <div className={styles.businessManagement}>
                <TopHeader title="创建企业" onAdd={handleCreateBusiness} setSearchInputValue={handleSearchChange} />
                <Table
                    loading={loading}
                    columns={businessManageColumns}
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
        )
    }

    return (
        <div>
            {renderContent()}
        </div>
    )
}

export default BusinessPage;