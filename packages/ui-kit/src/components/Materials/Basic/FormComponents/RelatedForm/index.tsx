import { Form, Select } from '@arco-design/web-react';
import { nanoid } from 'nanoid';
import { memo, useState } from 'react';
// import { useAppEntityStore } from 'src/store/store_entity';
import { dataMethodPage, type AppEntityField, type PageMethodParam } from '@onebase/app';
import { STATUS_OPTIONS, STATUS_VALUES } from 'src/components/Materials/constants';
import { FORM_COMPONENT_TYPES } from '../../../componentTypes';
import './index.css';
import { type XRelatedFormConfig } from './schema';

const XRelatedForm = memo((props: XRelatedFormConfig & { runtime?: boolean }) => {
  const { runtime = true } = props;
  //   const { appEntities } = useAppEntityStore();

  const {
    label,
    dataField,
    // relatedFormDataField,
    placeholder,
    tooltip,
    status,
    // defaultValue,
    verify,
    align,
    layout,
    color,
    bgColor,
    labelColSpan = 0,
    description
  } = props;

  const [options, setOptions] = useState<any[]>([]);

  //   useEffect(() => {
  //     console.log('relatedFormDataField: ', relatedFormDataField);
  //     // console.log('appEntities: ', appEntities);

  //     if (relatedFormDataField.length == 2 && appEntities.entities.length >= 0) {
  //       const relatedEntity = appEntities.entities.find((entity) => entity.entityID === relatedFormDataField[0]);
  //       const relatedField = relatedEntity?.fields.find((field) => field.fieldID === relatedFormDataField[1]);

  //       console.log(relatedEntity?.entityID, relatedField);
  //       if (relatedEntity?.entityID && relatedField) {
  //         handleGetRelatedData(relatedEntity?.entityID, relatedField);
  //       }

  //       //   console.log(relatedFormDataField);
  //       //   console.log(appEntities);
  //     }
  //   }, [appEntities, relatedFormDataField]);

  const handleGetRelatedData = async (entityId: string, relatedField: AppEntityField) => {
    console.log('runtime: ', runtime);
    if (!runtime) {
      return;
    }
    // TODO(mickey) 分页问题
    const req: PageMethodParam = {
      entityId: entityId,
      pageNo: 1,
      pageSize: 100
    };

    console.log(req);
    const res = await dataMethodPage(req);
    console.log(res);

    if (res && res.list) {
      setOptions([
        ...(res.list || []).map((item: any) => ({
          label: item[relatedField.displayName],
          value: item[relatedField.fieldID]
        })),
        // TODO(mickey): remove
        { label: '牛逼', value: 233333 }
      ]);
    }
  };

  return (
    <Form.Item
      label={label.display && label.text}
      field={
        dataField.length > 0 ? dataField[dataField.length - 1] : `${FORM_COMPONENT_TYPES.RELATED_FORM}_${nanoid()}`
      }
      layout={layout}
      tooltip={tooltip}
      labelCol={{
        style: { width: labelColSpan, flex: 'unset' }
      }}
      wrapperCol={{ style: { flex: 1 } }}
      rules={[{ required: verify?.required }]}
      style={{
        opacity: status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN] ? 0.5 : 1,
        pointerEvents: status === STATUS_VALUES[STATUS_OPTIONS.READONLY] ? 'none' : 'unset',
        margin: '0px'
      }}
    >
      <Select
        placeholder={placeholder}
        showSearch
        options={options}
        style={{
          width: '100%',
          color,
          textAlign: align,
          backgroundColor: bgColor
        }}
      />
    </Form.Item>
  );
});

export default XRelatedForm;
