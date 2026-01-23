import { STATUS_OPTIONS, STATUS_VALUES } from '@/components/Materials/constants';
import { Alert } from '@arco-design/web-react';
import { IconClose } from '@arco-design/web-react/icon';
import { isRuntimeEnv } from '@onebase/common';
import { memo } from 'react';
import '../index.css';
import { XAlertConfig } from './schema';

const XAlert = memo((props: XAlertConfig) => {
  const { label, content, alertType, showIcon, allowClose, width, status } = props;

  return (
    <Alert
      type={alertType}
      title={label.display ? label.text : undefined}
      content={content.display ? content.text : undefined}
      showIcon={showIcon}
      closable={isRuntimeEnv() ? allowClose : false }
      action={
        !isRuntimeEnv() && allowClose ? <IconClose /> : null
      }

      style={{
        borderRadius: '8px',
        width: width || '100%',
        opacity: status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN] ? 0.4 : 1
      }}
    />
  );
});

export default XAlert;
