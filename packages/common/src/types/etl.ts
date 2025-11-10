export enum ETLNodeType {
  INPUT_NODE = 'jdbc_input',
  OUTPUT_NODE = 'jdbc_output',
  JOIN_NODE = 'join',
  UNION_NODE = 'union'
}

export enum ETLDrawerTab {
  DATA_CONFIG = 'dataConfig',
  DATA_PREVIEW = 'dataPreview',
  NODE_REMARK = 'nodeRemark'
}
