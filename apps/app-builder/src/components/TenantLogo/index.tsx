import { Image } from '@arco-design/web-react';
import { useEffect, useState } from 'react';
import styles from './index.module.less';
import { getFileUrlById, type TenantInfo } from '@onebase/platform-center';
import LogoSVG from '@/assets/images/app_header_logo.svg';
import { getPlatformExports, getPlatform } from '@/products';

interface ITenantLogoProps {
  tenantInfo: TenantInfo | null;
}

const TenantLogo: React.FC<ITenantLogoProps> = ({ tenantInfo }) => {
  const [PlatformLogo, setPlatformLogo] = useState<React.ComponentType<any> | null>(null);
  const currentPlatform = getPlatform();

  useEffect(() => {
    // 如果是灵畿平台，动态加载灵畿 Logo
    if (currentPlatform === 'lingji') {
      getPlatformExports().then(exports => {
        console.log('[TenantLogo] 平台导出:', exports);
        if (exports.LingjiLogo) {
          console.log('[TenantLogo] 设置 LingjiLogo');
          setPlatformLogo(() => exports.LingjiLogo!);
        } else {
          console.warn('[TenantLogo] LingjiLogo 未找到');
        }
      }).catch(e => {
        console.error('[TenantLogo] 加载平台包失败:', e);
      });
    }
  }, [currentPlatform]);

  // 灵畿平台使用专属 Logo（优先）
  if (currentPlatform === 'lingji' && PlatformLogo) {
    return <PlatformLogo />;
  }

  // 其他平台或灵畿 Logo 还未加载时，使用默认 logo + tenantInfo
  return (
    <>
      <Image src={LogoSVG} alt="logo" height={28} />
      <div className={styles.line}></div>
      {tenantInfo?.logoUrl && <Image src={getFileUrlById(tenantInfo.logoUrl)} height={28} alt="tenant-logo" />}
      <div className={styles.tenantName}>{tenantInfo?.name}</div>
    </>
  );
};

export default TenantLogo;