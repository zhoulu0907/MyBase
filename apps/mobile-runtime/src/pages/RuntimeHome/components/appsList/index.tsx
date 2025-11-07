import { useI18n } from '@/hooks/useI18n';
import { Collapse, Ellipsis } from '@arco-design/mobile-react';
import {
  listApplicationMenu,
  menuSignal,
  MenuType,
  TASKMENU_TYPE,
  VisibleType,
  type ApplicationMenu,
  type ListApplicationMenuReq
} from '@onebase/app';
import { useSignals } from '@preact/signals-react/runtime';
import { IconUser, IconHome } from '@arco-design/mobile-react/esm/icon';
import React, { useEffect, useState } from 'react';
import { useLocation, useNavigate, useParams, useSearchParams } from 'react-router-dom';


import styles from './index.module.less';

interface TreeNode {
  key: string;
  id: string;
  icon: string;
  title: string;
  isVisible: number;
  children?: TreeNode[];
}

const levelStyle = (level: number) => ({ padding: `0 ${level > 5 ? '0' : '0.12rem'}` })

const AppsList: React.FC<{ treeData: TreeNode[] }> = ({ treeData }) => {
  const navigate = useNavigate();
  const location = useLocation();
  const getGroupItem = (itemData: TreeNode, level: number = 0) => {
    if (!itemData.children || itemData.children.length === 0) {
      return <div key={itemData.key + 1} className={styles.treeItem} style={levelStyle(level)} onClick={() => handlerItemClick(itemData.id)}>
        <IconUser className={styles.treeItemIcon} />
        <Ellipsis text={itemData.title} />
      </div>
    }
    return <Collapse
      className={styles.treeItemGroup}
      style={levelStyle(level)}
      key={itemData.key}
      header={
        <div className={styles.treeItemGroupHeader}>
          <IconUser className={styles.treeItemIcon} />
          <Ellipsis text={level + '--' + itemData.title} />
        </div>
      }
      value={itemData.key}
      content={
        itemData.children.map((child) => getGroupItem(child, level + 1))
      }
    />
  }
  const handlerItemClick = (curMenuId: string) => {
    const sp = new URLSearchParams(location.search);
    sp.set('curMenu', String(curMenuId));
    // sp.delete('curTab');
    const to = `${location.pathname.replace('/runtime-home', '/runtime')}?${sp.toString()}`;
    navigate(to);
  };

  return (
    <div className={styles.appsList}>
      <div className={styles.label}>
        应用菜单
      </div>
      {
        treeData.map((item) => getGroupItem(item))
      }
    </div>
  );
};

export default AppsList;
