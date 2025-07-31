import { baseConfig, baseDefault, statusConfig, widthConfig, type ICommonBaseType, type TPagePositionSelectKeyType, type TStatusSelectKeyType, type TWidthSelectKeyType } from "@/components/Materials/common";
import type { TableColumnProps } from "@arco-design/web-react";
import { CONFIG_TYPES, PAGINATION_POSITION_OPTIONS, PAGINATION_POSITION_VALUES, WIDTH_OPTIONS, WIDTH_VALUES } from "../../../constants";
import type { IBooleanConfigType, ILabelConfigType, IStatusConfigType, ITableColumnConfigType, ITablePagePositionConfigType, ITextConfigType, IWidthConfigType, TBooleanDefaultType, TRadioDefaultType, TSelectDefaultType, TTextDefaultType } from "../../../types";


export interface XTableSchema {
    editData: TXTableEditData;
    config: XTableConfig;
}

export type TXTableEditData = Array<
  ITextConfigType |
  ILabelConfigType |
  IWidthConfigType<TWidthSelectKeyType> |
  IStatusConfigType<TStatusSelectKeyType> |
  ITableColumnConfigType|
  IBooleanConfigType|
  ITablePagePositionConfigType<TPagePositionSelectKeyType>
>;


export interface XTableConfig extends ICommonBaseType {
    /**
     * 输入框标题
     */
    label: TTextDefaultType;

    /**
     * 默认值
     */
    defaultValue?: any[];

    /**
     * 默认表头
     */
    columns?: TableColumnProps[];

    /**
     * 是否显示边框
     */
    border?: TBooleanDefaultType;

    /**
     * 是否显示边框单元格
     */
    borderCell?: TBooleanDefaultType;

    /**
     * 是否显示斑马线
     */
    stripe?: TBooleanDefaultType;

    /**
     * 是否显示表头
     */
    showHeader?: TBooleanDefaultType;

    /**
     * 鼠标悬浮效果
     */
    hover?: TBooleanDefaultType;

    /**
     * 分页位置
     */
    pagePosition?: TSelectDefaultType<TPagePositionSelectKeyType>;


    /**
     * 组件状态：可用、隐藏、只读
     * 可选值: 'default' | 'hidden' | 'readonly'
     */
    status?: TRadioDefaultType<TStatusSelectKeyType>;

    /**
     * 字段宽度
     */
    width: TSelectDefaultType<TWidthSelectKeyType>;
}

const pagePositionConfig: ITablePagePositionConfigType<TPagePositionSelectKeyType> = {
    key: 'pagePosition',
    name: '分页位置',
    type: CONFIG_TYPES.TABLE_PAGE_POSITION_RADIO,
    range: [
        {
            key: PAGINATION_POSITION_VALUES[PAGINATION_POSITION_OPTIONS.BR],
            text: PAGINATION_POSITION_OPTIONS.BR,
            value: PAGINATION_POSITION_VALUES[PAGINATION_POSITION_OPTIONS.BR],
        },
        {
            key: PAGINATION_POSITION_VALUES[PAGINATION_POSITION_OPTIONS.BL],
            text: PAGINATION_POSITION_OPTIONS.BL,
            value: PAGINATION_POSITION_VALUES[PAGINATION_POSITION_OPTIONS.BL],
        },
        {
            key: PAGINATION_POSITION_VALUES[PAGINATION_POSITION_OPTIONS.TR],
            text: PAGINATION_POSITION_OPTIONS.TR,
            value: PAGINATION_POSITION_VALUES[PAGINATION_POSITION_OPTIONS.TR],
        },
        {
            key: PAGINATION_POSITION_VALUES[PAGINATION_POSITION_OPTIONS.TL],
            text: PAGINATION_POSITION_OPTIONS.TL,
            value: PAGINATION_POSITION_VALUES[PAGINATION_POSITION_OPTIONS.TL],
        },
        {
            key: PAGINATION_POSITION_VALUES[PAGINATION_POSITION_OPTIONS.TOP_CENTER],
            text: PAGINATION_POSITION_OPTIONS.TOP_CENTER,
            value: PAGINATION_POSITION_VALUES[PAGINATION_POSITION_OPTIONS.TOP_CENTER],
        },
        {
            key: PAGINATION_POSITION_VALUES[PAGINATION_POSITION_OPTIONS.BOTTOM_CENTER],
            text: PAGINATION_POSITION_OPTIONS.BOTTOM_CENTER,
            value: PAGINATION_POSITION_VALUES[PAGINATION_POSITION_OPTIONS.BOTTOM_CENTER],
        },
    ],
}


const XTable: XTableSchema = {
    editData: [
        ...baseConfig,
        {
            key: 'label',
            name: '标题',
            type: CONFIG_TYPES.LABEL_INPUT,
        },
        {
            key: 'border',
            name: '显示边框',
            type: CONFIG_TYPES.SWITCH_INPUT,
        },
        {
            key: 'borderCell',
            name: '显示单元格',
            type: CONFIG_TYPES.SWITCH_INPUT,
        },
        {
            key: 'showHeader',
            name: '显示表头',
            type: CONFIG_TYPES.SWITCH_INPUT,
        },
        {
            key: 'hover',
            name: '鼠标悬浮效果',
            type: CONFIG_TYPES.SWITCH_INPUT,
        },
        {
            key: 'stripe',
            name: '开启斑马纹',
            type: CONFIG_TYPES.SWITCH_INPUT,
        },
        pagePositionConfig,
        statusConfig,
        widthConfig,
        {
            key: 'columns',
            name: '表头配置',
            type: CONFIG_TYPES.TABLE_COLUMN_LIST,
        },
    ],
    config: {
        ...baseDefault,
        label: '',
        stripe: true,
        border: true,
        borderCell: true,
        showHeader: true,
        hover: true,
        width: WIDTH_VALUES[WIDTH_OPTIONS.FULL],
        pagePosition: PAGINATION_POSITION_VALUES[PAGINATION_POSITION_OPTIONS.BR],
        defaultValue: [
            {
                key: "1",
                name: "Jane Doe",
                salary: 23000,
                address: "32 Park Road, London",
                email: "jane.doe@example.com",
                gender: "male",
            },
            {
                key: "2",
                name: "Alisa Ross",
                salary: 25000,
                address: "35 Park Road, London",
                email: "alisa.ross@example.com",
                gender: "male",
            },
            {
                key: "3",
                name: "Kevin Sandra",
                salary: 22000,
                address: "31 Park Road, London",
                email: "kevin.sandra@example.com",
                gender: "male",
            }
        ],
        columns: [
            {
                title: "name",
                dataIndex: "name",
                fixed: 'left',
                width: 140,
            },
            {
                title: "salary",
                dataIndex: "salary",
            },
            {
                title: "address",
                dataIndex: "address",
            },
            {
                title: "Email",
                dataIndex: "email",
            },
            {
                title: "gender",
                dataIndex: "gender",
            },
            {
                title: "opearate",
                dataIndex: "opearate",
                fixed: 'right',
            }
        ],
    }
};

export default XTable;