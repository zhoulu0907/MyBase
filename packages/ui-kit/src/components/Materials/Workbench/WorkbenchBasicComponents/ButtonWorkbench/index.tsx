import { memo } from 'react';
import type { XButtonWorkbenchConfig } from './schema';
import { TWbTextAlignDefaultType } from '@/components/Materials';

/**
 * 按钮组件（移动端独有）
 * Web 端占位实现，用于兼容性
 */
const XButtonWorkbench = memo((props: XButtonWorkbenchConfig & { runtime?: boolean; detailMode?: boolean }) => {
  const {
    label,
    backgroundColor = '#F1F2F3',
    textColor = '#1D2129',
    textSize = 16,
    textAlign = {
      horizontal: 'center',
      vertical: 'middle'
    } as TWbTextAlignDefaultType,
  } = props;

  const buttonText = label?.text || '按钮';

  const buttonStyle: React.CSSProperties = {
    width: '100%',
    backgroundColor: backgroundColor,
    color: textColor,
    fontSize: `${textSize}px`,
    textAlign: textAlign.horizontal as 'left' | 'center' | 'right',
    padding: '12px 16px',
    borderRadius: '4px',
    border: 'none',
    cursor: 'default',
    boxSizing: 'border-box' as const,
    display: 'flex',
    alignItems: 'center',
    justifyContent: textAlign.horizontal === 'left' ? 'flex-start' : textAlign.horizontal === 'right' ? 'flex-end' : 'center',
  };

  return (
    <div style={{ width: '100%', padding: '16px', boxSizing: 'border-box' }}>
      {label?.display && label?.text && (
        <div style={{ fontSize: '16px', fontWeight: 600, color: '#1d2129', marginBottom: '12px' }}>
          {label.text}
        </div>
      )}
      <div style={buttonStyle}>
        {buttonText}
      </div>
      <div style={{ fontSize: '12px', color: '#86909C', marginTop: '8px' }}>
        （此组件仅在移动端可用）
      </div>
    </div>
  );
});

export default XButtonWorkbench;

