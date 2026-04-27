import {
    baseConfig,
    baseDefault,
    statusConfig,
    labelConfig,
    canvasCardConfig,
    paginationConfig,
    type ICommonBaseType,
    type TStatusSelectKeyType,
    type TWidthSelectKeyType,
    TPagePositionSelectKeyType
} from '../../../common';
import { PAGINATION_POSITION_OPTIONS, PAGINATION_POSITION_VALUES, STATUS_OPTIONS, STATUS_VALUES, WIDTH_OPTIONS, WIDTH_VALUES } from '../../../constants';
import type {
    IStatusConfigType,
    ITextConfigType,
    ICommonConfigType,
    TRadioDefaultType,
    ILabelConfigType,
    TTextDefaultType,
    TBooleanDefaultType,
    TSelectDefaultType,
    ICanvasCardConfigType,
    TNumberDefaultType,
    IPaginationConfigType
} from '../../../types';

export type DisplayFieldsConfig = {
  mainImage?: string;
  mainImageFill?: string;
  categoryTags?: string[];
  mainTitle?: string;
  cardContent?: string;
  auxiliaryInfo?: string[];
  countHint?: string;
  showCountIcon?: boolean;
  countIcon?: string;
  avatar?: string;
  avatarFill?: string;
  cardFields?: string[];
}

export interface XCanvasCardSchema {
  editData: TXCanvasCardEditData;
  config: XCanvasCardConfig;
}

export type TXCanvasCardEditData = Array<
  ITextConfigType | IStatusConfigType<TStatusSelectKeyType> | ILabelConfigType | ICanvasCardConfigType | ICommonConfigType | IPaginationConfigType
>;

export interface XCanvasCardConfig extends ICommonBaseType {
  /**
   * 组件状态：可用、隐藏、只读
   * 可选值: 'default' | 'hidden' | 'readonly'
   */
  status?: TRadioDefaultType<TStatusSelectKeyType>;

  /**
   * 字段宽度
   */
  width: TSelectDefaultType<TWidthSelectKeyType>;

  /**
   * 标题配置
   */
  label: {
    text: TTextDefaultType;
    display: TBooleanDefaultType;
  };

  /**
   * 组件名称
   */
  componentName?: 'CanvasCardType1' | 'CanvasCardType2';

  /**
   * 表名
   */
  tableName?: TTextDefaultType;

  /**
   * 元数据
   */
  metaData?: TTextDefaultType;

  /**
   * 显示字段配置
   */
  displayFields?: DisplayFieldsConfig;

  /**
   * 页面类型
   */
  pageSetType?: number;

  /**
 * 分页  开启分页 分页数 分页位置
 */
  paginationConfig: {
    display: TBooleanDefaultType;
    pageSize: TNumberDefaultType;
    pagePosition: TSelectDefaultType<TPagePositionSelectKeyType>;
  }
}

const XCanvasCard: XCanvasCardSchema = {
  editData: [...baseConfig, labelConfig, canvasCardConfig, statusConfig, paginationConfig],
  config: {
    ...baseDefault,
    label: {
      text: '画布卡片',
      display: false
    },
    componentName: 'CanvasCardType1',
    status: STATUS_VALUES[STATUS_OPTIONS.DEFAULT],
    width: WIDTH_VALUES[WIDTH_OPTIONS.FULL],
    tableName: '',
    metaData: '',
    displayFields: {
      mainImage: '',
      mainImageFill: 'fill',
      categoryTags: [''],
      mainTitle: '',
      cardContent: '',
      auxiliaryInfo: [],
      countHint: '',
      showCountIcon: false,
      countIcon: 'icon1',
      avatar: '',
      avatarFill: 'fill',
      cardFields: ['', '', '', '']
    },
    paginationConfig: {
      display: true,
      pageSize: 20,
      pagePosition: PAGINATION_POSITION_VALUES[PAGINATION_POSITION_OPTIONS.BR]
    },
  }
};

export default XCanvasCard;
