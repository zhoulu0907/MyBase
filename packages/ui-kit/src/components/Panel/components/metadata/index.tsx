import IconCollapsedDown from '@/assets/images/collapse_down_icon.svg';
import IconCollapsed from '@/assets/images/collapsed_left_icon.svg';
import IconSearchForm from '@/assets/images/search_form_icon.svg';
import {
  COMPONENT_MAP,
  COMPONENT_TYPE_DISPLAY_NAME_MAP,
  ENTITY_COMPONENT_TYPES,
  FORM_COMPONENT_TYPES
} from '@/components/Materials';
import { COMPONENT_GROUP_NAME } from '@/utils';
import { Collapse, Input, Layout } from '@arco-design/web-react';
import { AppEntity, FilterEntityFields, type AppEntityField } from '@onebase/app';
import React, { useEffect, useState } from 'react';
import { ReactSortable } from 'react-sortablejs';
import FieldCard from '../FieldCard';
import './index.css';

const Sider = Layout.Sider;
const InputSearch = Input.Search;
const CollapseItem = Collapse.Item;

interface MetadataContainerProps {
  childCollapsed: string | undefined;
  setChildCollapsed: () => void;
  mainEntity: AppEntity;
  subEntities: any;
}

interface FieldItem {
  id: string;
  displayName: string;
  label: string;
  type: string;
  fieldID: string;
  entityID: string;
}

interface GroupedSection {
  name: string;
  data: FieldItem[];
}

type FieldItemsMap = Record<string, GroupedSection[]>;

const MetadataContainer: React.FC<MetadataContainerProps> = ({
  childCollapsed,
  setChildCollapsed,
  mainEntity,
  subEntities
}) => {
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
            fieldID: field.fieldId,
            entityID: mainEntity.entityId,
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
    subEntities.entities.forEach((subEntity: AppEntity) => {
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
            fieldID: field.fieldId,
            entityID: subEntity.entityId,
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
        <div className="rightHeader">
          <div className="title"></div>

          <div className="right">
            <div className="search" onClick={() => setShowSearchInput(true)}>
              {!showSearchInput ? (
                <img src={IconSearchForm} alt="search some component" />
              ) : (
                <InputSearch autoFocus onBlur={() => setShowSearchInput(false)} />
              )}
            </div>
            <div className="collapse" onClick={setChildCollapsed}>
              <img src={IconCollapsed} alt="collapse" />
            </div>
          </div>
        </div>

        <div className="rightBody">
          <div className="entityHeader">业务实体</div>
          <div className="entityListWrapper">
            <div className="entityList">
              <Collapse className="entityCollapse" bordered={false} defaultActiveKey={['main']} triggerRegion="icon">
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
                      setList={() => {}}
                      group={{
                        name: COMPONENT_GROUP_NAME,
                        pull: 'clone',
                        put: false
                      }}
                      sort={false}
                      className="fieldListContent"
                      forceFallback={true}
                      animation={150}
                    >
                      <div
                        className="mainEntityHeader"
                        onClick={() => setActiveEntityID(mainEntity.entityId)}
                        data-cp-type={ENTITY_COMPONENT_TYPES.MAIN_ENTITY}
                        data-entity-id={mainEntity.entityId}
                      >
                        <div className="mainEntityHeaderIcon">主</div>
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
                      ...subEntities.entities.map((subEntity: AppEntity) => ({
                        ...subEntity,
                        id: subEntity.entityId,
                        type: 'entity',
                        displayName: 'entity'
                      }))
                    ]}
                    setList={() => {}}
                    group={{
                      name: COMPONENT_GROUP_NAME,
                      pull: 'clone',
                      put: false
                    }}
                    sort={false}
                    className="fieldListContent"
                    forceFallback={true}
                    animation={150}
                  >
                    {subEntities.entities.map((subEntity: AppEntity) => (
                      <div
                        className="subEntityHeader"
                        key={subEntity.entityId}
                        onClick={() => setActiveEntityID(subEntity.entityId)}
                        data-cp-type={ENTITY_COMPONENT_TYPES.SUB_ENTITY}
                        data-entity-id={subEntity.entityId}
                      >
                        <div className="subEntityHeaderIcon">子</div>
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

          <div className="fieldHeader">数据字段</div>

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
                <div className="fieldList">
                  <ReactSortable
                    list={field.data || []}
                    setList={() => {}}
                    group={{
                      name: COMPONENT_GROUP_NAME,
                      pull: 'clone',
                      put: false
                    }}
                    sort={false}
                    className="fieldListContent"
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
                        fieldID={item.fieldID}
                        entityID={item.entityID}
                      />
                    ))}
                  </ReactSortable>
                </div>
              </Collapse.Item>
            ))}
          </Collapse>
        </div>
      </Sider>
    </div>
  );
};

export default MetadataContainer;
