/**
 * Copyright (c) 2025 Bytedance Ltd. and/or its affiliates
 * SPDX-License-Identifier: MIT
 */

import { useCallback } from 'react';
import { Tooltip } from '@arco-design/web-react';
import { FlowNodeEntity, useNodeRender } from '@flowgram.ai/free-layout-editor';
import { ConfigProvider } from '@douyinfe/semi-ui';
import { IconInfoCircle } from '@douyinfe/semi-icons';

import { NodeStatusBar } from '../testrun/node-status-bar';
import { NodeRenderContext } from '../../context';
import { NodeWrapper } from './node-wrapper';

const getAllErrorMessages = (form: any) => {
  if (!form?.state?.errors) return [];
  const errorMessages: string[] = [];
  Object.values(form.state.errors).forEach((fieldErrors: any) => {
    if (Array.isArray(fieldErrors)) {
      fieldErrors.forEach((error: any) => {
        if (error?.message && typeof error.message === 'string' && error.message.trim() !== '') {
          errorMessages.push(error.message);
        }
      });
    }
  });

  return errorMessages;
};
export const BaseNode = ({ node }: { node: FlowNodeEntity }) => {
  /**
   * Provides methods related to node rendering
   * 提供节点渲染相关的方法
   */
  const nodeRender = useNodeRender();
  /**
   * It can only be used when nodeEngine is enabled
   * 只有在节点引擎开启时候才能使用表单
   */
  const form = nodeRender.form;

const errorMessages = getAllErrorMessages(form);
  /**
   * Used to make the Tooltip scale with the node, which can be implemented by itself depending on the UI library
   * 用于让 Tooltip 跟随节点缩放, 这个可以根据不同的 ui 库自己实现
   */
  const getPopupContainer = useCallback(() => node.renderData.node || document.body, []);

  return (
    <ConfigProvider getPopupContainer={getPopupContainer}>
      <NodeRenderContext.Provider value={nodeRender}>
        <NodeWrapper>
          {form?.state.invalid && (
            <div
              style={{
                position: 'absolute',
                right: -30,
                top: -6,
                zIndex: 99
              }}
            >
              <Tooltip
                content={
                  <div>
                    {errorMessages?.map((message, index) => (
                      <div key={index}>{message}</div>
                    ))}
                  </div>
                }
              >
                <IconInfoCircle
                  style={{
                    color: 'red',
                    borderRadius: 8,
                    display: 'inline-block',
                    padding: '6px'
                  }}
                />
              </Tooltip>
            </div>
          )}
          {form?.render()}
        </NodeWrapper>
        <NodeStatusBar />
      </NodeRenderContext.Provider>
    </ConfigProvider>
  );
};
