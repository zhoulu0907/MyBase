import { Collapse, Select, Form, Input } from '@arco-design/web-react';
import { useState, useMemo } from 'react';
import { WorkbenchAttributes, UseWorkbenchAttributeContext, PanelContentStyle } from '../../components/CommonWorkbenchAttributes';
import { findItem } from '../../../../utils/edit-data';
import styles from '../../index.module.less';

const CollapseItem = Collapse.Item;

const SECTION_KEYS = {
  LLM: 'llm',
  TITLE: 'title',
  STYLE: 'style'
} as const;

const PROVIDER_OPTIONS = [
  { key: 'OpenAI', value: 'OpenAI', text: 'OpenAI' },
  { key: 'DeepSeek', value: 'DeepSeek', text: 'DeepSeek' },
  { key: 'Anthropic', value: 'Anthropic', text: 'Anthropic' },
  { key: 'Custom', value: 'Custom', text: 'Custom' }
];

const ChatbotAgentConfig = () => {
  const { editData, configs, handlePropsChange, renderEditItem } = UseWorkbenchAttributeContext();
  const [activeKeys, setActiveKeys] = useState<string[]>([
    SECTION_KEYS.LLM,
    SECTION_KEYS.TITLE
  ]);

  const configItems = useMemo(() => {
    return {
      label: findItem(editData, 'label'),
      provider: findItem(editData, 'provider'),
      baseUrl: findItem(editData, 'baseUrl'),
      model: findItem(editData, 'model'),
      apiKey: findItem(editData, 'apiKey'),
      systemPrompt: findItem(editData, 'systemPrompt'),
      theme: findItem(editData, 'theme')
    };
  }, [editData]);

  const handleProviderChange = (value: string) => {
    handlePropsChange('provider', value);
    const defaultUrls: Record<string, string> = {
      OpenAI: 'https://api.openai.com',
      DeepSeek: 'https://api.deepseek.com',
      Anthropic: 'https://api.anthropic.com',
      Custom: ''
    };
    if (defaultUrls[value] !== undefined) {
      handlePropsChange('baseUrl', defaultUrls[value]);
    }
  };

  return (
    <WorkbenchAttributes
      renderPanels={({ cpID }) => (
        <Collapse
          activeKey={activeKeys}
          onChange={(_key, keys) => setActiveKeys(keys)}
          accordion={false}
          bordered={false}
          expandIconPosition="right"
          className={styles.collapseConfigs}
        >
          <CollapseItem header="LLM 配置" name={SECTION_KEYS.LLM} contentStyle={PanelContentStyle}>
            {configItems.provider && (
              <Form.Item label={configItems.provider.name}>
                <Select
                  value={configs.provider}
                  onChange={handleProviderChange}
                  options={PROVIDER_OPTIONS}
                />
              </Form.Item>
            )}
            {configItems.baseUrl && (
              <div>{renderEditItem(configItems.baseUrl)}</div>
            )}
            {configItems.model && (
              <div>{renderEditItem(configItems.model)}</div>
            )}
            {configItems.apiKey && (
              <Form.Item label={configItems.apiKey.name}>
                <Input.Password
                  value={configs.apiKey}
                  onChange={(value) => handlePropsChange('apiKey', value)}
                  placeholder="请输入 API Key"
                />
              </Form.Item>
            )}
            {configItems.systemPrompt && (
              <div>{renderEditItem(configItems.systemPrompt)}</div>
            )}
          </CollapseItem>
          <CollapseItem header="标题配置" name={SECTION_KEYS.TITLE} contentStyle={PanelContentStyle}>
            {configItems.label && <div>{renderEditItem(configItems.label)}</div>}
          </CollapseItem>
        </Collapse>
      )}
    />
  );
};

export default ChatbotAgentConfig;