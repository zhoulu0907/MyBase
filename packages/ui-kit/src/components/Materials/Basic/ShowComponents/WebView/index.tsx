import { memo, useState, useEffect } from 'react';
import { useSignals } from '@preact/signals-react/runtime';
import { pagesRuntimeSignal } from '@onebase/common';
import { menuSignal } from '@onebase/app';
import { STATUS_OPTIONS, STATUS_VALUES } from '../../../constants';
import { type XWebViewConfig } from './schema';
import './index.css';

const XWebView = memo((props: XWebViewConfig & { runtime?: boolean; detailMode?: boolean }) => {
  useSignals();

  const { status, title, webViewUrl, runtime = true, params, tableName: configTableName } = props;

  const [iframeError, setIframeError] = useState(false);
  const [validUrl, setValidUrl] = useState('');
  const [iframeHeight, setIframeHeight] = useState('900px');
  const { rowData, rowDataId } = pagesRuntimeSignal;
  const { curMenu } = menuSignal;

  const getValidUrl = (url: string) => {
    if (!url) return '';
    if (url.startsWith('http://') || url.startsWith('https://')) {
      return url;
    }
    return `https://${url}`;
  };

  const buildUrlWithParams = (baseUrl: string) => {
    const tableRowData = rowData.value;
    const searchParams = new URLSearchParams();

    if (runtime) {
      const currentRecordId = rowDataId.value;
      if (currentRecordId) {
        searchParams.append('recordId', currentRecordId);
      }
      if (configTableName) {
        searchParams.append('tableName', configTableName);
      }
      const currentMenuId = curMenu.value?.id;
      if (currentMenuId) {
        searchParams.append('menuId', currentMenuId);
      }
    }

    if (params && params.length > 0) {
      params.forEach(param => {
        const value = tableRowData[param.key];
        if (value !== undefined && value !== null && value !== '') {
          searchParams.append(param.key, String(value));
        }
      });
    }

    const queryString = searchParams.toString();
    const validBaseUrl = getValidUrl(baseUrl);

    return queryString ? `${validBaseUrl}?${queryString}` : validBaseUrl;
  };

  useEffect(() => {
    const url = buildUrlWithParams(webViewUrl);
    setValidUrl(url);
  }, [webViewUrl, params, runtime, rowData.value, rowDataId.value, curMenu.value?.id, configTableName]);

  useEffect(() => {
    const handleMessage = (event: MessageEvent) => {
      if (event.data && event.data.type === 'iframeHeight') {
        setIframeHeight(`${event.data.height + 10}px`);
      }
    };

    const handleResize = () => {
      const iframe = document.querySelector('iframe[title="WebView"]');
      if (iframe && iframe.contentWindow) {
        iframe.contentWindow.postMessage({ type: 'recalculateHeight' }, '*');
      }
    };

    window.addEventListener('message', handleMessage);
    window.addEventListener('resize', handleResize);

    return () => {
      window.removeEventListener('message', handleMessage);
      window.removeEventListener('resize', handleResize);
    };
  }, []);

  // 如果 iframe 加载失败，显示备用内容
  if (iframeError) {
    return (
      <div
        style={{
          width: '100%',
          height: '200px',
          display: runtime && status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN] ? 'none' : 'flex',
          alignItems: 'center',
          justifyContent: 'center',
          border: '1px solid #e0e0e0',
          borderRadius: '8px',
          boxSizing: 'border-box',
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
        opacity: status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN] ? 0.4 : 1,
        display: runtime && status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN] ? 'none' : 'flex',
        flexDirection: 'column'
      }}
    >
      <div className='iframeTitle'>{title}</div>
      <iframe
        src={validUrl}
        style={{
          width: '100%',
          height: iframeHeight,
          border: 'none',
          boxSizing: 'border-box'
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
