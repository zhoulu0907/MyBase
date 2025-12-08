// 配置类型常量
export const CONFIG_TYPES = {
  // 默认组件
  TEXT_INPUT: 'TextInput',
  TEXT_AREA_INPUT: 'TextAreaInput',
  SELECT_INPUT: 'SelectInput',
  DYNAMIC_SELECT_INPUT: 'DynamicSelectInput',
  NUMBER_INPUT: 'NumberInput',
  SWITCH_INPUT: 'SwitchInput',
  DATE_INPUT: 'DateInput',
  CHECKED: 'Checked',

  // 组件标题输入
  LABEL_INPUT: 'LabelInput',
  // 组件占位符输入
  PLACEHOLDER_INPUT: 'PlaceholderInput',
  // 组件选项输入
  SELECT_OPTIONS_INPUT: 'OptionsInput',
  MUTIPLE_SELECT_OPTIONS_INPUT: 'MutipleOptionsInput',
  // 组件提示输入
  TOOLTIP_INPUT: 'TooltipInput',
  // 组件状态选择
  STATUS_RADIO: 'StatusRadio',
  // 组件宽度选择
  WIDTH_RADIO: 'WidthRadio',
  // 组件默认值
  DEFAULT_VALUE: 'DefaultValue',
  // 组件必填选择
  REQUIRED_CHECKBOX: 'RequiredCheckBox',
  // 布局列数选择
  COLUMN_COUNT_RADIO: 'ColumnCountRadio',
  // 表格分页位置选择
  TABLE_PAGE_POSITION_RADIO: 'TablePagePositionRadio',
  // 字段数据配置
  FIELD_DATA: 'FieldData',
  // 关联表单数据配置
  RELATED_FORM_DATA: 'RelatedFormData',
  // 表格数据配置
  TABLE_DATA: 'TableData',
  // 表格分页数量
  TABLE_PAGE_SIZE: 'TablePageSize',
  // 图片、附件上传大小限制
  UPLOAD_SIZE: 'UploadSize',
  // 图片、附件上传数量限制
  UPLOAD_LIMIT: 'UploadLimit',
  // 图片、附件支持的文件类型
  // SUPPORT_FILE_TYPE: 'SupportFileType',
  // 图片压缩率
  UPLOAD_COMPRESS: 'UploadCompress',
  // 日期类型
  DATE_TYPE: 'DateType',
  // 表单布局方式
  FORM_LAYOUT: 'FormLayout',
  // 文本对齐方式
  TEXT_ALIGN: 'TextAlign',
  // 标题宽度
  LABEL_COL_SPAN: 'LabelColSpan',
  // 颜色
  COLOR: 'Color',
  // 单选框配置
  RADIO_DATA: 'RadioData',
  // 复选框配置
  CHECKBOX_DATA: 'CheckboxData',
  // 轮播图管理
  CAROUSEL: 'Carousel',
  // 图片填充方式
  FILL_STYLE: 'FillStyle',
  // 安全
  SECURITY: 'Security',
  // 校验
  VERIFY: 'Verify',
  // 数字格式
  NUMBER_FORMAT: 'NumberFormat',
  // 选择数据源
  SELECT_DATA_SOURCE: 'SelectDataSource',
  // 子表单组件
  SUB_TABLE: 'SubTable',
  // 页签组件
  TABS: 'Tabs',
  // 页签组件类型
  TABS_TYPE: 'TabsType',
  // 页签组件位置
  TABS_POSITION: 'TabsPosition',
  // 静态文件
  IMAGE: 'Image',
  FILE: 'File',
  // 图片处理
  IMAGE_HANDLE: 'ImageHandle',
  // 折叠面板 折叠状态
  COLLAPSED: 'Collapsed',
  // 折叠面板 样式
  COLLAPSED_STYLE: 'CollapsedStyle',
  // 自动编号规则
  AUTO_CODE_RULES: 'autoCodeRules',
  // 日期格式
  DATE_FORMAT: 'DateFormat',
  // 日期可选范围
  DATE_RANGE: 'DateRange',
  // 时间格式
  TIME_FORMAT: 'TimeFormat',
  TIME_RANGE: 'TimeRange',
  // 填充文本 switch
  SWITCH_FILL_TEXT: 'SwitchFillText',
  // 手机类型
  PHONE_TYPE: 'phoneType',

  TABLE_OPERATION: 'TableOperation',
  TABLE_BUTTON: 'advancedButtonPermission',
  //选择部门默认值
  DEPT_DEFAULT_VALUE: 'deptDefaultValue',
  //选择部门可选范围
  DEPT_SELECT_SCOPE: 'deptSelectScope',
  // 数据选择方式
  DATA_SELECT_MODE: 'DataSelectMode',
  // 分割线组件提示输入
  DIVIDER_TOOLTIP_INPUT: 'DividerTooltipInput',
  // 分割线样式
  DIVIDER_STYLE_TYPE: 'DividerStyleType',
} as const;

// 状态选项常量
export const STATUS_OPTIONS = {
  DEFAULT: '普通',
  READONLY: '只读',
  HIDDEN: '隐藏'
} as const;

export const STATUS_VALUES = {
  [STATUS_OPTIONS.DEFAULT]: 'default',
  [STATUS_OPTIONS.READONLY]: 'readonly',
  [STATUS_OPTIONS.HIDDEN]: 'hidden'
} as const;

// 宽度选项常量
export const WIDTH_OPTIONS = {
  QUARTER: '1/4',
  THIRD: '1/3',
  HALF: '1/2',
  TWO_THIRDS: '2/3',
  THREE_QUARTERS: '3/4',
  FULL: 'editor.full'
} as const;

// 宽度值映射
export const WIDTH_VALUES = {
  [WIDTH_OPTIONS.QUARTER]: '25%',
  [WIDTH_OPTIONS.THIRD]: '33.33%',
  [WIDTH_OPTIONS.HALF]: '50%',
  [WIDTH_OPTIONS.TWO_THIRDS]: '66.66%',
  [WIDTH_OPTIONS.THREE_QUARTERS]: '75%',
  [WIDTH_OPTIONS.FULL]: '100%'
} as const;

// 列数选项常量
export const COLUMN_COUNT_OPTIONS = {
  ONE: 1,
  TWO: 2,
  THREE: 3,
  FOUR: 4
} as const;

export const COLUMN_COUNT_VALUES = {
  [COLUMN_COUNT_OPTIONS.ONE]: 1,
  [COLUMN_COUNT_OPTIONS.TWO]: 2,
  [COLUMN_COUNT_OPTIONS.THREE]: 3,
  [COLUMN_COUNT_OPTIONS.FOUR]: 4
} as const;

export const PAGINATION_POSITION_OPTIONS = {
  BR: '右下',
  BL: '左下',
  TR: '右上',
  TL: '左上',
  TOP_CENTER: '上中',
  BOTTOM_CENTER: '下中'
} as const;

export const PAGINATION_POSITION_VALUES = {
  [PAGINATION_POSITION_OPTIONS.BR]: 'br',
  [PAGINATION_POSITION_OPTIONS.BL]: 'bl',
  [PAGINATION_POSITION_OPTIONS.TR]: 'tr',
  [PAGINATION_POSITION_OPTIONS.TL]: 'tl',
  [PAGINATION_POSITION_OPTIONS.TOP_CENTER]: 'topCenter',
  [PAGINATION_POSITION_OPTIONS.BOTTOM_CENTER]: 'bottomCenter'
} as const;

// 内容对齐方式
export const ALIGN_OPTIONS = {
  LEFT: '左',
  CENTER: '中',
  RIGHT: '右'
} as const;

export const ALIGN_VALUES = {
  [ALIGN_OPTIONS.LEFT]: 'left',
  [ALIGN_OPTIONS.CENTER]: 'center',
  [ALIGN_OPTIONS.RIGHT]: 'right'
} as const;

// 上传组件展示格式
export const UPLOAD_OPTIONS = {
  TEXT: '文本',
  LIST: '列表',
  CARD: '卡片'
} as const;

export const UPLOAD_TYPE_OPTIONS = {
  TEXT: '点击',
  LIST: '拖拽',
  CARD: '卡片'
} as const;

// 按钮分为 主要按钮、次要按钮、虚线按钮、线形按钮和文本按钮五种
export const UPLOAD_BUTTON_TYPES = {
  PRIMARY: 'primary',
  SECONDARY: 'secondary',
  DASHED: 'dashed',
  OUTLINE: 'outline',
  TEXT: 'text'
} as const;


export const UPLOAD_VALUES = {
  [UPLOAD_OPTIONS.TEXT]: 'text',
  [UPLOAD_OPTIONS.LIST]: 'picture-list',
  [UPLOAD_OPTIONS.CARD]: 'picture-card'
} as const;

export const UPLOAD_TRIGGER_TYPE = {
  CLICK: 'click',
  DRAG: 'drag'
} as const;

// 日期选择格式
export const DATE_OPTIONS = {
  YEAR: '年',
  MONTH: '年月',
  DATE: '年月日',
  FULL: '年月日时'
} as const;

// 时间选择格式
export const TIME_OPTIONS = {
  HOUR: '时',
  MINUTE: '分',
  SECOND: '秒'
} as const;

export const DATE_VALUES = {
  [DATE_OPTIONS.YEAR]: 'year',
  [DATE_OPTIONS.MONTH]: 'month',
  [DATE_OPTIONS.DATE]: 'date',
  [DATE_OPTIONS.FULL]: 'full'
} as const;

export const TIME_VALUES = {
  [TIME_OPTIONS.HOUR]: 'hour',
  [TIME_OPTIONS.MINUTE]: 'minute',
  [TIME_OPTIONS.SECOND]: 'second',
} as const;

export const DATE_FORMAT = {
  [DATE_VALUES[DATE_OPTIONS.YEAR]]: 'YYYY',
  [DATE_VALUES[DATE_OPTIONS.MONTH]]: 'YYYY-MM',
  [DATE_VALUES[DATE_OPTIONS.DATE]]: 'YYYY-MM-DD',
  [DATE_VALUES[DATE_OPTIONS.FULL]]: 'YYYY-MM-DD HH:mm:ss',
} as const;

export const TIME_FORMAT = {
  [TIME_VALUES[TIME_OPTIONS.HOUR]]: 'HH',
  [TIME_VALUES[TIME_OPTIONS.MINUTE]]: 'HH:mm',
  [TIME_VALUES[TIME_OPTIONS.SECOND]]: 'HH:mm:ss',
} as const;

export const TIME_12_FORMAT = {
  [TIME_VALUES[TIME_OPTIONS.HOUR]]: 'hh A',
  [TIME_VALUES[TIME_OPTIONS.MINUTE]]: 'hh:mm A',
  [TIME_VALUES[TIME_OPTIONS.SECOND]]: 'hh:mm:ss A',
} as const;

// 表单的布局
export const LAYOUT_OPTIONS = {
  HORIZONTAL: '水平',
  VERTICAL: '垂直'
} as const;

export const LAYOUT_VALUES = {
  [LAYOUT_OPTIONS.HORIZONTAL]: 'horizontal',
  [LAYOUT_OPTIONS.VERTICAL]: 'vertical'
} as const;

// 图片填充方式
export const FILL_OPTIONS = {
  CONTAIN: '原图局中',
  COVER: '局中填满',
  FILL: '拉伸填满'
} as const;

export const FILL_VALUES = {
  [FILL_OPTIONS.CONTAIN]: 'contain',
  [FILL_OPTIONS.COVER]: 'cover',
  [FILL_OPTIONS.FILL]: 'fill'
} as const;

// 页签样式常量
export const TABS_TYPE_OPTIONS = {
  LINE: 'line',
  CARD: 'card',
  CARD_GUTTER: 'card-gutter',
  TEXT: 'text',
  ROUNDED: 'rounded',
  CAPSULE: 'capsule'
} as const;

// 页签位置常量
export const TABS_POSITION_OPTIONS = {
  TOP: '上',
  BOTTOM: '下',
  LEFT: '左',
  RIGHT: '右'
} as const;

export const TABS_POSITION_VALUES = {
  [TABS_POSITION_OPTIONS.TOP]: 'top',
  [TABS_POSITION_OPTIONS.BOTTOM]: 'bottom',
  [TABS_POSITION_OPTIONS.LEFT]: 'left',
  [TABS_POSITION_OPTIONS.RIGHT]: 'right'
} as const;

// 折叠选项常量
export const COLLAPSED_OPTIONS = {
  EXPOSED: '展开',
  COLLAPSED: '收起',
  DISABLED_COLLAPSED: '不折叠'
} as const;

export const COLLAPSED_VALUES = {
  [COLLAPSED_OPTIONS.EXPOSED]: 'exposed',
  [COLLAPSED_OPTIONS.COLLAPSED]: 'collapsed',
  [COLLAPSED_OPTIONS.DISABLED_COLLAPSED]: 'noCollapsed',
} as const;

// 表格的行点击跳转方式
export enum RedirectMethod {
  DRAWER = 'drawer',
  NEW_TAB = 'newTab',
  CURRENT_TAB = 'currentTab',
  MODAL = 'modal',
  REFRESH = 'refresh',
  PROMPT_JUMP = 'promptJump'
}

// 内容对齐方式
export const BUTTON_OPTIONS = {
  HIDDEN: '隐藏',
  DISABLED: '置灰'
} as const;

export const BUTTON_VALUES = {
  [BUTTON_OPTIONS.HIDDEN]: 'hidden',
  [BUTTON_OPTIONS.DISABLED]: 'disabled'
} as const;

// 表格操作按钮
export enum TableOperationButton {
  EDIT = 'edit',
  DELETE = 'delete'
}

// 按钮展示
export enum TableOperationButtonStyle {
  ICON = 'icon',
  TEXT = 'text',
  ALL = 'all'
}

// 默认值
export const DEFAULT_VALUE_TYPES = {
  CUSTOM: 'custom',
  FORMULA: 'formula',
  LINKAGE: 'linkage'
} as const;
export const DEFAULT_VALUE_TYPES_LABELS = {
  custom: '自定义',
  formula: '公式计算',
  linkage: '数据联动'
} as const;

export const PHONE_TYPE = {
  MOBILE: 'mobile', // 手机
  LANDLINE: 'landline', // 座机
} as const;

// 静态值、动态值、变量
export const DATE_EXTREME_TYPE = {
  STATIC: 'static',
  DYNAMIC: 'dynamic',
  VARIABLE: 'variable',
} as const;

// 当天/昨天/明天/7天前/7天后/30天前/30天后/自定义
export const DATE_DYNAMIC_TYPE = {
  TODAY: 'today',
  YESTERDAY: 'yesterday',
  TOMORROW: 'tomorrow',
  BEFOREWEEK: 'beforeWeek',
  AFTERWEEK: 'afterWeek',
  BEFOREMONTH: 'beforeMonth',
  AFTERMONTH: 'afterMonth',
  CUSTOM: 'custom'
} as const;

export const DATE_DYNAMIC_VALUE = {
  today: 0,
  yesterday: -1,
  tomorrow: 1,
  beforeWeek: -7,
  afterWeek: 7,
  beforeMonth: -30,
  afterMonth: 30,
  custom: null
} as const;

export const WEEK_OPTIONS_LABEL = {
  // Monday/Tuesday/Wednesday/Thursday/Friday/Saturday/Sunday
  Monday: '星期一',
  Tuesday: '星期二',
  Wednesday: '星期三',
  Thursday: '星期四',
  Friday: '星期五',
  Saturday: '星期六',
  Sunday: '星期日',
} as const;

// 对应getDay返回数字
export const WEEK_OPTIONS_NUMBER = {
  // Monday/Tuesday/Wednesday/Thursday/Friday/Saturday/Sunday
  Monday: 1,
  Tuesday: 2,
  Wednesday: 3,
  Thursday: 4,
  Friday: 5,
  Saturday: 6,
  Sunday: 0
} as const;

export const WEEK_OPTIONS = {
  // Monday/Tuesday/Wednesday/Thursday/Friday/Saturday/Sunday
  MONDAY: 'Monday',
  TUESDAY: 'Tuesday',
  WEDNESDAY: 'Wednesday',
  THURSDAY: 'Thursday',
  FRIDAY: 'Friday',
  SATURDAY: 'Saturday',
  SUNDAY: 'Sunday'
} as const;

export const COLOR_MODE_TYPES = {
  TAG: 'tag',
  POINT: 'point'
}

// 
export const DEFAULT_OPTIONS_TYPE = {
  CUSTOM: 'CUSTOM',
  DICT: 'DICT'
} as const;