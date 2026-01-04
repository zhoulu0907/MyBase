import { Collapse } from '@arco-design/web-react';
import { useState } from 'react';
import { WorkbenchAttributes, PanelContentStyle } from '../../components/CommonWorkbenchAttributes';
import { findItem } from '../../../../utils/edit-data';
import styles from '../../index.module.less';

const CollapseItem = Collapse.Item;

const SECTION_KEYS = {
  TITLE: 'title',
  BUTTON: 'button',
  JUMP: 'jump'
} as const;

const ButtonWorkbenchConfig = () => {
  const [activeKeys, setActiveKeys] = useState<string[]>([SECTION_KEYS.TITLE, SECTION_KEYS.BUTTON, SECTION_KEYS.JUMP]);

  return (
    <WorkbenchAttributes
      renderPanels={({ editData, renderEditItem, configs }) => {
        // 查找各个配置项
        const titleConfig = findItem(editData, 'label');
        const backgroundColorConfig = findItem(editData, 'backgroundColor');
        const textColorConfig = findItem(editData, 'textColor');
        const textSizeConfig = findItem(editData, 'textSize');
        const textAlignConfig = findItem(editData, 'textAlign');
        const jumpTypeConfig = findItem(editData, 'jumpType');
        const jumpPageIdConfig = findItem(editData, 'jumpPageId');
        const jumpExternalUrlConfig = findItem(editData, 'jumpExternalUrl');

        // 根据跳转类型判断显示哪个配置项
        const jumpType = (configs?.jumpType as string) || 'internal';
        const showJumpPageId = jumpType === 'internal' && jumpPageIdConfig;
        const showJumpExternalUrl = jumpType === 'external' && jumpExternalUrlConfig;

        return (
          <Collapse
            activeKey={activeKeys}
            onChange={(_key, keys) => setActiveKeys(keys)}
            accordion={false}
            bordered={false}
            expandIconPosition="right"
            className={styles.collapseConfigs}
          >
            {/* 标题配置 */}
            {titleConfig && (
              <CollapseItem
                key={SECTION_KEYS.TITLE}
                header="标题配置"
                name={SECTION_KEYS.TITLE}
                contentStyle={PanelContentStyle}
              >
                <div>{renderEditItem(titleConfig)}</div>
              </CollapseItem>
            )}

            {/* 按钮配置 - 包含多个配置项 */}
            {(backgroundColorConfig || textColorConfig || textSizeConfig || textAlignConfig) && (
              <CollapseItem
                key={SECTION_KEYS.BUTTON}
                header="按钮配置"
                name={SECTION_KEYS.BUTTON}
                contentStyle={PanelContentStyle}
              >
                <div>
                  {backgroundColorConfig && <div>{renderEditItem(backgroundColorConfig)}</div>}
                  {textColorConfig && <div>{renderEditItem(textColorConfig)}</div>}
                  {textSizeConfig && <div>{renderEditItem(textSizeConfig)}</div>}
                  {textAlignConfig && <div>{renderEditItem(textAlignConfig)}</div>}
                </div>
              </CollapseItem>
            )}

            {/* 跳转配置 */}
            {jumpTypeConfig && (
              <CollapseItem
                key={SECTION_KEYS.JUMP}
                header="跳转配置"
                name={SECTION_KEYS.JUMP}
                contentStyle={PanelContentStyle}
              >
                <div>
                  <div>{renderEditItem(jumpTypeConfig)}</div>
                  {showJumpPageId && <div>{renderEditItem(jumpPageIdConfig)}</div>}
                  {showJumpExternalUrl && <div>{renderEditItem(jumpExternalUrlConfig)}</div>}
                </div>
              </CollapseItem>
            )}
          </Collapse>
        );
      }}
    />
  );
};

export default ButtonWorkbenchConfig;
