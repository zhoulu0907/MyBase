import { Button, Tooltip } from '@arco-design/web-react';
import { WorkflowDragService, useService } from '@flowgram.ai/free-layout-editor';
import { IconQuestionCircle } from '@arco-design/web-react/icon';

import React from 'react';
import styles from './index.module.less';
import approver from './../../assets/bpmLogo/approver.png';
import executor_big from './../../assets/bpmLogo/executor_big.png';
import automation from './../../assets/bpmLogo/automation.png';
import ccto from './../../assets/bpmLogo/ccto.png';
import conditional_branch from './../../assets/bpmLogo/conditional_branch.png';
import parallel_branch from './../../assets/bpmLogo/parallel_branch.png';
import sink_node from './../../assets/bpmLogo/sink_node.png';
import wait from './../../assets/bpmLogo/wait.png';
import task from './../../assets/bpmLogo/task.png';
import message from './../../assets/bpmLogo/message.png';
import subprocessTwo from './../../assets/bpmLogo/subprocessTwo.png';
import copy from './../../assets/bpmLogo/copy.png';
import deleteIcon from './../../assets/bpmLogo/deleteIcon.png';
import classNames from 'classnames';
import { ApproverNodeRegistry, CcRecipientsNodeRegistry, ExecutorNodeRegistry } from '../../nodes/index';

/**
 * 流程编辑页面
 * 集成触发器编辑器作为主内容
 */
const LeftNavBar: React.FC = () => {
  const nodeList = [
    {
      navTitle: '人工节点',
      navList: [
        { img: approver, title: '审批人', type: 'approver', registry: ApproverNodeRegistry },
        { img: executor_big, title: '执行人', type: 'executor', registry: ExecutorNodeRegistry },
        { img: ccto, title: '抄送人', type: 'ccRecipients', registry: CcRecipientsNodeRegistry }
      ]
    },
    {
      navTitle: '分支节点',
      navList: [
        { img: conditional_branch, title: '条件分支' },
        { img: parallel_branch, title: '并行分支' },
        { img: sink_node, title: '汇聚节点' }
      ],
      tips: true
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
  const CustomTooltipContent = () => {
    return (
      <div className={styles.tipContent}>
        <div>条件分支：按分支的优先级排序，仅执行满足条件的第一个分支</div>
        <div>并行分支：分支间无优先级，满足条件的分支都会执行</div>
        <div>汇聚节点：等待所有应到达的上游分支完成，合并路径后继续流程</div>
      </div>
    );
  };

  return (
    <div className={styles.leftNav}>
      <div className={classNames(styles.process, styles.processNodeTitle)}>流程节点</div>
      <div className={styles.innerNodesBox}>
        {nodeList?.map((item, i) => (
          <div key={i}>
            <div className={styles.navTitleColor}>
              {item.navTitle}
              {item.tips && (
                <Tooltip position="tl" trigger="hover" content={<CustomTooltipContent />}>
                  <IconQuestionCircle style={{ fontSize: '15px', marginLeft: '4px', color: '#AAAEB3' }} />
                </Tooltip>
              )}
            </div>
            {/* 左侧子节点 */}
            {item.navList.map((nodeItem:any, index) => (
              <Button
                className={styles.nodeItem}
                key={index}
                onMouseDown={(e) =>{
                    if(nodeItem?.type){
                        startDragSerivce.startDragCard(nodeItem?.type, e, {
                          data: {
                            name: nodeItem?.title,
                            registry: nodeItem?.registry
                          }
                        });

                    }else{
                         startDragSerivce.startDragCard('node', e, {
                           data: {
                             title: `${nodeItem.title}`
                           }
                         });
                    }
                }
                  
                }
              >
                <img
                  src={nodeItem.img}
                  alt=""
                  style={{ display: 'inline-block', verticalAlign: 'middle' }}
                  loading="lazy"
                  width="20px"
                  height="20px"
                />
                <span style={{ fontSize: '10px', marginLeft: '2px' }}>{nodeItem.title}</span>
              </Button>
            ))}
          </div>
        ))}
      </div>

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
  );
};

export default LeftNavBar;
