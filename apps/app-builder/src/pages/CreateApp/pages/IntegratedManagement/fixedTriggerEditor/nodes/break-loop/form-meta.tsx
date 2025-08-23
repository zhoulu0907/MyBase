/**
 * Copyright (c) 2025 Bytedance Ltd. and/or its affiliates
 * SPDX-License-Identifier: MIT
 */

import { type FormMeta } from '@flowgram.ai/fixed-layout-editor';

import { FormHeader } from '../../form-components';

export const renderForm = () => (
  <>
    <FormHeader />
  </>
);

export const formMeta: FormMeta = {
  render: renderForm
};
