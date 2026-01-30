import { Modal } from '@arco-design/web-react';
import { IconLeft } from '@arco-design/web-react/icon';
import type { CSSProperties, ReactNode } from 'react';
import { memo, useCallback, useEffect, useState } from 'react';
import styles from './index.module.css';

interface CheckMoreModalProps {
  visible: boolean;
  onClose: () => void;
  runtime?: boolean;
  preview?: boolean;
  title?: string;
  contentClassName?: string;
  children: ReactNode;
}

const CheckMoreModal = memo((props: CheckMoreModalProps) => {
  const { visible, onClose, runtime, preview, title, contentClassName = '', children } = props;
  const [modalStyle, setModalStyle] = useState<CSSProperties>({});
  const titleLabel = title || '快捷入口';

  const getPopupContainer = useCallback(() => {
    if (runtime) {
      return document.querySelector('.runtime-preview-formpage') || document.body;
    }

    if (preview) {
      const stableContainer = document.querySelector('.previewPage') ||
        document.querySelector('[class*="previewPage"]') ||
        document.querySelector('[class*="preview-page"]');

      if (stableContainer && stableContainer.childNodes.length > 0) {
        return stableContainer.childNodes[0] as HTMLElement;
      }

      return document.body;
    }

    return document.body;
  }, [runtime, preview]);

  const calculateModalStyle = useCallback(() => {
    const container = getPopupContainer();
    if (!container) {
      return {};
    }

    const containerRect = container.getBoundingClientRect();

    if (runtime) {
      return {
        position: 'fixed' as const,
        top: `${containerRect.top}px`,
        left: `${containerRect.left}px`,
        width: `${containerRect.width}px`,
        height: `${containerRect.height}px`,
      };
    }

    if (preview) {
      return {
        position: 'fixed' as const,
        top: `116px`,
        left: `${containerRect.left}px`,
        width: `${containerRect.width}px`,
        height: `${containerRect.height}px`,
      };
    }
  }, [preview, getPopupContainer]);

  useEffect(() => {
    if (visible) {
      requestAnimationFrame(() => {
        const style = calculateModalStyle();
        setModalStyle(style || {});
      });
    }
  }, [visible, calculateModalStyle]);

  return (
    <Modal
      title={
        <div style={{ display: 'flex', alignItems: 'center' }}>
          <IconLeft
            style={{ cursor: 'pointer', fontSize: '18px', marginRight: '10px' }}
            onClick={onClose}
          />
          <span>{titleLabel}</span>
        </div>
      }
      visible={visible}
      onCancel={onClose}
      closable={false}
      footer={null}
      getPopupContainer={getPopupContainer}
      style={modalStyle}
      wrapClassName={styles.quickEntryModalWrap}
      className={styles.quickEntryModal}
      mask={false}
    >
      <div className={contentClassName}>
        {children}
      </div>
    </Modal>
  );
});

export default CheckMoreModal;
