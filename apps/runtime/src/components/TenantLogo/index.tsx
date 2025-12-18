import { Image } from '@arco-design/web-react';
import styles from './index.module.less';
import LogoSVG from '@/assets/images/ob_logo.svg';
import type { CorpDetailResponse } from '@onebase/platform-center';

interface ITenantLogoProps {
  tenantInfo: CorpDetailResponse | null;
}

const TenantLogo: React.FC<ITenantLogoProps> = ({ tenantInfo }) => {
  return (
    <>
      <Image src={LogoSVG} alt="logo" height={28} />
      <div className={styles.line}></div>
      {tenantInfo?.corpLogo && <Image src={tenantInfo?.corpLogo} height={28} alt="tenant-logo" />}
      <div className={styles.tenantName}>{tenantInfo?.corpName}</div>
    </>
  );
};

export default TenantLogo;
