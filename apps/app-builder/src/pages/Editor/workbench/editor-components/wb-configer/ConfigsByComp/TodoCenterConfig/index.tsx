import { Collapse } from '@arco-design/web-react';
import { useState } from 'react';
import { WorkbenchAttributes, PanelContentStyle } from '../../components/CommonWorkbenchAttributes';
import { findItem } from '../../../../utils/edit-data';
import styles from '../../index.module.less';

/**
 * 待办中心配置
 */

const CollapseItem = Collapse.Item;

const SECTION_KEYS = {
  STYLE: 'style',
  TITLE: 'title',
  DATA_CONFIG: 'dataConfig',
  CONTENT: 'content'
} as const;

const TodoCenterConfig = () => {
  const [activeKeys, setActiveKeys] = useState<string[]>([SECTION_KEYS.TITLE, SECTION_KEYS.CONTENT]);

  const collapseItemList = (editData: Record<string, unknown>[]) =>
    [
      {
        header: '样式设置',
        name: SECTION_KEYS.STYLE,
        contentStyle: PanelContentStyle,
        content: findItem(editData, 'style')
      },
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
      renderPanels={({ editData, renderEditItem }) => (
        <Collapse
          activeKey={activeKeys}
          onChange={(_key, keys) => setActiveKeys(keys)}
          accordion={false}
          bordered={false}
          expandIconPosition="right"
          className={styles.collapseConfigs}
        >
          {collapseItemList(editData).map((item) => (
            <CollapseItem key={item.name} header={item.header} name={item.name} contentStyle={item.contentStyle}>
              {item.content && <div>{renderEditItem(item.content)}</div>}
            </CollapseItem>
          ))}
        </Collapse>
      )}
    />
  );
};

export default TodoCenterConfig;
