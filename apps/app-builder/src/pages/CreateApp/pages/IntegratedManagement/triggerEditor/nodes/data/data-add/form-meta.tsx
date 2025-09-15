import { type FormMeta, type FormRenderProps } from '@flowgram.ai/fixed-layout-editor';

import { triggerEditorSignal } from '@/store/singals/trigger_editor';
import { Form, Grid, Input, Radio, Select } from '@arco-design/web-react';
import type { AppEntityField } from '@onebase/app';
import { useEffect, useState } from 'react';
import FieldEditor from '../../../components/field-editor';
import { FormContent, FormHeader, FormOutputs } from '../../../form-components';
import { useIsSidebar, useNodeRenderContext } from '../../../hooks';
import { type FlowNodeJSON } from '../../../typings';

const RadioGroup = Radio.Group;
const Option = Select.Option;

const ADD_TYPE = {
  MAIN_ENTITY: 'mainEntity',
  SUB_ENTITY: 'subEntity'
};

export const renderForm = ({ form }: FormRenderProps<FlowNodeJSON['data']>) => {
  const isSidebar = useIsSidebar();
  const { node } = useNodeRenderContext();
  const { mainEntities, subEntities } = triggerEditorSignal;

  const [fieldDataList, setFieldDataList] = useState<AppEntityField[]>([]);
  const [entityList, setEntityList] = useState<any[]>();

  const handlePropsOnChange = (values: any) => {
    triggerEditorSignal.setNodeData(node.id, values);
  };

  const onValuesChange = (changeValue: any, values: any) => {
    console.log('onValuesChange: ', changeValue, values);

    handlePropsOnChange(values);
  };

  const [payloadForm] = Form.useForm();

  const addType = Form.useWatch('addType', payloadForm);

  useEffect(() => {
    if (addType) {
      if (addType == ADD_TYPE.MAIN_ENTITY) {
        setEntityList(mainEntities.value);
      } else {
        setEntityList(subEntities.value);
      }
    }
  }, [addType]);

  useEffect(() => {
    if (payloadForm.getFieldValue('entityId')) {
      setFieldDataList(
        [...mainEntities.value, ...subEntities.value].find(
          (item) => item.entityId === payloadForm.getFieldValue('entityId')
        )?.fields || []
      );
    }
  }, [payloadForm]);

  return (
    <>
      <FormHeader />
      {isSidebar ? (
        <FormContent>
          <Form
            form={payloadForm}
            initialValues={{ ...triggerEditorSignal.nodeData.value[node.id] }}
            onValuesChange={onValuesChange}
            layout="vertical"
          >
            <Grid.Row>
              <Form.Item label="节点ID" field="id" initialValue={node.id}>
                <Input disabled />
              </Form.Item>
            </Grid.Row>

            <Grid.Row>
              <Form.Item label="新增方式" field="addType" rules={[{ required: true, message: '请选择新增方式' }]}>
                <RadioGroup
                  onChange={(_value) => {
                    payloadForm.clearFields('entityId');
                    payloadForm.clearFields('fieldList');
                  }}
                >
                  <Radio value={ADD_TYPE.MAIN_ENTITY}>在主表中新增</Radio>
                  <Radio value={ADD_TYPE.SUB_ENTITY}>在子表中新增</Radio>
                </RadioGroup>
              </Form.Item>
            </Grid.Row>

            <Grid.Row>
              <Form.Item field="entityId" rules={[{ required: true, message: '请选择表单' }]} layout="vertical">
                <Select
                  style={{ width: '100%' }}
                  onChange={(value) => {
                    [...mainEntities.value, ...subEntities.value].forEach((item) => {
                      if (item.entityId === value) {
                        setFieldDataList(item.fields);
                      }
                    });

                    payloadForm.clearFields('fieldList');
                  }}
                >
                  {entityList?.map((item) => (
                    <Option key={item.entityId} value={item.entityId}>
                      {item.entityName}
                    </Option>
                  ))}
                </Select>
              </Form.Item>
            </Grid.Row>

            <Grid.Row>
              <Form.Item label="新增数据" field="batchType" rules={[{ required: true, message: '请选择新增数据' }]}>
                <RadioGroup>
                  <Radio value={false}>新增单条数据</Radio>
                  <Radio value={true}>新增多条数据</Radio>
                </RadioGroup>
              </Form.Item>
            </Grid.Row>

            <Grid.Row>
              <Form.Item label="字段设置" field="fields">
                <FieldEditor fieldList={fieldDataList} form={payloadForm} />
              </Form.Item>
            </Grid.Row>
          </Form>
        </FormContent>
      ) : (
        <FormContent>
          <FormOutputs />
        </FormContent>
      )}
    </>
  );
};

export const formMeta: FormMeta<FlowNodeJSON['data']> = {
  render: renderForm
};
