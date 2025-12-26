import { useEffect, useRef, useState } from 'react';
import { Avatar, Form, Input, Message, Popconfirm, Tooltip, type FormInstance } from '@arco-design/web-react';
import { DynamicIcon } from '@onebase/common';
import { appIconMap } from '@onebase/ui-kit';
import { appIcon, appIconColor } from '../CreateApp/const';
import { IconCamera } from '@arco-design/web-react/icon';
import checkIcon from '@/assets/images/check_icon.svg';
import { updateApplication, type Application, type UpdateApplicationReq } from '@onebase/app';
import { useAppStore } from '@/store';
import styles from './index.module.less';
import sample from 'lodash-es/sample';

interface IProps {
  data?: Application;
  form?: FormInstance;
  isCreateApp?: boolean;
  avatarSize?: number;
  iconSize?: number;
}

type CloseReason = 'confirm' | 'cancel' | 'outside' | 'esc';

const AppAvatarIcon = (props: IProps) => {
  const { data, form, isCreateApp, avatarSize = 80, iconSize = 54 } = props;
  const { curAppInfo, setCurAppInfo } = useAppStore();
  const [iconName, setIconName] = useState<Application['iconName']>();
  const [iconColor, setIconColor] = useState<Application['iconColor']>();

  const initialRef = useRef<{
    iconName: Application['iconName'] | null;
    iconColor: Application['iconColor'] | undefined;
  } | null>(null);
  const actionRef = useRef<CloseReason | null>(null);

  useEffect(() => {
    if (data && data?.id) {
      console.log('data', data);
      setIconName(data.iconName);
      setIconColor(data.iconColor);
      initialRef.current = { iconName: data.iconName, iconColor: data.iconColor };
    }
  }, [data]);

  useEffect(() => {
    if (isCreateApp) {
      const randomIcon = {
        iconName: sample(appIcon)!,
        iconColor: sample(appIconColor)!
      };
      setIconName(randomIcon.iconName);
      setIconColor(randomIcon.iconColor);
      form?.setFieldsValue({
        ...randomIcon
      });
    }
  }, [isCreateApp]);

  /* 基础设置编辑Icon */
  const handleSaveAppIcon = async () => {
    if (isCreateApp) {
      form?.setFieldsValue({
        iconName,
        iconColor
      });
    } else {
      try {
        actionRef.current = 'confirm';
        const params: UpdateApplicationReq = {
          ...data,
          iconName: iconName || '',
          iconColor: iconColor || '',
          tagIds: data?.tags?.map((v) => v.id)
        };
        const res = await updateApplication(params);
        if (res) {
          Message.success('保存成功');
          setCurAppInfo({
            ...curAppInfo,
            iconName: iconName || '',
            iconColor: iconColor || ''
          });
        }
      } catch (_error) {
        console.error('保存失败 _error:', _error);
      }
    }
  };

  const handleOnCancel = (visible?: boolean) => {
    const reason = actionRef.current ?? 'outside';
    if (!visible && reason !== 'confirm' && !isCreateApp) {
      setIconName(initialRef.current?.iconName);
      setIconColor(initialRef.current?.iconColor);
    }
    actionRef.current = null;
  };

  return (
    <>
      <Form.Item field="iconName" hidden>
        <Input />
      </Form.Item>
      <Form.Item field="iconColor" hidden>
        <Input />
      </Form.Item>
      <div className={styles.avatarIconWrap}>
        <div className={styles.avatarWrap} style={{ width: avatarSize, height: avatarSize }}>
          <Avatar
            size={avatarSize}
            style={{
              borderRadius: 12,
              background: iconColor,
              fontWeight: 700,
              fontSize: 28
            }}
          >
            {iconName && (
              <DynamicIcon
                IconComponent={appIconMap[iconName as keyof typeof appIconMap]}
                theme="outline"
                size={iconSize}
                fill="#F2F3F5"
              />
            )}
          </Avatar>
          <Popconfirm
            icon={null}
            title={null}
            position="bl"
            okText="确认"
            className={styles.editAppAvatarIcon}
            onOk={() => {
              handleSaveAppIcon();
            }}
            onCancel={() => {
              actionRef.current = 'cancel';
              handleOnCancel();
            }}
            onVisibleChange={(visible) => {
              handleOnCancel(visible);
            }}
            style={{ maxWidth: '381px' }}
            content={
              <>
                <div className={styles.avatarWrapper}>
                  {appIcon.map((item, index) => (
                    <div
                      className={styles.avatar}
                      key={index}
                      style={{ backgroundColor: item === iconName ? iconColor : '#F2F3F5' }}
                      onClick={() => setIconName(item)}
                    >
                      <DynamicIcon
                        IconComponent={appIconMap[item as keyof typeof appIconMap]}
                        theme="outline"
                        size="24"
                        fill={item === iconName ? '#F2F3F5' : '#272E3B'}
                      />
                    </div>
                  ))}
                </div>
                <div className={styles.avatarColor}>
                  {appIconColor.map((color, index) => (
                    <div
                      className={styles.color}
                      key={`color-${index}`}
                      style={{ backgroundColor: color }}
                      onClick={() => setIconColor(color)}
                    >
                      {color === iconColor && <img src={checkIcon} />}
                    </div>
                  ))}
                </div>
              </>
            }
          >
            <Tooltip content="修改图标">
              <div className={styles.avatarOverlay}>
                <div className={styles.cameraBox}>
                  <IconCamera />
                </div>
              </div>
            </Tooltip>
          </Popconfirm>
        </div>
      </div>
    </>
  );
};

export default AppAvatarIcon;
