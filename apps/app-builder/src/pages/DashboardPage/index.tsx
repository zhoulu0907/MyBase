import React, { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import styles from './index.module.less';
import { getDashboardDetailApi } from '@onebase/app';
const PreviewPage: React.FC = () => {
  const { dashboardId } = useParams();
  const [imgSrc, setImgSrc] = useState<string | null>(null);
  const [loading, setLoading] = useState<boolean>(true);

  useEffect(() => {
    if (dashboardId) {
      getScreenData(dashboardId);
    }
  }, [dashboardId]);

  // 发送请求接口 获取图片数据
  const getScreenData = async (id: string) => {
    setLoading(true);
    try {
      const resp = await getDashboardDetailApi(id);
      setImgSrc(resp.indexImage);
      setLoading(false);
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
        {/* <img src={imgSrc} alt="预览图片" style={{ width: '100%', height: '100%' }} /> */}
        <iframe
          // src="http://s25029301301.dev.internal.virtueit.net:81/v0/appdashboard/#/project/dataset-form"
          src="http://s25029301301.dev.internal.virtueit.net:81/v0/appdashboard/#/"
          // src={`http://s25029301301.dev.internal.virtueit.net:81/v0/appdashboard/#/chat/home/${dashboardId}`}
          // frameborder="0"
          style={{ width: '100vw', height: '100vh' }}
        ></iframe>
      </div>
    </>
  );
};

export default PreviewPage;
