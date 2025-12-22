
import { memo, useEffect, useState } from 'react';
import { nanoid } from 'nanoid';
import { Ellipsis, Form, Picker } from '@arco-design/mobile-react';
import { ValidatorType, ITypeRules } from '@arco-design/mobile-utils';
import { getDeptUser, UserVO } from '@onebase/platform-center';
import { FORM_COMPONENT_TYPES, STATUS_OPTIONS, STATUS_VALUES, FormSchema } from '@onebase/ui-kit';
import '../index.css';

type XUserSelectConfig = typeof FormSchema.XUserSelectSchema.config;
const XUserSelect = memo((props: XUserSelectConfig & { runtime?: boolean; detailMode?: boolean; defaultOptionsConfig?: any; form?: any; }) => {
  const {
    label,
    dataField,
    status,
    verify,
    layout,
    runtime = true,
    detailMode,
    form
  } = props;

  const [userData, setUserData] = useState<UserVO[]>([]);
  
  // 生成唯一的字段ID
  const fieldId = dataField && dataField.length > 0
  ? dataField[dataField.length - 1]
  : `${FORM_COMPONENT_TYPES.USER_SELECT}_${nanoid()}`;

  useEffect(() => {
    userData.length === 0 && fetchUserData();
  }, [userData]);

  const fetchUserData = async () => {
    const res = await getDeptUser();
    setUserData(res.userList);
  };

  const rules: ITypeRules<ValidatorType.Custom>[] = [
    {
      required: verify?.required,
      type: ValidatorType.Custom,
      message: `${label.text}是必填项`
    }
  ];

  const readonlyValue = Array.isArray(form?.getFieldValue(fieldId)) ? form?.getFieldValue(fieldId)[0] : '--';

  return (
    <Form.Item
      className="inputTextWrapperOBMobile"
      label={label.display && <Ellipsis text={label.text} />}
      field={fieldId}
      rules={rules}
      style={{
        textAlign: 'right',
        pointerEvents: (!runtime || detailMode) ? 'none' : 'unset',
        opacity: status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN] ? 0.4 : 1
      }}
    >
      {status === STATUS_VALUES[STATUS_OPTIONS.READONLY] || detailMode ? (
        <div className="readonlyText">{readonlyValue}</div>
      ) : (
        <Picker
          cascade={false}
          data={[userData.map(v => v.nickname)]}
          maskClosable
          onChange={(val) => {
            const id = val[0];
            const target = userData.find(item => item.nickname === id);
            if (target) {
              form?.setFieldValue(fieldId, {
                id: target.id,
                name: target.nickname,
              });
            }
          }}
        />
      )}
    </Form.Item>
  );
});

export default XUserSelect;