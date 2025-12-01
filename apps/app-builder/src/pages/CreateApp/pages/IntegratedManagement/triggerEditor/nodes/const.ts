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
  tryBlock = '尝试',
  catchBlock = '异常捕获',

  // 数据节点
  dataAdd = '数据新增',
  dataCalc = '数据计算',
  dataDelete = '数据删除',
  dataQuery = '数据查询(单条)',
  dataQueryMultiple = '数据查询(多条)',
  dataUpdate = '数据更新',

  // 其他节点
  javascript = 'JS脚本',
  json = 'JSON',
  log = '日志',
  message = '消息',
  script = '脚本',
  dataMapper = '数据映射',

  // 交互节点
  modal = '弹窗',
  navigate = '跳转',
  refresh = '刷新',
  tooltip = '提示'
}
