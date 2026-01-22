import {
  workbenchBaseConfig,
  workbenchBaseDefault,
  workbenchStatusConfig,
  workbenchWidthConfig,
  IStatusConfigType,
  ITextConfigType,
  IWidthConfigType,
  TRadioDefaultType,
  TSelectDefaultType,
  type ICommonBaseWorkbenchType,
  type TWorkbenchStatusSelectKeyType,
  type TWorkbenchWidthSelectKeyType
} from '../../config/workbenchShared';
import {
  WORKBENCH_STATUS_OPTIONS,
  WORKBENCH_STATUS_VALUES,
  WORKBENCH_WIDTH_OPTIONS,
  WORKBENCH_WIDTH_VALUES,
  WORKBENCH_CONFIG_TYPES,
  DATA_CONFIG_RANGE,
  WORKBENCH_THEME_OPTIONS
} from '../../core/constants';
import { ILabelConfigType, IBooleanConfigType, TBooleanDefaultType, TTextDefaultType, IThemeConfigType, INumberConfigType } from '../../core/types';
import { IInformationListContentConfigType } from '../../core/types';

export interface XInformationListSchema {
  editData: TXInformationListEditData;
  config: XInformationListConfig;
}

export type TXInformationListEditData = Array<
  | ILabelConfigType
  | ITextConfigType
  | IStatusConfigType<TWorkbenchStatusSelectKeyType>
  | IWidthConfigType<TWorkbenchWidthSelectKeyType>
  | IBooleanConfigType
  | IThemeConfigType
  | IInformationListContentConfigType
  | INumberConfigType
>;

export interface InformationListItem {
  id: string;
  image?: string;
  title?: string;
  subtitle?: string;
  author?: string;
  date?: string;
  linkType?: 'internal' | 'external';
  internalPageId?: string;
  url?: string;
  [key: string]: unknown;
}

export interface XInformationListConfig extends ICommonBaseWorkbenchType {
  componentName: string;
  label: {
    text: TTextDefaultType;
    display: TBooleanDefaultType;
  };
  theme: string;
  /**
   * 静态资讯列表数据
   */
  informationListConfig: InformationListItem[];
  /**
   * 数据源模式（静态/动态）
   */
  dataSourceMode: 'static' | 'dynamic';
  /**
   * 动态内容来源（表单/实体）
   */
  contentSource?: string;
  /**
   * 动态字段映射
   */
  imageField?: string;
  titleField?: string;
  subtitleField?: string;
  authorField?: string;
  dateField?: string;
  linkField?: string;
  /**
   * 静态资讯列表数据
   */
  staticInformationList?: InformationListItem[];
  /**
   * 筛选条件
   */
  filterCondition?: any[];
  dataCount: number;
  status?: TRadioDefaultType<TWorkbenchStatusSelectKeyType>;
  width: TSelectDefaultType<TWorkbenchWidthSelectKeyType>;
}

const InformationListContentConfig: IInformationListContentConfigType = {
  key: 'informationListContent',
  name: '资讯列表内容',
  type: WORKBENCH_CONFIG_TYPES.WB_INFORMATION_LIST_CONTENT,
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
      { key: 'imageField', label: '图片', placeholder: '请选择字段' },
      { key: 'titleField', label: '主标题', placeholder: '请选择字段' },
      { key: 'subtitleField', label: '副标题', placeholder: '请选择字段' },
      { key: 'authorField', label: '作者', placeholder: '请选择字段' },
      { key: 'dateField', label: '日期', placeholder: '请选择字段' },
      { key: 'linkField', label: '链接', placeholder: '请选择字段' }
    ],
    filterField: {
      key: 'filterCondition',
      label: '筛选条件',
      buttonText: '设置条件'
    },
    staticFieldKey: 'staticInformationList',
  }
};

const XInformationList: XInformationListSchema = {
  editData: [...workbenchBaseConfig, workbenchStatusConfig, workbenchWidthConfig, InformationListContentConfig, {
    key: 'label',
    name: '标题名称',
    type: WORKBENCH_CONFIG_TYPES.LABEL_INPUT
  },
  {
    key: 'theme',
    name: '样式库',
    type: WORKBENCH_CONFIG_TYPES.WB_THEME_SELECTOR
  }, {
    key: 'dataCount',
    name: '数据条数',
    type: WORKBENCH_CONFIG_TYPES.NUMBER_INPUT,
  }],
  config: {
    ...workbenchBaseDefault,
    componentName: 'InformationList',
    theme: WORKBENCH_THEME_OPTIONS.THEME_1,
    label: {
      text: '资讯列表',
      display: true
    },
    dataCount: 10,
    informationListConfig: [],
    dataSourceMode: 'static',
    contentSource: '',
    imageField: '',
    titleField: '',
    subtitleField: '',
    authorField: '',
    dateField: '',
    linkField: '',
    filterCondition: [],
    staticInformationList: [],
    width: WORKBENCH_WIDTH_VALUES[WORKBENCH_WIDTH_OPTIONS.FULL],
    status: WORKBENCH_STATUS_VALUES[WORKBENCH_STATUS_OPTIONS.DEFAULT],
  }
};

export default XInformationList;

