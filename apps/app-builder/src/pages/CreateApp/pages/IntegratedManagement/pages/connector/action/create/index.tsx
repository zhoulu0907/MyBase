import { Button, Form, Grid, Input, Message } from '@arco-design/web-react';
import {
  createScriptAction,
  getScriptAction,
  updateScriptAction,
  type CreateScriptActionReq,
  type ScriptActionItem,
  type UpdateScriptActionReq
} from '@onebase/app';
import { getHashQueryParam } from '@onebase/common';
import React, { useEffect } from 'react';
import styles from './index.module.less';

import CodeMirror, { EditorView } from '@uiw/react-codemirror';

const Row = Grid.Row;
const Col = Grid.Col;

// 定义页面 Props 类型（目前预留扩展点）
export interface CreateScriptActionPageProps {
  onSuccess: Function;
  editData?: ScriptActionItem;
}

const CreateScriptActionPage: React.FC<CreateScriptActionPageProps> = ({ onSuccess, editData }) => {
  const [form] = Form.useForm();
  const isEdit = !!editData;

  const rawScript = Form.useWatch('rawScript', form);

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

    if (isEdit && editData) {
      const req: UpdateScriptActionReq = {
        scriptId: editData.scriptId,
        scriptName: form.getFieldValue('scriptName'),
        description: form.getFieldValue('description'),
        inputParameter: form.getFieldValue('inputParameter') || '',
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
    EditorView.theme({
      '&.cm-editor.cm-focused': {
        outline: '0 solid transparent'
      }
    }),

    EditorView.lineWrapping
  ];

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
          <div className={styles.title}>动作入参</div>
        </Row>
        <Row>
          <Form.Item field="inputParameter">
            <Input placeholder="请输入动作入参" />
          </Form.Item>
        </Row>

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
          <div className={styles.title}>动作出参</div>
        </Row>

        <Row>
          <Form.Item field="outputParameter">
            <Input placeholder="请输入动作出参" />
          </Form.Item>
        </Row>
        <Row>
          <Col span={1}>
            <Button type="primary" onClick={handleSubmit}>
              {isEdit ? '更新' : '保存'}
            </Button>
          </Col>
          <Col span={1}>
            <Button onClick={handleCancel}>取消</Button>
          </Col>
        </Row>
      </Form>
    </div>
  );
};

export default CreateScriptActionPage;
