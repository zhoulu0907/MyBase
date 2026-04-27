import styles from './index.module.less';
import LinkWithCopy from './LinkWithCopy';
import { useHideInLingji } from '@/pages/Setting/hooks/useHideInLingji';

interface ExternalLoginLinksProps {
  hrefPC: string;
  hrefMobile: string;
  onNavigate: (url: string) => void;
}

function ExternalLoginLinks({ hrefPC, hrefMobile, onNavigate }: ExternalLoginLinksProps) {
  const { hideExternalLoginLinks } = useHideInLingji();

  return (
    <div className={styles.linkContent}>
      <span className={styles.appLabel}>本应用</span>
      <span className={styles.linkLabel}>外部用户登录/注册地址 </span>

      {!hideExternalLoginLinks && (
        <>
          <span className={styles.platformLabel}>Web端: </span>
          <LinkWithCopy url={hrefPC} onNavigate={onNavigate} className={styles.webLink} />

          <span className={styles.platformLabel}>移动端: </span>
          <LinkWithCopy url={hrefMobile} onNavigate={onNavigate} className={styles.mobileLink} />
        </>
      )}
    </div>
  );
}

export default ExternalLoginLinks;
