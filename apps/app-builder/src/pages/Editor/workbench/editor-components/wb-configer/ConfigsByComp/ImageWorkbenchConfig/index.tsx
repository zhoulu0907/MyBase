import { Collapse } from '@arco-design/web-react';
import { useState } from 'react';
import { WorkbenchAttributes, PanelContentStyle } from '../../components/CommonWorkbenchAttributes';
import { findItem } from '../../../../utils/edit-data';
import styles from '../../index.module.less';

const CollapseItem = Collapse.Item;

const SECTION_KEYS = {
  TITLE: 'title',
  CONTENT: 'content',
  JUMP: 'jump'
} as const;

const ImageWorkbenchConfig = () => {
  const [activeKeys, setActiveKeys] = useState<string[]>([SECTION_KEYS.TITLE, SECTION_KEYS.CONTENT, SECTION_KEYS.JUMP]);

  return (
    <WorkbenchAttributes
      renderPanels={({ editData, renderEditItem, configs }) => {
        const imageItems = findItem(editData, 'image');
        const labelItems = findItem(editData, 'label');
        const fillItems = findItem(editData, 'fillStyle');
        const jumpItems = findItem(editData, 'jumpType');
        const jumpPageIdItems = findItem(editData, 'jumpPageId');
        const jumpExternalUrlItems = findItem(editData, 'jumpExternalUrl');

        // 根据跳转类型判断显示哪个配置项
        const jumpType = (configs?.jumpType as string) || 'internal';
        const showJumpPageId = jumpType === 'internal' && jumpPageIdItems;
        const showJumpExternalUrl = jumpType === 'external' && jumpExternalUrlItems;

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
              {labelItems && <div>{renderEditItem(labelItems)}</div>}
            </CollapseItem>
            <CollapseItem header="图片配置" name={SECTION_KEYS.CONTENT} contentStyle={PanelContentStyle}>
              {imageItems && <div>{renderEditItem(imageItems)}</div>}
              {fillItems && <div>{renderEditItem(fillItems)}</div>}
            </CollapseItem>
            <CollapseItem header="跳转配置" name={SECTION_KEYS.JUMP} contentStyle={PanelContentStyle}>
              {jumpItems && <div>{renderEditItem(jumpItems)}</div>}
              {showJumpPageId && <div>{renderEditItem(jumpPageIdItems)}</div>}
              {showJumpExternalUrl && <div>{renderEditItem(jumpExternalUrlItems)}</div>}
            </CollapseItem>
          </Collapse>
        );
      }}
    />
  );
};

export default ImageWorkbenchConfig;
