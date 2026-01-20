import { Collapse } from '@arco-design/web-react';
import { useEffect, useState } from 'react';
import { UserPermissionManager } from '@onebase/common';
import { WorkbenchAttributes, PanelContentStyle } from '../../components/CommonWorkbenchAttributes';
import { findItem } from '../../../../utils/edit-data';
import { StyleLibrary } from './StyleLibrary';
import styles from '../../index.module.less';
import type { WorkbenchAttributeContext } from '../../components/CommonWorkbenchAttributes/useWorkbenchAttributeContext';

/**
 * 待办列表配置
 */

const CollapseItem = Collapse.Item;

const SECTION_KEYS = {
  THEME: 'theme',
  TITLE: 'title',
  DATA_CONFIG: 'dataConfig'
} as const;

const TodoListPanels = ({
  editData,
  renderEditItem,
  handleMultiPropsChange,
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
  const titleConfig = findItem(editData, 'label');
  const dataConfig = findItem(editData, 'dataConfig');
  const dataCountConfig = findItem(editData, 'dataCount');

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
      <CollapseItem
        key={SECTION_KEYS.TITLE}
        header="标题配置"
        name={SECTION_KEYS.TITLE}
        contentStyle={PanelContentStyle}
      >
        {titleConfig && renderEditItem(titleConfig)}
      </CollapseItem>
      <CollapseItem
        key={SECTION_KEYS.DATA_CONFIG}
        header="数据内容配置"
        name={SECTION_KEYS.DATA_CONFIG}
        contentStyle={PanelContentStyle}
      >
        {dataConfig && renderEditItem(dataConfig)}
        {dataCountConfig && renderEditItem(dataCountConfig)}
      </CollapseItem>
    </Collapse>
  );
};

const TodoListConfig = () => {
  const [activeKeys, setActiveKeys] = useState<string[]>([
    SECTION_KEYS.THEME,
    SECTION_KEYS.TITLE,
    SECTION_KEYS.DATA_CONFIG
  ]);
  return (
    <WorkbenchAttributes
      renderPanels={({ editData, renderEditItem, handleMultiPropsChange, handlePropsChange, configs }) => {
        return (
          <TodoListPanels
            editData={editData}
            renderEditItem={renderEditItem}
            handleMultiPropsChange={handleMultiPropsChange}
            handlePropsChange={handlePropsChange}
            configs={configs}
            activeKeys={activeKeys}
            setActiveKeys={setActiveKeys}
          />
        );
      }}
    />
  );
};

export default TodoListConfig;
