import { Message, Modal, Grid } from '@arco-design/web-react';
import {
  getFormulaById,
  getFormulaFunctionSimpleList,
  type VariablesList,
  type variableItem,
  getEntityListByApp,
  getEntityFields,
  type ChildVariablesField,
  type fieldListWithNodeData
} from '@onebase/app';
import { useCallback, useEffect, useMemo, useRef, useState } from 'react';
import { triggerEditorSignal } from '@/store/singals/trigger_editor';
import { FormulaInput, FunctionList, InfoPanel, VariableList, DebuggedFormula } from './components';
import styles from './index.module.less';
import type { FormulaEditorProps, FunctionItem, info } from './utils/types';
import { useAppStore } from '@/store';
import { IconLeft } from '@arco-design/web-react/icon';
import { NodeType } from '@onebase/common';
import { getPrecedingNodes } from '@/pages/CreateApp/pages/IntegratedManagement/triggerEditor/nodes/utils';
import { triggerNodeOutputSignal } from '@/store/singals/trigger_node_output';

const Row = Grid.Row;
const Col = Grid.Col;
/**
 * 公式编辑器
 * @param visible - 编辑器是否可见
 * @param onCancel - 取消编辑回调
 * @param onConfirm - 确认编辑回调
 * @param initialFormula - 初始公式
 */
export function FormulaEditor({ fieldName, visible, onCancel, onConfirm, initialFormula = '' }: FormulaEditorProps) {
  const [formula, setFormula] = useState(initialFormula); //公式的值
  const [variableSearch, setVariableSearch] = useState('');
  const [functionSearch, setFunctionSearch] = useState('');
  const [funcList, setFuncList] = useState<FunctionItem[]>([]); //公式编辑器中的函数列表展示
  const editorRef = useRef<{ insertAtPosition: (text: string, type: string, position?: number) => void } | null>(null);
  const [info, setInfo] = useState<info | null>(null);
  const [variables, setVariables] = useState<VariablesList[]>([]); //公式编辑器中的左侧变量列表展示
  const [isDebugMode, setIsDebugMode] = useState<boolean>(false);
  const { curAppId } = useAppStore();

  useEffect(() => {
    setFormula(initialFormula);
  }, [initialFormula]);

  useEffect(() => {
    curAppId && retrievedEntityListByApp();
  }, [curAppId]);
  /**
   * 获取变量一级列表
   */
  const nodeTypes = [NodeType.DATA_QUERY, NodeType.DATA_QUERY_MULTIPLE, NodeType.DATA_CALC];

  const retrievedEntityListByApp = async () => {
    const nodes = getPrecedingNodes(curAppId, triggerEditorSignal.nodes.value, nodeTypes);
    try {
      const res: any[] = await getEntityListByApp(curAppId);
      if (res.length > 0) {
        const result = await Promise.all(
          res.map(async (item) => {
            try {
              if (!item.fields?.length) {
                const childEntityList = await getEntityFields({
                  entityId: item.entityId
                });
                return {
                  ...item,
                  variableId: item.entityId,
                  variableName: item.entityName,
                  fields: [...(item.fields || []), ...childEntityList].reverse()
                };
              } else {
                return item;
              }
            } catch (error) {
              console.log('加载二级实体列表失败:', error);
            }
          })
        );
        nodes?.forEach((nodeItem) => {
          const nodeOutput = triggerNodeOutputSignal.getTriggerNodeOutput(nodeItem.id);
          const newFields = nodeOutput?.conditionFields?.map((data: any) => {
            return {
              ...data,
              displayName: data.label,
              entityId: nodeItem.id,
              id: nodeItem.id,
              fieldName: nodeItem.data?.title || '',
              isNode: true
            };
          });
          const reverseNewFields = (newFields || []).reverse();
          result.push({
            variableName: nodeItem.data?.title,
            fields: reverseNewFields,
            variableId: nodeItem.id,
            tableName: ''
          });
        });
        console.log('result', result);
        setVariables(result as any);
      }
    } catch (error) {
      console.error('加载变量列表失败:', error);
    }
  };

  /**
   * 初始化函数列表
   */
  const getFuncList = async () => {
    const res = await getFormulaFunctionSimpleList();
    if (res) {
      setFuncList(res);
    }
    // setFuncList(mockFunctions)
  };

  /**
   * 获取公式详情
   * @param id - 公式ID
   */
  const getFormulaInfo = (id: string) => {
    getFormulaById(id).then((res: any) => {
      console.log(res, 'getFormulaInfo>>>>>>>>>>');
      if (res) {
        setInfo(res);
      }
    });
  };

  /**
   * 过滤变量列表
   * 根据变量名称或类型是否包含搜索关键词（不区分大小写）
   */
  const filteredVariables: VariablesList[] = useMemo(() => {
    if (!variableSearch) return variables || [];
    const newFields =
      variables?.[0]?.fields?.filter(
        (v) =>
          v.fieldName.toLowerCase().includes(variableSearch.toLowerCase()) ||
          v.fieldName.toLowerCase().includes(variableSearch.toLowerCase())
      ) || [];
    variables[0].fields = newFields;
    return variables;
  }, [variableSearch]);

  /**
   * 过滤函数列表
   * 根据函数名称或摘要是否包含搜索关键词（不区分大小写）
   */
  const filteredFunctions = useMemo(() => {
    if (!functionSearch) return funcList;
    return funcList.filter(
      (f) =>
        f.name.toLowerCase().includes(functionSearch.toLowerCase()) ||
        f.summary.toLowerCase().includes(functionSearch.toLowerCase())
    );
  }, [functionSearch]);

  /**
   * 插入变量到公式
   * @param variable - 要插入的变量
   */
  const handleInsertVariable = useCallback((variable: ChildVariablesField) => {
    if (editorRef.current) {
      // 使用编辑器的插入方法，支持光标定位
      //如果fieldtype是node 代表是节点， 需要传入的格式是$节点.字段
      if (variable.isNode) {
        editorRef.current.insertAtPosition(
          `[[${variable.id}.${variable.value}.$${variable.fieldName}.${variable.displayName}]]`,
          'var'
        );
      } else {
        editorRef.current.insertAtPosition(`[[${variable.id}.${variable.displayName}]]`, 'var');
      }
    } else {
      // 降级处理：直接添加到末尾
      setFormula((prev) => prev + variable.displayName);
    }
  }, []);

  /**
   * 点击函数
   * @param func - 选中的函数
   */
  const handleChooseFunction = useCallback((func: FunctionItem) => {
    getFormulaInfo(func.id);

    console.log('editorRef', editorRef);

    // 插入函数到公式
    if (editorRef.current) {
      // 使用编辑器的插入方法，支持光标定位
      console.log(func.id, func.expression, 'fnfnfnfnfnfnf');
      editorRef.current.insertAtPosition(`{{${func.id}.${func.name}}}()`, 'fn');
    } else {
      // 降级处理：直接添加到末尾
      setFormula((prev) => prev + func.name + '()');
    }
  }, []);

  /**
   * 复制公式
   */
  const handleCopy = useCallback(() => {
    navigator.clipboard.writeText(formula);
    Message.success('公式已复制到剪贴板');
  }, [formula]);

  /**
   * 对现在的公式去掉{{id.}}和[[id.]]只保留函数名和变量名
   * @returns 返回简化后的公式模版
   */
  const formattedFormula = () => {
    const newFormula = formula.replace(/(\[\[.+?\]\]|\{\{.+?\}\})/g, (match) => {
      let content;
      if (match.startsWith('{{')) {
        content = match.slice(2, -2);
        content = content.replace(/^[^.]+\./, '');
      } else {
        content = match.slice(2, -2);
        content = content.replace(/^[^\.]+\.(.+)$/, '$1,');
        const temp = content.split('$');
        if (temp.length > 1) {
          content = `$${temp[1]}`;
        }
      }
      return content;
    });
    return newFormula.replace(/,(?=\s*\))/g, '').replace(/,+/g, ',');
  };

  /**
   * 设置运行态所需要的parameters
   * 格式:
   * {
   *  $节点: 节点ID,
   *  $节点.字段: 字段ID
   * }
   */
  const getParameters = (formulaData: string) => {
    const regex = /\[\[(.*?)\]\]/g;
    const copyFormulaData = formulaData;
    const matches = [...copyFormulaData.matchAll(regex)];
    const variablesMapping: { [key: string]: string } = {};
    matches.forEach((match) => {
      const temp = match[1].split('.');
      let fieldName = '';
      let fieldId = '';
      let nodeName = '';
      let nodeId = '';
      //[nodeid->temp[0], variableid->temp[1], nodeName->temp[2], variableName->temp[3]]
      if (temp.length > 3) {
        nodeName = temp[2];
        nodeId = temp[0];
        fieldName = `${temp[2] + '.' + temp[3]}`;
        fieldId = temp[1];
        variablesMapping[nodeName] = nodeId;
      } else {
        fieldId = temp[0] || '';
        fieldName = temp[1] || '';
      }
      variablesMapping[fieldName] = fieldId;
    });
    return variablesMapping;
  };

  /**
   * 点击确认之后计算
   */
  const handleConfirm = useCallback(async () => {
    const newFormula = formattedFormula();
    const params = getParameters(formula);
    onConfirm(formula, newFormula, params);
    setIsDebugMode(false);
  }, [formula, onConfirm, onCancel]);

  /**
   * 公式编辑器准备就绪
   * @param editor - 编辑器实例
   */
  const handleEditorReady = useCallback(
    (editor: { insertAtPosition: (text: string, type: string, position?: number) => void }) => {
      editorRef.current = editor;
    },
    []
  );

  /**
   * 组件挂载时初始化函数列表
   */
  useEffect(() => {
    if (!visible) return;
    getFuncList();
  }, [visible]);

  /**点击调试页面的返回按钮 */
  const handleGoBack = () => {
    setIsDebugMode(false);
  };

  const getFieldType = (keyName: string, value: any) => {
    let fieldType: string = '';
    variables.forEach((variable) => {
      const fieldIndex = variable?.fields?.findIndex((item) => keyName.includes(item.displayName) && item.id === value);
      if (fieldIndex !== -1) {
        fieldType = variable?.fields?.[fieldIndex as number].fieldType || 'TEXT';
      }
    });
    return fieldType;
  };

  /**
   * 调试模式下获取只包含实体的数据
   *
   */
  const displayEntityField = () => {
    const currentVariablesObj = getParameters(formula);
    let newVariablesData: variableItem[] = [];
    if (variables.length > 0) {
      Object.keys(currentVariablesObj).forEach((key) => {
        if (!key.includes('$')) {
          newVariablesData.push({
            fieldName: key,
            fieldId: currentVariablesObj[key],
            fieldType: getFieldType(key, currentVariablesObj[key])
          });
        }
      });
    }
    return newVariablesData;
  };

  /**调试模式下获取只包含节点的数据 */
  const displaywithNodeField = () => {
    const currentVariablesObj = getParameters(formula);
    const newObj: fieldListWithNodeData = {};
    // 遍历原对象中以 $ 开头的键
    for (const key in currentVariablesObj) {
      if (key.startsWith('$')) {
        const [baseKey, name] = key.split('.'); // 分割为 [基础key, 字段名name]
        const id = currentVariablesObj[key]; // id 为原键对应的值
        if (name) {
          // 初始化 newObj 中的基础 key 结构，fieldList 为对象数组
          if (!newObj[baseKey]) {
            newObj[baseKey] = { fieldList: [] };
          }
          // 构建 { id, name } 对象并添加到 fieldList
          newObj[baseKey].fieldList.push({ fieldId: id, fieldName: name, fieldType: 'NUMBER' });
        }
      }
    }
    return newObj;
  };

  /**点击调试 */
  const handleClickDebug = () => {
    setIsDebugMode(true);
  };

  /**点击取消按钮 */
  const handleCancel = () => {
    setIsDebugMode(false);
    onCancel();
  };

  const entityFields = displayEntityField();

  const tableData = displaywithNodeField();

  return (
    <Modal
      visible={visible}
      onCancel={handleCancel}
      title={
        <div className={styles.formulaHeader}>
          {isDebugMode && <IconLeft className={styles.goBack} onClick={handleGoBack} />}
          <span className={styles.title}>公式编辑</span>
          <span className={styles.subtitle}>使用数学运算符编辑公式</span>
        </div>
      }
      style={{ width: '1000px' }}
      className={styles.formulaEditor}
      maskClosable={false}
      onOk={handleConfirm}
      okButtonProps={{
        disabled: !formula
      }}
    >
      {/* 内容区域 */}
      <div className={styles.contentWrapper}>
        {/* 公式编辑区 */}
        <FormulaInput
          fieldName={fieldName}
          value={formula}
          onChange={setFormula}
          onCopy={handleCopy}
          onDebug={handleClickDebug}
          filteredVariables={filteredVariables}
          filteredFunctions={filteredFunctions}
          onEditorReady={handleEditorReady}
        />
        {/* 底部面板（变量名称/函数公式/函数概要） */}
        {isDebugMode ? (
          <DebuggedFormula entityFields={entityFields} tableData={tableData} formula={formattedFormula()} />
        ) : (
          <Row>
            <Col xs={2} sm={4} md={8} lg={8} xl={8} xxl={8}>
              <VariableList
                variables={variableSearch ? filteredVariables : variables}
                searchValue={variableSearch}
                onSearchChange={setVariableSearch}
                onInsertVariable={handleInsertVariable}
              />
            </Col>
            <Col xs={20} sm={16} md={8} lg={8} xl={8} xxl={8}>
              <FunctionList
                functions={functionSearch ? filteredFunctions : funcList}
                searchValue={functionSearch}
                onSearchChange={setFunctionSearch}
                onChooseFunction={handleChooseFunction}
              />
            </Col>
            {info && (
              <Col xs={2} sm={4} md={8} lg={8} xl={8} xxl={8}>
                <InfoPanel info={info} />
              </Col>
            )}
          </Row>
        )}
      </div>
    </Modal>
  );
}
