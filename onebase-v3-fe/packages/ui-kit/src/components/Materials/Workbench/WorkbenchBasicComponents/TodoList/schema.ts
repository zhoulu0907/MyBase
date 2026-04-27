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
  WORKBENCH_THEME_OPTIONS
} from '../../core/constants';
import { ILabelConfigType, IBooleanConfigType, TBooleanDefaultType, TTextDefaultType } from '../../core/types';
import { IDataConfigConfigType, IThemeConfigType, INumberConfigType } from '../../core/types';
import { labelNameConfig, themeConfig, dataConfigConfig, dataCountConfig, showMoreLinkConfig, showMoreConfig } from '../../config/commonConfig';

// 待办事项数据结构
export interface ITodoItem {
  id: string;
  processTitle: string;
  initiator: {
    userId: string;
    name: string;
    avatar: string;
  };
  flowStatus?: string;
  taskStatus?: string;
  formSummary: string;
  arrivalTime?: number;
  createTime?: number;
  submitTime?: number;
  handleTime?: number;
  updateTime?: number;
  taskId: string;
  instanceId: string;
  businessUuid: string;
  nodeCode: string;
}

// 默认待办列表数据（用于非 runtime 模式）
export const pendingListDefault: ITodoItem[] = [
  {
    id: '1',
    processTitle: '张三发起的流程表单测试_表单',
    initiator: {
      userId: '155019577667616772',
      name: '张三',
      avatar: ''
    },
    flowStatus: 'in_approval',
    formSummary: '流程表单测试_表单',
    arrivalTime: 1764924217968,
    submitTime: 1764924218175,
    taskId: '1446542543792771072',
    instanceId: '1446542538365341696',
    businessUuid: '019aed4e-fd96-7901-92ba-7c1de80cd46f',
    nodeCode: '3'
  },
  {
    id: '2',
    processTitle: '李四发起的流程表单测试_表单',
    initiator: {
      userId: '155019577667616772',
      name: '李四',
      avatar: ''
    },
    flowStatus: 'in_approval',
    formSummary: '流程表单测试_表单',
    arrivalTime: 1764924152154,
    submitTime: 1764924153065,
    taskId: '1446542266519916544',
    instanceId: '1446542260803080192',
    businessUuid: '019aed4e-fd96-7901-92ba-7c1de80cd46f',
    nodeCode: '3'
  },
];

export interface XTodoListSchema {
  editData: TXTodoListEditData;
  config: XTodoListConfig;
}

export type TXTodoListEditData = Array<
  | ILabelConfigType
  | ITextConfigType
  | IStatusConfigType<TWorkbenchStatusSelectKeyType>
  | IWidthConfigType<TWorkbenchWidthSelectKeyType>
  | IBooleanConfigType
  | IDataConfigConfigType
  | IThemeConfigType
  | INumberConfigType
>;

export interface XTodoListConfig extends ICommonBaseWorkbenchType {
  componentName: string;
  label: {
    text: TTextDefaultType;
    display: TBooleanDefaultType;
  };
  dataConfig: {
    showPending: boolean;
    showCreated: boolean;
    showHandled: boolean;
    showCc: boolean;
  }
  showMore: boolean,
  showMoreLink: string;
  theme: string;
  dataCount: number;
  userAvatar: TTextDefaultType;
  userName: TTextDefaultType;
  status?: TRadioDefaultType<TWorkbenchStatusSelectKeyType>;
  width: TSelectDefaultType<TWorkbenchWidthSelectKeyType>;
}

const XTodoList: XTodoListSchema = {
  editData: [...workbenchBaseConfig, workbenchStatusConfig, workbenchWidthConfig,
    labelNameConfig,
    dataConfigConfig,
    themeConfig,
    dataCountConfig,
    showMoreConfig,
    showMoreLinkConfig
  ],
  config: {
    ...workbenchBaseDefault,
    componentName: 'TodoList',
    theme: WORKBENCH_THEME_OPTIONS.THEME_1,
    label: {
      text: '待办列表',
      display: true
    },
    dataConfig: {
      showPending: true,
      showCreated: true,
      showHandled: true,
      showCc: true
    },
    showMore: false,
    showMoreLink: '',
    dataCount: 5,
    userAvatar: '',
    userName: '',
    width: WORKBENCH_WIDTH_VALUES[WORKBENCH_WIDTH_OPTIONS.FULL],
    status: WORKBENCH_STATUS_VALUES[WORKBENCH_STATUS_OPTIONS.DEFAULT],
  }
};

export default XTodoList;

