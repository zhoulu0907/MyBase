import { useState, useEffect } from 'react';
import { Form, Select, Spin } from '@arco-design/web-react';
import { registerConfigRenderer } from '../../registry';
import { WORKBENCH_CONFIG_TYPES, type IWbAgentSelectorConfigType } from '@onebase/ui-kit';
import axios from 'axios';
import styles from '../../index.module.less';

interface Props {
  handlePropsChange: (key: string, value: string) => void;
  handleMultiPropsChange: (updates: { key: string; value: any }[]) => void;
  item: IWbAgentSelectorConfigType;
  configs: Record<string, unknown>;
}

interface AgentItem {
  botId: string;
  appName: string;
  tenantId: string;
}

const AgentSelectorConfig = ({ handlePropsChange, handleMultiPropsChange, item, configs }: Props) => {
  const [loading, setLoading] = useState(false);
  const [agentList, setAgentList] = useState<AgentItem[]>([]);

  const currentAgentId = (configs[item.key] as string) || '';
  const iframeUrl = configs.iframeUrl as string | undefined;
  const isDisabled = !!iframeUrl && iframeUrl.trim() !== '';

  useEffect(() => {
    if (!isDisabled) {
      fetchAgentList();
    }
  }, [isDisabled]);

  const fetchAgentList = async () => {
    setLoading(true);
    try {
      const response = await axios.post(
        'http://bote.sit.artifex-cmcc.com.cn/bote/api/bote/manager/bot/queryPublishedAppPage',
        {
          extTenantId: '153935442021842944',
          spaceId: '1348207178626895872',
          pageNum: 1,
          pageSize: 10
        },
        {
          headers: {
            Authorization: 'Bearer 44fb86e0f64d436fac8eb632333af4f6',
            'Content-Type': 'application/json'
          }
        }
      );

      if (response.data?.resultCode === '0' && response.data?.resultObject?.list) {
        const list = response.data.resultObject.list.map((item: any) => ({
          botId: item.botId,
          appName: item.appName,
          tenantId: item.tenantId
        }));
        setAgentList(list);
      }
    } catch (error) {
      console.error('获取智能体列表失败:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleChange = (value: string) => {
    handlePropsChange(item.key, value);
    const selectedAgent = agentList.find(agent => agent.botId === value);
    if (selectedAgent) {
      handleMultiPropsChange([
        { key: 'agentId', value },
        { key: 'agentName', value: selectedAgent.appName },
        { key: 'agentTenantId', value: selectedAgent.tenantId }
      ]);
    }
  };

  const handleClear = () => {
    handlePropsChange(item.key, '');
    handleMultiPropsChange([
      { key: 'agentId', value: '' },
      { key: 'agentName', value: '' },
      { key: 'agentTenantId', value: '' }
    ]);
  };

  return (
    <Form.Item className={styles.formItem} label={item.name}>
      <Spin loading={loading} style={{ width: '100%' }}>
        <Select
          value={currentAgentId}
          onChange={handleChange}
          onClear={handleClear}
          placeholder={isDisabled ? '已配置URL地址' : '请选择智能体'}
          allowClear={!isDisabled}
          disabled={isDisabled}
        >
          {agentList.map((agent) => (
            <Select.Option key={agent.botId} value={agent.botId}>
              {agent.appName}
            </Select.Option>
          ))}
        </Select>
      </Spin>
    </Form.Item>
  );
};

export default AgentSelectorConfig;

registerConfigRenderer(WORKBENCH_CONFIG_TYPES.WB_AGENT_SELECTOR, ({ handlePropsChange, handleMultiPropsChange, item, configs }) => (
  <AgentSelectorConfig handlePropsChange={handlePropsChange} handleMultiPropsChange={handleMultiPropsChange} item={item} configs={configs} />
));
