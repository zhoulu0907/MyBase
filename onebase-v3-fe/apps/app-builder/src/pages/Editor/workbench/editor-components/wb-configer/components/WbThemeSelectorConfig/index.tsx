import { Button, Grid } from '@arco-design/web-react';
import { IconSwap } from '@arco-design/web-react/icon';
import { useState, useCallback, useMemo } from 'react';
import ConfigDrawer from '@/pages/Editor/workbench/components/configDrawer';
import { WORKBENCH_CONFIG_TYPES, WORKBENCH_THEME_OPTIONS } from '@onebase/ui-kit';
import { registerConfigRenderer } from '../../registry';
import styles from './index.module.less';

const Row = Grid.Row;

export type RenderPreviewCardFn = (
  theme: string,
  isShowActive: boolean,
  currentTheme: string,
  onThemeChange: (theme: string) => void
) => React.ReactNode;

export interface WbStyleLibraryConfigProps {
  handlePropsChange: (key: string, value: unknown) => void;
  item: { key: string; meta?: { renderPreviewCard?: RenderPreviewCardFn } };
  configs: Record<string, unknown>;
  renderPreviewCard?: RenderPreviewCardFn; // 如果通过 props 传入，优先使用 props
  styleOptions?: string[]; // 默认 [THEME_1, THEME_2, THEME_3]
  drawerTitle?: string; // 默认 "切换外观样式"
}

const WbThemeSelectorConfig = ({
  handlePropsChange,
  item,
  configs,
  renderPreviewCard: renderPreviewCardFromProps,
  styleOptions = [WORKBENCH_THEME_OPTIONS.THEME_1, WORKBENCH_THEME_OPTIONS.THEME_2, WORKBENCH_THEME_OPTIONS.THEME_3],
  drawerTitle = '切换外观样式'
}: WbStyleLibraryConfigProps) => {
  const [drawerVisible, setDrawerVisible] = useState(false);

  // 优先使用 props 传入的 renderPreviewCard，其次使用 item.meta 中的
  // 如果都没有传入，返回"未传入配置"提示
  const renderPreviewCard =
    renderPreviewCardFromProps ||
    item.meta?.renderPreviewCard ||
    (() => <div style={{ padding: '20px', textAlign: 'center', color: '#86909c' }}>未传入配置</div>);

  // 兼容两种数据结构：
  const itemKey = item?.key;

  const currentTheme = useMemo(() => {
    if (!itemKey) {
      return WORKBENCH_THEME_OPTIONS.THEME_1;
    }
    const value = configs?.[itemKey];
    if (typeof value === 'string') {
      // 如果是字符串，直接使用
      return value || WORKBENCH_THEME_OPTIONS.THEME_1;
    } else if (value && typeof value === 'object' && 'theme' in value) {
      // 如果是对象，取 theme 属性
      return (value as { theme?: string }).theme || WORKBENCH_THEME_OPTIONS.THEME_1;
    }
    return WORKBENCH_THEME_OPTIONS.THEME_1;
  }, [configs, itemKey]);

  const handleThemeChange = useCallback(
    (theme: string) => {
      if (currentTheme === theme) return;
      if (!itemKey) return;
      const currentValue = configs?.[itemKey];
      if (typeof currentValue === 'string') {
        handlePropsChange(itemKey, theme);
      } else {
        handlePropsChange(itemKey, { ...(currentValue as object), theme });
      }
      setDrawerVisible(false);
    },
    [currentTheme, configs, handlePropsChange, itemKey]
  );

  // 样式库抽屉内容
  const renderDrawerContent = () => (
    <div className={styles.drawerContent}>
      {styleOptions.map((theme) => (
        <div key={theme}>{renderPreviewCard(theme, true, currentTheme, handleThemeChange)}</div>
      ))}
    </div>
  );

  return (
    <div className={styles.styleLibrary}>
      <div className={styles.styleOptions}>
        <Row gutter={[8, 8]}>{renderPreviewCard(currentTheme, false, currentTheme, handleThemeChange)}</Row>
      </div>
      <Button
        type="outline"
        className={styles.changeStyleBtn}
        icon={<IconSwap />}
        onClick={() => setDrawerVisible(true)}
      >
        更改样式
      </Button>
      <ConfigDrawer visible={drawerVisible} title={drawerTitle} onClose={() => setDrawerVisible(false)}>
        {renderDrawerContent()}
      </ConfigDrawer>
    </div>
  );
};

export default WbThemeSelectorConfig;

registerConfigRenderer(WORKBENCH_CONFIG_TYPES.WB_THEME_SELECTOR, ({ handlePropsChange, item, configs }) => (
  <WbThemeSelectorConfig handlePropsChange={handlePropsChange} item={item} configs={configs} />
));
