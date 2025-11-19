
import { Cell, Collapse, Form, Picker } from '@arco-design/mobile-react';
import { nanoid } from 'nanoid';
import { memo, useEffect, useState } from 'react';
import { FORM_COMPONENT_TYPES } from '../../../componentTypes';
import { STATUS_OPTIONS, STATUS_VALUES } from '../../../constants';
import type { XSubTableConfig } from './schema';
import { getSimpleUserList, UserVO } from '@onebase/platform-center';
import { IconDelete } from '@arco-design/mobile-react/esm/icon';
import { pagesRuntimeSignal } from '@onebase/common';


import styles from './index.module.css';

const XSubTable = memo((props: XSubTableConfig & { runtime?: boolean; detailMode?: boolean; defaultOptionsConfig?: any; }) => {
  const {
    label,
    tooltip,
    status,
    verify,
    layout,
    defaultOptionsConfig,
    runtime = true,
    detailMode
  } = props;

  // const {
  //   subEntities,
  //   setSubEntities
  // } = pagesRuntimeSignal;

  const { subEntities, setSubEntities, subTableDataLength } = pagesRuntimeSignal;

  // const [userData, setUserData] = useState<UserVO[]>([]);
  console.log(subEntities.value, 'subEntities222', subTableDataLength.value)


  useEffect(() => {
    console.log(subEntities.value, 'subEntities', subTableDataLength.value)
    // fetchUserData();
  }, [subEntities.value]);

  // const fetchUserData = async () => {
  //   const res = await getSimpleUserList();
  //   setUserData(res);
  // };

  const handleDelete = (e) => {
    e.stopPropagation();
    console.log('delete this')
  };

  return (
    <Cell label={'子表'} append={
      <Collapse
        className={styles.collapse}
        header={
          <div className={styles.collapseHeader}>
            #1
            <IconDelete onClick={handleDelete} />
          </div>
        }
        value="1"
        defaultActive
        content={
          <Cell.Group>
            <Cell label={'xxx'}>
              333
            </Cell>
            <Cell label="List Content" text='555' />
            <Cell label="List Content" text='666' showArrow />
            <Cell label="List Content" showArrow />
          </Cell.Group>
        }
      />
    }>
    </Cell>
  );
});

export default XSubTable;