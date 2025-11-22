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

  const [inputSchema, setInputSchema] = useState<any>(null);

  useEffect(() => {
    if (editData) {
      handleGetScriptAction(editData.scriptId);
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
        rawScript: res.rawScript || ''
      });
    }
  };

  const handleSubmit = async () => {
    const id = getHashQueryParam('id');

    // 如果当前是图形模式，需要将表单数据转换回 JSON 字符串
    let inputParameterValue = form.getFieldValue('inputParameter');
    if (inputEditType === EditTypeEnum.Visual) {
      const formData = form.getFieldValue('inputParameterSchema');
      if (formData && formData.length > 0) {
        try {
          const jsonObj = formDataToJson(formData);
          inputParameterValue = JSON.stringify(jsonObj, null, 2);
        } catch (e) {
          Message.error('表单数据转换失败，请检查字段配置');
          return;
        }
      } else {
        inputParameterValue = '{}';
      }
    }

    if (isEdit && editData) {
      const req: UpdateScriptActionReq = {
        scriptId: editData.scriptId,
        scriptName: form.getFieldValue('scriptName'),
        description: form.getFieldValue('description'),
        inputParameter: inputParameterValue || '',
        outputParameter: form.getFieldValue('outputParameter') || '',
        rawScript: form.getFieldValue('rawScript') || ''
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
        inputParameter: form.getFieldValue('inputParameter'),
        outputParameter: form.getFieldValue('outputParameter'),
        rawScript: form.getFieldValue('rawScript')
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
      setInputEditType(EditTypeEnum.Visual);
      // 将 JSON 字符串转换为 schema 并初始化表单数据
      if (inputParameter) {
        try {
          const schema = jsonToJsonSchema(inputParameter);
          setInputSchema(schema);
          // 初始化表单数据
          const formData = schemaToFormData(schema);
          form.setFieldValue('inputParameterSchema', formData);
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
        form.setFieldValue('inputParameterSchema', []);
      }
    } else {
      setInputEditType(EditTypeEnum.Json);
      // 将表单数据转换回 JSON 字符串
      const formData = form.getFieldValue('inputParameterSchema');
      if (formData && formData.length > 0) {
        const jsonObj = formDataToJson(formData);
        form.setFieldValue('inputParameter', JSON.stringify(jsonObj, null, 2));
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
        <Row>
          <Form.Item field="inputParameter">
            {inputEditType === EditTypeEnum.Json ? (
              <Input.TextArea placeholder="请输入动作入参（JSON格式）" rows={6} />
            ) : (
              <div style={{ display: 'none' }} />
            )}
          </Form.Item>
        </Row>
        {inputEditType === EditTypeEnum.Visual && inputSchema && (
          <Row>
            <Form.Item field="inputParameterSchema" label="字段配置">
              <SchemaForm form={form} schema={inputSchema} fieldPrefix="inputParameterSchema" level={0} />
            </Form.Item>
          </Row>
        )}

        <Row>
          <div className={styles.title}>设置动作JS</div>
        </Row>

        <Row>
          <Form.Item field="rawScript">
            <CodeMirror
              height="500px"
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
              onChange={(checked) => setOutputEditType(checked ? EditTypeEnum.Visual : EditTypeEnum.Json)}
            ></Switch>
          </Col>
        </Row>

        <Row>
          <Form.Item field="outputParameter">
            {outputEditType === 'json' ? (
              <Input.TextArea placeholder="请输入动作出参" />
            ) : (
              <div style={{ display: 'none' }} />
            )}
          </Form.Item>
        </Row>
        <Row>
          <Col span={2}>
            <Button type="primary" onClick={handleSubmit}>
              {isEdit ? '更新' : '保存'}
            </Button>
          </Col>
          <Col span={2}>
            <Button onClick={handleCancel}>取消</Button>
          </Col>
        </Row>
      </Form>
    </div>
  );
};

export default CreateScriptActionPage;
