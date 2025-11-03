import { Button, Message, Space, Tag } from "@arco-design/web-react";
import { useTableData } from "../../hooks/useTable";
import { CommonTable } from "../table/commonTable";
import { TopHeader } from "../topHeader";
import styles from "./authorizedApp.module.less"
import { forwardRef, useImperativeHandle } from "react";
import type { IAuthorizedAppProps, AuthorizedAppRef } from "../../types/appItem";


export const AuthorizedApp = forwardRef<AuthorizedAppRef, IAuthorizedAppProps>(
  ({ setAddAppModalVisible, onEdit, className}, ref) => {
    // 模拟表格数据（5条示例数据）
    const initialAppData = Array(5).fill().map((_, index) => ({
        key: index + 1,
        appName: "CustomerRM_1c",
        appId: 'CustomerRM_1c',
        version: 'v1.2.3',
        effectTime: '2025-03-29 12:46:21',
        expireTime: '2025-03-29 12:46:21',
    }));
    const {
        displayData,
        currentPage,
        setSearchValue,
        setCurrentPage,
        getEditItem,
        removeItem,
        addItem
    } = useTableData(initialAppData);

    useImperativeHandle(ref, () => ({
      addNewApp: (newData) => {
        addItem(newData);
      }
    }));

    // 处理删除
    const handleRemove = (key: string | number) => {
        removeItem(key);
        Message.success('移除成功');
    };

    //处理编辑
    const handleEditAuthorizedApp = (key: string | number) => {
        const item = getEditItem(key);
        onEdit(item?.appName ? item.appName : "");
    };

    //点击创建应用打开modal
    const handleAddApplication = () => {
        setAddAppModalVisible(true);
    }

    const columns = [
        {
            title: '应用名称',
            dataIndex: 'appName',
            width: 180,
            render: (text: string) => (
                <Space size={12} align="center">{text}</Space>
            ),
        },
        {
            title: '应用ID',
            dataIndex: 'appId',
            width: 180,
        },
        {
            title: '版本号',
            dataIndex: 'version',
            width: 100,
            render: (text: string) => (
                <Tag color="gray" size="small">{text}</Tag>
            ),
        },
        {
            title: '授权启效时间',
            dataIndex: 'effectTime',
            width: 200,
        },
        {
            title: '过期时间',
            dataIndex: 'expireTime',
            width: 200,
        },
        {
            title: '操作',
            width: 140,
            render: (_: any, record: any) => (
                <Space size="mini">
                    <Button type="text" onClick={() => handleEditAuthorizedApp(record)}>
                        编辑
                    </Button>
                    <Button type="text" onClick={() => handleRemove(record.key)}>
                        移除
                    </Button>
                </Space>
            ),
        },
    ];

    return (
        <div className={className ? className : styles.authorizedApp}>
            <TopHeader
                className={styles.headerSection}
                title="添加应用"
                onAdd={handleAddApplication}
                isBusiness={false}
                setSearchInputValue={setSearchValue}
            />
            <CommonTable
                data={displayData}
                columns={columns}
                pageination={{
                    sizeCanChange: true,
                    showTotal: true,
                    total: displayData.length,
                    pageSize: 5,
                    current: currentPage,
                    pageSizeChangeResetCurrent: true,
                    onChange: setCurrentPage,
                }}
            />
        </div>
    )
})