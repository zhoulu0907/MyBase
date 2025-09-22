import IconCollapsed from '@/assets/images/collapsed_left_icon.svg';
import IconSearchForm from '@/assets/images/search_form_icon.svg';
import FieldCard from '@/components/FieldCard';
import { useAppEntityStore } from '@/store/store_entity';
import { Collapse, Input, Layout } from '@arco-design/web-react';
import { FilterEntityFields, type AppEntityField } from '@onebase/app';
import {
  COMPONENT_GROUP_NAME,
  COMPONENT_TYPE_DISPLAY_NAME_MAP,
  ENTITY_COMPONENT_TYPES,
  FORM_COMPONENT_TYPES
} from '@onebase/ui-kit';
import React, { useEffect, useState } from 'react';
import { ReactSortable } from 'react-sortablejs';
import { COMPONENT_MAP } from './component_map';
import styles from './index.module.less';

const Sider = Layout.Sider;
const InputSearch = Input.Search;
const CollapseItem = Collapse.Item;

interface MetadataContainerProps {
  childCollapsed: string | undefined;
  setChildCollapsed: () => void;
}

const MetadataContainer: React.FC<MetadataContainerProps> = ({ childCollapsed, setChildCollapsed }) => {
  const { mainEntity, subEntities } = useAppEntityStore();

  const [activeEntityID, setActiveEntityID] = useState<string>(mainEntity.entityId);
  const [showSearchInput, setShowSearchInput] = useState<boolean>(false);

  // 现在支持多个 entity，每个 entityId 对应一个字段数组
  const [fieldItems, setFieldItems] = useState<{
    [entityID: string]: {
      id: string;
      displayName: string;
      label: string;
      type: string;
      fieldID: string;
      entityID: string;
    }[];
  }>({});

  // 主表字段
  useEffect(() => {
    if (mainEntity.fields.length > 0) {
      const newFieldItems = mainEntity.fields
        //   系统字段不展示
        .filter((field: AppEntityField) => !FilterEntityFields.includes(field.fieldName))
        .map((field: AppEntityField, index: number) => {
          let cpType = COMPONENT_MAP[field.fieldType];
          console.log('cpType:', cpType);
          if (!cpType) {
            cpType = FORM_COMPONENT_TYPES.INPUT_TEXT;
          }
          return {
            // TODO(mickey): 使用uuid作为id
            id: `${cpType}-${index}-${Date.now()}`,
            displayName: COMPONENT_TYPE_DISPLAY_NAME_MAP[cpType] || '',
            label: field.displayName,
            type: cpType,
            fieldID: field.fieldId,
            entityID: mainEntity.entityId
          };
        })
        .filter((item) => item !== null);

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
          //   console.log('field: ', field);
          return {
            // TODO(mickey): 使用uuid作为id
            id: `${cpType}-${index}-${Date.now()}`,
            displayName: COMPONENT_TYPE_DISPLAY_NAME_MAP[cpType] || '',
            // displayName: field.displayName,
            label: field.displayName,
            type: cpType,
            fieldID: field.fieldId,
            entityID: subEntity.entityId
          };
        })
        .filter((item) => item !== null);

      setFieldItems((prevFieldItems) => ({
        ...prevFieldItems,
        [subEntity.entityId]: newFieldItems
      }));
    });
  }, [subEntities]);

  useEffect(() => {
    console.log('fieldItems', fieldItems);
  }, [fieldItems]);

  // todo 搜索功能

  return (
    <div>
      <Sider collapsed={!childCollapsed} collapsible collapsedWidth={0} trigger={null} width={295}>
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
          <div className={styles.entityHeader}>业务实体</div>
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
                          type: 'entity',
                          displayName: 'entity'
                        }
                      ]}
                      setList={() => { }}
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
                        console.log('onEnd', e);
                      }}
                    >
                      <div
                        className={styles.mainEntityHeader}
                        onClick={() => setActiveEntityID(mainEntity.entityId)}
                        data-cp-type={ENTITY_COMPONENT_TYPES.MAIN_ENTITY}
                        data-entity-id={mainEntity.entityId}
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
                        type: 'entity',
                        displayName: 'entity'
                      }))
                    ]}
                    setList={() => { }}
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
                      console.log('onEnd', e);
                    }}
                  >
                    {subEntities.entities.map((subEntity) => (
                      <div
                        className={styles.subEntityHeader}
                        key={subEntity.entityId}
                        onClick={() => setActiveEntityID(subEntity.entityId)}
                        data-cp-type={ENTITY_COMPONENT_TYPES.SUB_ENTITY}
                        data-entity-id={subEntity.entityId}
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

          <div className={styles.fieldList}>
            <ReactSortable
              list={fieldItems[activeEntityID] || []}
              setList={() => { }}
              group={{
                name: COMPONENT_GROUP_NAME,
                pull: 'clone',
                put: false
              }}
              sort={false}
              className={styles.fieldListContent}
              forceFallback={true}
              animation={150}
              // onClone={(e) => {
              //   console.log('onClone', e);
              // }}
              onEnd={(e) => {
                console.log('onEnd', e);
                const cpType = e.item.getAttribute('data-cp-type');
                console.log('cpType', cpType);
                e.item.id = `${cpType}-${Date.now()}`;

                const newFieldItems = fieldItems[activeEntityID]?.map((c, idx) => ({
                  ...c,
                  id: `${c.type}-${idx}-${Date.now()}`
                }));

                console.log('newFieldItems', newFieldItems);
                setFieldItems({ ...fieldItems, [activeEntityID]: newFieldItems });
              }}
            >
              {fieldItems[activeEntityID]?.map((item) => (
                <FieldCard
                  key={item.id}
                  id={item.id}
                  displayName={item.displayName}
                  label={item.label}
                  type={item.type}
                  fieldID={item.fieldID}
                  entityID={item.entityID}
                />
              ))}
            </ReactSortable>
          </div>
        </div>
      </Sider>
    </div>
  );
};

export default MetadataContainer;
