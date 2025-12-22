import React, { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import styles from './index.module.less';
const PreviewPage: React.FC = () => {
  const { screenProjectId } = useParams<{ screenProjectId: string }>();
  const [imgSrc, setImgSrc] = useState<string | null>(null);
  const [loading, setLoading] = useState<boolean>(true);

  useEffect(() => {
    console.log('screenProjectId:', screenProjectId);

    if (screenProjectId) {
      getScreenData();
    }
  }, [screenProjectId]);

  // 发送请求接口 获取图片数据
  const getScreenData = async () => {
    setLoading(true);
    try {
      // 这里应该调用实际的API获取图片信息
      // 示例代码：
      // const response = await fetch(`/api/screen-project/${screenProjectId}/image`);
      // const data = await response.json();
      // setImgSrc(data.imgSrc);
      setImgSrc(screenProjectId);

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
        <img className={styles.previewImg} src={imgSrc} />
        <iframe
          src="http://10.0.104.38:9091/#/project/dataset-form"
          // frameborder="0"
          style={{ width: '100vw', height: '100vh' }}
        ></iframe>
      </div>
    </>
  );
};

export default PreviewPage;
