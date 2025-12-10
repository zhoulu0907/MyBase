import React, { useEffect, useState, useCallback, useMemo } from 'react';
import { Button, Form, Message, Modal, Switch, Input, ColorPicker, Spin } from '@arco-design/web-react';
import { IconDelete, IconTranslate, IconPlusCircle } from '@arco-design/web-react/icon';
import { arrayMove } from '@/pages/CreateApp/pages/DataFactory/pages/Entity/components/Modals/ConfigFieldModal/utils/transform';
import { getDictDataListByType, StatusEnum, type DictData } from '@onebase/platform-center';
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
  dictType: string;
}

const BatchConfigModal: React.FC<BatchConfigModalProps> = ({ visible, onCancel, onOk, loading = false, dictType }) => {
  const [form] = Form.useForm();
  const [colorMode, setColorMode] = useState(false);
  const [dictValues, setDictValues] = useState<DictValueItem[]>([]);
  const [showConfirmModal, setShowConfirmModal] = useState(false);
  const [tableLoading, setTableLoading] = useState(true);
  const [externalErrors, setExternalErrors] = useState<Record<string, string>>({});

  // 合并表单数据和状态数据
  const mergeFormDataWithState = useCallback(
    (formDictValues?: DictData[]): DictValueItem[] => {
      const formValues = formDictValues || form.getFieldsValue().dictValues || [];
      const formDataMap = new Map(formValues.map((item: DictData) => [item.id, item]));

      return dictValues.map((stateItem) => {
        const formItem = formDataMap.get(stateItem.id);
        if (formItem) {
          return {
            ...stateItem,
            ...formItem,
            id: stateItem.id,
            isDelete: stateItem.isDelete
          };
        }
        return stateItem;
      });
    },
    [form, dictValues]
  );

  // 获取可见的数据
  const getVisibleItems = useCallback(
    (items?: DictValueItem[]): DictValueItem[] => {
      const targetItems = items || mergeFormDataWithState();
      return targetItems.filter((item) => !item.isDelete);
    },
    [mergeFormDataWithState]
  );

  // 获取删除的数据
  const getDeletedItems = useCallback(
    (items?: DictValueItem[]): DictValueItem[] => {
      const targetItems = items || mergeFormDataWithState();
      return targetItems.filter((item) => item.isDelete);
    },
    [mergeFormDataWithState]
  );

  const activeDictValues = useMemo(() => {
    return getVisibleItems(dictValues);
  }, [dictValues, getVisibleItems]);

  const updateDictValuesAndForm = useCallback(
    (newValues: DictValueItem[]) => {
      setDictValues(newValues);
      form.setFieldsValue({ dictValues: getVisibleItems(newValues) });
    },
    [form]
  );

  const clearErrors = useCallback(() => {
    setExternalErrors({});
  }, []);

  const loadAllDictDataList = useCallback(async () => {
    try {
      setTableLoading(true);
      const res = await getDictDataListByType(dictType);
      let colorEnabled = false;
      if (res.some((item) => item?.colorType?.startsWith('#'))) {
        colorEnabled = true;
      }
      setColorMode(colorEnabled);
      setDictValues(res);
      form.setFieldsValue({ dictValues: res });
    } catch (error) {
      console.error('加载字典数据失败:', error);
    } finally {
      setTimeout(() => {
        setTableLoading(false);
      }, 500);
    }
  }, [dictType, form]);

  // 使用编码生成hook
  const { isGenerating, generateCodes, canGenerate } = useCodeGenerator({
    onSuccess: (generatedItems) => {
      const deletedItems = getDeletedItems();
      const newValues = [...generatedItems, ...deletedItems];
      updateDictValuesAndForm(newValues);
      Message.success('编码生成成功');
    },
    onError: (error) => {
      Message.error(`编码生成失败: ${error.message}`);
    }
  });

  useEffect(() => {
    if (visible) {
      loadAllDictDataList();
    } else {
      // 关闭时重置
      form.resetFields();
      setDictValues([]);
      clearErrors();
    }
  }, [visible]);

  // 新增字典值
  const addDictValue = () => {
    const mergedDictValues = mergeFormDataWithState();

    if (mergedDictValues.length >= 100) {
      Message.error('字典值数量不能超过100');
      return;
    }

    const newValue: DictValueItem = {
      id: `temp-${Date.now()}-${dictValues.length}`,
      label: '',
      value: '',
      colorType: colorMode ? getColorByIndex(dictValues.length) : '',
      status: StatusEnum.ENABLE,
      sort: dictValues.length + 1,
      isDelete: false
    };
    const newValues = [...mergedDictValues, newValue];
    updateDictValuesAndForm(newValues);
    clearErrors();
  };

  // 删除字典值
  const deleteDictValue = (id: string) => {
    const mergedDictValues = mergeFormDataWithState();
    const newValues = id.startsWith('temp-')
      ? mergedDictValues.filter((item) => item.id !== id)
      : mergedDictValues.map((item) => (item.id === id ? { ...item, isDelete: true } : item));
    updateDictValuesAndForm(newValues);
    clearErrors();
  };

  // 处理彩色模式切换
  const handleColorModeChange = (checked: boolean) => {
    setColorMode(checked);
    const mergedDictValues = mergeFormDataWithState();
    const visibleItems = getVisibleItems();
    const visibleItemIdMap = new Map(visibleItems.map((item, index) => [item.id, index]));

    const newValues = mergedDictValues.map((item) => {
      if (item.isDelete) return item;
      return {
        ...item,
        colorType: checked && visibleItemIdMap.has(item.id) ? getColorByIndex(visibleItemIdMap.get(item.id)!) : ''
      };
    });

    updateDictValuesAndForm(newValues);
  };

  // 一键生成编码 - 显示确认对话框
  const handleGenerateCodes = () => {
    const visibleValues = getVisibleItems();
    if (!canGenerate(visibleValues)) {
      Message.warning('没有需要生成编码的项');
      return;
    }
    setShowConfirmModal(true);
  };

  // 处理拖拽排序
  const handleSort = ({ oldIndex, newIndex }: { oldIndex: number; newIndex: number }) => {
    const mergedDictValues = mergeFormDataWithState();
    const visibleValues = getVisibleItems(mergedDictValues);
    const newVisibleValues = arrayMove([...visibleValues], oldIndex, newIndex) as DictValueItem[];
    const sortableValues = newVisibleValues.map((item: DictValueItem, index: number) => ({
      ...item,
      sort: index + 1
    }));

    const deletedItems = getDeletedItems(mergedDictValues);
    const newValues = [...sortableValues, ...deletedItems];
    updateDictValuesAndForm(newValues);
  };

  // 确认生成编码
  const handleConfirmGenerate = async () => {
    try {
      const mergedDictValues = mergeFormDataWithState();
      const visibleValues = getVisibleItems(mergedDictValues);
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
              size="small"
              className={styles.colorPicker}
              showPreset
              presetColors={Object.values(arcoPalette.light)}
            />
          </Form.Item>
        )
      });
    }

    columns.push({
      title: '',
      dataIndex: 'id',
      width: 1,
      render: (_: unknown, record: DictData, index: number) => (
        <Form.Item field={`dictValues.${index}.id`} noStyle>
          <Input value={record.id} style={{ display: 'none' }} readOnly />
        </Form.Item>
      )
    });

    columns.push({
      title: (
        <>
          <span className={styles.requiredDot}>*</span>字典值
        </>
      ),
      dataIndex: 'label',
      width: 200,
      render: (_: unknown, record: DictData, index: number) => {
        const fieldName = `dictValues.${index}.label`;
        return (
          <Form.Item
            field={fieldName}
            rules={[{ required: true, message: '请输入字典值', validateTrigger: ['onChange', 'onBlur'] }]}
            style={{ margin: 0 }}
            validateStatus={externalErrors[fieldName] ? 'error' : undefined}
            help={externalErrors[fieldName]}
          >
            <Input placeholder="请输入字典值" value={record.label} />
          </Form.Item>
        );
      }
    });

    columns.push({
      title: (
        <>
          <span className={styles.requiredDot}>*</span>字典值编码
        </>
      ),
      dataIndex: 'value',
      width: 250,
      render: (_: unknown, record: DictData, index: number) => {
        const fieldName = `dictValues.${index}.value`;
        return (
          <Form.Item
            field={fieldName}
            rules={[{ required: true, message: '请输入字典值编码' }]}
            style={{ margin: 0 }}
            validateTrigger={['onChange', 'onBlur']}
            validateStatus={externalErrors[fieldName] ? 'error' : undefined}
            help={externalErrors[fieldName]}
          >
            <Input placeholder="请输入字母、数字或下划线" value={record.value} />
          </Form.Item>
        );
      }
    });

    columns.push({
      title: '启用状态',
      dataIndex: 'status',
      width: 100,
      render: (_: unknown, _record: DictData, index: number) => (
        <Form.Item
          field={`dictValues.${index}.status`}
          style={{ margin: 0 }}
          triggerPropName="checked"
          normalize={(v) => (v ? StatusEnum.ENABLE : StatusEnum.DISABLE)}
          formatter={(v) => v === StatusEnum.ENABLE || v === true}
        >
          <Switch size="small" />
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
      const mergedValues = mergeFormDataWithState(formValues.dictValues);
      onOk(mergedValues);
    } catch (error) {
      // 手动渲染表单错误
      const errs = (error && (error as Record<string, unknown>).errors) || {};

      if (typeof errs === 'object') {
        const map: Record<string, string> = {};
        Object.keys(errs).forEach((key: string) => {
          if (key) map[key] = (errs as Record<string, { message?: string }>)[key]?.message || '校验失败';
        });
        setExternalErrors(map);
      }
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
        maskClosable={false}
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
              <Spin loading={tableLoading} tip="正在加载字典值...">
                <Form form={form} initialValues={{ dictValues: activeDictValues }}>
                  <Form.List field="dictValues">
                    {() => <SortableTable data={activeDictValues} columns={createColumns()} onSort={handleSort} />}
                  </Form.List>
                </Form>
              </Spin>
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
                disabled={isGenerating}
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
