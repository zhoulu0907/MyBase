import { useEffect, useState, type FC } from 'react';
import { Button, Input } from '@arco-design/web-react';
import { IconPlus, IconSearch, IconDownload } from '@arco-design/web-react/icon';

import styles from './index.module.less';

const AppPermission: FC = () => {
  const handleSearchChange = () => {};
  const handleAdd = () => {};
  return (
    <div className={styles.datasetPage}>
      <div className={styles.dataFilter}>
        <div className={styles.datasetTitle}>大屏模板</div>
        <div>
          <Button style={{ marginRight: '6px' }} type="outline" icon={<IconDownload />} onClick={handleAdd}>
            导入模板
          </Button>
          <Button type="primary" icon={<IconPlus />} onClick={handleAdd}>
            新建模板
          </Button>
        </div>
      </div>
      <div className={styles.dataFilter}>
        <div>应用模板</div>
        <Input
          className={styles.appInput}
          allowClear
          suffix={<IconSearch />}
          onChange={handleSearchChange}
          placeholder="搜索"
        />
      </div>
    </div>
  );
};
export default AppPermission;
