import { Form, Select, Radio } from '@arco-design/web-react';
import { useEffect, useRef, useState } from 'react';
import { CONFIG_TYPES, ENTITY_FIELD_TYPE, getPopupContainer, useAppEntityStore } from '@onebase/ui-kit';
import { type AppEntityField } from '@onebase/app';
import { registerConfigRenderer } from '../../registry';
import styles from '../../index.module.less';

interface Props {
  handlePropsChange: (key: string, value: any) => void;
  item: any;
  configs: any;
}

const DynamicCoverImageConfig = ({ handlePropsChange, item, configs }: Props) => {
  const coverFieldKey = 'coverField';
  const imageFillKey = 'imageFill';
  const { mainEntity, subEntities } = useAppEntityStore();

  const [fieldList, setFieldList] = useState<AppEntityField[]>([]);

  useEffect(() => {
    if (!configs?.metaData) {
      setFieldList([]);
      return;
    }
    if (configs.metaData === mainEntity.entityUuid) {
      setFieldList(mainEntity.fields.filter((ele) => ele.fieldType === ENTITY_FIELD_TYPE.IMAGE.VALUE));
    } else {
      const subEntity = subEntities.entities.find((ele) => ele.entityUuid === configs.metaData);
      if (subEntity) {
        setFieldList(subEntity.fields.filter((ele) => ele.fieldType === ENTITY_FIELD_TYPE.IMAGE.VALUE));
      }
    }
  }, [configs?.metaData]);

  return (
    <Form.Item className={styles.formItem} label={item.name || '封面图片'}>
      <Select
        allowClear
        placeholder={`请选择${item.name}`}
        value={configs[coverFieldKey]}
        getPopupContainer={getPopupContainer}
        style={{ marginBottom: '8px' }}
        onChange={(value) => {
          handlePropsChange(coverFieldKey, value);
        }}
      >
        {fieldList.map((item) => (
          <Select.Option key={item.fieldName} value={item.fieldName}>
            {item.displayName}
          </Select.Option>
        ))}
      </Select>

      <Radio.Group
        value={configs[imageFillKey]}
        type="button"
        size="default"
        style={{ width: '100%', display: 'flex' }}
        onChange={(value) => {
          handlePropsChange(imageFillKey, value);
        }}
      >
        {item.range.map((option: any) => (
          <Radio key={option.value} value={option.value} style={{ flex: 1, textAlign: 'center', whiteSpace: 'nowrap' }}>
            {option.label}
          </Radio>
        ))}
      </Radio.Group>
    </Form.Item>
  );
};

export default DynamicCoverImageConfig;

registerConfigRenderer(CONFIG_TYPES.COVER_IMAGE, ({ handlePropsChange, item, configs }) => (
  <DynamicCoverImageConfig handlePropsChange={handlePropsChange} item={item} configs={configs} />
));
