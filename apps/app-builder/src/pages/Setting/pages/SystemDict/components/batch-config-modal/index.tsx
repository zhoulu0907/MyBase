import React, { useEffect, useState } from 'react';
import { Button, Form, Message, Modal, Switch, Input, ColorPicker, Spin } from '@arco-design/web-react';
import { IconDelete, IconDragDotVertical, IconTranslate, IconPlusCircle } from '@arco-design/web-react/icon';
import { SortableHandle } from 'react-sortable-hoc';
import type { DictData } from '@onebase/platform-center';
import { useCodeGenerator, type DictValueItem } from '../../hooks/useCodeGenerator';
import CodeGenerationConfirmModal from '../code-generation-confirm-modal';
import styles from './index.module.less';

// 拖拽手柄组件
const DragHandle = SortableHandle(() => <IconDragDotVertical className={styles.dragHandle} />);

// 可排序的表格行组件（暂时不使用）
// const SortableTableRow = SortableElement(
//   ({ children, ...props }: { children: React.ReactNode; [key: string]: unknown }) => {
//     return <tr {...props}>{children}</tr>;
//   }
// );

// 可排序的表格体组件（暂时不使用）
// const SortableTableBody = SortableContainer((props: { children: React.ReactNode; [key: string]: unknown }) => {
//   return <tbody {...props} />;
// });

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
        color: '#1890ff', // 默认颜色
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
      color: '#1890ff',
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

  // 处理拖拽排序（暂时不使用）
  // const handleSort = ({ oldIndex, newIndex }: { oldIndex: number; newIndex: number }) => {
  //   const newValues = arrayMove([...dictValues], oldIndex, newIndex);
  //   // 更新排序
  //   const updatedValues = newValues.map((item, index) => ({
  //     ...item,
  //     sort: index + 1
  //   }));
  //   setDictValues(updatedValues);
  //   form.setFieldsValue({ dictValues: updatedValues });
  // };

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
                  {() => (
                    <div className={styles.dictTableContainer}>
                      <table className={styles.dictTable}>
                        <thead>
                          <tr>
                            <th style={{ width: 40 }}></th>
                            {colorMode && <th style={{ width: 32 }}></th>}
                            <th style={{ width: 200 }}>
                              <span className={styles.requiredDot}>*</span>字典值
                            </th>
                            <th style={{ width: 200 }}>
                              <span className={styles.requiredDot}>*</span>字典值编码
                            </th>
                            <th style={{ width: 100 }}>启用状态</th>
                            <th style={{ width: 60 }}>操作</th>
                          </tr>
                        </thead>
                        <tbody>
                          {dictValues.map((item, index) => (
                            <tr key={item.id}>
                              <td>
                                <div className="arco-table-cell">
                                  <DragHandle />
                                </div>
                              </td>
                              {colorMode && (
                                <td>
                                  <div className="arco-table-cell">
                                    <ColorPicker
                                      value={item.color}
                                      onChange={(color) =>
                                        updateDictValue(
                                          item.id,
                                          'color',
                                          typeof color === 'string' ? color : (color as any).hex || color
                                        )
                                      }
                                      size="small"
                                      className={styles.colorPicker}
                                    />
                                  </div>
                                </td>
                              )}
                              <td>
                                <div className="arco-table-cell">
                                  <Form.Item
                                    field={`dictValues.${index}.label`}
                                    rules={[{ required: true, message: '请输入字典值' }]}
                                    style={{ margin: 0 }}
                                  >
                                    <Input
                                      placeholder="请输入字典值"
                                      value={item.label}
                                      onChange={(value) => updateDictValue(item.id, 'label', value)}
                                    />
                                  </Form.Item>
                                </div>
                              </td>
                              <td>
                                <div className="arco-table-cell">
                                  <Form.Item
                                    field={`dictValues.${index}.value`}
                                    rules={[{ required: true, message: '请输入字典值编码' }]}
                                    style={{ margin: 0 }}
                                  >
                                    <Input
                                      placeholder="请输入字典值编码"
                                      value={item.value}
                                      onChange={(value) => updateDictValue(item.id, 'value', value)}
                                    />
                                  </Form.Item>
                                </div>
                              </td>
                              <td>
                                <div className="arco-table-cell">
                                  <Switch
                                    checked={item.status === 1}
                                    onChange={(checked) => updateDictValue(item.id, 'status', checked ? 1 : 0)}
                                    size="small"
                                  />
                                </div>
                              </td>
                              <td>
                                <div className="arco-table-cell">
                                  <Button
                                    type="text"
                                    status="danger"
                                    icon={<IconDelete />}
                                    onClick={() => deleteDictValue(item.id)}
                                    size="small"
                                  />
                                </div>
                              </td>
                            </tr>
                          ))}
                        </tbody>
                      </table>
                    </div>
                  )}
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
