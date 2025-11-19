
import { Form, Picker } from '@arco-design/mobile-react';
import { nanoid } from 'nanoid';
import { memo, useEffect, useState } from 'react';
import { FORM_COMPONENT_TYPES } from '../../../componentTypes';
import { STATUS_OPTIONS, STATUS_VALUES } from '../../../constants';
import type { XInputUserSelectConfig } from './schema';
import { getSimpleUserList, UserVO } from '@onebase/platform-center';
import '../index.css';

const XUserSelect = memo((props: XInputUserSelectConfig & { runtime?: boolean; detailMode?: boolean; defaultOptionsConfig?: any; }) => {
  const {
    label,
    dataField,
    tooltip,
    status,
    verify,
    layout,
    labelColSpan = 0,
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

  return (
    <Form.Item
      className="inputTextWrapper"
      label={label.display && label.text}
      field={fieldId}
      required={verify?.required}
      style={{
        textAlign: 'right',
        pointerEvents: runtime ? 'unset' : 'none'
      }}
    >
      {status === STATUS_VALUES[STATUS_OPTIONS.READONLY] || !runtime || detailMode ? (
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