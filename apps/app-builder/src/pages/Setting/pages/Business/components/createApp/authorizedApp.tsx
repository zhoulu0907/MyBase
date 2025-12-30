import { Button, Message, Space, Tag } from "@arco-design/web-react";
import { CommonTable } from "../table/commonTable";
import { TopHeader } from "../topHeader";
import styles from "./authorizedApp.module.less"
import type { AppItem, authorizedAppList, authorizedTimeGroup, IAuthorizedAppProps, OutletContextType } from "../../types/appItem";
import {formatTimeYMDHMS} from "@onebase/common";
import { type CorpAppParams, type updateAppParams, getCorpAppSimpleListApi } from "@onebase/platform-center";
import EditAuthorizedTime from "../modal/editAuthorizedTime";
import { useState } from "react";
import { useLocation, useOutletContext } from "react-router-dom";
import { CreateAppModal } from "../modal/createAppModal";


export const AuthorizedApp:React.FC<IAuthorizedAppProps> = ({
    className, loading, tableData, pageination, visible,addAppModalVisible, setAddAppModalVisible,
    onChange, onSearch,onUpdateTime, setVisible, onRemoveAuthorizedApp, onSubmit
 }) => {
    const location = useLocation();
    const { currentId } = useOutletContext<OutletContextType>();
    const [authorizedAppItem, setAuthorizedAppItem] = useState<AppItem | null>(null);
    const [dropdownList, setDropdownList] = useState<authorizedAppList[]>([]);

    const getApplicationIdResult = async() => {
        try{
            const res: authorizedAppList[]= await getCorpAppSimpleListApi(currentId);
            setDropdownList(res ? res : [])
        }catch(error) {
            Message.error("获取列表失败")
        }
    }

    //点击创建应用打开modal
    const handleAddApplication = async() => {
        if(!location.pathname?.includes('/create-enterprise')) {
            await getApplicationIdResult();
        }
        setAddAppModalVisible(true);
    }

    //点击modal的取消按钮
    const handleCloseModal = () => {
        setAddAppModalVisible(false);
    }

    //编辑时间
    const handleSubmitTime = (values: authorizedTimeGroup) => {
        const params: updateAppParams = {
            id: authorizedAppItem?.id,
            corpId: currentId,
            applicationId: authorizedAppItem?.applicationId,
            authorizationTime: values.appTime?.authorizationTime || "",
            expiresTime: values.appTime?.expiresTime || ""

        }
        onUpdateTime(params);
    }
    
    //弹出编辑时间的modal
    const handleEditAuthorizedTime = (record: AppItem) => {
        setVisible(true);
        setAuthorizedAppItem(record);
    }

    const handleRemove  = async(id: string) => {
        onRemoveAuthorizedApp(id);
    }

    // 提交新应用（弹窗确认后调用）
    const handleAddSubmit = async(newAppData: any) => {
        const newData :CorpAppParams = {
            corpId: currentId,
            applicationIdList: newAppData.applicationIdList,
            authorizationTime: newAppData.appTime?.authorizationTime,
            expiresTime: newAppData.appTime?.expiresTime
        }
        onSubmit(newData);
    };  

    const columns = [
        {
            title: '应用名称',
            dataIndex: 'applicationName',
            width: 180,
            render: (text: string) => (
                <Space size={12} align="center">{text}</Space>
            ),
        },
        {
            title: '应用ID',
            dataIndex: 'applicationCode',
            width: 180,
        },
        {
            title: '版本号',
            dataIndex: 'versionNumber',
            width: 100,
            render: (text: string) => (
                <Tag color="gray" size="small">{text}</Tag>
            ),
        },
        {
            title: '授权起效时间',
            dataIndex: 'authorizationTime',
            width: 200,
            render: (timeValue: string) => (
                <div>{formatTimeYMDHMS(timeValue)}</div>
            )
        },
        {
            title: '过期时间',
            dataIndex: 'expiresTime',
            width: 200,
            render: (timeValue: string) => (
                <div>{formatTimeYMDHMS(timeValue)}</div>
            )
        },
        {
            title: '操作',
            width: 140,
            render: (_: any, record: any) => (
                <Space size="mini">
                    <Button type="text" onClick={() => handleEditAuthorizedTime(record)}>
                        编辑
                    </Button>
                    <Button type="text" onClick={() => handleRemove(record.id)}>
                        移除
                    </Button>
                </Space>
            ),
        },
    ];

    return (
        <>
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
        {/* 编辑授权应用 */}
        <EditAuthorizedTime visible={visible} setVisible={setVisible} onUpdateData={handleSubmitTime} initialFormData={authorizedAppItem}/>
        {/* 创建授权应用modal */}
        <CreateAppModal dropdownList={dropdownList} visible={addAppModalVisible} tableData={tableData} onCloseAppModal={handleCloseModal} onSaveAppData={handleAddSubmit} />
        </>
    )
}