import { Button, Descriptions, Result, Space } from "@arco-design/web-react"
import styles from "./authorizedApp.module.less";

interface ICreateSuccessProps {
    basicInfoForm: any,
    setCurrentStep: (value: number) => void;
    setAddAppModalVisible: (visible: boolean) =>void;
}

export const CreateSuccess:React.FC<ICreateSuccessProps> = ({basicInfoForm, setCurrentStep, setAddAppModalVisible}) => {
    const desData = [
        { label: "管理员账号", value: basicInfoForm.getFieldValue("") },
        { label: "初始密码", value: basicInfoForm.getFieldValue("") }
    ]

    //点击创建企业button
    const handleCreate = () => {
        setCurrentStep(3);
        setAddAppModalVisible(true);
    }

    //创建企业的第四步的返回button
    const handleGoBack = () => {
        setCurrentStep(3);
    }

    return (
        <Result
            className={styles.confirmInfo}
            status='success'
            title="创建完成"
            subTitle='新增企业成功'
            extra={[
                <Descriptions className={styles.desContent} data={desData} column={1} />,
                <Space className={styles.confirmFooter}>
                    <Button key='again' type="secondary" onClick={handleGoBack}>
                        返回列表
                    </Button>
                    <Button key='back' type='primary' onClick={handleCreate}>
                        再建一个
                    </Button>
                </Space>
            ]}
        >
        </Result>
    )
}