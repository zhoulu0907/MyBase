import {
    baseConfig,
    baseDefault,
    statusConfig,
    widthConfig,
    titleTextConfig,
    webViewUrlConfig,
    webViewParamsConfig,
    type ICommonBaseType,
    type TStatusSelectKeyType,
    type TWidthSelectKeyType
} from '../../../common';
import {
    STATUS_OPTIONS,
    STATUS_VALUES,
    WIDTH_OPTIONS,
    WIDTH_VALUES
} from '../../../constants';
import type {
    IBooleanConfigType,
    IStatusConfigType,
    ITextConfigType,
    IWidthConfigType,
    IWebViewParamsConfigType,
    TRadioDefaultType,
    TSelectDefaultType,
    TTextDefaultType
} from '../../../types';

export interface WebViewParamConfig {
  key: string;
  fieldName: string;
  displayName: string;
  fieldType: string;
}

export interface XWebViewSchema {
  editData: TXWebViewEditData;
  config: XWebViewConfig;
}

export type TXWebViewEditData = Array<
  ITextConfigType
  | IWidthConfigType<TWidthSelectKeyType>
  | IStatusConfigType<TStatusSelectKeyType>
  | IBooleanConfigType
  | IWebViewParamsConfigType
>;

export interface XWebViewConfig extends ICommonBaseType {
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
   * 标题
   */
  title: TTextDefaultType;

  /**
   * 网页链接
   */
  webViewUrl: TTextDefaultType;

  /**
   * 数据绑定
   */
  metaData: TTextDefaultType;

  /**
   * 表名
   */
  tableName: TTextDefaultType;

  /**
   * 参数列表
   */
  params?: WebViewParamConfig[];
}

const XWebView: XWebViewSchema = {
  editData: [
    ...baseConfig,
    titleTextConfig,
    webViewUrlConfig,
    webViewParamsConfig,
    widthConfig,
    statusConfig
    // TODO(mickey): 补充颜色 背景色配置
  ],
  config: {
    ...baseDefault,
    title: '网页浏览器',
    webViewUrl: 'https://example.com', // 使用支持 iframe 的示例网站
    metaData: '',
    tableName: '',
    params: [],
    width: WIDTH_VALUES[WIDTH_OPTIONS.FULL],
    status: STATUS_VALUES[STATUS_OPTIONS.DEFAULT]
  }
};

export default XWebView;
