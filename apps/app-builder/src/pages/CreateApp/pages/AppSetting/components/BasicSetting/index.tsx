import { useState, useEffect, useRef } from 'react';
import {
  Avatar,
  Button,
  Card,
  Message,
  Modal,
  Popconfirm,
  Radio,
  Space,
  Tag,
  Tooltip,
  type FormInstance
} from '@arco-design/web-react';
import Title from '@arco-design/web-react/es/Typography/title';
import Paragraph from '@arco-design/web-react/es/Typography/paragraph';
import { IconCamera, IconEdit, IconInfoCircle } from '@arco-design/web-react/icon';
import checkSVG from '@/assets/images/appBasic/mode_check_icon.svg';
import dataSVG from '@/assets/images/appBasic/app_data_mode.svg';
import pageDesignSVG from '@/assets/images/appBasic/page_design.svg';
import previewSVG from '@/assets/images/appBasic/app_review.svg';
import internalSVG from '@/assets/images/appBasic/internal_mode.svg';
import saasSVG from '@/assets/images/appBasic/saas_mode.svg';
import checkIcon from '@/assets/images/check_icon.svg';
import DynamicIcon from '@/components/DynamicIcon';
import { appIcon, appIconColor } from '@/components/CreateApp/const';
import { defaultTheme, ThemeColorMap } from '@/pages/Home/pages/EnterpriseApp/const';
import { getDatasourceList, updateApplication, type Application, type UpdateApplicationReq } from '@onebase/app';
import { appIconMap } from '@onebase/ui-kit';
import ThememCard from './components/ThemeCard';
import styles from './index.module.less';
import EditAppModal from './components/EditAppModal';
import { PUBLISH_MODULE } from '@onebase/common';
import { useAppStore } from '@/store';

interface IProps {
  form: FormInstance;
  data: Application;
}

type CloseReason = 'confirm' | 'cancel' | 'outside' | 'esc';

// 基础设置
const BasicSetting = (props: IProps) => {
  const { form, data } = props;
  const { curAppInfo, setCurAppInfo } = useAppStore();
  const [isOwnDatasource, setIsOwnDatasource] = useState<boolean>(false); // 是否使用自有数据源
  const [iconName, setIconName] = useState<Application['iconName']>();
  const [iconColor, setIconColor] = useState<Application['iconColor']>();
  const [themeColor, setThemeColor] = useState<Application['themeColor']>('#009E9E'); // 应用主题色
  const initialRef = useRef<{
    iconName: Application['iconName'] | null;
    iconColor: Application['iconColor'] | undefined;
  } | null>(null);
  const actionRef = useRef<CloseReason | null>(null);

  // Edit Modal
  const [editModalVisible, setEditModalVisible] = useState<boolean>(false);
  const [publishModel, setPublishModel] = useState<string>();

  useEffect(() => {
    if (data && data?.id) {
      getDatasourceDetail();
      console.log('data', data);
      setIconName(data.iconName);
      setIconColor(data.iconColor);
      setThemeColor(data.themeColor);
      setPublishModel(data.publishModel);
      initialRef.current = { iconName: data.iconName, iconColor: data.iconColor };
    }
  }, [data]);

  const getDatasourceDetail = async () => {
    const res = await getDatasourceList({ applicationId: data?.id });

    // 现阶段一个应用只有一个数据源
    const curDatasourceOrigin = res[0].datasourceOrigin;
    setIsOwnDatasource(curDatasourceOrigin === 1);
  };

  const handleOnCancel = (visible?: boolean) => {
    const reason = actionRef.current ?? 'outside';
    if (!visible && reason !== 'confirm') {
      setIconName(initialRef.current?.iconName);
      setIconColor(initialRef.current?.iconColor);
    }
    actionRef.current = null;
  };

  /* 基础设置编辑模式 */
  const handleSaveAppMode = async () => {
    try {
      const params: UpdateApplicationReq = {
        ...data,
        iconColor: data.iconColor || '',
        iconName: data.iconName || '',
        tagIds: data.tags?.map((v) => v.id),
        publishModel: PUBLISH_MODULE.SASS
      };
      const res = await updateApplication(params);
      if (res) {
        Message.success('保存成功');
        setPublishModel(PUBLISH_MODULE.SASS);
      }
    } catch (_error) {
      console.error('保存失败 _error:', _error);
    }
  };

  /* 基础设置编辑Icon */
  const handleSaveAppIcon = async () => {
    try {
      actionRef.current = 'confirm';
      const params: UpdateApplicationReq = {
        ...data,
        iconName: iconName || '',
        iconColor: iconColor || '',
        tagIds: data.tags?.map((v) => v.id)
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
  };

  const switchToSaas = () => {
    Modal.confirm({
      title: <span>请确认是否将发布模式切换至SaaS模式</span>,
      content: (
        <div style={{ color: 'var(--color-text-3)' }}>
          <span>当前应用为“内部模式”。切换至 SaaS 模式将带来以下变化：</span>
          <ul style={{ margin: '0', paddingLeft: '14px' }}>
            <li>不可回退：切换后无法再切回内部模式</li>
            <li>清除人员数据：为适配多企业使用，原“用户角色”中的人员配置将被清空</li>
            <li>需配置角色：请在发布前设置好所需角色，发布时必须至少配置一个角色</li>
            <li>发布后不可修改：应用发布后，“用户角色”配置将锁定，不可变更</li>
          </ul>
          <span>确认切换前，请务必做好准备。</span>
        </div>
      ),
      className: styles.switchToSaasModal,
      onOk: () => {
        handleSaveAppMode();
      }
    });
  };

  return (
    <div className={styles.basicSetting}>
      {/* 基础信息 */}
      <div className={styles.title}>
        <div className={styles.leftShape} />
        <Title heading={6} style={{ margin: 0 }}>
          基础信息
        </Title>
        <IconEdit
          onClick={() => setEditModalVisible(true)}
          style={{
            color: '#009E9E',
            cursor: 'pointer'
          }}
        />
      </div>
      <EditAppModal editModalVisible={editModalVisible} setEditModalVisible={setEditModalVisible} appData={data} />
      <div
        className={styles.basicDiv}
        style={{
          ['--background-color' as any]: `linear-gradient(180deg, ${ThemeColorMap[themeColor || '']} 0%,  #FFF 62.7%)`
        }}
      >
        <div className={styles.basicContent}>
          <div className={styles.avatarWrap}>
            <Avatar
              size={80}
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
                  size="54"
                  fill="#F2F3F5"
                />
              )}
            </Avatar>
            <Popconfirm
              icon={null}
              title={null}
              position="bl"
              okText="确认"
              className={styles.editApp}
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

          <div>
            <span className={styles.appName}>{data?.appName}</span>
            <Paragraph className={styles.desc}>{data?.description}</Paragraph>
            <Space>
              {data?.tags?.map((tag: { id: string; tagName: string }) => (
                <Tag
                  key={tag.id}
                  style={{
                    color: themeColor || defaultTheme,
                    height: '22px',
                    backgroundColor: ThemeColorMap[themeColor ?? defaultTheme]
                  }}
                >
                  {tag.tagName}
                </Tag>
              ))}
            </Space>
          </div>
        </div>

        <div className={styles.appInfo}>
          <div>
            <span>应用编码</span>
            <span className={styles.appCode}>{data?.appCode}</span>
          </div>

          <div className={styles.appTheme}>
            <span>主题设置</span>
            <ThememCard themeColor={themeColor || defaultTheme} cardGap={16} editable={false} />
          </div>
        </div>
      </div>

      {/* 应用模式 */}
      <div className={styles.title}>
        <div className={styles.leftShape} />
        <Title heading={6} style={{ margin: 0 }}>
          应用模式
        </Title>
      </div>

      <div className={styles.appMode}>
        <div className={styles.appModeInfo}>
          <div className={styles.mode}>
            <span className={styles.modeTitle}>应用模式</span>
            <span className={styles.modeValue}>经典模式</span>
          </div>

          <div className={styles.mode}>
            <span className={styles.modeTitle}>模式特点</span>
            <Space>
              <span className={styles.modeSpec}>
                <img src={checkSVG} />
                数据建模先行
              </span>
              <span className={styles.modeSpec}>
                <img src={checkSVG} />
                元数据驱动表单生成
              </span>
            </Space>
          </div>

          <div className={styles.mode}>
            <span className={styles.modeTitle}>模式配置</span>
            <Space>
              <Card bordered size="small" className={styles.modeConfig}>
                <span className={styles.modeSpec}>
                  <img src={dataSVG} />
                  数据建模
                </span>
                <div className={styles.modeDesc}>完善的数据资产管理</div>
              </Card>

              <Card bordered size="small" className={styles.modeConfig}>
                <span className={styles.modeSpec}>
                  <img src={pageDesignSVG} />
                  页面设计
                </span>
                <div className={styles.modeDesc}>可视化界面创建表单</div>
              </Card>
            </Space>
          </div>
        </div>

        <div className={styles.preview}>
          <div className={styles.modeTitle}>预览图</div>
          <img className={styles.previewImg} src={previewSVG} alt="previewImage" />
        </div>
      </div>

      {/* 发布模式 */}
      <div className={styles.title}>
        <div className={styles.leftShape} />
        <Title heading={6} style={{ margin: 0 }}>
          发布模式
        </Title>
      </div>

      <div className={styles.releaseMode}>
        <span className={styles.modeTitle}>应用模式</span>
        <Space>
          {publishModel === PUBLISH_MODULE.INNER ? (
            <>
              <Card bordered size="small" className={styles.modeConfig}>
                <span className={styles.releaseModeSpec}>
                  <img src={internalSVG} />
                  内部模式
                </span>
              </Card>

              <Button type="text" onClick={switchToSaas}>
                切换至SaaS模式
              </Button>
            </>
          ) : (
            <>
              <Card bordered size="small" className={styles.modeConfig}>
                <span className={styles.releaseModeSpec}>
                  <img src={saasSVG} />
                  SaaS模式
                </span>
              </Card>
              <Tooltip content="注意：SaaS模式不能切换成内部模式" position="tl">
                <IconInfoCircle style={{ color: '#86909C' }} />
              </Tooltip>
            </>
          )}
        </Space>
      </div>
    </div>
  );
};

export default BasicSetting;
