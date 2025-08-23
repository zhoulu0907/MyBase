import BranchIcon from '@/assets/flow/branch_icon.svg';
import { Tabs } from '@arco-design/web-react';
import { WorkflowDragService, useService } from '@flowgram.ai/free-layout-editor';
import React from 'react';
import { ALL_NODE_TYPES, WorkflowNodeType } from '../../node/constants';
import styles from './index.module.less';

const TabPane = Tabs.TabPane;

/**
 * 节点添加面板组件
 * 该组件用于展示可添加的节点类型，支持拖拽到编辑区
 * 目前为占位实现，后续可根据实际需求扩展
 */
const NodeAddPanel: React.FC = () => {
  const startDragSerivce = useService<WorkflowDragService>(WorkflowDragService);

  return (
    <div className={styles.nodeAddPanel}>
      <div className={styles.panelTitle}>节点库</div>
      <div className={styles.nodeCardContainer}>
        <Tabs defaultActiveTab={WorkflowNodeType.All} size="mini" type="line">
          {ALL_NODE_TYPES.map((nodeType) => (
            <TabPane key={nodeType.type} title={nodeType.type} className={styles.nodeCardTab}>
              {nodeType.nodes.map((nodeItem) => (
                <div
                  key={nodeItem.type}
                  className={styles.nodeCard}
                  style={{ marginBottom: 8, cursor: 'pointer' }}
                  onMouseDown={(e) =>
                    startDragSerivce.startDragCard(nodeItem.type, e, {
                      type: nodeItem.type,
                      data: {
                        title: `${nodeItem.name}`
                      }
                    })
                  }
                >
                  <img className={styles.nodeCardIcon} src={BranchIcon} />

                  <div className={styles.nodeCardName}>{nodeItem.name}</div>
                </div>
              ))}
            </TabPane>
          ))}
        </Tabs>
      </div>
    </div>
  );
};

export default NodeAddPanel;
