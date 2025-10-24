/**
 * Copyright (c) 2025 Bytedance Ltd. and/or its affiliates
 * SPDX-License-Identifier: MIT
 */

import { Button } from '@douyinfe/semi-ui';
import { IconPlus } from '@douyinfe/semi-icons';

import { useAddNode } from './use-add-node';
import { WorkflowDragService, useService, useClientContext } from '@flowgram.ai/free-layout-editor';

import { LLMNodeRegistry } from '../../nodes/llm/index';
export const AddNode = (props: { disabled: boolean; onSave: any }) => {
  const addNode = useAddNode();
  const startDragSerivce = useService<WorkflowDragService>(WorkflowDragService);
  const onSaveTest = () => {
    props.onSave();
  };

  return (
    <>
      <Button
        data-testid="demo.free-layout.add-node"
        icon={<IconPlus />}
        color="highlight"
        style={{ backgroundColor: 'rgba(171,181,255,0.3)', borderRadius: '8px' }}
        disabled={props.disabled}
        onClick={(e) => {
          const rect = e.currentTarget.getBoundingClientRect();
          addNode(rect);
        }}
      >
        Add Node
      </Button>
      <Button
        onMouseDown={(e) =>
          startDragSerivce.startDragCard('node', e, {
            data: {
              type: LLMNodeRegistry.type,
              registry: LLMNodeRegistry
            }
          })
        }
      >
        {/* 点击就可以增加节点，拖拽也支持 */}
        测试拖拽节点
      </Button>
      <div
        onMouseDown={(e) =>
          startDragSerivce.startDragCard('node', e, {
            data: {
              title: `Newnode`,
              content: 'xxxx'
            }
          })
        }
      >
        拖拽
      </div>
      <Button onClick={() => onSaveTest()}>模拟保存</Button>
    </>
  );
};
