import { Tabs } from '@arco-design/web-react';
import { IconRight } from '@arco-design/web-react/icon';
import type { CSSProperties } from 'react';
import { memo, useMemo, useState } from 'react';
import { ReactSVG } from 'react-svg';
import { WORKBENCH_STATUS_OPTIONS, WORKBENCH_STATUS_VALUES, WORKBENCH_THEME_OPTIONS } from '../../core/constants';
import type { QuickEntryStyleConfig, QuickEntryGroupConfig } from '../../core/types';
import { mobileMenuIcons } from '@/utils/menuIcons';
import { type XQuickEntryConfig } from './schema';
import { getDefaultIcon } from './getDefaultIcon';
import { useJump } from '../../hooks/useJump';
import styles from './index.module.css';
import CheckMoreModal from './checkMoreModal';

// 主题三使用的颜色数组（对应 arcoPalette.primary 的颜色，添加 20% 透明度）
const THEME_THREE_COLORS = ['#24b28f20', '#eb693a20', '#1979ff20', '#7e5aea20', '#009e9e20', '#ebbc0020'];

const defaultLabel: XQuickEntryConfig['label'] = {
  text: '快捷入口',
  display: true
};

const defaultTitleConfig: XQuickEntryConfig['titleConfig'] = {
  showMore: true,
  jumpType: 'internal',
  jumpPageId: '',
  jumpExternalUrl: '',
  enableGroup: false
};

const defaultStyleConfig: QuickEntryStyleConfig = {
  theme: WORKBENCH_THEME_OPTIONS.THEME_1
};

const defaultGroupConfig: QuickEntryGroupConfig = {
  enableGroup: false,
  groups: []
};

const XQuickEntry = memo((props: XQuickEntryConfig & { runtime?: boolean; preview?: boolean }) => {
  const { id, status, width, label, titleConfig, styleConfig, groupConfig, runtime, preview } = props;
  const { handleJump } = useJump();
  const [moreModalVisible, setMoreModalVisible] = useState(false);

  const finalLabel = label || defaultLabel;
  const finalTitleConfig = titleConfig || defaultTitleConfig;
  const finalStyleConfig = styleConfig || defaultStyleConfig;
  const finalGroupConfig = groupConfig || defaultGroupConfig;

  const groups = finalGroupConfig?.groups ?? [];
  const enableGroup = Boolean(finalGroupConfig?.enableGroup ?? finalTitleConfig?.enableGroup);

  // 扁平化所有菜单图标，用于查找
  const allWebMenuIcons = useMemo(
    () => mobileMenuIcons.map((ele) => ele.children).reduce((acc, current) => acc.concat(current), []),
    []
  );

  const handleClickEntry = (item: {
    linkAddress?: string;
    menuUuid?: string;
  }, closeModal?: boolean) => {
    if (runtime && (item.linkAddress || item.menuUuid)) {
      handleJump({
        menuUuid: item.menuUuid,
        linkAddress: item.linkAddress,
        runtime,
      });
      // 如果是在弹窗中点击，关闭弹窗
      if (closeModal) {
        setMoreModalVisible(false);
      }
    }
  };

  // 图标渲染(使用移动端菜单图标库)
  const getSvgIcon = (icon: string, index: number) => {
    const bgColor = THEME_THREE_COLORS[index % THEME_THREE_COLORS.length].slice(0, 7);
    const iconSrc =
      allWebMenuIcons.find((ele) => ele.code === icon)?.icon ||
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
    index: number,
    closeModalOnClick?: boolean
  ) => {
    const theme = finalStyleConfig?.theme || WORKBENCH_THEME_OPTIONS.THEME_1;
    const isThemeOne = theme === WORKBENCH_THEME_OPTIONS.THEME_1;
    const isThemeTwo = theme === WORKBENCH_THEME_OPTIONS.THEME_2;
    const isThemeThree = theme === WORKBENCH_THEME_OPTIONS.THEME_3;

    // 样式一：图标 + 名称（垂直布局）
    if (isThemeOne) {
      return (
        <div
          key={`${item.entryName}-${item.group}-${index}`}
          className={`${styles.quickEntryItem} ${styles.quickEntryItemThemeOne}`}
          onClick={() => handleClickEntry(item, closeModalOnClick)}
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
        className={`${styles.quickEntryItem} ${isThemeTwo ? styles.quickEntryItemThemeTwo : styles.quickEntryItemThemeThree}`}
        onClick={() => handleClickEntry(item, closeModalOnClick)}
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

  const renderContent = (showAll: boolean = false) => {
    // 收集所有入口项，用于计算全局索引
    // 若配置查看更多，则只展示前4个入口项（弹窗中展示全部）
    // 20260317更新：入口全部展示
    const showMoreCount = showAll ? undefined : (finalTitleConfig?.showMore ? 4 : undefined);
    const allItems = showMoreCount
      ? groups.flatMap((group) => group.entries ?? []).slice(0, showMoreCount)
      : groups.flatMap((group) => group.entries ?? []);
    let globalIndex = 0;
    // 弹窗中的入口项点击后需要关闭弹窗
    const closeModalOnClick = showAll;

    if (enableGroup && groups.length > 0) {
      return (
        <Tabs defaultActiveTab={groups[0]?.groupName || `group-0`}>
          {groups.map((group, groupIndex) => (
            <Tabs.TabPane key={group.groupName || `group-${groupIndex}`} title={group.groupName}>
              <div className={styles.quickEntryItems}>
                {(showMoreCount ? group.entries?.slice(0, showMoreCount) : group.entries)?.map((item) => {
                  const currentIndex = globalIndex++;
                  return renderEntryItem(item, currentIndex, closeModalOnClick);
                })}
              </div>
            </Tabs.TabPane>
          ))}
        </Tabs>
      );
    } else {
      return (
        <div className={styles.quickEntryItems}>
          {allItems.map((item, index) => renderEntryItem(item, index, closeModalOnClick))}
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
    opacity: isHidden ? 0.4 : 1,
    height: '100%'
  };

  if (runtime && isHidden) {
    return null;
  }

  const themeClassMap: Record<string, string> = {
    'theme-one': styles.quickEntryThemeOne,
    'theme-two': styles.quickEntryThemeTwo,
    'theme-three': styles.quickEntryThemeThree
  };

  const handleClickMore = () => {
    // 20260317更新：查看更多由弹出弹窗改为跳转至用户配置的链接
    if (!runtime) {
      return;
    }
    handleJump({
      menuUuid: finalTitleConfig?.jumpType === 'internal' ? finalTitleConfig?.jumpPageId : undefined,
      linkAddress: finalTitleConfig?.jumpType === 'external' ? finalTitleConfig?.jumpExternalUrl : undefined,
      runtime,
    });
  };
  const handleCloseMoreModal = () => {
    setMoreModalVisible(false);
  };

  return (
    <div
      className={`${styles.quickEntry} ${themeClass ? themeClassMap[themeClass] || '' : ''}`}
      style={containerStyle}
    >
      {(finalLabel?.display || finalTitleConfig?.showMore) && (
        <div className={styles.quickEntryHeader}>
          {finalLabel?.display && (
            <span className={styles.quickEntryHeaderTitle}>{finalLabel?.text || '快捷入口'}</span>
          )}
          {finalTitleConfig?.showMore && (
            <span className={styles.quickEntryMore} onClick={() => handleClickMore()}>
              更多 <IconRight />
            </span>
          )}
        </div>
      )}
      {renderContent(true)}

      {/* <CheckMoreModal
        visible={moreModalVisible}
        onClose={handleCloseMoreModal}
        runtime={runtime}
        preview={preview}
        title={finalLabel?.text}
        contentClassName={`${styles.quickEntryModalContent} ${styles.quickEntry} ${themeClass ? themeClassMap[themeClass] || '' : ''}`}
      >
        {renderContent(true)}
      </CheckMoreModal> */}
    </div>
  );
});

export default XQuickEntry;
