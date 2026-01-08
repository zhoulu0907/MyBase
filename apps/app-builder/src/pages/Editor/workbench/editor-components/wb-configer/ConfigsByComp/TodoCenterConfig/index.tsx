import { Collapse } from '@arco-design/web-react';
import { useState } from 'react';
import { WorkbenchAttributes, PanelContentStyle } from '../../components/CommonWorkbenchAttributes';
import { findItem } from '../../../../utils/edit-data';
import { StyleLibrary } from './StyleLibrary';
import styles from '../../index.module.less';

/**
 * 待办中心配置
 */

const CollapseItem = Collapse.Item;

const SECTION_KEYS = {
  THEME: 'theme',
  TITLE: 'title',
  DATA_CONFIG: 'dataConfig',
  CONTENT: 'content'
} as const;

const TodoCenterConfig = () => {
  const [activeKeys, setActiveKeys] = useState<string[]>([
    SECTION_KEYS.THEME,
    SECTION_KEYS.TITLE,
    SECTION_KEYS.DATA_CONFIG,
    SECTION_KEYS.CONTENT
  ]);
  const collapseItemList = (editData: Record<string, unknown>[]) =>
    [
      {
        header: '标题配置',
        name: SECTION_KEYS.TITLE,
        contentStyle: PanelContentStyle,
        content: findItem(editData, 'label')
      },
      {
        header: '数据内容配置',
        name: SECTION_KEYS.DATA_CONFIG,
        contentStyle: PanelContentStyle,
        content: findItem(editData, 'dataConfig')
      }
      // {
      //   header: '内容编辑',
      //   name: SECTION_KEYS.CONTENT,
      //   contentStyle: PanelContentStyle,
      //   content: findItem(editData, 'Wb_RichTextContent')
      // }
    ].filter((item) => item.content !== null);

  return (
    <WorkbenchAttributes
      renderPanels={({ editData, renderEditItem, handlePropsChange, configs }) => {
        const themeConfig = findItem(editData, 'theme');

        return (
          <Collapse
            activeKey={activeKeys}
            onChange={(_key, keys) => setActiveKeys(keys)}
            accordion={false}
            bordered={false}
            expandIconPosition="right"
            className={styles.collapseConfigs}
          >
            {themeConfig && (
              <CollapseItem
                key={SECTION_KEYS.THEME}
                header="样式库"
                name={SECTION_KEYS.THEME}
                contentStyle={PanelContentStyle}
              >
                <StyleLibrary
                  handlePropsChange={handlePropsChange}
                  item={themeConfig.item as { key: string }}
                  configs={configs}
                />
              </CollapseItem>
            )}
            {collapseItemList(editData).map((item) => (
              <CollapseItem key={item.name} header={item.header} name={item.name} contentStyle={item.contentStyle}>
                {item.content && <div>{renderEditItem(item.content)}</div>}
              </CollapseItem>
            ))}
          </Collapse>
        );
      }}
    />
  );
};

export default TodoCenterConfig;
