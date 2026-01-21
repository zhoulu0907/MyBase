import { Collapse } from '@arco-design/web-react';
import { useState } from 'react';
import { WorkbenchAttributes, PanelContentStyle } from '../../components/CommonWorkbenchAttributes';
import { findItem } from '../../../../utils/edit-data';
import type { WorkbenchAttributeContext } from '../../components/CommonWorkbenchAttributes/useWorkbenchAttributeContext';
import { StyleLibrary } from './StyleLibrary';
import styles from '../../index.module.less';

const CollapseItem = Collapse.Item;

const SECTION_KEYS = {
  THEME: 'theme',
  CONTENT: 'content'
} as const;

const WelcomeCardPanels = ({
  editData,
  renderEditItem,
  handlePropsChange,
  configs,
  activeKeys,
  setActiveKeys
}: {
  editData: WorkbenchAttributeContext['editData'];
  renderEditItem: WorkbenchAttributeContext['renderEditItem'];
  handleMultiPropsChange: WorkbenchAttributeContext['handleMultiPropsChange'];
  handlePropsChange: WorkbenchAttributeContext['handlePropsChange'];
  configs: WorkbenchAttributeContext['configs'];
  activeKeys: string[];
  setActiveKeys: (keys: string[]) => void;
}) => {
  const themeConfig = findItem(editData, 'theme');
  const welcomeTextConfig = findItem(editData, 'welcomeText');
  const welcomeDescConfig = findItem(editData, 'welcomeDesc');

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

      {(welcomeTextConfig || welcomeDescConfig) && (
        <CollapseItem
          key={SECTION_KEYS.CONTENT}
          header="内容配置"
          name={SECTION_KEYS.CONTENT}
          contentStyle={PanelContentStyle}
        >
          <div>
            {welcomeTextConfig && <div>{renderEditItem(welcomeTextConfig)}</div>}
            {welcomeDescConfig && <div>{renderEditItem(welcomeDescConfig)}</div>}
          </div>
        </CollapseItem>
      )}
    </Collapse>
  );
};

const WelcomeCardConfig = () => {
  const [activeKeys, setActiveKeys] = useState<string[]>([SECTION_KEYS.THEME, SECTION_KEYS.CONTENT]);

  return (
    <WorkbenchAttributes
      renderPanels={({ editData, renderEditItem, handleMultiPropsChange, handlePropsChange, configs }) => (
        <WelcomeCardPanels
          editData={editData}
          renderEditItem={renderEditItem}
          handleMultiPropsChange={handleMultiPropsChange}
          handlePropsChange={handlePropsChange}
          configs={configs}
          activeKeys={activeKeys}
          setActiveKeys={setActiveKeys}
        />
      )}
    />
  );
};

export default WelcomeCardConfig;
