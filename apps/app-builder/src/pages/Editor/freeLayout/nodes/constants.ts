/**
 * Copyright (c) 2025 Bytedance Ltd. and/or its affiliates
 * SPDX-License-Identifier: MIT
 */

export enum WorkflowNodeType {
  // Start = 'c',
  // End = 'end',
  LLM = 'llm',
  HTTP = 'http',
  CODE = 'code',
  VARIABLE = 'variable',
  CONDITION = 'condition',
  LOOP = 'loop',
  BLOCKSTART = 'block-start',
  BLOCKEND = 'block-end',
  COMMENT = 'comment',
  CONTINUE = 'continue',
  BREAK = 'break',
  START = 'start',
  END = 'end',
  PROCESSNODE = 'process-node',
  INITIATION = 'initiation',
  APPROVER = 'approver',
  CCRECIPIENTS = 'cc',
  BRANCH_IN = 'branch-in',
  CONDITIONAL_BRANCH = 'conditional-branch',
  SINK_NODE_BRANCH='sink-node-branch',
  PARALLEL_BRANCH='parallel-branch',
}
