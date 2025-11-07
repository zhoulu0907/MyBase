import { Form, TreeSelect } from '@arco-design/web-react';
import { memo } from 'react';
import { STATUS_OPTIONS, STATUS_VALUES } from '../../../constants';
import '../index.css';
import type { XInputDeptSelectConfig } from './schema';
import { getPopupContainer } from '@/utils';

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

const XDeptSelect = memo((props: XInputDeptSelectConfig & { runtime?: boolean; detailMode?: boolean }) => {
  const { label, tooltip, status, verify, layout, labelColSpan = 0, runtime = true } = props;

  return (
    <div className="formWrapper">
      <Form.Item
        label={
          label.display &&
          label.text && <span className={tooltip ? 'tooltipLabelText' : 'labelText'}>{label.text}</span>
        }
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
        <TreeSelect
          placeholder="请选择"
          allowClear
          treeData={treeData}
          getPopupContainer={getPopupContainer}
          style={{
            width: '100%',
            pointerEvents: runtime ? 'unset' : 'none'
          }}
        />
      </Form.Item>
    </div>
  );
});

export default XDeptSelect;
