import { Dropdown } from '@arco-design/web-react';
import { IconFolder, IconSettings } from '@arco-design/web-react/icon';
import React from 'react';
import styles from './index.module.less';

/**
 * MenuItem 组件
 * 用于在页面管理器中渲染单个菜单项
 * @param props.label 菜单项显示文本
 * @param props.icon 可选，菜单项图标
 * @param props.onClick 点击事件处理函数
 */
interface MenuItemProps {
  label: string;
  icon?: React.ReactNode;
  isGroup: boolean;
  onClick: () => void;
  settingOnClick: () => void;
  dropList: React.ReactNode;
  maxWidth: number;
}

const MyMenuItem: React.FC<MenuItemProps> = ({ label, icon, isGroup, onClick, settingOnClick, dropList, maxWidth }) => {
  return (
    <div className={styles.myMenuItem} onClick={onClick} role="menuitem" tabIndex={0}>
      {/* {icon && <span className="mr-2">{icon}</span>} */}
      <div
        className={styles.menuName}
        style={{
          maxWidth: maxWidth + 'px'
        }}
      >
        {isGroup ? <IconFolder style={{ marginRight: '10px' }} /> : null}

        {label}
      </div>
      <div className={styles.dropdownContainer}>
        <Dropdown droplist={dropList} trigger="click" position="bl">
          <IconSettings
            onClick={() => {
              settingOnClick();
            }}
          />
        </Dropdown>
      </div>
    </div>
  );
};

export default MyMenuItem;
