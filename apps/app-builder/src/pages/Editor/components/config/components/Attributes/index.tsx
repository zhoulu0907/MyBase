import type { TXInputTextEditData } from "@/components/Materials/Basic/FormComponents/InputText/schema";
import { CONFIG_TYPES } from "@/components/Materials/constants";
import { usePageEditorStore } from "@/hooks/useStore";
import {
    Button,
    Checkbox,
    Form,
    Input,
    InputNumber,
    Message,
    Radio,
    Switch
} from "@arco-design/web-react";
import { IconDelete, IconDragDotVertical } from "@arco-design/web-react/icon";
import { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { ReactSortable } from "react-sortablejs";
import styles from "./index.module.less";

const FormItem = Form.Item;

/**
 * 属性配置面板组件
 * @param props.cpID 组件唯一ID
 */
interface ConfigsProps {
    cpID: string;
}

const Attributes = ({ cpID }: ConfigsProps) => {
    const { t } = useTranslation();

    const { curComponentSchema, setCurComponentSchema, setPageComponentSchemas } = usePageEditorStore();

    const [editData, setEditData] = useState<TXInputTextEditData>([]);
    const [configs, setConfigs] = useState<any>({});

    useEffect(() => {
        if (!cpID) {
            return;
        }

        setEditData(curComponentSchema.editData);
        setConfigs(curComponentSchema.config);

    }, [cpID, curComponentSchema]);

    const handlePropsChange = (key: string, value: string | boolean | any[]) => {
        console.log(`更新了属性: ${key} 值为: ${value}`)

        const newCurComponentSchema = {
            id: cpID,
            type: curComponentSchema.type,
            editData: curComponentSchema.editData,
            config: {
                ...curComponentSchema.config,
                [key]: value
            },
            layout: curComponentSchema.layout
        };

        // console.log(curComponentSchema.config)
        // console.log(newCurComponentSchema.config)

        setCurComponentSchema(newCurComponentSchema)
        setPageComponentSchemas(cpID, newCurComponentSchema)
    }

    const handleLayoutChange = (key: string, value: string) => {
        console.log(`更新了布局属性: ${key} 值为: ${value}`)

        const newCurComponentSchema = {
            id: cpID,
            type: curComponentSchema.type,
            editData: curComponentSchema.editData,
            config: {
                ...curComponentSchema.config,
                [key]: value
            },
            layout: {
                ...curComponentSchema.layout,
                [key === 'width' ? 'w' : key === 'height' ? 'h' : key]: value
            }
        };

        setCurComponentSchema(newCurComponentSchema)
        setPageComponentSchemas(cpID, newCurComponentSchema)
    }


    // 可根据 id 获取/设置对应组件的属性，这里暂时未实现具体逻辑
    return (
         <div className={styles.attributes}>
            {cpID &&<Form autoComplete="off" layout="vertical">
                <FormItem label="组件ID">
                    <span>{cpID}</span>
                </FormItem>

                {editData.map((item: any, index: number) => {
                    if (item.type !== CONFIG_TYPES.SWITCH_INPUT) {
                        return (
                            <FormItem label={item.name} key={index}
                            >
                                {
                                (item.type === CONFIG_TYPES.TEXT_INPUT
                                    || item.type === CONFIG_TYPES.LABEL_INPUT
                                    || item.type === CONFIG_TYPES.TOOLTIP_INPUT
                                    || item.type === CONFIG_TYPES.PLACEHOLDER_INPUT
                                ) &&
                                    <Input
                                        placeholder={`请输入${item.name}`}
                                        value={configs[item.key]}
                                        onChange={(value) => {
                                            handlePropsChange(item.key, value)
                                        }}
                                    />
                                }
                                {
                                    (item.type === CONFIG_TYPES.DESCRIPTION_INPUT) &&
                                    <Input.TextArea
                                        placeholder={`请输入${item.name}`}
                                        value={configs[item.key]}
                                        onChange={(value) => {
                                            handlePropsChange(item.key, value)
                                        }}
                                    />
                                }
                                {
                                    (item.type === CONFIG_TYPES.WIDTH_RADIO) &&
                                    <Radio.Group
                                        type="button"
                                        direction="horizontal"
                                        size="mini"
                                        value={configs[item.key]}
                                        onChange={(value) => {
                                            handleLayoutChange(item.key, value)
                                        }}
                                    >
                                        {item.range.map((item: any) => (
                                            <Radio key={item.key} value={item.value}
                                            className={styles.widthRadio}
                                            >
                                                {item.text && item.text.startsWith('formEditor.')
                                                    ? t(item.text)
                                                    : item.text}
                                            </Radio>
                                        ))}
                                    </Radio.Group>
                                }
                                {
                                    (item.type === CONFIG_TYPES.STATUS_RADIO) &&
                                    <Radio.Group
                                        type="button"
                                        size="default"
                                        value={configs[item.key]}
                                        onChange={(value) => {
                                            handlePropsChange(item.key, value)
                                        }}
                                        style={{
                                            width: '100%',
                                            display: 'flex',
                                        }}
                                    >
                                        {item.range.map((item: any) => (
                                            <Radio
                                                key={item.key}
                                                value={item.value}
                                                style={{
                                                    flex: 1,
                                                    textAlign: 'center',
                                                }}
                                            >
                                                {item.text && item.text.startsWith('formEditor.')
                                                    ? t(item.text)
                                                    : item.text}
                                            </Radio>
                                        ))}
                                    </Radio.Group>
                                }
                                {
                                    (item.type === CONFIG_TYPES.COLUMN_COUNT_RADIO) &&
                                    <Radio.Group
                                        type="button"
                                        size="default"
                                        value={configs[item.key]}
                                        onChange={(value) => {
                                            handlePropsChange(item.key, value)
                                        }}
                                        className={styles.columnCountRadioGroup}
                                    >
                                        {item.range.map((item: any) => (
                                            <Radio key={item.key} value={item.value}
                                                className={styles.columnCountRadio}
                                            >
                                                {item.text && item.text.startsWith('formEditor.')
                                                    ? t(item.text)
                                                    : item.text}
                                            </Radio>
                                        ))}
                                    </Radio.Group>
                                }
                                {
                                    (item.type === CONFIG_TYPES.TABLE_PAGE_POSITION_RADIO) &&
                                    <Radio.Group
                                        type="button"
                                        size="large"
                                        direction="horizontal"
                                        value={configs[item.key]}
                                        onChange={(value) => {
                                            handlePropsChange(item.key, value)
                                        }}
                                    >
                                        {item.range.map((item: any) => (
                                            <Radio key={item.key} value={item.value}
                                            className={styles.pagePositionRadio}
                                            >
                                                {item.text}
                                            </Radio>
                                        ))}
                                    </Radio.Group>
                                }
                                {
                                    (item.type === CONFIG_TYPES.TABLE_COLUMN_LIST) &&
                                    <Form.List
                                        initialValue={configs[item.key] || []}
                                        field={item.key}
                                    >
                                        {(_fields, { add, remove }) => (
                                            <div>
                                                <ReactSortable
                                                        list={configs[item.key]}
                                                        setList={()=>{}}
                                                        group={{
                                                            name: "table-col-item",
                                                        }}
                                                        swap
                                                        sort={true}
                                                        handle=".table-col-item-handle"
                                                        className={styles.componentCollapseContent}
                                                        forceFallback={true}
                                                        animation={150}
                                                        onSort={(e)=>{
                                                            const newList = [...(configs[item.key] || [])];
                                                            // 根据 onSort 事件中的 oldIndex 和 newIndex 交换数组元素
                                                            const { oldIndex, newIndex } = e;
                                                            console.log(oldIndex, newIndex)
                                                            if (oldIndex !== undefined && newIndex !== undefined && oldIndex !== newIndex) {
                                                                // 复制一份新数组
                                                                const movedList = [...newList];
                                                                // 取出被移动的元素
                                                                const [movedItem] = movedList.splice(oldIndex, 1);
                                                                // 插入到新位置
                                                                movedList.splice(newIndex, 0, movedItem);
                                                                // 更新属性
                                                                handlePropsChange(item.key, movedList);
                                                            }
                                                        }}
                                                    >
                                                {configs[item.key].map((_col: any, idx: number) => (


                                                    <div

                                                        key={idx}
                                                        className={styles.tableColumnItem}
                                                    >
                                                        <IconDragDotVertical
                                                            className="table-col-item-handle"
                                                            style={{
                                                                cursor: 'move',
                                                                color: '#555',
                                                            }}
                                                        />
                                                        <Input
                                                            size="small"
                                                            value={configs[item.key][idx].title}
                                                            onChange={(e) => {
                                                                const newList = [...(configs[item.key] || [])];
                                                                newList[idx] = {...newList[idx], title: e, dataIndex: e}

                                                                handlePropsChange(item.key, newList);
                                                            }}
                                                            className={styles.tableColumnItemInput}
                                                            // TODO(mickey): 国际化
                                                            placeholder={`请输入第${idx + 1}项`}
                                                        />
                                                        <InputNumber
                                                            size="small"
                                                            max={500}
                                                            min={50}
                                                            value={configs[item.key][idx].width}
                                                            className={styles.tableColumnItemInput}
                                                            onChange={(e) => {
                                                                const newList = [...(configs[item.key] || [])];
                                                                newList[idx] = {...newList[idx], width: e}

                                                                handlePropsChange(item.key, newList);
                                                            }}
                                                            // TODO(mickey): 国际化
                                                            placeholder="宽度"
                                                        />
                                                        <Checkbox
                                                            checked={configs[item.key][idx].fixed || false}
                                                            onChange={(e) => {

                                                                const newList = [...(configs[item.key] || [])];
                                                                if (newList[idx].width === undefined) {
                                                                    // TODO(mickey): 国际化
                                                                    Message.error("请先设置宽度")
                                                                    return
                                                                }
                                                                newList[idx] = {...newList[idx], fixed: e ? 'left' : false}
                                                                handlePropsChange(item.key, newList);
                                                            }}
                                                        >
                                                            固定
                                                        </Checkbox>
                                                        <Button
                                                            icon={<IconDelete />}
                                                            shape='circle'
                                                            size="mini"
                                                            status='danger'
                                                            className={styles.tableColumnItemButton}
                                                            onClick={() => {
                                                                const newList = [...(configs[item.key] || [])];
                                                                newList.splice(idx, 1);
                                                                handlePropsChange(item.key, newList);
                                                                remove(idx)
                                                            }}
                                                        ></Button>
                                                    </div>

                                                ))}
                                                </ReactSortable>
                                                <Button
                                                    type="outline"
                                                    onClick={() => {
                                                        const newList = [...(configs[item.key] || []), {"title": "", "dataIndex": ""}];
                                                        add({"title": "", "dataIndex": ""});
                                                        handlePropsChange(item.key, newList);
                                                    }}
                                                >
                                                    新增列
                                                </Button>
                                            </div>
                                        )}
                                    </Form.List>
                                }

                            </FormItem>
                        )
                    }

                    if (item.type === CONFIG_TYPES.SWITCH_INPUT) {
                        return (
                            <FormItem label={item.name} key={index}
                                layout="inline"
                            >
                                {
                                    (item.type === CONFIG_TYPES.SWITCH_INPUT) &&
                                    <Switch
                                        size="small"
                                        checked={configs[item.key]}
                                        onChange={(value) => {
                                            handlePropsChange(item.key, value)
                                        }}
                                    />
                                }
                            </FormItem>
                        )
                    }

                })}
            </Form>}
        </div>
    );
};

export default Attributes;
