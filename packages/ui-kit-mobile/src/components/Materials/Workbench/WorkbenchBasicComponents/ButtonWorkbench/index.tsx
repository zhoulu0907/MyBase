import { memo, useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { Button } from '@arco-design/mobile-react';
import { workbenchSchema } from '@onebase/ui-kit';
import { ApplicationMenu, listApplicationMenu } from '@onebase/app';
import styles from './index.module.css';

type XButtonWorkbenchConfig = typeof workbenchSchema.XButtonWorkbench.config;

const XButtonWorkbench = memo((props: XButtonWorkbenchConfig & { runtime?: boolean; detailMode?: boolean }) => {
  const {
    id,
    label,
    runtime = true,
    backgroundColor,
    textColor,
    textSize,
    textAlign,
    jumpType,
    jumpPageId,
    jumpExternalUrl,
    status
  } = props;

  const textAlignHorizontal = textAlign.horizontal || 'center';
  const textAlignVertical = textAlign.vertical || 'middle';

  const { appId } = useParams<{ appId?: string }>();
  const [appRuntimeMenu, setAppRuntimeMenu] = useState<ApplicationMenu[]>([]);

  const navigate = useNavigate();

  // 获取应用运行态菜单数据
  useEffect(() => {
    if (!runtime || !appId) return;
    getApplicationMenu();
  }, [runtime, appId]);

  const getApplicationMenu = async () => {
    if (appId) {
      const res = await listApplicationMenu({ applicationId: appId });
      setAppRuntimeMenu(res || []);
    }
  };

  // 处理按钮点击
  const handleButtonClick = () => {
    if (!runtime) return;

    if (jumpType === 'external') {
      // 跳转外部链接
      if (jumpExternalUrl) {
        if (jumpExternalUrl.startsWith('http://') || jumpExternalUrl.startsWith('https://')) {
          window.open(jumpExternalUrl, '_blank');
        } else {
          navigate(jumpExternalUrl);
        }
      }
    } else if (jumpType === 'internal') {
      // 关联已有页面
      if (jumpPageId) {
        const targetMenu = appRuntimeMenu.find(
          (menu) => menu.menuUuid === jumpPageId);

        if (targetMenu && targetMenu.id) {
          // 获取当前URL的查询参数，更新或添加 curMenu 参数
          const searchParams = new URLSearchParams(location.search);
          searchParams.set('curMenu', targetMenu.id);
          // 将 /runtime-home 替换为 /runtime
          const newPath = location.pathname.replace('/runtime-home', '/runtime');
          const to = `${newPath}?${searchParams.toString()}`;
          navigate(to);
        }
      }
    }
  };

  // 将垂直对齐值转换为 CSS 的 alignItems 值
  const getAlignItems = (verticalAlign: string) => {
    switch (verticalAlign) {
      case 'top':
        return 'flex-start';
      case 'bottom':
        return 'flex-end';
      case 'middle':
      default:
        return 'center';
    }
  };

  // 按钮样式
  const buttonStyle: React.CSSProperties = {
    width: '100%',
    textAlign: textAlignHorizontal as 'left' | 'center' | 'right',
    borderRadius: '4px',
    border: 'none',
    cursor: runtime ? 'pointer' : 'default',
    boxSizing: 'border-box' as const,
    display: 'flex',
    alignItems: getAlignItems(textAlignVertical),
    justifyContent: textAlignHorizontal === 'left' ? 'flex-start' : textAlignHorizontal === 'right' ? 'flex-end' : 'center',
  };

  const buttonTextStyle: React.CSSProperties = {
    fontSize: `${textSize}px`,
  };

  // 按钮文本
  const buttonText = label?.text || '按钮';

  return (
    <div className={styles.buttonWrapper}>
      {label?.display && label?.text && (
        <div className={styles.title}>{label.text}</div>
      )}
      <div
        className={styles.button}
        style={
          {
            '--custom-bg-color': backgroundColor || undefined
          } as React.CSSProperties
        }
      >
        <Button
          inline
          size="huge"
          needActive
          style={buttonStyle}
          onClick={handleButtonClick}
          disabled={!runtime}
          color={textColor}
          bgColor={backgroundColor}
        >
          <span style={buttonTextStyle}>{buttonText}</span>
        </Button>
      </div>
    </div>
  );
});

export default XButtonWorkbench;
