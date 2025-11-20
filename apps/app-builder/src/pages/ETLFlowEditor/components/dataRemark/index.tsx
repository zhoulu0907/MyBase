import { Input } from '@arco-design/web-react';
import { etlEditorSignal } from '@onebase/common';
import { useSignals } from '@preact/signals-react/runtime';
import React, { useState } from 'react';
const { TextArea } = Input;

/**
 * 节点备注输入组件
 * @param props.nodeData 节点数据对象
 * @param props.curNode 当前节点对象
 */
interface DataRemarkProps {}

const DataRemark: React.FC<DataRemarkProps> = ({}) => {
  useSignals();

  const { nodeData, curNode } = etlEditorSignal;

  const [remark, setRemark] = useState<string>(nodeData.value[curNode.value.id]?.description || '');

  const handleChangeRemark = (value: string) => {
    if (!curNode.value.id) {
      return;
    }
    nodeData.value[curNode.value.id].description = value;
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
