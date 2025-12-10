import { Button, Grid } from '@arco-design/web-react';
import { useSignals } from '@preact/signals-react/runtime';
import { useState } from 'react';
import { IconSwap } from '@arco-design/web-react/icon';
import IconEntry1 from '@/assets/workbench/quick-entry/entry1.svg';
import IconEntry2 from '@/assets/workbench/quick-entry/entry2.svg';
import IconEntry3 from '@/assets/workbench/quick-entry/entry3.svg';
import IconEntry4 from '@/assets/workbench/quick-entry/entry4.svg';
import IconEntry5 from '@/assets/workbench/quick-entry/entry5.svg';
import IconEntry6 from '@/assets/workbench/quick-entry/entry6.svg';
import arcoPalette from '@/constants/arco-palette.json';
import ConfigDrawer from '@/pages/Editor/workbench/components/configDrawer';
import { useQuickEntrySection } from '../hooks/useQuickEntrySection';
import type { QuickEntryStyleConfig } from '../types';
import styles from './index.module.less';

const Row = Grid.Row;
const Col = Grid.Col;
const THEME_OPTIONS = {
  THEME_1: 'theme-one',
  THEME_2: 'theme-two',
  THEME_3: 'theme-three'
};
interface StyleLibraryProps {
  cpID: string;
}

const DEFAULT_STYLE_CONFIG: QuickEntryStyleConfig = { theme: THEME_OPTIONS.THEME_1 };

const StyleLibrary = ({ cpID }: StyleLibraryProps) => {
  useSignals();

  const [styleConfig, updateStyleConfig] = useQuickEntrySection(cpID, 'styleConfig', DEFAULT_STYLE_CONFIG);
  const [drawerVisible, setDrawerVisible] = useState(false);

  const currentTheme = styleConfig.theme || THEME_OPTIONS.THEME_1;

  const handleThemeChange = (theme: string) => {
    if (currentTheme === theme) {
      return;
    }
    updateStyleConfig({ theme });
  };

  const entryOptions = [
    { label: '名称一', icon: IconEntry1, desc: '辅助描述文案' },
    { label: '名称二', icon: IconEntry2, desc: '辅助描述文案' },
    { label: '名称三', icon: IconEntry3, desc: '辅助描述文案' },
    { label: '名称四', icon: IconEntry4, desc: '辅助描述文案' },
    { label: '名称五', icon: IconEntry5, desc: '辅助描述文案' },
    { label: '名称六', icon: IconEntry6, desc: '辅助描述文案' }
  ];

  const renderPreviewCard = (theme: string, isShowActive = true) => {
    switch (theme) {
      // 样式一
      case THEME_OPTIONS.THEME_1:
        return (
          <div
            className={
              styles.previewCardFirst +
              ' ' +
              (isShowActive && styles.previewCardClick) +
              ' ' +
              (currentTheme === THEME_OPTIONS.THEME_1 && isShowActive && styles.previewCardActive)
            }
            onClick={() => handleThemeChange(THEME_OPTIONS.THEME_1)}
          >
            <div className={styles.previewCardInner}>
              {entryOptions.map((item) => (
                <div key={item.label} className={styles.previewItemFirst}>
                  <img src={item.icon} alt={item.label} className={styles.previewIcon} />
                  <span className={styles.previewIconLabel}>{item.label}</span>
                </div>
              ))}
            </div>
          </div>
        );

      // 样式二
      case THEME_OPTIONS.THEME_2:
        return (
          <div
            className={
              styles.previewCardSecond +
              ' ' +
              (isShowActive && styles.previewCardClick) +
              ' ' +
              (currentTheme === THEME_OPTIONS.THEME_2 && isShowActive && styles.previewCardActive)
            }
            onClick={() => handleThemeChange(THEME_OPTIONS.THEME_2)}
          >
            <div className={styles.previewCardInner}>
              {entryOptions.slice(0, 2).map((card) => (
                <div key={card.label} className={styles.previewItemSecond}>
                  <img src={card.icon} alt={card.label} className={styles.previewIcon} />
                  <div className={styles.previewMeta}>
                    <div className={styles.previewTitle}>{card.label}</div>
                    <div className={styles.previewDesc}>{card.desc}</div>
                  </div>
                </div>
              ))}
            </div>
          </div>
        );

      // 样式三
      case THEME_OPTIONS.THEME_3:
        return (
          <div
            className={
              styles.previewCardThird +
              ' ' +
              (isShowActive && styles.previewCardClick) +
              ' ' +
              (currentTheme === THEME_OPTIONS.THEME_3 && isShowActive && styles.previewCardActive)
            }
            onClick={() => handleThemeChange(THEME_OPTIONS.THEME_3)}
          >
            <div className={styles.previewCardInner}>
              {entryOptions.slice(0, 2).map((card, index) => (
                <div
                  key={card.label}
                  className={styles.previewItemThird}
                  style={{ backgroundColor: Object.values(arcoPalette.primary)[index] + '20' }}
                >
                  <img src={card.icon} alt={card.label} className={styles.previewIcon} />
                  <div className={styles.previewMeta}>
                    <div className={styles.previewTitle}>{card.label}</div>
                    <div className={styles.previewDesc}>{card.desc}</div>
                  </div>
                </div>
              ))}
            </div>
          </div>
        );
    }
  };

  // 样式库抽屉内容
  const renderDrawerContent = () => (
    <div className={styles.drawerContent}>
      {renderPreviewCard(THEME_OPTIONS.THEME_1)}
      {renderPreviewCard(THEME_OPTIONS.THEME_2)}
      {renderPreviewCard(THEME_OPTIONS.THEME_3)}
    </div>
  );

  return (
    <div className={styles.styleLibrary}>
      <div className={styles.styleOptions}>
        <Row gutter={[8, 8]}>{renderPreviewCard(currentTheme, false)}</Row>
      </div>
      <Button
        type="outline"
        className={styles.changeStyleBtn}
        icon={<IconSwap />}
        onClick={() => setDrawerVisible(true)}
      >
        更改样式
      </Button>
      <ConfigDrawer visible={drawerVisible} title="切换外观样式" onClose={() => setDrawerVisible(false)}>
        {renderDrawerContent()}
      </ConfigDrawer>
    </div>
  );
};

export default StyleLibrary;
