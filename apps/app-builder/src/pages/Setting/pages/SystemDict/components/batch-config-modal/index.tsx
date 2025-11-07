import React, { useEffect, useState } from 'react';
import { Button, Form, Message, Modal, Switch, Input, ColorPicker, Spin } from '@arco-design/web-react';
import { IconDelete, IconTranslate, IconPlusCircle } from '@arco-design/web-react/icon';
import { arrayMove } from 'react-sortable-hoc';
import type { DictData } from '@onebase/platform-center';
import { useCodeGenerator } from '../../hooks/useCodeGenerator';
import CodeGenerationConfirmModal from '../code-generation-confirm-modal';
import SortableTable from './SortableTable';
import styles from './index.module.less';
import arcoPalette from '@/constants/arco-palette.json';

interface DictValueItem extends DictData {
  isDelete?: boolean;
}

// 颜色分配工具
const getDefaultColors = (): string[] => {
  const lightColors = arcoPalette.light;
  return Object.values(lightColors);
};

// 根据索引获取颜色
const getColorByIndex = (index: number): string => {
  const colors = getDefaultColors();
  return colors[index % colors.length];
};

interface BatchConfigModalProps {
  visible: boolean;
  onCancel: () => void;
  onOk: (values: DictData[]) => void;
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

  // 同步表单数据
  const syncFormData = (values: DictData[]) => {
    const formData = values.map((item) => ({
      label: item.label,
      value: item.value,
      colorType: item.colorType,
      status: item.status,
      sort: item.sort
    }));
    form.setFieldsValue({ dictValues: formData });
  };

  // 获取未删除的数据
  const getVisibleDictValues = () => {
    return dictValues.filter((item) => !item.isDelete);
  };

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
      const initialDictValues = initialValues.map((item, index) => ({
        id: item.id || `temp-${Date.now()}-${index}`,
        label: item.label || '',
        value: item.value || '',
        colorType: colorMode ? item.colorType || getColorByIndex(index) : '', // 默认颜色
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
    const visibleValues = getVisibleDictValues();
    const newValue: DictData = {
      id: `temp-${Date.now()}-${visibleValues.length}`,
      label: '',
      value: '',
      colorType: colorMode ? getColorByIndex(visibleValues.length) : 'rgb(var(--primary-6))', // 根据彩色模式分配颜色
      status: 1,
      sort: visibleValues.length + 1
    };
    const newValues = [...dictValues, newValue];
    setDictValues(newValues);
    syncFormData(newValues);
  };

  // 删除字典值
  const deleteDictValue = (id: string) => {
    let newValues: DictData[];

    if (id.startsWith('temp-')) {
      // 临时数据直接删除
      newValues = dictValues.filter((item) => item.id !== id);
    } else {
      // 已保存数据标记为删除
      newValues = dictValues.map((item) => (item.id === id ? { ...item, isDelete: true } : item));
    }

    setDictValues(newValues);
    syncFormData(newValues);
  };

  // 更新字典值
  const updateDictValue = (id: string, field: keyof DictData, value: string | number) => {
    const newValues = dictValues.map((item) => (item.id === id ? { ...item, [field]: value } : item));
    setDictValues(newValues);

    // 只更新对应的表单字段，不覆盖整个表单数据
    const targetIndex = newValues.findIndex((item) => item.id === id);
    if (targetIndex !== -1) {
      form.setFieldValue(`dictValues.${targetIndex}.${field}`, value);
    }
  };

  // 处理彩色模式切换
  const handleColorModeChange = (checked: boolean) => {
    setColorMode(checked);

    if (checked) {
      // 开启彩色模式时，为所有字典值重新分配颜色
      const newValues = dictValues.map((item, index) => ({
        ...item,
        colorType: getColorByIndex(index)
      }));
      setDictValues(newValues);
      syncFormData(newValues);
    } else {
      // 关闭彩色模式时，将所有颜色重置为默认颜色
      const newValues = dictValues.map((item) => ({
        ...item,
        colorType: 'rgb(var(--primary-6))'
      }));
      setDictValues(newValues);
      syncFormData(newValues);
    }
  };

  // 一键生成编码 - 显示确认对话框
  const handleGenerateCodes = () => {
    const visibleValues = getVisibleDictValues();
    if (!canGenerate(visibleValues)) {
      Message.warning('没有需要生成编码的项');
      return;
    }
    setShowConfirmModal(true);
  };

  // 处理拖拽排序
  const handleSort = ({ oldIndex, newIndex }: { oldIndex: number; newIndex: number }) => {
    const visibleValues = getVisibleDictValues();
    const newVisibleValues = arrayMove([...visibleValues], oldIndex, newIndex);

    const updatedValues = dictValues.map((item) => {
      if (item.isDelete) return item; // 跳过已删除的数据

      const visibleIndex = newVisibleValues.findIndex((visibleItem) => visibleItem.id === item.id);
      return visibleIndex !== -1 ? { ...item, sort: visibleIndex + 1 } : item;
    });

    setDictValues(updatedValues);
    syncFormData(updatedValues);
  };

  // 确认生成编码
  const handleConfirmGenerate = async () => {
    try {
      const visibleValues = getVisibleDictValues();
      await generateCodes(visibleValues);
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
      render?: (value: unknown, record: DictData, index: number) => React.ReactNode;
    }> = [];

    // 颜色列（如果启用彩色模式）
    if (colorMode) {
      columns.push({
        title: '',
        dataIndex: 'colorType',
        width: 32,
        render: (_: unknown, record: DictData, index: number) => (
          <Form.Item field={`dictValues.${index}.colorType`} style={{ margin: 0 }}>
            <ColorPicker
              value={record.colorType}
              onChange={(color) => {
                const colorValue = typeof color === 'string' ? color : String(color);
                updateDictValue(record.id, 'colorType', colorValue);
              }}
              size="small"
              className={styles.colorPicker}
            />
          </Form.Item>
        )
      });
    }

    columns.push({
      title: (
        <>
          <span className={styles.requiredDot}>*</span>字典值
        </>
      ),
      dataIndex: 'label',
      width: 200,
      render: (_: unknown, record: DictData, index: number) => (
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

    columns.push({
      title: (
        <>
          <span className={styles.requiredDot}>*</span>字典值编码
        </>
      ),
      dataIndex: 'value',
      width: 200,
      render: (_: unknown, record: DictData, index: number) => (
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

    columns.push({
      title: '启用状态',
      dataIndex: 'status',
      width: 100,
      render: (_: unknown, record: DictData, index: number) => (
        <Form.Item field={`dictValues.${index}.status`} style={{ margin: 0 }} triggerPropName="checked">
          <Switch
            checked={record.status === 1}
            onChange={(checked) => updateDictValue(record.id, 'status', checked ? 1 : 0)}
            size="small"
          />
        </Form.Item>
      )
    });

    columns.push({
      title: '操作',
      dataIndex: 'operation',
      width: 60,
      render: (_: unknown, record: DictData) => (
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
      const formDictValues = formValues.dictValues || [];

      // 将表单数据与 dictValues 状态中的元数据合并
      const allDictValues = dictValues;
      const mergedValues = allDictValues.map((stateItem, index) => {
        const formItem = formDictValues[index];
        if (!formItem) return stateItem;

        return {
          ...stateItem,
          label: formItem.label || stateItem.label,
          value: formItem.value || stateItem.value,
          colorType: formItem.colorType || stateItem.colorType,
          status: formItem.status !== undefined ? formItem.status : stateItem.status
        };
      });

      // 过滤掉空的字典值
      const validValues = mergedValues.filter((item: DictData) => item.label.trim() && item.value.trim());

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
        <Spin loading={isGenerating} tip="正在生成编码..." style={{ width: '100%' }}>
          <div className={styles.configContainer}>
            {/* 彩色模式开关 */}
            <div className={styles.colorModeSection}>
              <span className={styles.colorModeLabel}>彩色模式</span>
              <Switch checked={colorMode} onChange={handleColorModeChange} size="small" />
            </div>

            {/* 字典值列表 */}
            <div className={styles.dictValuesSection} id="dict-config-container">
              <Form form={form}>
                <Form.List field="dictValues">
                  {() => <SortableTable data={getVisibleDictValues()} columns={createColumns()} onSort={handleSort} />}
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
