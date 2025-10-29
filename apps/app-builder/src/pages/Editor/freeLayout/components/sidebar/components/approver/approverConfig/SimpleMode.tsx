
import { useState, useEffect } from 'react';
import { Radio, Form, Select } from '@arco-design/web-react';
import {IconQuestionCircle} from '@arco-design/web-react/icon';
import styles from './index.module.less';

const FormItem = Form.Item;
const RadioGroup = Radio.Group;
const Option = Select.Option;

const SimpleMode = ({setApprovalConfigData}:any) => {
    const [userOptions, setUserOptions] = useState([]);
    const [roleOptions, setRoleOptions] = useState([]);
    const [simpleCkType, setSimpleCkType] = useState<string>('user');
    const [form] = Form.useForm();
    // 校验规则
    const approverFormRules = {
        user: [{ required: true, message: '请选择审批人' }],
        role: [{ required: true, message: '请选择角色' }]
    };
    let [formRes, setFormRes] = useState<any>({})

    function initUserData() {
        let userRes = {
            "code": 0,
            "data": {
                "list": [
                    {
                        "id": "101802183959412736",
                        "username": "gaoguoqing",
                        "nickname": "高国清",
                        "deptId": "545473992555892736",
                        "deptName": "科创中心",
                        "email": "zhentao789@126.com",
                        "mobile": "15890615800",
                        "sex": 0,
                        "avatar": "",
                        "status": 1,
                        "loginIp": "0:0:0:0:0:0:0:1",
                        "loginDate": 1761294924937,
                        "createTime": 1761112867155,
                        "adminType": 1
                    },
                    {
                        "id": "1",
                        "username": "admin",
                        "nickname": "admin",
                        "remark": "管理员",
                        "deptId": "1",
                        "deptName": "222",
                        "postIds": [
                            "1"
                        ],
                        "email": "aoteman@1226.com",
                        "mobile": "18818260272",
                        "sex": 2,
                        "avatar": "http://test.yudao.iocoder.cn/96c787a2ce88bf6d0ce3cd8b6cf5314e80e7703cd41bf4af8cd2e2909dbd6b6d.png",
                        "status": 1,
                        "loginIp": "10.0.104.23",
                        "loginDate": 1761706784448,
                        "createTime": 1609837427000,
                        "adminType": 1
                    },
                    {
                        "id": "104558951679918080",
                        "username": "user10001",
                        "nickname": "admin123",
                        "email": "111111@126.com",
                        "mobile": "15890615800",
                        "sex": 0,
                        "avatar": "",
                        "status": 1,
                        "loginIp": "",
                        "createTime": 1761273332532,
                        "adminType": 2
                    },
                    {
                        "id": "104536566310174720",
                        "username": "10000",
                        "nickname": "admin123",
                        "email": "111111@126.com",
                        "mobile": "15890615800",
                        "sex": 0,
                        "avatar": "",
                        "status": 1,
                        "loginIp": "",
                        "createTime": 1761272029727,
                        "adminType": 2
                    },
                    {
                        "id": "92907220776845312",
                        "username": "newliu",
                        "nickname": "新柳",
                        "mobile": "19099998888",
                        "sex": 0,
                        "avatar": "",
                        "status": 1,
                        "loginIp": "",
                        "createTime": 1760595112956,
                        "adminType": 2
                    },
                    {
                        "id": "91573633428258816",
                        "username": "adminzjx",
                        "nickname": "ZJX",
                        "deptId": "100",
                        "deptName": "一级部门",
                        "mobile": "18616337513",
                        "sex": 0,
                        "avatar": "",
                        "status": 1,
                        "loginIp": "",
                        "createTime": 1760517487392,
                        "adminType": 2
                    },
                    {
                        "id": "91163120454107136",
                        "username": "wangjie",
                        "nickname": "王杰",
                        "email": "",
                        "mobile": "18771963538",
                        "sex": 0,
                        "avatar": "",
                        "status": 1,
                        "loginIp": "10.0.104.23",
                        "loginDate": 1760578017132,
                        "createTime": 1760493592517,
                        "adminType": 2
                    },
                    {
                        "id": "85546007599284224",
                        "username": "chenyongqiang",
                        "nickname": "陈永强",
                        "deptId": "889796964974590",
                        "deptName": "onebase",
                        "email": "",
                        "mobile": "17862966370",
                        "sex": 0,
                        "avatar": "",
                        "status": 1,
                        "loginIp": "",
                        "createTime": 1760166633871,
                        "adminType": 2
                    },
                    {
                        "id": "84165295868870656",
                        "username": "zchtest06",
                        "nickname": "zchtest06",
                        "email": "",
                        "mobile": "15000588036",
                        "sex": 0,
                        "avatar": "",
                        "status": 1,
                        "loginIp": "",
                        "createTime": 1760086265839,
                        "adminType": 2
                    },
                    {
                        "id": "84164832012402688",
                        "username": "zchtest05",
                        "nickname": "zchtest05",
                        "email": "",
                        "mobile": "15000588035",
                        "sex": 0,
                        "avatar": "",
                        "status": 1,
                        "loginIp": "",
                        "createTime": 1760086238304,
                        "adminType": 2
                    }
                ],
                "total": "65"
            },
            "msg": ""
        }
        let select_arr:any = []
        userRes.data.list.forEach((item:any) => {
            select_arr.push({
                userId: item.id, name: item.nickname
            })
        })
        setUserOptions(select_arr)
    }
    function initRoleData() {
        let roleRes = {
            "code": 0,
            "data": {
                "list": [
                    {
                        "id": "1",
                        "username": "admin",
                        "nickname": "admin",
                        "remark": "管理员",
                        "deptId": "1",
                        "deptName": "222",
                        "postIds": [
                            "1"
                        ],
                        "email": "aoteman@1226.com",
                        "mobile": "18818260272",
                        "sex": 2,
                        "avatar": "http://test.yudao.iocoder.cn/96c787a2ce88bf6d0ce3cd8b6cf5314e80e7703cd41bf4af8cd2e2909dbd6b6d.png",
                        "status": 1,
                        "loginIp": "10.0.104.23",
                        "loginDate": 1761706784448,
                        "createTime": 1609837427000,
                        "adminType": 1
                    },
                    {
                        "id": "84164832012402688",
                        "username": "zchtest05",
                        "nickname": "zchtest05",
                        "email": "",
                        "mobile": "15000588035",
                        "sex": 0,
                        "avatar": "",
                        "status": 1,
                        "loginIp": "",
                        "createTime": 1760086238304,
                        "adminType": 2
                    }
                ],
                "total": "65"
            },
            "msg": ""
        }
        let select_arr:any = []
        roleRes.data.list.forEach((item:any) => {
            select_arr.push({
                roleId: item.id, roleName: item.nickname
            })
        })
        setRoleOptions(select_arr)
    }

    function changeSimpleType(val: string) {
        setSimpleCkType(val)
        form.clearFields()
        setFormRes({})
    }

    useEffect(() => {
        let selOptions:any[] = [];
        let itemKey = ''
        if (simpleCkType === 'user') {
            selOptions = userOptions;
            itemKey = 'userId'
        } else if (simpleCkType === 'role') {
            selOptions = roleOptions;
            itemKey = 'roleId'
        } 
        setApprovalConfigData('approverConfig', {
            approverType: simpleCkType,
            users: selOptions.filter((item:any) => {
                if (Array.isArray(formRes[simpleCkType])) {
                    return formRes[simpleCkType].indexOf(item[itemKey]) > -1
                }
                return false
            })
        })
    }, [simpleCkType, formRes])

    useEffect(() => {
        initUserData()
        initRoleData()
    }, [])

    return <>
        <RadioGroup className={styles.approverRadioGroup} value={simpleCkType} onChange={changeSimpleType}>
            <Radio value="user">指定成员</Radio>
            <Radio value="role">指定角色<IconQuestionCircle /></Radio>
            <Radio value="deptManager" disabled>部门负责人</Radio>
            <Radio value="multistageManager" disabled>多级主管</Radio>
            <Radio value="directManager" disabled>直属主管</Radio>
            <Radio value="deptContact" disabled>部门接口人</Radio>
            <Radio value="initiator" disabled>发起人本人</Radio>
            <Radio value="initiatorChoice" disabled>发起人自选</Radio>
            <Radio value="formMember" disabled>表单内成员字段</Radio>
        </RadioGroup>
        <div className={styles.configTitle}></div>
        <Form
            form={form}
            layout="vertical"
            autoComplete="off"
            onValuesChange={() => {
                setFormRes(form.getFieldsValue())
            }}
        >
            {simpleCkType === 'user' && <FormItem
                    className={styles.approverForm}
                    label="选择审批人"
                    field="user"
                    rules={approverFormRules.user}
                    wrapperCol={{style: {width: '100%'}}}
                >
                <Select mode="multiple" placeholder="选择审批人" defaultValue={['Beijing', 'Shenzhen']} allowClear>
                    {userOptions.map((option:any) => (
                        <Option key={option?.userId} value={option?.userId}>
                            {option.name}
                        </Option>
                    ))}
                </Select>
            </FormItem>}
            {simpleCkType === 'role' && <FormItem
                    className={styles.approverForm}
                    label="选择角色"
                    field="role"
                    rules={approverFormRules.role}
                    wrapperCol={{style: {width: '100%'}}}
                >
                <Select mode="multiple" placeholder="选择角色" defaultValue={['Beijing', 'Shenzhen']} allowClear>
                    {roleOptions.map((option:any) => (
                        <Option key={option?.roleId} value={option?.roleId}>
                            {option.roleName}
                        </Option>
                    ))}
                </Select>
            </FormItem>}
        </Form>
    </>
}

export default SimpleMode;