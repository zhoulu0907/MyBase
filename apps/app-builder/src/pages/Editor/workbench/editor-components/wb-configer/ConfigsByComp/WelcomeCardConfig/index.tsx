import { Collapse } from '@arco-design/web-react';
import { UserPermissionManager } from '@onebase/common';
import { useEffect, useState } from 'react';
import { WorkbenchAttributes, PanelContentStyle } from '../../components/CommonWorkbenchAttributes';
import { findItem } from '../../../../utils/edit-data';
import styles from '../../index.module.less';
import type { WorkbenchAttributeContext } from '../../components/CommonWorkbenchAttributes/useWorkbenchAttributeContext';

const CollapseItem = Collapse.Item;

const SECTION_KEYS = {
  THEME: 'theme',
  CONTENT: 'content'
} as const;

const WelcomeCardPanels = ({
  editData,
  renderEditItem,
  handleMultiPropsChange,
  activeKeys,
  setActiveKeys
}: {
  editData: WorkbenchAttributeContext['editData'];
  renderEditItem: WorkbenchAttributeContext['renderEditItem'];
  handleMultiPropsChange: WorkbenchAttributeContext['handleMultiPropsChange'];
  activeKeys: string[];
  setActiveKeys: (keys: string[]) => void;
}) => {
  const userPermissionInfo = UserPermissionManager.getUserPermissionInfo();

  // 读取用户头像和姓名作为默认值
  useEffect(() => {
    if (userPermissionInfo?.user) {
      const currentAvatar = userPermissionInfo.user.avatar || '';
      const currentNickname = userPermissionInfo.user.nickname || '';

      const updates = [];
      updates.push({ key: 'userAvatar', value: currentAvatar });
      updates.push({ key: 'userName', value: currentNickname });
      handleMultiPropsChange(updates);
    }
  }, []);

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
          <div>{renderEditItem(themeConfig)}</div>
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

const ButtonWorkbenchConfig = () => {
  const [activeKeys, setActiveKeys] = useState<string[]>([SECTION_KEYS.THEME, SECTION_KEYS.CONTENT]);

  return (
    <WorkbenchAttributes
      renderPanels={({ editData, renderEditItem, handleMultiPropsChange }) => (
        <WelcomeCardPanels
          editData={editData}
          renderEditItem={renderEditItem}
          handleMultiPropsChange={handleMultiPropsChange}
          activeKeys={activeKeys}
          setActiveKeys={setActiveKeys}
        />
      )}
    />
  );
};

export default ButtonWorkbenchConfig;
