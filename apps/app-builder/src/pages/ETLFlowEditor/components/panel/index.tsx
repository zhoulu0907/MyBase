import NodeInputIcon from '@/assets/images/etl/node_input.svg';
import NodeJoinIcon from '@/assets/images/etl/node_join.svg';
import NodeOutputIcon from '@/assets/images/etl/node_output.svg';
import NodeUnionIcon from '@/assets/images/etl/node_union.svg';
import { useService, WorkflowDragService } from '@flowgram.ai/free-layout-editor';
import { etlEditorSignal, ETLNodeType } from '@onebase/common';
import React from 'react';
import styles from './index.module.less';
import { generateNodeId } from './utils';

/**
 * ETL 流程编辑器侧边栏面板组件
 */

const NodeList = [
  {
    category: '输入输出',
    nodes: [
      {
        type: ETLNodeType.INPUT_NODE,
        name: '输入',
        icon: <img src={NodeInputIcon} alt="input" />
      },
      {
        type: ETLNodeType.OUTPUT_NODE,
        name: '输出',
        icon: <img src={NodeOutputIcon} alt="output" />
      }
    ]
  },
  {
    category: '数据处理',
    nodes: [
      {
        type: ETLNodeType.JOIN_NODE,
        name: '横向连接',
        icon: <img src={NodeJoinIcon} alt="join" />
      },
      {
        type: ETLNodeType.UNION_NODE,
        name: '追加合并',
        icon: <img src={NodeUnionIcon} alt="union" />
      }
    ]
  }
];
const ETLFlowPanel: React.FC = () => {
  const startDragSerivce = useService<WorkflowDragService>(WorkflowDragService);
  const { setNodeData } = etlEditorSignal;

  return (
    <div className={styles.panel}>
      {NodeList.map((item) => (
        <div className={styles.nodeCategory} key={item.category}>
          <div className={styles.nodeCategoryTitle}>{item.category}</div>

          <div className={styles.nodeList}>
            {item.nodes.map((node) => (
              <div
                className={styles.node}
                key={node.type}
                onMouseDown={(e) => {
                  const nodeId = generateNodeId(node.type);
                  startDragSerivce.startDragCard(node.type.toLowerCase(), e, {
                    id: nodeId,
                    data: {
                      id: nodeId,
                      title: `${node.name}节点`,
                      type: node.type
                    }
                  });

                  setNodeData(nodeId, {
                    id: nodeId,
                    title: `${node.name}节点`,
                    type: node.type
                  });
                }}
              >
                {node.icon}
                <span className={styles.nodeText}>{node.name}</span>
              </div>
            ))}
          </div>
        </div>
      ))}
    </div>
  );
};

export default ETLFlowPanel;
