import { useEffect, useState } from 'react';
import { Button, Form, Select, TreeSelect } from '@arco-design/web-react';
import { FormulaEditor } from '@/components/FormulaEditor';
import { getDeptList } from '@onebase/platform-center';
import { listToTree } from '@onebase/common';
import { getPopupContainer, CONFIG_TYPES, DEFAULT_VALUE_TYPES, DEFAULT_VALUE_TYPES_LABELS } from '@onebase/ui-kit';
import { registerConfigRenderer } from '../../registry';
import { IconLaunch } from '@arco-design/web-react/icon';
import styles from '../../index.module.less';

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
  const defaultValueConfigKey = item.key || 'defaultValueConfig';

  const [formulaVisible, setFormulaVisible] = useState<boolean>(false);
  const [defaultValueConfig, setDefaultValueConfig] = useState({
    type: '',
    formulaValue: undefined,
    formattedFormula: ''
  });

  // dept tree
  const [deptTree, setDeptTree] = useState<any[]>([]);
  const [curDeptTree, setCurDeptTree] = useState<any[]>([]);
  const [defaultValue, setDefaultValue] = useState<string | undefined>(configs[DEFAULTDEPTVALUE]);

  useEffect(() => {
    fetchDeptList();
  }, []);

  useEffect(() => {
    if (deptTree && deptTree.length > 0) {
      getCurDeptTree(deptTree);
    }
  }, [configs['selectScope'], deptTree]);

  useEffect(() => {
    setDefaultValueConfig((prev) => ({ ...prev, ...configs[defaultValueConfigKey] }));
  }, [configs[defaultValueConfigKey]]);

  // 获取部门列表
  const fetchDeptList = async () => {
    const res = await getDeptList();
    const treeData = listToTree(res, {}, true);
    setDeptTree(treeData);
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

  const handleFormulaConfirm = (formulaData: string, formattedFormula: string) => {
    setFormulaVisible(false);
    const newConfig = {
      ...configs[defaultValueConfigKey],
      formulaValue: formulaData,
      formattedFormula: formattedFormula
    };
    handlePropsChange(defaultValueConfigKey, newConfig);
  };

  return (
    <>
      <FormItem layout="vertical" labelAlign="left" label={'默认值'} style={{ marginBottom: '8px' }}>
        <Select
          getPopupContainer={getPopupContainer}
          onChange={(value) => {
            const newConfig = { ...configs[defaultValueConfigKey], type: value, formulaValue: '' };
            handlePropsChange(defaultValueConfigKey, newConfig);
          }}
          value={defaultValueConfig?.type}
          options={[
            { label: DEFAULT_VALUE_TYPES_LABELS[DEFAULT_VALUE_TYPES.CUSTOM], value: DEFAULT_VALUE_TYPES.CUSTOM },
            { label: DEFAULT_VALUE_TYPES_LABELS[DEFAULT_VALUE_TYPES.FORMULA], value: DEFAULT_VALUE_TYPES.FORMULA }
            // { label: DEFAULT_VALUE_TYPES_LABELS[DEFAULT_VALUE_TYPES.LINKAGE], value: DEFAULT_VALUE_TYPES.LINKAGE }
          ]}
        />
      </FormItem>

      {defaultValueConfig.type === DEFAULT_VALUE_TYPES.CUSTOM && (
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
      )}

      {defaultValueConfig.type === DEFAULT_VALUE_TYPES.FORMULA && (
        <FormItem>
          <Button long onClick={() => setFormulaVisible(true)} className={styles.formulaBtn}>
            {defaultValueConfig?.formulaValue ? (
              <>
                <span>{defaultValueConfig?.formattedFormula}</span>
                <IconLaunch />
              </>
            ) : (
              <>ƒx 编辑公式</>
            )}
          </Button>
        </FormItem>
      )}

      <FormulaEditor
        fieldName={configs?.label?.text}
        initialFormula={defaultValueConfig?.formulaValue}
        visible={formulaVisible}
        onCancel={() => setFormulaVisible(false)}
        onConfirm={handleFormulaConfirm}
      />
    </>
  );
};

export default DynamicDeptDefaultValueConfig;

registerConfigRenderer(CONFIG_TYPES.DEPT_DEFAULT_VALUE, ({ id, handlePropsChange, item, configs }) => (
  <DynamicDeptDefaultValueConfig id={id} handlePropsChange={handlePropsChange} item={item} configs={configs} />
));
