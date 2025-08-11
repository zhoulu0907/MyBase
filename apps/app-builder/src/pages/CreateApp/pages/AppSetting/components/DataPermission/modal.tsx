import { useState } from 'react';
import {
    Checkbox,
    Button,
    Modal,
    Form,
    Input,
    Select,
} from '@arco-design/web-react';
import { IconPlus, IconClose } from '@arco-design/web-react/icon';
import styles from './index.module.less';

const Option = Select.Option;
const CheckboxGroup = Checkbox.Group;

const options = ['Beijing', 'Shanghai'];

const options2 = [
    {
        label: '可查看',
        value: '1',
    },
    {
        label: '可操作',
        value: '2',
    },
];

const conditionData = {
    status: '',
    condition: '',
    value: '',
};

interface IProps {
    form: any;
    status: 'edit' | 'create';
    visible: boolean;
    onClose: (e?: MouseEvent | undefined) => void | Promise<any>;
}

// 数据权限弹窗
const PermissionModal = (props: IProps) => {
    const { form, status, visible = false, onClose } = props;

    const [value, setValue] = useState<string[]>(['1', '2']);
    const [checkAll, setCheckAll] = useState<boolean>(true);
    const [indeterminate, setIndeterminate] = useState<boolean>(false);
    const [visibleFilter, setVisibleFilter] = useState<boolean>(false);
    const [conditionGroup, setConditionGroup] = useState<any[]>([
        [conditionData],
    ]); // 条件组

    function onChangeAll(checked: boolean) {
        if (checked) {
            setIndeterminate(false);
            setCheckAll(true);
            setValue(['1', '2']);
        } else {
            setIndeterminate(false);
            setCheckAll(false);
            setValue([]);
        }
    }

    function onChange(checkList: string[]) {
        setIndeterminate(
            !!(checkList.length && checkList.length !== options2.length)
        );
        setCheckAll(!!(checkList.length === options2.length));
        setValue(checkList);
    }

    return (
        <>
            {/* 添加、编辑数据权限组 */}
            <Modal
                title={
                    <div style={{ textAlign: 'left' }}>
                        {status === 'create' ? '添加' : '编辑'}数据权限组
                    </div>
                }
                visible={visible}
                onOk={onClose}
                onCancel={onClose}
                autoFocus={false}
                focusLock={true}
                okButtonProps={{
                    disabled: true,
                }}
                okText='创建'
                style={{ width: 716 }}
            >
                <Form
                    form={form}
                    layout='vertical'
                    style={{ padding: '0 65px', boxSizing: 'border-box' }}
                >
                    <Form.Item field='' label='权限组名称' rules={[{ required: true }]}>
                        <Input placeholder='请输入权限组名称' />
                    </Form.Item>
                    <Form.Item field='' label='说明' rules={[{ required: false }]}>
                        <Input placeholder='请输入权限组说明' />
                    </Form.Item>
                    <Form.Item field='' label='业务实体' rules={[{ required: true }]}>
                        <Select placeholder='请选择业务实体' onChange={(value) => {}}>
                            {options.map((option, index) => (
                                <Option key={option} disabled={index === 3} value={option}>
                                    {option}
                                </Option>
                            ))}
                        </Select>
                    </Form.Item>
                    <Form.Item field='' label='权限范围' rules={[{ required: true }]}>
                        <div
                            style={{
                                display: 'flex',
                                alignItems: 'center',
                                justifyContent: 'space-between',
                            }}
                        >
                            <Select
                                placeholder='拥有者'
                                onChange={(value) => {}}
                                style={{ width: 150 }}
                            >
                                {options.map((option, index) => (
                                    <Option key={option} disabled={index === 3} value={option}>
                                        {option}
                                    </Option>
                                ))}
                            </Select>
                            是
                            <Select
                                placeholder='本人'
                                onChange={(value) => {}}
                                style={{ width: 360 }}
                            >
                                {options.map((option, index) => (
                                    <Option key={option} disabled={index === 3} value={option}>
                                        {option}
                                    </Option>
                                ))}
                            </Select>
                        </div>
                    </Form.Item>
                    <Form.Item field='' label='数据过滤' rules={[{ required: false }]}>
                        <div
                            style={{
                                border: '1px solid #E5E6EB',
                                borderRadius: 4,
                                padding: 18,
                                boxSizing: 'border-box',
                            }}
                        >
                            <Button
                                type='outline'
                                icon={<IconPlus />}
                                onClick={() => setVisibleFilter(true)}
                            >
                                添加条件组
                            </Button>
                        </div>
                    </Form.Item>
                    <Form.Item field='' rules={[{ required: false }]}>
                        <div style={{ marginBottom: 20 }}>
                            <Checkbox
                                onChange={onChangeAll}
                                checked={checkAll}
                                indeterminate={indeterminate}
                            >
                                操作权限
                            </Checkbox>
                        </div>
                        <CheckboxGroup
                            value={value}
                            options={options2}
                            onChange={onChange}
                        />
                    </Form.Item>
                </Form>
            </Modal>
            {/* 添加条件组 */}
            <Modal
                title={null}
                visible={visibleFilter}
                onOk={() => setVisibleFilter(false)}
                onCancel={() => setVisibleFilter(false)}
                autoFocus={false}
                focusLock={true}
                okButtonProps={{
                    disabled: true,
                }}
                style={{ width: 600 }}
                footer={null}
            >
                <Form
                    form={form}
                    layout='vertical'
                    style={{ padding: '0', boxSizing: 'border-box' }}
                >
                    <Form.Item field='' label='数据过滤' rules={[{ required: true }]}>
                        <div
                            style={{
                                border: '1px solid #E5E6EB',
                                borderRadius: 4,
                                padding: 18,
                                boxSizing: 'border-box',
                            }}
                        >
                            {conditionGroup.map((group, index) => (
                                <div
                                    key={index}
                                    style={{
                                        display: 'flex',
                                        flexWrap: 'wrap',
                                        alignItems: 'center',
                                        padding: '9px 18px 9px 9px',
                                        boxSizing: 'border-box',
                                        borderRadius: 4,
                                        background: 'rgb(225 227 231)',
                                        marginBottom: 15,
                                        position: 'relative',
                                    }}
                                >
                                    <IconClose
                                        style={{
                                            fontSize: 16,
                                            position: 'absolute',
                                            top: 15,
                                            right: 15,
                                            cursor: 'pointer',
                                        }}
                                        onClick={() => {
                                            const newGroup = conditionGroup.filter(
                                                (_, idx) => idx !== index
                                            );
                                            setConditionGroup(newGroup);
                                        }}
                                    />
                                    {group.map((item, idx: number) => (
                                        <div key={idx} style={{ marginBottom: 8 }}>
                                            <Select
                                                placeholder='归档状态'
                                                onChange={(value) => {}}
                                                style={{ width: 100, marginRight: 12 }}
                                            >
                                                {options.map((option, index) => (
                                                    <Option
                                                        key={option}
                                                        disabled={index === 3}
                                                        value={option}
                                                    >
                                                        {option}
                                                    </Option>
                                                ))}
                                            </Select>
                                            <Select
                                                placeholder='等于'
                                                onChange={(value) => {}}
                                                style={{ width: 100, marginRight: 12 }}
                                            >
                                                {options.map((option, index) => (
                                                    <Option
                                                        key={option}
                                                        disabled={index === 3}
                                                        value={option}
                                                    >
                                                        {option}
                                                    </Option>
                                                ))}
                                            </Select>
                                            <Select
                                                placeholder='静态值'
                                                onChange={(value) => {}}
                                                style={{ width: 100, marginRight: 12 }}
                                            >
                                                {options.map((option, index) => (
                                                    <Option
                                                        key={option}
                                                        disabled={index === 3}
                                                        value={option}
                                                    >
                                                        {option}
                                                    </Option>
                                                ))}
                                            </Select>
                                            <Select
                                                className={styles.customSelect}
                                                placeholder='已归档'
                                                onChange={(value) => {}}
                                                style={{ width: 140 }}
                                            >
                                                {options.map((option, index) => (
                                                    <Option
                                                        key={option}
                                                        disabled={index === 3}
                                                        value={option}
                                                    >
                                                        {option}
                                                    </Option>
                                                ))}
                                            </Select>
                                        </div>
                                    ))}

                                    <Button
                                        type='outline'
                                        size='mini'
                                        icon={<IconPlus />}
                                        style={{ marginTop: 5 }}
                                        onClick={() => {
                                            const newGroup = [...conditionGroup];
                                            newGroup[index].push(conditionData);
                                            setConditionGroup(newGroup);
                                        }}
                                    >
                                        并且
                                    </Button>
                                </div>
                            ))}
                            <Button
                                type='outline'
                                size='small'
                                icon={<IconPlus />}
                                onClick={() =>
                                    setConditionGroup((pre) => [...pre, [conditionData]])
                                }
                            >
                                或者
                            </Button>
                        </div>
                    </Form.Item>
                </Form>
            </Modal>
        </>
    );
};

export default PermissionModal;
