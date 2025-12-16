import { Collapse } from '@arco-design/web-react';
import { useState } from 'react';
import StyleLibrary from './StyleLibrary';
import TitleConfig from './TitleConfig';
import { WorkbenchAttributes, PanelContentStyle } from '../components/CommonWorkbenchAttributes';
// import { findItem } from '../../../utils/edit-data';
import styles from './index.module.less';

const CollapseItem = Collapse.Item;

const QuickEntryConfig = () => {
  const [activeKeys, setActiveKeys] = useState<string[]>(['style', 'title', 'entry']);

  return (
    <WorkbenchAttributes
      renderPanels={({ cpID, editData, renderEditItem }) => {
        // 找到入口配置项（key 为 'props'）
        const entryConfigItem = editData.find((item) => item.key === 'props');
        const entryConfigIndex = entryConfigItem ? editData.indexOf(entryConfigItem) : -1;

        return (
          <div className={styles.workbenchConfigs}>
            <div className={styles.componentInfo}>
              <Collapse
                activeKey={activeKeys}
                onChange={(_key, keys) => setActiveKeys(keys)}
                accordion={false}
                bordered={false}
                expandIconPosition="right"
                className={styles.collapseConfigs}
              >
                <CollapseItem header="样式库" name="style" contentStyle={PanelContentStyle}>
                  {cpID && <StyleLibrary cpID={cpID} />}
                </CollapseItem>
                <CollapseItem header="标题配置" name="title" contentStyle={PanelContentStyle}>
                  {cpID && <TitleConfig cpID={cpID} />}
                </CollapseItem>
                <CollapseItem header="入口配置" name="entry" contentStyle={PanelContentStyle}>
                  {entryConfigItem && entryConfigIndex >= 0 && renderEditItem(entryConfigItem, entryConfigIndex)}
                </CollapseItem>
              </Collapse>
            </div>
          </div>
        );
      }}
    />
  );
};

export default QuickEntryConfig;
