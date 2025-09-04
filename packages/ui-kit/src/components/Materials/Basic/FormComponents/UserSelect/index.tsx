import { Form, Select } from '@arco-design/web-react';
import type { UserVO } from '@onebase/platform-center';
import { getSimpleUserList } from '@onebase/platform-center';
import { nanoid } from 'nanoid';
import { memo, useEffect, useState } from 'react';
import { FORM_COMPONENT_TYPES } from '../../../componentTypes';
import { STATUS_OPTIONS, STATUS_VALUES } from '../../../constants';
import './index.css';
import type { XInputUserSelectConfig } from './schema';

const Option = Select.Option;

const XUserSelect = memo((props: XInputUserSelectConfig & { runtime?: boolean }) => {
  const { label, dataField, tooltip, status, verify, layout, labelColSpan = 0, description, runtime } = props;
  const [userData, setUserData] = useState<UserVO[]>([]);

  useEffect(() => {
    if (runtime === true) {
      getUserData();
    }
  }, []);
  const getUserData = async () => {
    // const param = {
    //   pageNo: 1,
    //   pageSize: 99999,
    //   keywords: ''
    // }
    const res = await getSimpleUserList();
    setUserData(res || []);
  };

  return (
    <Form.Item
      label={label.display && label.text}
      field={dataField.length > 0 ? dataField[dataField.length - 1] : `${FORM_COMPONENT_TYPES.USER_SELECT}_${nanoid()}`}
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
        placeholder="请选择"
        showSearch={true}
        filterOption={(inputValue, option) =>
          option.props.value.toLowerCase().indexOf(inputValue.toLowerCase()) >= 0 ||
          option.props.children.toLowerCase().indexOf(inputValue.toLowerCase()) >= 0
        }
        style={{ width: '100%' }}
        allowClear
      >
        {userData.map((option) => (
          <Option key={option.id} value={option.id}>
            {option.nickname}
          </Option>
        ))}
      </Select>
      <div className="description showEllipsis">{description}</div>
    </Form.Item>
  );
});

export default XUserSelect;
