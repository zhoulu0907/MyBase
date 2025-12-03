
import { memo, useEffect, useState } from 'react';
import { nanoid } from 'nanoid';
import { Form, Picker } from '@arco-design/mobile-react';
import { ValidatorType, ITypeRules } from '@arco-design/mobile-utils';
import { getSimpleUserList, UserVO } from '@onebase/platform-center';
import { FORM_COMPONENT_TYPES, STATUS_OPTIONS, STATUS_VALUES, FormSchema } from '@onebase/ui-kit';
import '../index.css';

type XUserSelectConfig = typeof FormSchema.XUserSelectSchema.config;
const XUserSelect = memo((props: XUserSelectConfig & { runtime?: boolean; detailMode?: boolean; defaultOptionsConfig?: any; }) => {
  const {
    label,
    dataField,
    status,
    verify,
    layout,
    defaultOptionsConfig,
    runtime = true,
    detailMode
  } = props;

  const [userData, setUserData] = useState<UserVO[]>([]);

  // 生成唯一的字段ID
  const fieldId = dataField && dataField.length > 0
    ? dataField[dataField.length - 1]
    : `${FORM_COMPONENT_TYPES.INPUT_TEXT}_${nanoid()}`;

  useEffect(() => {
    fetchUserData();
  }, []);

  const fetchUserData = async () => {
    const res = await getSimpleUserList();
    setUserData(res);
  };

  const rules: ITypeRules<ValidatorType.Custom>[] = [
    {
      type: ValidatorType.Custom,
      validator: (value, callback) => {
        if (!value && verify?.required) {
          callback(`${label.text}是必填项`);
        }
      }
    }
  ];

  return (
    <Form.Item
      className="inputTextWrapperOBMobile"
      label={label.display && label.text}
      field={fieldId}
      rules={rules}
      style={{
        textAlign: 'right',
        pointerEvents: (!runtime || detailMode) ? 'none' : 'unset',
        opacity: status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN] ? 0.4 : 1
      }}
    >
      {status === STATUS_VALUES[STATUS_OPTIONS.READONLY] || detailMode ? (
        /* todo */
        <div>{defaultOptionsConfig.defaultOptions.find((item: any) => item.value == '')?.label || '--'}</div>
      ) : (
        <Picker
          cascade={false}
          data={[userData.map(v => v.nickname)]}
          maskClosable
        />
      )}
    </Form.Item>
  );
});

export default XUserSelect;