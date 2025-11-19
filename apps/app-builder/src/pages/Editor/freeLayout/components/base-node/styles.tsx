/**
 * Copyright (c) 2025 Bytedance Ltd. and/or its affiliates
 * SPDX-License-Identifier: MIT
 */

import styled from 'styled-components';
import { IconInfoCircle } from '@douyinfe/semi-icons';

export const NodeWrapperStyle = styled.div`
  align-items: flex-start;
  background-color: #fff;
  border-radius: 4px;
  box-shadow:
    0 4px 4px 0 rgba(0, 0, 0, 0.15),
    0 4px 4px 0 rgba(0, 0, 0, 0.15);
  display: flex;
  flex-direction: column;
  justify-content: center;
  position: relative;
  width: 120px;
  height: auto;

  &.selected {
    border: 2px solid #65bf73;
  }

  &.completedBorder {
    border: 2px solid #00b42a;
  }
  &.processingBorder {
    border: 2px solid #165dff;
  }
  &.pendingBorder {
    border: 2px solid #e5e6eb;
  }
  &.error {
    border: 1px solid red;
  }
`;

export const ErrorIcon = () => (
  <IconInfoCircle
    style={{
      position: 'absolute',
      color: 'red',
      left: -6,
      top: -6,
      zIndex: 1,
      background: 'white',
      borderRadius: 8
    }}
  />
);
