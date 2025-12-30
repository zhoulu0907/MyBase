import type { CSSProperties } from 'react';
import { memo, useMemo } from 'react';
import { WORKBENCH_STATUS_OPTIONS, WORKBENCH_STATUS_VALUES, WORKBENCH_THEME_OPTIONS } from '../../core/constants';
import type { XWelcomeCardConfig } from './schema';
import styles from './index.module.css';

import theme1Image from '@/assets/workbench/welcome-card/theme1.svg';
import theme2Image from '@/assets/workbench/welcome-card/theme2.svg';
import theme3Image from '@/assets/workbench/welcome-card/theme3.svg';

const containerStyle: CSSProperties = {
  width: '100%',
  padding: '16px',
  borderRadius: 8,
  boxSizing: 'border-box'
};

const XWelcomeCard = memo((props: XWelcomeCardConfig & { runtime?: boolean }) => {
  const { status, runtime, theme, userAvatar, userName, welcomeText, welcomeDesc } = props;
  const hiddenStatusValue = WORKBENCH_STATUS_VALUES[WORKBENCH_STATUS_OPTIONS.HIDDEN];

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

  return (
    <div style={containerStyle}>
      <div className={styles.welcomeCard} style={{ height: cardHeight }}>
        <img src={backgroundImage} alt="background" className={styles.backgroundImage} />
        {userAvatar ? (
          <img src={userAvatar} alt="userAvatar" className={styles.userAvatar} />
        ) : (
          <div className={styles.userAvatar}>{userName.charAt(0)}</div>
        )}

        <div className={styles.welcomeCardContent}>
          <div className={styles.welcomeCardTitle}>{userName ? userName + '，' : ''} {welcomeText ? welcomeText : ''}</div>
          <div className={styles.welcomeCardDesc}>{welcomeDesc}</div>
        </div>
      </div>
    </div>
  );
});

export default XWelcomeCard;

