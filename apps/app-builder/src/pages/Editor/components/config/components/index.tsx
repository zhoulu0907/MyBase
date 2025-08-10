import { useI18n } from '@/hooks/useI18n';
import { Tabs, Typography } from '@arco-design/web-react';
import Advanced from './Advanced';
import Attributes from './Attributes';
import TabTitle from './components/TabTitle';
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
  const { t } = useI18n();

  return (
    <div className={styles.configs}>
      <Tabs defaultActiveTab="attributes" type="line" size="default">
        <TabPane key="attributes" title={<TabTitle title={t('formEditor.attribute')} />}>
          <Typography.Paragraph>
            <Attributes cpID={cpID} />
          </Typography.Paragraph>
        </TabPane>
        <TabPane key="advanced" title={<TabTitle title={t('formEditor.advanced')} />}>
          <Typography.Paragraph>
            <Advanced />
          </Typography.Paragraph>
        </TabPane>
      </Tabs>
    </div>
  );
};

export default MaterialConfiger;
