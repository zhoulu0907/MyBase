import { useCallback, useEffect, useState } from 'react';
import { Button, Message, Modal, Radio, Select } from '@arco-design/web-react';
import { IconDelete, IconPlus } from '@arco-design/web-react/icon';
import SortableTable from './SortableTable';
import styles from '../index.module.less';
import { useSignals } from '@preact/signals-react/runtime';

// 辅助函数：处理数组移动
const arrayMoveMutate = (array, from, to) => {
  const startIndex = to < 0 ? array.length + to : to;
  if (startIndex >= 0 && startIndex < array.length) {
    const item = array.splice(from, 1)[0];
    array.splice(startIndex, 0, item);
  }
};

const arrayMove = (array, from, to) => {
  array = [...array];
  arrayMoveMutate(array, from, to);
  return array;
};

const SORT_TEXT_MAP = {
  NUMBER: { asc: '0 → 9', desc: '9 → 0' },
  DATE: { asc: '早 → 晚', desc: '晚 → 早' },
  DATETIME: { asc: '早 → 晚', desc: '晚 → 早' },
  BOOLEAN: { asc: '否 → 是', desc: '是 → 否' },
  SELECT: { asc: '选项顺序', desc: '选项倒序' },
  TEXT: { asc: 'A → Z', desc: 'Z → A' }
};

interface IProps {
  visible: boolean;
  sortBy: string;
  configs: any;
  mainEntity: any;
  subEntities: any;
  onCancel: () => void;
  onOk: (values: any) => void;
}

const SortDataModal: React.FC<IProps> = ({ visible, sortBy, configs, mainEntity, subEntities, onCancel, onOk }) => {
  useSignals();

  const [data, setData] = useState([]);
  const [fieldTypes, setFieldTypes] = useState<any[]>([]);

  useEffect(() => {
    if (configs['tableName'] === mainEntity.tableName) {
      setFieldTypes(mainEntity?.fields.filter((entity) => entity.isSystemField !== 1));
    } else if (configs['tableName'] === subEntities?.entities[0].tableName) {
      const curFields = subEntities?.entities.find((entries) => entries.tableName === configs['tableName']);
      setFieldTypes(curFields.fields.filter((entity) => entity.isSystemField !== 1));
    }
  }, [mainEntity, subEntities, configs['tableName']]);

  useEffect(() => {
    setData(configs[sortBy]);
  }, [configs[sortBy]]);

  const handleAddField = useCallback(() => {
    if (data.length >= 5) return Message.warning('最多支持添加 5 个排序规则');
    const newKey = Date.now().toString();
    const newItem = {
      key: newKey,
      fieldName: '',
      sortBy: 0
    };
    setData([...data, newItem]);
  }, [data]);

  const handleUpdate = (key, field, value) => {
    setData((prevData) => {
      return prevData.map((item) => {
        if (item.key === key) {
          // 创建新对象以触发渲染
          return { ...item, [field]: value };
        }
        return item;
      });
    });
  };

  const onSortEnd = ({ oldIndex, newIndex }) => {
    if (oldIndex !== newIndex) {
      const newData = arrayMove([].concat(data), oldIndex, newIndex).filter((el) => !!el);
      setData(newData);
    }
  };

  const columns = [
    {
      title: '字段类型',
      dataIndex: 'fieldName',
      width: 260,
      render: (val, record) => (
        <Select
          value={val}
          onChange={(newVal) => handleUpdate(record.key, 'fieldName', newVal)}
          style={{ width: '100%' }}
        >
          {fieldTypes.map((field, index) => (
            <Select.Option key={index} value={field.fieldName}>
              {field.displayName}
            </Select.Option>
          ))}
        </Select>
      )
    },
    {
      title: '排序条件',
      dataIndex: 'sortBy',
      width: 260,
      render: (val, record) => {
        const curFields = fieldTypes.find((field) => field.fieldName === record.fieldName);
        const isMatch = Object.keys(SORT_TEXT_MAP).includes(curFields?.fieldType);
        const key = isMatch ? curFields?.fieldType : 'TEXT';
        const text = SORT_TEXT_MAP[key];
        if (!text) return null;

        return (
          <div style={{ display: 'flex' }}>
            <Radio.Group
              type="button"
              value={val}
              onChange={(newOrder) => {
                console.log(newOrder, record);
                handleUpdate(record.key, 'sortBy', newOrder);
              }}
              style={{ width: '100%', display: 'flex' }}
            >
              <Radio value={0} style={{ flex: '1', textAlign: 'center' }}>{text.asc}</Radio>
              <Radio value={1} style={{ flex: '1', textAlign: 'center' }}>{text.desc}</Radio>
            </Radio.Group>
          </div>
        );
      }
    },
    {
      title: '操作',
      width: 50,
      render: (_, record) => (
        <Button type="text" icon={<IconDelete />} onClick={() => setData(data.filter((i) => i.key !== record.key))} />
      )
    }
  ];

  return (
    <Modal
      className={styles.configFieldModal}
      title="排序规则设置"
      visible={visible}
      onOk={() => onOk(data)}
      onCancel={onCancel}
      okText="保存"
      cancelText="取消"
      style={{ width: 1260 }}
      // 关闭自动聚焦与焦点锁
      autoFocus={false}
      focusLock={false}
    >
      <div className={styles.container}>
        设置数据排序条件
        <SortableTable data={data} onSortEnd={onSortEnd} columns={columns} />
        <div className={styles.addFieldSection}>
          <Button type="secondary" icon={<IconPlus />} onClick={handleAddField} className={styles.addFieldButton}>
            添加条件
          </Button>
        </div>
      </div>
    </Modal>
  );
};

SortDataModal.displayName = 'ConfigFieldModal';
export default SortDataModal;
