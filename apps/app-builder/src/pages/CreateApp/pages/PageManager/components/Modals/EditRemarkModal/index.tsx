import { useEffect, useState } from 'react';
import { Modal, Input, Message } from '@arco-design/web-react';
import type { EditRemarkModal } from './indexType';
import { updateVersionAlias } from '@onebase/app/src/services';
import styles from './index.module.less';

export default function EditRemarkModal({ visible, setVisible, currentItem, getVersionMgmtData,getVersonList }: EditRemarkModal) {
  const [remark, setRemark] = useState<string>('');
  const submit = () => {
    updateVersionAlias({ id: currentItem.id, bpmVersionAlias: remark }).then((res: any) => {
      Message.success('修改成功');
      getVersionMgmtData();
      setVisible(false)
      getVersonList()
    });
  };

  useEffect(() => {
    setRemark(currentItem.bpmVersionAlias);
  }, [visible]);
  return (
    <Modal
      onOk={submit}
      onCancel={() => setVisible(false)}
      className={styles.editRemarkModal}
      title="修改备注"
      visible={visible}
    >
      <div className={styles.editRemarkBox}>
        <div className={styles.label}>流程版本备注: </div>
        <Input value={remark} onChange={(value) => setRemark(value)} placeholder="" />
      </div>
    </Modal>
  );
}
