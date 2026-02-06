import type { CSSProperties } from 'react';
import { memo, useMemo, useState, useEffect } from 'react';
import dayjs from 'dayjs';
import { WORKBENCH_STATUS_OPTIONS, WORKBENCH_STATUS_VALUES, WORKBENCH_THEME_OPTIONS } from '../../core/constants';
import { getWorkbenchRuntimeUserInfo } from '../../utils/user-avatar';
import type { XWelcomeCardConfig } from './schema';
import styles from './index.module.css';

import theme1Image from '@/assets/workbench/welcome-card/theme1.svg';
import theme2Image from '@/assets/workbench/welcome-card/theme2.svg';
import theme3Image from '@/assets/workbench/welcome-card/theme3.svg';

const XWelcomeCard = memo((props: XWelcomeCardConfig & { runtime?: boolean }) => {
  const { status, runtime, theme, userAvatar, userName, welcomeText, welcomeDesc } = props;
  
  const runtimeUserInfo = useMemo(
    () => getWorkbenchRuntimeUserInfo({ avatar: userAvatar, name: userName, runtime }),
    [userAvatar, userName, runtime]
  );
  const hiddenStatusValue = WORKBENCH_STATUS_VALUES[WORKBENCH_STATUS_OPTIONS.HIDDEN];
  const [currentTime, setCurrentTime] = useState(() => dayjs().format('YYYY-MM-DD HH:mm:ss'));

  useEffect(() => {
    if (!runtime || theme !== WORKBENCH_THEME_OPTIONS.THEME_3) {
      return;
    }

    const timer = setInterval(() => {
      setCurrentTime(dayjs().format('YYYY-MM-DD HH:mm:ss'));
    }, 1000);

    return () => {
      clearInterval(timer);
    };
  }, [runtime]);

  if (runtime && status === hiddenStatusValue) {
    return null;
  }

  const backgroundImage = useMemo(() => {
    const imageMap = {
      [WORKBENCH_THEME_OPTIONS.THEME_1]: theme1Image,
      [WORKBENCH_THEME_OPTIONS.THEME_2]: theme2Image,
      [WORKBENCH_THEME_OPTIONS.THEME_3]: theme3Image
    };
    return imageMap[theme as keyof typeof imageMap] || theme1Image;
  }, [theme]);

  const cardHeight = theme === WORKBENCH_THEME_OPTIONS.THEME_3 ? '150px' : '96px';
  const ratio = theme === WORKBENCH_THEME_OPTIONS.THEME_3 ? 271 / 150 : 271 / 96; // 保持宽高比

  return (
    <div className={styles.containerStyle}>
      <div className={styles.welcomeCard} style={{ minHeight: cardHeight, aspectRatio: ratio }}>
        <img src={backgroundImage} alt="background" className={styles.backgroundImage} />

        <div className={styles.welcomeCardContentWrapper}>
          {runtimeUserInfo.avatar ? (
            <img src={runtimeUserInfo.avatar} alt="userAvatar" className={styles.userAvatar} style={{backgroundColor: 'transparent'}}/>
          ) : (
            <div className={styles.userAvatar}>{(runtimeUserInfo.name || '').charAt(0)}</div>
          )}

          <div className={styles.welcomeCardContent}>
            <div className={styles.welcomeCardTitle}>
              {(welcomeText?.display && runtimeUserInfo.name) ? runtimeUserInfo.name + '，' : ''}
              {welcomeText?.text || ''}
            </div>
            {theme !== WORKBENCH_THEME_OPTIONS.THEME_3 && <div className={styles.welcomeCardDesc}>{welcomeDesc}</div>}
          </div>
        </div>

        {theme === WORKBENCH_THEME_OPTIONS.THEME_3 && (
          <>
            <div className={styles.welcomeCardDescTheme3}>{welcomeDesc}</div>
            <div className={styles.currentTime}>当前时间：{currentTime}</div>
          </>
        )}
      </div>
    </div>
  );
});

export default XWelcomeCard;

