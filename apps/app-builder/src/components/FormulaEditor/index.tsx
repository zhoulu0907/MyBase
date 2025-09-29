import { Button, Message, Modal, Space } from '@arco-design/web-react';
import { getFormulaById, getFormulaFunctionSimpleList } from '@onebase/app';
import { useCallback, useEffect, useMemo, useRef, useState } from 'react';
import { FormulaInput, FunctionList, InfoPanel, VariableList } from './components';
import styles from './index.module.less';
import type { FormulaEditorProps, FunctionItem, info, Variable } from './utils/types';

// 模拟数据
const mockVariables: Variable[] = [
  { value: '1', name: '订单编号', type: '文本', category: '订单管理' },
  { value: '2', name: '制单人', type: '用户', category: '订单管理' },
  { value: '3', name: '下单日期', type: '时间戳', category: '订单管理' },
  { value: '4', name: '客户信息-主键', type: '文本', category: '订单管理' },
  { value: '5', name: '联系人', type: '文本', category: '订单管理' },
  { value: '6', name: '联系电话', type: '文本', category: '订单管理' },
  { value: '7', name: '发货地址', type: '地址', category: '订单管理' }
];

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
  const filteredVariables = useMemo(() => {
    if (!variableSearch) return mockVariables;
    return mockVariables.filter(
      (v) =>
        v.name.toLowerCase().includes(variableSearch.toLowerCase()) ||
        v.type.toLowerCase().includes(variableSearch.toLowerCase())
    );
  }, [variableSearch]);

  /**
   * 过滤函数列表
   * 根据函数名称或摘要是否包含搜索关键词（不区分大小写）
   */
  const filteredFunctions = useMemo(() => {
    console.log(functionSearch, 'functionSearch');
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
  const handleInsertVariable = useCallback((variable: Variable) => {
    if (editorRef.current) {
      // 使用编辑器的插入方法，支持光标定位
      editorRef.current.insertAtPosition(`[[${variable.value}.${variable.name}]]`, 'var');
    } else {
      // 降级处理：直接添加到末尾
      setFormula((prev) => prev + variable.name);
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
      title={null}
      footer={null}
      style={{ width: '1000px' }}
      className={styles.formulaEditor}
      maskClosable={false}
    >
      {/* 头部 */}
      <div className={styles.header}>
        <div className={styles.titleSection}>
          <h2 className={styles.title}>公式编辑</h2>
          <p className={styles.subtitle}>使用数学运算符编辑公式</p>
        </div>
      </div>

      {/* 公式编辑区 */}
      <div className={styles.formulaSection}>
        {/* 文本单行 */}
        <FormulaInput
          value={formula}
          onChange={setFormula}
          onCopy={handleCopy}
          onDebug={handleDebug}
          filteredVariables={filteredVariables}
          filteredFunctions={filteredFunctions}
          onEditorReady={handleEditorReady}
        />
      </div>

      {/* 底部面板 */}
      <div className={styles.panelsSection}>
        {/* 变量列表 - 订单管理 */}
        <div className={styles.panel}>
          <VariableList
            variables={filteredVariables}
            searchValue={variableSearch}
            onSearchChange={setVariableSearch}
            onInsertVariable={handleInsertVariable}
          />
        </div>

        {/* 函数列表 - 常用函数 */}
        <div className={styles.panel}>
          <FunctionList
            functions={functionSearch ? filteredFunctions : funcList}
            searchValue={functionSearch}
            onSearchChange={setFunctionSearch}
            onChooseFunction={handleChooseFunction}
          />
        </div>

        {/* 说明面板 */}
        <div className={styles.panel}>
          <InfoPanel info={info} />
        </div>
      </div>

      {/* 底部按钮 */}
      <div className={styles.footer}>
        <Space>
          <Button onClick={onCancel}>取消</Button>
          <Button type="primary" onClick={handleConfirm}>
            确定
          </Button>
        </Space>
      </div>
    </Modal>
  );
}
