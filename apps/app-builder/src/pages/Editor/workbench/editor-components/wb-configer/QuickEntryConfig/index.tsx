import { Collapse } from '@arco-design/web-react';
import { useState } from 'react';
import { WorkbenchAttributes, PanelContentStyle } from '../components/CommonWorkbenchAttributes';
import { findItem } from '../../../utils/edit-data';
import styles from './index.module.less';

const CollapseItem = Collapse.Item;

const QuickEntryConfig = () => {
  const [activeKeys, setActiveKeys] = useState<string[]>(['style', 'title', 'entry']);

  return (
    <WorkbenchAttributes
      renderPanels={({ editData, renderEditItem }) => {

        // 找到入口配置项（key 为 'groupConfig'）
        const entryConfigItem = findItem(editData, 'groupConfig');

        // 找到样式配置项（key 为 'styleConfig'）
        const styleConfigItem = findItem(editData, 'styleConfig');

        // 找到标题配置项（key 为 'titleConfig'）
        const titleConfigItem = findItem(editData, 'titleConfig');

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
                  {styleConfigItem && renderEditItem(styleConfigItem.item, styleConfigItem.index)}
                </CollapseItem>
                <CollapseItem header="标题配置" name="title" contentStyle={PanelContentStyle}>
                  {titleConfigItem && renderEditItem(titleConfigItem.item, titleConfigItem.index)}
                </CollapseItem>
                <CollapseItem header="入口配置" name="entry" contentStyle={PanelContentStyle}>
                  {entryConfigItem && renderEditItem(entryConfigItem.item, entryConfigItem.index)}
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
