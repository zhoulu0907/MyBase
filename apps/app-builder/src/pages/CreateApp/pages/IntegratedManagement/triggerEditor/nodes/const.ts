/**
 * 节点类型枚举
 * 用于流程编辑器中区分不同类型的节点
 */
export enum NodeType {
  START = 'start',
  START_FORM = 'startForm',
  START_ENTITY = 'startEntity',
  START_TIME = 'startTime',
  START_DATE_FIELD = 'startDateField',
  START_API = 'startAPI',
  START_BPM = 'startBPM',
  END = 'end',
  CASE = 'case',
  CASE_DEFAULT = 'caseDefault',
  IF = 'if',
  IF_BLOCK = 'ifBlock',
  SWITCH = 'switch',
  LOOP = 'loop',
  BREAK_LOOP = 'breakLoop',
  TRY_CATCH = 'tryCatch',
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
  NAGIVATE = 'nagivate',
  REFRESH = 'refresh',
  TOOLTIP = 'tooltip'
}
