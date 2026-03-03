import { Form, Select } from '@arco-design/web-react';
import { useEffect, useState } from 'react';
import { LIST_COMPONENT_TYPES, CONFIG_TYPES, useListEditorSignal } from '@onebase/ui-kit';
import type { SelectOption } from '@onebase/app';
import { useSignals } from '@preact/signals-react/runtime';
import { registerConfigRenderer } from '../../registry';
import styles from '../../index.module.less';

interface Props {
  handlePropsChange: (key: string, value: any) => void;
  item: any;
  configs: any;
}

const DynamicGroupFilterConfig = ({ handlePropsChange, item, configs }: Props) => {
  useSignals();
  const { pageComponentSchemas } = useListEditorSignal;
  const groupFilterKey = 'groupFilter';

  const [groupList, setGroupList] = useState<SelectOption[]>([]);

  useEffect(() => {
    getGroupList();
  }, []);

  // 当前视图 树形目录组件
  const getGroupList = () => {
    let newGroupList: SelectOption[] = [];
    Object.entries(pageComponentSchemas.value).forEach(([key, value]: [string, any]) => {
      if (value && value.type === LIST_COMPONENT_TYPES.TREE) {
        newGroupList.push({
          label: value.config?.label?.text || '',
          value: value.id
        });
      }
    });
    setGroupList(newGroupList);
  };

  return (
    <Form.Item className={styles.formItem} label="绑定分组筛选">
      <Select
        options={groupList}
        allowClear
        value={configs[groupFilterKey]}
        onChange={(value) => {
          handlePropsChange(groupFilterKey, value);
        }}
      ></Select>
    </Form.Item>
  );
};

export default DynamicGroupFilterConfig;

registerConfigRenderer(CONFIG_TYPES.GROUP_FILTER, ({ handlePropsChange, item, configs }) => (
  <DynamicGroupFilterConfig handlePropsChange={handlePropsChange} item={item} configs={configs} />
));
