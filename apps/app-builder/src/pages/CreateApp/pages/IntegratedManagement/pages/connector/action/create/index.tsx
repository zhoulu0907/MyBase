import { Button, Form, Grid, Input, Message, Switch } from '@arco-design/web-react';
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
import { jsonToJsonSchema, schemaToFormData } from './util';

const Row = Grid.Row;
const Col = Grid.Col;

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
  const [inputEditType, setInputEditType] = useState<EditTypeEnum>(EditTypeEnum.Json);
  const [outputEditType, setOutputEditType] = useState<EditTypeEnum>(EditTypeEnum.Json);

  const [form] = Form.useForm();
  const isEdit = !!editData;

  const rawScript = Form.useWatch('rawScript', form);
  const inputParameter = Form.useWatch('inputParameter', form);
  const outputParameter = Form.useWatch('outputParameter', form);

  const [inputSchema, setInputSchema] = useState<any>({});
  const [outputSchema, setOutputSchema] = useState<any>({});

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
        inputParameter: res.inputParameter || '',
        outputParameter: res.outputParameter || '',
        rawScript: res.rawScript || '',
        inputSchema: res.inputSchema || [],
        outputSchema: res.outputSchema || []
      });
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
      const savedFormData = Array.isArray(savedSchema) ? savedSchema : null;

      // 如果已有保存的表单数据，直接使用
      if (savedFormData && savedFormData.length > 0) {
        // 使用保存的 schema（从表单数据中恢复）
        // schema 信息已包含在 formData 的每个字段的 schema 属性中
        // 需要重建 schema 对象
        const existingSchema = inputSchema || {
          $schema: 'http://json-schema.org/draft-07/schema#',
          type: 'object',
          properties: {}
        };
        setInputSchema(existingSchema);
        form.setFieldValue('inputSchema', savedFormData);
      } else if (inputParameter) {
        // 如果没有保存的表单数据，从 JSON 字符串转换
        try {
          const jsonObj = JSON.parse(inputParameter);
          const schema = jsonToJsonSchema(inputParameter);
          console.log('schema :', schema);
          setInputSchema(schema);
          // 初始化表单数据，传入原始 JSON 对象以保留值
          const formData = schemaToFormData(schema, jsonObj);
          form.setFieldValue('inputSchema', formData);
        } catch (e) {
          Message.error('无效的 JSON 字符串，无法转换为图形化表单');
          setInputEditType(EditTypeEnum.Json);
        }
      } else {
        // 如果没有输入参数，创建一个空的 object schema
        const emptySchema = {
          $schema: 'http://json-schema.org/draft-07/schema#',
          type: 'object',
          properties: {}
        };
        setInputSchema(emptySchema);
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
      const savedFormData = Array.isArray(savedSchema) ? savedSchema : null;

      // 如果已有保存的表单数据，直接使用
      if (savedFormData && savedFormData.length > 0) {
        // 使用保存的 schema
        const existingSchema = outputSchema || {
          $schema: 'http://json-schema.org/draft-07/schema#',
          type: 'object',
          properties: {}
        };
        setOutputSchema(existingSchema);
        form.setFieldValue('outputSchema', savedFormData);
      } else if (outputParameter) {
        // 如果没有保存的表单数据，从 JSON 字符串转换
        try {
          const jsonObj = JSON.parse(outputParameter);
          const schema = jsonToJsonSchema(outputParameter);
          setOutputSchema(schema);
          // 初始化表单数据，传入原始 JSON 对象以保留值
          const formData = schemaToFormData(schema, jsonObj);
          form.setFieldValue('outputSchema', formData);
        } catch (e) {
          Message.error('无效的 JSON 字符串，无法转换为图形化表单');
          setOutputEditType(EditTypeEnum.Json);
        }
      } else {
        // 如果没有输入参数，创建一个空的 object schema
        const emptySchema = {
          $schema: 'http://json-schema.org/draft-07/schema#',
          type: 'object',
          properties: {}
        };
        setOutputSchema(emptySchema);
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
          <Col span={22}>
            <div className={styles.title}>动作入参</div>
          </Col>
          <Col span={2}>
            <Switch
              checkedText="图形"
              uncheckedText="文本"
              checked={inputEditType === EditTypeEnum.Visual}
              onChange={handleInputParameterChange}
            ></Switch>
          </Col>
        </Row>

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
              <SchemaForm form={form} schema={inputSchema} fieldPrefix="inputSchema" level={0} />
            </Form.Item>
          </Row>
        )}

        <Row>
          <div className={styles.title}>设置动作JS</div>
        </Row>

        <Row>
          <Form.Item field="rawScript">
            <CodeMirror
              height="100px"
              className={styles.editor}
              extensions={customExtensions}
              value={rawScript}
              onChange={(value) => form.setFieldsValue({ rawScript: value })}
            ></CodeMirror>
          </Form.Item>
        </Row>

        <Row>
          <Col span={22}>
            <div className={styles.title}>动作出参</div>
          </Col>
          <Col span={2}>
            <Switch
              checkedText="图形"
              uncheckedText="文本"
              checked={outputEditType === EditTypeEnum.Visual}
              onChange={handleOutputParameterChange}
            ></Switch>
          </Col>
        </Row>

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
              <SchemaForm form={form} schema={outputSchema} fieldPrefix="outputSchema" level={0} />
            </Form.Item>
          </Row>
        )}
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
