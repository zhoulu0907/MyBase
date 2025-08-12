/**
 * 将时间戳转换为格式化日期字符串
 * @param timestamp 时间戳
 * @returns 格式化后的日期字符串 'YYYY-MM-DD HH:mm:ss'
 */
export const formatTimestamp = (timestamp: string | number | null | undefined): string => {
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

/**
 * 生成时间戳字符串，格式为 YYYYMMDDHHmmss
 * @returns 时间戳字符串
 */
export const generateTimestampString = (): string => {
  const now = new Date();
  const year = now.getFullYear();
  const month = (now.getMonth() + 1).toString().padStart(2, '0');
  const day = now.getDate().toString().padStart(2, '0');
  const hours = now.getHours().toString().padStart(2, '0');
  const minutes = now.getMinutes().toString().padStart(2, '0');
  const seconds = now.getSeconds().toString().padStart(2, '0');
  
  return `${year}${month}${day}${hours}${minutes}${seconds}`;
};