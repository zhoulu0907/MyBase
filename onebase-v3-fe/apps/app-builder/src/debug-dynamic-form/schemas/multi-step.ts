export const MULTI_STEP_SCHEMA = {
  type: "object",
  properties: {
    currentStep: {
      type: "string",
      default: "basic",
      "x-decorator": "FormItem",
      "x-component": "Input",
      "x-component-props": {
        style: { display: "none" },
      },
    },
    stepNav: {
      type: "void",
      "x-decorator": "FormItem",
      "x-component": "StepNav",
      "x-component-props": {
        steps: [
          { key: "basic", title: "基础信息" },
          { key: "input", title: "入参配置" },
          { key: "output", title: "出参配置" },
          { key: "test", title: "接口调试" },
        ],
      },
    },
    basicGroup: {
      type: "object",
      "x-visible": "{{$form.values.currentStep === 'basic'}}",
      properties: {
        sectionTitle: {
          type: "void",
          title: "基础信息",
          "x-component": "SectionTitle",
        },
        actionName: {
          type: "string",
          title: "动作名称",
          required: true,
          "x-decorator": "FormItem",
          "x-component": "Input",
          "x-component-props": {
            placeholder: "请输入动作名称",
          },
        },
        description: {
          type: "string",
          title: "动作描述",
          "x-decorator": "FormItem",
          "x-component": "TextArea",
          "x-component-props": {
            placeholder: "请输入功能描述",
            autoSize: { minRows: 3, maxRows: 6 },
          },
        },
      },
    },
    inputGroup: {
      type: "object",
      "x-visible": "{{$form.values.currentStep === 'input'}}",
      properties: {
        sectionTitle: {
          type: "void",
          title: "请求地址",
          "x-component": "SectionTitle",
        },
        requestLine: {
          type: "void",
          title: "请求地址",
          "x-decorator": "FormItem",
          "x-component": "HorizontalLayout",
          properties: {
            method: {
              type: "string",
              required: true,
              default: "GET",
              enum: [
                { label: "GET", value: "GET" },
                { label: "POST", value: "POST" },
                { label: "PUT", value: "PUT" },
                { label: "DELETE", value: "DELETE" },
              ],
              "x-component": "Select",
              "x-component-props": {
                style: { width: 120 },
              },
            },
            baseUrl: {
              type: "string",
              "x-component": "Input",
              "x-component-props": {
                placeholder: "例如：https://api.example.com",
                style: { flex: 1 },
              },
            },
            path: {
              type: "string",
              "x-component": "Input",
              "x-component-props": {
                placeholder: "/receive",
                style: { flex: 1 },
              },
            },
          },
        },
        apiParamsTitle: {
          type: "void",
          title: "请求参数",
          "x-component": "SectionTitle",
        },
        paramsTabs: {
          type: "void",
          "x-component": "Tabs",
          properties: {
            headers: {
              type: "array",
              title: "HTTP请求头",
              "x-component": "ParamsTable",
            },
            queryParams: {
              type: "array",
              title: "URL查询参数",
              "x-component": "ParamsTable",
            },
            body: {
              type: "string",
              title: "HTTP请求体",
              "x-component": "JsonEditor",
              "x-component-props": {
                placeholder: "请输入 JSON 请求体",
                height: "300px",
              },
            },
          },
        },
        connectorParamsTitle: {
          type: "void",
          title: "连接器入参",
          "x-component": "SectionTitle",
        },
        connectorParams: {
          type: "array",
          "x-component": "ConnectorParamsTable",
        },
      },
    },
    outputGroup: {
      type: "object",
      "x-visible": "{{$form.values.currentStep === 'output'}}",
      properties: {
        sectionTitle: {
          type: "void",
          title: "出参配置",
          "x-component": "SectionTitle",
        },
        responseHeaders: {
          type: "array",
          title: "响应头字段",
          "x-decorator": "FormItem",
          "x-component": "ParamsTable",
        },
        successRuleTitle: {
          type: "void",
          title: "调用成功规范",
          "x-component": "SectionTitle",
        },
        successStatusCode: {
          type: "number",
          title: "HTTP 状态码",
          "x-decorator": "FormItem",
          "x-component": "InputNumber",
          "x-component-props": {
            placeholder: "例如：200",
          },
        },
      },
    },
    testGroup: {
      type: "object",
      "x-visible": "{{$form.values.currentStep === 'test'}}",
      properties: {
        sectionTitle: {
          type: "void",
          title: "接口调试",
          "x-component": "SectionTitle",
        },
        mockRequestBody: {
          type: "string",
          title: "请求体示例",
          "x-decorator": "FormItem",
          "x-component": "JsonEditor",
          "x-component-props": {
            placeholder: '{ "userId": 1 }',
            height: "300px",
          },
        },
        mockResponseBody: {
          type: "string",
          title: "响应体示例",
          "x-decorator": "FormItem",
          "x-component": "JsonEditor",
          "x-component-props": {
            placeholder: '{ "name": "张三" }',
            height: "300px",
          },
        },
      },
    },
    stepActions: {
      type: "void",
      "x-component": "StepActions",
      "x-component-props": {
        steps: [
          { key: "basic", title: "基础信息" },
          { key: "input", title: "入参配置" },
          { key: "output", title: "出参配置" },
          { key: "test", title: "接口调试" },
        ],
      },
    },
  },
};
