import { Modal } from '@arco-design/web-react';
import { useState, useEffect } from 'react';
import ChatboxIcon from '@/assets/images/cp/chatbox.svg';
import './index.css';

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
  const [iframeHeight, setIframeHeight] = useState(200);

  useEffect(() => {
    const calculateHeight = () => {
      const bodyHeight = document.body.offsetHeight;
      setIframeHeight(bodyHeight - 200);
    };

    calculateHeight();
    window.addEventListener('resize', calculateHeight);
    return () => window.removeEventListener('resize', calculateHeight);
  }, []);

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
          width: 60,
          height: 60,
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
          src={ChatboxIcon}
          alt="聊天助手"
          style={{
            width: 60,
            height: 60,
            objectFit: 'contain'
          }}
        />
      </div>
      <Modal
        title={null}
        visible={visible}
        onOk={() => setVisible(false)}
        onCancel={() => setVisible(false)}
        autoFocus={false}
        focusLock={true}
        footer={null}
        style={{ width: 800 }}
        className="chatbot-modal"
      >
        {iframeUrl ? (
          <iframe
            src={iframeUrl}
            style={{ width: '100%', height: iframeHeight, border: 'none' }}
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
