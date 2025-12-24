import { CONFIG_TYPES, WIDTH_VALUES, WIDTH_OPTIONS } from '@ob/plugin/sdk'

export const InputTextSchema = {
  schema: {
    // 对齐 UI-Kit 的可配置项列表（以字符串标识，宿主可映射到实际配置组件）
    editData: [
      CONFIG_TYPES.LABEL_INPUT,
      {
        key: 'titleColor',
        name: '标题颜色',
        type: 'ColorPickerSetter'
      },
      {
        key: 'prefix',
        name: '前缀',
        type: CONFIG_TYPES.TEXT_INPUT
      },
      CONFIG_TYPES.PLACEHOLDER_INPUT,
      CONFIG_TYPES.TOOLTIP_INPUT,
      CONFIG_TYPES.FIELD_DATA,
      CONFIG_TYPES.DEFAULT_VALUE,
      CONFIG_TYPES.VERIFY,
      CONFIG_TYPES.STATUS_RADIO,
      CONFIG_TYPES.TEXT_ALIGN,
      CONFIG_TYPES.FORM_LAYOUT,
      CONFIG_TYPES.SECURITY,
      CONFIG_TYPES.WIDTH_RADIO
    ],
    // 完整的默认配置，字段名与 UI-Kit 保持一致
    config: {
      label: { text: '单行文本', display: true },
      prefix: '',
      placeholder: '请输入',
      tooltip: '',
      dataField: [],
      defaultValueConfig: { type: 'custom', customValue: '', formulaValue: '' },
      verify: {
        required: false,
        noRepeat: false,
        lengthLimit: false,
        minLength: 0,
        maxLength: 0
      },
      status: 'default',
      align: 'left',
      layout: 'vertical',
      security: { display: false, type: 'none' },
      width: WIDTH_VALUES[WIDTH_OPTIONS.FULL]
    }
  },
  template: {
    h: 36,
    w: 118,
    displayName: '单行文本',
    icon: 'text_input_cp.svg',
    category: 'form',
    isPlugin: true
  },
  fieldMap: ['text', 'longText', 'id'],
  entityMap: ['text', 'url', 'address', 'geography', 'password', 'encrypted', 'aggregate', 'id']
}
