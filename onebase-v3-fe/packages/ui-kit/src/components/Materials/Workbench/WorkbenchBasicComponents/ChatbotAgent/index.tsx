import { useEffect, useRef } from 'react';
import { Input, Button } from '@arco-design/web-react';
import { useSignals } from '@preact/signals-react/runtime';
import ReactMarkdown from 'react-markdown';
import {
  messagesSignal,
  inputValueSignal,
  isLoadingSignal,
  setInputValue,
  addMessage,
  appendToMessage
} from './messagesStore';
import { useChatbot } from './useChatbot';
import styles from './index.module.css';

export interface XChatbotAgentProps {
  provider: 'OpenAI' | 'DeepSeek' | 'Anthropic' | 'Custom';
  baseUrl: string;
  model: string;
  apiKey: string;
  systemPrompt?: string;
  runtime?: boolean;
}

const XChatbotAgent: React.FC<XChatbotAgentProps> = ({
  provider,
  baseUrl,
  model,
  apiKey,
  systemPrompt,
  runtime = false
}) => {
  useSignals();

  const { sendMessage } = useChatbot();
  const messagesEndRef = useRef<HTMLDivElement>(null);

  const messages = messagesSignal.value;
  const inputValue = inputValueSignal.value;
  const isLoading = isLoadingSignal.value;

  useEffect(() => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  }, [messages]);

  const handleSend = async () => {
    if (!inputValue.trim() || isLoading) return;
    if (!apiKey.trim()) {
      addMessage({ role: 'assistant', content: '请先配置 API Key' });
      return;
    }
    const content = inputValue.trim();
    setInputValue('');

    const userMsgId = addMessage({ role: 'user', content });
    isLoadingSignal.value = true;

    try {
      const assistantMsgId = addMessage({ role: 'assistant', content: '' });
      await sendMessage(content, { provider, baseUrl, model, apiKey, systemPrompt }, assistantMsgId);
    } catch (error) {
      console.error('发送消息失败:', error);
      addMessage({ role: 'assistant', content: '抱歉，发送消息失败，请重试。' });
    } finally {
      isLoadingSignal.value = false;
    }
  };

  const handleKeyDown = (e: React.KeyboardEvent<HTMLInputElement>) => {
    if (e.key === 'Enter' && !e.shiftKey) {
      e.preventDefault();
      handleSend();
    }
  };

  const formatTime = (timestamp: number) => {
    const date = new Date(timestamp);
    return date.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' });
  };

  return (
    <div className={styles['chatbot-container']}>
      <div className={styles['chatbot-messages']}>
        {messages.length === 0 && !isLoading && (
          <div className={styles['chatbot-empty']}>
            <span>暂无消息</span>
            <span>开始和 AI 对话吧</span>
          </div>
        )}
        {messages.map((msg) => (
          <div key={msg.id} className={`${styles['chatbot-message']} ${styles[msg.role]}`}>
            <div className={styles['chatbot-message-bubble']}>
              {msg.role === 'assistant' ? (
                <div className={styles['chatbot-markdown']}>
                  <ReactMarkdown>{msg.content}</ReactMarkdown>
                </div>
              ) : (
                msg.content
              )}
            </div>
            <div className={styles['chatbot-message-time']}>{formatTime(msg.timestamp)}</div>
          </div>
        ))}
        {isLoading && messages[messages.length - 1]?.role === 'user' && (
          <div className={`${styles['chatbot-message']} ${styles.assistant}`}>
            <div className={styles['chatbot-loading']}>
              <div className={styles['chatbot-loading-dot']}></div>
              <div className={styles['chatbot-loading-dot']}></div>
              <div className={styles['chatbot-loading-dot']}></div>
            </div>
          </div>
        )}
        <div ref={messagesEndRef} />
      </div>
      <div className={styles['chatbot-input-wrapper']}>
        <Input
          className={styles['chatbot-input']}
          placeholder="输入消息..."
          value={inputValue}
          onChange={(value) => setInputValue(value)}
          onKeyDown={handleKeyDown}
          disabled={isLoading}
        />
        <Button
          className={styles['chatbot-send-btn']}
          disabled={!inputValue.trim() || isLoading}
          onClick={handleSend}
          type="primary"
        >
          发送
        </Button>
      </div>
    </div>
  );
};

export default XChatbotAgent;