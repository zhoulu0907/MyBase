// 配置类型常量
export const CONFIG_TYPES = {
  // 默认组件
  TEXT_INPUT: 'TextInput',
  TEXT_AREA_INPUT: 'TextAreaInput',
  SELECT_INPUT: 'SelectInput',
  DYNAMIC_SELECT_INPUT: 'DynamicSelectInput',
  NUMBER_INPUT: 'NumberInput',
  SWITCH_INPUT: 'SwitchInput',

  SEARCH_ITEM_LIST: 'SearchItemList',

  // 组件标题输入
  LABEL_INPUT: 'LabelInput',
  // 组件占位符输入
  PLACEHOLDER_INPUT: 'PlaceholderInput',
  // 组件描述输入
  DESCRIPTION_INPUT: 'DescriptionInput',
  // 组件提示输入
  TOOLTIP_INPUT: 'TooltipInput',
  // 组件状态选择
  STATUS_RADIO: 'StatusRadio',
  // 组件宽度选择
  WIDTH_RADIO: 'WidthRadio',
  // 组件默认值选择
  DEFAULT_VALUE_SELECT: 'DefaultValueSelect',
  // 组件默认值输入
  DEFAULT_VALUE_INPUT: 'DefaultValueInput',
  // 组件必填选择
  REQUIRED_CHECKBOX: 'RequiredCheckBox',
  // 布局列数选择
  COLUMN_COUNT_RADIO: 'ColumnCountRadio',
  // 表格分页位置选择
  TABLE_PAGE_POSITION_RADIO: 'TablePagePositionRadio',
  // 表格列配置
  TABLE_COLUMN_LIST: 'TableColumnList',
  // 表格分页数量
  TABLE_PAGE_SIZE: 'TablePageSize',
  // 图片、附件上传大小限制
  UPLOAD_SIZE: 'UploadSize',
  // 图片、附件上传数量限制
  UPLOAD_LIMIT: 'UploadLimit',
  // 图片压缩率
  UPLOAD_COMPRESS: 'UploadCompress',
  // 日期类型
  DATE_TYPE: 'DateType',
  // 表单布局方式
  FORM_LAYOUT: 'FormLayout',
  // 文本对齐方式
  TEXT_ALIGN: 'TextAlign',
  // 颜色
  COLOR: 'Color',

} as const;


// 状态选项常量
export const STATUS_OPTIONS = {
    DEFAULT: '普通',
    READONLY: '只读',
    HIDDEN: '隐藏',
} as const;

export const STATUS_VALUES = {
    [STATUS_OPTIONS.DEFAULT]: 'default',
    [STATUS_OPTIONS.READONLY]: 'readonly',
    [STATUS_OPTIONS.HIDDEN]: 'hidden',
} as const;


// 宽度选项常量
export const WIDTH_OPTIONS = {
    QUARTER: '1/4',
    THIRD: '1/3',
    HALF: '1/2',
    TWO_THIRDS: '2/3',
    THREE_QUARTERS: '3/4',
    FULL: 'formEditor.full',
  } as const;

// 宽度值映射
export const WIDTH_VALUES = {
  [WIDTH_OPTIONS.QUARTER]: '25%',
  [WIDTH_OPTIONS.THIRD]: '33.33%',
  [WIDTH_OPTIONS.HALF]: '50%',
  [WIDTH_OPTIONS.TWO_THIRDS]: '66.66%',
  [WIDTH_OPTIONS.THREE_QUARTERS]: '75%',
  [WIDTH_OPTIONS.FULL]: '100%',
} as const;

// 列数选项常量
export const COLUMN_COUNT_OPTIONS = {
  ONE: 1,
  TWO: 2,
  THREE: 3,
  FOUR: 4,
} as const;

export const COLUMN_COUNT_VALUES = {
  [COLUMN_COUNT_OPTIONS.ONE]: 1,
  [COLUMN_COUNT_OPTIONS.TWO]: 2,
  [COLUMN_COUNT_OPTIONS.THREE]: 3,
  [COLUMN_COUNT_OPTIONS.FOUR]: 4,
} as const;

export const PAGINATION_POSITION_OPTIONS = {
  BR: '右下',
  BL: '左下',
  TR: '右上',
  TL: '左上',
  TOP_CENTER: '上中',
  BOTTOM_CENTER: '下中',
} as const;

export const PAGINATION_POSITION_VALUES = {
  [PAGINATION_POSITION_OPTIONS.BR]: 'br',
  [PAGINATION_POSITION_OPTIONS.BL]: 'bl',
  [PAGINATION_POSITION_OPTIONS.TR]: 'tr',
  [PAGINATION_POSITION_OPTIONS.TL]: 'tl',
  [PAGINATION_POSITION_OPTIONS.TOP_CENTER]: 'topCenter',
  [PAGINATION_POSITION_OPTIONS.BOTTOM_CENTER]: 'bottomCenter',
} as const;

// 内容对齐方式
export const ALIGN_OPTIONS = {
    LEFT: '左',
    CENTER: '中',
    RIGHT: '右',
} as const;

export const ALIGN_VALUES = {
    [ALIGN_OPTIONS.LEFT]: 'left',
    [ALIGN_OPTIONS.CENTER]: 'center',
    [ALIGN_OPTIONS.RIGHT]: 'right',
} as const;

// 上传组件展示格式
export const UPLOAD_OPTIONS = {
    TEXT: '文本',
    LIST: '列表',
    CARD: '平铺',
} as const;

export const UPLOAD_VALUES = {
    [UPLOAD_OPTIONS.TEXT]: 'text',
    [UPLOAD_OPTIONS.LIST]: 'picture-list',
    [UPLOAD_OPTIONS.CARD]: 'picture-card',
} as const;

// 日期选择格式
export const DATE_OPTIONS = {
    ONLY_YEAR: '年',
    ONLY_MONTH: '年月',
    ONLY_DATE: '年月日',
    FULL: '年月日时',
} as const;

export const DATE_VALUES = {
    [DATE_OPTIONS.ONLY_YEAR]: 'ONLY_YEAR',
    [DATE_OPTIONS.ONLY_MONTH]: 'ONLY_MONTH',
    [DATE_OPTIONS.ONLY_DATE]: 'ONLY_DATE',
    [DATE_OPTIONS.FULL]: 'FULL',
} as const;


// 表单的布局
export const LAYOUT_OPTIONS = {
    HORIZONTAL: '水平',
    VERTICAL: '垂直',
} as const;

export const LAYOUT_VALUES = {
    [LAYOUT_OPTIONS.HORIZONTAL]: 'horizontal',
    [LAYOUT_OPTIONS.VERTICAL]: 'vertical',
} as const;
