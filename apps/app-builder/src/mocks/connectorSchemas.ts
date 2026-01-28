export const mockConnConfig = {
    type: "object",
    properties: {
        envMode: {
            type: "string",
            title: "环境信息",
            enum: ["create", "select"],
            enumNames: ["创建环境信息", "选择已有环境信息"],
            default: "create",
            "x-decorator": "FormItem",
            "x-component": "Radio.Group",
            "x-component-props": { optionType: "button", buttonStyle: "solid" }
        },
        existingEnvId: {
            type: "string",
            title: "请选择环境信息",
            "x-decorator": "FormItem",
            "x-component": "Select",
            "x-visible": "{{ $form.values.envMode === 'select' }}",
            "x-component-props": { placeholder: "请选择环境信息", allowClear: true }
        },
        envName: {
            type: "string",
            title: "环境名称",
            "x-decorator": "FormItem",
            "x-component": "Input",
            "x-visible": "{{ $form.values.envMode === 'create' }}",
            required: true
        },
        url: {
            type: "string",
            title: "URL",
            "x-decorator": "FormItem",
            "x-component": "Input",
            "x-visible": "{{ $form.values.envMode === 'create' }}",
            required: true
        },
        authType: {
            type: "string",
            title: "选择认证类型",
            "x-decorator": "FormItem",
            "x-component": "Select",
            "x-visible": "{{ $form.values.envMode === 'create' }}",
            "default": "none",
            "enum": [
                { "label": "无认证", "value": "none" },
                { "label": "Basic Auth", "value": "Basic" },
                { "label": "Token认证", "value": "Token" },
                { "label": "OAuth 2.0", "value": "OAuth" },
                { "label": "自定义认证", "value": "CustomSignature" }
            ],
            required: true
        },
        authConfig: {
            type: "void",
            title: "认证设置",
            "x-component": "AuthSettingsCard",
            "x-visible": "{{ $form.values.envMode === 'create' && $form.values.authType !== 'none' }}",
            properties: {
                // Basic Auth
                basic_username: {
                    type: "string",
                    title: "Username",
                    "x-decorator": "FormItem",
                    "x-component": "Input",
                    "x-component-props": { placeholder: "请输入用户名" },
                    "x-visible": "{{ $form.values.authType === 'Basic' }}",
                    required: true
                },
                basic_password: {
                    type: "string",
                    title: "Password",
                    "x-decorator": "FormItem",
                    "x-component": "Input.Password",
                    "x-component-props": { placeholder: "请输入密码" },
                    "x-visible": "{{ $form.values.authType === 'Basic' }}",
                    required: true
                },
                // Custom Signature
                // Custom Signature
                custom_location_type: {
                    type: "string",
                    title: "添加位置",
                    "x-decorator": "FormItem",
                    "x-component": "Select",
                    "x-visible": "{{ $form.values.authType === 'CustomSignature' }}",
                    "enum": ["Header", "Query", "Body"],
                    "default": "Header",
                    required: true
                },
                custom_params_header: {
                    type: "array",
                    "x-decorator": "FormItem",
                    "x-component": "KeyValueList",
                    "x-visible": "{{ $form.values.authType === 'CustomSignature' && $form.values.custom_location_type === 'Header' }}",
                    "default": []
                },
                custom_params_query: {
                    type: "array",
                    "x-decorator": "FormItem",
                    "x-component": "KeyValueList",
                    "x-visible": "{{ $form.values.authType === 'CustomSignature' && $form.values.custom_location_type === 'Query' }}",
                    "default": []
                },
                custom_params_body: {
                    type: "array",
                    "x-decorator": "FormItem",
                    "x-component": "KeyValueList",
                    "x-visible": "{{ $form.values.authType === 'CustomSignature' && $form.values.custom_location_type === 'Body' }}",
                    "default": []
                },
                // Token Auth
                token_auth_panel: {
                    type: "void",
                    "x-component": "TokenAuthPanel",
                    "x-visible": "{{ $form.values.authType === 'Token' }}",
                    "x-component-props": {}
                },
                // Hidden fields for Token Auth data storage
                token_method: {
                    type: "string",
                    "x-decorator": "FormItem",
                    "x-component": "Input",
                    "x-visible": false,
                    default: "GET"
                },
                token_url: {
                    type: "string",
                    "x-decorator": "FormItem",
                    "x-component": "Input",
                    "x-visible": false
                },
                token_params_header: {
                    type: "array",
                    "x-decorator": "FormItem",
                    "x-component": "Input", // Just dummy component, managed by Panel
                    "x-visible": false,
                    default: []
                },
                token_params_body: {
                    type: "array",
                    "x-decorator": "FormItem",
                    "x-component": "Input",
                    "x-visible": false,
                    default: []
                },
                token_params_query: {
                    type: "array",
                    "x-decorator": "FormItem",
                    "x-component": "Input",
                    "x-visible": false,
                    default: []
                },
                token_params_path: {
                    type: "array",
                    "x-decorator": "FormItem",
                    "x-component": "Input",
                    "x-visible": false,
                    default: []
                },
                token_result_path: {
                    type: "string",
                    "x-decorator": "FormItem",
                    "x-component": "Input",
                    "x-visible": false
                },
                token_refresh_policy: {
                    type: "string",
                    "x-decorator": "FormItem",
                    "x-component": "Input",
                    "x-visible": false
                }
            }
        }
    }
};
