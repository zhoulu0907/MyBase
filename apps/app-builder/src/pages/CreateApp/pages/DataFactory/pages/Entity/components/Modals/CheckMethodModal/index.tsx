import { Modal } from '@arco-design/web-react';
import { getMethodDataById } from '@onebase/app';
import React, { useEffect, useState } from 'react';
import styles from '../modal.module.less';
import type { EntityListItem } from '../../EntityTable/types';

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
        console.log(res);
        if (res) {
          setMethodData(res);
        }
      });
    }
  }, [visible, methodCode, entity]);

  return (
    <Modal
      className={styles['check-method-modal']}
      title="查看数据方法"
      visible={visible}
      footer={false}
      onCancel={() => setVisible(false)}
    >
      <div className={styles['check-method-modal-content']}>
        <div className={styles['check-method-modal-content-header']}>
          <div className={styles['check-method-modal-content-header-title']}>{methodData?.methodName}</div>
        </div>
      </div>
    </Modal>
  );
};

export default CheckMethodModal;
