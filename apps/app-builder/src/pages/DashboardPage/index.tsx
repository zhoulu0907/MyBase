import React, { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import styles from './index.module.less';
const PreviewPage: React.FC = () => {
  const { dashboardId } = useParams<{ dashboardId: string }>();
  const [imgSrc, setImgSrc] = useState<string | null>(null);
  const [loading, setLoading] = useState<boolean>(true);

  useEffect(() => {
    console.log('dashboardId:', dashboardId);

    if (dashboardId) {
      getScreenData();
    }
  }, [dashboardId]);

  // 发送请求接口 获取图片数据
  const getScreenData = async () => {
    setLoading(true);
    try {
      // 这里应该调用实际的API获取图片信息
      // 示例代码：
      // const response = await fetch(`/api/screen-project/${dashboardId}/image`);
      // const data = await response.json();
      // setImgSrc(data.imgSrc);
      setImgSrc(dashboardId);

      // 模拟API调用
      setTimeout(() => {
        // 模拟从API获取到的图片地址
        // setImgSrc('实际从API获取的图片地址');
        setLoading(false);
      }, 1000);
    } catch (error) {
      console.error('获取图片失败:', error);
      setLoading(false);
    }
  };

  if (loading) {
    return <div>加载中...</div>;
  }

  if (!imgSrc) {
    return <div>图片未找到</div>;
  }

  return (
    <>
      <div className={styles.previewPage}>
        {/* 由于目标站点设置了 X-Frame-Options: sameorigin，无法在 iframe 中加载 */}
        {/* 改为在新窗口打开 */}
        <div style={{ textAlign: 'center', padding: '20px' }}>
          <p>预览页面将在新窗口中打开。</p>
          <button
            onClick={() => window.open('http://s25029301301.dev.internal.virtueit.net:81/v0/appdashboard/#/', '_blank')}
          >
            点击打开预览
          </button>
        </div>
        {/* <iframe
          // src="http://s25029301301.dev.internal.virtueit.net:81/v0/appdashboard/#/project/dataset-form"
          src="http://s25029301301.dev.internal.virtueit.net:81/v0/appdashboard/#/"
          // src={`http://s25029301301.dev.internal.virtueit.net:81/v0/appdashboard/#/chat/home/${dashboardId}`}
          // frameborder="0"
          style={{ width: '100vw', height: '100vh' }}
        ></iframe> */}
      </div>
    </>
  );
};

export default PreviewPage;
