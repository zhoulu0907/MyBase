import { Divider } from '@arco-design/web-react';
import { STATUS_OPTIONS, STATUS_VALUES } from '@onebase/ui-kit';
import { v4 as uuidv4 } from 'uuid';
import type { GridItem } from '@onebase/ui-kit';
import type { WorkbenchComponentSchema, WorkbenchComponentOperation } from '../../../types/workbench-component';
import CompDeleteIcon from '@/assets/images/app_delete.svg';
import CompCopyIcon from '@/assets/images/copy_comp_icon.svg';
import CompShowIcon from '@/assets/images/eye_off_icon.svg';
import styles from '../index.module.less';

interface OperationButtonsProps {
  component: GridItem;
  pageComponentSchema: WorkbenchComponentSchema;
  onOperation: WorkbenchComponentOperation;
}

export function OperationButtons({ component, pageComponentSchema, onOperation }: OperationButtonsProps) {
  const isHidden = pageComponentSchema.config.status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN];

  const handleShow = (e: React.MouseEvent) => {
    e.stopPropagation();
    onOperation.show(component.id);
  };

  const handleCopy = (e: React.MouseEvent) => {
    e.stopPropagation();
    onOperation.copy({ ...component, id: `${component.type}-${uuidv4()}` }, component.id);
  };

  const handleDelete = (e: React.MouseEvent) => {
    e.stopPropagation();
    onOperation.delete(component.id);
  };

  return (
    <div className={styles.operationArea}>
      {isHidden && (
        <>
          <div className={styles.copyButton} onClick={handleShow}>
            <img src={CompShowIcon} alt="component show" />
          </div>
          <Divider className={styles.divider} type="vertical" />
        </>
      )}

      <div className={styles.copyButton} onClick={handleCopy}>
        <img src={CompCopyIcon} alt="component copy" />
      </div>
      <Divider className={styles.divider} type="vertical" />
      <div className={styles.deleteButton} onClick={handleDelete}>
        <img src={CompDeleteIcon} alt="component delete" />
      </div>
    </div>
  );
}
