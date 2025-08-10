import React from 'react';
import styles from './index.module.less';

interface TelegramContainerProps {}

const TelegramContainer: React.FC<TelegramContainerProps> = ({}) => {
  const { t } = useI18n();

  return (
    <div>
      <div className={styles.rightHeader}>{t('editor.telegram')}</div>

      <div className={styles.rightBody}></div>
    </div>
  );
};

export default TelegramContainer;
