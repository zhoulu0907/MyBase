import { baseConfig, baseDefault, statusConfig, widthConfig, type ICommonBaseType, type TStatusSelectKeyType, type TWidthSelectKeyType } from "@/components/Materials/common";
import { STATUS_OPTIONS, STATUS_VALUES, WIDTH_OPTIONS, WIDTH_VALUES } from "@/components/Materials/constants";
import type { IBooleanConfigType, IStatusConfigType, ITextConfigType, IWidthConfigType, TRadioDefaultType, TSelectDefaultType } from "@/components/Materials/types";


export interface XImageSchema {
    editData: TXImageEditData;
    config: XImageConfig;
}

export type TXImageEditData = Array<
  ITextConfigType |
  IWidthConfigType<TWidthSelectKeyType> |
  IStatusConfigType<TStatusSelectKeyType> |
  IBooleanConfigType
>;


export interface XImageConfig extends ICommonBaseType {
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

const XImage: XImageSchema = {
    editData: [
        ...baseConfig,
        widthConfig,
        statusConfig,
    ],
    config: {
        ...baseDefault,
        width: WIDTH_VALUES[WIDTH_OPTIONS.HALF],
        status: STATUS_VALUES[STATUS_OPTIONS.DEFAULT],
    }
};

export default XImage;