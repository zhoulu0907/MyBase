import { useEffect, useRef, useState } from 'react';

/**
 * 监听工作台容器宽度变化
 */
export function useWorkbenchContainer() {
  const containerRef = useRef<HTMLDivElement>(null);
  const [containerWidth, setContainerWidth] = useState(0);

  useEffect(() => {
    if (!containerRef.current) return;

    const resizeObserver = new ResizeObserver((entries) => {
      const newWidth = entries[0].contentRect.width;
      setContainerWidth(newWidth);
    });

    resizeObserver.observe(containerRef.current);

    // 初始化宽度
    if (containerRef.current) {
      setContainerWidth(containerRef.current.offsetWidth);
    }

    return () => {
      resizeObserver.disconnect();
    };
  }, []);

  return { containerRef, containerWidth };
}
