import iconStartForm from '@/assets/flow/nodes/startForm.svg';
import type { TriggerRange } from '../../../components/const';
import { type FlowNodeRegistry } from '../../../typings';
import { NodeType } from '@onebase/common';
import { formMeta } from './form-meta';

export const StartFormNodeRegistry: FlowNodeRegistry = {
  type: NodeType.START_FORM,
  title: '界面交互触发节点',
  category: 'trigger',
  meta: {
    isStart: true, // Mark as start
    draggable: false, // 开始节点无法拖拽
    deleteDisable: true, // Start node cannot delete
    selectable: false, // Start node cannot select
    copyDisable: true, // Start node cannot copy
    expandable: false, // disable expanded
    addDisable: true // Start Node cannot be added
  },
  info: {
    icon: iconStartForm,
    description: '这是界面交互触发节点，用于设置启动工作流所需的信息。'
  },
  /**
   * Render node via formMeta
   */
  formMeta
};

export interface StartFormNodeOutput {
  pageId: string;
  fieldId: string;
  triggerRange: TriggerRange;
}
