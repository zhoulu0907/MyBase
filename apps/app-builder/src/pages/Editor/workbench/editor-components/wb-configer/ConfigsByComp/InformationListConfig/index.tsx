import { Collapse } from '@arco-design/web-react';
import { useState, useMemo } from 'react';
import WorkbenchInformationListContentConfig from './WorkbenchInformationListContentConfig';
import {
  WorkbenchAttributes,
  UseWorkbenchAttributeContext,
  PanelContentStyle
} from '../../components/CommonWorkbenchAttributes';
import { findItem } from '../../../../utils/edit-data';
import { StyleLibrary } from './StyleLibrary';
import styles from '../../index.module.less';

const CollapseItem = Collapse.Item;

const SECTION_KEYS = {
  THEME: 'theme',
  TITLE: 'title',
  CONTENT: 'content'
} as const;

const InfortmationListConfig = () => {
  const { editData, configs, handlePropsChange, renderEditItem } = UseWorkbenchAttributeContext();
  const [activeKeys, setActiveKeys] = useState<string[]>([
    SECTION_KEYS.THEME,
    SECTION_KEYS.TITLE,
    SECTION_KEYS.CONTENT
  ]);

  const configItems = useMemo(() => {
    return {
      label: findItem(editData, 'label'),
      theme: findItem(editData, 'theme'),
      informationListContent: findItem(editData, 'informationListContent'),
      dataCount: findItem(editData, 'dataCount'),
      showMore: findItem(editData, 'showMore'),
      showMoreLink: findItem(editData, 'showMoreLink')
    };
  }, [editData]);

  return (
    <WorkbenchAttributes
      renderPanels={({ cpID }) => (
        <Collapse
          activeKey={activeKeys}
          onChange={(_key, keys) => setActiveKeys(keys)}
          accordion={false}
          bordered={false}
          expandIconPosition="right"
          className={styles.collapseConfigs}
        >
          <CollapseItem header="样式库" name={SECTION_KEYS.THEME} contentStyle={PanelContentStyle}>
            <StyleLibrary
              handlePropsChange={handlePropsChange}
              item={configItems.theme?.item as { key: string }}
              configs={configs}
            />
          </CollapseItem>
          <CollapseItem header="标题配置" name={SECTION_KEYS.TITLE} contentStyle={PanelContentStyle}>
            {configItems.label && <div>{renderEditItem(configItems.label)}</div>}
            {configItems.showMore && <div>{renderEditItem(configItems.showMore)}</div>}
            {configItems.showMoreLink && <div>{renderEditItem(configItems.showMoreLink)}</div>}
          </CollapseItem>
          <CollapseItem header="数据来源配置" name={SECTION_KEYS.CONTENT} contentStyle={PanelContentStyle}>
            {cpID && configItems.informationListContent && (
              <WorkbenchInformationListContentConfig
                id={cpID}
                item={configItems.informationListContent.item}
                configs={configs}
                handlePropsChange={handlePropsChange}
              />
            )}
            {configItems.dataCount && <div>{renderEditItem(configItems.dataCount)}</div>}
          </CollapseItem>
        </Collapse>
      )}
    />
  );
};

export default InfortmationListConfig;
