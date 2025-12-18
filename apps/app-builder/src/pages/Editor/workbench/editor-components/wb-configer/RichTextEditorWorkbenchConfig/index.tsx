import { Collapse } from '@arco-design/web-react';
import { useState } from 'react';
import { WorkbenchAttributes, PanelContentStyle } from '../components/CommonWorkbenchAttributes';
import { findItem } from '../../../utils/edit-data';
import styles from '../index.module.less';

const CollapseItem = Collapse.Item;

const SECTION_KEYS = {
  TITLE: 'title',
  STYLE: 'style',
  CONTENT: 'content'
} as const;

const RichTextEditorWorkbenchConfig = () => {
  const [activeKeys, setActiveKeys] = useState<string[]>([
    SECTION_KEYS.TITLE,
    SECTION_KEYS.STYLE,
    SECTION_KEYS.CONTENT
  ]);

  return (
    <WorkbenchAttributes
      renderPanels={({ editData, renderEditItem }) => {
        const collapseItemList = [
          {
            header: '标题配置',
            name: SECTION_KEYS.TITLE,
            contentStyle: PanelContentStyle,
            content: findItem(editData, 'label')
          },
          {
            header: '样式设置',
            name: SECTION_KEYS.STYLE,
            contentStyle: PanelContentStyle,
            content: findItem(editData, 'Wb_Color')
          },
          {
            header: '内容编辑',
            name: SECTION_KEYS.CONTENT,
            contentStyle: PanelContentStyle,
            content: findItem(editData, 'Wb_RichTextContent')
          }
        ].filter((item) => item.content !== null);

        return (
          <Collapse
            activeKey={activeKeys}
            onChange={(_key, keys) => setActiveKeys(keys)}
            accordion={false}
            bordered={false}
            expandIconPosition="right"
            className={styles.collapseConfigs}
          >
            {collapseItemList.map((item) => (
              <CollapseItem key={item.name} header={item.header} name={item.name} contentStyle={item.contentStyle}>
                {item.content && <div>{renderEditItem(item.content.item, item.content.index)}</div>}
              </CollapseItem>
            ))}
          </Collapse>
        );
      }}
    />
  );
};

export default RichTextEditorWorkbenchConfig;
