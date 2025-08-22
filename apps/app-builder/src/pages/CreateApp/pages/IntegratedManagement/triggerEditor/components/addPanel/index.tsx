import { WorkflowDragService, useService } from '@flowgram.ai/free-layout-editor';
import React from 'react';
import { WorkflowNodeType } from '../../node/constants';
import styles from './index.module.less';

/**
 * 节点添加面板组件
 * 该组件用于展示可添加的节点类型，支持拖拽到编辑区
 * 目前为占位实现，后续可根据实际需求扩展
 */
const NodeAddPanel: React.FC = () => {
  const startDragSerivce = useService<WorkflowDragService>(WorkflowDragService);

  const nodes = [
    {
      type: 'branch',
      name: '分支节点'
    },
    {
      type: 'loop',
      name: '循环节点'
    },
    {
      type: WorkflowNodeType.Start,
      name: '开始节点'
    }
  ];

  return (
    <div className={styles.nodeAddPanel}>
      <div style={{ fontWeight: 600, marginBottom: 8 }}>添加节点</div>
      <div>
        {nodes.map((node) => (
          <div
            key={node.type}
            className={styles.nodeCard}
            style={{ marginBottom: 8, cursor: 'pointer' }}
            onMouseDown={(e) =>
              startDragSerivce.startDragCard(node.type, e, {
                type: node.type,
                data: {
                  title: `${node.name}`
                }
              })
            }
          >
            {node.name}
          </div>
        ))}
      </div>
    </div>
  );
};

export default NodeAddPanel;
