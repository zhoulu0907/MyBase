import { Space } from '@arco-design/web-react';
import {
  getApplication,
  pageApplicationVersion,
  type Application,
  type GetApplicationReq,
  type PageApplicationVersionReq
} from '@onebase/app';
import React, { useEffect, useState } from 'react';
import { useSearchParams } from 'react-router-dom';
import AppStatusHeader from './components/header';
import VersionManagement from './components/list';
import PublishVersionModal from './components/modals/publish';

import styles from './index.module.less';

export interface VersionRecord {
  id: string;
  versionName: string;
  versionNumber: string;
  description: string;
  environment: string;
  operationType: string;
  updaterName: string;
  updateTime: string;
}

const AppReleasePage: React.FC = () => {
  const [searchParams] = useSearchParams();
  const appId = searchParams.get('appId') || '';

  const [appInfo, setAppInfo] = useState<Application>();

  //   const [appStatus, setAppStatus] = useState<number>(AppStatus.DEVELOPING);
  //   const [currentVersion, setCurrentVersion] = useState('');
  //   const [versionURL, setVersionURL] = useState('');

  const [publishModalVisible, setPublishModalVisible] = useState(false);

  const [versionList, setVersionList] = useState<VersionRecord[]>([]);
  const [pageNo, setPageNo] = useState(1);
  const [pageSize, setPageSize] = useState(10);
  const [total, setTotal] = useState(0);

  useEffect(() => {
    if (appId) {
      handleGetApplicationInfo();
      handleGetApplicationVersion();
    }
  }, [appId]);

  useEffect(() => {
    handleGetApplicationVersion();
  }, [pageNo, pageSize]);

  const handleGetApplicationInfo = async () => {
    const appReq: GetApplicationReq = {
      id: appId
    };

    const appResp = await getApplication(appReq);
    console.log('==== appResp ====', appResp);

    if (appResp) {
      setAppInfo(appResp);
      //   setAppStatus(appResp.appStatus);
      //   if (appResp.versionNumber) {
      //     setCurrentVersion(appResp.versionNumber);
      //   }

      //   if (appResp.versionUrl) {
      //     setVersionURL(appResp.versionUrl);
      //   }
    }
  };

  const handleGetApplicationVersion = async () => {
    const versionReq: PageApplicationVersionReq = {
      applicationId: appId,
      pageNo: pageNo,
      pageSize: pageSize
    };

    const versionResp = await pageApplicationVersion(versionReq);

    setVersionList(versionResp.list);
    setTotal(versionResp.total);
  };

  const handleReleaseToggle = () => {
    setPublishModalVisible(true);
  };

  const handlePublishModalOk = async () => {
    setPublishModalVisible(false);
    handleGetApplicationVersion();
  };

  return (
    <div className={styles.appReleasePage}>
      <Space direction="vertical" size={16} style={{ width: '100%' }}>
        {/* 应用状态头部 */}
        {appInfo && <AppStatusHeader appInfo={appInfo} onReleaseToggle={handleReleaseToggle} />}
        {/* 应用访问链接 */}
        {/* {<AppAccessLink accessUrl={versionURL} />} */}

        {/* 版本管理 */}
        <VersionManagement
          applicationId={appId}
          list={versionList}
          total={total}
          pageSize={pageSize}
          setPageSize={setPageSize}
          currentPage={pageNo}
          setCurrentPage={setPageNo}
        />
      </Space>

      <PublishVersionModal
        applicationId={appId}
        visible={publishModalVisible}
        onCancel={() => setPublishModalVisible(false)}
        onOk={handlePublishModalOk}
      />
    </div>
  );
};

export default AppReleasePage;
