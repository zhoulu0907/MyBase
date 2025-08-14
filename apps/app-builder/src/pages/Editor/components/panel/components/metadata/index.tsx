import FieldCard from '@/components/FieldCard';
import { FORM_COMPONENT_TYPES } from '@/constants/componentTypes';
import { useI18n } from '@/hooks/useI18n';
import { COMPONENT_GROUP_NAME } from '@/pages/Editor/utils/const';
import { useAppEntityStore } from '@/store/store_entity';
import { Collapse } from '@arco-design/web-react';
import type { AppEntityField } from '@onebase/app';
import React, { useEffect, useState } from 'react';
import { ReactSortable } from 'react-sortablejs';
import { COMPONENT_MAP } from './component_map';
import styles from './index.module.less';

const CollapseItem = Collapse.Item;

interface MetadataContainerProps {}

const MetadataContainer: React.FC<MetadataContainerProps> = ({}) => {
  const { t } = useI18n();
  const { mainEntity } = useAppEntityStore();

  const [fieldItems, setFieldItems] = useState<
    { id: string; displayName: string; type: string; fieldID: string; entityID: string }[]
  >([]);

  useEffect(() => {
    if (mainEntity.fields.length > 0) {
      console.log(mainEntity.fields);
      const newFieldItems = mainEntity.fields
        .filter((field: AppEntityField) => field.isSystemField === 1)
        .map((field: AppEntityField, index: number) => {
          let cpType = COMPONENT_MAP[field.fieldType];
          if (!cpType) {
            cpType = FORM_COMPONENT_TYPES.INPUT_TEXT;
          }
          return {
            id: `${cpType}-${index}-${Date.now()}`,
            displayName: field.fieldName,
            type: cpType,
            fieldID: field.fieldID,
            entityID: mainEntity.entityID
          };
        })
        .filter((item) => item !== null);

      console.log(newFieldItems);
      setFieldItems(newFieldItems);
    }
  }, [mainEntity]);

  return (
    <div>
      <div className={styles.rightHeader}>{t('editor.metadata')}</div>

      <div className={styles.rightBody}>
        <div className={styles.entityHeader}>业务实体</div>
        <div className={styles.entityListWrapper}>
          <div className={styles.entityList}>
            <Collapse className={styles.entityCollapse} bordered={false} defaultActiveKey={['1']}>
              <CollapseItem
                name="1"
                header={
                  <div className={styles.mainEntityHeader}>
                    <div className={styles.mainEntityHeaderIcon}>主</div>
                    {mainEntity.entityName || '无'}
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
                  backgroundColor: 'white'
                }}
              >
                {/* <div className={styles.subEntityHeader}>
                  <div className={styles.subEntityHeaderIcon}>子</div>
                  活动签到记录表
                </div>
                <div className={styles.relEntityHeader}>
                  <div className={styles.relEntityHeaderIcon}>关联</div>
                  党员信息表
                </div> */}
              </CollapseItem>
            </Collapse>

            {/* <div className={styles.importEntityHeader}>
              <div className={styles.importEntityHeaderIcon}>引入</div>
              党建活动年度统计
            </div>
            <div className={styles.importEntityHeader}>
              <div className={styles.importEntityHeaderIcon}>引入</div>
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
            </Button> */}
          </div>
        </div>

        <div className={styles.fieldHeader}>数据字段</div>

        <div className={styles.fieldList}>
          <ReactSortable
            list={fieldItems}
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
            onClone={(e) => {
              console.log('onClone', e);
            }}
            onEnd={(e) => {
              console.log('onEnd', e);
              const cpType = e.item.getAttribute('data-cp-type');
              console.log('cpType', cpType);
              e.item.id = `${cpType}-${Date.now()}`;

              const newFieldItems = fieldItems.map((c, idx) => ({
                ...c,
                id: `${c.type}-${idx}-${Date.now()}`
              }));

              console.log('newFieldItems', newFieldItems);
              setFieldItems(newFieldItems);
            }}
          >
            {fieldItems.map((item) => (
              <FieldCard
                key={item.id}
                id={item.id || `${item.type}-${Date.now()}`}
                displayName={item.displayName}
                type={item.type}
                fieldID={item.fieldID}
                entityID={item.entityID}
              />
            ))}
          </ReactSortable>
        </div>
      </div>
    </div>
  );
};

export default MetadataContainer;
