import React from 'react';
import styles from './index.module.less';
import DynamicIcon from '../DynamicIcon';
import { menuIconList } from '../DynamicIcon/const';

/**
 * MenuItem 组件
 * 用于在页面管理器中渲染单个菜单项
 * @param props.label 菜单项显示文本
 * @param props.icon 可选，菜单项图标
 * @param props.onClick 点击事件处理函数
 */
interface MenuItemProps {
  menuID: string;
  label: string;
  menuIcon: string;
  onClick: () => void;
  maxWidth: number;
}

const RuntimeMenuItem: React.FC<MenuItemProps> = ({ label, menuIcon, onClick, maxWidth }) => {
  return (
    <div className={styles.runtimeMenuItem} onClick={onClick} role="menuitem" tabIndex={0}>
      <div
        className={styles.menuName}
        style={{
          maxWidth: maxWidth + 'px'
        }}
      >
        <DynamicIcon
          IconComponent={menuIconList.find(icon => icon.code === menuIcon)?.icon}
          theme="outline"
          size="18"
          fill="#333"
          style={{ marginRight: 4 }}
        />
        {label}
      </div>
    </div>
  );
};

export default RuntimeMenuItem;
