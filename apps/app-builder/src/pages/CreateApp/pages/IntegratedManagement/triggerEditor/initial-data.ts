
import { type FlowDocumentJSON } from './typings';

export const initialData: FlowDocumentJSON = {
  nodes: [
    {
      id: 'start_0',
      type: 'start',
      blocks: [],
      data: {
        title: '触发节点',
        outputs: {
          type: 'object',
          properties: {

          },
        },
      },
    },

    {
      id: 'end_0',
      type: 'end',
      blocks: [],
      data: {
        title: '结束',
      },
    },
  ],
};
