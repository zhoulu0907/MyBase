/**
 * 查找编辑数据中的指定项
 * @param editData 编辑数据
 * @param key 指定项的键
 * @returns 指定项的索引和值
 */
export const findItem = (editData: Record<string, unknown>[], key: string) => {
  const index = editData.findIndex((item) => typeof item.key === 'string' && item.key === key);
  return index > -1 ? { item: editData[index], index } : null;
};

/**
 * 找出现有项目中匹配指定前缀格式的最大序号
 * @param items 项目数组
 * @param fieldName 要检查的字段名
 * @param prefix 前缀字符串（如 "新增链接"、"图片名称"）
 * @returns 下一个可用的序号（最大序号 + 1）
 */
export const getNextIndex = <T extends Record<string, unknown>>(
  items: T[],
  fieldName: keyof T,
  prefix: string
): number => {
  const maxIndex = items.reduce((max, item) => {
    const value = item[fieldName];
    if (typeof value === 'string') {
      const regex = new RegExp(`^${prefix}(\\d+)$`);
      const match = value.match(regex);
      if (match) {
        const num = parseInt(match[1], 10);
        return Math.max(max, num);
      }
    }
    return max;
  }, 0);
  return maxIndex + 1;
};
