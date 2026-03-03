import {
  workbenchBaseConfig,
  workbenchBaseDefault,
  workbenchWidthConfig,
  type ICommonBaseWorkbenchType,
  type TWorkbenchWidthSelectKeyType,
  type TSelectDefaultType
} from '../../config/workbenchShared';
import { WORKBENCH_WIDTH_OPTIONS, WORKBENCH_WIDTH_VALUES } from '../../core/constants';

export interface XChatbotSchema {
  editData: any[];
  config: XChatbotConfig;
}

export interface XChatbotConfig extends ICommonBaseWorkbenchType {
  componentName: string;
  iframeUrl: string;
  width: TSelectDefaultType<TWorkbenchWidthSelectKeyType>;
}

const XChatbot: XChatbotSchema = {
  editData: [
    ...workbenchBaseConfig,
    workbenchWidthConfig,
    {
      id: 'iframeUrl',
      label: 'iframe地址',
      key: 'iframeUrl',
      type: 'Input',
      component: {
        type: 'Input',
        props: {
          placeholder: '请输入iframe地址'
        }
      },
      value: ''
    }
  ],
  config: {
    ...workbenchBaseDefault,
    componentName: 'Chatbot',
    iframeUrl: '',
    width: WORKBENCH_WIDTH_VALUES[WORKBENCH_WIDTH_OPTIONS.FULL]
  }
};

export default XChatbot;
