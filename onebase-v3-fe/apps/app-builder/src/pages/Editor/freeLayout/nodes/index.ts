/**
 * Copyright (c) 2025 Bytedance Ltd. and/or its affiliates
 * SPDX-License-Identifier: MIT
 */

import { type FlowNodeRegistry } from '../typings';
import { VariableNodeRegistry } from './variable';
import { StartNodeRegistry } from './start';
import { LoopNodeRegistry } from './loop';
import { LLMNodeRegistry } from './llm';
import { HTTPNodeRegistry } from './http';
import { GroupNodeRegistry } from './group';
import { EndNodeRegistry } from './end';
import { ContinueNodeRegistry } from './continue';
import { ConditionNodeRegistry } from './condition';
import { CommentNodeRegistry } from './comment';
import { CodeNodeRegistry } from './code';
import { BreakNodeRegistry } from './break';
import { BlockStartNodeRegistry } from './block-start';
import { BlockEndNodeRegistry } from './block-end';
import { BpmStartNodeRegistry } from './bpm-start';
import { BpmEndNodeRegistry } from './bpm-end';
import { ProcessNodeRegistry } from './process-node';
import { ModalNodeRegistry } from './modal/index';
import { ExecutorNodeRegistry } from './executor/index';
import { ApproverNodeRegistry } from './approver/index';
import { CcRecipientsNodeRegistry } from './CcRecipients/index';
import { InitiateNodeRegistry } from './initiate/index';
import { ParallelBranchNodeRegistry } from './parallel-branch';
import { ConditionalBranchNodeRegistry } from './conditional-branch';
import { SinkNodeBranchNodeRegistry } from './sink-node';

export { WorkflowNodeType } from './constants';

export const nodeRegistries: FlowNodeRegistry[] = [
  ConditionNodeRegistry,
  StartNodeRegistry,
  EndNodeRegistry,
  LLMNodeRegistry,
  LoopNodeRegistry,
  CommentNodeRegistry,
  BlockStartNodeRegistry,
  BpmStartNodeRegistry,
  BlockEndNodeRegistry,
  BpmEndNodeRegistry,
  HTTPNodeRegistry,
  CodeNodeRegistry,
  ContinueNodeRegistry,
  BreakNodeRegistry,
  VariableNodeRegistry,
  GroupNodeRegistry,
  ProcessNodeRegistry,
  ModalNodeRegistry,
  ExecutorNodeRegistry,
  ApproverNodeRegistry,
  CcRecipientsNodeRegistry,
  InitiateNodeRegistry,
  ParallelBranchNodeRegistry,
  ConditionalBranchNodeRegistry,
  SinkNodeBranchNodeRegistry
];
export {
  ExecutorNodeRegistry,
  ApproverNodeRegistry,
  CcRecipientsNodeRegistry,
  InitiateNodeRegistry,
  ParallelBranchNodeRegistry,
  ConditionalBranchNodeRegistry,
  SinkNodeBranchNodeRegistry
};
