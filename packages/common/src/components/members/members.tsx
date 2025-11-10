import { useState } from 'react';
import { Button, Modal, Checkbox } from '@arco-design/web-react';
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
  isFromPermission?: boolean;
  onExpand: (value: string) => void;
  onSearch: (value: string) => void;
  onCancel: () => void;
  onConfirm: (value: any[], isIncludeChild?: boolean) => void;
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
    isFromPermission = false,
    onExpand,
    onSearch,
    onCancel,
    onConfirm,
    onUpdateSelectedMembers
  } = props;

  const [resetFlag, setResetFlag] = useState(false);
  const [isIncludeChild, setIsIncludeChild] = useState(false);

  const isSelectDepartment = title === 'specifiedDepartment';
  const isSelectPerson = title === 'specifiedPerson';
  const modalTitle = (() => {
  if (!isSelectDepartment && !isSelectPerson) return title;
  const action = isFromPermission ? '添加' : '指定';
  const subject = isSelectDepartment ? '部门' : '成员';
  return `${action}${subject}`;
})();

  // 点击取消时的处理函数
  const handleCancel = () => {
    setResetFlag(flag => !flag);
    onCancel();
  };

  return (
    <Modal
      title={
        <div style={{ textAlign: 'left' }}>{modalTitle}</div>
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
        <div style={{display: 'flex'}}>
          <div style={{ flex: 1,textAlign: 'left' }}>
            {(isFromPermission && isSelectDepartment) && <Checkbox checked={isIncludeChild} onChange={(value)=>setIsIncludeChild(value)}>包含勾选部门及下级部门</Checkbox>}
          </div>
          <div>
            <Button type="default" onClick={handleCancel} style={{ marginRight: 12 }}>
              取消
            </Button>
            <Button type="primary" disabled={selectedMembers.length === 0} onClick={() => onConfirm(selectedMembers, isIncludeChild)}>
              确定
            </Button>
          </div>
        </div>
      }
    >
      <DeptMember 
        visible={visible}
        title={title}
        data={data}
        loading={loading}
        selectedMembers={selectedMembers}
        resetFlag={resetFlag}
        onExpand={onExpand}
        onSearch={onSearch}
        onUpdateSelectedMembers={onUpdateSelectedMembers}/>
    </Modal>
  );
};

export default AddMembers;
