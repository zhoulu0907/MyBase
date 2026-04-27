import { Modal } from '@arco-design/web-react';
import { getMethodDataById } from '@onebase/app';
import React, { useEffect, useState } from 'react';
import type { EntityListItem } from '../../EntityTable/types';
import styles from '../modal.module.less';

const CheckMethodModal: React.FC<{
  visible: boolean;
  setVisible: (visible: boolean) => void;
  entity: EntityListItem;
  methodCode: string;
}> = ({ visible, setVisible, entity, methodCode }) => {
  const [methodData, setMethodData] = useState<any>(null);

  useEffect(() => {
    if (visible && methodCode) {
      getMethodDataById({ methodCode: methodCode, entityId: entity.id }).then((res) => {
        if (res) {
          setMethodData(res);
        }
      });
    }
  }, [visible, methodCode, entity]);

  return (
    <Modal
      className={styles.checkMethodModal}
      title="查看数据方法"
      visible={visible}
      footer={false}
      onCancel={() => setVisible(false)}
    >
      <div className={styles.checkMethodModalContent}>
        <div className={styles.checkMethodModalContentHeader}>
          <div className={styles.checkMethodModalContentHeaderTitle}>{methodData?.methodName}</div>
        </div>
      </div>
    </Modal>
  );
};

export default CheckMethodModal;
