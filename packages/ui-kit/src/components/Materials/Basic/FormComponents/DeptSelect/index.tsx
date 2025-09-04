import { Form, TreeSelect } from '@arco-design/web-react';
import { memo } from 'react';
import { STATUS_OPTIONS, STATUS_VALUES } from '../../../constants';
import './index.css';
import type { XInputDeptSelectConfig } from './schema';

// TODO(Mickey): 放到schema的config中
// 示例树形结构：部门
const treeData = [
  {
    key: 'node1',
    title: 'Trunk',
    children: [
      {
        key: 'node2',
        title: 'Leaf'
      }
    ]
  },
  {
    key: 'node3',
    title: 'Trunk2',
    children: [
      {
        key: 'node4',
        title: 'Leaf'
      },
      {
        key: 'node5',
        title: 'Leaf'
      }
    ]
  }
];

const XDeptSelect = memo((props: XInputDeptSelectConfig) => {
  const { label, tooltip, status, verify, layout, labelColSpan = 0, description } = props;

  return (
    <Form.Item
      label={label.display && label.text}
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
      <TreeSelect placeholder="请选择" style={{ width: '100%' }} allowClear treeData={treeData}></TreeSelect>
      <div className="description showEllipsis">{description}</div>
    </Form.Item>
  );
});

export default XDeptSelect;
