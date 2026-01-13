// ==================== 类型导入 ====================
import type {
  IAlignConfigType,
  ICarouselConfigType,
  IDataFieldConfigType,
  IDateTypeConfigType,
  ILayoutConfigType,
  INumberConfigType,
  IRadioDataConfigType,
  ICheckboxDataConfigType,
  IRelatedFormDataConfigType,
  ISelectConfigType,
  ISelectOptionsConfigType,
  IMutipleSelectOptionsConfigType,
  IStatusConfigType,
  ITableDataConfigType,
  ITablePagePositionConfigType,
  ITablePageSizeConfigType,
  IColumnCountConfigType,
  ITextConfigType,
  IWidthConfigType,
  TTextDefaultType,
  ISelectDataSourceConfigType,
  ISubTableConfigType,
  ITabsConfigType,
  ITabsTypeConfigType,
  ITabsPositionConfigType,
  IImageConfigType,
  IFileConfigType,
  ICollapsedConfigType,
  ICollapsedStyleConfig,
  ITableButtonConfigType,
  ITableOperationConfigType,
  IBooleanConfigType,
  IDataSelectModeConfigType,
  IPhoneType,
  IDividerTooltipConfigType,
  IDividerStyleTypeConfigType,
  IPlaceholderConfigType,
  ITooltipConfigType,
  IVerifyConfigType,
  ISecurityConfigType,
  IDefaultValueConfigType,
  IDateFormatConfigType,
  ITimeFormatConfigType,
  INumberFormatConfigType,
  IDateRangeConfigType,
  ITimeRangeConfigType,
  IImageHandleConfigType,
  IAutoCodeConfigType,
  ISwitchFillTextConfigType,
  IColorConfigType,
  ICommonConfigType,
} from './types';

// ==================== 基础与通用 ====================
export interface ICommonBaseType {
  id: string;
  cpName: TTextDefaultType;
}

export const baseConfig: ITextConfigType[] = [
  // {
  //   key: 'cpName',
  //   name: '组件名称',
  //   type: 'TextInput'
  // }
];

// ==================== 布局与尺寸 ====================
export type TWidthSelectKeyType = '25%' | '33.33%' | '50%' | '66.66%' | '75%' | '100%';
export const widthConfig: IWidthConfigType<TWidthSelectKeyType> = {
  key: 'width',
  name: '宽度',
  type: 'WidthRadio',
  range: [
    {
      key: '1/4',
      text: '1/4',
      value: '25%'
    },
    {
      key: '1/3',
      text: '1/3',
      value: '33.33%'
    },
    {
      key: '1/2',
      text: '1/2',
      value: '50%'
    },
    {
      key: '2/3',
      text: '2/3',
      value: '66.66%'
    },
    {
      key: '3/4',
      text: '3/4',
      value: '75%'
    },
    {
      key: 'editor.full',
      text: 'editor.full',
      value: '100%'
    }
  ]
};

// ==================== 状态与对齐 ====================
export type TStatusSelectKeyType = 'default' | 'readonly' | 'hidden';
export const statusConfig: IStatusConfigType<TStatusSelectKeyType> = {
  key: 'status',
  name: '显示状态',
  type: 'StatusRadio',
  range: [
    {
      key: '普通',
      text: '普通',
      value: 'default'
    },
    {
      key: '只读',
      text: '只读',
      value: 'readonly'
    },
    {
      key: '隐藏',
      text: '隐藏',
      value: 'hidden'
    }
  ]
};

export type TLayoutStatusSelectKeyType = 'default' | 'hidden';
export const statusHiddenConfig: IStatusConfigType<TLayoutStatusSelectKeyType> = {
  key: 'status',
  name: '显示状态',
  type: 'StatusRadio',
  range: [
    {
      key: '普通',
      text: '普通',
      value: 'default'
    },
    {
      key: '隐藏',
      text: '隐藏',
      value: 'hidden'
    }
  ]
};

export type TAlignSelectKeyType = 'left' | 'center' | 'right';
export const alignConfig: IAlignConfigType<TAlignSelectKeyType> = {
  key: 'align',
  name: '对齐方式',
  type: 'TextAlign',
  range: [
    {
      key: '左',
      text: '左',
      value: 'left'
    },
    {
      key: '中',
      text: '中',
      value: 'center'
    },
    {
      key: '右',
      text: '右',
      value: 'right'
    }
  ]
};

// ==================== 日期与时间 ====================
export type TDateTypeSelectKeyType = 'year' | 'month' | 'date' | 'full';
export type TTimeTypeSelectKeyType = 'hour' | 'minute' | 'second';
export const dateTypeConfig: IDateTypeConfigType<TDateTypeSelectKeyType> = {
  key: 'dateType',
  name: '日期格式',
  type: 'DateType',
  range: [
    {
      key: '年',
      text: '年',
      value: 'year'
    },
    {
      key: '年月',
      text: '年月',
      value: 'month'
    },
    {
      key: '年月日',
      text: '年月日',
      value: 'date'
    },
    {
      key: '年月日时',
      text: '年月日时',
      value: 'full'
    }
  ]
};

// ==================== 表单布局 ====================
export type TLayoutSelectKeyType = 'horizontal' | 'vertical';
export const layoutConfig: ILayoutConfigType<TLayoutSelectKeyType> = {
  key: 'layout',
  name: '布局方式',
  type: 'FormLayout',
  range: [
    {
      key: '水平',
      text: '水平',
      value: 'horizontal'
    },
    {
      key: '垂直',
      text: '垂直',
      value: 'vertical'
    }
  ]
};

export type TDirectionSelectKeyType = 'horizontal' | 'vertical';
export const directionConfig: ILayoutConfigType<TDirectionSelectKeyType> = {
  key: 'direction',
  name: '选项分布方式',
  type: 'FormLayout',
  range: [
    {
      key: '水平',
      text: '横向排列',
      value: 'horizontal'
    },
    {
      key: '垂直',
      text: '纵向排列',
      value: 'vertical'
    }
  ]
};

// ==================== 表单字段与校验 ====================
// 标题宽度
export const labelColSpanConfig: INumberConfigType = {
  key: 'labelColSpan',
  name: '标题宽度',
  type: 'NumberInput'
};

export const labelConfig = {
  key: 'label',
  name: '标题',
  type: 'LabelInput'
};

export const placeholderConfig: IPlaceholderConfigType = {
  key: 'placeholder',
  name: '占位提示',
  type: 'PlaceholderInput'
};

export const tooltipConfig: ITooltipConfigType = {
  key: 'tooltip',
  name: '字段描述',
  type: 'TooltipInput'
};

export const verifyConfig: IVerifyConfigType = {
  key: 'verify',
  name: '校验',
  type: 'Verify'
};

export const securityConfig: ISecurityConfigType = {
  key: 'security',
  name: '安全',
  type: 'Security'
};

// ==================== 上传与图片 ====================
// 文件列表
export type TUploadSelectKeyType = 'text' | 'picture-list' | 'picture-card';
export const listTypeConfig: IStatusConfigType<TUploadSelectKeyType> = {
  key: 'listType',
  name: '展示样式',
  type: 'StatusRadio',
  range: [
    {
      key: '卡片',
      text: '卡片',
      value: 'picture-card'
    },
    {
      key: '列表',
      text: '列表',
      value: 'picture-list'
    }
  ]
};
export const uploadTypeConfig: IStatusConfigType<TUploadSelectKeyType> = {
  key: 'uploadType',
  name: '上传方式',
  type: 'StatusRadio',
  range: [
    {
      key: '文本',
      text: '点击',
      value: 'text'
    },
    {
      key: '列表',
      text: '拖拽',
      value: 'picture-list'
    },
    {
      key: '卡片',
      text: '卡片',
      value: 'picture-card'
    }
  ]
};

export const uploadMethodConfig: IStatusConfigType<TUploadSelectKeyType> = {
  key: 'uploadType',
  name: '上传方式',
  type: 'StatusRadio',
  range: [
    { key: '文本', text: '点击', value: 'text' },
    { key: '列表', text: '拖拽', value: 'picture-list' }
  ]
};

export type TUploadButtonType = 'primary' | 'secondary' | 'dashed' | 'outline' | 'text';

// ==================== 表格与分页 ====================
export type TPagePositionSelectKeyType = 'br' | 'bl' | 'tr' | 'tl' | 'topCenter' | 'bottomCenter';
export const pagePositionConfig: ISelectConfigType<TPagePositionSelectKeyType> = {
  key: 'pagePosition',
  name: '分页位置',
  type: 'SelectInput',
  range: [
    {
      key: '右下',
      text: '右下',
      value: 'br'
    },
    {
      key: '左下',
      text: '左下',
      value: 'bl'
    },
    {
      key: '右上',
      text: '右上',
      value: 'tr'
    },
    {
      key: '左上',
      text: '左上',
      value: 'tl'
    },
    {
      key: '上中',
      text: '上中',
      value: 'topCenter'
    },
    {
      key: '下中',
      text: '下中',
      value: 'bottomCenter'
    }
  ]
};

// ==================== 展示填充样式 ====================
export type TFillSelectKeyType = 'contain' | 'cover' | 'fill';
export const fillConfig: IStatusConfigType<TFillSelectKeyType> = {
  key: 'fillStyle',
  name: '填充方式',
  type: 'StatusRadio',
  range: [
    {
      key: '原图局中',
      text: '原图局中',
      value: 'contain'
    },
    {
      key: '局中填满',
      text: '局中填满',
      value: 'cover'
    },
    {
      key: '拉伸填满',
      text: '拉伸填满',
      value: 'fill'
    }
  ]
};

// ==================== 数据绑定与选择 ====================
export const dataFieldConfig: IDataFieldConfigType[] = [
  {
    key: 'dataField',
    name: '数据绑定',
    type: 'FieldData'
  }
];

export const relatedFormdataFieldConfig: IRelatedFormDataConfigType[] = [
  {
    key: 'relatedFormDataField',
    name: '关联表单',
    type: 'RelatedFormData'
  }
];

// ==================== 表格元数据 ====================
export const tableMetaDataConfig: ITableDataConfigType = {
  key: 'metaData',
  name: '数据',
  type: 'TableData'
};

export const keyDataConfig: ITableDataConfigType = {
  key: 'keyData',
  name: '主键',
  type: 'TableData'
};

// ==================== 默认基础值 ====================
export const baseDefault = {
  cpName: '',
  id: ''
};

// ==================== 选项与多选 ====================
export const radioDataConfig: IRadioDataConfigType = {
  key: 'defaultOptionsConfig',
  name: '自定义选项',
  type: 'RadioData'
};

export const checkboxDataConfig: ICheckboxDataConfigType = {
  key: 'defaultOptionsConfig',
  name: '自定义选项',
  type: 'CheckboxData'
};

export const selectOptionsConfig: ISelectOptionsConfigType = {
  key: 'defaultOptionsConfig',
  name: '自定义选项',
  type: 'OptionsInput'
};

export const mutipleSelectOptionsConfig: IMutipleSelectOptionsConfigType = {
  key: 'defaultOptionsConfig',
  name: '自定义选项',
  type: 'MutipleOptionsInput'
};

// ==================== 轮播与媒体 ====================
export const carouselConfig: ICarouselConfigType = {
  key: 'carousel',
  name: '图片',
  type: 'Carousel'
};

export const selectDataResourceConfig: ISelectDataSourceConfigType = {
  key: 'selectedDataSource',
  name: '选择数据源',
  type: 'SelectDataSource'
}

export const dataSelectModeConfig: IDataSelectModeConfigType = {
  key: 'selectMethod',
  name: '数据选择方式',
  type: 'DataSelectMode',
  range: [
    { key: 'dropdown', text: '下拉框', value: 'dropdown' },
    { key: 'modal', text: '弹窗', value: 'modal' }
  ]
}

// ==================== 子表单与 Tabs ====================
export const subTableConfig: ISubTableConfigType = {
  key: 'subTableConfig',
  name: '展示样式',
  type: 'SubTable'
};

export const tabsConfig: ITabsConfigType = {
  key: 'tabs',
  name: '多标签显示',
  type: 'Tabs'
};

// 页签样式
export type TTabsTypeSelectKeyType = 'line' | 'card' | 'card-gutter' | 'text' | 'rounded' | 'capsule';
export const tabsTypeConfig: ITabsTypeConfigType<TTabsTypeSelectKeyType> = {
  key: 'type',
  name: '页签样式',
  type: 'TabsType',
  range: [
    {
      key: 'line',
      label: 'line',
      value: 'line'
    },
    {
      key: 'card',
      label: 'card',
      value: 'card'
    },
    {
      key: 'card-gutter',
      label: 'card-gutter',
      value: 'card-gutter'
    },
    {
      key: 'text',
      label: 'text',
      value: 'text'
    },
    {
      key: 'rounded',
      label: 'rounded',
      value: 'rounded'
    },
    {
      key: 'capsule',
      label: 'capsule',
      value: 'capsule'
    }
  ]
};

// 页签位置
export type TTabsPositionSelectKeyType = 'left' | 'top' | 'bottom' | 'right';
export const tabsPositionConfig: ITabsPositionConfigType<TTabsPositionSelectKeyType> = {
  key: 'tabPosition',
  name: '页签位置',
  type: 'TabsPosition',
  range: [
    {
      key: '左',
      label: '左',
      value: 'left'
    },
    {
      key: '上',
      label: '上',
      value: 'top'
    },
    {
      key: '下',
      label: '下',
      value: 'bottom'
    },
    {
      key: '右',
      label: '右',
      value: 'right'
    }
  ]
};
// ==================== 图片与文件 ====================
export const imageConfig: IImageConfigType = {
  key: 'image',
  name: '图片',
  type: 'Image'
};

export const fileConfig: IFileConfigType = {
  key: 'file',
  name: '图片',
  type: 'File'
};

// ==================== 折叠容器 ====================
export type TCollapsedSelectKeyType = 'exposed' | 'collapsed' | 'noCollapsed';
export const collapsedConfig: ICollapsedConfigType<TCollapsedSelectKeyType> = {
  key: 'collapsed',
  name: '折叠状态',
  type: 'Collapsed',
  range: [
    {
      key: '展开',
      text: '展开',
      value: 'exposed'
    },
    {
      key: '收起',
      text: '收起',
      value: 'collapsed'
    },
    {
      key: '不折叠',
      text: '不折叠',
      value: 'noCollapsed'
    }
  ]
};

export const collapsedStyleConfig: ICollapsedStyleConfig = {
  key: 'collapseStyle',
  name: '样式',
  type: 'CollapsedStyle'
};

// ==================== 表格按钮与操作栏 ====================
export type TButtonSelectKeyType = 'hidden' | 'disabled';
export const tableButtonPermissionConfig: ITableButtonConfigType<TButtonSelectKeyType> = {
  key: 'advancedButtonPermission',
  name: '按钮权限配置',
  type: 'advancedButtonPermission',
  advanced: true,
  range: [
    {
      key: '隐藏',
      text: '隐藏',
      value: 'hidden'
    },
    {
      key: '置灰',
      text: '置灰',
      value: 'disabled'
    }
  ]
};

export const tableOperationConfig: ITableOperationConfigType = {
  key: 'tableOperation',
  name: '操作栏配置',
  type: 'TableOperation',
  advanced: true,
};

// ==================== 规则与处理 ====================
export const autoCodeConfig: IAutoCodeConfigType = {
  key: 'rules',
  name: '编号规则配置',
  type: 'autoCodeRules'
}

export const imageHandleConfig: IImageHandleConfigType = {
  key: 'imageHandle',
  name: '图片处理',
  type: 'ImageHandle'
}

export const dateRangeConfig: IDateRangeConfigType = {
  key: 'dateRange',
  name: '可选范围',
  type: 'DateRange'
}

export const timeRangeConfig: ITimeRangeConfigType = {
  key: 'timeRange',
  name: '可选范围',
  type: 'TimeRange'
}

export const switchFillTextConfig: ISwitchFillTextConfigType = {
  key: 'fillText',
  name: '填充文本',
  type: 'SwitchFillText'
}

export const minRowsConfig: INumberConfigType = {
  key: 'minRows',
  name: '文本展示行数',
  type: 'NumberInput',
  min: 1,
  max: 10,
  step: 1,
}

export const autoplayConfig: IBooleanConfigType = {
  key: 'autoplay',
  name: '自动轮播',
  type: 'SwitchInput'
}

export const carouselIntervalConfig: INumberConfigType = {
  key: 'interval',
  name: '轮播间隔',
  type: 'NumberInput',
  step: 1
}

// ==================== 默认值与格式 ====================
// 默认值
export const defaultValueConfig: IDefaultValueConfigType = {
  key: 'defaultValueConfig',
  name: '默认值',
  type: 'DefaultValue',
  valueType: 'string'
}

export const defaultDateValueConfig: IDefaultValueConfigType = {
  key: 'defaultValueConfig',
  name: '默认值',
  type: 'DefaultValue',
  valueType: 'date'
}

export const startDefaultDateValueConfig: IDefaultValueConfigType = {
  key: 'startDefaultValueConfig',
  name: '开始日期默认值',
  type: 'DefaultValue',
  valueType: 'date'
}

export const endDefaultDateValueConfig: IDefaultValueConfigType = {
  key: 'endDefaultValueConfig',
  name: '结束日期默认值',
  type: 'DefaultValue',
  valueType: 'date'
}

export const defaultDateTimeValueConfig: IDefaultValueConfigType = {
  key: 'defaultValueConfig',
  name: '默认值',
  type: 'DefaultValue',
  valueType: 'dateTime'
}

export const defaultTimeValueConfig: IDefaultValueConfigType = {
  key: 'defaultValueConfig',
  name: '默认值',
  type: 'DefaultValue',
  valueType: 'time'
}

export const defaultNumberValueConfig: IDefaultValueConfigType = {
  key: 'defaultValueConfig',
  name: '默认值',
  type: 'DefaultValue',
  valueType: 'number'
}

export const defaultBooleanValueConfig: IDefaultValueConfigType = {
  key: 'defaultValueConfig',
  name: '默认值',
  type: 'DefaultValue',
  valueType: 'boolean'
}

export const defaultPhoneValueConfig: IDefaultValueConfigType = {
  key: 'defaultValueConfig',
  name: '默认值',
  type: 'DefaultValue',
  valueType: 'phone'
}

export const dateFormatConfig: IDateFormatConfigType<TDateTypeSelectKeyType> = {
  key: 'dateFormat',
  name: '日期格式',
  type: 'DateFormat',
  range: [
    { label: '年', value: 'year' },
    { label: '年-月', value: 'month' },
    { label: '年-月-日', value: 'date' },
  ]
}

export const timeFormatConfig: ITimeFormatConfigType<TTimeTypeSelectKeyType> = {
  key: 'timeFormat',
  name: '时间格式',
  type: 'TimeFormat',
  range: [
    { label: '时', value: 'hour' },
    { label: '时:分', value: 'minute' },
    { label: '时:分:秒', value: 'second' }
  ]
}
 export const dateTimeimeFormatConfig: any = {
  key: 'dateFormat',
  name: '日期时间格式',
  type: 'DateTimeFormat',
  range: [
    { label: '年-月-日 时:分:秒', value: 'second' },
    { label: '年-月-日 时:分', value: 'minute' },
  ]
}

export const numberFormatConfig: INumberFormatConfigType = {
  key: 'numberFormat',
  name: '格式',
  type: 'NumberFormat'
}

export const stepConfig: INumberConfigType = {
  key: 'step',
  name: '数字步长',
  type: 'NumberInput'
}

export const textDefaultValueConfig: ITextConfigType = {
  key: 'defaultValue',
  name: '默认值',
  type: 'TextInput'
}

export const buttonNameConfig: ITextConfigType = {
  key: 'buttonName',
  name: '按钮名称',
  type: 'TextInput'
}

export const uploadButtonTypeConfig: IStatusConfigType<TUploadButtonType> = {
  key: 'buttonType',
  name: '按钮类型',
  type: 'StatusRadio',
  range: [
    { key: 'primary', text: '主要按钮', value: 'primary' },
    { key: 'secondary', text: '次要按钮', value: 'secondary' },
    { key: 'outline', text: '线框按钮', value: 'outline' }
  ]
}

export const showDownloadConfig: IBooleanConfigType = {
  key: 'showDownload',
  name: '列表页支持下载',
  type: 'SwitchInput'
}

// ==================== 文本与颜色 ====================
export const colorConfig: IColorConfigType = {
  key: 'color',
  name: '文本颜色',
  type: 'Color'
}

export const contentConfig: ITextConfigType = {
  key: 'content',
  name: '文本内容',
  type: 'TextInput'
}

export const titleTextConfig: ITextConfigType = {
  key: 'title',
  name: '标题',
  type: 'TextInput'
}

export const webViewUrlConfig: ITextConfigType = {
  key: 'webViewUrl',
  name: '网页链接',
  type: 'TextInput'
}

export const bgColorConfig: IColorConfigType = {
  key: 'bgColor',
  name: '背景颜色',
  type: 'Color'
}

export const maxLengthConfig: INumberConfigType = {
  key: 'maxLength',
  name: '文本最大长度',
  type: 'NumberInput'
}

// ==================== 部门/用户配置与范围 ====================
export const defaultValueModeConfig: ICommonConfigType = {
  key: 'defaultValueMode',
  name:'默认值',
  type: 'deptDefaultValue'
}

export const selectScopeConfig: ICommonConfigType = {
  key: 'selectScope',
  name:'可选范围',
  type: 'deptSelectScope'
}

export const dynamicUserSelectConfig: ICommonConfigType = {
  key: 'defaultValueMode',
  name:'默认值',
  type: 'userDefaultValue'
}

export const phoneTypeConfig: IPhoneType = {
  key: 'phoneType',
  name: '类型',
  type: 'phoneType',
  range: [
    { label: '手机', value: 'mobile' },
    { label: '座机', value: 'landline' },
  ]
}

// ==================== 分割线与列数 ====================
export const dividerTooltipConfig: IDividerTooltipConfigType = {
  key: 'tooltip',
  name: '字段描述',
  type: 'DividerTooltipInput'
}

export const dividerStyleTypeConfig: IDividerStyleTypeConfigType = {
  key: 'styleType',
  name: '样式',
  type: 'DividerStyleType'
}

export const columnCountConfig: IColumnCountConfigType<number> = {
  key: 'colCount',
  name: '列数',
  type: 'ColumnCountRadio',
  range: [
    { key: '1', text: '1', value: 1 },
    { key: '2', text: '2', value: 2 },
    { key: '3', text: '3', value: 3 },
    { key: '4', text: '4', value: 4 },
  ]
}

// ==================== 表格分页与开关项 ====================
export const tablePagePositionConfig: ITablePagePositionConfigType<TPagePositionSelectKeyType> = {
  key: 'pagePosition',
  name: '分页位置',
  type: 'TablePagePositionRadio',
  range: [
    { key: 'tl', text: '左上', value: 'tl' },
    { key: 'topCenter', text: '上中', value: 'topCenter' },
    { key: 'tr', text: '右上', value: 'tr' },
    { key: 'bl', text: '左下', value: 'bl' },
    { key: 'bottomCenter', text: '下中', value: 'bottomCenter' },
    { key: 'br', text: '右下', value: 'br' },
  ]
}

export const tablePageSizeConfig: ITablePageSizeConfigType = {
  key: 'pageSize',
  name: '分页数量',
  type: 'TablePageSize'
}

// Table 开关项公共配置
export const tableBorderConfig: IBooleanConfigType = {
  key: 'border',
  name: '显示边框',
  type: 'SwitchInput'
}

export const tableBorderCellConfig: IBooleanConfigType = {
  key: 'borderCell',
  name: '显示单元格',
  type: 'SwitchInput'
}

export const tableShowHeaderConfig: IBooleanConfigType = {
  key: 'showHeader',
  name: '显示表头',
  type: 'SwitchInput'
}

export const tableHoverConfig: IBooleanConfigType = {
  key: 'hover',
  name: '鼠标悬浮效果',
  type: 'SwitchInput'
}

export const tableStripeConfig: IBooleanConfigType = {
  key: 'stripe',
  name: '开启斑马纹',
  type: 'SwitchInput'
}

export const tableShowTotalConfig: IBooleanConfigType = {
  key: 'showTotal',
  name: '显示表格总数',
  type: 'SwitchInput'
}

export const tableShowOperateConfig: IBooleanConfigType = {
  key: 'showOpearate',
  name: '开启操作项',
  type: 'SwitchInput'
}

export const tableFixedOperateConfig: IBooleanConfigType = {
  key: 'fixedOpearate',
  name: '固定操作项',
  type: 'SwitchInput'
}

export const rowRedirectConfig: ITableDataConfigType = {
  key: 'advancedRowRedirect',
  name: '行点击跳转',
  type: 'TableData',
  advanced: true
}

export const COMMON_CONFIG: Record<string, unknown> = {
  baseConfig,
  baseDefault,
  widthConfig,
  statusConfig,
  statusHiddenConfig,
  alignConfig,
  dateTypeConfig,
  timeFormatConfig,
  dateFormatConfig,
  layoutConfig,
  directionConfig,
  labelColSpanConfig,
  labelConfig,
  placeholderConfig,
  tooltipConfig,
  verifyConfig,
  securityConfig,
  listTypeConfig,
  uploadTypeConfig,
  uploadMethodConfig,
  uploadButtonTypeConfig,
  pagePositionConfig,
  pageSizeConfig: tablePageSizeConfig,
  tableMetaDataConfig,
  keyDataConfig,
  tablePagePositionConfig,
  tablePageSizeConfig,
  tableBorderConfig,
  tableBorderCellConfig,
  tableShowHeaderConfig,
  tableHoverConfig,
  tableStripeConfig,
  tableShowTotalConfig,
  tableShowOperateConfig,
  tableFixedOperateConfig,
  tableOperationConfig,
  tableButtonPermissionConfig,
  rowRedirectConfig,
  fillConfig,
  imageConfig,
  fileConfig,
  selectDataResourceConfig,
  dataSelectModeConfig,
  subTableConfig,
  tabsConfig,
  tabsTypeConfig,
  tabsPositionConfig,
  collapsedConfig,
  collapsedStyleConfig,
  colorConfig,
  contentConfig,
  titleTextConfig,
  bgColorConfig,
  maxLengthConfig,
  textDefaultValueConfig,
  buttonNameConfig,
  defaultValueConfig,
  defaultDateValueConfig,
  startDefaultDateValueConfig,
  endDefaultDateValueConfig,
  defaultDateTimeValueConfig,
  defaultTimeValueConfig,
  numberFormatConfig,
  stepConfig,
  defaultNumberValueConfig,
  defaultBooleanValueConfig,
  defaultPhoneValueConfig,
  phoneTypeConfig,
  imageHandleConfig,
  dateRangeConfig,
  timeRangeConfig,
  autoplayConfig,
  carouselIntervalConfig,
  carouselConfig,
  radioDataConfig,
  checkboxDataConfig,
  selectOptionsConfig,
  mutipleSelectOptionsConfig,
  dataFieldConfig,
  relatedFormdataFieldConfig,
  defaultValueModeConfig,
  selectScopeConfig,
  dynamicUserSelectConfig,
  showDownloadConfig,
  dividerTooltipConfig,
  dividerStyleTypeConfig,
  columnCountConfig,
  autoCodeConfig
}

export const COMMON_CONFIG_GROUPS = {
  base: {
    baseConfig,
    baseDefault
  },
  layout: {
    widthConfig,
    layoutConfig,
    labelColSpanConfig,
    directionConfig
  },
  status: {
    statusConfig,
    statusHiddenConfig
  },
  align: {
    alignConfig
  },
  text: {
    labelConfig,
    placeholderConfig,
    tooltipConfig,
    verifyConfig,
    colorConfig,
    contentConfig,
    titleTextConfig,
    bgColorConfig,
    maxLengthConfig,
    textDefaultValueConfig,
    buttonNameConfig
  },
  dateTime: {
    dateTypeConfig,
    dateFormatConfig,
    timeFormatConfig,
    dateRangeConfig,
    timeRangeConfig,
    defaultDateValueConfig,
    startDefaultDateValueConfig,
    endDefaultDateValueConfig,
    defaultDateTimeValueConfig,
    defaultTimeValueConfig
  },
  number: {
    numberFormatConfig,
    stepConfig,
    defaultNumberValueConfig
  },
  boolean: {
    defaultBooleanValueConfig
  },
  phone: {
    phoneTypeConfig,
    defaultPhoneValueConfig
  },
  upload: {
    listTypeConfig,
    uploadTypeConfig,
    uploadMethodConfig,
    uploadButtonTypeConfig,
    showDownloadConfig
  },
  imageFile: {
    imageConfig,
    fileConfig,
    imageHandleConfig,
    fillConfig
  },
  carousel: {
    carouselConfig,
    autoplayConfig,
    carouselIntervalConfig
  },
  table: {
    tableMetaDataConfig,
    keyDataConfig,
    tablePagePositionConfig,
    tablePageSizeConfig,
    tableBorderConfig,
    tableBorderCellConfig,
    tableShowHeaderConfig,
    tableHoverConfig,
    tableStripeConfig,
    tableShowTotalConfig,
    tableShowOperateConfig,
    tableFixedOperateConfig,
    tableOperationConfig,
    tableButtonPermissionConfig,
    rowRedirectConfig
  },
  tabs: {
    tabsConfig,
    tabsTypeConfig,
    tabsPositionConfig
  },
  collapse: {
    collapsedConfig,
    collapsedStyleConfig
  },
  column: {
    columnCountConfig
  },
  divider: {
    dividerTooltipConfig,
    dividerStyleTypeConfig
  },
  dataSelect: {
    selectDataResourceConfig,
    dataSelectModeConfig
  },
  deptUser: {
    defaultValueModeConfig,
    selectScopeConfig,
    dynamicUserSelectConfig
  },
  fields: {
    dataFieldConfig,
    relatedFormdataFieldConfig
  },
  autoCode: {
    autoCodeConfig
  },
  security: {
    securityConfig
  }
} as const

export function getCommonConfig<T = unknown>(key: keyof typeof COMMON_CONFIG): T {
  return COMMON_CONFIG[key] as T
}
