export const DS_RESOURCE_TYPE = {
  INTERNAL: 'internal',
  EXTERNAL: 'external',
  EXTERNAL_WITH_INTERNAL: 'external_with_internal',
};

export const DS_RESOURCE_TYPE_LABEL = {
  [DS_RESOURCE_TYPE.INTERNAL]: '内部数据源',
  [DS_RESOURCE_TYPE.EXTERNAL]: '外部数据源',
  [DS_RESOURCE_TYPE.EXTERNAL_WITH_INTERNAL]: '外部数据源中引用自有数据源已有资产',
};