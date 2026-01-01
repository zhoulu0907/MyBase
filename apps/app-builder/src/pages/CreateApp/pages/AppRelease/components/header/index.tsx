import ReleaseBtnUpIcon from '@/assets/images/release_btn_up_icon.svg';
import { Button, Card, Divider } from '@arco-design/web-react';
import { IconCopy } from '@arco-design/web-react/icon';
import { AppStatus, type Application } from '@onebase/app';
import {
  copyToClipboard,
  DynamicIcon,
  getRuntimeMobileURL,
  getRuntimeURL,
  PUBLISH_MODULE,
  TokenManager
} from '@onebase/common';
import { appIconMap } from '@onebase/ui-kit';
import dayjs from 'dayjs';
import React from 'react';
import styles from './index.module.less';

interface AppStatusHeaderProps {
  appInfo: Application;
  onReleaseToggle: () => void;
  onOfflineToggle: () => void;
}

const AppStatusHeader: React.FC<AppStatusHeaderProps> = ({ appInfo, onReleaseToggle, onOfflineToggle }) => {
  const tenantId = TokenManager.getTenantInfo()?.tenantId || '';
  const redirectURL = `${getRuntimeURL()}/#/onebase/${tenantId}/${appInfo.id}/runtime/`;
  const runtimeURL = `${getRuntimeURL()}/#/login?redirectURL=${redirectURL}`;
  const runtimeMobileURL = `${getRuntimeMobileURL()}/#/login?redirectURL=${redirectURL}`;

  const appStatus = appInfo.appStatus;

  const navigateToRunTime = (text: string) => {
    window.open(text);
  };

  return (
    <Card>
      <div className={styles.appStatusHeader}>
        <div className={styles.appInfo}>
          <div className={styles.left}>
            <div className={styles.appIcon} style={{ backgroundColor: appInfo.iconColor }}>
              <DynamicIcon
                IconComponent={appIconMap[appInfo?.iconName as keyof typeof appIconMap]}
                theme="outline"
                size="32"
                fill="#F2F3F5"
              />
            </div>
            <div className={styles.appNameWrapper}>
              <div className={styles.appName}>
                <div>{appInfo.appName}</div>
                <div
                  className={
                    appInfo.publishModel === PUBLISH_MODULE.SASS
                      ? styles.saasAppPublishModel
                      : styles.innerAppPublishModel
                  }
                >
                  {appInfo.publishModel === PUBLISH_MODULE.SASS ? 'SaaS模式' : '内部模式'}
                </div>
              </div>
              <div className={styles.appId}>
                应用ID: {appInfo.appCode || '--'}
                <Divider type="vertical" />
                数据资产标识: {appInfo.appUid || '--'}
                <Divider type="vertical" />
                最后更新时间: {dayjs(appInfo.updateTime).format('YYYY-MM-DD HH:mm:ss') || '--'}
              </div>
            </div>
          </div>

          <div className={styles.right}>
            <Button type={'default'} status={'default'} onClick={onOfflineToggle}>
              {'应用下架'}
            </Button>
            <Button
              type={'primary'}
              status={'default'}
              icon={<img className={styles.releaseBtnIcon} src={ReleaseBtnUpIcon} alt="" />}
              onClick={onReleaseToggle}
            >
              {'应用发布'}
            </Button>
          </div>
        </div>

        <Divider type="horizontal" />
        <div className={styles.releaseStatus}>
          <div className={styles.statusInfo}>
            <div className={styles.statusKey}>发布状态</div>
            <div className={styles.statusValue}>
              {appStatus === AppStatus.DEVELOPING && <div className={styles.appStatusDeveloping}>开发中</div>}
              {appStatus == AppStatus.EDITING_AFTER_PUBLISH && (
                <div className={styles.appStatusEditAfterPublished}>迭代中</div>
              )}
              {(appStatus == AppStatus.PUBLISHED || appStatus == AppStatus.EDITING_AFTER_PUBLISH) && (
                <div className={styles.appStatusPublished}>已发布</div>
              )}
            </div>
          </div>
          <div className={styles.statusInfo}>
            <div className={styles.statusKey}>当前版本</div>
            <div className={styles.statusValue}>{appInfo.versionNumber || '--'}</div>
          </div>
          <div className={styles.statusInfo}>
            <div className={styles.statusKey}>发布人</div>
            <div className={styles.statusValue}>{appInfo.publisher || '--'}</div>
          </div>
          <div className={styles.statusInfo}>
            <div className={styles.statusKey}>发布时间</div>
            <div className={styles.statusValue}>
              {appInfo.publishTime ? dayjs(appInfo.publishTime).format('YYYY-MM-DD HH:mm:ss') : '--'}
            </div>
          </div>
          <div className={styles.statusInfo} style={{ flex: 3 }}>
            <div className={styles.statusKey}>访问链接</div>
            <div className={styles.releaseUrlWrapper}>
              <div className={styles.releaseUrlItem}>
                Web端:
                <div className={styles.linkText} onClick={() => navigateToRunTime(runtimeURL)}>
                  {runtimeURL}
                </div>
                <IconCopy onClick={() => copyToClipboard(runtimeURL)} style={{ fontSize: 16 }} />
              </div>
              <div className={styles.releaseUrlItem}>
                移动端:
                <div className={styles.linkText} onClick={() => navigateToRunTime(runtimeMobileURL)}>
                  {runtimeMobileURL}
                </div>
                <IconCopy onClick={() => copyToClipboard(runtimeMobileURL)} style={{ fontSize: 16 }} />
              </div>
            </div>
          </div>
        </div>
      </div>
    </Card>
  );
};

export default AppStatusHeader;
