import { triggerEditorSignal } from '@/store/singals/trigger_editor';
import { triggerNodeOutputSignal } from '@/store/singals/trigger_node_output';
import { Button } from '@arco-design/web-react';
import { WorkflowDragService, useService } from '@flowgram.ai/free-layout-editor';
import { LLMNodeRegistry } from './../../freeLayout/nodes/llm/index';

// import { ProcessStatus, updateFlowMgmtDefinition } from '@onebase/app';
import React from 'react';
import { FlowEditor } from '../../freeLayout/index';
import styles from './index.module.less';
import approver from './../../freeLayout/assets/bpmLogo/approver.png';
import executor_big from './../../freeLayout/assets/bpmLogo/executor_big.png';
import automation from './../../freeLayout/assets/bpmLogo/automation.png';
import ccto from './../../freeLayout/assets/bpmLogo/ccto.png';
import conditional_branch from './../../freeLayout/assets/bpmLogo/conditional_branch.png';
import parallel_branch from './../../freeLayout/assets/bpmLogo/parallel_branch.png';
import sink_node from './../../freeLayout/assets/bpmLogo/sink_node.png';
import wait from './../../freeLayout/assets/bpmLogo/wait.png';
import task from './../../freeLayout/assets/bpmLogo/task.png';
import message from './../../freeLayout/assets/bpmLogo/message.png';
import subprocessTwo from './../../freeLayout/assets/bpmLogo/subprocessTwo.png';
import copy from './../../freeLayout/assets/bpmLogo/copy.png';
import deleteIcon from './../../freeLayout/assets/bpmLogo/deleteIcon.png';

/**
 * 流程编辑页面
 * 集成触发器编辑器作为主内容
 */
const FlowEditorPage: React.FC = () => {
  // const { nodeData, nodes, flowId } = triggerEditorSignal;
  // const { getTriggerNodeOutput } = triggerNodeOutputSignal;
  const nodeList = [
    {
      navTitle: '人工节点',
      navList: [
        { img: approver, title: '审批人' },
        { img: executor_big, title: '执行人' },
        { img: ccto, title: '抄送人' }
      ]
    },
    {
      navTitle: '分支节点',
      navList: [
        { img: conditional_branch, title: '条件分支' },
        { img: parallel_branch, title: '并行分支' },
        { img: sink_node, title: '汇聚节点' }
      ]
    },
    {
      navTitle: '逻辑节点',
      navList: [
        { img: subprocessTwo, title: '子流程' },
        { img: wait, title: '等待' },
        { img: automation, title: '自动化' },
        { img: task, title: '任务' }
      ]
    },
    {
      navTitle: '消息节点',
      navList: [{ img: message, title: '消息通知' }]
    }
  ];

  const startDragSerivce = useService<WorkflowDragService>(WorkflowDragService);

  return (
    <div className={styles.flowEditorPage}>
      <div className={styles.body}>
        <div className={styles.leftNav}>
          <div className={styles.processNodeTitle}>流程节点</div>

          {nodeList.map((item) => (
            <div className={styles.processNodeTitle}>
              <div className={styles.navTitleColor}> {item.navTitle}</div>
              {/* 左侧子节点 */}
              {item.navList.map((nodeItem) => (
                <Button
                  className={styles.nodeContent}
                  onMouseDown={(e) =>
                    startDragSerivce.startDragCard('node', e, {
                      data: {
                        type: LLMNodeRegistry.type,
                        registry: LLMNodeRegistry
                      }
                    })
                  }
                >
                  <img src={nodeItem.img} alt="" loading="lazy" />
                  <div className={styles.nodeTitle}>{nodeItem.title}</div>
                </Button>
              ))}
            </div>
          ))}
          <div className={styles.line}></div>
          <div className={styles.remark}>
            <div className={styles.copy}>
              <img src={copy} alt="" loading="lazy" width="12px" height="13px" />
              <span>复制</span>
            </div>
            <div>
              <img src={deleteIcon} alt="" loading="lazy" width="13px" height="13px" />
              <span className={styles.delete}>删除</span>
            </div>
          </div>
        </div>
        <FlowEditor />
      </div>
    </div>
  );
};

export default FlowEditorPage;
