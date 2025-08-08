import { useState, type FC } from 'react';
import { Radio, Checkbox, Collapse } from '@arco-design/web-react';
import {
    IconInteraction,
    IconDashboard,
    IconMindMapping,
} from '@arco-design/web-react/icon';

const RadioGroup = Radio.Group;
const CollapseItem = Collapse.Item;
const CheckboxGroup = Checkbox.Group;

const options = [
    {
        label: '新增',
        value: '1',
    },
    {
        label: '导入',
        value: '2',
    },
    {
        label: '导出',
        value: '3',
    },
    {
        label: '保存',
        value: '4',
    },
    {
        label: '分享',
        value: '5',
    },
];

const options2 = [
    {
        label: (
            <>
                <IconInteraction style={{ color: '#5D77EC', marginRight: 10 }} />
                全量表
            </>
        ),
        value: '1',
    },
    {
        label: (
            <>
                <IconInteraction style={{ color: '#5D77EC', marginRight: 10 }} />
                领导简表
            </>
        ),
        value: '2',
    },
    {
        label: (
            <>
                <IconDashboard style={{ color: '#E7924D', marginRight: 10 }} />
                可视化看板
            </>
        ),
        value: '3',
    },
    {
        label: (
            <>
                <IconMindMapping style={{ color: '#C862A2', marginRight: 10 }} />
                甘特图
            </>
        ),
        value: '4',
    },
];

// 管理员面板
const FuncPermission: FC = () => {
    const [value, setValue] = useState(['1', '2', '3']);
    const [checkAll, setCheckAll] = useState(false);
    const [indeterminate, setIndeterminate] = useState(true);

    function onChangeAll(checked: boolean) {
        if (checked) {
            setIndeterminate(false);
            setCheckAll(true);
            setValue(['1', '2', '3', '4']);
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
        <Collapse defaultActiveKey={['1', '2', '3']} expandIconPosition='right'>
            <CollapseItem header='页面权限' name='1'>
                <RadioGroup type='button' name='lang' defaultValue='yes'>
                    <Radio value='yes'>可访问</Radio>
                    <Radio value='no'>无权限</Radio>
                </RadioGroup>
            </CollapseItem>

            <CollapseItem header='操作权限' name='2'>
                <CheckboxGroup
                    options={options}
                    defaultValue={['1', '2', '3', '4', '5']}
                />
            </CollapseItem>

            <CollapseItem header='视图权限' name='3'>
                <div>
                    <Checkbox
                        onChange={onChangeAll}
                        checked={checkAll}
                        indeterminate={indeterminate}
                    >
                        {checkAll ? '取消全选' : '全选'}
                    </Checkbox>
                </div>
                <CheckboxGroup
                    direction='vertical'
                    value={value}
                    options={options2}
                    onChange={onChange}
                />
            </CollapseItem>
        </Collapse>
    );
};

export default FuncPermission;
