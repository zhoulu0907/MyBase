import { getPrecedingNodes } from '@/pages/CreateApp/pages/IntegratedManagement/triggerEditor/nodes/utils';
import { useAppStore } from '@/store';
import { triggerEditorSignal } from '@/store/singals/trigger_editor';
import { triggerNodeOutputSignal } from '@/store/singals/trigger_node_output';
import { Grid, Message, Modal, Spin } from '@arco-design/web-react';
import { IconLeft } from '@arco-design/web-react/icon';
import {
  RELATION_TYPE,
  debugFormula,
  getEntityFields,
  getEntityListByApp,
  getFormulaById,
  getFormulaFunctionSimpleList,
  type ChildVariablesField,
  type fieldListWithNodeData,
  type formulaParams,
  type variableItem,
  type VariablesList
} from '@onebase/app';
import { cloneDeep } from 'lodash-es';
import { NodeType } from '@onebase/common';
import { useCallback, useEffect, useMemo, useRef, useState } from 'react';
import { DebuggedFormula, FormulaInput, FunctionList, InfoPanel, VariableList } from './components';
import styles from './index.module.less';
import { functionType } from './utils/formula';
import type { FormulaEditorProps, functionGroup, FunctionItem, info } from './utils/types';
import copy from 'copy-to-clipboard';

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
  const [activeKey, setActiveKey] = useState<string[]>([functionType.COMMON]);
  const [variableSearch, setVariableSearch] = useState('');
  const [functionSearch, setFunctionSearch] = useState('');
  const [functionLoading, setFunctionLoading] = useState<boolean>(false);
  const [variableLoading, setVariableLoading] = useState<boolean>(false);
  const [funcList, setFuncList] = useState<functionGroup[]>([]); //公式编辑器中的函数列表展示
  const editorRef = useRef<{ insertAtPosition: (text: string, type: string, position?: number) => void } | null>(null);
  const [info, setInfo] = useState<info | null>(null);
  const [variables, setVariables] = useState<VariablesList[]>([]); //公式编辑器中的左侧变量列表展示
  const [isDebugMode, setIsDebugMode] = useState<boolean>(false);
  const [loading, setLoading] = useState<boolean>(false);
  const [error, setError] = useState<string>('');
  const { curAppId } = useAppStore();

  useEffect(() => {
    curAppId && retrievedEntityListByApp();
  }, [curAppId]);

  //监听显示,数据初始化
  useEffect(() => {
    if (visible) {
      init();
    }
  }, [visible]);

  /**
   * 组件挂载时初始化函数列表
   */
  useEffect(() => {
    getFuncList();
  }, []);

  const init = () => {
    // 公式初始化
    setFormula(initialFormula);
    // 变量初始化
    setVariableSearch('');
    // 常用函数初始化
    setFunctionSearch('');
  };

  /**
   * 获取变量一级列表
   */
  const nodeTypes = [
    NodeType.START_FORM,
    NodeType.START_ENTITY,
    NodeType.DATA_QUERY,
    NodeType.DATA_QUERY_MULTIPLE,
    NodeType.DATA_CALC
  ];

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
                // 子表字段：例如子表叫“订单明细”、字段叫“数量”，则这个字段在此处的列表里叫“订单明细.数量”
                const hasChildTable = item.relationshipTypes.includes('SUBTABLE_ONE_TO_MANY');
                if (item.relationType === RELATION_TYPE.SLAVE && hasChildTable) {
                  return {
                    ...item,
                    variableId: item.entityId,
                    variableName: item.entityName,
                    fields: [...(item.fields || []), ...childEntityList]
                      .reverse()
                      .map((ele) => ({ ...ele, displayName: `${item.entityName}.${ele.displayName}` }))
                  };
                }
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

        setVariables(result as any);
      }
    } catch (error) {
      Message.error('加载变量列表失败');
    } finally {
      setVariableLoading(false);
    }
  };

  /**
   * 初始化函数列表
   */
  const getFuncList = async () => {
    setFunctionLoading(true);
    try {
      const res = await getFormulaFunctionSimpleList();
      if (res) {
        setFuncList(res);
      }
    } catch (error) {
      Message.error('获取函数列表失败');
    } finally {
      setFunctionLoading(false);
    }
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
    const cloneVariables = cloneDeep(variables);
    const newFields =
      cloneVariables?.[0]?.fields?.filter(
        (v) =>
          v.displayName.toLowerCase().includes(variableSearch.toLowerCase()) ||
          v.displayName.toLowerCase().includes(variableSearch.toLowerCase())
      ) || [];
    cloneVariables[0].fields = newFields;
    return cloneVariables;
  }, [variableSearch]);

  /**
   * 过滤函数列表
   * 根据函数名称或摘要是否包含搜索关键词（不区分大小写）
   */
  const filteredFunctions = useMemo(() => {
    if (!functionSearch) return funcList;
    let filteredData: functionGroup[] = [];
    funcList?.forEach((item: functionGroup) => {
      const matchingFunctions =
        item.functions?.filter(
          (f) =>
            f.name.toLowerCase().includes(functionSearch.toLowerCase()) ||
            f.summary.toLowerCase().includes(functionSearch.toLowerCase())
        ) || [];
      if (matchingFunctions.length > 0) {
        filteredData.push({
          type: item.type,
          functions: matchingFunctions
        });
      }
    });
    const currentActiveKey = filteredData?.[0]?.type;
    if (currentActiveKey) {
      setActiveKey([currentActiveKey]);
    }
    return filteredData;
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
  const handleEditorCopy = useCallback(async () => {
    try {
      copy(formula);
      Message.success('公式已复制到剪贴板');
    } catch (err) {
      if (navigator.clipboard) {
        await navigator.clipboard.writeText(formula);
        Message.success('公式已复制到剪贴板');
      } else {
        console.error('复制失败: ', err);
        const textarea = document.createElement('textarea');
        textarea.value = formula;
        document.body.appendChild(textarea);
        textarea.select();
        document.execCommand('copy');
        document.body.removeChild(textarea);
        Message.success('公式已复制到剪贴板');
      }
    }
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
        const raw = match.slice(2, -2);
        const rawParts = raw.split('.');
        const dollarIdx = rawParts.findIndex((p) => p.startsWith('$'));
        if (dollarIdx !== -1 && dollarIdx < rawParts.length - 1) {
          const nodeLabel = rawParts[dollarIdx].slice(1);
          const displayName = rawParts[dollarIdx + 1] || '';
          content = `$${nodeLabel}.${displayName},`;
        } else if (rawParts.length === 3) {
          // 2) 子表：[[id.子表.字段]] => $子表.字段
          const subTableName = rawParts[1] || '';
          const fieldName = rawParts[2] || '';
          content = `$${subTableName}.${fieldName},`;
        } else {
          // 3) 主表：[[id.字段]] => 字段
          const fieldName = rawParts[1] || '';
          content = `${fieldName},`;
        }
      }
      return content;
    });

    //最后一个replace处理的场景是if(数量, >90) 去除逗号
    return newFormula
      .replace(/,(?=\s*\))/g, '')
      .replace(/,+/g, ',')
      .replace(/([\u4e00-\u9fa5]+(?:\.[\u4e00-\u9fa5]+)?),(>=|<=|>|<<|==|!=|\*)/g, '$1$2')
      .replace(/,$/, '');
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
      const dollarIdx = temp.findIndex((p) => p.startsWith('$'));
      if (dollarIdx !== -1 && dollarIdx < temp.length - 1) {
        const nodeId = temp[0] || '';
        const tableName = temp[1] || '';
        const fieldName = temp[2] || '';
        const nodeLabel = temp[dollarIdx].slice(1);
        const displayName = temp[dollarIdx + 1] || '';
        
        if (nodeLabel && displayName) {
          const fullFieldPath = `${tableName}.${fieldName}`;
          variablesMapping[`$${nodeLabel}.${displayName}`] = fullFieldPath;
          variablesMapping[`$${nodeLabel}`] = nodeId;
        }
        return;
      }

      if (temp.length === 2) {
        const fieldId = temp[0] || '';
        const fieldName = temp[1] || '';
        if (fieldName) variablesMapping[fieldName] = fieldId;
        return;
      }

      if (temp.length === 3) {
        const fieldId = temp[0] || '';
        const fieldName = `${temp[1] || ''}.${temp[2] || ''}`;
        if (fieldName !== '.' && fieldName) variablesMapping[`$${fieldName}`] = fieldId;
        return;
      }
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

  /**点击调试页面的返回按钮 */
  const handleGoBack = () => {
    setIsDebugMode(false);
  };

  const getFieldType = (keyName: string, value: any) => {
    let fieldType: string = '';
    variables.forEach((variable) => {
      const directIndex = variable?.fields?.findIndex(
        (item: any) => item.id === value || item.value === value || item.fieldId === value || item.fieldUuid === value
      );
      if (directIndex !== -1) {
        fieldType = variable?.fields?.[directIndex as number].fieldType || 'TEXT';
        return;
      }

      const fieldIndex = variable?.fields?.findIndex((item: any) => {
        const display = item.displayName || item.label || item.fieldName || '';
        return (
          !!display &&
          keyName.includes(display) &&
          (item.id === value || item.value === value || item.fieldId === value || item.fieldUuid === value)
        );
      });
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
        if (!key.includes('$') && !key.includes('.')) {
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

  /**
   * 调试模式下获取只包含交互/节点的数据（单值输入）
   * token 形如：[[nodeId.fieldId.xxx.$节点名.字段名]] 或 [[nodeId.fieldId.$节点名.字段名]]
   * 输出字段名：$节点名.字段名
   */
  const displayNodeField = () => {
    const regex = /\[\[(.*?)\]\]/g;
    const matches = [...formula.matchAll(regex)];
    const dedupe = new Set<string>();
    const result: variableItem[] = [];

    matches.forEach((m) => {
      const parts = m[1].split('.');
      const dollarIdx = parts.findIndex((p: string) => p.startsWith('$'));
      if (dollarIdx !== -1 && dollarIdx < parts.length - 1) {
        const nodeLabel = parts[dollarIdx].slice(1);
        const displayName = parts[dollarIdx + 1] || '';
        const fieldId = parts[1] || '';
        const fieldName = `$${nodeLabel}.${displayName}`;
        if (nodeLabel && displayName && fieldId && !dedupe.has(fieldName)) {
          dedupe.add(fieldName);
          result.push({
            fieldName,
            fieldId,
            fieldType:
              getFieldType(
                displayName,
                dollarIdx >= 3 ? `${parts[1] || ''}.${parts[dollarIdx - 1] || ''}` : fieldId
              ) || 'TEXT'
          });
        }
      }
    });
    return result;
  };

  /**调试模式下获取只包含节点的数据 */
  const displaywithNodeField = () => {
    const newObj: fieldListWithNodeData = {};
    const regex = /\[\[(.*?)\]\]/g;
    const matches = [...formula.matchAll(regex)];
    matches.forEach((match) => {
      const parts = match[1].split('.');
      if (parts.length === 3) {
        const fieldId = parts[0] || '';
        const subTableName = parts[1] || '';
        const fieldName = parts[2] || '';
        if (!subTableName || !fieldName) return;

        const baseKey = `$${subTableName}`;
        if (!newObj[baseKey]) {
          newObj[baseKey] = { fieldList: [] };
        }

        const exists = newObj[baseKey].fieldList.some((f: any) => f.fieldName === fieldName);
        if (!exists) {
          const keyNameForType = `${subTableName}.${fieldName}`;
          newObj[baseKey].fieldList.push({
            fieldId,
            fieldName,
            fieldType: getFieldType(keyNameForType, fieldId)
          });
        }
      }
    });
    return newObj;
  };

  /**点击调试 */
  const handleClickDebug = async () => {
    setLoading(true);
    const newFormula = formattedFormula();
    const selectedVariables = getParameters(formula);
    const newFormulaData: formulaParams = {
      formula: newFormula,
      parameters: selectedVariables
    };
    try {
      await debugFormula(newFormulaData);
      setIsDebugMode(true);
    } catch (error: any) {
      if (error.message) {
        setError(error.message);
      }
    } finally {
      setLoading(false);
    }
  };

  /**点击取消按钮 */
  const handleCancel = () => {
    setIsDebugMode(false);
    onCancel();
  };

  const entityFields = displayEntityField();

  const tableData = displaywithNodeField();

  const nodeFields = displayNodeField();

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
      unmountOnExit
    >
      {/* 内容区域 */}
      <div className={styles.contentWrapper}>
        {/* 公式编辑区 */}
        <Spin loading={loading} style={{ marginTop: '8px', display: 'block' }}>
          <FormulaInput
            fieldName={fieldName}
            value={formula}
            error={error}
            isDebugMode={isDebugMode}
            onChange={setFormula}
            onCopy={handleEditorCopy}
            onDebug={handleClickDebug}
            filteredVariables={filteredVariables}
            filteredFunctions={filteredFunctions}
            onEditorReady={handleEditorReady}
          />
        </Spin>
        {/* 底部面板（变量名称/函数公式/函数概要） */}
        {isDebugMode ? (
          <DebuggedFormula
            entityFields={entityFields}
            nodeFields={nodeFields}
            tableData={tableData}
            formula={formattedFormula()}
          />
        ) : (
          <Row>
            <Col xs={2} sm={4} md={8} lg={8} xl={8} xxl={8}>
              <VariableList
                variableLoading={variableLoading}
                variables={variableSearch ? filteredVariables : variables}
                searchValue={variableSearch}
                onSearchChange={setVariableSearch}
                onInsertVariable={handleInsertVariable}
              />
            </Col>
            <Col xs={20} sm={16} md={8} lg={8} xl={8} xxl={8}>
              <FunctionList
                activeKey={activeKey}
                setActiveKey={setActiveKey}
                functions={functionSearch ? filteredFunctions : funcList}
                functionLoading={functionLoading}
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
