import { Alert } from '@arco-design/web-react';
import type { CSSProperties } from 'react';
import { memo } from 'react';

import { WORKBENCH_STATUS_OPTIONS, WORKBENCH_STATUS_VALUES } from '../../core/constants';
import type { XInformationListConfig } from './schema';

const containerStyle: CSSProperties = {
  width: '100%',
  minHeight: 120,
  display: 'flex',
  alignItems: 'center',
  justifyContent: 'center',
  backgroundColor: 'var(--color-neutral-1, #f5f6f8)',
  border: '1px dashed var(--color-border-2, #dcdfe6)',
  borderRadius: 8,
  padding: 16,
  boxSizing: 'border-box'
};

/**
 * 待办中心占位组件
 * 在真正组件未实现前提供兜底展示，避免页面崩溃
 */
const XInformationList = memo((props: XInformationListConfig & { runtime?: boolean }) => {
  const { status, runtime } = props;
  const hiddenStatusValue = WORKBENCH_STATUS_VALUES[WORKBENCH_STATUS_OPTIONS.HIDDEN];

  if (runtime && status === hiddenStatusValue) {
    return null;
  }

  return (
    <div style={containerStyle}>
      <Alert
        type="info"
        title="待办中心组件开发中"
        content="该组件暂未提供完整实现，当前为占位展示。"
        showIcon
      />
    </div>
  );
});

export default XInformationList;

