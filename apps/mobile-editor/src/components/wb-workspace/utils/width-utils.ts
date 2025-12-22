/**
 * 将百分比宽度转换为像素
 */
export function parseWidthToPixel(widthStr: string, containerWidth: number): number {
  const percentage = parseFloat(widthStr.replace('%', ''));
  return (percentage / 100) * containerWidth;
}

/**
 * 获取组件默认宽度
 */
export function getDefaultWidth(componentType: string, defaultWidths: Record<string, string>): string {
  return defaultWidths[componentType] || defaultWidths.default || '50%';
}
