import { Avatar, Form, Input, Select } from '@arco-design/web-react';
import { IconClose, IconSearch } from '@arco-design/web-react/icon';
import { getSimpleUserPage } from '@onebase/platform-center';
import { debounce } from 'lodash-es';
import { memo, useCallback, useEffect, useRef, useState } from 'react';
import { FORM_COMPONENT_TYPES } from '../../../componentTypes';
import { STATUS_OPTIONS, STATUS_VALUES } from '../../../constants';
import AdvanceSelectModal from './AdvanceSelectModal';
import type { XInputUserSelectConfig } from './schema';

import { getPopupContainer } from '@/utils';

import '../index.css';
import './index.css';
import { isRuntimeEnv, TokenManager } from '@onebase/common';

const XUserSelect = memo((props: XInputUserSelectConfig & { runtime?: boolean; detailMode?: boolean }) => {
  const {
    label,
    dataField,
    tooltip,
    status,
    verify,
    layout,
    selectScope,
    defaultUserValue,
    labelColSpan = 0,
    runtime,
    detailMode
  } = props;
  const [userData, setUserData] = useState<any[]>([]);
  // 分页
  const [pageNo, setPageNo] = useState<number>(1);
  const [total, setTotal] = useState<number | string>(0);
  // 搜索条件
  const [keywords, setKeywords] = useState<string>('');
  // 是否加载中
  const [fetching, setFetching] = useState<boolean>(false);

  const { form } = Form.useFormContext();
  const fieldName =
    dataField.length > 0 ? dataField[dataField.length - 1] : `${FORM_COMPONENT_TYPES.USER_SELECT}_${props.id}`;
  const [advanceVisible, setAdvanceVisible] = useState(false); //高级选项popup
  const [currentSelectUser, setCurrentSelectUser] = useState<string>();
  const [currentSelectUserID, setCurrentSelectUserID] = useState<string>();

  const runtimeRemoveRef = useRef<boolean>(false);

  const fieldValue = Form.useWatch(fieldName, form);
  const tokenInfo = TokenManager.getTokenInfo();

  useEffect(() => {
    if (
      runtime &&
      status !== STATUS_VALUES[STATUS_OPTIONS.READONLY] &&
      !detailMode &&
      keywords === '' &&
      (!selectScope || selectScope.length === 0)
    ) {
      getUserData('');
    } else if (keywords === '') {
    }
  }, [keywords]);

  useEffect(() => {
    if (selectScope && selectScope.length > 0) {
      const selectMembers = selectScope.map((member: any) => ({
        id: member.key,
        // deptName: member.deptName
        nickname: member.name,
        email: member.email
      }));
      setUserData(selectMembers);
      setTotal(0);
      setPageNo(1);
      setKeywords('');
    } else {
      getUserData('');
    }
  }, [selectScope]);

  useEffect(() => {
    if (isRuntimeEnv()) {
      if (fieldValue) {
        setCurrentSelectUser(fieldValue?.name);
        form.setFieldValue(fieldName, {
          id: fieldValue?.id,
          name: fieldValue?.name
        });
      } else if (
        defaultUserValue &&
        !runtimeRemoveRef.current &&
        status !== STATUS_VALUES[STATUS_OPTIONS.READONLY] &&
        !detailMode
      ) {
        const defaultUser = userData.find((user) => user.id === defaultUserValue);
        if (defaultUser) {
          setCurrentSelectUser(defaultUser?.nickname);
          form.setFieldValue(fieldName, {
            id: defaultUserValue,
            name: defaultUser?.nickname
          });
        }
      }
    }
  }, [userData, fieldValue]);

  useEffect(() => {
    if (runtime && !isRuntimeEnv() && userData.length > 0 && defaultUserValue && !runtimeRemoveRef.current) {
      const defaultUser = userData.find((user) => user.id === defaultUserValue);
      if (defaultUser) {
        setCurrentSelectUser(defaultUser?.nickname);
        form.setFieldValue(fieldName, {
          id: defaultUserValue,
          name: defaultUser?.nickname
        });
      }
    }
    runtimeRemoveRef.current = false;
  }, [defaultUserValue, runtime, userData]);

  // 第一页的加载
  const debouncedSearch = useCallback(
    debounce((value) => {
      if (!selectScope || selectScope.length === 0) {
        getUserData(value);
      } else {
        const users = selectScope
          .filter((user) => user.name.indexOf(value) >= 0)
          .map((member: any) => ({
            id: member.key,
            // deptName: member.deptName
            nickname: member.name,
            email: member.email
          }));
        setUserData(users);
      }
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
    const { list, total } = await getSimpleUserPage(param, tokenInfo?.loginMethod);
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
      const { list, total } = await getSimpleUserPage(param, tokenInfo?.loginMethod);
      setPageNo(pageNo + 1);
      setTotal(total);
      setUserData((prev) => [...prev, ...list]);
      setFetching(false);
    }
  };

  const handleSelectChange = (id: string) => {
    console.log('value: ', id);
    console.log('userData: ', userData);

    const user = userData.find((item) => item.id === id);

    setCurrentSelectUser(user?.nickname);
    setCurrentSelectUserID(id);

    console.log('fieldName: ', fieldName);

    form.setFieldValue(fieldName, {
      id: id,
      name: user?.nickname
    });
  };

  const handleRemove = (e: React.MouseEvent<SVGElement, MouseEvent>) => {
    // 阻止下拉框弹出
    e.stopPropagation();
    setCurrentSelectUser(undefined);
    setCurrentSelectUserID(undefined);
    form.setFieldValue(fieldName, undefined);
    runtimeRemoveRef.current = true;
  };

  const handleOKModal = (user: any) => {
    setCurrentSelectUser(user.name);
    setCurrentSelectUserID(user.value);

    form.setFieldValue(fieldName, {
      id: user.value,
      name: user.name
    });

    setAdvanceVisible(false);
  };

  return (
    <div className="formWrapper">
      <Form.Item
        label={
          label.display &&
          label.text && <span className={tooltip ? 'tooltipLabelText' : 'labelText'}>{label.text}</span>
        }
        field={fieldName}
        layout={layout}
        tooltip={tooltip}
        labelCol={layout === 'horizontal' ? { span: 10 } : {}}
        rules={[{ required: verify?.required, message: `${label.text}是必填项` }]}
        hidden={runtime && status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN]}
        style={{
          margin: 0,
          opacity: status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN] ? 0.4 : 1
        }}
      >
        {status === STATUS_VALUES[STATUS_OPTIONS.READONLY] || detailMode ? (
          <div>{fieldValue?.name ?? '--'}</div>
        ) : (
          <Select
            placeholder="请选择"
            showSearch={true}
            filterOption={false}
            onSearch={debouncedSearch}
            onPopupScroll={scrollHandler}
            getPopupContainer={getPopupContainer}
            style={{
              width: '100%',
              pointerEvents: runtime ? 'unset' : 'none'
            }}
            onVisibleChange={() => setKeywords('')}
            onChange={(value) => handleSelectChange(value)}
            options={userData.map((option) => ({
              label: (
                <div className="optionDiv">
                  <Avatar size={34} className="optionAvatar">
                    {option.nickname?.[0]}
                  </Avatar>
                  <div>
                    <div className="optionName">{option.nickname}</div>
                    <div className="optionInfo">
                      {option.deptName} {option.email}
                    </div>
                  </div>
                </div>
              ),
              value: option.id,
              subValue: option.nickname
            }))}
            triggerProps={{
              autoAlignPopupWidth: false,
              autoAlignPopupMinWidth: true,
              position: 'bl'
            }}
            dropdownRender={(menu) => (
              <div>
                <div className="dropdownRender">
                  <Input className="searchInput" placeholder="搜索人员" onChange={(value) => debouncedSearch(value)} />
                  <IconSearch className="searchIcon" />
                  <span className="advanceBtn" onClick={(e) => setAdvanceVisible(true)}>
                    高级
                  </span>
                </div>
                {menu}
              </div>
            )}
            renderFormat={() => {
              return (
                currentSelectUser && (
                  <span className="renderFormat">
                    <Avatar size={24} className="avatar">
                      {currentSelectUser?.[0]}
                    </Avatar>
                    <span className="displayName"> {currentSelectUser} </span>
                    <IconClose
                      className="closeBtn"
                      onMouseDown={(e) => {
                        // 阻止 mousedown 导致 input 聚焦/下拉打开
                        e.preventDefault();
                        e.stopPropagation();
                      }}
                      onClick={(e) => {
                        handleRemove(e);
                      }}
                    />
                  </span>
                )
              );
            }}
          />
        )}
      </Form.Item>
      <AdvanceSelectModal
        runtime={runtime}
        visible={advanceVisible}
        currentSelectUserID={currentSelectUserID}
        status={status}
        detailMode={detailMode}
        onCancel={() => setAdvanceVisible(false)}
        onOk={(value: any) => handleOKModal(value)}
      />
    </div>
  );
});

export default XUserSelect;
