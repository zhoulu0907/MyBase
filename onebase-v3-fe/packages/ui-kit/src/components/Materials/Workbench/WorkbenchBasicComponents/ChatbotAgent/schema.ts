import {
  workbenchBaseConfig,
  workbenchBaseDefault,
  workbenchWidthConfig,
  type ICommonBaseWorkbenchType,
  type TWorkbenchWidthSelectKeyType,
  type TSelectDefaultType,
  type ITextConfigType,
  type IWidthConfigType,
  type ILabelConfigType,
  type IBooleanConfigType,
  type ISelectConfigType,
  type IPasswordConfigType
} from '../../config/workbenchShared';
import { WORKBENCH_WIDTH_OPTIONS, WORKBENCH_WIDTH_VALUES, WORKBENCH_CONFIG_TYPES } from '../../core/constants';

export interface XChatbotAgentSchema {
  editData: TXChatbotAgentEditData;
  config: XChatbotAgentConfig;
}

export type TXChatbotAgentEditData = Array<
  | ILabelConfigType
  | ITextConfigType
  | IWidthConfigType<TWorkbenchWidthSelectKeyType>
  | IBooleanConfigType
  | ISelectConfigType<string>
  | IPasswordConfigType
>;

export interface XChatbotAgentConfig extends ICommonBaseWorkbenchType {
  componentName: string;
  provider: 'OpenAI' | 'DeepSeek' | 'Anthropic' | 'Custom';
  baseUrl: string;
  model: string;
  apiKey: string;
  systemPrompt?: string;
  width: TSelectDefaultType<TWorkbenchWidthSelectKeyType>;
}

const XChatbotAgent: XChatbotAgentSchema = {
  editData: [
    ...workbenchBaseConfig,
    {
      key: 'provider',
      name: 'AI提供商',
      type: WORKBENCH_CONFIG_TYPES.SELECT_INPUT,
      range: [
        { key: 'OpenAI', text: 'OpenAI', value: 'OpenAI' },
        { key: 'DeepSeek', text: 'DeepSeek', value: 'DeepSeek' },
        { key: 'Anthropic', text: 'Anthropic', value: 'Anthropic' },
        { key: 'Custom', text: 'Custom', value: 'Custom' }
      ]
    },
    {
      key: 'baseUrl',
      name: 'API地址',
      type: WORKBENCH_CONFIG_TYPES.TEXT_INPUT
    },
    {
      key: 'model',
      name: '模型名称',
      type: WORKBENCH_CONFIG_TYPES.TEXT_INPUT
    },
    {
      key: 'apiKey',
      name: 'API Key',
      type: WORKBENCH_CONFIG_TYPES.PASSWORD_INPUT
    },
    {
      key: 'systemPrompt',
      name: '系统提示词',
      type: WORKBENCH_CONFIG_TYPES.TEXT_INPUT
    },
    workbenchWidthConfig
  ],
  config: {
    ...workbenchBaseDefault,
    componentName: 'ChatbotAgent',
    provider: 'OpenAI',
    baseUrl: 'https://api.openai.com',
    model: 'gpt-4o',
    apiKey: '',
    width: WORKBENCH_WIDTH_VALUES[WORKBENCH_WIDTH_OPTIONS.FULL]
  }
};

export default XChatbotAgent;