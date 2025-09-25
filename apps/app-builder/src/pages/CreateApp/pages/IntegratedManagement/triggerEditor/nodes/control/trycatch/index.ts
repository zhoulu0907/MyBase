import iconTryCatch from '@/assets/flow/icon-trycatch.svg';
import { v4 as uuidv4 } from 'uuid';
import { type FlowNodeRegistry } from '../../../typings';
import { NodeType } from '../../const';
import { formMeta } from './form-meta';

export const TryCatchNodeRegistry: FlowNodeRegistry = {
  type: NodeType.TRY_CATCH,
  title: '异常处理节点',
  category: 'control',
  info: {
    icon: iconTryCatch,
    description: '异常捕获'
  },
  meta: {
    expandable: false // disable expanded
  },
  formMeta,
  onAdd() {
    return {
      id: `tryCatch${uuidv4()}`,
      type: NodeType.TRY_CATCH,
      data: {
        title: '异常处理'
      },
      blocks: [
        {
          id: `tryBlock${uuidv4()}`,
          type: 'tryBlock',
          blocks: []
        },
        {
          id: `catchBlock${uuidv4()}`,
          type: 'catchBlock',
          blocks: [],
          data: {
            title: '异常捕获_1'
          }
        }
      ]
    };
  }
};
