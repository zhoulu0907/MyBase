import IconEntry1 from '@/assets/workbench/quick-entry/entry1.svg';
import IconEntry2 from '@/assets/workbench/quick-entry/entry2.svg';
import IconEntry3 from '@/assets/workbench/quick-entry/entry3.svg';
import IconEntry4 from '@/assets/workbench/quick-entry/entry4.svg';
import IconEntry5 from '@/assets/workbench/quick-entry/entry5.svg';
import IconEntry6 from '@/assets/workbench/quick-entry/entry6.svg';
import arcoPalette from '@/constants/arco-palette.json';
import { WORKBENCH_THEME_OPTIONS } from '@onebase/ui-kit';
import WbThemeSelectorConfig from '../../components/WbThemeSelectorConfig';
import styles from '../../components/WbThemeSelectorConfig/index.module.less';

interface StyleLibraryProps {
  handlePropsChange: (key: string, value: unknown) => void;
  item: { key: string };
  configs: Record<string, unknown>;
}

const entryOptions = [
  { label: '名称一', icon: IconEntry1, desc: '辅助描述文案' },
  { label: '名称二', icon: IconEntry2, desc: '辅助描述文案' },
  { label: '名称三', icon: IconEntry3, desc: '辅助描述文案' },
  { label: '名称四', icon: IconEntry4, desc: '辅助描述文案' },
  { label: '名称五', icon: IconEntry5, desc: '辅助描述文案' },
  { label: '名称六', icon: IconEntry6, desc: '辅助描述文案' }
];

export function StyleLibrary({ handlePropsChange, item, configs }: StyleLibraryProps) {
  const renderPreviewCard = (
    theme: string,
    isShowActive: boolean,
    currentTheme: string,
    onThemeChange: (theme: string) => void
  ) => {
    switch (theme) {
      // 样式一
      case WORKBENCH_THEME_OPTIONS.THEME_1:
        return (
          <div
            className={
              styles.previewCardFirst +
              ' ' +
              (isShowActive && styles.previewCardClick) +
              ' ' +
              (currentTheme === WORKBENCH_THEME_OPTIONS.THEME_1 && isShowActive && styles.previewCardActive)
            }
            onClick={() => onThemeChange(WORKBENCH_THEME_OPTIONS.THEME_1)}
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
      case WORKBENCH_THEME_OPTIONS.THEME_2:
        return (
          <div
            className={
              styles.previewCardSecond +
              ' ' +
              (isShowActive && styles.previewCardClick) +
              ' ' +
              (currentTheme === WORKBENCH_THEME_OPTIONS.THEME_2 && isShowActive && styles.previewCardActive)
            }
            onClick={() => onThemeChange(WORKBENCH_THEME_OPTIONS.THEME_2)}
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
      case WORKBENCH_THEME_OPTIONS.THEME_3:
        return (
          <div
            className={
              styles.previewCardThird +
              ' ' +
              (isShowActive && styles.previewCardClick) +
              ' ' +
              (currentTheme === WORKBENCH_THEME_OPTIONS.THEME_3 && isShowActive && styles.previewCardActive)
            }
            onClick={() => onThemeChange(WORKBENCH_THEME_OPTIONS.THEME_3)}
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
      default:
        return null;
    }
  };

  return (
    <WbThemeSelectorConfig
      handlePropsChange={handlePropsChange}
      item={item}
      configs={configs}
      renderPreviewCard={renderPreviewCard}
    />
  );
}
