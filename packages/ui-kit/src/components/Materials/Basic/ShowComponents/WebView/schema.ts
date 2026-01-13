import {
    baseConfig,
    baseDefault,
    statusConfig,
    widthConfig,
    titleTextConfig,
    webViewUrlConfig,
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
    TRadioDefaultType,
    TSelectDefaultType,
    TTextDefaultType
} from '../../../types';

export interface XWebViewSchema {
  editData: TXWebViewEditData;
  config: XWebViewConfig;
}

export type TXWebViewEditData = Array<
  ITextConfigType | IWidthConfigType<TWidthSelectKeyType> | IStatusConfigType<TStatusSelectKeyType> | IBooleanConfigType
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
}

const XWebView: XWebViewSchema = {
  editData: [
    ...baseConfig,
    titleTextConfig,
    webViewUrlConfig,
    widthConfig,
    statusConfig
    // TODO(mickey): 补充颜色 背景色配置
  ],
  config: {
    ...baseDefault,
    title: '网页浏览器',
    webViewUrl: 'https://example.com', // 使用支持 iframe 的示例网站
    width: WIDTH_VALUES[WIDTH_OPTIONS.FULL],
    status: STATUS_VALUES[STATUS_OPTIONS.DEFAULT]
  }
};

export default XWebView;
