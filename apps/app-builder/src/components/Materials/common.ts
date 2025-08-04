import { ALIGN_OPTIONS, ALIGN_VALUES, CONFIG_TYPES, DATE_OPTIONS, DATE_VALUES, LAYOUT_OPTIONS, LAYOUT_VALUES, PAGINATION_POSITION_OPTIONS, PAGINATION_POSITION_VALUES, STATUS_OPTIONS, STATUS_VALUES, UPLOAD_OPTIONS, UPLOAD_VALUES, WIDTH_OPTIONS, WIDTH_VALUES } from "./constants";
import type { IAlignConfigType, IDateTypeConfigType, IDynamicSelectConfigType, ILabelColSpanConfigType, ILayoutConfigType, ISelectConfigType, IStatusConfigType, ITextConfigType, IWidthConfigType, TTextDefaultType } from "./types";


export interface ICommonBaseType {
    id: string;
    cpName: TTextDefaultType;
}

export const baseConfig: ITextConfigType[] = [
    {
      key: 'cpName',
      name: '组件名称',
      type: CONFIG_TYPES.TEXT_INPUT,
    }
];


export type TWidthSelectKeyType = typeof WIDTH_VALUES[keyof typeof WIDTH_VALUES];
export const widthConfig: IWidthConfigType<TWidthSelectKeyType>  = {
    key: 'width',
    name: '宽度',
    type: CONFIG_TYPES.WIDTH_RADIO,
    range: [
        {
            key: WIDTH_OPTIONS.QUARTER,
            text: WIDTH_OPTIONS.QUARTER,
            value: WIDTH_VALUES[WIDTH_OPTIONS.QUARTER],
        },
        {
            key: WIDTH_OPTIONS.THIRD,
            text: WIDTH_OPTIONS.THIRD,
            value: WIDTH_VALUES[WIDTH_OPTIONS.THIRD],
        },
        {
            key: WIDTH_OPTIONS.HALF,
            text: WIDTH_OPTIONS.HALF,
            value: WIDTH_VALUES[WIDTH_OPTIONS.HALF],
        },
        {
            key: WIDTH_OPTIONS.TWO_THIRDS,
            text: WIDTH_OPTIONS.TWO_THIRDS,
            value: WIDTH_VALUES[WIDTH_OPTIONS.TWO_THIRDS],
        },
        {
            key: WIDTH_OPTIONS.THREE_QUARTERS,
            text: WIDTH_OPTIONS.THREE_QUARTERS,
            value: WIDTH_VALUES[WIDTH_OPTIONS.THREE_QUARTERS],
        },
        {
            key: WIDTH_OPTIONS.FULL,
            text: WIDTH_OPTIONS.FULL,
            value: WIDTH_VALUES[WIDTH_OPTIONS.FULL],
        },
    ]
}

export type TStatusSelectKeyType = typeof STATUS_VALUES[keyof typeof STATUS_VALUES];
export const statusConfig: IStatusConfigType<TStatusSelectKeyType> = {
    key: 'status',
    name: '组件状态',
    type: CONFIG_TYPES.STATUS_RADIO,
    range: [
        {
            key: STATUS_OPTIONS.DEFAULT,
            text: STATUS_OPTIONS.DEFAULT,
            value: STATUS_VALUES[STATUS_OPTIONS.DEFAULT],
        },
        {
            key: STATUS_OPTIONS.READONLY,
            text: STATUS_OPTIONS.READONLY,
            value: STATUS_VALUES[STATUS_OPTIONS.READONLY],
        },
        {
            key: STATUS_OPTIONS.HIDDEN,
            text: STATUS_OPTIONS.HIDDEN,
            value: STATUS_VALUES[STATUS_OPTIONS.HIDDEN],
        },
    ],
}

export type TAlignSelectKeyType = typeof ALIGN_VALUES[keyof typeof ALIGN_VALUES];
export const alignConfig: IAlignConfigType<TAlignSelectKeyType> = {
    key: 'align',
    name: '对齐方式',
    type: CONFIG_TYPES.TEXT_ALIGN,
    range: [
        {
            key: ALIGN_OPTIONS.LEFT,
            text: ALIGN_OPTIONS.LEFT,
            value: ALIGN_VALUES[ALIGN_OPTIONS.LEFT],
        },
        {
            key: ALIGN_OPTIONS.CENTER,
            text: ALIGN_OPTIONS.CENTER,
            value: ALIGN_VALUES[ALIGN_OPTIONS.CENTER],
        },
        {
            key: ALIGN_OPTIONS.RIGHT,
            text: ALIGN_OPTIONS.RIGHT,
            value: ALIGN_VALUES[ALIGN_OPTIONS.RIGHT],
        },
    ],
}

export type TDateTypeSelectKeyType = typeof DATE_VALUES[keyof typeof DATE_VALUES];
export const dateTypeConfig: IDateTypeConfigType<TDateTypeSelectKeyType> = {
    key: 'dateType',
    name: '日期格式',
    type: CONFIG_TYPES.DATE_TYPE,
    range: [
        {
            key: DATE_OPTIONS.ONLY_YEAR,
            text: DATE_OPTIONS.ONLY_YEAR,
            value: DATE_VALUES[DATE_OPTIONS.ONLY_YEAR],
        },
        {
            key: DATE_OPTIONS.ONLY_MONTH,
            text: DATE_OPTIONS.ONLY_MONTH,
            value: DATE_VALUES[DATE_OPTIONS.ONLY_MONTH],
        },
        {
            key: DATE_OPTIONS.ONLY_DATE,
            text: DATE_OPTIONS.ONLY_DATE,
            value: DATE_VALUES[DATE_OPTIONS.ONLY_DATE],
        },
        {
            key: DATE_OPTIONS.FULL,
            text: DATE_OPTIONS.FULL,
            value: DATE_VALUES[DATE_OPTIONS.FULL],
        },
    ],
}

export type TLayoutSelectKeyType = typeof LAYOUT_VALUES[keyof typeof LAYOUT_VALUES];
export const layoutConfig: ILayoutConfigType<TLayoutSelectKeyType> = {
    key: 'layout',
    name: '布局方式',
    type: CONFIG_TYPES.FORM_LAYOUT,
    range: [
        {
            key: LAYOUT_OPTIONS.HORIZONTAL,
            text: LAYOUT_OPTIONS.HORIZONTAL,
            value: LAYOUT_VALUES[LAYOUT_OPTIONS.HORIZONTAL],
        },
        {
            key: LAYOUT_OPTIONS.VERTICAL,
            text: LAYOUT_OPTIONS.VERTICAL,
            value: LAYOUT_VALUES[LAYOUT_OPTIONS.VERTICAL],
        },
    ],
}

// 标签宽度
export const labelColSpanConfig: ILabelColSpanConfigType = {
    key: 'labelColSpan',
    name: '标签宽度',
    type: CONFIG_TYPES.LABEL_COL_SPAN,
}

// 文件列表
export type TUploadSelectKeyType = typeof UPLOAD_VALUES[keyof typeof UPLOAD_VALUES];
export const listTypeConfig: IStatusConfigType<TUploadSelectKeyType> = {
    key: 'listType',
    name: '展示样式',
    type: CONFIG_TYPES.STATUS_RADIO,
    range: [
        {
            key: UPLOAD_OPTIONS.TEXT,
            text: UPLOAD_OPTIONS.TEXT,
            value: UPLOAD_VALUES[UPLOAD_OPTIONS.TEXT],
        },
        {
            key: UPLOAD_OPTIONS.LIST,
            text: UPLOAD_OPTIONS.LIST,
            value: UPLOAD_VALUES[UPLOAD_OPTIONS.LIST],
        },
        {
            key: UPLOAD_OPTIONS.CARD,
            text: UPLOAD_OPTIONS.CARD,
            value: UPLOAD_VALUES[UPLOAD_OPTIONS.CARD],
        },
    ],
}

export type TPagePositionSelectKeyType = typeof PAGINATION_POSITION_VALUES[keyof typeof PAGINATION_POSITION_VALUES];
export const pagePositionConfig: ISelectConfigType<TPagePositionSelectKeyType> = {
    key: 'pagePosition',
    name: '分页位置',
    type: CONFIG_TYPES.SELECT_INPUT,
    range: [
        {
            key: PAGINATION_POSITION_OPTIONS.BR,
            text: PAGINATION_POSITION_OPTIONS.BR,
            value: PAGINATION_POSITION_VALUES[PAGINATION_POSITION_OPTIONS.BR],
        },
        {
            key: PAGINATION_POSITION_OPTIONS.BL,
            text: PAGINATION_POSITION_OPTIONS.BL,
            value: PAGINATION_POSITION_VALUES[PAGINATION_POSITION_OPTIONS.BL],
        },
        {
            key: PAGINATION_POSITION_OPTIONS.TR,
            text: PAGINATION_POSITION_OPTIONS.TR,
            value: PAGINATION_POSITION_VALUES[PAGINATION_POSITION_OPTIONS.TR],
        },
        {
            key: PAGINATION_POSITION_OPTIONS.TL,
            text: PAGINATION_POSITION_OPTIONS.TL,
            value: PAGINATION_POSITION_VALUES[PAGINATION_POSITION_OPTIONS.TL],
        },
        {
            key: PAGINATION_POSITION_OPTIONS.TOP_CENTER,
            text: PAGINATION_POSITION_OPTIONS.TOP_CENTER,
            value: PAGINATION_POSITION_VALUES[PAGINATION_POSITION_OPTIONS.TOP_CENTER],
        },
        {
            key: PAGINATION_POSITION_OPTIONS.BOTTOM_CENTER,
            text: PAGINATION_POSITION_OPTIONS.BOTTOM_CENTER,
            value: PAGINATION_POSITION_VALUES[PAGINATION_POSITION_OPTIONS.BOTTOM_CENTER],
        },
    ]
}

export const metaDataConfig: IDynamicSelectConfigType = {
    key: 'metaData',
    name: '数据',
    type: CONFIG_TYPES.DYNAMIC_SELECT_INPUT,
}

export const keyDataConfig: IDynamicSelectConfigType = {
    key: 'keyData',
    name: '主键',
    type: CONFIG_TYPES.DYNAMIC_SELECT_INPUT,
}

export const baseDefault = {
    cpName: '',
    id: ''
};
