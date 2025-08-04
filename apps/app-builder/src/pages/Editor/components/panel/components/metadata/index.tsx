import FieldCard from "@/components/FieldCard";
import { FORM_COMPONENT_TYPES } from "@/constants/componentTypes";
import { Button, Collapse } from "@arco-design/web-react";
import { IconPlus } from "@arco-design/web-react/icon";
import React, { useState } from "react";
import { useTranslation } from "react-i18next";
import { ReactSortable } from "react-sortablejs";
import { COMPONENT_GROUP_NAME } from "../../../const";
import styles from "./index.module.less";


const CollapseItem = Collapse.Item;

interface MetadataContainerProps {
}

const MetadataContainer: React.FC<MetadataContainerProps> = ({}) => {
    const { t } = useTranslation();

    const [fieldItems, setFieldItems] = useState<{ id: string, displayName: string, type: string }[]>([
        { id: 'field-1', displayName: '活动名称', type: FORM_COMPONENT_TYPES.INPUT_TEXT },
        { id: 'field-2', displayName: '活动目的', type: FORM_COMPONENT_TYPES.INPUT_TEXT },
        { id: 'field-3', displayName: '活动地点', type: FORM_COMPONENT_TYPES.INPUT_TEXT },
        { id: 'field-4', displayName: '活动时间', type: FORM_COMPONENT_TYPES.DATE_TIME_PICKER },
        { id: 'field-5', displayName: '报名截止日期', type: FORM_COMPONENT_TYPES.DATE_PICKER },
        { id: 'field-6', displayName: '参与人数', type: FORM_COMPONENT_TYPES.INPUT_NUMBER },
        { id: 'field-7', displayName: '主办单位', type: FORM_COMPONENT_TYPES.INPUT_TEXT },
        { id: 'field-8', displayName: '参与人姓名', type: FORM_COMPONENT_TYPES.INPUT_TEXT },
        { id: 'field-9', displayName: '参与人电话', type: FORM_COMPONENT_TYPES.INPUT_PHONE },
        { id: 'field-10', displayName: '参与人邮箱', type: FORM_COMPONENT_TYPES.INPUT_EMAIL },
        { id: 'field-11', displayName: '活动预算', type: FORM_COMPONENT_TYPES.INPUT_NUMBER },
        { id: 'field-12', displayName: '活动状态', type: FORM_COMPONENT_TYPES.SELECT_ONE },
    ]);

    return (
        <div>
            <div className={styles.rightHeader}>
                {t('formEditor.metadata')}
            </div>

            <div className={styles.rightBody}>
                <div className={styles.entityHeader}>
                    业务实体
                </div>
                <div className={styles.entityListWrapper}>
                    <div className={styles.entityList}>
                        <Collapse
                            className={styles.entityCollapse}
                            bordered={false}
                            defaultActiveKey={["1"]}
                        >
                            <CollapseItem
                                name="1"
                                header={
                                    <div className={styles.mainEntityHeader}>
                                        <div className={styles.mainEntityHeaderIcon}>
                                            主
                                        </div>
                                        党建活动记录表
                                    </div>
                                }
                                contentStyle={{
                                    // borderLeft: '1px solid #e8e8e8',
                                    // marginLeft: '20px',
                                    paddingLeft: '25px',
                                    display: 'flex',
                                    flexDirection: 'column',
                                    alignItems: 'center',
                                    justifyContent: 'flex-start',
                                    backgroundColor: 'white',

                                }}
                            >
                                <div className={styles.subEntityHeader}>
                                    <div className={styles.subEntityHeaderIcon}>子</div>
                                    活动签到记录表
                                </div>
                                <div className={styles.relEntityHeader}>
                                    <div className={styles.relEntityHeaderIcon}>
                                    关联
                                    </div>
                                    党员信息表
                                </div>
                            </CollapseItem>
                        </Collapse>

                        <div className={styles.importEntityHeader}>
                            <div className={styles.importEntityHeaderIcon}>
                                引入
                            </div>
                            党建活动年度统计
                        </div>
                        <div className={styles.importEntityHeader}>
                            <div className={styles.importEntityHeaderIcon}>
                                引入
                            </div>
                            党建经费使用统计
                        </div>

                        <Button
                            type="outline"
                            size="mini"
                            className={styles.addImportEntityButton}
                            icon={<IconPlus />}
                            style={{
                                color: '#4E5969',
                                border: '1px solid #E5E6EB'
                            }}
                        >
                            添加引入实体
                        </Button>

                    </div>
                </div>

                <div className={styles.fieldHeader}>
                    数据字段
                </div>

                <div className={styles.fieldList}>
                    <ReactSortable
                        list={fieldItems}
                        setList={()=>{}}
                        group={{
                            name: COMPONENT_GROUP_NAME,
                            pull: "clone",
                            put: false,
                        }}
                        sort={false}
                        className={styles.fieldListContent}
                        forceFallback={true}
                        animation={150}
                        onClone={(e)=>{
                            console.log("onClone", e);
                        }}
                        onEnd={(e)=>{
                            console.log("onEnd", e);
                            const cpType = e.item.getAttribute('data-cp-type')
                            console.log("cpType", cpType);
                            e.item.id = `${cpType}-${Date.now()}`;

                            console.log(fieldItems)

                            const newFieldItems = fieldItems.map((c,idx) => ({
                                ...c,
                                id: `${c.type}-${idx}-${Date.now()}`
                            }));

                            console.log("newFieldItems", newFieldItems);
                            setFieldItems(newFieldItems);
                        }}
                    >
                        {
                            fieldItems.map(item => (
                                <FieldCard
                                    key={item.id}
                                    id={item.id || `${item.type}-${Date.now()}`}
                                    displayName={item.displayName}
                                    type={item.type}
                                />
                            ))
                        }
                    </ReactSortable>

                </div>


            </div>
        </div>
    );
};

export default MetadataContainer;
