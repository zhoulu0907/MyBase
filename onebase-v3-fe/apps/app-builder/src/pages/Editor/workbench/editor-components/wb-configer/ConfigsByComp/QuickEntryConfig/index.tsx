import { Collapse } from '@arco-design/web-react';
import { useState } from 'react';
import { WorkbenchAttributes, PanelContentStyle } from '../../components/CommonWorkbenchAttributes';
import { findItem } from '../../../../utils/edit-data';
import { StyleLibrary } from './StyleLibrary';
import styles from '../../index.module.less';

const CollapseItem = Collapse.Item;

const defaultLabel = {
  text: '快捷入口',
  display: true
};

const QuickEntryConfig = () => {
  const [activeKeys, setActiveKeys] = useState<string[]>(['style', 'title', 'entry']);

  return (
    <WorkbenchAttributes
      renderPanels={({ editData, renderEditItem, handlePropsChange, configs }) => {
        const entryConfigItem = findItem(editData, 'groupConfig');
        const styleConfigItem = findItem(editData, 'styleConfig');
        const titleConfigItem = findItem(editData, 'titleConfig');
        const labelItem = findItem(editData, 'label');

        // 兼容缺少label配置的旧数据
        if (!configs?.label) {
          handlePropsChange('label', defaultLabel);
        }

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
                  {styleConfigItem && (
                    <StyleLibrary
                      handlePropsChange={handlePropsChange}
                      item={styleConfigItem.item as { key: string }}
                      configs={configs}
                    />
                  )}
                </CollapseItem>
                <CollapseItem header="标题配置" name="title" contentStyle={PanelContentStyle}>
                  {labelItem && renderEditItem(labelItem)}
                  {titleConfigItem && renderEditItem(titleConfigItem)}
                </CollapseItem>
                <CollapseItem header="入口配置" name="entry" contentStyle={PanelContentStyle}>
                  {entryConfigItem && renderEditItem(entryConfigItem)}
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
