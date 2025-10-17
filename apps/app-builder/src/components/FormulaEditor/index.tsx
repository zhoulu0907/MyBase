import { Message, Modal, Grid, Spin } from '@arco-design/web-react';
import { getFormulaById, getFormulaFunctionSimpleList, type VariablesEntity, executeFormula, type formulaParams, getEntityListByApp, getEntityFields, type ChildEntityField } from '@onebase/app';
import { useCallback, useEffect, useMemo, useRef, useState } from 'react';
import { FormulaInput, FunctionList, InfoPanel, VariableList } from './components';
import styles from './index.module.less';
import type { FormulaEditorProps, FunctionItem, info } from './utils/types';
import { useAppStore } from '@/store';

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
  const [formula, setFormula] = useState(initialFormula);
  const [loading, setLoading] = useState<boolean>(false);
  const [variableSearch, setVariableSearch] = useState('');
  const [functionSearch, setFunctionSearch] = useState('');
  const [funcList, setFuncList] = useState<FunctionItem[]>([]);
  const editorRef = useRef<{ insertAtPosition: (text: string, type: string, position?: number) => void } | null>(null);
  const [info, setInfo] = useState<info | null>(null);
  const [variables, setVariables] = useState<VariablesEntity[]>([])
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
  const retrievedEntityListByApp = async () => {
    try {
      const res: VariablesEntity[] = await getEntityListByApp(curAppId);
      if (res.length > 0) {
        const result = await Promise.all(res.map(async (item) => {
          try {
            if (!item.fields?.length) {
              const childEntityList = await getEntityFields({
                entityId: item.entityId
              });
              return {
                ...item,
                fields: [...item.fields || [], ...childEntityList]
              }
            } else {
              return item;
            }
          } catch (error) {
            console.log("加载二级实体列表失败:", error)
          }
        }))
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
  const filteredVariables: VariablesEntity[] = useMemo(() => {
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
  const handleInsertVariable = useCallback((variable: ChildEntityField) => {
    if (editorRef.current) {
      // 使用编辑器的插入方法，支持光标定位
      editorRef.current.insertAtPosition(`[[${variable.appId}.${variable.displayName}]]`, 'var');
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
      const fieldId = temp[0] || "";
      const fieldName = temp[1] || "";
      variablesMapping[fieldName] = fieldId;
    })
    return variablesMapping;
  }

  /**
   * 点击确认之后计算
   */
  const handleConfirm = useCallback(async () => {
    await handleDebug();
    onConfirm(formula);
  }, [formula, onConfirm, onCancel]);

  /**
   * 调试公式
   */
  const handleDebug = useCallback(async () => {
    setLoading(true);
    const newFormula = formattedFormula();
    const selectedVariables = retrieveAllVariables(formula);
    const newFormulaData: formulaParams = {
      formula: newFormula,
      parameters: selectedVariables
    }
    try {
      await executeFormula(newFormulaData);
      Message.info('公式调试成功');
    } catch (error) {
      console.log(error)
    } finally {
      setLoading(false)
    }
  }, [formula]);

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

  return (
    <Modal
      visible={visible}
      onCancel={onCancel}
      title={
        <div className={styles.formulaHeader}>
          <span className={styles.title}>公式编辑</span>
          <span className={styles.subtitle}>使用数学运算符编辑公式</span>
        </div>
      }
      style={{ width: '1000px' }}
      className={styles.formulaEditor}
      maskClosable={false}
      onOk={handleConfirm}
      confirmLoading={loading}
      okButtonProps={{
        disabled: !formula
      }}
    >
      {/* 内容区域 */}
      <div className={styles.contentWrapper}>
        {/* 公式编辑区 */}
        <Spin loading={loading} style={{ display: 'block', marginTop: 8, }}>
          <FormulaInput
            value={formula}
            onChange={setFormula}
            onCopy={handleCopy}
            onDebug={handleDebug}
            filteredVariables={filteredVariables}
            filteredFunctions={filteredFunctions}
            onEditorReady={handleEditorReady}
          />
        </Spin>
        {/* 底部面板（变量名称/函数公式/函数概要） */}
        <Row>
          <Col xs={2} sm={4} md={8} lg={8} xl={8} xxl={8}>
            <VariableList
              variables={variables}
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
      </div>
    </Modal>
  );
}
