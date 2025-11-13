import React, { useCallback, useEffect, useState } from 'react'
import { debounce } from 'lodash-es';
import { Avatar, Button, Input, Modal, Radio, Tabs, Typography } from '@arco-design/web-react';
import TabPane from '@arco-design/web-react/es/Tabs/tab-pane';

import { getSimpleUserPage, UserVO } from '@onebase/platform-center';
import { DeptMember } from '@onebase/common';
import { getDeptUser, GetDeptUserReq } from '@onebase/app';

import './index.css';

const RadioGroup = Radio.Group;
const UserTab = '1';
const DeptTab = '2';

interface AdvanceSelectModalProps {
    runtime: boolean | undefined;
    visible: boolean;
    currentSelectUserID: number | undefined;
    onCancel: any;
    onOk: any;
}

const AdvanceSelectModal: React.FC<AdvanceSelectModalProps> = ({
  runtime,
  visible,
  currentSelectUserID,
  onCancel,
  onOk
}) =>{
    const [activeTab, setActiveTab] = useState<string>('1');
    const [userData, setUserData] = useState<UserVO[]>([]);
    // 分页
    const [pageNo, setPageNo] = useState<number>(1);
    const [total, setTotal] = useState<number | string>(0);
    // 搜索条件
    const [keywords, setKeywords] = useState<string>('');
    // 是否加载中
    const [fetching, setFetching] = useState<boolean>(false);
    const [selectUser, setSelectUser] = useState<number>();
    const [selectUserName, setSelectUserName] = useState<string | undefined>('');

    // 部门选择
    const [deptData, setDeptData] = useState<any>();
    const [memberLoading, setMemberLoading] = useState<boolean>(false);
    const [selectedMembers, setSelectedMembers] = useState<any[]>([]);

    const [finalSelect, setFinalSelect] = useState<any>();

    useEffect(() => {
        if (runtime === true) {
          getUserData('');
          getDeptUsers({});
        }
    }, []);

    useEffect(() => {
      reset();
    },[currentSelectUserID])

    useEffect(() => {
      if(visible) {
        if(finalSelect) {
          if(finalSelect.tab === UserTab) {
            setSelectUser(finalSelect.value);
            setSelectedMembers([]);
          } else {
            setSelectedMembers([finalSelect.value]);
            setSelectUserName('');
          }
          setSelectUserName(finalSelect.name);
        } else {
          reset();
        }
      }
    },[visible])

    const reset = () => {
        setSelectUserName('');
        setSelectUser(undefined);
        setSelectedMembers([]);
        setFinalSelect(undefined);
    }

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
    const scrollHandler = async (element: any) => {
        const { scrollTop, scrollHeight, clientHeight } = element.currentTarget;
        const scrollBottom = scrollHeight - (scrollTop + clientHeight);

        if (scrollBottom < 10 && !fetching && Number(total) > userData.length) {
            setFetching(true);
            const param = {
                pageNo: pageNo+1,
                pageSize: 20,
                keywords: keywords
            };
            const { list, total } = await getSimpleUserPage(param);
            console.log(list, param.pageNo)
            setPageNo(pageNo + 1);
            setTotal(total);
            setUserData((prev) => prev.concat(list));
            setFetching(false);
        }
    };

    const handleUserChange = (value: number) => {
        setSelectUser(value);
        const user = userData.find(user => user.id === value);
        setSelectUserName(user?.nickname);
        setSelectedMembers([]);
    }

    // 第一页的加载
    const debouncedSearch = useCallback(
        debounce((value) => {
          getUserData(value);
        }, 500),[]);

    // 按部门Tab
    const handleTabChange = async (value: string) => {
        setActiveTab(value);
    }

    // 获取部门用户信息 TODO
  const getDeptUsers = async ({ deptId, keywords }: { deptId?: string; keywords?: string }) => {
    setMemberLoading(true);
    try {
      const params: GetDeptUserReq = {
        roleId: '37775560235057153',   //TODO
        deptId,
        keywords
      };
      const res = await getDeptUser(params);
      setDeptData(res);
    } catch (error) {
    } finally {
      setMemberLoading(false);
    }
  };

   // 展开下级
  const handleExpand = async (deptId: string) => {
    await getDeptUsers({ deptId });
  };

  const debouncedUpdate = useCallback(
    debounce((value) => {
      getDeptUsers({ keywords: value });
    }, 500),
    []
  );

  useEffect(() => {
    return () => debouncedUpdate.cancel();
  }, [debouncedUpdate]);

  const handleUpdateSelectedMembers = (members: any[]) => {
     setSelectedMembers(members);
     setSelectUserName(members.length > 0 ? members[0].name : '');
     setSelectUser(undefined);
  };

  const handleOnOk = () => {
    const tab = selectUser ? UserTab : DeptTab;
    setFinalSelect({name: selectUserName, value: selectUser || selectedMembers[0], tab: tab})
    onOk({name: selectUserName, value: selectUser || selectedMembers[0].key})
  }

  return (
    <>
        <Modal
            className='advanceSelectModel'
            title={<span className='modalTitle'>选择用户</span>}
            visible={visible}
            onCancel={onCancel}
            autoFocus={false}
            focusLock={true}
            escToExit={false}
            maskClosable={false}
            footer={
                <>
                    <span className='footerSpan'>单选, 已选择「{selectUserName}」</span>
                    <Button onClick={onCancel}>取消</Button>
                    <Button disabled={(selectedMembers.length === 0 && !selectUser)} onClick={handleOnOk} type="primary">确定</Button>
                </>
            }
      >
        <div>
            <Tabs activeTab={activeTab} onChange={(value) => handleTabChange(value)}>
                <TabPane key={UserTab} title='按用户'>
                    <Typography.Paragraph>
                        <div className='tabContent' onScroll={scrollHandler}>
                            <div className='modalSearch'>
                                <Input.Search placeholder='搜索用户' onChange={(value) => debouncedSearch(value)}/>
                            </div>
                            <RadioGroup direction='vertical' className='itemGroup'
                                value={selectUser}
                                onChange={(value) => handleUserChange(value)}>
                                {userData.map((user) => (
                                    <Radio key={user.id} value={user.id} className='userItem'>
                                        <Avatar size={24} className='avatar'>
                                            {user?.nickname[0]}
                                        </Avatar>
                                        <span className='userName'>{user.nickname}</span>
                                        <span className='userPhone'>{user.email}</span>
                                        <span className='userName'>{user.deptName}</span>
                                    </Radio>
                                ))}
                            </RadioGroup>
                        </div>
                    </Typography.Paragraph>
                </TabPane>
                <TabPane key={DeptTab} title='按部门'>
                    <Typography.Paragraph>
                        <DeptMember 
                            visible={visible && activeTab === UserTab}
                            title='选择用户'
                            data={deptData}
                            loading={memberLoading}
                            selectedMembers={selectedMembers}
                            isMultiple={false}
                            onExpand={handleExpand}
                            onSearch={debouncedUpdate}
                            onUpdateSelectedMembers={handleUpdateSelectedMembers}/>
                    </Typography.Paragraph>
                </TabPane>
            </Tabs>
        </div>
      </Modal>
    </>
  )
}

export default AdvanceSelectModal