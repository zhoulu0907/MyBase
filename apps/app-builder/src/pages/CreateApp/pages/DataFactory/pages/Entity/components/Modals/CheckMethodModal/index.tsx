import { Modal } from '@arco-design/web-react';
import { getMethodDataById } from '@onebase/app';
import React, { useEffect } from 'react';
import styles from '../modal.module.less';
import type { EntityListItem } from '../../EntityTable/types';

const CheckMethodModal: React.FC<{
  visible: boolean;
  setVisible: (visible: boolean) => void;
  entity: EntityListItem;
  methodId: string;
}> = ({ visible, setVisible, entity, methodId }) => {
  useEffect(() => {
    if (visible && methodId) {
      getMethodDataById({ id: methodId, entityId: entity.id }).then((res) => {
        console.log(res);
      });
    }
  }, [visible, methodId, entity]);

  return (
    <Modal
      className={styles['check-method-modal']}
      title="查看数据方法"
      visible={visible}
      footer={false}
      onCancel={() => setVisible(false)}
    >
      <div className={styles['check-method-modal-content']}></div>
    </Modal>
  );
};

export default CheckMethodModal;
