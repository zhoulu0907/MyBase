import { Button, Form, Grid, Input, Message, Tabs } from '@arco-design/web-react';
import {
  createScriptAction,
  getScriptAction,
  updateScriptAction,
  type CreateScriptActionReq,
  type ScriptActionItem,
  type UpdateScriptActionReq
} from '@onebase/app';
import { getHashQueryParam } from '@onebase/common';
import React, { useEffect, useState } from 'react';
import styles from './index.module.less';

import { javascript } from '@codemirror/lang-javascript';
import CodeMirror, { EditorView } from '@uiw/react-codemirror';
import SchemaForm from './SchemaForm';
import { jsonToJsonSchema } from './util';

const Row = Grid.Row;
const Col = Grid.Col;
const TabPane = Tabs.TabPane;

export interface CreateScriptActionPageProps {
  onSuccess: Function;
  editData?: ScriptActionItem;
}

// 声明 Visual 和 JSON 两种编辑类型的枚举
export enum EditTypeEnum {
  Visual = 'visual',
  Json = 'json'
}

const CreateScriptActionPage: React.FC<CreateScriptActionPageProps> = ({ onSuccess, editData }) => {
  const [inputEditType, setInputEditType] = useState<EditTypeEnum>(EditTypeEnum.Visual);
  const [outputEditType, setOutputEditType] = useState<EditTypeEnum>(EditTypeEnum.Visual);

  const [form] = Form.useForm();
  const isEdit = !!editData;

  const rawScript = Form.useWatch('rawScript', form);
  const inputParameter = Form.useWatch('inputParameter', form);
  const outputParameter = Form.useWatch('outputParameter', form);

  const [inputSchema, setInputSchema] = useState<any>([]);
  const [outputSchema, setOutputSchema] = useState<any>([]);

  // 缓存文本模式下的 JSON 值
  const [cachedInputParameter, setCachedInputParameter] = useState<string>('');
  const [cachedOutputParameter, setCachedOutputParameter] = useState<string>('');

  useEffect(() => {
    if (editData) {
      handleGetScriptAction(editData.id);
    }
  }, [editData, form]);

  const handleGetScriptAction = async (scriptId: string) => {
    const res = await getScriptAction(scriptId);
    console.log(res);
    if (res) {
      form.setFieldsValue({
        scriptName: res.scriptName,
        description: res.description || '',
        inputParameter: res.inputParameter ? JSON.stringify(res.inputParameter, null, 2) : '',
        outputParameter: res.outputParameter ? JSON.stringify(res.outputParameter, null, 2) : '',
        rawScript: res.rawScript || '',
        inputSchema: res.inputSchema || [],
        outputSchema: res.outputSchema || []
      });
      setInputSchema(res.inputSchema || []);
      setOutputSchema(res.outputSchema || []);
    }
  };

  const handleSubmit = async () => {
    try {
      await form.validate();
    } catch (error) {
      console.error('表单验证失败:', error);
    }

    const id = getHashQueryParam('id');

    // 如果当前是图形模式，需要将表单数据转换回 JSON 字符串
    let inputParameterValue = form.getFieldValue('inputParameter');
    let inputSchemaValue = form.getFieldValue('inputSchema');
    if (inputEditType === EditTypeEnum.Visual) {
      const formData = form.getFieldValue('inputSchema');
      if (formData && formData.length > 0) {
        try {
          const jsonObj = formDataToJson(formData);
          inputParameterValue = JSON.stringify(jsonObj, null, 2);
          inputSchemaValue = formData;
        } catch (e) {
          Message.error('表单数据转换失败，请检查字段配置');
          return;
        }
      } else {
        inputParameterValue = '{}';
        inputSchemaValue = [];
      }
    }

    // 处理输出参数
    let outputParameterValue = form.getFieldValue('outputParameter');
    let outputSchemaValue = form.getFieldValue('outputSchema');
    if (outputEditType === EditTypeEnum.Visual) {
      const formData = form.getFieldValue('outputSchema');
      if (formData && formData.length > 0) {
        try {
          const jsonObj = formDataToJson(formData);
          outputParameterValue = JSON.stringify(jsonObj, null, 2);
          outputSchemaValue = formData;
        } catch (e) {
          Message.error('输出参数表单数据转换失败，请检查字段配置');
          return;
        }
      } else {
        outputParameterValue = '{}';
        outputSchemaValue = [];
      }
    }

    if (isEdit && editData) {
      const req: UpdateScriptActionReq = {
        id: editData.id,
        scriptName: form.getFieldValue('scriptName'),
        description: form.getFieldValue('description'),
        inputParameter: inputParameterValue || '',
        outputParameter: outputParameterValue || '',
        rawScript: form.getFieldValue('rawScript') || '',
        inputSchema: inputSchemaValue,
        outputSchema: outputSchemaValue
      };

      const res = await updateScriptAction(req);
      console.log(res);
      if (res) {
        Message.success('更新成功');
        onSuccess();
      }
    } else {
      const req: CreateScriptActionReq = {
        connectorId: id || '',
        scriptName: form.getFieldValue('scriptName'),
        description: form.getFieldValue('description'),
        inputParameter: inputParameterValue || '',
        outputParameter: outputParameterValue || '',
        rawScript: form.getFieldValue('rawScript'),
        inputSchema: inputSchemaValue,
        outputSchema: outputSchemaValue
      };

      const res = await createScriptAction(req);
      console.log(res);
      if (res) {
        Message.success('创建成功');
        onSuccess();
      }
    }
  };

  const handleCancel = () => {
    onSuccess();
  };

  const customExtensions = [
    javascript(),
    EditorView.theme({
      '&.cm-editor.cm-focused': {
        outline: '0 solid transparent'
      }
      //   '&.cm-editor': {
      //     backgroundColor: '#282c34',
      //     color: '#fff'
      //   },
      //   '&.cm-scroller': {
      //     backgroundColor: '#282c34'
      //   },
      //   '&.cm-content': {
      //     backgroundColor: '#282c34',
      //     color: '#fff'
      //   },
      //   '&.cm-gutters': {
      //     backgroundColor: '#282c34',
      //     borderRight: '1px solid #3e4451'
      //   },
      //   '&.cm-line': {
      //     color: '#abb2bf'
      //   }
    }),

    EditorView.lineWrapping
  ];

  const handleInputParameterChange = (checked: boolean) => {
    if (checked) {
      // 切换到图形模式：缓存当前的文本值
      const currentTextValue = form.getFieldValue('inputParameter') || '';
      setCachedInputParameter(currentTextValue);

      setInputEditType(EditTypeEnum.Visual);

      // 优先使用已保存的 schema（在编辑模式下）
      const savedSchema = form.getFieldValue('inputSchema');

      // 如果已有保存的表单数据，直接使用
      if (savedSchema && savedSchema.length > 0) {
        setInputSchema(savedSchema);
      } else if (inputParameter) {
        // 如果没有保存的表单数据，从 JSON 字符串转换
        try {
          const schema = jsonToJsonSchema(inputParameter);
          console.log('schema :', schema);
          setInputSchema(schema);
          // 初始化表单数据，传入原始 JSON 对象以保留值
          form.setFieldValue('inputSchema', schema);
        } catch (e) {
          Message.error('无效的 JSON 字符串，无法转换为图形化表单');
          setInputEditType(EditTypeEnum.Json);
        }
      } else {
        // 如果没有输入参数，创建一个空的 schema
        setInputSchema([]);
        form.setFieldValue('inputSchema', []);
      }
    } else {
      // 切回文本模式：将表单数据转换回 JSON 字符串
      setInputEditType(EditTypeEnum.Json);
      const formData = form.getFieldValue('inputSchema');
      if (formData && formData.length > 0) {
        try {
          const jsonObj = formDataToJson(formData);
          const jsonString = JSON.stringify(jsonObj, null, 2);
          form.setFieldValue('inputParameter', jsonString);
        } catch (e) {
          // 如果转换失败，恢复缓存的文本值
          Message.warning('表单数据转换失败，已恢复之前的文本内容');
          form.setFieldValue('inputParameter', cachedInputParameter);
        }
      } else {
        // 如果表单数据为空，恢复缓存的文本值
        form.setFieldValue('inputParameter', cachedInputParameter || '{}');
      }
    }
  };

  const handleOutputParameterChange = (checked: boolean) => {
    if (checked) {
      // 切换到图形模式：缓存当前的文本值
      const currentTextValue = form.getFieldValue('outputParameter') || '';
      setCachedOutputParameter(currentTextValue);

      setOutputEditType(EditTypeEnum.Visual);

      // 优先使用已保存的 schema（在编辑模式下）
      const savedSchema = form.getFieldValue('outputSchema');

      // 如果已有保存的表单数据，直接使用 不需要操作
      if (savedSchema && savedSchema.length > 0) {
        // 使用保存的 schema
        setOutputSchema(savedSchema);
      } else if (outputParameter) {
        // 如果没有保存的表单数据，从 JSON 字符串转换
        try {
          const schema = jsonToJsonSchema(outputParameter);
          setOutputSchema(schema);
          // 初始化表单数据，传入原始 JSON 对象以保留值
          form.setFieldValue('outputSchema', schema);
        } catch (e) {
          Message.error('无效的 JSON 字符串，无法转换为图形化表单');
          setOutputEditType(EditTypeEnum.Json);
        }
      } else {
        // 如果没有输入参数，创建一个空的 schema
        setOutputSchema([]);
        form.setFieldValue('outputSchema', []);
      }
    } else {
      // 切回文本模式：将表单数据转换回 JSON 字符串
      setOutputEditType(EditTypeEnum.Json);
      const formData = form.getFieldValue('outputSchema');
      if (formData && formData.length > 0) {
        try {
          const jsonObj = formDataToJson(formData);
          const jsonString = JSON.stringify(jsonObj, null, 2);
          form.setFieldValue('outputParameter', jsonString);
        } catch (e) {
          // 如果转换失败，恢复缓存的文本值
          Message.warning('表单数据转换失败，已恢复之前的文本内容');
          form.setFieldValue('outputParameter', cachedOutputParameter);
        }
      } else {
        // 如果表单数据为空，恢复缓存的文本值
        form.setFieldValue('outputParameter', cachedOutputParameter || '{}');
      }
    }
  };

  // 将表单数据转换回 JSON 对象
  const formDataToJson = (formData: any[]): any => {
    if (!formData || formData.length === 0) {
      return {};
    }

    const result: any = {};
    formData.forEach((item) => {
      if (item.name) {
        const type = item.type || 'string';
        if (type === 'object') {
          // 对象类型：递归处理子字段
          if (item.children && item.children.length > 0) {
            result[item.name] = formDataToJson(item.children);
          } else {
            result[item.name] = {};
          }
        } else if (type === 'array') {
          // 数组类型：处理数组项
          if (item.children && item.children.length > 0) {
            result[item.name] = item.children.map((child: any) => {
              const childType = child.type || 'string';
              if (childType === 'object' && child.children) {
                return formDataToJson(child.children);
              } else if (childType === 'array' && child.children) {
                return child.children.map((subChild: any) => {
                  if (subChild.type === 'object' && subChild.children) {
                    return formDataToJson(subChild.children);
                  }
                  return subChild.value ?? null;
                });
              }
              return child.value ?? null;
            });
          } else {
            result[item.name] = [];
          }
        } else {
          // 基本类型
          result[item.name] = item.value ?? null;
        }
      }
    });
    return result;
  };

  return (
    <div className={styles.createScriptActionPage}>
      <Form layout="vertical" form={form}>
        <Row>
          <Form.Item
            label="动作名称"
            field="scriptName"
            required
            rules={[{ required: true, message: '请输入动作名称' }]}
          >
            <Input placeholder="请输入动作名称" />
          </Form.Item>
        </Row>
        <Row>
          <Form.Item label="描述" field="description">
            <Input.TextArea placeholder="请输入动作描述" />
          </Form.Item>
        </Row>
        <Row>
          <Tabs defaultActiveTab="actionInput" style={{ width: '100%' }}>
            <TabPane key="actionInput" title="动作入参">
              {inputEditType === EditTypeEnum.Json && (
                <Row>
                  <Form.Item field="inputParameter">
                    <Input.TextArea placeholder="请输入动作入参（JSON格式）" rows={4} />
                  </Form.Item>
                </Row>
              )}
              {inputEditType === EditTypeEnum.Visual && inputSchema && (
                <Row>
                  <Form.Item field="inputSchema">
                    <SchemaForm form={form} schema={inputSchema} fieldPrefix="inputSchema" />
                  </Form.Item>
                </Row>
              )}
            </TabPane>

            <TabPane key="actionScript" title="设置动作JS">
              <Row>
                <Form.Item field="rawScript">
                  <CodeMirror
                    height="400px"
                    className={styles.editor}
                    extensions={customExtensions}
                    value={rawScript}
                    onChange={(value) => form.setFieldsValue({ rawScript: value })}
                  ></CodeMirror>
                </Form.Item>
              </Row>
            </TabPane>

            <TabPane key="actionOutput" title="动作出参">
              {outputEditType === EditTypeEnum.Json && (
                <Row>
                  <Form.Item field="outputParameter">
                    <Input.TextArea placeholder="请输入动作出参（JSON格式）" rows={4} />
                  </Form.Item>
                </Row>
              )}
              {outputEditType === EditTypeEnum.Visual && outputSchema && (
                <Row>
                  <Form.Item field="outputSchema">
                    <SchemaForm form={form} schema={outputSchema} fieldPrefix="outputSchema" />
                  </Form.Item>
                </Row>
              )}
            </TabPane>
          </Tabs>
        </Row>

        <Row gutter={16}>
          <Col span={1.5}>
            <Button type="primary" onClick={handleSubmit}>
              {isEdit ? '更新' : '保存'}
            </Button>
          </Col>
          <Col span={1.5}>
            <Button onClick={handleCancel}>取消</Button>
          </Col>
        </Row>
      </Form>
    </div>
  );
};

export default CreateScriptActionPage;
