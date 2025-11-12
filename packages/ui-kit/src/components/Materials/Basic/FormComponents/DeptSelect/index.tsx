import { Form, Tooltip, TreeSelect } from '@arco-design/web-react';
import { memo, useEffect, useRef, useState } from 'react';
import { STATUS_OPTIONS, STATUS_VALUES } from '../../../constants';
import type { XInputDeptSelectConfig } from './schema';
import { FORM_COMPONENT_TYPES } from '@/components/Materials/componentTypes';
import { getDeptList, getDeptsById, GetDeptsByIdReq } from '@onebase/platform-center';
import { getPopupContainer } from '@/utils';
import '../index.css';
import { listToTree } from '@onebase/common';

const XDeptSelect = memo((props: XInputDeptSelectConfig & { runtime?: boolean; detailMode?: boolean }) => {
  const { label, dataField, tooltip, status, verify, layout, selectScope, defaultDeptValue, labelColSpan = 0, runtime = true, detailMode } = props;

  const { form } = Form.useFormContext();

  const fieldName = dataField.length > 0 ? dataField[dataField.length - 1] : `${FORM_COMPONENT_TYPES.DEPT_SELECT}_${props.id}`;
  const [deptFlatTree, setDeptFlatTree] = useState<any[]>([]);
  const [deptTree, setDeptTree] = useState<any[]>([]);
  const [curDeptTree, setCurDeptTree] = useState<any[]>([]);
  const [currentSelectDept, setCurrentSelectDept] = useState<string>();

  // hover
  const cacheDeptListRef = useRef<Record<string, string>>({});
  const loadingDeptRef = useRef<Record<string, boolean>>({});
  const [, forceUpdate] = useState(0);

  const fieldValue = Form.useWatch(fieldName, form);

  useEffect(() => {
    fetchDeptList();
  }, []);

  useEffect(() => {
    if (runtime === true && fieldValue) {
      setCurrentSelectDept(fieldValue?.deptName);
    }
  }, [fieldValue]);

  useEffect(() => {
    getCurDeptTree(deptTree);
    form.setFieldValue(fieldName, undefined);
  }, [selectScope]);

  useEffect(() => {
    if(runtime && defaultDeptValue && !fieldValue) {
      if (!deptFlatTree || deptFlatTree.length === 0) return; // 等待数据
      handleChange(defaultDeptValue);
    }
  }, [defaultDeptValue, deptFlatTree, fieldValue]);

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
    setDeptFlatTree(res);
    const treeData = listToTree(res, {}, true);
    setDeptTree(treeData);
    getCurDeptTree(treeData);
    console.log('deptFlatTree',deptFlatTree)
  };

  const filterTreeNode = (inputText: string, node: any) => {
    return node.props.title.indexOf(inputText) > -1;
  };

  const handleChange = (value: string) => {
    const curSelectDeptObj = deptFlatTree.find(dept => dept.id === value);
    const curSelectDept ={
        deptID: curSelectDeptObj.id,
        deptName: curSelectDeptObj.name
      };
    setCurrentSelectDept(curSelectDeptObj.name);
    form.setFieldValue(fieldName, curSelectDept);
  };

  // hover 获取部门层级
  const fetchDeptData = async (id: string, idType: string) => {
    if (loadingDeptRef.current[id]) return; // 已在加载中，防止重复请求
  
    loadingDeptRef.current[id] = true;
    forceUpdate((s) => s + 1);
  
    try {
      const params: GetDeptsByIdReq = { id, idType };
      const res = await getDeptsById(params);
      const deptListName = buildPathFromFlat(res, id);
      cacheDeptListRef.current[id] = deptListName;
    } catch (err: any) {
        cacheDeptListRef.current[id] = '加载失败';
    } finally {
        loadingDeptRef.current[id] = false;
        forceUpdate((s) => s + 1);
    }
  };

  const buildPathFromFlat = (nodes: any[], id: string): string => {
    const sep = ' / ';
    const map = new Map(nodes.map((n) => [String(n.id), n]));
    const names: string[] = [];

    let cur = map.get(String(id));
    while (cur) {
      names.push(String(cur.name));
      const parentId = cur.parentId ?? null;
      if (!parentId || !map.has(String(parentId)) || String(parentId) === String(cur.id)) break;
      cur = map.get(String(parentId));
    }
    return names.reverse().join(sep);
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
        {status === STATUS_VALUES[STATUS_OPTIONS.READONLY] || detailMode ? (
          <Tooltip
          content={
            loadingDeptRef.current[fieldValue?.deptID] ? '加载中...' : (cacheDeptListRef.current[fieldValue?.deptID] ?? '未加载')
          }
          onVisibleChange={(visible) => {
            // 按当前行的 id 去判断是否需要请求，而不是全局 content
            if (visible && !cacheDeptListRef.current[fieldValue?.deptID] && !loadingDeptRef.current[fieldValue?.deptID]) {
              fetchDeptData(fieldValue?.deptID, 'dept');
            }
          }}
        >
          <span>{currentSelectDept || '--'}</span>
        </Tooltip>
        ) : (
        <TreeSelect
          placeholder="请选择"
          allowClear
          showSearch={true}
          treeData={curDeptTree}
          filterTreeNode={filterTreeNode}
          getPopupContainer={getPopupContainer}
          treeCheckable={false}
          onChange={handleChange}
          renderFormat={() => {return (
            <span>{currentSelectDept}</span>
          )}}
          style={{
            width: '100%',
            pointerEvents: runtime ? 'unset' : 'none'
          }}
        />)}
      </Form.Item>
    </div>
  );
});

export default XDeptSelect;
