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
