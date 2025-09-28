import { useCallback, useEffect, useState } from 'react';
import { Button, Modal, Input, Space, List, Breadcrumb, Avatar, Typography, Spin } from '@arco-design/web-react';
import { IconRight, IconClose } from '@arco-design/web-react/icon';
import { formatDeptAndUsers } from './const';
import DeptMember from './deptMember';

interface IData {
  children: IData[];
  [property: string]: any;
}
interface IProps {
  title?: string;
  width?: number;
  data: IData;
  loading: boolean;
  visible: boolean;
  selectedMembers: any[];
  onExpand: (value: string) => void;
  onSearch: (value: string) => void;
  onCancel: () => void;
  onConfirm: (value: any[]) => void;
  onUpdateSelectedMembers?: (members: any[]) => void;
}

// 添加成员
const AddMembers = (props: IProps) => {
  const {
    title = '选择成员',
    width = 800,
    visible,
    data,
    loading,
    selectedMembers,
    onExpand,
    onSearch,
    onCancel,
    onConfirm,
    onUpdateSelectedMembers
  } = props;

  const isSelectDepartment = title === 'specifiedDepartment';
  const isSelectPerson = title === 'specifiedPerson';

  // 点击取消时的处理函数
  const handleCancel = () => {
    onCancel();
  };

  return (
    <Modal
      title={
        <div style={{ textAlign: 'left' }}>{isSelectDepartment ? '指定部门' : isSelectPerson ? '指定成员' : title}</div>
      }
      onOk={onCancel}
      onCancel={handleCancel}
      visible={visible}
      autoFocus={false}
      focusLock={true}
      simple
      unmountOnExit
      closable={true}
      style={{ width }}
      maskClosable={true}
      footer={
        <div style={{ textAlign: 'right' }}>
          <Button type="default" onClick={handleCancel} style={{ marginRight: 12 }}>
            取消
          </Button>
          <Button type="primary" disabled={selectedMembers.length === 0} onClick={() => onConfirm(selectedMembers)}>
            确定
          </Button>
        </div>
      }
    >
      <DeptMember 
        visible={visible}
        title={title}
        data={data}
        loading={loading}
        selectedMembers={selectedMembers}
        onExpand={onExpand}
        onSearch={onSearch}
        onUpdateSelectedMembers={onUpdateSelectedMembers}/>
    </Modal>
  );
};

export default AddMembers;
