import { CONFIG_TYPES, PAGINATION_POSITION_OPTIONS, PAGINATION_POSITION_VALUES, STATUS_OPTIONS, STATUS_VALUES, WIDTH_OPTIONS, WIDTH_VALUES } from "./constants";
import type { ISelectConfigType, IStatusConfigType, ITextConfigType, IWidthConfigType, TTextDefaultType } from "./types";


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

export const baseDefault = {
    cpName: '',
    id: ''
};
