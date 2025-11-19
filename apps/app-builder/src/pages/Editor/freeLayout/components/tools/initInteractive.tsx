/**
 * Copyright (c) 2025 Bytedance Ltd. and/or its affiliates
 * SPDX-License-Identifier: MIT
 */

import { useEffect } from 'react';

import { usePlaygroundTools, type InteractiveType as IdeInteractiveType } from '@flowgram.ai/free-layout-editor';
export const CACHE_KEY = 'workflow_prefer_interactive_type';
export const SHOW_KEY = 'show_workflow_interactive_type_guide';
export const IS_MAC_OS = /(Macintosh|MacIntel|MacPPC|Mac68K|iPad)/.test(navigator.userAgent);

export const getPreferInteractiveType = () => {
  const data = localStorage.getItem(CACHE_KEY) as string;
  if (data && [InteractiveType.Mouse, InteractiveType.Pad].includes(data as InteractiveType)) {
    return data;
  }
  return IS_MAC_OS ? InteractiveType.Pad : InteractiveType.Mouse;
};

export const setPreferInteractiveType = (type: InteractiveType) => {
  localStorage.setItem(CACHE_KEY, type);
};

export enum InteractiveType {
  Mouse = 'MOUSE',
  Pad = 'PAD'
}

const InitInteractive = () => {
  const tools = usePlaygroundTools();
  useEffect(() => {
    tools.setMouseScrollDelta((zoom) => zoom / 20);
    const preferInteractiveType = getPreferInteractiveType();
    tools.setInteractiveType(preferInteractiveType as IdeInteractiveType);
  }, []);

  return <></>;
};

export default InitInteractive;
