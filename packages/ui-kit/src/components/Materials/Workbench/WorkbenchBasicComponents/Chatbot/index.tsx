import { Modal } from '@arco-design/web-react';
import { useState } from 'react';

export interface XChatbotProps {
  config?: {
    iframeUrl?: string;
  };
  runtime?: boolean;
}

const XChatbot: React.FC<XChatbotProps> = ({ config, runtime = false }) => {
  const [visible, setVisible] = useState(false);

  const handleClick = () => {
    if (runtime) {
      setVisible(true);
    }
  };

  return (
    <>
      <div
        style={{
          width: 80,
          height: 80,
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
          backgroundColor: '#165DFF',
          borderRadius: 8,
          cursor: runtime ? 'pointer' : 'default'
        }}
        onClick={handleClick}
      >
        <svg
          width="32"
          height="32"
          viewBox="0 0 24 24"
          fill="none"
          xmlns="http://www.w3.org/2000/svg"
        >
          <path
            d="M12 2C6.48 2 2 6.48 2 12C2 17.52 6.48 22 12 22C17.52 22 22 17.52 22 12C22 6.48 17.52 2 12 2ZM13 17H11V11H13V17ZM13 9H11V7H13V9Z"
            fill="white"
          />
        </svg>
      </div>
      <Modal
        title="聊天助手"
        visible={visible}
        onOk={() => setVisible(false)}
        onCancel={() => setVisible(false)}
        autoFocus={false}
        focusLock={true}
        style={{ width: 800 }}
      >
        {config?.iframeUrl ? (
          <iframe
            src={config.iframeUrl}
            style={{ width: '100%', height: 500, border: 'none' }}
            title="Chatbot"
          />
        ) : (
          <div style={{ padding: 20, textAlign: 'center', color: '#999' }}>
            请在配置面板中设置 iframe 地址
          </div>
        )}
      </Modal>
    </>
  );
};

export default XChatbot;
