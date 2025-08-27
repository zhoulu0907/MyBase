/**
 * 工具类：从当前 window.location.hash 中解析指定参数
 * @param {string} key 需要获取的参数名
 * @returns {string | null} 参数值，未找到返回 null
 */
export function getHashQueryParam(key: string): string | null {
  const hash = window.location.hash;
  const queryIndex = hash.indexOf('?');
  if (queryIndex !== -1) {
    const queryString = hash.substring(queryIndex + 1);
    const params = new URLSearchParams(queryString);
    return params.get(key);
  }
  return null;
}
