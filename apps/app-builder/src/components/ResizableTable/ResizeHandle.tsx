import { forwardRef } from 'react';
import type { ResizeHandleProps } from './types';
import styles from './index.module.less';

const ResizeHandle = forwardRef<HTMLSpanElement, ResizeHandleProps>((props, ref) => {
  const { handleAxis, ...restProps } = props;
  return (
    <span
      ref={ref}
      className={`react-resizable-handle react-resizable-handle-${handleAxis || 'e'}`}
      {...restProps}
      onClick={(e) => {
        e.stopPropagation();
      }}
    >
      <span className={styles.resizeHandleLine} />
    </span>
  );
});

ResizeHandle.displayName = 'ResizeHandle';

export default ResizeHandle;