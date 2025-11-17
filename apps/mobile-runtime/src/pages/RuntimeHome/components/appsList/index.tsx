import { useI18n } from '@/hooks/useI18n';
import { Collapse, Ellipsis, Grid, Tabs } from '@arco-design/mobile-react';
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
import React, { useEffect, useMemo, useState } from 'react';
import { useLocation, useNavigate, useParams, useSearchParams } from 'react-router-dom';
import { DynamicIcon, menuIconList } from '@onebase/common';
import { splitAndFlatten, type TreeNode } from '@/utils/tree';
import styles from './index.module.less';

const isGridLayout = true;

const levelStyle = (level: number) => ({ padding: `0 ${level > 5 ? '0' : '0.24rem'}` })

const AppsList: React.FC<{ treeData: TreeNode[] }> = ({ treeData }) => {
  const navigate = useNavigate();
  const location = useLocation();
  const getGroupItem = (itemData: TreeNode, level: number = 0) => {
    if (itemData.isPage) {
      return <div key={itemData.key + 1} className={styles.treeItem} style={levelStyle(level)} onClick={() => handlerItemClick(itemData.id!)}>
        <DynamicIcon
          IconComponent={menuIconList.find((icon) => icon.code === itemData.icon)?.icon}
          theme="outline"
          size="0.2rem"
          fill="#009E9E"
          style={{ padding: '0.04rem', marginRight: '0.08rem' }}
        />
        <Ellipsis text={itemData.title} />
      </div>
    }
    return <Collapse
      className={styles.treeItemGroup}
      style={levelStyle(level)}
      key={itemData.key}
      header={
        <div className={styles.treeItemGroupHeader}>
          <DynamicIcon
            IconComponent={menuIconList.find((icon) => icon.code === itemData.icon)?.icon}
            theme="outline"
            size="0.2rem"
            fill="#009E9E"
            style={{ padding: '0.04rem', marginRight: '0.08rem' }}
          />
          <Ellipsis text={level + '--' + itemData.title} />
        </div>
      }
      value={itemData.key}
      content={
        itemData.children && itemData.children.length > 0 ?
          itemData.children.map((child) => getGroupItem(child, level + 1))
          : <div className={styles.treeNone}>无菜单</div>

      }
    />
  }

  const GridLayout = ({ data }: { data: TreeNode[] }) => {
    const renderData = data.filter((node) => node.isPage).map((item) => ({
      img: <DynamicIcon
        IconComponent={menuIconList.find((icon) => icon.code === item.icon)?.icon}
        theme="outline"
        size="0.48rem"
        fill="#F2F3F5"
        style={{ backgroundColor: '#009E9E', borderRadius: '0.16rem', padding: '0.16rem' }}
      />,
      title: <Ellipsis text={item.title} />,
      onClick: () => handlerItemClick(item.id!)
    }));

    return (
      <Grid className={styles.gridLayout} data={renderData} gutter={16} columns={4} />
    );
  };

  const handlerItemClick = (curMenuId: string) => {
    const sp = new URLSearchParams(location.search);
    sp.set('curMenu', String(curMenuId));
    // sp.delete('curTab');
    const to = `${location.pathname.replace('/runtime-home', '/runtime')}?${sp.toString()}`;
    navigate(to);
  };

  const gridData = useMemo(() => {
    const flatData = splitAndFlatten(treeData).filter(item => item.children && item.children.length > 0);
    return {
      tabs: flatData.map(item => ({ title: item.title })),
      grids: flatData,
    }
  }, [treeData]);

  if (!isGridLayout) {
    return (
      <div className={styles.appsList}>
        <div className={styles.label}>
          应用菜单
        </div>
        {treeData.length > 0 ? treeData.map((item) => getGroupItem(item)) : <div className={styles.treeNone}>无菜单</div>}
      </div>
    );
  }

  const leafItems = treeData.filter(item => item.children && item.children.length === 0);

  return (
    <>
      <div className={styles.appsList} style={leafItems.length === 0 ? { marginBottom: '-0.32rem' } : {}}>
        <div className={styles.label}>
          应用菜单
        </div>
        {leafItems.length > 0 && <GridLayout data={leafItems} />}
        {leafItems.length === 0 && gridData.tabs.length === 0 && <div className={styles.treeNone}>无菜单</div>}
      </div>

      {gridData.tabs.length > 0 && <div className={styles.appsList}>
        <Tabs
          className={styles.tabs}
          tabs={gridData.tabs}
          tabBarArrange={"start"}
          tabBarHasDivider={false}
        >
          {
            gridData.grids.map(item => (
              <GridLayout data={item.children} key={item.key} />
            ))
          }
        </Tabs>
      </div>}
    </>
  );
};

export default AppsList;
