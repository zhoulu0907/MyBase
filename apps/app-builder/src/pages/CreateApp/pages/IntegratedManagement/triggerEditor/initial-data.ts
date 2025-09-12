import { TriggerRange } from './components/const';
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
          properties: {}
        }
      }
    },

    {
      id: 'end_0',
      type: 'end',
      blocks: [],
      data: {
        title: '结束'
      }
    }
  ]
};

export const EndInitData = {
  id: 'end_0',
  type: 'end',
  blocks: [],
  data: {
    title: '结束',
    initialData: {}
  }
};

export const StartFormInitData = {
  nodes: [
    {
      id: 'start_form_0',
      type: 'start_form',
      blocks: [],
      data: {
        title: '界面交互触发节点',
        initialData: {
          id: 'start_form_0',
          triggerRange: TriggerRange.Record,
          filterCondition: [],
          isChildTriggerAllowed: false
        },
        outputs: {
          type: 'object',
          properties: {}
        }
      }
    },
    EndInitData
  ]
} as FlowDocumentJSON;

export const StartEntityInitData = {
  nodes: [
    {
      id: 'start_entity_0',
      type: 'start_entity',
      blocks: [],
      data: {
        title: '表单(实体)触发节点',
        initialData: {
          id: 'start_entity_0',
          entityId: ''
        },
        outputs: {
          type: 'object',
          properties: {}
        }
      }
    },
    EndInitData
  ]
} as FlowDocumentJSON;

export const StartTimeInitData = {
  nodes: [
    {
      id: 'start_time_0',
      type: 'start_time',
      blocks: [],
      data: {
        title: '定时触发节点',
        initialData: {
          id: 'start_time_0'
        },
        outputs: {
          type: 'object',
          properties: {}
        }
      }
    },
    EndInitData
  ]
} as FlowDocumentJSON;

export const StartDateFieldInitData = {
  nodes: [
    {
      id: 'start_date_field_0',
      type: 'start_date_field',
      blocks: [],
      data: {
        title: '日期字段触发节点',
        initialData: {
          id: 'start_date_field_0',
          entityId: '',
          batchMode: false,
          batchSize: 100,
          offsetMode: 0,
          offsetValue: 0,
          offsetUnit: 'day',
          dailyExecTime: '00:00'
        },
        outputs: {
          type: 'object',
          properties: {}
        }
      }
    },
    EndInitData
  ]
} as FlowDocumentJSON;

export const StartApiInitData = {
  nodes: [
    {
      id: 'start_api_0',
      type: 'start_api',
      blocks: [],
      data: {
        title: 'API触发节点',
        outputs: {
          type: 'object',
          properties: {}
        }
      }
    },
    EndInitData
  ]
} as FlowDocumentJSON;

export const StartBpmInitData = {
  nodes: [
    {
      id: 'start_bpm_0',
      type: 'start_bpm',
      blocks: [],
      data: {
        title: '子流程触发节点',
        outputs: {
          type: 'object',
          properties: {}
        }
      }
    },
    EndInitData
  ]
} as FlowDocumentJSON;
