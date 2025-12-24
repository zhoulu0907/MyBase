import { Image } from '@arco-design/web-react';
import styles from './index.module.less';
import LogoSVG from '@/assets/images/ob_logo.svg';
import type { CorpDetailResponse } from '@onebase/platform-center';
import { appIconMap } from '@onebase/ui-kit';
import { DynamicIcon } from '@onebase/common';

type TenantInfo = CorpDetailResponse & {
  iconColor?: string;
  iconName?: string;
  appCode?: string;
};

interface ITenantLogoProps {
  tenantInfo: TenantInfo | null;
}

const TenantLogo: React.FC<ITenantLogoProps> = ({ tenantInfo }) => {
  return (
    <>
      {!tenantInfo?.appCode && (
        <>
          <Image src={LogoSVG} alt="logo" height={28} />
          <div className={styles.line}></div>
        </>
      )}
      {tenantInfo?.corpLogo ? (
        <Image src={tenantInfo?.corpLogo} height={28} alt="tenant-logo" />
      ) : (
        <div
          className={styles.appIcon}
          style={{
            background: tenantInfo?.iconColor || 'transparent'
          }}
        >
          <DynamicIcon
            IconComponent={appIconMap[tenantInfo?.iconName as keyof typeof appIconMap]}
            theme="outline"
            size="18"
            fill="#F2F3F5"
          />
        </div>
      )}
      <div className={styles.tenantName}>{tenantInfo?.corpName || tenantInfo?.appName}</div>
    </>
  );
};

export default TenantLogo;
