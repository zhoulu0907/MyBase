import { Button, Space, Tag } from "@arco-design/web-react";
import { CommonTable } from "../table/commonTable";
import { TopHeader } from "../topHeader";
import styles from "./authorizedApp.module.less"
import type { IAuthorizedAppProps } from "../../types/appItem";


export const AuthorizedApp:React.FC<IAuthorizedAppProps> = ({className, loading, tableData,pageination, setAddAppModalVisible, onChange, onSearch }) => {
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
        <div className={className ? className: styles.authorizedApp}>
            <TopHeader
                title="添加应用"
                onAdd={handleAddApplication}
                isBusiness={false}
                setSearchInputValue={onSearch} 
            />
            <CommonTable
                loading={loading}
                data={tableData}
                columns={columns}
                pageination={pageination}
                onChange={onChange}
            />
        </div>
    )
}