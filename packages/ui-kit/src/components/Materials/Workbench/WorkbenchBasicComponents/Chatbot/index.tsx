import { Modal } from '@arco-design/web-react';
import { useState } from 'react';

const CHATBOX_IMAGE = '/chatbox.png';

export interface XChatbotProps {
  config?: {
    iframeUrl?: string;
    [key: string]: any;
  };
  runtime?: boolean;
  iframeUrl?: string;
}

const XChatbot: React.FC<XChatbotProps> = ({ config, runtime = false, iframeUrl: propIframeUrl }) => {
  const [visible, setVisible] = useState(false);

  const handleClick = () => {
    if (runtime) {
      setVisible(true);
    }
  };

  const iframeUrl = propIframeUrl || config?.iframeUrl;

  return (
    <>
      <div
        style={{
          width: 80,
          height: 80,
          borderRadius: '50%',
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
          cursor: runtime ? 'pointer' : 'default',
          overflow: 'hidden'
        }}
        onClick={handleClick}
      >
        <img
          src={CHATBOX_IMAGE}
          alt="聊天助手"
          style={{
            width: 80,
            height: 80,
            objectFit: 'contain'
          }}
        />
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
        {iframeUrl ? (
          <iframe
            src={iframeUrl}
            style={{ width: '100%', height: 500, border: 'none' }}
            title="Chatbot"
          />
        ) : (
          <div style={{ padding: 20, textAlign: 'center', color: '#999' }}>
            请在配置面板中设置 URL 地址
          </div>
        )}
      </Modal>
    </>
  );
};

export default XChatbot;
