import { Tabs } from '@arco-design/mobile-react';
import { IconArrowIn } from '@arco-design/mobile-react/esm/icon';
import type { CSSProperties } from 'react';
import { nanoid } from 'nanoid';
import { memo } from 'react';
import { WORKBENCH_STATUS_OPTIONS, WORKBENCH_STATUS_VALUES, QUICK_ENTRY_THEME_OPTIONS, QUICK_ENTRY_THEME_VALUES, workbenchSchema, type QuickEntryPropsConfig, WorkbenchComponentSchema } from '@onebase/ui-kit';
import { getDefaultIcon } from './getDefaultIcon';
import './index.css';

type XQuickEntryConfig = typeof workbenchSchema.XQuickEntry.config;

// 主题三使用的颜色数组（对应 arcoPalette.primary 的颜色，添加 20% 透明度）
const THEME_THREE_COLORS = ['#24b28f20', '#eb693a20', '#1979ff20', '#7e5aea20', '#009e9e20', '#ebbc0020'];

const defaultQuickEntryProps: QuickEntryPropsConfig = {
  titleConfig: { showTitle: true, titleName: '快捷入口', showMore: true },
  styleConfig: { theme: QUICK_ENTRY_THEME_VALUES[QUICK_ENTRY_THEME_OPTIONS.THEME_1] },
  groupConfig: { enableGroup: false, groups: [] }
};

const XQuickEntry = memo((props: XQuickEntryConfig & { runtime?: boolean; detailMode?: boolean }) => {
  const { id, status, width, props: quickEntryProps, runtime } = props;

  const { titleConfig, styleConfig, groupConfig } = quickEntryProps || defaultQuickEntryProps;

  const groups = groupConfig?.groups ?? [];
  const enableGroup = Boolean(groupConfig?.enableGroup);

  const renderEntryItem = (
    item: {
      entryName: string;
      entryIcon?: string;
      entryType?: string;
      linkAddress?: string;
      menuId?: string;
      group?: string;
      entryDesc?: string;
    },
    index: number
  ) => {
    const theme = styleConfig?.theme || QUICK_ENTRY_THEME_VALUES[QUICK_ENTRY_THEME_OPTIONS.THEME_1];
    const isThemeOne = theme === QUICK_ENTRY_THEME_VALUES[QUICK_ENTRY_THEME_OPTIONS.THEME_1];
    const isThemeTwo = theme === QUICK_ENTRY_THEME_VALUES[QUICK_ENTRY_THEME_OPTIONS.THEME_2];
    const isThemeThree = theme === QUICK_ENTRY_THEME_VALUES[QUICK_ENTRY_THEME_OPTIONS.THEME_3];
    // 样式一：图标 + 名称（垂直布局）
    if (isThemeOne) {
      return (
        <div
          key={`${item.entryName}-${item.group}-${index}`}
          className="quick-entry-item quick-entry-item-theme-one"
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
          
          <img src={item.entryIcon || getDefaultIcon(index)} alt={item.entryName} className="quick-entry-item-icon-image" />
          <div className="quick-entry-item-title">{item.entryName}</div>
        </div>
      );
    }

    // 样式二和样式三：图标 + 名称 + 描述（水平布局）
    const backgroundColor = isThemeThree
      ? THEME_THREE_COLORS[index % THEME_THREE_COLORS.length]
      : undefined;

    return (
      <div
        key={`${item.entryName}-${item.group}-${index}`}
        className={`quick-entry-item ${
          isThemeTwo ? 'quick-entry-item-theme-two' : 'quick-entry-item-theme-three'
        }`}
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
          cursor: runtime && (item.linkAddress || item.menuId) ? 'pointer' : 'default',
          backgroundColor
        }}
      >
        <img src={item.entryIcon || getDefaultIcon(index)} alt={item.entryName} className="quick-entry-item-icon-image" />
        <div className="quick-entry-item-meta">
          <div className="quick-entry-item-title">{item.entryName}</div>
          {item.entryDesc && <div className="quick-entry-item-desc">{item.entryDesc}</div>}
        </div>
      </div>
    );
  };

  const renderContent = () => {
    // 收集所有入口项，用于计算全局索引
    const allItems = groups.flatMap((group) => group.entries ?? []);
    let globalIndex = 0;

    if (enableGroup && groups.length > 0) {
      return (
        <Tabs defaultActiveTab={groups[0]?.groupName || `group-0`}>
          {groups.map((group, groupIndex) => (
            <div className="quick-entry-items" key={groupIndex}>
                {group.entries?.map((item) => {
                  const currentIndex = globalIndex++;
                  return renderEntryItem(item, currentIndex);
                })}
              </div>
          ))}
        </Tabs>
      );
    } else {
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
          {titleConfig?.showMore && <span className="quick-entry-more">更多 <IconArrowIn /></span>}
        </div>
      )}
      {renderContent()}
    </div>
  );
});

export default XQuickEntry;
