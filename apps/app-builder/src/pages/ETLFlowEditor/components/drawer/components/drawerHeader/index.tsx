import { Button, Tabs } from '@arco-design/web-react';
import { ETLDrawerTab, etlEditorSignal, ETLNodeType } from '@onebase/common';
import { useSignals } from '@preact/signals-react/runtime';
import React, { useEffect, useState } from 'react';
import NodeTitle from '../nodeTitle';
import styles from './index.module.less';
import { cloneDeep } from 'lodash-es';

/**
 * ETLFlowEditor 抽屉头部
 * 初始化页面，展示节点的标题
 * 后续可以按需扩展更多头部功能
 */
interface DrawerHeaderProps {
  onOk: (title: string) => void;
}

const DrawerHeader: React.FC<DrawerHeaderProps> = ({ onOk }) => {
  useSignals();

  const { curNode, nodeData, curDrawerTab, setCurDrawerTab } = etlEditorSignal;
  const [titleValue, setTitleValue] = useState(cloneDeep(nodeData.value[curNode.value.id]?.title) || '');

  useEffect(() => {
    const latestTitle = nodeData.value[curNode.value.id]?.title || '';
    setTitleValue(latestTitle);
  }, [curNode.value.id]);

  return (
    <div className={styles.drawerHeader}>
      <NodeTitle title={titleValue} onChange={setTitleValue}/>

      <Tabs
        type="line"
        className={styles.drawerHeaderTabs}
        activeTab={curDrawerTab.value}
        onChange={(value) => {
          setCurDrawerTab(value as ETLDrawerTab);
        }}
      >
        <Tabs.TabPane key={ETLDrawerTab.DATA_CONFIG} title="节点配置"></Tabs.TabPane>

        {curNode.value.flowNodeType === ETLNodeType.JOIN_NODE && (
          <Tabs.TabPane key={ETLDrawerTab.FIELD_CONFIG} title="字段设置"></Tabs.TabPane>
        )}
        <Tabs.TabPane key={ETLDrawerTab.DATA_PREVIEW} title="数据预览"></Tabs.TabPane>
        <Tabs.TabPane key={ETLDrawerTab.NODE_REMARK} title="节点备注"></Tabs.TabPane>
      </Tabs>

      <Button type="primary" onClick={() => onOk(titleValue)}>
        确定
      </Button>
    </div>
  );
};

export default DrawerHeader;
