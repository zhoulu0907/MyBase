import { Drawer } from '@arco-design/web-react';
import type { DrawerProps } from '@arco-design/web-react';
import { IconLeft } from '@arco-design/web-react/icon';
import type { ReactNode } from 'react';
import { useCallback } from 'react';
import styles from './index.module.less';

type BaseDrawerProps = Omit<DrawerProps, 'visible' | 'title' | 'children' | 'className'>;

export interface ConfigDrawerProps extends BaseDrawerProps {
  visible: boolean;
  title?: ReactNode;
  onClose?: () => void;
  className?: string;
  children?: ReactNode;
  getContainer?: () => HTMLElement | null;
}

const ConfigDrawer = ({
  visible,
  title,
  width = '310px',
  onClose,
  children,
  mask = false,
  maskClosable = false,
  getContainer,
  placement = 'right',
  unmountOnExit = true,
  ...rest
}: ConfigDrawerProps) => {
  const getPopupContainer = useCallback(() => {
    // return getContainer?.() ?? document.querySelector<HTMLElement>(DEFAULT_CONTAINER_SELECTOR) ?? document.body;
    return document.body;
  }, [getContainer]);

  return (
    <Drawer
      {...rest}
      visible={visible}
      width={width}
      placement={placement}
      mask={mask}
      maskClosable={maskClosable}
      closable={false}
      getPopupContainer={getPopupContainer}
      className={styles.drawer}
      onCancel={onClose}
      unmountOnExit={unmountOnExit}
      title={
        <div className={styles.header}>
          <button type="button" className={styles.backBtn} onClick={onClose}>
            <IconLeft />
          </button>
          {title && <div className={styles.title}>{title}</div>}
        </div>
      }
      footer={null}
    >
      {children}
    </Drawer>
  );
};

export default ConfigDrawer;
