/**
 * Copyright (c) 2025 Bytedance Ltd. and/or its affiliates
 * SPDX-License-Identifier: MIT
 */

import { type FlowNodeEntity } from '@flowgram.ai/free-layout-editor';

import { type FlowNodeRegistry } from '../../typings';

export const getIcon = (node: FlowNodeEntity) => {
  const icon = node.getNodeRegistry<FlowNodeRegistry>().info?.icon;
  if (!icon) return null;
  return <img src={icon} width={'100%'} height={'100%'} />;
};
