import type { CSSProperties } from 'react';
import { memo, useEffect, useState } from 'react';
import { getTaskCenterOverview } from '@onebase/app/src/services/app_runtime';
import { TokenManager } from '@onebase/common';
import { WORKBENCH_STATUS_OPTIONS, WORKBENCH_STATUS_VALUES, WORKBENCH_THEME_OPTIONS, DATA_CONFIG_NAME_MAP } from '../../core/constants';
import type { XTodoCenterConfig } from './schema';
import { useJump } from '../../hooks/useJump';
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

const colorList = [
  '#FF7D00',
  '#165DFF',
  '#00B42A',
  '#F53F3F'
];

const defaultOverviewData = {
  todoCount: 123,
  doneCount: 123,
  myCreatedCount: 123,
  ccCount: 123
};

const overviewDataMap = {
  showPending: 'todoCount',
  showCreated: 'myCreatedCount',
  showHandled: 'doneCount',
  showCc: 'ccCount'
};

const XTodoCenter = memo((props: XTodoCenterConfig & { runtime?: boolean }) => {
  const { label, dataConfig, theme, status, runtime } = props;
  const hiddenStatusValue = WORKBENCH_STATUS_VALUES[WORKBENCH_STATUS_OPTIONS.HIDDEN];
  const [overviewData, setOverviewData] = useState<any>(defaultOverviewData);
  const { handleBPMJump } = useJump();

  if (runtime && status === hiddenStatusValue) {
    return null;
  }

  const handleClick = (key: string) => {
    if (!runtime) {
      return;
    }

    handleBPMJump(key);
  };

  useEffect(() => {
    if (runtime) {
      getTaskCenterOverview({ appId: TokenManager.getCurAppId() || '' }).then((res) => {
        if (res) {
          setOverviewData(res);
        }
      });
    }
  }, [runtime]);

  return (
    <div style={containerStyle}>

      <div className={styles.todoCenterHeader}>
        {label?.display && (
          <span className={styles.todoCenterHeaderTitle}>{label?.text}</span>
        )}
      </div>

      <div className={styles.todoCenterContent}>
        {Object.entries(dataConfig).map(([key, value]: [string, boolean], index: number) => (
          value &&
          (<div key={key} className={styles.todoCenterContentItem} style={{ backgroundColor: theme === WORKBENCH_THEME_OPTIONS.THEME_1 ? '#F2F3F5' : colorList[index % colorList.length] + '20', cursor: runtime ? 'pointer' : 'default' }} onClick={() => handleClick(key)}>
            <div className={styles.todoCenterContentItemLeft}>
              <div className={styles.todoCenterContentItemTitle}>{DATA_CONFIG_NAME_MAP[key] || key}</div>
              <div className={styles.todoCenterContentItemValue}>{overviewData[overviewDataMap[key as keyof typeof overviewDataMap]]}</div>
            </div>

            <div className={styles.todoCenterContentItemRight} style={{ backgroundColor: theme === WORKBENCH_THEME_OPTIONS.THEME_1 ? 'rgba(var(--primary-6))' : colorList[index % colorList.length] }}>
              <img src={ICON_MAP[key]} alt={key} height={28} />
            </div>
          </div>)
        ))}
      </div>
    </div>
  );
});

export default XTodoCenter;

