import { Tabs, Typography } from '@arco-design/web-react';
import { useTranslation } from 'react-i18next';
import Attributes from './Attributes';
import TabTitle from './components/TabTitle';
import Data from './Data';
import styles from './index.module.less';

const TabPane = Tabs.TabPane;

/**
 * 配置面板组件
 * @param props.cpID 组件唯一ID
 */
interface MaterialConfigerProps {
    cpID: string;
}


const MaterialConfiger = ({ cpID }: MaterialConfigerProps) => {
    const { t } = useTranslation();

    return (
        <div className={styles.configs}>
            <Tabs
                defaultActiveTab="attributes"
                type="line"
                inkBarSize={{ width: '100%', height: 3 }}
                size='default'
            >
                <TabPane key="attributes"
                    title={<TabTitle title={t("formEditor.attribute")}/>}
                >
                    <Typography.Paragraph>
                        <Attributes cpID={cpID} />
                    </Typography.Paragraph>
                </TabPane>
                <TabPane key="data"
                    title={<TabTitle title={t("formEditor.data")}/>}
                >
                    <Typography.Paragraph>
                        <Data />
                    </Typography.Paragraph>
                </TabPane>
            </Tabs>
        </div>
    );
};

export default MaterialConfiger;
