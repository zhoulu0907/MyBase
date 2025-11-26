import { Button, Input, Message, Select } from '@arco-design/web-react';
import { IconCopy, IconPlus } from '@arco-design/web-react/icon';
import { getRuntimeURL, TokenManager } from '@onebase/common';
import { statusOptions } from '../../constants';
import styles from './index.module.less';

interface topHeaderProps {
  title: string;
  type?: string;
  onAdd?: () => void;
  setSearchInputValue: (value: string) => void;
  isBusiness?: boolean;
  onchange?: (statusValue: number | null) => void;
}

export const TopHeader: React.FC<topHeaderProps> = ({ type, title, onAdd, onchange, isBusiness = true, setSearchInputValue }) => {
  const tenantId = TokenManager.getTenantInfo()?.tenantId || '';
  const redirectURL = `${getRuntimeURL()}/#/onebase/runtime/?tenantId=${tenantId}`;
  const href = `${getRuntimeURL()}/#/login?redirectURL=${redirectURL}`;
  
  const navigateToRunTime = (text: string) => {
    window.open(text);
  };

  const handleChange = (statusValue: number) => {
    onchange && onchange(statusValue === 2 ? null : statusValue);
  }  
  const fallbackCopyToClipboard = (text: string) => {
    const textArea = document.createElement('textarea');
    textArea.value = text;
    textArea.style.position = 'fixed';
    textArea.style.opacity = '0';
    textArea.style.top = '0';
    textArea.style.left = '0';
    document.body.appendChild(textArea);
    textArea.select();
    try {
      document.execCommand('copy');
      Message.success('复制成功!');
    } catch (err) {
      console.error('execCommand 失败:', err);
      Message.error('复制失败');
    }
    document.body.removeChild(textArea);
  };


const copyToClipboard = async (text: string) => {
  try {
    // 首先尝试使用现代 Clipboard API
    if (navigator.clipboard && window.isSecureContext) {
      await navigator.clipboard.writeText(text);
      Message.success('复制成功!');
    } else {
      // 降级到传统方法
      fallbackCopyToClipboard(text);
    }
  } catch (error) {
    console.error('复制失败:', error);
    Message.error('复制失败');
  }
};

  return (
    <div className={styles.topHeader}>
      {/*顶部左侧 新建企业*/}
      <div className={styles.createBusiness}>
        {type !== 'authorized-application' && (
          <Button type="primary" icon={<IconPlus />} onClick={onAdd}>
            {title}
          </Button>
        )}
        {isBusiness && (
          <div className={styles.linkContent}>
            <span>企业用户登录地址:</span>
            <div className={styles.linkText} onClick={()=>navigateToRunTime(href)}>www.onebase.com/enterprise</div>
            <IconCopy onClick={()=>copyToClipboard(href)} style={{ fontSize: 16 }} />
          </div>
        )}
      </div>
      {/* 顶部右侧 搜索*/}
      <div className={styles.searchContent}>
        {isBusiness && <Select bordered={false} options={statusOptions} onChange={handleChange}/>}
        <Input.Search
          allowClear
          placeholder={`输入${title}名称`}
          className={styles.searchInput}
          onChange={(value) => setSearchInputValue(value)}
        />
      </div>
    </div>
  );
};
