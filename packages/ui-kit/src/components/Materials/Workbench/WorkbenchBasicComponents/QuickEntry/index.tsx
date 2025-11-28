import { Tabs } from '@arco-design/web-react';
import { IconRight } from '@arco-design/web-react/icon';
import type { CSSProperties } from 'react';
import { memo } from 'react';
import { WORKBENCH_STATUS_OPTIONS, WORKBENCH_STATUS_VALUES } from '../../constants';
import type { QuickEntryPropsConfig } from '../../types';
import '../index.css';
import './index.css';
import { type XQuickEntryConfig } from './schema';

const defaultQuickEntryProps: QuickEntryPropsConfig = {
  titleConfig: { showTitle: true, titleName: '快捷入口', showMore: true },
  styleConfig: { theme: '样式一' },
  groupConfig: { enableGroup: false, groups: [] }
};

const XQuickEntry = memo((props: XQuickEntryConfig & { runtime?: boolean; detailMode?: boolean }) => {
  const { status, width, props: quickEntryProps, runtime } = props;

  const { titleConfig, styleConfig, groupConfig } = quickEntryProps || defaultQuickEntryProps;

  const groups = groupConfig?.groups ?? [];
  const enableGroup = Boolean(groupConfig?.enableGroup);

  const renderEntryItem = (item: {
    entryName: string;
    entryIcon?: string;
    entryType?: string;
    linkAddress?: string;
    menuId?: string;
    group?: string;
  }, index: number) => {
    return (
      <div
        key={`${item.entryName}-${item.group}-${index}`}
        className="quick-entry-item"
        onClick={() => {
          if (!runtime) return;

          if (item.linkAddress) {
            if (item.linkAddress.startsWith('http')) {
              window.open(item.linkAddress);
            } else {
              console.log('Navigate to:', item.linkAddress);
            }
            return;
          }

          if (item.menuId) {
            // TODO: 集成与应用菜单的跳转能力
            console.log('Navigate to menu:', item.menuId);
          }
        }}
        style={{
          pointerEvents: runtime ? 'unset' : 'none',
          cursor: runtime && (item.linkAddress || item.menuId) ? 'pointer' : 'default'
        }}
      >
        {item.entryIcon && (
          <div className="quick-entry-item-icon">
            {/* 当 entryIcon 为图片地址时使用图片展示，否则作为文本展示 */}
            {/^https?:\/\//.test(item.entryIcon) ? (
              <img src={item.entryIcon} alt={item.entryName} className="quick-entry-item-icon-image" />
            ) : (
              item.entryIcon
            )}
          </div>
        )}
        <div className="quick-entry-item-title">{item.entryName}</div>
      </div>
    );
  };

  const renderContent = () => {
    if (enableGroup && groups.length > 0) {
      return (
        <Tabs defaultActiveTab="0">
          {groups.map((group, index) => (
            <Tabs.TabPane key={group.groupName || `group-${index}`} title={group.groupName}>
              <div className="quick-entry-items">
                {group.entries?.map((item, index) => renderEntryItem(item, index))}
              </div>
            </Tabs.TabPane>
          ))}
        </Tabs>
      );
    } else {
      const allItems = groups.flatMap((group) => group.entries ?? []);
      return (
        <div className="quick-entry-items">
          {allItems.map((item, index) => renderEntryItem(item, index))}
        </div>
      );
    }
  };

  const normalizeThemeClass = (theme?: string) => {
    if (!theme) return '';
    const slug = theme
      .trim()
      .replace(/\s+/g, '-')
      .replace(/[^\w-]/g, '')
      .toLowerCase();
    if (slug) {
      return slug;
    }
    return Array.from(theme)
      .map((char) => char.charCodeAt(0).toString(16))
      .join('');
  };

  const themeClass = normalizeThemeClass(styleConfig?.theme);
  const statusValueHidden = WORKBENCH_STATUS_VALUES[WORKBENCH_STATUS_OPTIONS.HIDDEN];
  const isHidden = status === statusValueHidden;
  const containerStyle: CSSProperties = {
    // width: width || '100%',
    opacity: isHidden ? 0.4 : 1
  };

  if (runtime && isHidden) {
    return null;
  }

  return (
    <div className={`quick-entry ${themeClass ? `quick-entry-theme-${themeClass}` : ''}`} style={containerStyle}>
      {(titleConfig?.showTitle || titleConfig?.showMore) && (
        <div className="quick-entry-header">
          {titleConfig?.showTitle && (
            <span className="quick-entry-header-title">{titleConfig?.titleName || '快捷入口'}</span>
          )}
          {titleConfig?.showMore && <span className="quick-entry-more">更多 <IconRight /></span>}
        </div>
      )}
      {renderContent()}
    </div>
  );
});

export default XQuickEntry;
