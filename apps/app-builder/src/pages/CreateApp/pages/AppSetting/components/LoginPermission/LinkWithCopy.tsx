import { IconCopy } from '@arco-design/web-react/icon';
import { copyToClipboard } from '@onebase/common';
import styles from './index.module.less';

interface LinkWithCopyProps {
  url: string;
  onNavigate?: (url: string) => void;
  showCopy?: boolean;
  className?: string;
}

function LinkWithCopy({ url, onNavigate, showCopy = true, className }: LinkWithCopyProps) {
  const handleClick = () => {
    if (onNavigate) {
      onNavigate(url);
    }
  };

  const handleCopy = () => {
    copyToClipboard(url);
  };

  return (
    <span className={className}>
      <div className={styles.linkText} onClick={handleClick}>
        {url}
      </div>
      {showCopy && <IconCopy onClick={handleCopy} className={styles.copyIcon} />}
    </span>
  );
}

export default LinkWithCopy;
