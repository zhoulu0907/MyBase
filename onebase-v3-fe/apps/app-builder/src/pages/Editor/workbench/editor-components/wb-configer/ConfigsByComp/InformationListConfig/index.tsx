import { Collapse } from '@arco-design/web-react';
import { useState } from 'react';
import WorkbenchInformationListContentConfig from './WorkbenchInformationListContentConfig';
import type { Props } from './types';
import { WorkbenchAttributes, PanelContentStyle } from '../../components/CommonWorkbenchAttributes';
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
  const [activeKeys, setActiveKeys] = useState<string[]>([
    SECTION_KEYS.THEME,
    SECTION_KEYS.TITLE,
    SECTION_KEYS.CONTENT
  ]);

  return (
    <WorkbenchAttributes
      renderPanels={({ cpID, editData, configs, handlePropsChange, handleMultiPropsChange, renderEditItem }) => {
        const configItems = {
          label: findItem(editData, 'label'),
          theme: findItem(editData, 'theme'),
          informationListContent: findItem(editData, 'informationListContent'),
          dataCount: findItem(editData, 'dataCount'),
          showMore: findItem(editData, 'showMore'),
          jumpTypeConfig: findItem(editData, 'jumpType'),
          jumpPageIdConfig: findItem(editData, 'jumpPageId'),
          jumpExternalUrlConfig: findItem(editData, 'jumpExternalUrl')
        };

        // 是否显示更多按钮
        const showMoreEnabled = !!configs.showMore;
        // 跳转类型
        const jumpType = (configs.jumpType as string) || 'internal';
        const showJumpPageId = showMoreEnabled && jumpType === 'internal' && configItems.jumpPageIdConfig;
        const showJumpExternalUrl = showMoreEnabled && jumpType === 'external' && configItems.jumpExternalUrlConfig;

        return (
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
              {showMoreEnabled && configItems.jumpTypeConfig && <div>{renderEditItem(configItems.jumpTypeConfig)}</div>}
              {showJumpPageId && <div>{renderEditItem(showJumpPageId)}</div>}
              {showJumpExternalUrl && <div>{renderEditItem(showJumpExternalUrl)}</div>}
            </CollapseItem>
            <CollapseItem header="数据来源配置" name={SECTION_KEYS.CONTENT} contentStyle={PanelContentStyle}>
              {cpID && configItems.informationListContent && (
                <WorkbenchInformationListContentConfig
                  id={cpID}
                  item={configItems.informationListContent.item as Props['item']}
                  configs={configs}
                  handlePropsChange={handlePropsChange}
                  handleMultiPropsChange={handleMultiPropsChange}
                />
              )}
              {configItems.dataCount && <div>{renderEditItem(configItems.dataCount)}</div>}
            </CollapseItem>
          </Collapse>
        );
      }}
    />
  );
};

export default InfortmationListConfig;
