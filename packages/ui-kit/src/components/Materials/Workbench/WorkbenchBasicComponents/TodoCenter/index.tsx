import { Alert } from '@arco-design/web-react';
import type { CSSProperties } from 'react';
import { memo } from 'react';
import { WORKBENCH_STATUS_OPTIONS, WORKBENCH_STATUS_VALUES, WORKBENCH_THEME_OPTIONS, DATA_CONFIG_NAME_MAP } from '../../core/constants';
import type { XTodoCenterConfig } from './schema';
import styles from './index.module.css';

import showCc from '@/assets/workbench/todo-center/showCcIcon.svg';
import showCreated from '@/assets/workbench/todo-center/showCreatedIcon.svg';
import showHandled from '@/assets/workbench/todo-center/showHandledIcon.svg';
import showPending from '@/assets/workbench/todo-center/showPendingIcon.svg';

// 图标映射配置
const ICON_MAP: Record<string, string> = {
  showCc,
  showCreated,
  showHandled,
  showPending
};

const containerStyle: CSSProperties = {
  width: '100%',
  minHeight: 120,
  borderRadius: 8,
  padding: 16,
  boxSizing: 'border-box'
};

const XTodoCenter = memo((props: XTodoCenterConfig & { runtime?: boolean }) => {
  const { label, dataConfig, theme, status, runtime } = props;
  const hiddenStatusValue = WORKBENCH_STATUS_VALUES[WORKBENCH_STATUS_OPTIONS.HIDDEN];

  if (runtime && status === hiddenStatusValue) {
    return null;
  }

  return (
    <div style={containerStyle}>

      <div className={styles.todoCenterHeader}>
        {label?.display && (
          <span className={styles.todoCenterHeaderTitle}>{label?.text}</span>
        )}
      </div>

      <div className={styles.todoCenterContent}>
        {Object.entries(dataConfig).map(([key, value]: [string, boolean]) => (
          <div key={key} className={styles.todoCenterContentItem} style={{ backgroundColor: theme === WORKBENCH_THEME_OPTIONS.THEME_1 ? '#F2F3F5' : '#f5f6f8' }}>
            <div className={styles.todoCenterContentItemLeft}>
              <div className={styles.todoCenterContentItemTitle}>{DATA_CONFIG_NAME_MAP[key] || key}</div>
              <div className={styles.todoCenterContentItemValue}>123</div>
            </div>

            <div className={styles.todoCenterContentItemRight} style={{ backgroundColor: theme === WORKBENCH_THEME_OPTIONS.THEME_1 ? 'rgba(var(--primary-6))' : '#f5f6f8' }}>
              <img src={ICON_MAP[key]} alt={key} height={28} />
            </div>
          </div>
        ))}
      </div>
    </div>
  );
});

export default XTodoCenter;

