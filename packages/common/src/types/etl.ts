export enum ETLNodeType {
  INPUT_NODE = 'jdbc_input',
  OUTPUT_NODE = 'jdbc_output',
  JOIN_NODE = 'pair_join',
  UNION_NODE = 'union'
}

export enum ETLDrawerTab {
  DATA_CONFIG = 'dataConfig',
  DATA_PREVIEW = 'dataPreview',
  NODE_REMARK = 'nodeRemark'
}

export enum ETLJoinType {
  FULL_JOIN = 'full',
  LEFT_JOIN = 'left',
  RIGHT_JOIN = 'right',
  INNER_JOIN = 'inner'
}
