export const downloadFile = (blob: Blob, filename: string) => {
  // 创建一个临时的 URL 对象指向文件数据
  const url = window.URL.createObjectURL(blob);
  
  // 创建一个隐藏的 <a> 标签并触发点击事件来下载文件
  const link = document.createElement('a');
  link.href = url;
  link.download = filename; // 设置下载文件名
  
  // 触发下载
  document.body.appendChild(link);
  link.click();
  
  // 清理临时创建的对象
  document.body.removeChild(link);
  window.URL.revokeObjectURL(url);
};