import { ICON_Map_By_Type } from '@/components/MaterialCard/icons';
import { useI18n } from '@/hooks/useI18n';
import { Tabs } from '@arco-design/web-react';
import { usePageEditorSignal } from '@onebase/ui-kit';
import TabTitle from '../TabTitle';
import Advanced from './Advanced';
import Attributes from './Attributes';
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

  const { curComponentSchema } = usePageEditorSignal();

  return (
    <div className={styles.configs}>
      <div className={styles.componentName}>
        <div className={styles.icon}>{ICON_Map_By_Type[curComponentSchema?.type]}</div>
        {curComponentSchema?.config?.cpName}
      </div>
      <div className={styles.componentInfo}>
        <Tabs defaultActiveTab="attributes" type="line" size="default">
          <TabPane key="attributes" title={<TabTitle title={t('editor.attribute')} />}>
            <Attributes cpID={cpID} />
          </TabPane>
          <TabPane key="advanced" title={<TabTitle title={t('editor.advanced')} />}>
            <Advanced cpID={cpID} />
          </TabPane>
        </Tabs>
      </div>
    </div>
  );
};

export default MaterialConfiger;
