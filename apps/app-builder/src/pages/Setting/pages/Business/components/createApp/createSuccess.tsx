import { Button, Result, Space, Typography, Grid} from "@arco-design/web-react"
import styles from "./createSuccess.module.less";
import type { successData } from "../../types/appItem";
import { useNavigate } from "react-router-dom";
const Row = Grid.Row;
const Col = Grid.Col;

interface ICreateSuccessProps {
    successData: successData | null;
    setCurrentStep: (value: number) => void;
    setAddAppModalVisible: (visible: boolean) =>void;
}

export const CreateSuccess:React.FC<ICreateSuccessProps> = ({ successData, setCurrentStep, setAddAppModalVisible}) => {
    const navigate = useNavigate();
    //点击创建企业button
    const handleCreate = () => {
        setCurrentStep(1);
        setAddAppModalVisible(true);
    }

    //创建企业的第四步的返回button
    const handleGoBack = () => {
        navigate("/onebase/setting/enterprise");
    }

    return (
        <Result
            className={styles.confirmInfo}
            status='success'
            title="创建完成"
            subTitle='新增企业成功'
            extra={[
                <Space direction="vertical" className={styles.notification}>
                     <Row className={styles.section}>
                        <Col span={5}><Typography.Text type='secondary' className={styles.title}>管理员账号</Typography.Text></Col>
                        <Col span={19}><Typography>{successData?.username || "" }</Typography></Col>
                    </Row>
                    <Row className={styles.section}>
                        <Col span={5}><Typography.Text type='secondary' className={styles.title}>初始密码</Typography.Text></Col>
                        <Col span={19}><Typography.Paragraph copyable className={styles.link}>{successData?.password || ""}</Typography.Paragraph></Col>
                    </Row>
                   <Row>
                        <Typography.Text type="error">注意：当前密码只显示一次，请注意保存</Typography.Text>
                   </Row>
                </Space>,
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