import launchSVG from '@/assets/images/launch.svg';
import appExportSVG from '@/assets/images/appExport.svg';
import appUpdateSVG from '@/assets/images/appUpdate.svg';
import DynamicIcon from '@/components/DynamicIcon';
import { Avatar, Divider, Dropdown, Menu, Space, Tag, Tooltip, Typography } from '@arco-design/web-react';
import { IconDelete, IconEdit, IconMoreVertical } from '@arco-design/web-react/icon';
import { type Application } from '@onebase/app';
import { appIconMap } from '@onebase/ui-kit';
import { getFileUrlById, PlatformTenantPublishMode } from '@onebase/platform-center';
import dayjs from 'dayjs';
import React, { useState } from 'react';
import { ApplicationStatus, ApplicationStatusLabel, TagColor } from '../../const';
import styles from './index.module.less';
import type { developUser } from '@onebase/app/src/types';
import { hasPermission, TENANT_APP_PERMISSION as ACTIONS } from '@onebase/common';
import AppExportModal from '@/components/AppExportModal';
import AppImportModal from '@/components/AppImportModal';

const AvatarGroup = Avatar.Group;

interface AppCardProps {
  item: Application;
  optionVisibleId: string;
  onOptionVisibleChange: (visible: boolean, id: string) => void;
  onEdit: (appId: string) => void;
  onLaunch: (appId: string) => void;
  onDelete: (item: Application) => void;
}

const AppCard: React.FC<AppCardProps> = ({
  item,
  optionVisibleId,
  onOptionVisibleChange,
  onEdit,
  onLaunch,
  onDelete
}) => {
  const getModel = (model?: string) => {
    if (model === PlatformTenantPublishMode.inner) {
      return '内部模式';
    } else if (model === PlatformTenantPublishMode.saas) {
      return 'SaaS模式';
    }
    return '未知模式';
  };

  const getColor = (model?: string) => {
    return model === PlatformTenantPublishMode.inner ? 'cyan' : 'blue';
  };

  const getDevelopStatus = (developStatus?: string) => {
    if (developStatus === ApplicationStatus.ITERATE) {
      return ApplicationStatusLabel.ITERATE;
    }
    return '';
  };

  const getTagColor = (item: Application) => {
    return item.appStatus === 0 ? '#4E5969' : '#00B42A';
  };

  const getTagBackgroundColor = (item: Application) => {
    return item.appStatus === 0 ? '#F7F8FA' : '#E8FFEA';
  };

  // 应用导出弹窗
  const [exportVisible, setExportVisible] = useState(false);
  // 应用导入/更新弹窗
  const [importVisible, setImportVisible] = useState(false);

  const menu = (
    <Menu style={{ marginRight: '10px' }}>
      <Menu.Item
        key="1"
        onClick={(e) => {
          e.stopPropagation();
          onLaunch(item.id);
        }}
      >
        <div className={styles.menuItem}>
          <img src={launchSVG} alt="访问应用" style={{ marginRight: 4 }} />
          访问应用
        </div>
      </Menu.Item>
      <Menu.Item
        key="2"
        onClick={(e) => {
          e.stopPropagation();
          setExportVisible(true);
        }}
      >
        <div className={styles.menuItem}>
          <img src={appExportSVG} alt="导出" style={{ marginRight: 4 }} />
          导出
        </div>
      </Menu.Item>
      {hasPermission(ACTIONS.UPDATE) && (
        <Menu.Item
          key="3"
          onClick={(e) => {
            e.stopPropagation();
            setImportVisible(true);
          }}
        >
          <div className={styles.menuItem}>
            <img src={appUpdateSVG} alt="导入更新" style={{ marginRight: 4 }} />
            导入更新
          </div>
        </Menu.Item>
      )}
      {hasPermission(ACTIONS.DELETE) && (
        <Menu.Item
          key="4"
          onClick={(e) => {
            e.stopPropagation();
            onDelete(item);
          }}
          style={{ color: 'red' }}
        >
          <IconDelete style={{ marginRight: 4 }} />
          删除
        </Menu.Item>
      )}
    </Menu>
  );

  return (
    <div className={styles.appCard} key={item.id}>
      <div className={styles.appCardTop}>
        <div className={styles.appCardHeader}>
          <div className={styles.appName}>
            <div className={styles.appIcon} style={{ backgroundColor: item.iconColor }}>
              <DynamicIcon
                IconComponent={appIconMap[item.iconName as keyof typeof appIconMap]}
                theme="outline"
                size="32"
                fill="#F2F3F5"
              />
            </div>
            <div className={styles.appCardInfo}>
              <div className={styles.infoHeader}>
                <Tooltip content={item.appName}>
                  <div className={styles.appTitle}>{item.appName}</div>
                </Tooltip>
                <div className={styles.tagWrapper}>
                  {item?.developStatus && (
                    <Tag
                      color={TagColor[item.appStatus]}
                      style={{
                        fontSize: 12,
                        fontWeight: 400
                      }}
                    >
                      {getDevelopStatus(item.developStatus)}
                    </Tag>
                  )}

                  <Tag
                    color={TagColor[item.appStatus]}
                    style={{
                      fontSize: 12,
                      fontWeight: 400
                    }}
                  >
                    {item.appStatusText}
                  </Tag>
                </div>
              </div>
              <div className={styles.tagAndTime}>
                <div className={styles.online}>
                  <Tag
                    color={TagColor[item.appStatus]}
                    style={{
                      fontSize: 12,
                      fontWeight: 400,
                      color: getTagColor(item),
                      backgroundColor: getTagBackgroundColor(item)
                    }}
                    className={styles.tag}
                  >
                    <span
                      className={styles.circle}
                      style={{
                        backgroundColor: getTagColor(item)
                      }}
                    ></span>
                    <span>{item.appStatusText}</span>
                  </Tag>
                </div>
                <div className={styles.updateTime}>
                  更新时间：{dayjs(item?.updateTime).format('YYYY-MM-DD HH:mm:ss')}
                </div>
              </div>
            </div>
          </div>
        </div>

        <div className={styles.appCardBody}>
          <Tooltip content={item.description}>
            <div className={styles.appDesc}>{item.description ?? '该应用暂无介绍。'}</div>
          </Tooltip>
          <div className={styles.appTags}>
            <Tag color={getColor(item.publishModel)} className={styles.tag}>
              {getModel(item.publishModel)}
            </Tag>
            {item.tags && item.tags.length > 0 && <Divider type="vertical" style={{ margin: '0' }} />}
            {item.tags?.map((tag: { id: string; tagName: string }) => (
              <Tag
                key={tag.id}
                style={{
                  color: '#4E5969',
                  backgroundColor: '#F2F3F5'
                }}
              >
                {tag.tagName}
              </Tag>
            ))}
          </div>
        </div>
      </div>
      <Divider style={{ margin: '12px 0 0' }} />
      <div className={styles.appCardFooter}>
        <div className={styles.footerLeft}>
          {item?.userPhotoList && item?.userPhotoList.length > 0 && (
            <>
              <AvatarGroup
                size={24}
                maxCount={5}
                maxPopoverTriggerProps={{
                  disabled: true
                }}
              >
                {item?.userPhotoList?.map((item: developUser, index: number) => {
                  return item.avatar ? (
                    <Avatar key={index}>
                      <img src={getFileUrlById(item.avatar)} alt="avatar" />
                    </Avatar>
                  ) : (
                    <Avatar key={index} style={{ backgroundColor: '#009e9e' }}>
                      {item?.nickName?.charAt(0)}
                    </Avatar>
                  );
                })}
              </AvatarGroup>
              {(item?.userPhotoList?.length > 1 && (
                <>
                  <Typography.Text type="secondary">
                    {item?.createUser}等{item?.userPhotoList?.length}人开发
                  </Typography.Text>
                </>
              )) || <Typography.Text type="secondary">{item?.createUser}</Typography.Text>}
            </>
          )}
        </div>

        <div className={styles.footerRight}>
          <Space>
            {hasPermission(ACTIONS.UPDATE) && (
              <IconEdit className={styles.operationIcon} fontSize={16} onClick={() => onEdit(item.id)} />
            )}
            <Dropdown
              droplist={menu}
              trigger="click"
              position="bottom"
              popupVisible={optionVisibleId === item.id}
              onVisibleChange={(v) => onOptionVisibleChange(v, item.id)}
            >
              <IconMoreVertical className={styles.operationIcon} fontSize={16} style={{ color: '#272e3b' }} />
            </Dropdown>
          </Space>
        </div>
      </div>
      <AppExportModal visible={exportVisible} onClose={() => setExportVisible(false)} appInfo={item} />
      <AppImportModal visible={importVisible} onClose={() => setImportVisible(false)} appInfo={item} />
    </div>
  );
};

export default AppCard;
