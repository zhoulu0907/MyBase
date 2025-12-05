import IconCollapsedDown from '@/assets/images/collapse_down_icon.svg';
import IconCollapsed from '@/assets/images/collapsed_left_icon.svg';
import IconSearchForm from '@/assets/images/search_form_icon.svg';
import FieldCard from '@/components/FieldCard';
import { Collapse, Input, Layout } from '@arco-design/web-react';
import { ENTITY_TYPE_VALUE, FilterEntityFields, type AppEntityField } from '@onebase/app';
import {
  COMPONENT_GROUP_NAME,
  COMPONENT_MAP,
  COMPONENT_TYPE_DISPLAY_NAME_MAP,
  ENTITY_COMPONENT_TYPES,
  FORM_COMPONENT_TYPES,
  useAppEntityStore
} from '@onebase/ui-kit';
import React, { useEffect, useState } from 'react';
import { ReactSortable } from 'react-sortablejs';
import styles from './index.module.less';

const Sider = Layout.Sider;
const InputSearch = Input.Search;
const CollapseItem = Collapse.Item;

interface MetadataContainerProps {
  childCollapsed: string | undefined;
  setChildCollapsed: () => void;
}

interface FieldItem {
  id: string;
  displayName: string;
  label: string;
  type: string;
  fieldName: string;
  tableName: string;
}

interface GroupedSection {
  name: string;
  data: FieldItem[];
}

type FieldItemsMap = Record<string, GroupedSection[]>;

const MetadataContainer: React.FC<MetadataContainerProps> = ({ childCollapsed, setChildCollapsed }) => {
  const { mainEntity, subEntities } = useAppEntityStore();

  const [activeEntityID, setActiveEntityID] = useState<string>(mainEntity.entityId);
  const [showSearchInput, setShowSearchInput] = useState<boolean>(false);

  // 现在支持多个 entity，每个 entityId 对应一个字段数组
  const [fieldItems, setFieldItems] = useState<FieldItemsMap>({ [mainEntity.entityId]: [] });

  // 主表字段
  useEffect(() => {
    if (mainEntity.fields.length > 0) {
      const newFieldItems = mainEntity.fields
        //   系统字段不展示
        .filter((field: AppEntityField) => !FilterEntityFields.includes(field.fieldName))
        .map((field: AppEntityField, index: number) => {
          let cpType = COMPONENT_MAP[field.fieldType];
          if (!cpType) {
            cpType = FORM_COMPONENT_TYPES.INPUT_TEXT;
          }
          return {
            // TODO(mickey): 使用uuid作为id
            id: `${cpType}-${index}-${Date.now()}`,
            displayName: COMPONENT_TYPE_DISPLAY_NAME_MAP[cpType] || '',
            label: field.displayName,
            type: cpType,

            tableName: mainEntity.tableName,
            fieldName: field.fieldName,
            isSystemField: field.isSystemField
          };
        })
        .filter((item) => item !== null)
        .reduce(
          (acc: any, field: any) => {
            if (field.isSystemField === 1) {
              acc[1].data.push(field);
            } else {
              acc[0].data.push(field);
            }
            return acc;
          },
          [
            { name: '自定义字段', data: [] },
            { name: '系统字段', data: [] }
          ]
        );

      setFieldItems((prevFieldItems) => ({
        ...prevFieldItems,
        [mainEntity.entityId]: newFieldItems
      }));
    }
  }, [mainEntity]);

  // 子表字段
  useEffect(() => {
    subEntities.entities.forEach((subEntity) => {
      const newFieldItems = subEntity.fields
        //   部分系统字段不展示
        .filter((field: AppEntityField) => !FilterEntityFields.includes(field.fieldName))
        .map((field: AppEntityField, index: number) => {
          let cpType = COMPONENT_MAP[field.fieldType];
          if (!cpType) {
            cpType = FORM_COMPONENT_TYPES.INPUT_TEXT;
          }
          return {
            // TODO(mickey): 使用uuid作为id
            id: `${cpType}-${index}-${Date.now()}`,
            displayName: COMPONENT_TYPE_DISPLAY_NAME_MAP[cpType] || '',
            // displayName: field.displayName,
            label: field.displayName,
            type: cpType,

            tableName: subEntity.tableName,
            fieldName: field.fieldName,
            isSystemField: field.isSystemField
          };
        })
        .filter((item) => item !== null)
        .reduce(
          (acc: any, field: any) => {
            if (field.isSystemField === 1) {
              acc[1].data.push(field);
            } else {
              acc[0].data.push(field);
            }
            return acc;
          },
          [
            { name: '自定义字段', data: [] },
            { name: '系统字段', data: [] }
          ]
        );

      setFieldItems((prevFieldItems) => ({
        ...prevFieldItems,
        [subEntity.entityId]: newFieldItems
      }));
    });
  }, [subEntities]);

  // todo 搜索功能

  return (
    <div>
      <Sider collapsed={!childCollapsed} collapsible collapsedWidth={0} trigger={null} width={270}>
        <div className={styles.rightHeader}>
          <div className={styles.title}></div>

          <div className={styles.right}>
            <div className={styles.search} onClick={() => setShowSearchInput(true)}>
              {!showSearchInput ? (
                <img src={IconSearchForm} alt="search some component" />
              ) : (
                <InputSearch autoFocus onBlur={() => setShowSearchInput(false)} />
              )}
            </div>
            <div className={styles.collapse} onClick={setChildCollapsed}>
              <img src={IconCollapsed} alt="collapse" />
            </div>
          </div>
        </div>

        <div className={styles.rightBody}>
          <div className={styles.entityHeader}>数据资产</div>
          <div className={styles.entityListWrapper}>
            <div className={styles.entityList}>
              <Collapse
                className={styles.entityCollapse}
                bordered={false}
                defaultActiveKey={['main']}
                triggerRegion="icon"
              >
                <CollapseItem
                  name="main"
                  header={
                    <ReactSortable
                      list={[
                        {
                          ...mainEntity,
                          id: mainEntity.entityId,
                          type: ENTITY_TYPE_VALUE.MAIN,
                          displayName: ENTITY_TYPE_VALUE.MAIN
                        }
                      ]}
                      setList={() => {}}
                      group={{
                        name: COMPONENT_GROUP_NAME,
                        pull: 'clone',
                        put: false
                      }}
                      sort={false}
                      className={styles.fieldListContent}
                      forceFallback={true}
                      animation={150}
                    >
                      <div
                        className={styles.mainEntityHeader}
                        onClick={() => setActiveEntityID(mainEntity.entityId)}
                        data-cp-type={ENTITY_COMPONENT_TYPES.MAIN_ENTITY}
                        data-entity-id={mainEntity.entityId}
                        data-table-name={mainEntity.tableName}
                      >
                        <div className={styles.mainEntityHeaderIcon}>主</div>
                        {mainEntity.entityName || '无'}
                      </div>
                    </ReactSortable>
                  }
                  contentStyle={{
                    // borderLeft: '1px solid #e8e8e8',
                    // marginLeft: '20px',
                    paddingLeft: '25px',
                    display: 'flex',
                    flexDirection: 'column',
                    alignItems: 'center',
                    justifyContent: 'flex-start',
                    backgroundColor: 'white'
                  }}
                >
                  <ReactSortable
                    list={[
                      ...subEntities.entities.map((subEntity) => ({
                        ...subEntity,
                        id: subEntity.entityId,
                        type: ENTITY_TYPE_VALUE.SUB,
                        displayName: ENTITY_TYPE_VALUE.SUB
                      }))
                    ]}
                    setList={() => {}}
                    group={{
                      name: COMPONENT_GROUP_NAME,
                      pull: 'clone',
                      put: false
                    }}
                    sort={false}
                    className={styles.fieldListContent}
                    forceFallback={true}
                    animation={150}
                  >
                    {subEntities.entities.map((subEntity) => (
                      <div
                        className={styles.subEntityHeader}
                        key={subEntity.entityId}
                        onClick={() => setActiveEntityID(subEntity.entityId)}
                        data-cp-type={ENTITY_COMPONENT_TYPES.SUB_ENTITY}
                        data-entity-id={subEntity.entityId}
                        data-table-name={subEntity.tableName}
                      >
                        <div className={styles.subEntityHeaderIcon}>子</div>
                        {subEntity.entityName || '无'}
                      </div>
                    ))}
                  </ReactSortable>

                  {/* <div className={styles.relEntityHeader}>
                  <div className={styles.relEntityHeaderIcon}>关联</div>
                  党员信息表
                </div> */}
                </CollapseItem>
              </Collapse>

              {/*
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
            </Button> */}
            </div>
          </div>

          <div className={styles.fieldHeader}>数据字段</div>
          <div className={styles.fieldListWrapper}>
            <Collapse
              defaultActiveKey={'自定义字段'}
              accordion={false}
              bordered={false}
              expandIconPosition="right"
              expandIcon={<img src={IconCollapsedDown} alt="" />}
            >
              {fieldItems[activeEntityID].map((field: GroupedSection) => (
                <Collapse.Item
                  header={field.name}
                  name={field.name}
                  key={field.name}
                  style={{ border: 'none' }}
                  contentStyle={{ backgroundColor: '#fff', border: 'none', paddingLeft: 13 }}
                >
                  <div className={styles.fieldList}>
                    <ReactSortable
                      list={field.data || []}
                      setList={() => {}}
                      group={{
                        name: COMPONENT_GROUP_NAME,
                        pull: 'clone',
                        put: false
                      }}
                      sort={false}
                      className={styles.fieldListContent}
                      forceFallback={true}
                      animation={150}
                      onEnd={(e) => {
                        const cpType = e.item.getAttribute('data-cp-type');
                        e.item.id = `${cpType}-${Date.now()}`;

                        const newFieldItems = field.data?.map((c: FieldItem, idx: number) => ({
                          ...c,
                          id: `${c.type}-${idx}-${Date.now()}`
                        }));

                        setFieldItems((prevFieldItems) => ({
                          ...prevFieldItems,
                          [activeEntityID]: prevFieldItems[activeEntityID].map((section) =>
                            section.name === field.name ? { ...section, data: newFieldItems } : section
                          )
                        }));
                      }}
                    >
                      {field.data?.map((item) => (
                        <FieldCard
                          key={item.id}
                          id={item.id}
                          displayName={item.displayName}
                          label={item.label}
                          type={item.type}
                          tableName={item.tableName}
                          fieldName={item.fieldName}
                        />
                      ))}
                    </ReactSortable>
                  </div>
                </Collapse.Item>
              ))}
            </Collapse>
          </div>
        </div>
      </Sider>
    </div>
  );
};

export default MetadataContainer;
