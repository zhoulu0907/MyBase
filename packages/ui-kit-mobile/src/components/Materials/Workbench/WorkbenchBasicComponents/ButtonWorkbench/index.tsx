import { memo } from 'react';
import { useParams } from 'react-router-dom';
import { Button } from '@arco-design/mobile-react';
import { workbenchSchema } from '@onebase/ui-kit';
import { useJump } from '../../hooks/useJump';
import styles from './index.module.css';

type XButtonWorkbenchConfig = typeof workbenchSchema.XButtonWorkbench.config;

const XButtonWorkbench = memo((props: XButtonWorkbenchConfig & { runtime?: boolean; detailMode?: boolean }) => {
  const {
    id,
    label,
    runtime = true,
    backgroundColor,
    textColor,
    textSize,
    textAlign,
    jumpType,
    jumpPageId,
    jumpExternalUrl,
    status
  } = props;

  const textAlignHorizontal = textAlign.horizontal || 'center';
  const textAlignVertical = textAlign.vertical || 'middle'

  const { handleJump } = useJump();

  // 处理按钮点击
  const handleButtonClick = async () => {
    await handleJump({
      menuUuid: jumpType === 'internal' ? jumpPageId : undefined,
      linkAddress: jumpType === 'external' ? jumpExternalUrl : undefined,
      runtime,
    });
  };

  // 将垂直对齐值转换为 CSS 的 alignItems 值
  const getAlignItems = (verticalAlign: string) => {
    switch (verticalAlign) {
      case 'top':
        return 'flex-start';
      case 'bottom':
        return 'flex-end';
      case 'middle':
      default:
        return 'center';
    }
  };

  // 按钮样式
  const buttonStyle: React.CSSProperties = {
    width: '100%',
    textAlign: textAlignHorizontal as 'left' | 'center' | 'right',
    borderRadius: '4px',
    border: 'none',
    cursor: runtime ? 'pointer' : 'default',
    boxSizing: 'border-box' as const,
    display: 'flex',
    alignItems: getAlignItems(textAlignVertical),
    justifyContent: textAlignHorizontal === 'left' ? 'flex-start' : textAlignHorizontal === 'right' ? 'flex-end' : 'center',
  };

  const buttonTextStyle: React.CSSProperties = {
    fontSize: `${textSize}px`,
  };

  // 按钮文本
  const buttonText = label?.text || '按钮';

  return (
    <div className={styles.buttonWrapper}>
      {label?.display && label?.text && (
        <div className={styles.title}>{label.text}</div>
      )}
      <div
        className={styles.button}
        style={
          {
            '--custom-bg-color': backgroundColor || undefined
          } as React.CSSProperties
        }
      >
        <Button
          inline
          size="huge"
          needActive
          style={buttonStyle}
          onClick={handleButtonClick}
          disabled={!runtime}
          color={textColor}
          bgColor={backgroundColor}
        >
          <span style={buttonTextStyle}>{buttonText}</span>
        </Button>
      </div>
    </div>
  );
});

export default XButtonWorkbench;
