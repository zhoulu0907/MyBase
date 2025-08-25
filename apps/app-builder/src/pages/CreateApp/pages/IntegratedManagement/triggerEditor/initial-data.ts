
import { type FlowDocumentJSON } from './typings';

export const initialData: FlowDocumentJSON = {
  nodes: [
    {
      id: 'start_0',
      type: 'start',
      blocks: [],
      data: {
        title: '开始',
        outputs: {
          type: 'object',
          properties: {
            // query: {
            //   type: 'string',
            //   default: 'Hello Flow.',
            // },
            // enable: {
            //   type: 'boolean',
            //   default: true,
            // },
            // array_obj: {
            //   type: 'array',
            //   items: {
            //     type: 'object',
            //     properties: {
            //       int: {
            //         type: 'number',
            //       },
            //       str: {
            //         type: 'string',
            //       },
            //     },
            //   },
            // },
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
        inputsValues: {
          success: { type: 'constant', content: true, schema: { type: 'boolean' } },
        },
      },
    },
  ],
};
