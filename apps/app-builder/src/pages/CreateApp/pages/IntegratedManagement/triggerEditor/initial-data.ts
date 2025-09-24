import { TriggerRange } from './components/const';
import { type FlowDocumentJSON } from './typings';

export const EndInitData = {
  id: 'end_0',
  type: 'end',
  blocks: [],
  data: {
    title: '结束',
    initialData: {}
  }
};

export const StartFormInitData: FlowDocumentJSON = {
  nodes: [
    {
      id: 'start_form_0',
      type: 'startForm',
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
};

export const StartEntityInitData: FlowDocumentJSON = {
  nodes: [
    {
      id: 'start_entity_0',
      type: 'startEntity',
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
};

export const StartTimeInitData: FlowDocumentJSON = {
  nodes: [
    {
      id: 'start_time_0',
      type: 'startTime',
      blocks: [],
      data: {
        title: '定时触发节点',
        initialData: {
          id: 'start_time_0',
          repeatType: 'none',
          startTime: '',
          endTime: '',
          triggerTime: '',
          repeatWeek: [],
          repeatDay: [],
          triggerDate: ''
        },
        outputs: {
          type: 'object',
          properties: {}
        }
      }
    },
    EndInitData
  ]
};

export const StartDateFieldInitData: FlowDocumentJSON = {
  nodes: [
    {
      id: 'start_date_field_0',
      type: 'startDateField',
      blocks: [],
      data: {
        title: '日期字段触发节点',
        initialData: {
          id: 'start_date_field_0',
          entityId: '',
          batchMode: false,
          batchSize: 100,
          offsetMode: 'none',
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
};

export const StartApiInitData: FlowDocumentJSON = {
  nodes: [
    {
      id: 'start_api_0',
      type: 'startAPI',
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
};

export const StartBpmInitData: FlowDocumentJSON = {
  nodes: [
    {
      id: 'start_bpm_0',
      type: 'startBPM',
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
};
