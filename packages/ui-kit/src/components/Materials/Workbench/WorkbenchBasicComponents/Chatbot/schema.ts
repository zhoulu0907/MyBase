import {
  workbenchBaseConfig,
  workbenchBaseDefault,
  workbenchWidthConfig,
  type ICommonBaseWorkbenchType,
  type TWorkbenchWidthSelectKeyType,
  type TSelectDefaultType,
  type ITextConfigType,
  type IWidthConfigType
} from '../../config/workbenchShared';
import { WORKBENCH_WIDTH_OPTIONS, WORKBENCH_WIDTH_VALUES, WORKBENCH_CONFIG_TYPES } from '../../core/constants';
import type { IWbAgentSelectorConfigType } from '../../core/types';

export interface XChatbotSchema {
  editData: Array<ITextConfigType | IWidthConfigType<TWorkbenchWidthSelectKeyType> | IWbAgentSelectorConfigType>;
  config: XChatbotConfig;
}

export interface XChatbotConfig extends ICommonBaseWorkbenchType {
  componentName: string;
  iframeUrl: string;
  agentId?: string;
  agentName?: string;
  width: TSelectDefaultType<TWorkbenchWidthSelectKeyType>;
  floatingConfig?: {
    right: number;
    bottom: number;
    width: number;
    height: number;
  };
}

const XChatbot: XChatbotSchema = {
  editData: [
    ...workbenchBaseConfig,
    {
      key: 'agentId',
      name: '智能体',
      type: WORKBENCH_CONFIG_TYPES.WB_AGENT_SELECTOR
    },
    {
      key: 'iframeUrl',
      name: 'URL地址',
      type: WORKBENCH_CONFIG_TYPES.TEXT_INPUT
    }
  ],
  config: {
    ...workbenchBaseDefault,
    componentName: 'Chatbot',
    iframeUrl: 'http://10.11.112.38:9500/bote/#/driver/bot?tenantId=0&botId=1338078781184737280&modeType=single&token=4f0fc76675484ad8a2ab29941debf7f4&pattern=S',
    width: WORKBENCH_WIDTH_VALUES[WORKBENCH_WIDTH_OPTIONS.FULL],
    floatingConfig: {
      right: 24,
      bottom: 24,
      width: 56,
      height: 56
    }
  }
};

export default XChatbot;
