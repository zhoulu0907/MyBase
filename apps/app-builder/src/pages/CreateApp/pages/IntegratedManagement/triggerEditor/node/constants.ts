export enum WorkflowNodeType {
  All = '全部',
  Control = '控制节点',
  Data = '数据节点',
  Interactive = '交互节点',
  Other = '其他节点',
}

export enum WorkflowNode {
  Start = 'start',             // 开始节点
  // 控制节点
  ControlTrigger = 'control_trigger',         // 触发节点
  ControlEnd = 'control_end',                 // 结束节点
  ControlBranch = 'control_branch',           // 分支节点
  ControlLoop = 'control_loop',               // 循环节点
  ControlDelay = 'control_delay',             // 延迟节点
  ControlSubProcess = 'control_sub_process',  // 子流程调用节点
  ControlException = 'control_exception',     // 异常处理节点

  // 数据节点
  DataQuery = 'data_query',             // 数据查询节点
  DataCreate = 'data_create',           // 数据新增节点
  DataUpdate = 'data_update',           // 数据更新节点
  DataDelete = 'data_delete',           // 数据删除节点
  DataCompute = 'data_compute',         // 数据计算节点

  // 其他节点
  MessageNotify = 'message_notify',         // 消息通知节点
  LogRecord = 'log_record',                 // 日志记录节点
  Script = 'script',                        // 脚本节点
  Connector = 'connector',                  // 连接器节点
  JsonParse = 'json_parse',                 // JSON 解析节点
  DataMapping = 'data_mapping',             // 数据映射节点

  // 交互节点
  InteractivePopup = 'interactive_popup',                    // 弹窗节点
  InteractivePageRedirect = 'interactive_page_redirect',     // 页面跳转节点
  InteractiveMessageTip = 'interactive_message_tip',         // 消息提示节点
  InteractiveRefresh = 'interactive_refresh',                // 刷新节点
}

export const CONTROL_NODES = [
    {
        type: WorkflowNode.ControlTrigger,
        name: '触发节点',
    },
    {
        type: WorkflowNode.ControlEnd,
        name: '结束节点',
    },
    {
        type: WorkflowNode.ControlBranch,
        name: '分支节点',
    },
    {
        type: WorkflowNode.ControlLoop,
        name: '循环节点',
    },
    {
        type: WorkflowNode.ControlDelay,
        name: '延迟节点',
    },
    {
        type: WorkflowNode.ControlException,
        name: '异常处理节点',
    },
]

export const DATA_NODES = [
    {
        type:   WorkflowNode.DataQuery,
        name: '数据查询节点',
    },
    {
        type: WorkflowNode.DataCreate,
        name: '数据新增节点',
    },
    {
        type: WorkflowNode.DataUpdate,
        name: '数据更新节点',
    },
    {
        type: WorkflowNode.DataDelete,
        name: '数据删除节点',
    },
    {
        type: WorkflowNode.DataCompute,
        name: '数据计算节点',
    },
]

export const INTERACTIVE_NODES = [
    {
        type:   WorkflowNode.InteractivePopup,
        name: '弹窗节点',
    },
    {
        type: WorkflowNode.InteractivePageRedirect,
        name: '页面跳转节点',
    },
    {
        type: WorkflowNode.InteractiveMessageTip,
        name: '消息提示节点',
    },
    {
        type: WorkflowNode.InteractiveRefresh,
        name: '刷新节点',
    },
];

export const OTHER_NODES = [
    {
        type: WorkflowNode.MessageNotify,
        name: '消息通知节点',
    },
    {
        type: WorkflowNode.LogRecord,
        name: '日志记录节点',
    },
    {
        type: WorkflowNode.Script,
        name: '脚本节点',
    },
    {
        type: WorkflowNode.Connector,
        name: '连接器节点',
    },
    {
        type: WorkflowNode.JsonParse,
        name: 'JSON 解析节点',
    },
    {
        type: WorkflowNode.DataMapping,
        name: '数据映射节点',
    },
]

export const ALL_NODE_TYPES = [
    {
        type: WorkflowNodeType.All,
        nodes: [...CONTROL_NODES, ...DATA_NODES, ...INTERACTIVE_NODES, ...OTHER_NODES],
    },
    {
        type: WorkflowNodeType.Control,
        nodes: CONTROL_NODES,
    },
    {
        type: WorkflowNodeType.Data,
        nodes: DATA_NODES,
    },
    {
        type: WorkflowNodeType.Interactive,
        nodes: INTERACTIVE_NODES,
    },
    {
        type: WorkflowNodeType.Other,
        nodes: OTHER_NODES,
    }
]
