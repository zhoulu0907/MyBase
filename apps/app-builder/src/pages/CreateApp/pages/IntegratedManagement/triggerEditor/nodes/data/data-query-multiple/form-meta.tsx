import {
  type FormMeta,
  type FormRenderProps,
} from "@flowgram.ai/fixed-layout-editor";
import { triggerEditorSignal } from "@/store/singals/trigger_editor";
import { Form, Input, InputNumber, Select } from "@arco-design/web-react";
import { FormContent, FormHeader, FormOutputs } from "../../../form-components";
import { useIsSidebar, useNodeRenderContext } from "../../../hooks";
import { type FlowNodeJSON } from "../../../typings";
import {
  getEntityListByApp,
  getEntityFields,
  getFieldCheckTypeApi,
  type ConfitionField,
  type EntityFieldValidationTypes
} from '@onebase/app';
import { useEffect, useState } from "react";
import { useAppStore } from "@/store/store_app";
import ConditionEditor from '../../../components/condition-editor';

interface SelectOption {
  label: string;
  value: string;
}

export const renderForm = ({ form }: FormRenderProps<FlowNodeJSON["data"]>) => {
  const isSidebar = useIsSidebar();
  const { node } = useNodeRenderContext();
  // 当前页应用id
  const { curAppId } = useAppStore();
  // 数据源选择
  const [entityList, setEntityList] = useState<SelectOption[]>([]);
  // 排序字段下拉列表
  const [fieldList, setFieldList] = useState<SelectOption[]>([]);
  // 查询条件
  const [validationTypes, setValidationTypes] = useState<EntityFieldValidationTypes[]>([]);
  const [conditionFields, setConditionFields] = useState<ConfitionField[]>([]);

  useEffect(() => {
    const formData = payloadForm.getFieldsValue();
    if (formData.dataType) {
      getEntityList(formData.dataType)
      if (formData.dataSource) {
        getFieldList(formData.dataType, formData.dataSource)
      }
    }
  }, [])

  // 表单项内容变更
  const handlePropsOnChange = (key: string, value: any) => {
    const nodeData = triggerEditorSignal.nodeData.value[node.id];
    triggerEditorSignal.setNodeData(node.id, {
      ...nodeData,
      [key]: value,
    });
    if (key === "dataType") {
      dataTypeChange(value);
    } else if (key === "dataSource") {
      dataSourceChange(value);
    }
  };
  /**
   * 获取方式变更
   * 更新数据源下拉列表，清除已选择数据源
   * 清除排序字段下拉列表，清除已选择排序字段
   */
  const dataTypeChange = async (value: string) => {
    payloadForm.clearFields(["dataSource", "sortBy"]);
    const nodeData = triggerEditorSignal.nodeData.value[node.id];
    triggerEditorSignal.setNodeData(node.id, {
      ...nodeData,
      dataSource: '', // null 和 '' 在 Select 中都被认为是值
      sortBy: '', // 清除已选择排序字段
    });
    setEntityList([]);
    setFieldList([]);
    if (value) {
      getEntityList(value);
    }
  };

  // 数据源变更  更新排序字段下拉列表，清除已选择排序字段
  const dataSourceChange = async (value: string) => {
    payloadForm.clearFields(["sortBy"]);
    const nodeData = triggerEditorSignal.nodeData.value[node.id];
    triggerEditorSignal.setNodeData(node.id, {
      ...nodeData,
      sortBy: '', // 清除已选择排序字段
    });
    // 根据数据源重新获取字段列表
    if (value) {
      const dataType = payloadForm.getFieldValue("dataType");
      getFieldList(dataType, value)
    }
  };
  // 获取数据源列表
  const getEntityList = async (dataType: string) => {
    if (dataType === "business") {
      // 业务表单
    } else if (dataType === "database") {
      // 数据库表  根据应用ID获取实体列表
      const res = await getEntityListByApp(curAppId);
      console.log("数据库表res: ", res);
      const fieldOptions = res.map((field: any) => ({
        label: field.entityName,
        value: field.entityId,
      }));
      setEntityList(fieldOptions);
    } else if (dataType === "interface") {
      // API接口
    }

  }
  // 获取排序字段下拉列表
  const getFieldList = async (dataType: string, dataSource: string) => {
    // 根据数据源 查询指定实体的字段列表
    // todo 根据不同获取方式走不同接口
    if (dataType === "business") {
      // 业务表单
    } else if (dataType === "database") {
      // 数据库表 查询指定实体的字段列表
      const res = await getEntityFields({ entityId: dataSource });
      const filedIds: string[] = [];
      const newConditionFields: ConfitionField[] = [];
      const fieldOptions: SelectOption[] = []
      res.forEach((item: any) => {
        fieldOptions.push({
          label: item.displayName,
          value: item.id,
        })
        filedIds.push(item.id);
        newConditionFields.push({
          label: item.displayName,
          value: item.id,
          fieldType: item.fieldType
        });
      });
      setFieldList(fieldOptions);
      setConditionFields(newConditionFields);
      if (filedIds?.length) {
        const newValidationTypes = await getFieldCheckTypeApi(filedIds);
        console.log('validationTypes: ', newValidationTypes);
        setValidationTypes(newValidationTypes);
      }
    } else if (dataType === "interface") {
      // API接口
    }
  }

  const [payloadForm] = Form.useForm();

  // 获取方式  数据查询的来源  业务表单、数据库表、API接口
  const dataTypeOptions = [
    { label: "业务表单", value: "business" },
    { label: "数据库表", value: "database" },
    { label: "API接口", value: "interface" },
  ];
  // 排序类型 1-升序、2-降序
  const sortTypeOptions = [
    { label: "升序", value: "1" },
    { label: "降序", value: "2" },
  ];

  return (
    <>
      <FormHeader />
      {isSidebar ? (
        <FormContent>
          <Form
            form={payloadForm}
            layout='vertical'
            initialValues={{ ...triggerEditorSignal.nodeData.value[node.id] }}
          >
            <Form.Item label="节点ID" field="id " initialValue={node.id}>
              <Input disabled />
            </Form.Item>
            <Form.Item label="节点名称" field="nodeName">
              <Input onChange={(e) => handlePropsOnChange("nodeName", e)} />
            </Form.Item>
            <Form.Item label="获取方式" field="dataType">
              <Select
                options={dataTypeOptions}
                allowClear
                onChange={(e) => handlePropsOnChange("dataType", e)}
              ></Select>
            </Form.Item>
            <Form.Item label="数据源" field="dataSource">
              <Select
                options={entityList}
                allowClear
                onChange={(e) => handlePropsOnChange("dataSource", e)}
              ></Select>
            </Form.Item>
            <Form.Item label="查询条件" field="filter_condition">
              <ConditionEditor
                onChange={(e) => handlePropsOnChange('filter_condition', e)}
                data={triggerEditorSignal.nodeData.value[node.id]?.filter_condition || []}
                fields={conditionFields}
                entityFieldValidationTypes={validationTypes}
              />
            </Form.Item>
            <Form.Item label="排序类型" field="sortType">
              <Select
                options={sortTypeOptions}
                allowClear
                onChange={(e) => handlePropsOnChange("sortType", e)}
              ></Select>
            </Form.Item>
            <Form.Item label="排序字段" field="sortBy">
              <Select
                options={fieldList}
                allowClear
                onChange={(e) => handlePropsOnChange("sortBy", e)}
              ></Select>
            </Form.Item>
            <Form.Item label="每页条数" field="pageSize">
              <InputNumber
                mode="button"
                precision={0}
                onChange={(e) => handlePropsOnChange("pageSize", e)}
              />
            </Form.Item>
            <Form.Item label="当前页码" field="pageNo">
              <InputNumber
                mode="button"
                precision={0}
                onChange={(e) => handlePropsOnChange("pageNo", e)}
              />
            </Form.Item>
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

export const formMeta: FormMeta<FlowNodeJSON["data"]> = {
  render: renderForm,
};
