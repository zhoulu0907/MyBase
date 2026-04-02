import { useState } from 'react';
import type { Application } from '@onebase/app';
import { PlatformTenantPublishMode } from '@onebase/platform-center';
import { ApplicationStatus, ApplicationStatusLabel } from '../../const';

export function useAppCard() {
  const [exportVisible, setExportVisible] = useState(false);
  const [importVisible, setImportVisible] = useState(false);
  const [editModalVisible, setEditModalVisible] = useState(false);

  const getModel = (model?: string) => {
    if (model === PlatformTenantPublishMode.inner) {
      return '内部模式';
    } else if (model === PlatformTenantPublishMode.saas) {
      return 'SaaS模式';
    }
    return '未知模式';
  };

  const getDevelopStatus = (developStatus?: string) => {
    if (developStatus === ApplicationStatus.ITERATE) {
      return ApplicationStatusLabel.ITERATE;
    }
    return '';
  };

  const getTagColor = (item: Application) => {
    return item.appStatus === 0 ? '#547781' : '#2DC86D';
  };

  const getTagBackgroundColor = (item: Application) => {
    return item.appStatus === 0 ? 'rgba(36, 81, 93, 0.08)' : 'rgba(34, 206, 118, 0.08)';
  };

  return {
    exportVisible,
    setExportVisible,
    importVisible,
    setImportVisible,
    editModalVisible,
    setEditModalVisible,
    getModel,
    getDevelopStatus,
    getTagColor,
    getTagBackgroundColor
  };
}
