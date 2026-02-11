import { Collapse } from '@arco-design/web-react';
import { useState } from 'react';
import { WorkbenchAttributes, PanelContentStyle } from '../../components/CommonWorkbenchAttributes';
import { findItem } from '../../../../utils/edit-data';
import styles from '../../index.module.less';

const CollapseItem = Collapse.Item;

const SECTION_KEYS = {
  TITLE: 'title',
  TABLE_CONFIG: 'tableConfig'
} as const;

const DataListConfig = () => {
  const [activeKeys, setActiveKeys] = useState<string[]>([SECTION_KEYS.TITLE, SECTION_KEYS.TABLE_CONFIG]);

  return (
    <WorkbenchAttributes
      renderPanels={({ editData, renderEditItem }) => {
        const labelItem = findItem(editData, 'label');
        const tableItem = findItem(editData, 'tableInfo');

        return (
          <Collapse
            activeKey={activeKeys}
            onChange={(_key, keys) => setActiveKeys(keys)}
            accordion={false}
            bordered={false}
            expandIconPosition="right"
            className={styles.collapseConfigs}
          >
            <CollapseItem header="标题配置" name={SECTION_KEYS.TITLE} contentStyle={PanelContentStyle}>
              {labelItem && <div>{renderEditItem(labelItem)}</div>}
            </CollapseItem>
            <CollapseItem header="数据列表配置" name={SECTION_KEYS.TABLE_CONFIG} contentStyle={PanelContentStyle}>
              {tableItem && <div>{renderEditItem(tableItem)}</div>}
            </CollapseItem>
          </Collapse>
        );
      }}
    />
  );
};

export default DataListConfig;
