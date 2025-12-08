import '@wangeditor/editor/dist/css/style.css'; // 引入 css
import { memo } from 'react';
import styles from './index.module.css';
import type { XRichTextConfig } from './schema';

const XRichText = memo((props: XRichTextConfig & { runtime?: boolean; detailMode?: boolean }) => {
  const {
    id,
    label,
    runtime = true,
    WbRichTextContent = '',
    WbColor = '#FFFFFF',
    status
  } = props;

  const displayValue = WbRichTextContent || '';

  const containerStyle: React.CSSProperties = {
    width: '100%',
    backgroundColor: WbColor,
    padding: '10px',
    borderRadius: '4px',
    boxSizing: 'border-box' as const,
    minHeight: '100px',
  };

  return (
    <div className={styles.richTextWrapper}>
      {label.display &&
        label.text && <div className={styles.title}>{label.text}</div>}
      {displayValue ? 
        <div dangerouslySetInnerHTML={{ __html: displayValue }} style={containerStyle}></div> : 
        <div style={{...containerStyle, color: '#86909C'} as React.CSSProperties}>请输入</div>}
    </div>
  );
});

export default XRichText;
