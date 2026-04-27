import { useCallback, useRef } from 'react';
import { WORKBENCH_COMPONENT_TYPES } from '@onebase/ui-kit';

// 4列网格系统的预设值（百分比）
const GRID_4_PRESETS = [25, 50, 75, 100];
// 百分比系统的预设值
const PERCENTAGE_PRESETS = [33.33, 66.66];
// 吸附阈值（像素）
const SNAP_THRESHOLD_PX = 20;

// 使用4列网格的组件类型
const GRID_4_COMPONENTS = [WORKBENCH_COMPONENT_TYPES.QUICK_ENTRY];

/**
 * 根据组件类型决定使用哪种宽度系统
 */
function getWidthSystem(componentType: string): 'grid-4' | 'percentage' {
  return (GRID_4_COMPONENTS as readonly string[]).includes(componentType) ? 'grid-4' : 'percentage';
}

/**
 * 将像素宽度转换为百分比字符串
 */
function pixelToPercentage(pixelWidth: number, containerWidth: number): string {
  const percentage = (pixelWidth / containerWidth) * 100;
  return `${percentage}%`;
}

/**
 * 将百分比字符串转换为像素宽度
 */
function percentageToPixel(percentageStr: string, containerWidth: number): number {
  const percentage = parseFloat(percentageStr.replace('%', ''));
  return (percentage / 100) * containerWidth;
}

/**
 * 吸附算法：根据像素距离判断是否吸附到预设值
 */
function snapToPreset(pixelWidth: number, containerWidth: number, system: 'grid-4' | 'percentage'): string {
  const presets = system === 'grid-4' ? GRID_4_PRESETS : PERCENTAGE_PRESETS;

  for (const presetPercent of presets) {
    const presetPixel = (presetPercent / 100) * containerWidth;
    const distance = Math.abs(pixelWidth - presetPixel);

    if (distance < SNAP_THRESHOLD_PX) {
      return `${presetPercent}%`;
    }
  }

  // 如果没有吸附，返回实际百分比
  return pixelToPercentage(pixelWidth, containerWidth);
}

/**
 * 工作台组件宽度调整 Hook
 */
export function useWorkbenchResize(
  componentType: string,
  containerWidth: number,
  currentWidth: string,
  onWidthChange: (newWidth: string) => void
) {
  const widthSystem = getWidthSystem(componentType);
  const currentWidthPixel = useRef(percentageToPixel(currentWidth, containerWidth));

  // 更新当前宽度像素值
  const updateCurrentWidth = useCallback(
    (newWidth: string) => {
      currentWidthPixel.current = percentageToPixel(newWidth, containerWidth);
    },
    [containerWidth]
  );

  // 处理拖拽调整
  const handleResize = useCallback(
    (_e: React.SyntheticEvent, data: { size: { width: number } }) => {
      const snappedWidth = snapToPreset(data.size.width, containerWidth, widthSystem);
      onWidthChange(snappedWidth);
      updateCurrentWidth(snappedWidth);
      return snappedWidth;
    },
    [containerWidth, widthSystem, onWidthChange, updateCurrentWidth]
  );

  // 获取当前宽度的像素值
  const getCurrentWidthPixel = useCallback(() => {
    return currentWidthPixel.current;
  }, []);

  // 初始化当前宽度像素值
  if (containerWidth > 0) {
    currentWidthPixel.current = percentageToPixel(currentWidth, containerWidth);
  }

  return {
    handleResize,
    getCurrentWidthPixel,
    widthSystem,
    presets: widthSystem === 'grid-4' ? GRID_4_PRESETS : PERCENTAGE_PRESETS
  };
}

