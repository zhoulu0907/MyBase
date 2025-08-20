import React, { useState, useEffect, useRef } from 'react';
import { Modal, Button, Message } from '@arco-design/web-react';
import { IconRefresh } from '@arco-design/web-react/icon';
// @ts-ignore
import CryptoJS from 'crypto-js';
import axios from 'axios';
import { getCaptchaApi, checkCaptchaApi, type Captcha, type CaptchaCheck } from '@onebase/platform-center';

interface SliderCaptchaProps {
  visible: boolean;
  onSuccess: (captchaVerification: string) => void;
  onCancel: () => void;
  mode?: 'pop' | 'fixed';
}

const SliderCaptcha: React.FC<SliderCaptchaProps> = ({
  visible,
  onSuccess,
  onCancel,
  mode = 'pop'
}) => {
  const [backImgBase, setBackImgBase] = useState('');
  const [blockBackImgBase, setBlockBackImgBase] = useState('');
  const [backToken, setBackToken] = useState('');
  const [secretKey, setSecretKey] = useState('');
  const [tipWords, setTipWords] = useState('');
  const [passFlag, setPassFlag] = useState(false);
  const [text, setText] = useState('拖动滑块完成拼图');
  const [finishText, setFinishText] = useState('');
  const [setSize, setSetSize] = useState({
    imgWidth: '310px',
    imgHeight: '155px',
    barWidth: '310px',
    barHeight: '40px'
  });
  const [moveBlockLeft, setMoveBlockLeft] = useState<any>(undefined);
  const [leftBarWidth, setLeftBarWidth] = useState<any>(undefined);
  const [moveBlockBackgroundColor, setMoveBlockBackgroundColor] = useState('#fff');
  const [leftBarBorderColor, setLeftBarBorderColor] = useState('#ddd');
  const [iconColor, setIconColor] = useState('#000');
  const [iconClass, setIconClass] = useState('icon-right');
  const [status, setStatus] = useState(false);
  const [isEnd, setIsEnd] = useState(false);
  const [showRefresh, setShowRefresh] = useState(true);
  const [transitionLeft, setTransitionLeft] = useState('');
  const [transitionWidth, setTransitionWidth] = useState('');
  const [startLeft, setStartLeft] = useState(0);
  const [startMoveTime, setStartMoveTime] = useState(0);
  const [endMovetime, setEndMovetime] = useState(0);

  const barAreaRef = useRef<HTMLDivElement>(null);
  const moveBlockRef = useRef<HTMLDivElement>(null);

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

  // 获取验证码图片
  const getPictrue = async () => {
    try {
      // 使用专门的滑块验证码类型
      const captchaData: Captcha = {
        captchaType: 'blockPuzzle'
      };
      
      const res = await getCaptchaApi(captchaData);
      
      if (res.repCode === '0000') {
        setBackImgBase(res.repData.originalImageBase64);
        setBlockBackImgBase(res.repData.jigsawImageBase64);
        setBackToken(res.repData.token);
        setSecretKey(res.repData.secretKey);
        setText('拖动滑块完成拼图');
      } else {
        setTipWords(res.repMsg);
      }
    } catch (error) {
      Message.error('获取验证码失败');
    }
  };

  // 初始化
  const init = () => {
    getPictrue();
    setText('拖动滑块完成拼图');
    setFinishText('');
    setTransitionLeft('left .3s');
    setMoveBlockLeft(0);
    setLeftBarWidth(undefined);
    setTransitionWidth('width .3s');
    setLeftBarBorderColor('#ddd');
    setMoveBlockBackgroundColor('#fff');
    setIconColor('#000');
    setIconClass('icon-right');
    setIsEnd(false);
    setShowRefresh(true);
    
    setTimeout(() => {
      setTransitionWidth('');
      setTransitionLeft('');
    }, 300);
  };

  // 刷新验证码
  const refresh = async () => {
    setShowRefresh(true);
    setFinishText('');
    setTransitionLeft('left .3s');
    setMoveBlockLeft(0);
    setLeftBarWidth(undefined);
    setTransitionWidth('width .3s');
    setLeftBarBorderColor('#ddd');
    setMoveBlockBackgroundColor('#fff');
    setIconColor('#000');
    setIconClass('icon-right');
    setIsEnd(false);
    
    await getPictrue();
    
    setTimeout(() => {
      setTransitionWidth('');
      setTransitionLeft('');
      setText('拖动滑块完成拼图');
    }, 300);
  };

  // 鼠标按下事件
  const start = (e: React.MouseEvent | React.TouchEvent) => {
    if (isEnd) return;
    
    const clientX = 'touches' in e ? e.touches[0].pageX : e.clientX;
    const barAreaLeft = barAreaRef.current?.getBoundingClientRect().left || 0;
    const startLeftValue = Math.floor(clientX - barAreaLeft);
    
    setStartLeft(startLeftValue);
    setStartMoveTime(Date.now());
    setText('');
    setMoveBlockBackgroundColor('#337ab7');
    setLeftBarBorderColor('#337AB7');
    setIconColor('#fff');
    setStatus(true);
  };

  // 鼠标移动事件
  const move = (e: MouseEvent | TouchEvent) => {
    if (!status || isEnd) return;
    
    const clientX = 'touches' in e ? e.touches[0].pageX : e.clientX;
    const barAreaLeft = barAreaRef.current?.getBoundingClientRect().left || 0;
    let move_block_left = clientX - barAreaLeft;
    
    // 限制移动范围
    const barAreaWidth = barAreaRef.current?.offsetWidth || 0;
    const blockSize = 50; // 默认滑块宽度
    
    if (move_block_left >= barAreaWidth - blockSize / 2 - 2) {
      move_block_left = barAreaWidth - blockSize / 2 - 2;
    }
    if (move_block_left <= blockSize / 2) {
      move_block_left = blockSize / 2;
    }
    
    // 设置滑块位置
    const leftValue = move_block_left - startLeft + 'px';
    setMoveBlockLeft(leftValue);
    setLeftBarWidth(leftValue);
  };

  // 鼠标释放事件
  const end = async () => {
    if (!status || isEnd) return;
    
    setStatus(false);
    setEndMovetime(Date.now());
    
    // 计算移动距离
    const moveLeftDistance = parseInt((moveBlockLeft || '0').replace('px', ''));
    const scaledDistance = (moveLeftDistance * 310) / parseInt(setSize.imgWidth);
    
    try {
      // 使用专门的滑块验证码验证类型
      const captchaCheckData: CaptchaCheck = {
        captchaType: 'blockPuzzle',
        pointJson: secretKey 
          ? aesEncrypt(JSON.stringify({ x: scaledDistance, y: 5.0 }), secretKey)
          : JSON.stringify({ x: scaledDistance, y: 5.0 }),
        token: backToken
      };
      
      const res = await axios.post('/captcha/check', captchaCheckData);
      
      if (res.data.repCode === '0000') {
        setMoveBlockBackgroundColor('#5cb85c');
        setLeftBarBorderColor('#5cb85c');
        setIconColor('#fff');
        setIconClass('icon-check');
        setShowRefresh(false);
        setIsEnd(true);
        setPassFlag(true);
        setTipWords(`${((Date.now() - startMoveTime) / 1000).toFixed(2)}s 验证成功`);
        
        const captchaVerification = secretKey
          ? aesEncrypt(
              backToken + '---' + JSON.stringify({ x: scaledDistance, y: 5.0 }),
              secretKey
            )
          : backToken + '---' + JSON.stringify({ x: scaledDistance, y: 5.0 });
        
        setTimeout(() => {
          setTipWords('');
          if (mode === 'pop') {
            onSuccess(captchaVerification);
          }
        }, 1000);
      } else {
        setMoveBlockBackgroundColor('#d9534f');
        setLeftBarBorderColor('#d9534f');
        setIconColor('#fff');
        setIconClass('icon-close');
        setPassFlag(false);
        
        setTimeout(() => {
          refresh();
        }, 1000);
        
        setTipWords('验证失败');
        setTimeout(() => {
          setTipWords('');
        }, 1000);
      }
    } catch (error) {
      Message.error('验证失败');
    }
  };

  // 组件挂载时初始化事件监听器
  useEffect(() => {
    const handleMouseMove = (e: MouseEvent) => move(e);
    const handleTouchMove = (e: TouchEvent) => move(e);
    const handleMouseUp = () => end();
    const handleTouchEnd = () => end();
    
    window.addEventListener('mousemove', handleMouseMove);
    window.addEventListener('touchmove', handleTouchMove);
    window.addEventListener('mouseup', handleMouseUp);
    window.addEventListener('touchend', handleTouchEnd);
    
    return () => {
      window.removeEventListener('mousemove', handleMouseMove);
      window.removeEventListener('touchmove', handleTouchMove);
      window.removeEventListener('mouseup', handleMouseUp);
      window.removeEventListener('touchend', handleTouchEnd);
    };
  }, [status, isEnd, moveBlockLeft, startLeft, setSize.imgWidth]);

  // 组件显示时初始化
  useEffect(() => {
    if (visible) {
      init();
    }
  }, [visible]);

  return (
    <Modal
      title="安全验证"
      visible={visible}
      onCancel={onCancel}
      footer={null}
      alignCenter={false}
      style={{ top: '30%', width: '350px' }}
    >
      <div style={{ position: 'relative' }}>
        <div 
          className="verify-img-out"
          style={{ 
            height: parseInt(setSize.imgHeight) + 5 + 'px',
            position: 'relative'
          }}
        >
          <div 
            className="verify-img-panel"
            style={{ 
              position: 'relative',
              width: setSize.imgWidth,
              height: setSize.imgHeight
            }}
          >
            {backImgBase && (
              <img
                src={`data:image/png;base64,${backImgBase}`}
                alt=""
                style={{ 
                  display: 'block', 
                  width: '100%', 
                  height: '100%',
                  borderRadius: '4px'
                }}
              />
            )}
            
            {showRefresh && (
              <div 
                className="verify-refresh"
                onClick={refresh}
                style={{
                  position: 'absolute',
                  top: 0,
                  right: 0,
                  zIndex: 2,
                  width: '25px',
                  height: '25px',
                  padding: '5px',
                  textAlign: 'center',
                  cursor: 'pointer',
                  background: 'rgba(0, 0, 0, 0.3)',
                  borderRadius: '4px'
                }}
              >
                <IconRefresh style={{ color: '#fff' }} />
              </div>
            )}
            
            {tipWords && (
              <span 
                className={`verify-tips ${passFlag ? 'suc-bg' : 'err-bg'}`}
                style={{
                  position: 'absolute',
                  bottom: 0,
                  left: 0,
                  width: '100%',
                  height: '30px',
                  lineHeight: '30px',
                  color: '#fff',
                  textIndent: '10px',
                  backgroundColor: passFlag ? 'rgba(92, 184, 92, 0.5)' : 'rgba(217, 83, 79, 0.5)'
                }}
              >
                {tipWords}
              </span>
            )}
            
            {blockBackImgBase && (
              <div
                className="verify-sub-block"
                style={{
                  position: 'absolute',
                  zIndex: 3,
                  width: Math.floor((parseInt(setSize.imgWidth) * 47) / 310) + 'px',
                  height: setSize.imgHeight,
                  top: '-' + (parseInt(setSize.imgHeight) + 5) + 'px',
                  backgroundSize: setSize.imgWidth + ' ' + setSize.imgHeight,
                  display: isEnd ? 'none' : 'block'
                }}
              >
                <img
                  src={`data:image/png;base64,${blockBackImgBase}`}
                  alt=""
                  style={{ 
                    display: 'block', 
                    width: '100%', 
                    height: '100%',
                    WebkitUserDrag: 'none',
                    userDrag: 'none'
                  }}
                />
              </div>
            )}
          </div>
        </div>
        
        {/* 滑动条区域 */}
        <div
          ref={barAreaRef}
          className="verify-bar-area"
          style={{
            position: 'relative',
            textAlign: 'center',
            background: '#f7f9fa',
            borderRadius: '4px',
            border: `1px solid ${leftBarBorderColor}`,
            width: setSize.imgWidth,
            height: '40px',
            lineHeight: '40px',
            boxSizing: 'content-box',
            marginTop: '10px'
          }}
        >
          <span className="verify-msg" style={{ color: '#4d545d' }}>{text}</span>
          <span className="verify-msg" style={{ color: '#4d545d' }}>{finishText}</span>
          
          <div
            ref={moveBlockRef}
            className="verify-move-block"
            onMouseDown={start}
            onTouchStart={start}
            style={{
              position: 'absolute',
              top: 0,
              left: moveBlockLeft,
              width: '40px',
              height: '40px',
              backgroundColor: moveBlockBackgroundColor,
              cursor: 'pointer',
              borderRadius: '4px',
              boxShadow: '0 0 2px #888',
              transition: transitionLeft,
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center'
            }}
          >
            <div 
              className={`verify-icon iconfont ${iconClass}`}
              style={{ 
                color: iconColor,
                fontSize: '18px'
              }}
            />
          </div>
          
          <div
            className="verify-left-bar"
            style={{
              position: 'absolute',
              top: '-1px',
              left: '-1px',
              height: '38px',
              width: leftBarWidth,
              backgroundColor: '#D9EDFE',
              border: `1px solid ${leftBarBorderColor}`,
              borderRadius: '4px',
              transition: transitionWidth
            }}
          />
        </div>
      </div>
    </Modal>
  );
};

export default SliderCaptcha;