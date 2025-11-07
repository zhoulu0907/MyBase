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
  value: string;
  title: string;
  isVisible: number;
  children?: TreeNode[];
}

const levelStyle = (level: number) => ({ padding: `0 ${level > 5 ? '0' : '0.12rem'}` })

const AppsList: React.FC<{ treeData: TreeNode[] }> = ({ treeData }) => {

  const getGroupItem = (itemData:TreeNode, level:number = 0) => {
    if (!itemData.children || itemData.children.length === 0) {
      return <div className={styles.treeItem} style={levelStyle(level)}>
                   <IconUser className={styles.treeItemIcon}/>
                   <Ellipsis text={itemData.title} />
                 </div>
    }
    return <Collapse
             className={styles.treeItemGroup}
             style={levelStyle(level)}
             header={
               <div className={styles.treeItemGroupHeader}>
                 <IconUser className={styles.treeItemIcon}/>
                 <Ellipsis text={level + '--' + itemData.title} />
               </div>
             }
             value={itemData.key}
             content={
               itemData.children.map((child) => getGroupItem(child, level + 1))
             }
          />
  }

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
