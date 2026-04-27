import { useState } from 'react';
import { Button, Form } from '@arco-design/web-react';
import { CONFIG_TYPES, useAppEntityStore } from '@onebase/ui-kit';
import { registerConfigRenderer } from '../../registry';
import SortDataModal from './components/SortDataModal';
import SortIconSVG from '@/assets/images/sort_icon.svg';
import EditIconSVG from '@/assets/images/app_edit_black.svg';
import styles from './index.module.less';

interface Props {
  handlePropsChange: (key: string, value: any) => void;
  item: any;
  configs: any;
}

const SORTBY_KEY = 'sortByObject';

const DynamicSortByConfig = ({ handlePropsChange, item, configs }: Props) => {
  const [sortDataVisible, setSortDataVisible] = useState(false);

  const { mainEntity, subEntities } = useAppEntityStore();

  const handleSave = (values: any[]) => {
    handlePropsChange(SORTBY_KEY, values);
    setSortDataVisible(false);
  };

  return (
    <Form.Item className={styles.formItem} label="数据排序规则">
      {configs[SORTBY_KEY]?.length ? (
        <Button className={styles.SortedButton} type="secondary" long onClick={() => setSortDataVisible(true)}>
          {configs[SORTBY_KEY]?.length}个排序
          <img src={EditIconSVG} />
        </Button>
      ) : (
        <Button
          type="secondary"
          long
          onClick={() => setSortDataVisible(true)}
          icon={<img className={styles.defaultButtonIcon} src={SortIconSVG} />}
        >
          配置排序规则
        </Button>
      )}
      <SortDataModal
        visible={sortDataVisible}
        sortBy={SORTBY_KEY}
        configs={configs}
        mainEntity={mainEntity}
        subEntities={subEntities}
        onCancel={() => setSortDataVisible(false)}
        onOk={handleSave}
      />
    </Form.Item>
  );
};

export default DynamicSortByConfig;

registerConfigRenderer(CONFIG_TYPES.DATA_SORT_BY, ({ handlePropsChange, item, configs }) => (
  <DynamicSortByConfig handlePropsChange={handlePropsChange} item={item} configs={configs} />
));
