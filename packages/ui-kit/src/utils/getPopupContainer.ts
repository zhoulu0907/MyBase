/**
 * 通用获取 Popup 挂载容器
 * 解决滚动脱离、支持自定义 className
 */
export const getPopupContainer = (
  node?: HTMLElement,
  className: string = '.arco-form-item'
): HTMLElement => {
  if (!node) return document.body;

  // 优先查找传入的 className
  const container =
    node.closest(className) || node.closest('.arco-form-item') || node.parentElement;

  return (container as HTMLElement) || document.body;
};
