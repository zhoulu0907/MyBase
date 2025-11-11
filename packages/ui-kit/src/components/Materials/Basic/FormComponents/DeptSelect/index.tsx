import { Form, TreeSelect } from '@arco-design/web-react';
import { memo, useEffect, useState } from 'react';
import { STATUS_OPTIONS, STATUS_VALUES } from '../../../constants';
import type { XInputDeptSelectConfig } from './schema';
import { FORM_COMPONENT_TYPES } from '@/components/Materials/componentTypes';
import { getDeptList } from '@onebase/platform-center';
import { getPopupContainer } from '@/utils';
import '../index.css';
import { listToTree } from '@onebase/common';

const XDeptSelect = memo((props: XInputDeptSelectConfig & { runtime?: boolean; detailMode?: boolean }) => {
  const { label, dataField, tooltip, status, verify, layout,multipleMode,selectScope, labelColSpan = 0, runtime = true } = props;

  const { form } = Form.useFormContext();

  const fieldName = dataField.length > 0 ? dataField[dataField.length - 1] : `${FORM_COMPONENT_TYPES.DEPT_SELECT}_${props.id}`;
  const [deptTree, setDeptTree] = useState<any[]>([]);
  const [curDeptTree, setCurDeptTree] = useState<any[]>([]);

  const fieldValue = Form.useWatch(fieldName, form);

  useEffect(() => {
    fetchDeptList();
  }, []);

  useEffect(() => {
    getCurDeptTree(deptTree);
    form.setFieldValue(fieldName, undefined);
  }, [selectScope]);

  const getCurDeptTree = (treeData: any[]) => {
    if (selectScope && selectScope?.length > 0) {
      const tree = extractFromTree(treeData, selectScope);
      setCurDeptTree(tree);
    } else {
      setCurDeptTree(treeData || deptTree);
    }
  }

  const buildIndexFromTree = (tree: any[])=> {
    const map = new Map<string, any>();
    deepFind(map,tree);
    return map;
  }

  const deepFind = (map: Map<string, any>,nodes: any[]) => {
    for (const n of nodes) {
        map.set(n.id, n);
        if (n.children) deepFind(map, n.children);
      }
  }

  const extractFromTree = (tree: any[], ids: any[])=> {
    const map = buildIndexFromTree(tree);
    const clone = (n: any): any => ({ ...n, children: (n.children || []).map((c: any) => clone(c)) });
    return ids.map(i => map.get(i.key)).filter(Boolean).map(n => clone(n!));
  }

  // 获取部门列表
  const fetchDeptList = async () => {
    const res = await getDeptList();
    const treeData = listToTree(res, {}, true);
    setDeptTree(treeData);
    getCurDeptTree(treeData);
  };

  const filterTreeNode = (inputText: string, node: any) => {
    return node.props.title.indexOf(inputText) > -1;
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
          showSearch={true}
          treeCheckable={multipleMode}
          treeCheckStrictly={true}
          treeData={curDeptTree}
          filterTreeNode={filterTreeNode}
          maxTagCount={3}
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
