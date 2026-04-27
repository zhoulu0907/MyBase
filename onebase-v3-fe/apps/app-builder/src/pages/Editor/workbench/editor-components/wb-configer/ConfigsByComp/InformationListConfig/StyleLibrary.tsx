import { WORKBENCH_THEME_OPTIONS } from '@onebase/ui-kit';
import WbThemeSelectorConfig from '../../components/WbThemeSelectorConfig';
import commonStyles from '../../components/WbThemeSelectorConfig/index.module.less';

import theme1Image from '@/assets/workbench/information-list/theme1.png';
import theme2Image from '@/assets/workbench/information-list/theme2.png';

interface StyleLibraryProps {
  handlePropsChange: (key: string, value: unknown) => void;
  item: { key: string };
  configs: Record<string, unknown>;
}

export function StyleLibrary({ handlePropsChange, item, configs }: StyleLibraryProps) {
  const renderPreviewCard = (
    theme: string,
    isShowActive: boolean,
    currentTheme: string,
    onThemeChange: (theme: string) => void
  ) => {
    const imageMap = {
      [WORKBENCH_THEME_OPTIONS.THEME_1]: theme1Image,
      [WORKBENCH_THEME_OPTIONS.THEME_2]: theme2Image
    };

    return (
      <div
        className={
          commonStyles.previewCardFirst +
          ' ' +
          commonStyles.previewCardContainer +
          ' ' +
          (isShowActive && commonStyles.previewCardClick) +
          ' ' +
          (currentTheme === theme && isShowActive && commonStyles.previewCardActive)
        }
        onClick={() => onThemeChange(theme)}
      >
        <img src={imageMap[theme as keyof typeof imageMap]} alt={`Theme ${theme}`} width="100%" />
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
