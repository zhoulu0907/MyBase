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
  CASE = 'case',
  CASE_DEFAULT = 'caseDefault',
  IF = 'ifCase',
  IF_BLOCK = 'ifBlock',
  SWITCH = 'switch',
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
  NAGIVATE = 'nagivate',
  REFRESH = 'refresh',
  TOOLTIP = 'tooltip'
}

/**
 * 节点中文名称枚举
 * 展示不同的名称
 */

export enum NodeTypeName {
  // 开始节点
  startForm = '界面交互触发',
  startEntity = '表单(实体)触发',
  startTime = '定时触发',
  startDateField = '日期字段触发',
  startAPI = 'API触发',
  startBPM = '子流程触发',

  // 控制节点
  end = '结束',
  case = '分支',
  caseDefault = '默认分支',
  ifCase = '条件',
  ifBlock = '条件分支',
  switch = '分支',
  loop = '循环',
  breakLoop = '循环结束',
  tryCatch = '异常处理',
  tryBlock = '异常捕获',
  catchBlock = '尝试',

  // 数据节点
  dataAdd = '数据新增',
  dataCalc = '数据计算',
  dataDelete = '数据删除',
  dataQuery = '数据查询(单条)',
  dataQueryMultiple = '数据查询(多条)',
  dataUpdate = '数据更新',

  // 其他节点
  ipaas = '连接器',
  json = 'JSON',
  log = '日志',
  message = '消息',
  script = '脚本',
  dataMapper = '数据映射',

  // 交互节点
  modal = '弹窗',
  nagivate = '跳转',
  refresh = '刷新',
  tooltip = '提示'
}
