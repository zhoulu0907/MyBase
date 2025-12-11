import { useEffect, useState, type FC } from 'react';
import { Button, Input, Tabs, Typography } from '@arco-design/web-react';
import { IconPlus, IconSearch, IconDownload } from '@arco-design/web-react/icon';
import TemplateCard from '../TemplateCard';
import styles from './index.module.less';
const TabPane = Tabs.TabPane;
const ScreenTemplate: FC = () => {
  const handleSearchChange = () => {};
  const handleAdd = () => {};
  interface applicationDataList {
    id: string;
    name: string;
  }
  interface systemDataList {
    id: string;
    name: string;
  }

  const [applicationDataList, setApplicationDataList] = useState<applicationDataList[]>();
  const [systemDataList, setSystemDataList] = useState<systemDataList[]>();
  useEffect(() => {
    setApplicationDataList([
      {
        id: '1',
        name: '应用1'
      }
    ]);
    setSystemDataList([
      {
        id: '1',
        name: '系统1'
      }
    ]);
  }, [applicationDataList, systemDataList]);
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
      <div className={styles.dataContent}>
        <Tabs defaultActiveTab="1">
          <TabPane key="1" title="应用模板">
            <Typography.Paragraph>
              <div className={styles.appList}>
                {applicationDataList?.map((item) => (
                  <TemplateCard key={item.id} item={item} />
                ))}
              </div>
            </Typography.Paragraph>
          </TabPane>
          <TabPane key="2" title="系统模板">
            <Typography.Paragraph>
              <div className={styles.appList}>
                {systemDataList?.map((item) => (
                  <TemplateCard key={item.id} item={item} />
                ))}
              </div>
            </Typography.Paragraph>
          </TabPane>
        </Tabs>
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
export default ScreenTemplate;
