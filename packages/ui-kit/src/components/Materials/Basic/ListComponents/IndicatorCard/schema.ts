import type {
  TTextDefaultType,
  TBooleanDefaultType,
  ICommonConfigType,
  TRadioDefaultType,
  TSelectDefaultType,
  IStatusConfigType,
  IWidthConfigType,
  IIndicatorCardStyleConfigType,
  IIndicatorCardConfigType
} from '../../../types';
import {
  baseConfig,
  baseDefault,
  labelConfig,
  statusConfig,
  widthConfig,
  indicatorCardStyleConfig,
  indicatorCardConfig,
  type ICommonBaseType,
  type TStatusSelectKeyType,
  type TWidthSelectKeyType
} from '../../../common';
import {
  STATUS_OPTIONS,
  STATUS_VALUES,
  WIDTH_OPTIONS,
  WIDTH_VALUES,
  INDICATOR_CARD_STYLE_TYPE,
  INDICATOR_CALCULATE_TYPE,
  INDICATOR_TIME_DEMENSION,
  INDICATOR_COMPARE_CALCULATE_METHOD,
  INDICATOR_COMPARE_CALCULATE_TYPE
} from '../../../constants';

export interface XIndicatorCardSchema {
  editData: TXIndicatorCardEditData;
  config: XIndicatorCardConfig;
}

export type TXIndicatorCardEditData = Array<
  | ICommonConfigType
  | IIndicatorCardStyleConfigType
  | IIndicatorCardConfigType
  | IStatusConfigType<TStatusSelectKeyType>
  | IWidthConfigType<TWidthSelectKeyType>
>;

export interface XIndicatorCardConfig extends ICommonBaseType {
  /**
   * 输入框标题
   * text：标题
   * display：是否显示
   */
  label: {
    text: TTextDefaultType;
    display: TBooleanDefaultType;
  };
  /**
   * 样式库
   */
  styleType: string;

  /**
   * 指标配置
   */
  indicatorList: {
    // 指标标题
    label: {
      text: TTextDefaultType;
      display: TBooleanDefaultType;
    };
    // 指标描述
    describe: string;
    // 数据源
    metaData: string;
    tableName: string;
    // 指标字段
    calculateField: string;
    calculateType: string;
    // 数据过滤
    condition: any[];
    // 格式
    precisionLimit: boolean;
    precision?: number;
    percent: boolean;
    unitLimit: boolean;
    unit?: string;
    thousandsSeparator: boolean;
    absoluteValue: boolean;
    // 图标样式
    icon: {
      display: TBooleanDefaultType;
      name: string;
      color: string;
    };
    // 背景颜色
    backgroundColor: string;
    // 同环比
    compareLimit: boolean;
    // 同环比描述
    compareDescribe: string;
    // 时间字段
    timeField: string;
    // 时间维度
    timeDimension: string;
    // 计算方式
    compareCalculate: string;
    // 计算类型
    compareCalculateType: string;
    // 显示状态
    status: string;
    // 卡片宽度
    width: string;
  }[];

  /**
   * 显示状态：可用、隐藏、只读
   * 可选值: 'default' | 'hidden' | 'readonly'
   */
  status?: TRadioDefaultType<TStatusSelectKeyType>;
  /**
   * 宽度
   */
  width: TSelectDefaultType<TWidthSelectKeyType>;
}

const XIndicatorCard: XIndicatorCardSchema = {
  editData: [
    ...baseConfig,
    labelConfig,
    indicatorCardStyleConfig,
    indicatorCardConfig,
    // 显示状态
    statusConfig,
    // 宽度
    widthConfig
  ],
  config: {
    ...baseDefault,
    label: {
      text: '指标卡',
      display: false
    },
    styleType: INDICATOR_CARD_STYLE_TYPE.ONE,
    indicatorList: [
      {
        label: { display: true, text: '指标一' },
        // 指标描述
        describe: '',
        // 数据源
        metaData: '',
        tableName: '',
        // 指标字段
        calculateField: 'id',
        calculateType: INDICATOR_CALCULATE_TYPE.COUNT,
        // 数据过滤
        condition: [],
        // 格式
        precisionLimit: false,
        precision: 0,
        percent: false,
        unitLimit: false,
        unit: '',
        thousandsSeparator: false,
        absoluteValue: false,
        // 图标样式
        icon: { display: false, name: '', color: 'rgb(var(--primaey-6))' },
        // 背景颜色
        backgroundColor: '#FFFFFF',
        // 同环比
        compareLimit: false,
        // 同环比描述
        compareDescribe: '较上月',
        // 时间字段
        timeField: '',
        // 时间维度
        timeDimension: INDICATOR_TIME_DEMENSION.MONTH,
        // 计算方式
        compareCalculate: INDICATOR_COMPARE_CALCULATE_METHOD.COMPARE,
        // 计算类型
        compareCalculateType: INDICATOR_COMPARE_CALCULATE_TYPE.RATE,
        // 显示状态
        status: STATUS_VALUES[STATUS_OPTIONS.DEFAULT],
        // 卡片宽度
        width: WIDTH_VALUES[WIDTH_OPTIONS.QUARTER]
      },
      {
        label: { display: true, text: '指标二' },
        // 指标描述
        describe: '',
        // 数据源
        metaData: '',
        tableName: '',
        // 指标字段
        calculateField: 'id',
        calculateType: INDICATOR_CALCULATE_TYPE.COUNT,
        // 数据过滤
        condition: [],
        // 格式
        precisionLimit: false,
        precision: 0,
        percent: false,
        unitLimit: false,
        unit: '',
        thousandsSeparator: false,
        absoluteValue: false,
        // 图标样式
        icon: { display: false, name: '', color: 'rgb(var(--primaey-6))' },
        // 背景颜色
        backgroundColor: '#FFFFFF',
        // 同环比
        compareLimit: false,
        // 同环比描述
        compareDescribe: '较上月',
        // 时间字段
        timeField: '',
        // 时间维度
        timeDimension: INDICATOR_TIME_DEMENSION.MONTH,
        // 计算方式
        compareCalculate: INDICATOR_COMPARE_CALCULATE_METHOD.COMPARE,
        // 计算类型
        compareCalculateType: INDICATOR_COMPARE_CALCULATE_TYPE.RATE,
        // 显示状态
        status: STATUS_VALUES[STATUS_OPTIONS.DEFAULT],
        // 卡片宽度
        width: WIDTH_VALUES[WIDTH_OPTIONS.QUARTER]
      },
      {
        label: { display: true, text: '指标三' },
        // 指标描述
        describe: '',
        // 数据源
        metaData: '',
        tableName: '',
        // 指标字段
        calculateField: 'id',
        calculateType: INDICATOR_CALCULATE_TYPE.COUNT,
        // 数据过滤
        condition: [],
        // 格式
        precisionLimit: false,
        precision: 0,
        percent: false,
        unitLimit: false,
        unit: '',
        thousandsSeparator: false,
        absoluteValue: false,
        // 图标样式
        icon: { display: false, name: '', color: 'rgb(var(--primaey-6))' },
        // 背景颜色
        backgroundColor: '#FFFFFF',
        // 同环比
        compareLimit: false,
        // 同环比描述
        compareDescribe: '较上月',
        // 时间字段
        timeField: '',
        // 时间维度
        timeDimension: INDICATOR_TIME_DEMENSION.MONTH,
        // 计算方式
        compareCalculate: INDICATOR_COMPARE_CALCULATE_METHOD.COMPARE,
        // 计算类型
        compareCalculateType: INDICATOR_COMPARE_CALCULATE_TYPE.RATE,
        // 显示状态
        status: STATUS_VALUES[STATUS_OPTIONS.DEFAULT],
        // 卡片宽度
        width: WIDTH_VALUES[WIDTH_OPTIONS.QUARTER]
      },
      {
        label: { display: true, text: '指标四' },
        // 指标描述
        describe: '',
        // 数据源
        metaData: '',
        tableName: '',
        // 指标字段
        calculateField: 'id',
        calculateType: INDICATOR_CALCULATE_TYPE.COUNT,
        // 数据过滤
        condition: [],
        // 格式
        precisionLimit: false,
        precision: 0,
        percent: false,
        unitLimit: false,
        unit: '',
        thousandsSeparator: false,
        absoluteValue: false,
        // 图标样式
        icon: { display: false, name: '', color: 'rgb(var(--primaey-6))' },
        // 背景颜色
        backgroundColor: '#FFFFFF',
        // 同环比
        compareLimit: false,
        // 同环比描述
        compareDescribe: '较上月',
        // 时间字段
        timeField: '',
        // 时间维度
        timeDimension: INDICATOR_TIME_DEMENSION.MONTH,
        // 计算方式
        compareCalculate: INDICATOR_COMPARE_CALCULATE_METHOD.COMPARE,
        // 计算类型
        compareCalculateType: INDICATOR_COMPARE_CALCULATE_TYPE.RATE,
        // 显示状态
        status: STATUS_VALUES[STATUS_OPTIONS.DEFAULT],
        // 卡片宽度
        width: WIDTH_VALUES[WIDTH_OPTIONS.QUARTER]
      }
    ],
    status: STATUS_VALUES[STATUS_OPTIONS.DEFAULT],
    width: WIDTH_VALUES[WIDTH_OPTIONS.FULL]
  }
};

export default XIndicatorCard;
