import { menuSignal } from '@onebase/app';
import { useSignals } from '@preact/signals-react/runtime';
import React from 'react';
import DynamicIcon from '../DynamicIcon';
import { menuIconList } from '../DynamicIcon/const';
import styles from './index.module.less';
import { webMenuIcons } from '@onebase/ui-kit';

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

const RuntimeMenuItem: React.FC<MenuItemProps> = ({ label, menuID, menuIcon, onClick, maxWidth }) => {
  useSignals();
  const { curMenu } = menuSignal;
  const allWebMenuIcons = webMenuIcons.map((ele) => ele.children).reduce((acc, current) => acc.concat(current), []);

  return (
    <div className={styles.runtimeMenuItem} onClick={onClick} role="menuitem" tabIndex={0}>
      <div
        className={styles.menuName}
        style={{
          maxWidth: maxWidth + 'px'
        }}
      >
        <img
          style={{
            width: 'auto',
            height: '18px',
             marginRight: 16,
            color: curMenu.value?.id === menuID ? 'rgb(var(--primary-6))' : '#333',
            fill: curMenu.value?.id === menuID ? 'rgb(var(--primary-6))' : '#333'
          }}
          src={allWebMenuIcons.find((ele) => ele.code === menuIcon)?.icon}
          alt=""
        />
       
        {/* xxx-taskicon 是工作流程任务中心菜单的icon */}
        {menuIcon.indexOf('-taskicon') > 0 && <i className={`iconfont ${menuIcon}`} style={{ marginRight: '16px' }} />}
        <span style={{ color: curMenu.value?.id === menuID ? 'rgb(var(--primary-6))' : '#333' }}>{label}</span>
      </div>
    </div>
  );
};

export default RuntimeMenuItem;
