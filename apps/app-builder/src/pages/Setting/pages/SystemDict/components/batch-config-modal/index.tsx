import React, { useEffect, useState } from 'react';
import { Button, Form, Message, Modal, Switch, Input, ColorPicker, Spin } from '@arco-design/web-react';
import { IconDelete, IconTranslate, IconPlusCircle } from '@arco-design/web-react/icon';
import { arrayMove } from 'react-sortable-hoc';
import type { DictData } from '@onebase/platform-center';
import { useCodeGenerator, type DictValueItem } from '../../hooks/useCodeGenerator';
import CodeGenerationConfirmModal from '../code-generation-confirm-modal';
import SortableTable from './SortableTable';
import styles from './index.module.less';

// DictValueItem 类型已从 useCodeGenerator hook 中导入

interface BatchConfigModalProps {
  visible: boolean;
  onCancel: () => void;
  onOk: (values: DictValueItem[]) => void;
  loading?: boolean;
  initialValues?: DictData[];
}

const BatchConfigModal: React.FC<BatchConfigModalProps> = ({
  visible,
  onCancel,
  onOk,
  loading = false,
  initialValues = []
}) => {
  const [form] = Form.useForm();
  const [colorMode, setColorMode] = useState(true);
  const [dictValues, setDictValues] = useState<DictValueItem[]>([]);
  const [showConfirmModal, setShowConfirmModal] = useState(false);

  // 使用编码生成hook
  const { isGenerating, generateCodes, canGenerate } = useCodeGenerator({
    onSuccess: (generatedItems) => {
      setDictValues(generatedItems);
      form.setFieldsValue({ dictValues: generatedItems });
      Message.success('编码生成成功');
    },
    onError: (error) => {
      Message.error(`编码生成失败: ${error.message}`);
    }
  });

  useEffect(() => {
    if (visible) {
      // 初始化数据
      const initialDictValues = initialValues.map((item, index) => ({
        id: item.id ? item.id.toString() : `temp-${Date.now()}-${index}`,
        label: item.label || '',
        value: item.value || '',
        color: 'rgb(var(--primary-6))', // 默认颜色
        status: item.status || 1,
        sort: item.sort || index + 1
      }));
      setDictValues(initialDictValues);
      form.setFieldsValue({ dictValues: initialDictValues });
    } else {
      // 关闭时重置
      form.resetFields();
      setDictValues([]);
    }
  }, [visible, initialValues, form]);

  // 新增字典值
  const addDictValue = () => {
    const newValue: DictValueItem = {
      id: `temp-${Date.now()}-${dictValues.length}`,
      label: '',
      value: '',
      color: 'rgb(var(--primary-6))',
      status: 1,
      sort: dictValues.length + 1
    };
    const newValues = [...dictValues, newValue];
    setDictValues(newValues);
    form.setFieldsValue({ dictValues: newValues });
  };

  // 删除字典值
  const deleteDictValue = (id: string) => {
    const newValues = dictValues.filter((item) => item.id !== id);
    setDictValues(newValues);
    form.setFieldsValue({ dictValues: newValues });
  };

  // 更新字典值
  const updateDictValue = (id: string, field: keyof DictValueItem, value: string | number) => {
    const newValues = dictValues.map((item) => (item.id === id ? { ...item, [field]: value } : item));
    setDictValues(newValues);
    form.setFieldsValue({ dictValues: newValues });
  };

  // 处理拖拽排序
  const handleSort = ({ oldIndex, newIndex }: { oldIndex: number; newIndex: number }) => {
    const newValues = arrayMove([...dictValues], oldIndex, newIndex);
    // 更新排序
    const updatedValues = newValues.map((item, index) => ({
      ...item,
      sort: index + 1
    }));
    setDictValues(updatedValues);
    form.setFieldsValue({ dictValues: updatedValues });
  };

  // 一键生成编码 - 显示确认对话框
  const handleGenerateCodes = () => {
    if (!canGenerate(dictValues)) {
      Message.warning('没有需要生成编码的项');
      return;
    }
    setShowConfirmModal(true);
  };

  // 确认生成编码
  const handleConfirmGenerate = async () => {
    try {
      await generateCodes(dictValues);
      setShowConfirmModal(false);
    } catch {
      // 错误已在hook中处理
    }
  };

  // 创建表格列配置
  const createColumns = () => {
    const columns: Array<{
      title: React.ReactNode;
      dataIndex: string;
      width?: number;
      render?: (value: unknown, record: DictValueItem, index: number) => React.ReactNode;
    }> = [];

    // 颜色列（如果启用彩色模式）
    if (colorMode) {
      columns.push({
        title: '',
        dataIndex: 'color',
        width: 32,
        render: (_: unknown, record: DictValueItem) => (
          <ColorPicker
            value={record.color}
            onChange={(color) => {
              const colorValue = typeof color === 'string' ? color : String(color);
              updateDictValue(record.id, 'color', colorValue);
            }}
            size="small"
            className={styles.colorPicker}
          />
        )
      });
    }

    // 字典值列
    columns.push({
      title: (
        <>
          <span className={styles.requiredDot}>*</span>字典值
        </>
      ),
      dataIndex: 'label',
      width: 200,
      render: (_: unknown, record: DictValueItem, index: number) => (
        <Form.Item
          field={`dictValues.${index}.label`}
          rules={[{ required: true, message: '请输入字典值' }]}
          style={{ margin: 0 }}
        >
          <Input
            placeholder="请输入字典值"
            value={record.label}
            onChange={(value) => updateDictValue(record.id, 'label', value)}
          />
        </Form.Item>
      )
    });

    // 字典值编码列
    columns.push({
      title: (
        <>
          <span className={styles.requiredDot}>*</span>字典值编码
        </>
      ),
      dataIndex: 'value',
      width: 200,
      render: (_: unknown, record: DictValueItem, index: number) => (
        <Form.Item
          field={`dictValues.${index}.value`}
          rules={[{ required: true, message: '请输入字典值编码' }]}
          style={{ margin: 0 }}
        >
          <Input
            placeholder="请输入字典值编码"
            value={record.value}
            onChange={(value) => updateDictValue(record.id, 'value', value)}
          />
        </Form.Item>
      )
    });

    // 启用状态列
    columns.push({
      title: '启用状态',
      dataIndex: 'status',
      width: 100,
      render: (_: unknown, record: DictValueItem) => (
        <Switch
          checked={record.status === 1}
          onChange={(checked) => updateDictValue(record.id, 'status', checked ? 1 : 0)}
          size="small"
        />
      )
    });

    // 操作列
    columns.push({
      title: '操作',
      dataIndex: 'operation',
      width: 60,
      render: (_: unknown, record: DictValueItem) => (
        <Button
          type="text"
          status="danger"
          icon={<IconDelete />}
          onClick={() => deleteDictValue(record.id)}
          size="small"
        />
      )
    });

    return columns;
  };

  // 提交
  const handleOk = async () => {
    try {
      const formValues = await form.validate();
      const values = formValues.dictValues || [];

      // 过滤掉空的字典值
      const validValues = values.filter((item: DictValueItem) => item.label.trim() && item.value.trim());

      if (validValues.length === 0) {
        Message.warning('请至少添加一个有效的字典值');
        return;
      }

      console.log('validValues', validValues);

      onOk(validValues);
    } catch (error) {
      console.error('表单验证失败:', error);
    }
  };

  return (
    <>
      <Modal
        className={styles.batchConfigModal}
        title="字典值配置"
        visible={visible}
        onOk={handleOk}
        onCancel={onCancel}
        okText="确定"
        cancelText="取消"
        confirmLoading={loading || isGenerating}
        style={{ width: 800 }}
      >
        <Spin loading={isGenerating} tip="正在生成编码...">
          <div className={styles.configContainer}>
            {/* 彩色模式开关 */}
            <div className={styles.colorModeSection}>
              <span className={styles.colorModeLabel}>彩色模式</span>
              <Switch checked={colorMode} onChange={setColorMode} size="small" />
            </div>

            {/* 字典值列表 */}
            <div className={styles.dictValuesSection} id="dict-config-container">
              <Form form={form}>
                <Form.List field="dictValues">
                  {() => <SortableTable data={dictValues} columns={createColumns()} onSort={handleSort} />}
                </Form.List>
              </Form>
            </div>

            {/* 操作按钮 */}
            <div className={styles.actionButtons}>
              <Button type="text" onClick={addDictValue} className={styles.addButton}>
                <IconPlusCircle />
                新增字典值
              </Button>
              <Button
                type="text"
                onClick={handleGenerateCodes}
                className={styles.generateButton}
                disabled={isGenerating || !canGenerate(dictValues)}
              >
                <IconTranslate />
                一键生成编码
              </Button>
            </div>
          </div>
        </Spin>
      </Modal>

      {/* 编码生成确认对话框 */}
      <CodeGenerationConfirmModal
        visible={showConfirmModal}
        onConfirm={handleConfirmGenerate}
        onCancel={() => setShowConfirmModal(false)}
        loading={isGenerating}
      />
    </>
  );
};

export default BatchConfigModal;
