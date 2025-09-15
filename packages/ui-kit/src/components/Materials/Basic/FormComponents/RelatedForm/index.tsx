import { Form, Select } from '@arco-design/web-react';
import { nanoid } from 'nanoid';
import { memo, useState } from 'react';
// import { useAppEntityStore } from 'src/store/store_entity';
import { dataMethodPage, type AppEntityField, type PageMethodParam } from '@onebase/app';
import { STATUS_OPTIONS, STATUS_VALUES } from 'src/components/Materials/constants';
import { FORM_COMPONENT_TYPES } from '../../../componentTypes';
import '../index.css';
import { type XRelatedFormConfig } from './schema';

const XRelatedForm = memo((props: XRelatedFormConfig & { runtime?: boolean }) => {
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
    runtime = true
  } = props;

  const [options, setOptions] = useState<any[]>([]);

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
          value: item[relatedField.fieldId]
        })),
        // TODO(mickey): remove
        { label: '牛逼', value: 233333 }
      ]);
    }
  };

  return (
    <div className="formWrapper">
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
        hidden={runtime && status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN]}
        style={{
          margin: 0,
          opacity: status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN] ? 0.4 : 1
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
            backgroundColor: bgColor,
            pointerEvents: runtime ? 'unset' : 'none'
          }}
        />
      </Form.Item>
    </div>
  );
});

export default XRelatedForm;
