import { Modal, Button, Space, Message } from '@arco-design/web-react';
import { useState, useCallback, useMemo, useRef, useEffect } from 'react';
import { VariableList, FunctionList, InfoPanel, FormulaInput } from './components';
import { getFormulaFunctionSimpleList, getFormulaById } from '@onebase/app';
import type { Variable, FunctionItem, FormulaEditorProps, info } from './utils/types';
import styles from './index.module.less';

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

// const mockFunctions: FunctionItem[] = [
//   { id: '1', name: 'SUM', summary: '求和', type: '常用函数' },
//   { id: '2', name: 'AVERAGE', summary: '平均值', type: '常用函数' },
//   { id: '3', name: 'IF', summary: '条件判断', type: '常用函数' },
//   { id: '4', name: 'AND', summary: '与', type: '常用函数' },
//   { id: '5', name: 'OR', summary: '或', type: '常用函数' },
//   { id: '6', name: 'NOT', summary: '非', type: '常用函数' },
//   { id: '7', name: 'CONCATENATE', summary: '合并', type: '常用函数' },
//   { id: '8', name: 'TODAY', summary: '今天', type: '常用函数' }
// ];

export function FormulaEditor({ visible, onCancel, onConfirm, initialFormula = '' }: FormulaEditorProps) {
  const [formula, setFormula] = useState(initialFormula);
  const [variableSearch, setVariableSearch] = useState('');
  const [functionSearch, setFunctionSearch] = useState('');
  const [funcList, setFuncList] = useState<FunctionItem[]>([]);
  const editorRef = useRef<{ insertAtPosition: (text: string, type: string, position?: number) => void } | null>(null);
  const [info, setInfo] = useState<info>();
  const getFuncList = async () => {
    const res = await getFormulaFunctionSimpleList();
    if (res) {
      setFuncList(res);
    }
  };

  // 过滤变量和函数
  const filteredVariables = useMemo(() => {
    if (!variableSearch) return mockVariables;
    return mockVariables.filter(
      (v) =>
        v.name.toLowerCase().includes(variableSearch.toLowerCase()) ||
        v.type.toLowerCase().includes(variableSearch.toLowerCase())
    );
  }, [variableSearch]);

  const filteredFunctions = useMemo(() => {
    if (!functionSearch) return funcList;
    return funcList.filter(
      (f) =>
        f.name.toLowerCase().includes(functionSearch.toLowerCase()) ||
        f.summary.toLowerCase().includes(functionSearch.toLowerCase())
    );
  }, [functionSearch]);

  // 插入变量到公式
  const handleInsertVariable = useCallback((variable: Variable) => {
    if (editorRef.current) {
      // 使用编辑器的插入方法，支持光标定位
      editorRef.current.insertAtPosition(`[[${variable.value}.${variable.name}]]`, 'var');
    } else {
      // 降级处理：直接添加到末尾
      setFormula((prev) => prev + variable.name);
    }
  }, []);

  const getFormulaInfo = (id: string) => {
    getFormulaById(id).then((res) => {
      if (res) {
        setInfo(res);
      }
    });
  };

  // 点击函数
  const handleChooseFunction = useCallback((func: FunctionItem) => {
    getFormulaInfo(func.id);

    console.log('editorRef', editorRef);

    // 插入函数到公式
    if (editorRef.current) {
      // 使用编辑器的插入方法，支持光标定位
      editorRef.current.insertAtPosition(`{{${func.id}.${func.expression}}}()`, 'fn');
    } else {
      // 降级处理：直接添加到末尾
      setFormula((prev) => prev + func.name + '()');
    }
  }, []);

  // 复制公式
  const handleCopy = useCallback(() => {
    navigator.clipboard.writeText(formula);
    Message.success('公式已复制到剪贴板');
  }, [formula]);

  // 调试公式
  const handleDebug = useCallback(() => {
    console.log('调试公式:', formula);
    Message.info('公式调试信息已输出到控制台');
  }, [formula]);

  // 确认公式
  const handleConfirm = useCallback(() => {
    onConfirm(formula);
    onCancel();
  }, [formula, onConfirm, onCancel]);

  // 编辑器就绪回调
  const handleEditorReady = useCallback(
    (editor: { insertAtPosition: (text: string, type: string, position?: number) => void }) => {
      editorRef.current = editor;
    },
    []
  );

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
        {/* 变量列表 */}
        <div className={styles.panel}>
          <VariableList
            variables={filteredVariables}
            searchValue={variableSearch}
            onSearchChange={setVariableSearch}
            onInsertVariable={handleInsertVariable}
          />
        </div>

        {/* 函数列表 */}
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
