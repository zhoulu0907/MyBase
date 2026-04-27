import { menuSignal, TASKMENU_TYPE } from '@onebase/app';
import { webMenuIcons } from '@onebase/ui-kit';
import { useSignals } from '@preact/signals-react/runtime';
import React from 'react';
import { ReactSVG } from 'react-svg';
import styles from './index.module.less';
import ineedtodoSvg from '@/assets/images/task_center/willdo.svg';
import ihavedoneSvg from '@/assets/images/task_center/idone.svg';
import icreatedSvg from '@/assets/images/task_center/icreated.svg';
import icopiedSvg from '@/assets/images/task_center/icopied.svg';
import taskproxySvg from '@/assets/images/task_center/taskproxy.svg';
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
  const taskIconList = [
    { key: TASKMENU_TYPE.TASKINEEDTODO, value: ineedtodoSvg },
    { key: TASKMENU_TYPE.TASKIHAVEDONE, value: ihavedoneSvg },
    { key: TASKMENU_TYPE.TASKICREATED, value: icreatedSvg },
    { key: TASKMENU_TYPE.TASKICOPIED, value: icopiedSvg },
    { key: TASKMENU_TYPE.TASKTASKPROXY, value: taskproxySvg }
  ];
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
          <ReactSVG
            className={styles.menuIcon}
            src={taskIconList.find((ele) => ele.key === menuIcon)?.value || ''}
            beforeInjection={(svg) => {
              const fillColor = curMenu.value?.id === menuID ? 'rgb(var(--primary-6))' : '#333';
              svg.querySelectorAll('*').forEach((el) => {
                if (el.getAttribute('fill') === 'black' || el.getAttribute('fill') === '#4E5969') {
                  el.setAttribute('fill', fillColor);
                }
                if (el.getAttribute('stroke') === 'black' || el.getAttribute('stroke') === '#4E5969') {
                  el.setAttribute('stroke', fillColor);
                }
              });
              svg.setAttribute('width', '18px');
              svg.setAttribute('height', '18px');
            }}
          />
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
