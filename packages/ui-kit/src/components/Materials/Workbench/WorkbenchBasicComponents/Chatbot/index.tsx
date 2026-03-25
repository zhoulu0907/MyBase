import { Modal } from '@arco-design/web-react';
import { useState, useEffect, useMemo } from 'react';
import ChatboxIcon from '@/assets/images/cp/chatbox.svg';
import './index.css';

export interface XChatbotProps {
  agentId?: string;
  agentName?: string;
  agentTenantId?: string;
  runtime?: boolean;
}

const XChatbot: React.FC<XChatbotProps> = ({ agentId, agentName, agentTenantId, runtime = false }) => {
  const [visible, setVisible] = useState(false);
  const [iframeHeight, setIframeHeight] = useState(200);
  const [code, setCode] = useState<string>('');

  useEffect(() => {
    const calculateHeight = () => {
      const bodyHeight = document.body.offsetHeight;
      setIframeHeight(bodyHeight - 200);
    };

    calculateHeight();
    window.addEventListener('resize', calculateHeight);
    return () => window.removeEventListener('resize', calculateHeight);
  }, []);

  useEffect(() => {
    if (runtime) {
      fetchCode();
    }
  }, [runtime]);

  const fetchCode = async () => {
    try {
      const { oauthAuthorize } = await import('@onebase/platform-center');
      const authorizeRes = await oauthAuthorize({
        client_id: 'aitool',
        scope: '',
        redirect_uri: 'http://bote.sit.artifex-cmcc.com.cn/bote/manager/',
        response_type: 'code',
        auto_approve: true
      });
      if (authorizeRes?.code) {
        setCode(authorizeRes.code);
      }
    } catch (error) {
      console.error('获取授权码失败:', error);
    }
  };

  const handleClick = () => {
    if (runtime) {
      setVisible(true);
    }
  };

  const DEFAULT_URL_TEMPLATE = 'http://bote.sit.artifex-cmcc.com.cn/bote/#/driver/bot?tenantId={{tenantId}}&botId={{botId}}&modeType=single&systemCode=ONEBASE-Runtime&code={{code}}';

  const displayUrl = useMemo(() => {
    if (!agentId) {
      return '';
    }
    const tenantId = agentTenantId || '';
    const url = DEFAULT_URL_TEMPLATE
      .replace('{{tenantId}}', tenantId)
      .replace('{{botId}}', agentId)
      .replace('{{code}}', code || '');
    return url;
  }, [agentId, agentTenantId, code]);

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
