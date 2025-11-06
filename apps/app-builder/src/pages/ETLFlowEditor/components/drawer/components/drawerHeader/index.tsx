import { Tabs } from '@arco-design/web-react';
import { ETLDrawerTab, etlEditorSignal } from '@onebase/common';
import { useSignals } from '@preact/signals-react/runtime';
import React from 'react';
import NodeTitle from '../nodeTitle';
import styles from './index.module.less';

/**
 * ETLFlowEditor 抽屉头部
 * 初始化页面，展示节点的标题
 * 后续可以按需扩展更多头部功能
 */
interface DrawerHeaderProps {}

const DrawerHeader: React.FC<DrawerHeaderProps> = ({}) => {
  useSignals();

  const { curDrawerTab, setCurDrawerTab } = etlEditorSignal;

  return (
    <div className={styles.drawerHeader}>
      <NodeTitle title={etlEditorSignal.curNode.value?.title} />

      <Tabs
        type="line"
        className={styles.drawerHeaderTabs}
        activeTab={curDrawerTab.value}
        onChange={(value) => {
          setCurDrawerTab(value as ETLDrawerTab);
        }}
      >
        <Tabs.TabPane key={ETLDrawerTab.DATA_PREVIEW} title="数据预览"></Tabs.TabPane>
        <Tabs.TabPane key={ETLDrawerTab.NODE_REMARK} title="节点备注"></Tabs.TabPane>
      </Tabs>
    </div>
  );
};

export default DrawerHeader;
