export const CONFIG_TYPES = {
  TEXT_INPUT: 'TextInput',
  TEXT_AREA_INPUT: 'TextAreaInput',
  SELECT_INPUT: 'SelectInput',
  DYNAMIC_SELECT_INPUT: 'DynamicSelectInput',
  NUMBER_INPUT: 'NumberInput',
  SWITCH_INPUT: 'SwitchInput',
  DATE_INPUT: 'DateInput',
  CHECKED: 'Checked',
  RADIO_INPUT: 'RadioInput',
  LABEL_INPUT: 'LabelInput',
  PLACEHOLDER_INPUT: 'PlaceholderInput',
  SELECT_OPTIONS_INPUT: 'OptionsInput',
  MUTIPLE_SELECT_OPTIONS_INPUT: 'MutipleOptionsInput',
  TOOLTIP_INPUT: 'TooltipInput',
  STATUS_RADIO: 'StatusRadio',
  WIDTH_RADIO: 'WidthRadio',
  DEFAULT_VALUE: 'DefaultValue',
  REQUIRED_CHECKBOX: 'RequiredCheckBox',
  COLUMN_COUNT_RADIO: 'ColumnCountRadio',
  COLUMN_GAP_SELECT: 'ColumnGapSelect',
  TABLE_PAGE_POSITION_RADIO: 'TablePagePositionRadio',
  FIELD_DATA: 'FieldData',
  RELATED_FORM_DATA: 'RelatedFormData',
  TABLE_DATA: 'TableData',
  TABLE_PAGE_SIZE: 'TablePageSize',
  UPLOAD_SIZE: 'UploadSize',
  UPLOAD_LIMIT: 'UploadLimit',
  UPLOAD_COMPRESS: 'UploadCompress',
  DATE_TYPE: 'DateType',
  FORM_LAYOUT: 'FormLayout',
  TEXT_ALIGN: 'TextAlign',
  LABEL_COL_SPAN: 'LabelColSpan',
  COLOR: 'Color',
  RADIO_DATA: 'RadioData',
  CHECKBOX_DATA: 'CheckboxData',
  CAROUSEL: 'Carousel',
  FILL_STYLE: 'FillStyle',
  SECURITY: 'Security',
  VERIFY: 'Verify',
  NUMBER_FORMAT: 'NumberFormat',
  SELECT_DATA_SOURCE: 'SelectDataSource',
  SUB_TABLE: 'SubTable',
  TABS: 'Tabs',
  TABS_TYPE: 'TabsType',
  TABS_POSITION: 'TabsPosition',
  IMAGE: 'Image',
  FILE: 'File',
  IMAGE_HANDLE: 'ImageHandle',
  COLLAPSED: 'Collapsed',
  COLLAPSED_STYLE: 'CollapsedStyle',
  AUTO_CODE_RULES: 'autoCodeRules',
  DATE_FORMAT: 'DateFormat',
  DATE_RANGE: 'DateRange',
  TIME_FORMAT: 'TimeFormat',
  COMMON: 'common',
  USER_DEFAULT_VALUE: 'userDefaultValue',
  RATE_CONFIG: 'RateConfig',
  // 检查项 显示方式
  CHECK_ITEM_SHOW_MODE: 'CheckItemShowMode',
  // 卡片数据配置
  CARD_DATA: 'CardData',
  // 数据排序规则
  DATA_SORT_BY: 'DataSortBy',
  // 数据过滤
  DATA_FILTER: 'DataFilter',
  // 封面图片
  COVER_IMAGE: 'CoverImage',
  // 绑定分组筛选
  GROUP_FILTER: 'GroupFilter',
  // 卡片、画布卡片等 的分页配置
  PAGINATION_CONFIG: 'PaginationConfig'
} as const;

export const WIDTH_OPTIONS = {
  QUARTER: '1/4',
  THIRD: '1/3',
  HALF: '1/2',
  TWO_THIRDS: '2/3',
  THREE_QUARTERS: '3/4',
  FULL: 'editor.full'
} as const;

export const WIDTH_VALUES = {
  [WIDTH_OPTIONS.QUARTER]: '25%',
  [WIDTH_OPTIONS.THIRD]: '33.33%',
  [WIDTH_OPTIONS.HALF]: '50%',
  [WIDTH_OPTIONS.TWO_THIRDS]: '66.66%',
  [WIDTH_OPTIONS.THREE_QUARTERS]: '75%',
  [WIDTH_OPTIONS.FULL]: '100%'
} as const;

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

export const DATE_OPTIONS = {
  YEAR: 'Year',
  MONTH: 'Month',
  DATE: 'Date',
  DATETIME: 'DateTime'
} as const;

export const DATE_VALUES = {
  [DATE_OPTIONS.YEAR]: 'year',
  [DATE_OPTIONS.MONTH]: 'month',
  [DATE_OPTIONS.DATE]: 'date',
  [DATE_OPTIONS.DATETIME]: 'datetime'
} as const;
