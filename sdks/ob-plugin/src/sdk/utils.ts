/**
 * 生成唯一 ID
 * @param prefix 前缀
 * @returns unique id
 */
export const genId = (prefix: string = 'id'): string => {
  return `${prefix}_${Math.random().toString(36).slice(2, 8)}`;
};
