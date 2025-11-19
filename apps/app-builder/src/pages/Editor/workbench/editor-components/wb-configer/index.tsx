import { ICON_Map_By_Type } from '@/components/MaterialCard/icons';
import { Collapse } from '@arco-design/web-react';
import { usePageEditorSignal } from '@onebase/ui-kit';
import { useSignals } from '@preact/signals-react/runtime';
import { useState } from 'react';
// import EntryConfig from './EntryConfig';
// import OtherConfig from './OtherConfig';
// import StyleLibrary from './StyleLibrary';
// import TitleConfig from './TitleConfig';
import styles from './index.module.less';

const CollapseItem = Collapse.Item;

/**
 * 工作台配置面板组件
 */

const WorkbenchConfiger = () => {
  useSignals();

  const { curComponentID, curComponentSchema } = usePageEditorSignal();
  console.log('curComponentSchema', curComponentSchema);

  const [activeKeys, setActiveKeys] = useState<string[]>(['style']);

  return (
    <div className={styles.workbenchConfigs}>
      <div className={styles.componentName}>
        <div className={styles.icon}>{ICON_Map_By_Type[curComponentSchema?.type]}</div>
        {curComponentSchema?.displayName || curComponentSchema?.config?.cpName || '工作台组件'}
      </div>
      <div className={styles.componentInfo}>
        <Collapse
          activeKey={activeKeys}
          onChange={(_key, keys) => setActiveKeys(keys)}
          accordion={false}
          bordered={false}
          expandIconPosition="right"
          className={styles.collapseConfigs}
        >
          <CollapseItem
            header="样式库"
            name="style"
            contentStyle={{ backgroundColor: '#fff', border: 'none', paddingLeft: 13 }}
          >
            {/* <StyleLibrary cpID={curComponentID} /> */}
          </CollapseItem>
          <CollapseItem
            header="标题配置"
            name="title"
            contentStyle={{ backgroundColor: '#fff', border: 'none', paddingLeft: 13 }}
          >
            {/* <TitleConfig cpID={curComponentID} /> */}
          </CollapseItem>
          <CollapseItem
            header="入口配置"
            name="entry"
            contentStyle={{ backgroundColor: '#fff', border: 'none', paddingLeft: 13 }}
          >
            {/* <EntryConfig cpID={curComponentID} /> */}
          </CollapseItem>
          <CollapseItem
            header="其他配置"
            name="other"
            contentStyle={{ backgroundColor: '#fff', border: 'none', paddingLeft: 13 }}
          >
            {/* <OtherConfig cpID={curComponentID} /> */}
          </CollapseItem>
        </Collapse>
      </div>
    </div>
  );
};

export default WorkbenchConfiger;
