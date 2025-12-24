import {
  workbenchBaseConfig,
  workbenchBaseDefault,
  type ICommonBaseWorkbenchType,
  type TWorkbenchLayoutSelectKeyType,
  type TWorkbenchStatusSelectKeyType
} from '../../config/workbenchShared';
import { WORKBENCH_CONFIG_TYPES, WORKBENCH_STATUS_OPTIONS, WORKBENCH_STATUS_VALUES } from '../../core/constants';
import type {
  IBooleanConfigType,
  ILabelConfigType,
  ILayoutConfigType,
  INumberConfigType,
  IStatusConfigType,
  ITextAreaConfigType,
  ITextConfigType,
  ITooltipConfigType,
  TBooleanDefaultType,
  TNumberDefaultType,
  TRadioDefaultType,
  TTextDefaultType
} from '../../core/types';
import type { ICarouselContentConfigType, IVerifyConfigType } from '../../core/types';
import SlideOne from '@/assets/workbench/carousel/slide-1.svg';
import SlideTwo from '@/assets/workbench/carousel/slide-2.svg';
import SlideThree from '@/assets/workbench/carousel/slide-3.svg';

export interface XCarouselSchema {
  editData: TXCarouselEditData;
  config: XCarouselConfig;
}

export type TXCarouselEditData = Array<
  | ITextConfigType
  | IBooleanConfigType
  | INumberConfigType
  | ICarouselContentConfigType
  | IVerifyConfigType
  | TTextDefaultType
  | IStatusConfigType<TWorkbenchStatusSelectKeyType>
  | ILayoutConfigType<TWorkbenchLayoutSelectKeyType>
  | ITextAreaConfigType
  | ILabelConfigType
  | ITooltipConfigType
>;

interface Images {
  image: string;
  text?: string;
  url?: string;
}

export interface XCarouselConfig extends ICommonBaseWorkbenchType {
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
   * 轮播间隔，单位秒
   */
  interval?: TNumberDefaultType;
  /**
   * 图片列表
   */
  carouselConfig: Images[];

  /**
   * required：是否必填，未填写时提交报错
   * maxCount：最大上传数量，默认：10
   * maxSize：最大图片大小单位MB，默认：5
   */
  verify: {
    required: TBooleanDefaultType;
    maxCount: TNumberDefaultType;
    maxSize: TNumberDefaultType;
  };
  /**
   * 数据源模式（静态/动态）
   */
  dataSourceMode: 'static' | 'dynamic';
  /**
   * 动态内容来源（表单/实体）
   */
  contentSource?: string;
  /**
   * 轮播图片字段
   */
  imageField?: string;
  /**
   * 链接字段
   */
  linkField?: string;
  /**
   * 筛选条件
   */
  filterCondition?: any[];
  /**
   * 显示条数
   */
  displayCount: number;
  /**
   * 组件状态：可用、隐藏、只读
   * 可选值: 'default' | 'hidden' | 'readonly'
   */
  status?: TRadioDefaultType<TWorkbenchStatusSelectKeyType>;
}

const DEFAULT_CAROUSEL_CONTENT: Images[] = [
  {
    image: SlideOne,
    text: '轮播图1',
    url: ''
  },
  {
    image: SlideTwo,
    text: '轮播图2',
    url: ''
  }
];

const carouselContentConfig: ICarouselContentConfigType = {
  key: 'carouselContent',
  name: '轮播内容',
  type: WORKBENCH_CONFIG_TYPES.WB_CAROUSEL_CONTENT,
  meta: {
    modeField: {
      key: 'dataSourceMode',
      defaultValue: 'static',
      options: [
        { key: 'dynamic', text: '动态数据源', value: 'dynamic' },
        { key: 'static', text: '静态数据源', value: 'static' }
      ]
    },
    dynamicFields: [
      { key: 'contentSource', label: '内容来源', placeholder: '请选择表单' },
      { key: 'imageField', label: '轮播图片', placeholder: '请选择字段' },
      { key: 'linkField', label: '链接地址', placeholder: '请选择字段' }
    ],
    filterField: {
      key: 'filterCondition',
      label: '筛选条件',
      buttonText: '设置条件'
    },
    displayCountField: {
      key: 'displayCount',
      label: '显示条数',
      min: 1,
      max: 50,
      defaultValue: 10
    },
    staticFieldKey: 'carouselConfig'
  }
};

const XCarousel: XCarouselSchema = {
  editData: [
    ...workbenchBaseConfig,
    {
      key: 'label',
      name: '标题名称',
      type: WORKBENCH_CONFIG_TYPES.LABEL_INPUT
    },
    carouselContentConfig
  ],
  config: {
    ...workbenchBaseDefault,
    label: {
      text: '轮播图',
      display: true
    },
    carouselConfig: DEFAULT_CAROUSEL_CONTENT,
    interval: 3,
    verify: {
      required: false,
      maxCount: 10,
      maxSize: 5
    },
    dataSourceMode: 'static',
    contentSource: '',
    imageField: '',
    linkField: '',
    filterCondition: [],
    displayCount: DEFAULT_CAROUSEL_CONTENT.length,
    status: WORKBENCH_STATUS_VALUES[WORKBENCH_STATUS_OPTIONS.DEFAULT]
  }
};

export default XCarousel;
