import { Message, Modal, Grid } from '@arco-design/web-react';
import { getFormulaById, getFormulaFunctionSimpleList, type AppEntities, type AppEntity, type AppEntityField } from '@onebase/app';
import { useCallback, useEffect, useMemo, useRef, useState } from 'react';
import { FormulaInput, FunctionList, InfoPanel, VariableList } from './components';
import styles from './index.module.less';
import type { FormulaEditorProps, FunctionItem, info } from './utils/types';
const Row = Grid.Row;
const Col = Grid.Col;
// 模拟数据
const mockVariables: AppEntities = {
  entities: [
    {
      entityId: '1', entityName: '订单管理', entityType: '主表', fields: [
        {
          fieldId: "001", fieldName: "订单编号", fieldType: "BIGINT", isSystemField: 1, displayName: "订单编号"
        },
        {
          fieldId: "002", fieldName: "制单人", fieldType: "VARCHAR", isSystemField: 1, displayName: "制单人"
        },
        {
          fieldId: "003", fieldName: "发货地址", fieldType: "TEXT", isSystemField: 1, displayName: "发货地址"
        },
        {
          fieldId: "004", fieldName: "下单日期", fieldType: "DECIMAL", isSystemField: 1, displayName: "下单日期"
        },
        {
          fieldId: "005", fieldName: "联系电话", fieldType: "NUMBER", isSystemField: 1, displayName: "联系电话"
        },
        {
          fieldId: "005", fieldName: "客户信息-主键", fieldType: "TIMESTAMP", isSystemField: 1, displayName: "客户信息-主键"
        }
      ]
    },
    {
      entityId: '2', entityName: '其他管理', entityType: '主表', fields: [
        {
          fieldId: "001", fieldName: "测试1", fieldType: "BIGINT", isSystemField: 1, displayName: "测试1"
        },
        {
          fieldId: "002", fieldName: "测试2", fieldType: "INT", isSystemField: 1, displayName: "测试2"
        }
      ]
    }
  ]
};

/**
 * 公式编辑器
 * @param visible - 编辑器是否可见
 * @param onCancel - 取消编辑回调
 * @param onConfirm - 确认编辑回调
 * @param initialFormula - 初始公式
 */
export function FormulaEditor({ visible, onCancel, onConfirm, initialFormula = '' }: FormulaEditorProps) {
  const [formula, setFormula] = useState(initialFormula);
  const [variableSearch, setVariableSearch] = useState('');
  const [functionSearch, setFunctionSearch] = useState('');
  const [funcList, setFuncList] = useState<FunctionItem[]>([]);
  const editorRef = useRef<{ insertAtPosition: (text: string, type: string, position?: number) => void } | null>(null);
  const [info, setInfo] = useState<info | null>(null);

  useEffect(() => {
    setFormula(initialFormula);
  }, [initialFormula]);

  /**
   * 初始化函数列表
   */
  const getFuncList = async () => {
    const res = await getFormulaFunctionSimpleList();
    if (res) {
      console.log(res, 'getFuncList>>>>>>>>>>');
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
  const filteredVariables:AppEntity[] = useMemo(() => {
    if (!variableSearch) return mockVariables?.entities || [];
   const newFields = mockVariables?.entities?.[0]?.fields?.filter(
      (v) =>
        v.fieldName.toLowerCase().includes(variableSearch.toLowerCase()) ||
        v.fieldName.toLowerCase().includes(variableSearch.toLowerCase())
    ) || [];
    mockVariables.entities[0].fields = newFields;
    return mockVariables.entities
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
  const handleInsertVariable = useCallback((variable: AppEntityField) => {
    if (editorRef.current) {
      // 使用编辑器的插入方法，支持光标定位
      editorRef.current.insertAtPosition(`[[${variable.fieldId}.${variable.fieldName}]]`, 'var');
    } else {
      // 降级处理：直接添加到末尾
      setFormula((prev) => prev + variable.fieldName);
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
   * 调试公式
   */
  const handleDebug = useCallback(() => {
    console.log('调试公式:', formula);
    Message.info('公式调试信息已输出到控制台');
  }, [formula]);

  /**
   * 确认公式
   */
  const handleConfirm = useCallback(() => {
    onConfirm(formula);
    onCancel();
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
    >
      {/* 内容区域 */}
      <div className={styles.contentWrapper}>
        {/* 公式编辑区 */}
        <FormulaInput
          value={formula}
          onChange={setFormula}
          onCopy={handleCopy}
          onDebug={handleDebug}
          filteredVariables={filteredVariables}
          filteredFunctions={filteredFunctions}
          onEditorReady={handleEditorReady}
        />
        {/* 底部面板（变量名称/函数公式/函数概要） */}
        <Row>
          <Col xs={2} sm={4} md={6} lg={8} xl={10} xxl={8}>
            <VariableList
              variables={filteredVariables}
              searchValue={variableSearch}
              onSearchChange={setVariableSearch}
              onInsertVariable={handleInsertVariable}
            />
          </Col>
          <Col xs={20} sm={16} md={12} lg={8} xl={4} xxl={8}>
            <FunctionList
              functions={functionSearch ? filteredFunctions : funcList}
              searchValue={functionSearch}
              onSearchChange={setFunctionSearch}
              onChooseFunction={handleChooseFunction}
            />
          </Col>
          <Col xs={2} sm={4} md={6} lg={8} xl={10} xxl={8}>
            <InfoPanel info={info} />
          </Col>
        </Row>
      </div>
    </Modal>
  );
}
