import { CONFIG_TYPES, STATUS_OPTIONS, STATUS_VALUES, WIDTH_OPTIONS, WIDTH_VALUES } from '@ob/plugin/sdk'

// Mock Data for Entities
export const MOCK_ENTITIES = [
  { entityUuid: 'mock_entity_1', entityName: 'Mock User', tableName: 'mock_user' },
  { entityUuid: 'mock_entity_2', entityName: 'Mock Order', tableName: 'mock_order' },
  { entityUuid: 'mock_entity_3', entityName: 'Mock Product', tableName: 'mock_product' }
];

export const MOCK_FIELDS: Record<string, any[]> = {
  'mock_entity_1': [
    { fieldName: 'name', displayName: 'Name', fieldType: 'TEXT' },
    { fieldName: 'age', displayName: 'Age', fieldType: 'NUMBER' },
    { fieldName: 'email', displayName: 'Email', fieldType: 'TEXT' },
    {
      fieldName: 'status',
      displayName: 'Status',
      fieldType: 'SELECT',
      options: [
        { optionLabel: 'Pending', optionValue: 'pending' },
        { optionLabel: 'Paid', optionValue: 'paid' },
        { optionLabel: 'Cancelled', optionValue: 'cancelled' }
      ]
    }
  ],
  'mock_entity_2': [
    { fieldName: 'order_no', displayName: 'Order No', fieldType: 'TEXT' },
    { fieldName: 'amount', displayName: 'Amount', fieldType: 'NUMBER' }
  ],
  'mock_entity_3': [
    { fieldName: 'product_name', displayName: 'Product Name', fieldType: 'TEXT' },
    { fieldName: 'price', displayName: 'Price', fieldType: 'NUMBER' }
  ]
};

type EditItem = string | { key: string; name: string; type: string }

export function buildCommonFormEditData(extra: EditItem[] = []): EditItem[] {
  return [
    CONFIG_TYPES.LABEL_INPUT,
    CONFIG_TYPES.PLACEHOLDER_INPUT,
    CONFIG_TYPES.TOOLTIP_INPUT,
    CONFIG_TYPES.FIELD_DATA,
    CONFIG_TYPES.DEFAULT_VALUE,
    CONFIG_TYPES.VERIFY,
    CONFIG_TYPES.STATUS_RADIO,
    CONFIG_TYPES.TEXT_ALIGN,
    CONFIG_TYPES.FORM_LAYOUT,
    CONFIG_TYPES.SECURITY,
    CONFIG_TYPES.WIDTH_RADIO,
    ...extra
  ]
}

export function buildCommonFormDefaultConfig(overrides: Record<string, any> = {}): Record<string, any> {
  const base = {
    label: { text: '文字识别', display: true },
    placeholder: '',
    tooltip: '',
    dataField: [],
    defaultValueConfig: { type: 'CUSTOM', customValue: '', formulaValue: '' },
    verify: { required: false, noRepeat: false, lengthLimit: false, minLength: 0, maxLength: 0 },
    status: STATUS_VALUES[STATUS_OPTIONS.DEFAULT],
    align: 'left',
    layout: 'vertical',
    security: { display: false, type: 'none' },
    width: WIDTH_VALUES[WIDTH_OPTIONS.HALF]
  }
  return { ...base, ...overrides }
}

export function textEditData(): EditItem[] {
  return buildCommonFormEditData()
}

export function numberEditData(): EditItem[] {
  return buildCommonFormEditData([CONFIG_TYPES.NUMBER_FORMAT])
}

export function switchEditData(): EditItem[] {
  return buildCommonFormEditData([])
}

export function dateEditData(): EditItem[] {
  return buildCommonFormEditData([CONFIG_TYPES.DATE_TYPE, CONFIG_TYPES.DATE_RANGE, CONFIG_TYPES.DATE_FORMAT])
}

export function dateRangeEditData(): EditItem[] {
  return buildCommonFormEditData([CONFIG_TYPES.DATE_TYPE, CONFIG_TYPES.DATE_RANGE, CONFIG_TYPES.DATE_FORMAT])
}

export function imgUploadEditData(): EditItem[] {
  return buildCommonFormEditData([
    CONFIG_TYPES.UPLOAD_SIZE,
    CONFIG_TYPES.UPLOAD_LIMIT,
    CONFIG_TYPES.UPLOAD_COMPRESS,
    CONFIG_TYPES.IMAGE_HANDLE
  ])
}
