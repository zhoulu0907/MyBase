// 配置类型常量
export const CONFIG_TYPES = {
  // 默认组件
  TEXT_INPUT: 'TextInput',
  TEXT_AREA_INPUT: 'TextAreaInput',
  SELECT_INPUT: 'SelectInput',
  NUMBER_INPUT: 'NumberInput',
  SWITCH_INPUT: 'SwitchInput',

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