/**
 * Copyright (c) 2025 Bytedance Ltd. and/or its affiliates
 * SPDX-License-Identifier: MIT
 */

import { useState } from 'react';

import { getIcon } from './utils';
import { TitleInput } from './title-input';
import { Header } from './styles';
import { useNodeRenderContext } from '../../hooks';

export function FormProcess() {
  const { node, readonly } = useNodeRenderContext();
  const [titleEdit, updateTitleEdit] = useState<boolean>(false);

  return (
    <Header>
      {getIcon(node)}
      <TitleInput readonly={readonly} updateTitleEdit={updateTitleEdit} titleEdit={titleEdit} />
    </Header>
  );
}
