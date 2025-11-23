import { Tabs } from '@arco-design/web-react';
import { ETLDrawerTab, etlEditorSignal, ETLNodeType } from '@onebase/common';
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

  const { curNode, nodeData, curDrawerTab, setCurDrawerTab } = etlEditorSignal;

  return (
    <div className={styles.drawerHeader}>
      <NodeTitle title={nodeData.value[curNode.value.id]?.title || ''} />

      <Tabs
        type="line"
        className={styles.drawerHeaderTabs}
        activeTab={curDrawerTab.value}
        onChange={(value) => {
          setCurDrawerTab(value as ETLDrawerTab);
        }}
      >
        {curNode.value.flowNodeType !== ETLNodeType.INPUT_NODE && (
          <Tabs.TabPane key={ETLDrawerTab.DATA_CONFIG} title="节点配置"></Tabs.TabPane>
        )}
        {curNode.value.flowNodeType === ETLNodeType.JOIN_NODE && (
          <Tabs.TabPane key={ETLDrawerTab.FIELD_CONFIG} title="字段设置"></Tabs.TabPane>
        )}
        <Tabs.TabPane key={ETLDrawerTab.DATA_PREVIEW} title="数据预览"></Tabs.TabPane>
        <Tabs.TabPane key={ETLDrawerTab.NODE_REMARK} title="节点备注"></Tabs.TabPane>
      </Tabs>
    </div>
  );
};

export default DrawerHeader;
