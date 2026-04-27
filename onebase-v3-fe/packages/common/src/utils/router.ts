/**
 * 工具类：从当前 window.location.hash 中解析指定参数
 * @param {string} key 需要获取的参数名
 * @returns {string | null} 参数值，未找到返回 null
 */
export function getHashQueryParam(key: string, hash?: string): string | null {
  if (!hash) {
    hash = window.location.hash;
  }
  const queryIndex = hash.indexOf('?');
  if (queryIndex !== -1) {
    const queryString = hash.substring(queryIndex + 1);
    const params = new URLSearchParams(queryString);
    return params.get(key);
  }
  return null;
}

// 从 window.location.hash 中解析 redirectURL，再从 redirectURL 解析 appId 和 tenantId
export function getHashTenantIdAndAppId(setTenantId: (tenantId: string) => void, setAppId: (appId: string) => void) {
  const rawHash = window.location.hash;
  const prefix = '#/login?redirectURL=';
  if (rawHash.startsWith(prefix)) {
    const redirectURL = rawHash.replace(prefix, '');
    setTenantIdAndAppId(redirectURL)
  } else {
    setTenantIdAndAppId(rawHash)
  }

  function setTenantIdAndAppId(url: string) {
    // tenantId
    const tenantId = getHashQueryParam('tenantId', url) || '';
    const tenantIdMatch = url.match(/\/onebase\/(\d+)\//);
    const matchTenantId = tenantIdMatch && tenantIdMatch.length > 1 ? tenantIdMatch[1] : '';
    setTenantId(tenantId || matchTenantId);

    // appId
    const appId = getHashQueryParam('appId', url) || '';
    const appIdMatch = url.match(/\/onebase\/(\d+)\/(\d+)\//);
    const matchAppId = appIdMatch && appIdMatch.length > 2 ? appIdMatch[2] : '';
    setAppId(appId || matchAppId);
  }
}