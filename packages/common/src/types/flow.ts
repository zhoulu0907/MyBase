/**
 * 节点类型枚举
 * 用于流程编辑器中区分不同类型的节点
 */
export enum NodeType {
  START_FORM = 'startForm',
  START_ENTITY = 'startEntity',
  START_TIME = 'startTime',
  START_DATE_FIELD = 'startDateField',
  START_API = 'startAPI',
  START_BPM = 'startBPM',

  END = 'end',
  CASE = 'switchCase',
  CASE_DEFAULT = 'switchDefault',
  IF = 'ifCase',
  IF_BLOCK = 'ifBlock',
  SWITCH = 'switchCondition',
  LOOP = 'loop',
  BREAK_LOOP = 'breakLoop',
  TRY_CATCH = 'tryCatch',
  TRY_BLOCK = 'tryBlock',
  CATCH_BLOCK = 'catchBlock',

  DATA_ADD = 'dataAdd',
  DATA_CALC = 'dataCalc',
  DATA_DELETE = 'dataDelete',
  DATA_QUERY = 'dataQuery',
  DATA_QUERY_MULTIPLE = 'dataQueryMultiple',
  DATA_UPDATE = 'dataUpdate',

  IPAAS = 'ipaas',
  JSON = 'json',
  LOG = 'log',
  MESSAGE = 'message',
  SCRIPT = 'script',
  DATA_MAPPER = 'dataMapper',

  MODAL = 'modal',
  NAVIGATE = 'navigate',
  REFRESH = 'refresh',
  TOOLTIP = 'tooltip'
}

// 弹窗类型
export enum FLOW_MODAL_TYPE {
  CONFIRM = 'confirm', // 二次确认
  INFOR = 'infor', // 收集信息
  CUSTOM = 'custom'
}

// 弹窗取消后
export enum FLOW_MODAL_CANCEL {
  STOP = 0, // 事件终止
  CONTINUE = 1, // 事件继续执行
}
