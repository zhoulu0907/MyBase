import { useCallback } from 'react';
import { appendToMessage, updateMessage } from './messagesStore';
import TokenManager from '@onebase/common/src/utils/token';

interface ChatConfig {
  provider: string;
  baseUrl: string;
  model: string;
  apiKey: string;
  systemPrompt?: string;
}

const PROVIDER_BASE_URLS: Record<string, string> = {
  OpenAI: 'https://api.openai.com',
  DeepSeek: 'https://api.deepseek.com',
  Anthropic: 'https://api.anthropic.com',
  Custom: ''
};

export function useChatbot() {
  const sendMessage = useCallback(async (content: string, config: ChatConfig, assistantMsgId: string) => {
    const { provider, baseUrl, model, apiKey, systemPrompt } = config;

    const resolvedBaseUrl = baseUrl || PROVIDER_BASE_URLS[provider] || '';
    if (!resolvedBaseUrl) {
      updateMessage(assistantMsgId, '请配置有效的 API 地址');
      return;
    }

    const messages = [
      ...(systemPrompt ? [{ role: 'system', content: systemPrompt }] : []),
      { role: 'user', content }
    ];

    try {
      const tokenInfo = TokenManager.getTokenInfo();

      if (!tokenInfo?.accessToken) {
        throw new Error('未登录或登录已过期，请刷新页面重新登录');
      }

      const response = await fetch('/admin-api/ai/chat/stream', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${tokenInfo.accessToken}`,
          'X-Tenant-Id': tokenInfo.tenantId || ''
        },
        body: JSON.stringify({
          provider,
          baseUrl: resolvedBaseUrl,
          model,
          apiKey,
          messages,
          stream: true
        })
      });

      if (!response.ok) {
        if (response.status === 401) {
          throw new Error('登录已过期，请刷新页面重新登录后再试');
        }
        const errorData = await response.json().catch(() => ({}));
        throw new Error(errorData.msg || `请求失败 (${response.status})`);
      }

      const reader = response.body?.getReader();
      if (!reader) {
        throw new Error('No response body');
      }

      const decoder = new TextDecoder();
      let buffer = '';

      while (true) {
        const { done, value } = await reader.read();
        if (done) break;

        buffer += decoder.decode(value, { stream: true });
        const lines = buffer.split('\n');
        buffer = lines.pop() || '';

        for (const line of lines) {
          if (line.startsWith('data: ')) {
            const data = line.slice(6).trim();
            if (data === '[DONE]' || data === '{"done": true}' || data === '{"done":true}') {
              return;
            }
            try {
              const parsed = JSON.parse(data);
              if (parsed.delta) {
                appendToMessage(assistantMsgId, parsed.delta);
              }
              if (parsed.done) {
                return;
              }
              if (parsed.error) {
                throw new Error(parsed.error);
              }
            } catch (e: any) {
              if (e.message === 'JSON.parse') {
                // ignore parse error for non-JSON data
              } else {
                throw e;
              }
            }
          }
        }
      }
    } catch (error: any) {
      console.error('Chat error:', error);
      updateMessage(assistantMsgId, `抱歉，发生了错误：${error.message}`);
      throw error;
    }
  }, []);

  return { sendMessage };
}