/**
 * Copyright (c) 2025 Bytedance Ltd. and/or its affiliates
 * SPDX-License-Identifier: MIT
 */

import styled from 'styled-components';

export const Header = styled.div`
  box-sizing: border-box;
  display: flex;
  justify-content: flex-start;
  align-items: center;
  width: 116px;
  column-gap: 8px;
  border-radius: 4px;
  cursor: move;
  background: #fff;
  overflow: hidden;
  padding-left: 9px;
  padding-top: 4px;
  padding-bottom: 4px;
  box-sizing: border-box;
`;

export const Title = styled.div`
  font-size: 20px;
  flex: 1;
  width: 0;
`;

export const Icon = styled.img`
  width: 20px;
  height: 20px;
`;

export const Operators = styled.div`
  display: flex;
  align-items: center;
  column-gap: 4px;
`;
