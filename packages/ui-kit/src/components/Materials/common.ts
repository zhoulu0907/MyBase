import {
  ALIGN_OPTIONS,
  ALIGN_VALUES,
  CONFIG_TYPES,
  DATE_OPTIONS,
  DATE_VALUES,
  FILL_OPTIONS,
  FILL_VALUES,
  LAYOUT_OPTIONS,
  LAYOUT_VALUES,
  PAGINATION_POSITION_OPTIONS,
  PAGINATION_POSITION_VALUES,
  STATUS_OPTIONS,
  STATUS_VALUES,
  UPLOAD_OPTIONS,
  UPLOAD_VALUES,
  WIDTH_OPTIONS,
  WIDTH_VALUES
} from './constants';
import type {
  IAlignConfigType,
  ICarouselConfigType,
  IDataFieldConfigType,
  IDateTypeConfigType,
  ILayoutConfigType,
  INumberConfigType,
  IRadioDataConfigType,
  IRelatedFormDataConfigType,
  ISelectConfigType,
  ISelectOptionsConfigType,
  IStatusConfigType,
  ITableDataConfigType,
  ITextConfigType,
  IWidthConfigType,
  TTextDefaultType
} from './types';

export interface ICommonBaseType {
  id: string;
  cpName: TTextDefaultType;
}

export const baseConfig: ITextConfigType[] = [
  {
    key: 'cpName',
    name: '组件名称',
    type: CONFIG_TYPES.TEXT_INPUT
  }
];

export type TWidthSelectKeyType = (typeof WIDTH_VALUES)[keyof typeof WIDTH_VALUES];
export const widthConfig: IWidthConfigType<TWidthSelectKeyType> = {
  key: 'width',
  name: '宽度',
  type: CONFIG_TYPES.WIDTH_RADIO,
  range: [
    {
      key: WIDTH_OPTIONS.QUARTER,
      text: WIDTH_OPTIONS.QUARTER,
      value: WIDTH_VALUES[WIDTH_OPTIONS.QUARTER]
    },
    {
      key: WIDTH_OPTIONS.THIRD,
      text: WIDTH_OPTIONS.THIRD,
      value: WIDTH_VALUES[WIDTH_OPTIONS.THIRD]
    },
    {
      key: WIDTH_OPTIONS.HALF,
      text: WIDTH_OPTIONS.HALF,
      value: WIDTH_VALUES[WIDTH_OPTIONS.HALF]
    },
    {
      key: WIDTH_OPTIONS.TWO_THIRDS,
      text: WIDTH_OPTIONS.TWO_THIRDS,
      value: WIDTH_VALUES[WIDTH_OPTIONS.TWO_THIRDS]
    },
    {
      key: WIDTH_OPTIONS.THREE_QUARTERS,
      text: WIDTH_OPTIONS.THREE_QUARTERS,
      value: WIDTH_VALUES[WIDTH_OPTIONS.THREE_QUARTERS]
    },
    {
      key: WIDTH_OPTIONS.FULL,
      text: WIDTH_OPTIONS.FULL,
      value: WIDTH_VALUES[WIDTH_OPTIONS.FULL]
    }
  ]
};

export type TStatusSelectKeyType = (typeof STATUS_VALUES)[keyof typeof STATUS_VALUES];
export const statusConfig: IStatusConfigType<TStatusSelectKeyType> = {
  key: 'status',
  name: '组件状态',
  type: CONFIG_TYPES.STATUS_RADIO,
  range: [
    {
      key: STATUS_OPTIONS.DEFAULT,
      text: STATUS_OPTIONS.DEFAULT,
      value: STATUS_VALUES[STATUS_OPTIONS.DEFAULT]
    },
    {
      key: STATUS_OPTIONS.READONLY,
      text: STATUS_OPTIONS.READONLY,
      value: STATUS_VALUES[STATUS_OPTIONS.READONLY]
    },
    {
      key: STATUS_OPTIONS.HIDDEN,
      text: STATUS_OPTIONS.HIDDEN,
      value: STATUS_VALUES[STATUS_OPTIONS.HIDDEN]
    }
  ]
};

export type TAlignSelectKeyType = (typeof ALIGN_VALUES)[keyof typeof ALIGN_VALUES];
export const alignConfig: IAlignConfigType<TAlignSelectKeyType> = {
  key: 'align',
  name: '对齐方式',
  type: CONFIG_TYPES.TEXT_ALIGN,
  range: [
    {
      key: ALIGN_OPTIONS.LEFT,
      text: ALIGN_OPTIONS.LEFT,
      value: ALIGN_VALUES[ALIGN_OPTIONS.LEFT]
    },
    {
      key: ALIGN_OPTIONS.CENTER,
      text: ALIGN_OPTIONS.CENTER,
      value: ALIGN_VALUES[ALIGN_OPTIONS.CENTER]
    },
    {
      key: ALIGN_OPTIONS.RIGHT,
      text: ALIGN_OPTIONS.RIGHT,
      value: ALIGN_VALUES[ALIGN_OPTIONS.RIGHT]
    }
  ]
};

export type TDateTypeSelectKeyType = (typeof DATE_VALUES)[keyof typeof DATE_VALUES];
export const dateTypeConfig: IDateTypeConfigType<TDateTypeSelectKeyType> = {
  key: 'dateType',
  name: '日期格式',
  type: CONFIG_TYPES.DATE_TYPE,
  range: [
    {
      key: DATE_OPTIONS.YEAR,
      text: DATE_OPTIONS.YEAR,
      value: DATE_VALUES[DATE_OPTIONS.YEAR]
    },
    {
      key: DATE_OPTIONS.MONTH,
      text: DATE_OPTIONS.MONTH,
      value: DATE_VALUES[DATE_OPTIONS.MONTH]
    },
    {
      key: DATE_OPTIONS.DATE,
      text: DATE_OPTIONS.DATE,
      value: DATE_VALUES[DATE_OPTIONS.DATE]
    },
    {
      key: DATE_OPTIONS.FULL,
      text: DATE_OPTIONS.FULL,
      value: DATE_VALUES[DATE_OPTIONS.FULL]
    }
  ]
};

export type TLayoutSelectKeyType = (typeof LAYOUT_VALUES)[keyof typeof LAYOUT_VALUES];
export const layoutConfig: ILayoutConfigType<TLayoutSelectKeyType> = {
  key: 'layout',
  name: '布局方式',
  type: CONFIG_TYPES.FORM_LAYOUT,
  range: [
    {
      key: LAYOUT_OPTIONS.HORIZONTAL,
      text: LAYOUT_OPTIONS.HORIZONTAL,
      value: LAYOUT_VALUES[LAYOUT_OPTIONS.HORIZONTAL]
    },
    {
      key: LAYOUT_OPTIONS.VERTICAL,
      text: LAYOUT_OPTIONS.VERTICAL,
      value: LAYOUT_VALUES[LAYOUT_OPTIONS.VERTICAL]
    }
  ]
};

export type TDirectionSelectKeyType = (typeof LAYOUT_VALUES)[keyof typeof LAYOUT_VALUES];
export const directionConfig: ILayoutConfigType<TDirectionSelectKeyType> = {
  key: 'direction',
  name: '方向',
  type: CONFIG_TYPES.FORM_LAYOUT,
  range: [
    {
      key: LAYOUT_OPTIONS.HORIZONTAL,
      text: LAYOUT_OPTIONS.HORIZONTAL,
      value: LAYOUT_VALUES[LAYOUT_OPTIONS.HORIZONTAL]
    },
    {
      key: LAYOUT_OPTIONS.VERTICAL,
      text: LAYOUT_OPTIONS.VERTICAL,
      value: LAYOUT_VALUES[LAYOUT_OPTIONS.VERTICAL]
    }
  ]
};

// 标题宽度
export const labelColSpanConfig: INumberConfigType = {
  key: 'labelColSpan',
  name: '标题宽度',
  type: CONFIG_TYPES.NUMBER_INPUT
};

// 文件列表
export type TUploadSelectKeyType = (typeof UPLOAD_VALUES)[keyof typeof UPLOAD_VALUES];
export const listTypeConfig: IStatusConfigType<TUploadSelectKeyType> = {
  key: 'listType',
  name: '展示样式',
  type: CONFIG_TYPES.STATUS_RADIO,
  range: [
    {
      key: UPLOAD_OPTIONS.TEXT,
      text: UPLOAD_OPTIONS.TEXT,
      value: UPLOAD_VALUES[UPLOAD_OPTIONS.TEXT]
    },
    {
      key: UPLOAD_OPTIONS.LIST,
      text: UPLOAD_OPTIONS.LIST,
      value: UPLOAD_VALUES[UPLOAD_OPTIONS.LIST]
    },
    {
      key: UPLOAD_OPTIONS.CARD,
      text: UPLOAD_OPTIONS.CARD,
      value: UPLOAD_VALUES[UPLOAD_OPTIONS.CARD]
    }
  ]
};

export type TPagePositionSelectKeyType = (typeof PAGINATION_POSITION_VALUES)[keyof typeof PAGINATION_POSITION_VALUES];
export const pagePositionConfig: ISelectConfigType<TPagePositionSelectKeyType> = {
  key: 'pagePosition',
  name: '分页位置',
  type: CONFIG_TYPES.SELECT_INPUT,
  range: [
    {
      key: PAGINATION_POSITION_OPTIONS.BR,
      text: PAGINATION_POSITION_OPTIONS.BR,
      value: PAGINATION_POSITION_VALUES[PAGINATION_POSITION_OPTIONS.BR]
    },
    {
      key: PAGINATION_POSITION_OPTIONS.BL,
      text: PAGINATION_POSITION_OPTIONS.BL,
      value: PAGINATION_POSITION_VALUES[PAGINATION_POSITION_OPTIONS.BL]
    },
    {
      key: PAGINATION_POSITION_OPTIONS.TR,
      text: PAGINATION_POSITION_OPTIONS.TR,
      value: PAGINATION_POSITION_VALUES[PAGINATION_POSITION_OPTIONS.TR]
    },
    {
      key: PAGINATION_POSITION_OPTIONS.TL,
      text: PAGINATION_POSITION_OPTIONS.TL,
      value: PAGINATION_POSITION_VALUES[PAGINATION_POSITION_OPTIONS.TL]
    },
    {
      key: PAGINATION_POSITION_OPTIONS.TOP_CENTER,
      text: PAGINATION_POSITION_OPTIONS.TOP_CENTER,
      value: PAGINATION_POSITION_VALUES[PAGINATION_POSITION_OPTIONS.TOP_CENTER]
    },
    {
      key: PAGINATION_POSITION_OPTIONS.BOTTOM_CENTER,
      text: PAGINATION_POSITION_OPTIONS.BOTTOM_CENTER,
      value: PAGINATION_POSITION_VALUES[PAGINATION_POSITION_OPTIONS.BOTTOM_CENTER]
    }
  ]
};

export type TFillSelectKeyType = (typeof FILL_VALUES)[keyof typeof FILL_VALUES];
export const fillConfig: IStatusConfigType<TFillSelectKeyType> = {
  key: 'fillStyle',
  name: '填充方式',
  type: CONFIG_TYPES.STATUS_RADIO,
  range: [
    {
      key: FILL_OPTIONS.CONTAIN,
      text: FILL_OPTIONS.CONTAIN,
      value: FILL_VALUES[FILL_OPTIONS.CONTAIN]
    },
    {
      key: FILL_OPTIONS.COVER,
      text: FILL_OPTIONS.COVER,
      value: FILL_VALUES[FILL_OPTIONS.COVER]
    },
    {
      key: FILL_OPTIONS.FILL,
      text: FILL_OPTIONS.FILL,
      value: FILL_VALUES[FILL_OPTIONS.FILL]
    }
  ]
};

export const dataFieldConfig: IDataFieldConfigType[] = [
  {
    key: 'dataField',
    name: '数据字段',
    type: CONFIG_TYPES.FIELD_DATA
  }
];

export const relatedFormdataFieldConfig: IRelatedFormDataConfigType[] = [
  {
    key: 'relatedFormDataField',
    name: '关联表单',
    type: CONFIG_TYPES.RELATED_FORM_DATA
  }
];

export const tableMetaDataConfig: ITableDataConfigType = {
  key: 'metaData',
  name: '数据',
  type: CONFIG_TYPES.TABLE_DATA
};

export const keyDataConfig: ITableDataConfigType = {
  key: 'keyData',
  name: '主键',
  type: CONFIG_TYPES.TABLE_DATA
};

export const baseDefault = {
  cpName: '',
  id: ''
};

export const radioDataConfig: IRadioDataConfigType = {
  key: 'radioData',
  name: '自定义选项',
  type: CONFIG_TYPES.RADIO_DATA
};

export const selectOptionsConfig: ISelectOptionsConfigType = {
  key: 'selectOptions',
  name: '自定义选项',
  type: CONFIG_TYPES.SELECT_OPTIONS_INPUT
};

export const carouselConfig: ICarouselConfigType = {
  key: 'carousel',
  name: '图片',
  type: CONFIG_TYPES.CAROUSEL
};
