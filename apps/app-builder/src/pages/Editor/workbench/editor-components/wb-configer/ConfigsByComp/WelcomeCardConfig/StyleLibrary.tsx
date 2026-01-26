import dayjs from 'dayjs';
import { WORKBENCH_THEME_OPTIONS } from '@onebase/ui-kit';
import { getWorkbenchRuntimeUserInfo } from '@onebase/ui-kit/src/components/Materials/Workbench/utils/user-avatar';
import WbThemeSelectorConfig from '../../components/WbThemeSelectorConfig';
import commonStyles from '../../components/WbThemeSelectorConfig/index.module.less';
import styles from './index.module.less';

import theme1Image from '@onebase/ui-kit/src/assets/workbench/welcome-card/theme1.svg';
import theme2Image from '@onebase/ui-kit/src/assets/workbench/welcome-card/theme2.svg';
import theme3Image from '@onebase/ui-kit/src/assets/workbench/welcome-card/theme3.svg';

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
      [WORKBENCH_THEME_OPTIONS.THEME_2]: theme2Image,
      [WORKBENCH_THEME_OPTIONS.THEME_3]: theme3Image
    };

    const previewImage = imageMap[theme as keyof typeof imageMap] || theme1Image;

    // 从 configs 中获取数据，提供默认值
    const runtimeUserInfo = getWorkbenchRuntimeUserInfo({
      avatar: configs.userAvatar as string | undefined,
      name: configs.userName as string | undefined
    });
    const userName = runtimeUserInfo.name || '用户';
    const welcomeText = '下午好！';
    const welcomeDesc = '开心工作，认真生活';
    const userAvatar = runtimeUserInfo.avatar || undefined;
    const currentTime = dayjs().format('YYYY-MM-DD HH:mm:ss');

    const isTheme3 = theme === WORKBENCH_THEME_OPTIONS.THEME_3;

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
        <div
          className={
            styles.previewContentContainer +
            ' ' +
            (isTheme3 ? styles.previewCardTheme3 : '') +
            ' ' +
            (isTheme3 ? styles.previewCardHeightTheme3 : styles.previewCardHeight)
          }
        >
          {/* 背景图片 */}
          <img src={previewImage} alt={`Theme ${theme}`} className={styles.previewBackgroundImage} />

          {/* 内容区域 */}
          <div className={styles.previewContentWrapper}>
            {userAvatar ? (
              <img src={userAvatar} alt="userAvatar" className={styles.previewUserAvatarImg} />
            ) : (
              <div className={styles.previewUserAvatarPlaceholder}>{userName.charAt(0)}</div>
            )}
            <div className={styles.previewTextContainer}>
              <div
                className={
                  styles.previewTextEllipsis +
                  ' ' +
                  (isTheme3 ? styles.previewWelcomeTitleTheme3 : styles.previewWelcomeTitle)
                }
              >
                {userName ? userName + '，' : ''} {welcomeText || ''}
              </div>
              {!isTheme3 && (
                <div className={styles.previewTextEllipsis + ' ' + styles.previewWelcomeDesc}>{welcomeDesc}</div>
              )}
            </div>
          </div>

          {/* THEME_3 特有的内容 */}
          {isTheme3 && (
            <>
              <div className={styles.previewWelcomeDescTheme3}>{welcomeDesc}</div>
              <div className={styles.previewCurrentTime}>当前时间：{currentTime}</div>
            </>
          )}
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
    />
  );
}
