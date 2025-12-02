import { Collapse, Ellipsis, Grid, Tabs } from '@arco-design/mobile-react';
import { menuSignal } from '@onebase/app';
import React, { useMemo } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import { ReactSVG } from 'react-svg';
import { mobileMenuIcons } from '@onebase/ui-kit';
import { splitAndFlatten, type TreeNode } from '@/utils/tree';
import styles from './index.module.less';

const isGridLayout = true;

const levelStyle = (level: number) => ({ padding: `0 ${level > 5 ? '0' : '0.24rem'}` });

const AppsList: React.FC<{ treeData: TreeNode[] }> = ({ treeData }) => {
  const allMobileMenuIcons = mobileMenuIcons.map((ele) => ele.children).reduce((acc, current) => acc.concat(current), []);
  const { setCurMenu } = menuSignal;
  const navigate = useNavigate();
  const location = useLocation();

  const getGroupItem = (itemData: TreeNode, level: number = 0) => {
    if (itemData.isPage) {
      return (
        <div
          key={itemData.key + 1}
          className={styles.treeItem}
          style={levelStyle(level)}
          onClick={() => handlerItemClick(itemData)}
        >
          <ReactSVG
            className={styles.menuIcon}
            src={
              allMobileMenuIcons.find((ele) => ele.code === itemData.icon)?.icon ||
              allMobileMenuIcons.find((ele) => ele.code === 'FormPage')?.icon ||
              ''
            }
            style={{
              backgroundColor: 'transparent'
            }}
            beforeInjection={(svg) => {
              const fillColor = level === 0 ? 'rgb(var(--primary-6))' : '#333';
              svg.querySelectorAll('*').forEach((el) => el.removeAttribute('fill'));
              svg.setAttribute('fill', fillColor);
              svg.setAttribute('width', '0.48rem');
              svg.setAttribute('height', '0.48rem');
            }}
          />
          <Ellipsis text={itemData.title} />
        </div>
      );
    }

    return (
      <Collapse
        className={styles.treeItemGroup}
        style={levelStyle(level)}
        key={itemData.key}
        header={
          <div className={styles.treeItemGroupHeader}>
            <ReactSVG
              className={styles.menuIcon}
              src={
                allMobileMenuIcons.find((ele) => ele.code === itemData.icon)?.icon ||
                allMobileMenuIcons.find((ele) => ele.code === 'FormPage')?.icon ||
                ''
              }
              style={{
                marginRight: '0.16rem',
                backgroundColor: 'transparent'
              }}
              beforeInjection={(svg) => {
                const fillColor = itemData?.children?.length ? 'rgb(var(--primary-6))' : '#333';
                svg.querySelectorAll('*').forEach((el) => el.removeAttribute('fill'));
                svg.setAttribute('fill', fillColor);
                svg.setAttribute('width', '0.48rem');
                svg.setAttribute('height', '0.48rem');
              }}
            />
            <Ellipsis text={level + '--' + itemData.title} />
          </div>
        }
        value={itemData.key}
        content={
          itemData.children && itemData.children.length > 0 ? (
            itemData.children.map((child) => getGroupItem(child, level + 1))
          ) : (
            <div className={styles.treeNone}>无菜单</div>
          )
        }
      />
    );
  };

  const GridLayout = ({ data, isAppIcon = false }: { data: TreeNode[]; isAppIcon?: boolean }) => {
    const renderData = data
      .filter((node) => node.isPage)
      .map((item) => ({
        img: (
          <ReactSVG
            className={styles.menuIcon}
            src={
              allMobileMenuIcons.find((ele) => ele.code === item.icon)?.icon ||
              allMobileMenuIcons.find((ele) => ele.code === 'FormPage')?.icon ||
              ''
            }
            style={{
              backgroundColor: `rgb(var(--primary-${isAppIcon ? 6 : 1}))`
            }}
            beforeInjection={(svg) => {
              const fillColor = isAppIcon ? 'rgba(255,255,255,0.85)' : 'rgb(var(--primary-6))';
              svg.querySelectorAll('*').forEach((el) => el.removeAttribute('fill'));
              svg.setAttribute('fill', fillColor);
              svg.setAttribute('width', '0.48rem');
              svg.setAttribute('height', '0.48rem');
            }}
          />
        ),
        title: <Ellipsis text={item.title} />,
        onClick: () => handlerItemClick(item)
      }));

    return <Grid className={styles.gridLayout} data={renderData} gutter={16} columns={4} />;
  };

  const handlerItemClick = (item: TreeNode) => {
    const sp = new URLSearchParams(location.search);
    sp.set('curMenu', String(item.id));
    setCurMenu({
      id: item.id || '',
      menuCode: item.key,
      menuSort: item.menuSort,
      menuType: item.menuType,
      menuName: item.title,
      menuIcon: item.icon || '',
      isVisible: item.isVisible || 0,
      children: []
    });
    // sp.delete('curTab');
    const to = `${location.pathname.replace('/runtime-home', '/runtime')}?${sp.toString()}`;
    navigate(to);
  };

  const gridData = useMemo(() => {
    const flatData = splitAndFlatten(treeData).filter((item) => item.children && item.children.length > 0);
    return {
      tabs: flatData.map((item) => ({ title: item.title })),
      grids: flatData
    };
  }, [treeData]);

  if (!isGridLayout) {
    return (
      <div className={styles.appsList}>
        <div className={styles.label}>应用菜单</div>
        {treeData.length > 0 ? (
          treeData.map((item) => getGroupItem(item))
        ) : (
          <div className={styles.treeNone}>无菜单</div>
        )}
      </div>
    );
  }

  const leafItems = treeData.filter((item) => item.children && item.children.length === 0);

  return (
    <>
      <div className={styles.appsList} style={leafItems.length === 0 ? { marginBottom: '-0.32rem' } : {}}>
        <div className={styles.label}>应用菜单</div>
        {leafItems.length > 0 && <GridLayout data={leafItems} isAppIcon />}
        {leafItems.length === 0 && gridData.tabs.length === 0 && <div className={styles.treeNone}>无菜单</div>}
      </div>

      {gridData.tabs.length > 0 && (
        <div className={styles.appsList}>
          <Tabs className={styles.tabs} tabs={gridData.tabs} tabBarArrange={'start'} tabBarHasDivider={false}>
            {gridData.grids.map((item) => (
              <GridLayout data={item.children} key={item.key} />
            ))}
          </Tabs>
        </div>
      )}
    </>
  );
};

export default AppsList;
