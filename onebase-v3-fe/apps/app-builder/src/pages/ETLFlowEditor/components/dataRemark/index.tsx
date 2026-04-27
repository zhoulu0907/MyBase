import { Input } from '@arco-design/web-react';
import React from 'react';
import { useRemarkContext } from '../drawer';
const { TextArea } = Input;

/**
 * 节点备注输入组件
 * 使用 Context 来管理备注值，只有在点击确定时才会保存到 nodeData
 */
interface DataRemarkProps {}

const DataRemark: React.FC<DataRemarkProps> = () => {
  const { remark, setRemark } = useRemarkContext();

  const handleChangeRemark = (value: string) => {
    setRemark(value);
  };

  return (
    <TextArea
      onChange={handleChangeRemark}
      value={remark}
      placeholder="请输入节点备注"
      autoSize={{ minRows: 3, maxRows: 6 }}
      allowClear
    />
  );
};

export default DataRemark;
