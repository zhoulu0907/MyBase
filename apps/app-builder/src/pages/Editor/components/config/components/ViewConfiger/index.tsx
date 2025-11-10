import { useI18n } from '@/hooks/useI18n';
import { Form, Tabs } from '@arco-design/web-react';
import { useSignals } from '@preact/signals-react/runtime';
import TabTitle from '../TabTitle';
import ViewAttributes from './Attributes';
import styles from './index.module.less';

const { useForm } = Form;

const TabPane = Tabs.TabPane;
/**
 * 视图配置面板
 */
interface ViewConfigerProps {}

const ViewConfiger = ({}: ViewConfigerProps) => {
  useSignals();

  const { t } = useI18n();

  return (
    <div className={styles.configs}>
      <div className={styles.title}>视图</div>
      <div className={styles.viewInfo}>
        <Tabs defaultActiveTab="attributes" type="line" size="default">
          <TabPane key="attributes" title={<TabTitle title={t('editor.attribute')} />}>
            <ViewAttributes />
          </TabPane>
          <TabPane key="advanced" title={<TabTitle title={t('editor.advanced')} />}></TabPane>
        </Tabs>
      </div>
    </div>
  );
};

export default ViewConfiger;
