import { menuSignal } from '@onebase/app';
import { useSignals } from '@preact/signals-react/runtime';
import React from 'react';
import styles from './index.module.less';
import { webMenuIcons } from '@onebase/ui-kit';
import { ReactSVG } from 'react-svg';

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
        {menuIcon.includes('TASK-') ? (
          // TASK-XXX 是工作流程任务中心菜单的icon
          <i className={`iconfont ${menuIcon}`} style={{ marginRight: '16px' }} />
        ) : (
          // 正常菜单 icon
          <ReactSVG
            className={styles.menuIcon}
            src={
              allWebMenuIcons.find((ele) => ele.code === menuIcon)?.icon ||
              allWebMenuIcons.find((ele) => ele.code === 'FormPage')?.icon ||
              ''
            }
            beforeInjection={(svg) => {
              const fillColor = curMenu.value?.id === menuID ? 'rgb(var(--primary-6))' : '#333';
              svg.querySelectorAll('*').forEach((el) => el.removeAttribute('fill'));
              svg.setAttribute('fill', fillColor);
              svg.setAttribute('width', '18px');
              svg.setAttribute('height', '18px');
            }}
          />
        )}

        <span style={{ color: curMenu.value?.id === menuID ? 'rgb(var(--primary-6))' : '#333' }}>{label}</span>
      </div>
    </div>
  );
};

export default RuntimeMenuItem;
