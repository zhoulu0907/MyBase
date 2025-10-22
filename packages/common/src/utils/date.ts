/**
 * 将时间戳转换为格式化日期字符串
 * @param timestamp 时间戳
 * @returns 格式化后的日期字符串 'YYYY-MM-DD HH:mm:ss'
 */
export const formatTimeYMDHMS = (timestamp: string | number | null | undefined): string => {
  if (!timestamp) {
    return '--';
  }

  const date = new Date(timestamp);
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, '0');
  const day = String(date.getDate()).padStart(2, '0');
  const hours = String(date.getHours()).padStart(2, '0');
  const minutes = String(date.getMinutes()).padStart(2, '0');
  const seconds = String(date.getSeconds()).padStart(2, '0');

  return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`;
};
