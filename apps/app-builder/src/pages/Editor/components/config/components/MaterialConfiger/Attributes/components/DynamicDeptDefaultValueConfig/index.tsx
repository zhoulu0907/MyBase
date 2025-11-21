import { useEffect, useState } from 'react';
import { Button, Form, Select, TreeSelect } from '@arco-design/web-react';
import { FormulaEditor } from '@/components/FormulaEditor';
import { getDeptList } from '@onebase/platform-center';
import { listToTree } from '@onebase/common';
import { getPopupContainer, CONFIG_TYPES } from '@onebase/ui-kit';
import { registerConfigRenderer } from '../../registry';

export interface DynamicDeptDefaultValueConfigProps {
  handlePropsChange: (key: string, value: string | number | boolean | any[]) => void;
  item: any;
  configs: any;
  id: string;
}

const FormItem = Form.Item;
const DEFAULTDEPTVALUE = 'defaultDeptValue';

const DynamicDeptDefaultValueConfig: React.FC<DynamicDeptDefaultValueConfigProps> = ({
  handlePropsChange,
  item,
  configs,
  id
}) => {
  const [defaultValueMode, setDefaultValueMode] = useState(configs[item.key]);
  const [formulaVisible, setFormulaVisible] = useState<boolean>(false);
  // const [formulaFieldKey, setFormulaFieldKey] = useState<string>('');
  const [formulaData, setFormulaData] = useState<string>('');

  // dept tree
  const [deptTree, setDeptTree] = useState<any[]>([]);
  const [curDeptTree, setCurDeptTree] = useState<any[]>([]);
  const [defaultValue, setDefaultValue] = useState<string | undefined>(configs[DEFAULTDEPTVALUE]);

  useEffect(() => {
    fetchDeptList();
  }, []);

  useEffect(() => {
    getCurDeptTree(deptTree);
  }, [configs['selectScope']]);

  const handleModeChange = (value: string) => {
    setDefaultValueMode(value);
    handlePropsChange(item.key, value);
  };

  // 获取部门列表
  const fetchDeptList = async () => {
    const res = await getDeptList();
    const treeData = listToTree(res, {}, true);
    setDeptTree(treeData);
    getCurDeptTree(treeData);
  };

  const getCurDeptTree = (treeData: any[]) => {
    if (configs['selectScope'] && configs['selectScope']?.length > 0) {
      const tree = extractFromTree(treeData, configs['selectScope']);
      setCurDeptTree(tree.nodes);
      if (!tree.contains) {
        handlePropsChange(DEFAULTDEPTVALUE, '');
        setDefaultValue('');
      } else {
        setDefaultValue(configs[DEFAULTDEPTVALUE]);
      }
    } else {
      setCurDeptTree(treeData || deptTree);
    }
  };

  const buildIndexFromTree = (tree: any[]) => {
    const map = new Map<string, any>();
    deepFind(map, tree);
    return map;
  };

  const deepFind = (map: Map<string, any>, nodes: any[]) => {
    for (const n of nodes) {
      map.set(n.id, n);
      if (n.children) deepFind(map, n.children);
    }
  };

  const extractFromTree = (tree: any[], ids: any[]) => {
    const map = buildIndexFromTree(tree);
    const clone = (n: any): any => ({ ...n, children: (n.children || []).map((c: any) => clone(c)) });

    // 提取并克隆结果节点
    const nodes = ids
      .map((i) => map.get(i.key))
      .filter(Boolean)
      .map((n) => clone(n!));

    // 递归检查子树中是否包含目标 id
    const subtreeHas = (node: any, id: any): boolean => {
      if (node == null) return false;
      if (node.id === id) return true;
      return (node.children || []).some((c: any) => subtreeHas(c, id));
    };

    const contains = !configs[DEFAULTDEPTVALUE] ? false : nodes.some((n) => subtreeHas(n, configs[DEFAULTDEPTVALUE]));

    return { nodes, contains };
  };

  const filterTreeNode = (inputText: string, node: any) => {
    return node.props.title.indexOf(inputText) > -1;
  };

  const handleChange = (value: string) => {
    handlePropsChange(DEFAULTDEPTVALUE, value);
    setDefaultValue(value);
  };

  const handleFormulaConfirm = (formulaData: any, formattedFormula: string, params: any) => {
    setFormulaVisible(false);
    // form.setFieldValue(
    //   formulaFieldKey,
    //   {formulaData: formulaData, formula: formattedFormula, parameters: params}
    // );
    setFormulaData('');
    // setFormulaFieldKey('');
  };

  return (
    <>
      <FormItem layout="vertical" labelAlign="left" label={'默认值'} style={{ marginBottom: '8px' }}>
        <Select defaultValue={defaultValueMode} onChange={(value) => handleModeChange(value)}>
          <Select.Option value="custom">自定义</Select.Option>
          <Select.Option value="formula">公式计算</Select.Option>
        </Select>
      </FormItem>
      {defaultValueMode === 'custom' ? (
        <FormItem>
          <TreeSelect
            placeholder="请选择"
            allowClear
            showSearch={true}
            treeData={curDeptTree}
            value={defaultValue}
            filterTreeNode={filterTreeNode}
            getPopupContainer={getPopupContainer}
            onChange={handleChange}
          />
        </FormItem>
      ) : (
        <FormItem>
          <Button long onClick={() => setFormulaVisible(true)}>
            ƒx 编辑公式
          </Button>
        </FormItem>
      )}

      <FormulaEditor
        initialFormula={formulaData}
        visible={formulaVisible}
        onCancel={() => setFormulaVisible(false)}
        onConfirm={handleFormulaConfirm}
      />
    </>
  );
};

export default DynamicDeptDefaultValueConfig;

registerConfigRenderer(
  CONFIG_TYPES.DEPT_DEFAULT_VALUE,
  ({ id, handlePropsChange, item, configs }) => (
    <DynamicDeptDefaultValueConfig
      id={id}
      handlePropsChange={handlePropsChange}
      item={item}
      configs={configs}
    />
  )
);
