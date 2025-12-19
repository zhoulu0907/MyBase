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

  // 通用配置（映射到宿主的 baseConfig）
  COMMON: 'common',
  //选择人员默认值
  USER_DEFAULT_VALUE: 'userDefaultValue',
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
