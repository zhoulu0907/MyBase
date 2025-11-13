import { Form, Select } from '@arco-design/web-react';
import { dataMethodPage, menuSignal, type AppEntityField, type PageMethodParam } from '@onebase/app';
import { nanoid } from 'nanoid';
import { memo, useState } from 'react';
import { STATUS_OPTIONS, STATUS_VALUES } from 'src/components/Materials/constants';
import { FORM_COMPONENT_TYPES } from '../../../componentTypes';
import '../index.css';
import { type XRelatedFormConfig } from './schema';
import { getPopupContainer } from '@/utils';

const XRelatedForm = memo((props: XRelatedFormConfig & { runtime?: boolean; detailMode?: boolean }) => {
  const {
    label,
    dataField,
    // relatedFormDataField,
    placeholder,
    tooltip,
    status,
    layout,
    runtime = true
  } = props;

  const { curMenu } = menuSignal;
  const [options, setOptions] = useState<any[]>([]);

  const handleGetRelatedData = async (entityId: string, relatedField: AppEntityField) => {
    console.log('runtime: ', runtime);
    if (!runtime) {
      return;
    }
    // TODO(mickey) 分页问题
    const req: PageMethodParam = {
      menuId: curMenu.value?.id,
      entityId: entityId,
      pageNo: 1,
      pageSize: 100
    };

    const res = await dataMethodPage(req);

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
        label={
          label.display &&
          label.text && <span className={tooltip ? 'tooltipLabelText' : 'labelText'}>{label.text}</span>
        }
        field={
          dataField.length > 0 ? dataField[dataField.length - 1] : `${FORM_COMPONENT_TYPES.RELATED_FORM}_${nanoid()}`
        }
        layout={layout}
        tooltip={tooltip}
        wrapperCol={{ style: { flex: 1 } }}
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
          getPopupContainer={getPopupContainer}
          style={{
            width: '100%',
            pointerEvents: runtime ? 'unset' : 'none'
          }}
        />
      </Form.Item>
    </div>
  );
});

export default XRelatedForm;
