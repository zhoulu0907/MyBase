import { memo, useState } from 'react';
import { STATUS_OPTIONS, STATUS_VALUES } from '../../../constants';
import { type XWebViewConfig } from './schema';

const XWebView = memo((props: XWebViewConfig) => {
  const { status, title, webViewUrl } = props;

  const [iframeError, setIframeError] = useState(false);

  // 处理 URL，确保有协议前缀
  const getValidUrl = (url: string) => {
    if (!url) return '';
    if (url.startsWith('http://') || url.startsWith('https://')) {
      return url;
    }
    return `https://${url}`;
  };

  const validUrl = getValidUrl(webViewUrl);

  // 如果 iframe 加载失败，显示备用内容
  if (iframeError) {
    return (
      <div
        style={{
          width: '100%',
          height: '200px',
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
          border: '1px solid #e0e0e0',
          borderRadius: '4px',
          backgroundColor: '#f5f5f5'
        }}
      >
        <div style={{ textAlign: 'center' }}>
          <p style={{ margin: '0 0 10px 0', color: '#666' }}>无法加载网页内容</p>
          <a
            href={validUrl}
            target="_blank"
            rel="noopener noreferrer"
            style={{
              color: '#1890ff',
              textDecoration: 'none'
            }}
          >
            在新窗口中打开
          </a>
        </div>
      </div>
    );
  }

  return (
    <div
      style={{
        opacity: status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN] ? 0.5 : 1
      }}
    >
      {title}
      <iframe
        src={validUrl}
        style={{
          width: '100%',
          height: '400px', // 设置固定高度，避免 auto 导致的问题
          border: '1px solid #e0e0e0',
          borderRadius: '4px'
        }}
        title="WebView"
        sandbox="allow-scripts allow-same-origin allow-popups allow-forms"
        onError={() => setIframeError(true)}
        onLoad={() => setIframeError(false)}
      />
    </div>
  );
});

export default XWebView;
