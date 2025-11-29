import { useI18n } from '@/hooks/useI18n';
import { Breadcrumb } from '@arco-design/web-react';
import React, { useMemo } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import styles from './index.module.less';

interface BreadcrumbItemType {
  key: string;
  title: string;
  path?: string;
}

interface BreadcrumbProps {
  className?: string;
  items?: BreadcrumbItemType[];
}

const AppBreadcrumb: React.FC<BreadcrumbProps> = ({ className, items }) => {
  const { t } = useI18n();
  const location = useLocation();
  const navigate = useNavigate();

  // 根据当前路径生成面包屑项
  const breadcrumbItems = useMemo(() => {
    if (items && items.length > 0) {
      return items;
    }

    const pathSegments = location.pathname.split('/').filter(Boolean);

    // 如果路径是 /onebase 或 /onebase/，返回空数组（不显示面包屑）
    if (pathSegments.length === 1 && pathSegments[0] === 'onebase') {
      return [];
    }

    const result: BreadcrumbItemType[] = [];

    // 如果路径以/onebase开头，移除/onebase
    const segments = pathSegments[0] === 'onebase' ? pathSegments.slice(1) : pathSegments;

    let currentPath = '/onebase/setting';
    segments.forEach((segment, index) => {
      if (segment == 'setting') {
        return;
      }

      if (index == 0) {
        return;
      }

      currentPath += `/${segment}`;

      switch (segment) {
        case 'platform-info':
          break;
        case 'application':
          result.push({
            key: 'onebase',
            title: '应用管理',
            path: '/onebase'
          });
          break;
        case 'user':
        case 'role':
        case 'organization':
          result.push({
            key: 'onebase',
            title: '应用管理',
            path: '/onebase'
          });
          break;
        case 'spaceInfo':
        case 'system-dict':
        case 'security':
        case 'tenant':
          result.push({
            key: 'onebase',
            title: '系统配置',
            path: '/onebase'
          });
          break;
        case 'enterprise':
          result.push({
            key: 'onebase',
            title: '扩展功能',
            path: '/onebase'
          });
          break;
        default:
          break;
      }

      // 根据路径生成对应的标题
      let title: string;
      switch (segment) {
        case 'platform-info':
          title = t('sider.platformInfo');
          break;
        case 'application':
          title = t('sider.application');
          break;
        case 'user':
          title = t('sider.user');
          break;
        case 'role':
          title = t('sider.role');
          break;
        case 'organization':
          title = t('sider.organization');
          break;
        case 'system-dict':
          title = t('sider.systemDict');
          break;
        case 'security':
          title = t('sider.security');
          break;
        case 'spaceInfo':
          title = t('sider.spaceInfo');
          break;
        case 'enterpriseInfo':
          title = t('sider.enterpriseInfo');
          break;
        case 'tenant':
          title = t('sider.tenant');
          break;
        case 'enterprise':
          title = t('sider.enterprise');
          break;
        case 'create-enterprise':
          title = t('sider.createEnterprise');
          break;
        case 'authorized-application':
          title = t('sider.authorizedApplication');
          break;
        case 'edit':
          title = t('sider.edit');
          break;
        default:
          title = decodeURIComponent(segment);
      }

      result.push({
        key: segment,
        title,
        path: index === segments.length - 1 ? undefined : currentPath
      });
    });

    return result;
  }, [location.pathname, items, t]);

  const handleBreadcrumbClick = (item: BreadcrumbItemType) => {
    if (item.path) {
      navigate(item.path);
    }
  };

  // 如果没有面包屑项，不渲染面包屑
  if (breadcrumbItems.length === 0) {
    return null;
  }

  return (
    <div className={`${styles.breadcrumbContainer} ${className || ''}`}>
      <Breadcrumb separator="/">
        {breadcrumbItems.map((item, index) => (
          <Breadcrumb.Item
            key={item.key}
            // onClick={() => handleBreadcrumbClick(item)}
            // className={item.path ? styles.clickable : styles.current}
          >
            {item.title}
          </Breadcrumb.Item>
        ))}
      </Breadcrumb>
    </div>
  );
};

export default AppBreadcrumb;
