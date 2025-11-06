import { Message, Modal } from '@arco-design/web-react';
import CryptoJS from 'crypto-js';
import React, { forwardRef, useEffect, useImperativeHandle, useRef, useState, TouchEvent } from 'react';
import { Captcha, CaptchaCheck } from './types';

interface SliderCaptchaProps {
  onSuccess: (token: string) => void;
  onError?: (error: any) => void;
  getCaptchaApi: Function;
  checkCaptchaApi: Function;
}

export interface SliderCaptchaRef {
  showCaptcha: () => void;
  closeCaptcha: () => void;
}

const SliderCaptcha = forwardRef<SliderCaptchaRef, SliderCaptchaProps>(
  ({ onSuccess, onError, getCaptchaApi, checkCaptchaApi }, ref) => {
    const [visible, setVisible] = useState(false);
    const [captchaData, setCaptchaData] = useState<any>(null);
    const [loading, setLoading] = useState(false);
    const [dragging, setDragging] = useState(false);
    const [dragOffset, setDragOffset] = useState(0);
    const [bgImage, setBgImage] = useState('');
    const [sliderImage, setSliderImage] = useState('');
    const sliderRef = useRef<HTMLDivElement>(null);
    const trackRef = useRef<HTMLDivElement>(null);
    const [setSize, setSetSize] = useState({
      imgWidth: '310px',
      imgHeight: '155px',
      barWidth: '310px',
      barHeight: '40px'
    });

    // AES加密函数
    const aesEncrypt = (word: string, keyWord: string = 'XwKsGlMcdPMEhR1B') => {
      const key = CryptoJS.enc.Utf8.parse(keyWord);
      const srcs = CryptoJS.enc.Utf8.parse(word);
      const encrypted = CryptoJS.AES.encrypt(srcs, key, {
        mode: CryptoJS.mode.ECB,
        padding: CryptoJS.pad.Pkcs7
      });
      return encrypted.toString();
    };

    // 获取验证码
    const fetchCaptcha = async () => {
      try {
        setLoading(true);
        const params: Captcha = {
          captchaType: 'blockPuzzle',
          clientUid: ''
        };

        const resp = await getCaptchaApi(params);

        if (resp.repCode === '0000' && resp.repData) {
          const captcha = resp.repData;

          // 设置背景图和滑块图
          setBgImage(`data:image/png;base64,${captcha.originalImageBase64}`);
          setSliderImage(`data:image/png;base64,${captcha.jigsawImageBase64}`);

          // 保存完整的验证码数据
          setCaptchaData({
            ...captcha,
            secretKey: captcha.secretKey // 确保secretKey被正确保存
          });
        } else {
          // 更详细的错误信息
          const errorMessage = resp?.msg || '验证码获取失败';
          throw new Error(errorMessage);
        }
      } catch (error) {
        console.error('获取验证码失败:', error);

        // 显示更具体的错误信息
        if (error instanceof Error) {
          Message.error(error.message);
        } else {
          Message.error('获取验证码失败，请检查网络连接');
        }

        onError && onError(error);
      } finally {
        setLoading(false);
      }
    };

    // 显示验证码弹窗
    const showCaptcha = () => {
      setVisible(true);
      fetchCaptcha();
    };

    // 关闭验证码弹窗
    const closeCaptcha = () => {
      setVisible(false);
      setDragOffset(0);
      setDragging(false);
    };

    // 验证码校验
    const verifyCaptcha = async (pointJson: string) => {
      if (!captchaData?.token) {
        return;
      }

      try {
        const captchaVerification = captchaData.secretKey
          ? aesEncrypt(captchaData.token + '---' + pointJson, captchaData.secretKey)
          : captchaData.token + '---' + pointJson;
        // 使用secretKey进行AES加密
        const encryptedPointJson = captchaData.secretKey ? aesEncrypt(pointJson, captchaData.secretKey) : pointJson; // 如果没有secretKey，则不加密

        const params: CaptchaCheck = {
          captchaType: 'blockPuzzle',
          pointJson: encryptedPointJson,
          token: captchaData.token
        };

        const resp: any = await checkCaptchaApi(params);

        if (resp.repData?.result) {
          onSuccess(captchaVerification);
          setTimeout(() => {
            closeCaptcha();
          }, 100);
        } else {
          Message.error('验证失败，请重试');
          setDragOffset(0);
          fetchCaptcha(); // 重新获取验证码
        }
      } catch (error) {
        console.error('验证异常:', error);
        Message.error('验证失败，请重试');
        setDragOffset(0);
        onError && onError(error);
        fetchCaptcha(); // 重新获取验证码
      }
    };

    // 处理拖拽开始
    const handleDragStart = (e: React.MouseEvent | React.TouchEvent) => {
      e.preventDefault();
      setDragging(true);
    };

    // 处理拖拽移动
    const handleDragMove = (e: React.MouseEvent | MouseEvent | TouchEvent) => {
      if (!dragging || !trackRef.current) return;

      const trackRect = trackRef.current.getBoundingClientRect();
      // 兼容鼠标和触摸事件
      const clientX = 'touches' in e ? e.touches[0].clientX : e.clientX;
      const offsetX = Math.max(0, Math.min(clientX - trackRect.left, trackRect.width));
      setDragOffset(offsetX);
    };

    // 处理拖拽结束
    const handleDragEnd = () => {
      if (!dragging) {
        return;
      }

      setDragging(false);

      if (trackRef.current && captchaData) {
        const trackRect = trackRef.current.getBoundingClientRect();
        const percentage = dragOffset / trackRect.width;
        const moveLeft = Math.round(percentage * trackRect.width);

        // 构造pointJson参数
        const pointJson = JSON.stringify({
          x: moveLeft,
          y: 5.0, // 默认y值
          width: 100,
          height: 100
        });

        verifyCaptcha(pointJson);
      }
    };

    // 监听鼠标和触摸事件
    useEffect(() => {
      const handleMouseMove = (e: MouseEvent) => handleDragMove(e);
      const handleMouseUp = () => handleDragEnd();
      const handleTouchMove = (e: TouchEvent) => {
        e.preventDefault(); // 防止页面滚动
        handleDragMove(e);
      };
      const handleTouchEnd = () => handleDragEnd();
      const handleTouchCancel = () => handleDragEnd(); // 处理触摸被意外中断的情况

      if (dragging) {
        // 同时监听鼠标和触摸事件
        document.addEventListener('mousemove', handleMouseMove);
        document.addEventListener('mouseup', handleMouseUp);
        document.addEventListener('touchmove', handleTouchMove, { passive: false });
        document.addEventListener('touchend', handleTouchEnd);
        document.addEventListener('touchcancel', handleTouchCancel);
      }

      return () => {
        // 移除所有事件监听
        document.removeEventListener('mousemove', handleMouseMove);
        document.removeEventListener('mouseup', handleMouseUp);
        document.removeEventListener('touchmove', handleTouchMove);
        document.removeEventListener('touchend', handleTouchEnd);
        document.removeEventListener('touchcancel', handleTouchCancel);
      };
    }, [dragging, dragOffset]);

    // 暴露方法给父组件调用
    useImperativeHandle(ref, () => ({
      showCaptcha,
      closeCaptcha
    }));

    // 计算滑块垂直居中位置
    const getSliderTopPosition = () => {
      if (!captchaData?.captcha?.templateImageHeight) return '0px';
      const containerHeight = 150; // 容器高度
      const imageHeight = captchaData.captcha.templateImageHeight;
      const padding = 10; // 添加一些内边距
      return `${(containerHeight - imageHeight - padding) / 2}px`;
    };
    return (
      <>
        {visible && (
          <div
            style={{
              position: 'fixed',
              top: 0,
              left: 0,
              width: '100%',
              height: '100%',
              backgroundColor: 'rgba(0, 0, 0, 0.4)',
              zIndex: 1000
            }}
          />
        )}

        <Modal
          title="安全验证"
          visible={visible}
          onCancel={closeCaptcha}
          footer={null}
          className="slider-captcha-modal"
          style={{ zIndex: 1001, width: '350px' }}
          unmountOnExit
        >
          <div className="slider-captcha-container">
            {loading ? (
              <div className="captcha-loading">加载中...</div>
            ) : bgImage && sliderImage ? (
              <>
                <div
                  className="captcha-image-container"
                  style={{
                    position: 'relative',
                    marginBottom: '20px',
                    width: setSize.imgWidth,
                    height: setSize.imgHeight
                  }}
                >
                  {/* 背景图片 */}
                  <img
                    src={bgImage}
                    alt="captcha background"
                    className="captcha-bg-image"
                    style={{
                      width: '100%',
                      height: '100%',
                      display: 'block',
                      borderRadius: '4px',
                      boxShadow: '0 0 10px rgba(0, 0, 0, 0.1)',
                      border: '1px solid #ddd'
                    }}
                  />
                  {/* 拖动的滑块图片 */}
                  <img
                    src={sliderImage}
                    alt="captcha slider"
                    style={{
                      position: 'absolute',
                      top: getSliderTopPosition(),
                      left: `${dragOffset}px`,
                      width: Math.floor((parseInt(setSize.imgWidth) * 47) / 310) + 'px',
                      height: setSize.imgHeight,
                      objectFit: 'cover',
                      transition: dragging ? 'none' : 'left 0.3s',
                      userSelect: 'none',
                      zIndex: 10,
                      transform: 'scale(1.05)'
                    }}
                  />
                </div>

                <div className="slider-track-container">
                  <div
                    ref={trackRef}
                    className="slider-track"
                    style={{
                      position: 'relative',
                      height: '40px',
                      width: setSize.imgWidth,
                      backgroundColor: '#f2f3f5',
                      borderRadius: '2px',
                      cursor: 'pointer',
                      border: '1px solid #ddd',
                      boxShadow: '0 0 5px rgba(0, 0, 0, 0.1)',
                      touchAction: 'none' // 防止浏览器默认的触摸行为
                    }}
                    onMouseDown={handleDragStart}
                    onTouchStart={handleDragStart}
                  >
                    <div
                      ref={sliderRef}
                      className={`slider-button ${dragging ? 'dragging' : ''}`}
                      style={{
                        position: 'absolute',
                        left: `${dragOffset}px`,
                        top: '0',
                        width: '40px',
                        height: '40px',
                        backgroundColor: '#fff',
                        boxShadow: '0 0 5px rgba(0, 0, 0, 0.2)',
                        borderRadius: '2px',
                        cursor: 'pointer',
                        transition: dragging ? 'none' : 'left 0.3s',
                        zIndex: 2,
                        display: 'flex',
                        alignItems: 'center',
                        justifyContent: 'center',
                        border: '1px solid #ddd',
                        transform: 'scale(1.05)'
                      }}
                      onMouseDown={handleDragStart}
                      onTouchStart={handleDragStart}
                    >
                      <div className="slider-button-inner" style={{ color: '#999' }}>
                        →
                      </div>
                    </div>
                    <div
                      className="slider-track-text"
                      style={{
                        position: 'absolute',
                        top: '0',
                        left: '0',
                        width: '100%',
                        height: '100%',
                        display: 'flex',
                        alignItems: 'center',
                        justifyContent: 'center',
                        color: '#999',
                        fontSize: '14px',
                        userSelect: 'none',
                        pointerEvents: 'none'
                      }}
                    >
                      {dragging ? '松开验证' : '按住滑块拖动完成拼图'}
                    </div>
                  </div>
                </div>
              </>
            ) : (
              <div className="captcha-error">验证码加载失败</div>
            )}
          </div>
        </Modal>
      </>
    );
  }
);

SliderCaptcha.displayName = 'SliderCaptcha';

export { SliderCaptcha };
