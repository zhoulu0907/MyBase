import { Message, Modal, Grid } from '@arco-design/web-react';
import { getFormulaById, getFormulaFunctionSimpleList, type VariablesList, type variableItem, getEntityListByApp, getEntityFields, type ChildVariablesField } from '@onebase/app';
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
export function FormulaEditor({ visible, onCancel, onConfirm, initialFormula = '' }: FormulaEditorProps) {
  const [formula, setFormula] = useState(initialFormula);  //公式的值
  const [variableSearch, setVariableSearch] = useState('');
  const [functionSearch, setFunctionSearch] = useState('');
  const [funcList, setFuncList] = useState<FunctionItem[]>([]); //公式编辑器中的函数列表展示
  const editorRef = useRef<{ insertAtPosition: (text: string, type: string, position?: number) => void } | null>(null);
  const [info, setInfo] = useState<info | null>(null);
  const [variables, setVariables] = useState<VariablesList[]>([]) //公式编辑器中的左侧变量列表展示
  const [isDebugMode, setIsDebugMode] = useState<boolean>(false);
  const { curAppId } = useAppStore();

  useEffect(() => {
    setFormula(initialFormula);
  }, [initialFormula]);

  useEffect(() => {
    curAppId && retrievedEntityListByApp();
  }, [curAppId])
  /**
   * 获取变量一级列表
   */
  const nodeTypes = [
    NodeType.DATA_QUERY,
    NodeType.DATA_QUERY_MULTIPLE,
    NodeType.DATA_CALC
  ];

  const retrievedEntityListByApp = async () => {
    const nodes = getPrecedingNodes(curAppId, triggerEditorSignal.nodes.value, nodeTypes);
    try {
      const res: any[] = await getEntityListByApp(curAppId);
      if (res.length > 0) {
        const result = await Promise.all(res.map(async (item) => {
          try {
            if (!item.fields?.length) {
              const childEntityList = await getEntityFields({
                entityId: item.entityId
              });
              return {
                ...item,
                variableId: item.entityId,
                variableName: item.entityName,
                fields: [...item.fields || [], ...childEntityList].reverse()
              }
            } else {
              return item;
            }
          } catch (error) {
            console.log("加载二级实体列表失败:", error)
          }
        }))
        nodes?.forEach(nodeItem => {
          const nodeOutput = triggerNodeOutputSignal.getTriggerNodeOutput(nodeItem.id);
          const newFields = nodeOutput?.conditionFields?.map((data: any) => {
            return {
              ...data,
              displayName: data.label,
              entityId: nodeItem.id,
              appId: nodeItem.id,
              fieldName: nodeItem.data?.title || "",
              fieldType: "node",
            }
          })
          const reverseNewFields = (newFields || []).reverse();
          result.push({ variableName: nodeItem.data?.title, fields: reverseNewFields, variableId: nodeItem.id, tableName: "" });
        })
        console.log("result", result)
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
    const newFields = variables?.[0]?.fields?.filter(
      (v) =>
        v.fieldName.toLowerCase().includes(variableSearch.toLowerCase()) ||
        v.fieldName.toLowerCase().includes(variableSearch.toLowerCase())
    ) || [];
    variables[0].fields = newFields;
    return variables
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
      if (variable.fieldType === "node") {
        editorRef.current.insertAtPosition(`[[${variable.appId}.${variable.value}.$${variable.fieldName}.${variable.displayName}]]`, 'var');
      } else {
        editorRef.current.insertAtPosition(`[[${variable.appId}.${variable.displayName}]]`, 'var');
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
      if (match.startsWith("{{")) {
        content = match.slice(2, -2);
        content = content.replace(/^[^.]+\./, '');
      } else {
        content = match.slice(2, -2)
        content = content.replace(/^[^\.]+\.(.+)$/, '$1,');
        const temp = content.split("$");
        if (temp[1]) {
          content = `$${temp[1]}`
        }
      }
      return content;
    })
    return newFormula.replace(/,(?=\s*\))/g, '');
  }

  /**
   * 根据formula生成paramters信息{[变量名称]: fieldId}
   * @param formulaData 
   * @returns 
   */
  const retrieveAllVariables = (formulaData: string) => {
    const regex = /\[\[(.*?)\]\]/g;
    const copyFormulaData = formulaData;
    const matches = [...copyFormulaData.matchAll(regex)];
    const variablesMapping: { [key: string]: string } = {};
    matches.forEach((match) => {
      const temp = match[1].split(".");
      if (temp.length > 2) {
        variablesMapping[temp[2]] = temp[0];
        variablesMapping[temp[3]] = temp[1];
      } else {
        const fieldId = temp[0] || "";
        const fieldName = temp[1] || "";
        variablesMapping[fieldName] = fieldId;
      }
    })
    return variablesMapping;
  }

  /**
   * 点击确认之后计算
   */
  const handleConfirm = useCallback(async () => {
    // await handleDebug();
    onConfirm(formula);
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
  }

  const getAllRelatedVariables = () => {
    const currentVariablesObj = retrieveAllVariables(formula);
    let newVariablesData: variableItem[] = [];
    if(variables.length >0) {
      Object.keys(currentVariablesObj).forEach(key => {
        const variableIndex = variables.findIndex(data => data.variableId === currentVariablesObj[key]);
        const fieldIndex = variables[variableIndex]?.fields?.findIndex(field => field.displayName === key);
        if(fieldIndex !== -1) {
          newVariablesData.push({
              fieldName: key,
              fieldId: currentVariablesObj[key],
              fieldType: fieldIndex !== -1 ? variables[fieldIndex as any] : "TEXT"
          })
        }
      })
    }
    return newVariablesData;
  }

  /**点击调试 */
  const handleClickDebug = () => {
    setIsDebugMode(true);
  }

  /**点击取消按钮 */
  const handleCancel = () => {
    setIsDebugMode(false);
    onCancel();
  }

  const allRelatedVariables = getAllRelatedVariables();

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
            value={formula}
            onChange={setFormula}
            onCopy={handleCopy}
            onDebug={handleClickDebug}
            filteredVariables={filteredVariables}
            filteredFunctions={filteredFunctions}
            onEditorReady={handleEditorReady}
        />
        {/* 底部面板（变量名称/函数公式/函数概要） */}
        {isDebugMode ?
          <DebuggedFormula allRelatedVariables={allRelatedVariables} formula={formattedFormula()} /> :
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
            {info &&
              <Col xs={2} sm={4} md={8} lg={8} xl={8} xxl={8}>
                <InfoPanel info={info} />
              </Col>}
          </Row>
        }
      </div>
    </Modal>
  );
}
