import { Modal } from '@arco-design/web-react';
import { useState, useEffect, useMemo } from 'react';
import ChatboxIcon from '@/assets/images/cp/chatbox.svg';
import './index.css';

export interface XChatbotProps {
  config?: {
    iframeUrl?: string;
    agentId?: string;
    agentName?: string;
    [key: string]: any;
  };
  runtime?: boolean;
  iframeUrl?: string;
}

const XChatbot: React.FC<XChatbotProps> = ({ config, runtime = false, iframeUrl: propIframeUrl }) => {
  const [visible, setVisible] = useState(false);
  const [iframeHeight, setIframeHeight] = useState(200);

  const { agentId, iframeUrl: configIframeUrl } = config || {};

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

  const DEFAULT_URL = 'http://10.11.112.38:9500/bote/#/driver/bot?tenantId=0&botId=1338078781184737280&modeType=single&token=4f0fc76675484ad8a2ab29941debf7f4&pattern=S';

  const displayUrl = useMemo(() => {
    if (propIframeUrl) {
      return propIframeUrl;
    }
    if (configIframeUrl) {
      if (agentId) {
        const baseUrl = configIframeUrl.includes('?') ? configIframeUrl : `${configIframeUrl}`;
        return `${baseUrl}&botId=${agentId}`;
      }
      return configIframeUrl;
    }
    if (agentId) {
      return `${DEFAULT_URL}&botId=${agentId}`;
    }
    return DEFAULT_URL;
  }, [propIframeUrl, configIframeUrl, agentId]);

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
          alt="智能体对话"
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
        style={{ width: 800, borderRadius: '10px', overflow: 'hidden' }}
        className="chatbot-modal"
      >
        <iframe
          src={displayUrl}
          style={{ width: '100%', height: iframeHeight, border: 'none' }}
          title="Chatbot"
        />
      </Modal>
    </>
  );
};

export default XChatbot;
