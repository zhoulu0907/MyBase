import { memo, useEffect, useState } from 'react';
import { nanoid } from 'nanoid';
import { Form, Select } from '@arco-design/web-react';
import { STATUS_OPTIONS, STATUS_VALUES } from '../../../constants';
import type { XInputUserSelectConfig } from './schema';
import './index.css';
import { FORM_COMPONENT_TYPES } from '../../../componentTypes';
import { getSimpleUserList } from '@onebase/platform-center';
import type { UserVO } from '@onebase/platform-center';

const Option = Select.Option;

const XUserSelect = memo((props: XInputUserSelectConfig) => {
  const { label, dataField, tooltip, status, verify, layout, labelColSpan = 0, description } = props;
  const [userData, setUserData] = useState<UserVO[]>([]);

  useEffect(() => {
    getUserData();
  }, [])
  const getUserData = async () => {
    // const param = {
    //   pageNo: 1,
    //   pageSize: 99999,
    //   keywords: ''
    // }
    const res = await getSimpleUserList();
    setUserData(res || [])
  }

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
        placeholder="Select"
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
      <div className='description showEllipsis'>{description}</div>
    </Form.Item>
  );
});

export default XUserSelect;