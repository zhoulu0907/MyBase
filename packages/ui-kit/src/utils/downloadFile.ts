// 验证 URL 是否安全（仅允许 http/https/blob 协议）
const isValidUrl = (url: string): boolean => {
  try {
    const parsedUrl = new URL(url, window.location.origin);
    // 仅允许 http、https 和 blob 协议，防止 file://、javascript:// 等危险协议
    // blob: 协议由浏览器从 HTTP 响应自动生成，是安全的
    if (!['http:', 'https:', 'blob:'].includes(parsedUrl.protocol)) {
      return false;
    }
    return true;
  } catch {
    return false;
  }
};

// url转blob
const getBlob = async (url: string): Promise<Blob> => {
  if (!isValidUrl(url)) {
    throw new Error('Invalid URL: only http, https and blob protocols are allowed');
  }
  // 这是一个只读的 GET 请求，不涉及状态修改，CSRF 风险较低
  // fetch 默认 credentials: 'same-origin'，仅同源请求携带 Cookie
  const response = await fetch(url);
  if (!response.ok) {
    throw new Error(`Request failed with status ${response.status}`);
  }
  return response.blob();
};
// 文件保存
const saveAs = (blob: Blob, filename: string) => {
  const link = document.createElement('a');
  const body = document.body;

  link.href = window.URL.createObjectURL(blob);
  link.download = filename;

  // hide the link
  link.style.display = 'none';
  body.appendChild(link);

  link.click();
  body.removeChild(link);

  window.URL.revokeObjectURL(link.href);
};

export async function downloadFileByUrl(url: string, fileName: string) {
  if (!url) {
    return;
  }
  const blob = await getBlob(url);
  saveAs(blob, fileName);
}
