import { Tabs } from '@arco-design/web-react';
import dayjs from 'dayjs';
import { WORKBENCH_THEME_OPTIONS, DATA_CONFIG_NAME_MAP } from '@onebase/ui-kit';
import { pendingListDefault } from '@onebase/ui-kit/src/components/Materials/Workbench/WorkbenchBasicComponents/TodoList/schema';
import WbThemeSelectorConfig from '../../components/WbThemeSelectorConfig';
import commonStyles from '../../components/WbThemeSelectorConfig/index.module.less';

interface StyleLibraryProps {
  handlePropsChange: (key: string, value: unknown) => void;
  item: { key: string };
  configs: Record<string, unknown>;
}

const statusMap: Record<string, string> = {
  timeout: '超时',
  normal: '正常',
  completed: '已完成',
  cancelled: '已取消',
  in_approval: '审批中'
};

const defaultDataConfig = ['待我处理', '我创建的', '我已处理', '抄送我的'];

export function StyleLibrary({ handlePropsChange, item, configs }: StyleLibraryProps) {
  const renderPreviewCard = (
    theme: string,
    isShowActive: boolean,
    currentTheme: string,
    onThemeChange: (theme: string) => void
  ) => {
    const isTheme1 = theme === WORKBENCH_THEME_OPTIONS.THEME_1;
    const isActive = currentTheme === theme && isShowActive;

    return (
      <div
        className={`${commonStyles.previewCardFirst} ${commonStyles.previewCardContainer} ${isShowActive ? commonStyles.previewCardClick : ''} ${isActive ? commonStyles.previewCardActive : ''}`}
        onClick={() => onThemeChange(theme)}
        style={{
          transform: 'scale(0.8)',
          transformOrigin: 'top left',
          width: 'calc(100% / 0.8)',
          boxSizing: 'border-box'
        }}
      >
        <div
          style={{
            background: '#fff',
            padding: '12px',
            borderRadius: '8px',
            width: '100%',
            boxSizing: 'border-box'
          }}
        >
          {/* 标题 */}
          <div
            style={{
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'space-between',
              marginBottom: '12px',
              gap: '9px'
            }}
          >
            <span style={{ fontSize: '12px', fontWeight: 600, color: '#1d2129' }}>待办列表</span>
          </div>

          {/* Tabs */}
          <Tabs style={{ width: '100%' }} size="mini" headerPadding={false} activeTab="待我处理">
            {defaultDataConfig.map((item) => (
              <Tabs.TabPane key={item} title={DATA_CONFIG_NAME_MAP[item] || item}>
                <div style={{ display: 'flex', flexDirection: 'column', padding: '9px', gap: '7.5px' }}>
                  {pendingListDefault.slice(0, 2).map((item) => (
                    <div
                      key={item.id}
                      style={{
                        display: 'flex',
                        alignItems: 'center',
                        justifyContent: 'space-between',
                        gap: '4.5px',
                        borderRadius: '3px',
                        boxSizing: 'border-box',
                        padding: '3px 6px'
                      }}
                    >
                      {/* 头像区域 - 仅 THEME_1 显示 */}
                      {isTheme1 && (
                        <div
                          style={{
                            width: '30px',
                            height: '30px',
                            borderRadius: '7.5px',
                            boxSizing: 'border-box',
                            flexShrink: 0
                          }}
                        >
                          {item.initiator.avatar ? (
                            <img
                              src={item.initiator.avatar}
                              alt={item.initiator.name}
                              style={{
                                width: '100%',
                                height: '100%',
                                borderRadius: '50%',
                                objectFit: 'cover'
                              }}
                            />
                          ) : (
                            <div
                              style={{
                                background: 'rgb(var(--primary-6))',
                                color: '#fff',
                                height: '100%',
                                width: '100%',
                                borderRadius: '50%',
                                display: 'flex',
                                alignItems: 'center',
                                justifyContent: 'center',
                                fontSize: '10.5px'
                              }}
                            >
                              {item.initiator.name?.charAt(0)}
                            </div>
                          )}
                        </div>
                      )}

                      {/* 内容区域 */}
                      <div
                        style={{
                          width: isTheme1 ? 'calc(100% - 30px)' : '100%',
                          height: '100%',
                          display: 'flex',
                          flexDirection: 'column',
                          justifyContent: 'space-between',
                          gap: '3px'
                        }}
                      >
                        <div
                          style={{ display: 'flex', justifyContent: 'flex-start', gap: '9px', alignItems: 'center' }}
                        >
                          <div style={{ fontSize: '10.5px', fontWeight: 500, color: '#272E3B' }}>
                            {item.processTitle}
                          </div>
                          <div
                            style={{
                              fontSize: '9px',
                              padding: '1.5px 3px',
                              borderRadius: '3px',
                              color: item.flowStatus === 'timeout' ? '#F53F3F' : '#86909C',
                              backgroundColor: item.flowStatus === 'timeout' ? '#FFECE8' : '#d3d3d35c'
                            }}
                          >
                            {statusMap[item.flowStatus] || item.flowStatus}
                          </div>
                        </div>
                        <div
                          style={{
                            display: 'flex',
                            justifyContent: 'flex-start',
                            gap: '12px',
                            fontSize: '9px',
                            color: '#86909C'
                          }}
                        >
                          <div>发起人：{item.initiator.name}</div>
                          <div>创建时间：{dayjs(item.submitTime).format('YYYY-MM-DD HH:mm:ss')}</div>
                        </div>
                      </div>
                    </div>
                  ))}
                </div>
              </Tabs.TabPane>
            ))}
          </Tabs>
        </div>
      </div>
    );
  };

  return (
    <WbThemeSelectorConfig
      handlePropsChange={handlePropsChange}
      item={item}
      configs={configs}
      renderPreviewCard={renderPreviewCard}
      styleOptions={[WORKBENCH_THEME_OPTIONS.THEME_1, WORKBENCH_THEME_OPTIONS.THEME_2]}
    />
  );
}
