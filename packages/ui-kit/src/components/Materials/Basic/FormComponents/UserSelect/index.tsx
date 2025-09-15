import { Form, Select } from '@arco-design/web-react';
import { getSimpleUserPage, type UserVO } from '@onebase/platform-center';
import { debounce } from 'lodash-es';
import { nanoid } from 'nanoid';
import { memo, useCallback, useEffect, useState } from 'react';
import { FORM_COMPONENT_TYPES } from '../../../componentTypes';
import { STATUS_OPTIONS, STATUS_VALUES } from '../../../constants';
import type { XInputUserSelectConfig } from './schema';
import '../index.css';

const Option = Select.Option;

const XUserSelect = memo((props: XInputUserSelectConfig & { runtime?: boolean }) => {
  const { label, dataField, tooltip, status, verify, layout, labelColSpan = 0, runtime } = props;
  const [userData, setUserData] = useState<UserVO[]>([]);
  // 分页
  const [pageNo, setPageNo] = useState<number>(1);
  const [total, setTotal] = useState<number | string>(0);
  // 搜索条件
  const [keywords, setKeywords] = useState<string>('');
  // 是否加载中
  const [fetching, setFetching] = useState<boolean>(false);

  useEffect(() => {
    if (runtime === true) {
      getUserData('');
    }
  }, []);

  // 第一页的加载
  const debouncedSearch = useCallback(
    debounce((value) => {
      getUserData(value);
    }, 500),
    []
  );
  const getUserData = async (inputValue: string) => {
    setFetching(true);
    setKeywords(inputValue);
    const param = {
      pageNo: 1,
      pageSize: 20,
      keywords: inputValue
    };
    const { list, total } = await getSimpleUserPage(param);
    setPageNo(1);
    setTotal(total);
    setUserData(list || []);
    setFetching(false);
  };

  // 滚动加载
  const scrollHandler = async (element: HTMLDivElement) => {
    const { scrollTop, scrollHeight, clientHeight } = element;
    const scrollBottom = scrollHeight - (scrollTop + clientHeight);

    if (scrollBottom < 10 && !fetching && Number(total) > userData.length) {
      setFetching(true);
      const param = {
        pageNo: pageNo + 1,
        pageSize: 20,
        keywords: keywords
      };
      const { list, total } = await getSimpleUserPage(param);
      setPageNo(pageNo + 1);
      setTotal(total);
      setUserData((prev) => [...prev, ...list]);
      setFetching(false);
    }
  };

  return (
    <div className='formWrapper'>
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
        hidden={runtime && status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN]}
        style={{
          margin: 0,
          opacity: status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN] ? 0.4 : 1
        }}
      >
        <Select
          placeholder="请选择"
          showSearch={true}
          filterOption={false}
          onSearch={debouncedSearch}
          onPopupScroll={scrollHandler}
          allowClear
          style={{
            width: '100%',
            pointerEvents: runtime ? 'unset' : 'none'
          }}
        >
          {userData.map((option) => (
            <Option key={option.id} value={option.id}>
              {`${option.nickname}（${option.username}）`}
            </Option>
          ))}
        </Select>
      </Form.Item>
    </div>
  );
});

export default XUserSelect;
