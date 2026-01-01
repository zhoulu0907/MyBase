import { Tabs } from '@arco-design/mobile-react';
import { IconArrowIn } from '@arco-design/mobile-react/esm/icon';
import type { CSSProperties } from 'react';
import { ReactSVG } from 'react-svg';
import { memo, useMemo } from 'react';
import { mobileMenuIcons } from '@onebase/ui-kit';
import { WORKBENCH_STATUS_OPTIONS, WORKBENCH_STATUS_VALUES, QUICK_ENTRY_THEME_OPTIONS, QUICK_ENTRY_THEME_VALUES, workbenchSchema } from '@onebase/ui-kit';
import type { QuickEntryTitleConfig, QuickEntryStyleConfig, QuickEntryGroupConfig } from '@onebase/ui-kit';
import { getDefaultIcon } from './getDefaultIcon';
import { useJump } from '../../hooks/useJump';
import styles from './index.module.css';

type XQuickEntryConfig = typeof workbenchSchema.XQuickEntry.config;

// 主题三使用的颜色数组（对应 arcoPalette.primary 的颜色，添加 20% 透明度）
const THEME_THREE_COLORS = ['#24b28f20', '#eb693a20', '#1979ff20', '#7e5aea20', '#009e9e20', '#ebbc0020'];

const defaultTitleConfig: QuickEntryTitleConfig = {
  showTitle: true,
  titleName: '快捷入口',
  showMore: true,
  enableGroup: false
};

const defaultStyleConfig: QuickEntryStyleConfig = {
  theme: QUICK_ENTRY_THEME_VALUES[QUICK_ENTRY_THEME_OPTIONS.THEME_1]
};

const defaultGroupConfig: QuickEntryGroupConfig = {
  enableGroup: false,
  groups: []
};

const XQuickEntry = memo((props: XQuickEntryConfig & { runtime?: boolean; detailMode?: boolean }) => {
  const { id, status, width, titleConfig, styleConfig, groupConfig, runtime } = props;

  const finalTitleConfig = titleConfig || defaultTitleConfig;
  const finalStyleConfig = styleConfig || defaultStyleConfig;
  const finalGroupConfig = groupConfig || defaultGroupConfig;

  const groups = finalGroupConfig?.groups ?? [];
  const enableGroup = Boolean(finalGroupConfig?.enableGroup);

  // 扁平化所有菜单图标，用于查找
  const allWebMenuIcons = useMemo(
    () => mobileMenuIcons.map((ele: any) => ele.children).reduce((acc: any, current: any) => acc.concat(current), []),
    []
  );

  const { handleJump } = useJump();
  const handleClickEntry = async (item: {
    linkAddress?: string;
    menuUuid?: string;
  }) => {
    await handleJump({
      linkAddress: item.linkAddress,
      menuUuid: item.menuUuid,
      runtime
    });
  };

    // 图标渲染(使用移动端菜单图标库)
    const getSvgIcon = (icon: string, index: number) => {
      const bgColor = THEME_THREE_COLORS[index % THEME_THREE_COLORS.length].slice(0, 7);
      const iconSrc =
        allWebMenuIcons.find((ele: any) => ele.code === icon)?.icon ||
        '';
  
      // 如果找不到图标或图标为空，使用默认图标
      if (!icon || !iconSrc) {
        return (
          <img
            src={getDefaultIcon(index)}
            alt=""
            className={styles.quickEntryItemIconImage}
          />
        );
      }

      return (
        <ReactSVG
          className={styles.quickEntryItemIcon}
          src={iconSrc}
          style={{
            backgroundColor: bgColor
          }}
          beforeInjection={(svg: SVGElement) => {
            svg.querySelectorAll('*').forEach((el: Element) => el.removeAttribute('fill'));
            svg.setAttribute('fill', 'white');
            svg.setAttribute('width', '24px');
            svg.setAttribute('height', '24px');
          }}
        />
      );
    };

  const renderEntryItem = (
    item: {
      entryName: string;
      entryIcon?: string;
      entryType?: string;
      linkAddress?: string;
      menuUuid?: string;
      group?: string;
      entryDesc?: string;
    },
    index: number
  ) => {
    const theme = finalStyleConfig?.theme || QUICK_ENTRY_THEME_VALUES[QUICK_ENTRY_THEME_OPTIONS.THEME_1];
    const isThemeOne = theme === QUICK_ENTRY_THEME_VALUES[QUICK_ENTRY_THEME_OPTIONS.THEME_1];
    const isThemeTwo = theme === QUICK_ENTRY_THEME_VALUES[QUICK_ENTRY_THEME_OPTIONS.THEME_2];
    const isThemeThree = theme === QUICK_ENTRY_THEME_VALUES[QUICK_ENTRY_THEME_OPTIONS.THEME_3];
    // 样式一：图标 + 名称（垂直布局）
    if (isThemeOne) {
      return (
        <div
          key={`${item.entryName}-${item.group}-${index}`}
          className={`${styles.quickEntryItem} ${styles.quickEntryItemThemeOne}`}
          onClick={() => handleClickEntry(item)}
          style={{
            pointerEvents: runtime ? 'unset' : 'none',
            cursor: runtime && (item.linkAddress || item.menuUuid) ? 'pointer' : 'default'
          }}
        >
          {getSvgIcon(item.entryIcon || '', index)}
          <div className={styles.quickEntryItemTitle}>{item.entryName}</div>
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
        className={`${styles.quickEntryItem} ${
          isThemeTwo ? styles.quickEntryItemThemeTwo : styles.quickEntryItemThemeThree
        }`}
        onClick={() => handleClickEntry(item)}
        style={{
          pointerEvents: runtime ? 'unset' : 'none',
          cursor: runtime && (item.linkAddress || item.menuUuid) ? 'pointer' : 'default',
          backgroundColor
        }}
      >
        {getSvgIcon(item.entryIcon || '', index)}
        <div className={styles.quickEntryItemMeta}>
          <div className={styles.quickEntryItemTitle}>{item.entryName}</div>
          {item.entryDesc && <div className={styles.quickEntryItemDesc}>{item.entryDesc}</div>}
        </div>
      </div>
    );
  };

  const renderContent = () => {
    // 收集所有入口项，用于计算全局索引
    const allItems = groups.flatMap((group) => group.entries ?? []);
    let globalIndex = 0;

    if (enableGroup && groups.length > 0) {
      const tabs = groups.map((group) => ({
        title: group.groupName || '',
        key: group.groupName || ''
      }));
      
      return (
        <Tabs defaultActiveTab={0} tabs={tabs}>
          {groups.map((group, groupIndex) => (
            <div className={styles.quickEntryItems} key={group.groupName || `group-${groupIndex}`}>
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
        <div className={styles.quickEntryItems}>
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

  const themeClass = normalizeThemeClass(finalStyleConfig?.theme);
  const statusValueHidden = WORKBENCH_STATUS_VALUES[WORKBENCH_STATUS_OPTIONS.HIDDEN];
  const isHidden = status === statusValueHidden;
  const containerStyle: CSSProperties = {
    // width: width || '100%',
    opacity: isHidden ? 0.4 : 1
  };

  if (runtime && isHidden) {
    return null;
  }

  const themeClassMap: Record<string, string> = {
    'theme-one': styles.quickEntryThemeOne,
    'theme-two': styles.quickEntryThemeTwo,
    'theme-three': styles.quickEntryThemeThree
  };

  return (
    <div
      className={`${styles.quickEntry} ${themeClass ? themeClassMap[themeClass] || '' : ''}`}
      style={containerStyle}
    >
      {(finalTitleConfig?.showTitle || finalTitleConfig?.showMore) && (
        <div className={styles.quickEntryHeader}>
          {finalTitleConfig?.showTitle && (
            <span className={styles.quickEntryHeaderTitle}>{finalTitleConfig?.titleName || '快捷入口'}</span>
          )}
          {finalTitleConfig?.showMore && (
            <span className={styles.quickEntryMore}>
              更多 <IconArrowIn />
            </span>
          )}
        </div>
      )}
      {renderContent()}
    </div>
  );
});

export default XQuickEntry;
