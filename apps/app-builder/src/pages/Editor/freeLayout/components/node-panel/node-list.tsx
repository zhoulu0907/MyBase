/**
 * Copyright (c) 2025 Bytedance Ltd. and/or its affiliates
 * SPDX-License-Identifier: MIT
 */

import React, { type FC } from 'react';
import { Button } from '@douyinfe/semi-ui';

import styled from 'styled-components';
import type { NodePanelRenderProps } from '@flowgram.ai/free-node-panel-plugin';
import { useClientContext, WorkflowNodeEntity, WorkflowDragService, useService } from '@flowgram.ai/free-layout-editor';
import { Collapse, Tabs, Layout, Input } from '@arco-design/web-react';
import type { FlowNodeRegistry } from '../../typings';
import { nodeRegistries } from '../../nodes';
import IconCollapsedDown from '@workflow/images/collapse_down_icon.svg';
import './node-list.less';
import approver from '../../assets/bpmLogo/approver.png';
import executor_big from '../../assets/bpmLogo/executor_big.png';
import ccto from '../../assets/bpmLogo/ccto.png';
import conditional_branch from '../../assets/bpmLogo/conditional_branch.png';
import parallel_branch from '../../assets/bpmLogo/parallel_branch.png';
import sink_node from '../../assets/bpmLogo/sink_node.png';
import automation from '../../assets/bpmLogo/automation.png';
import subprocess from '../../assets/bpmLogo/subprocess.png';
import task from '../../assets/bpmLogo/task.png';
import wait from '../../assets/bpmLogo/wait.png';

import message from '../../assets/bpmLogo/message.png';
import { LLMNodeRegistry } from '../../nodes/llm/index';
import { ModalNodeRegistry } from '../../nodes/modal/index';
import {
  ApproverNodeRegistry,
  CcRecipientsNodeRegistry,
  ExecutorNodeRegistry,
  ParallelBranchNodeRegistry,
  HTTPNodeRegistry
} from '../../nodes/index';

interface NodeListProps {
  onSelect: NodePanelRenderProps['onSelect'];
  containerNode?: WorkflowNodeEntity;
}

export const NodeList: FC<NodeListProps> = (props) => {
  const { onSelect, containerNode } = props;
  const context = useClientContext();
  const handleClick = (e: React.MouseEvent, registry: FlowNodeRegistry) => {
    const json = registry.onAdd?.(context);
    onSelect({
      nodeType: registry.type as string,
      selectEvent: e,
      nodeJSON: json
    });
  };

  return (
    <div className="nodeListnodesWrap">
      <Collapse
        defaultActiveKey={['1', '2', '3', '4']}
        accordion={false}
        bordered={false}
        expandIconPosition="right"
        expandIcon={<img src={IconCollapsedDown} alt="" />}
      >
        <Collapse.Item className="collapseItem" header="人工节点" name="1">
          <div className="nodeItem" onClick={(e) => handleClick(e, ApproverNodeRegistry)}>
            <div className="nodeItemIcon">
              <img src={approver} alt="" />
            </div>
            审批人
          </div>
          <div className="nodeItem" onClick={(e) => handleClick(e, ExecutorNodeRegistry)}>
            <div className="nodeItemIcon">
              <img src={executor_big} alt="" />
            </div>
            执行人
          </div>
          <div className="nodeItem" onClick={(e) => handleClick(e, CcRecipientsNodeRegistry)}>
            <div className="nodeItemIcon">
              <img src={ccto} alt="" />
            </div>
            抄送人
          </div>
        </Collapse.Item>
        <Collapse.Item className="collapseItem" header="分支节点" name="2">
          <div className="nodeItem" onClick={(e) => handleClick(e, HTTPNodeRegistry)}>
            <div className="nodeItemIcon">
              <img src={conditional_branch} alt="" />
            </div>
            条件分支
          </div>
          <div className="nodeItem" onClick={(e) => handleClick(e, ParallelBranchNodeRegistry)}>
            <div className="nodeItemIcon">
              <img src={parallel_branch} alt="" />
            </div>
            并行分支
          </div>
          <div className="nodeItem">
            <div className="nodeItemIcon">
              <img src={sink_node} alt="" />
            </div>
            汇聚节点
          </div>
        </Collapse.Item>
        <Collapse.Item className="collapseItem" header="逻辑节点" name="3">
          <div className="nodeItem">
            <div className="nodeItemIcon">
              <img src={subprocess} alt="" />
            </div>
            子流程
          </div>
          <div className="nodeItem">
            <div className="nodeItemIcon">
              <img src={wait} alt="" />
            </div>
            等待
          </div>
          <div className="nodeItem">
            <div className="nodeItemIcon">
              <img src={automation} alt="" />
            </div>
            自动化
          </div>
          <div className="nodeItem">
            <div className="nodeItemIcon">
              <img src={task} alt="" />
            </div>
            任务
          </div>
        </Collapse.Item>
        <Collapse.Item className="collapseItem" header="消息节点" name="4">
          <div className="nodeItem">
            <div className="nodeItemIcon">
              <img src={message} alt="" />
            </div>
            消息通知
          </div>
        </Collapse.Item>
      </Collapse>
    </div>
  );
};
