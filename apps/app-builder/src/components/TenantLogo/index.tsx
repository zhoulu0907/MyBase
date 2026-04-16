import { Image } from '@arco-design/web-react';
import styles from './index.module.less';
import { getFileUrlById, type TenantInfo } from '@onebase/platform-center';
import LogoSVG from '@/assets/images/app_header_logo.svg';
import TiangongLogoSVG from '@/assets/images/tiangong_app_header_logo.svg';

const isArtifex = typeof window !== 'undefined' && window.location.hostname.includes('artifex');

interface ITenantLogoProps {
  tenantInfo: TenantInfo | null;
}

const TenantLogo: React.FC<ITenantLogoProps> = ({ tenantInfo }) => {
  return (
    <>
      <Image src={isArtifex ? TiangongLogoSVG : LogoSVG} alt="logo" height={28} />
      <div className={styles.line}></div>
      {(tenantInfo?.logoUrl && <Image src={getFileUrlById(tenantInfo.logoUrl)} height={28} alt="tenant-logo" />)}
      <div className={styles.tenantName}>{tenantInfo?.name}</div>
    </>
  );
};

export default TenantLogo;
