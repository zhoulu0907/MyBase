import { Button, Checkbox, Form, Message, Popconfirm, Space, Table, Tooltip, List, Card } from '@arco-design/web-react';
import { IconPlus, IconRefresh } from '@arco-design/web-react/icon';
import { memo, useEffect, useState } from 'react';
import { useSignals } from '@preact/signals-react/runtime';
import { isRuntimeEnv, menuPermissionSignal, pagesRuntimeSignal } from '@onebase/common';
import { getFileUrlById } from '@onebase/platform-center';
import {
  CATEGORY_TYPE,
  dataMethodDeleteV2,
  dataMethodPageV2,
  deleteFormDataPage,
  DeleteMethodV2Params,
  getEntityFieldsWithChildren,
  getFormDataPage,
  menuSignal,
  PageMethodV2Params,
  PageType,
  queryFlowExecForm,
  TRIGGER_EVENTS,
  VALIDATION_TYPE,
  type AppEntityField
} from '@onebase/app';
import { useFormEditorSignal } from 'src/signals/page_editor';
import { COMPONENT_MAP } from '../../../componentsMap';
import { getComponentSchema } from '../../../schema';
import type { XCardConfig } from './schema';
import { STATUS_OPTIONS, STATUS_VALUES, WIDTH_OPTIONS, WIDTH_VALUES } from '../../../constants';
import PreviewRender from 'src/components/render/PreviewRender';
import CardSearch from './cardSerach';
import './index.css';

type XCardSelectProps = {
  showSelect: boolean;
  defaultSelectedId?: string | number | null;
  onSelectedChange?: (value: any | null, fromDoubleClick?: boolean) => void;
  refreshAfterSelect?: boolean;
  //   隐藏草稿箱
  hiddenDraft?: boolean;
};

const XCard = memo(
  (
    props: XCardConfig & {
      runtime?: boolean;
      preview?: boolean;
      showFromPageData?: Function;
      showAddBtn?: boolean;
      refresh?: number;
      xTableSelectProps?: XCardSelectProps;
      pageSetType?: number;
    }
  ) => {
    useSignals();

    const { pageComponentSchemas: fromPageComponentSchemas, components } = useFormEditorSignal;
    const { menuPermission, canCreate, canEdit, canDelete } = menuPermissionSignal;

    const {
      status,
      runtime = true,
      metaData,
      coverField,
      imageFill,
      titleField,
      showFields,
      columns,
      layout,
      showAddBtn = true,
      searchItems,
      pageSetType,
      cardWidth
    } = props;
    const [form] = Form.useForm();
    const [cardForm] = Form.useForm();

    const [cardData, setCardData] = useState<any[]>([
      { key1: '1', key2: '2' },
      { key1: '1', key2: '2' },
      { key1: '1', key2: '2' },
      { key1: '1', key2: '2' },
      { key1: '1', key2: '2' },
      { key1: '1', key2: '2' },
      { key1: '1', key2: '2' },
      { key1: '1', key2: '2' }
    ]);

    const [mainMetaData, setMainMetaData] = useState<any>({});

    useEffect(() => {
      getMainMetaData();
    }, [metaData]);

    const getMainMetaData = async () => {
      const res = await getEntityFieldsWithChildren(metaData);
      setMainMetaData(res);
    };

    // 新增
    const handleCreate = () => {
      console.log('点击新增');
      if (!runtime) {
        return;
      }
    };
    // 查询
    const handleSearch = () => {};

    // 重置
    const handleReset = () => {};

    const handlePage = async () => {
      if (!runtime || !metaData || !isRuntimeEnv()) {
        return;
      }
    };

    const getSpan = () => {
      if (cardWidth === WIDTH_VALUES[WIDTH_OPTIONS.THIRD]) {
        return 8;
      }
      if (cardWidth === WIDTH_VALUES[WIDTH_OPTIONS.HALF]) {
        return 12;
      }
      if (cardWidth === WIDTH_VALUES[WIDTH_OPTIONS.FULL]) {
        return 24;
      }
      return 6;
    };

    const renderItem = (_record: any, fieldName: string, index: number, isTitle: boolean, column?: any) => {
      const componentSchemasKeys = Object.keys(fromPageComponentSchemas.value || {});
      if (!mainMetaData?.parentFields) {
        return <span>{_record[fieldName]}</span>;
      }
      // 表单配置
      const cpId = componentSchemasKeys.find((ele) => {
        return fromPageComponentSchemas.value[ele]?.config?.dataField?.includes(fieldName);
      });
      if (cpId) {
        // 当前组件配置
        const currentComponentSchemas = fromPageComponentSchemas.value[cpId];
        // 组件类型
        const cpType = currentComponentSchemas.type;
        if (!cpType) {
          return <span>{_record[fieldName]}</span>;
        }
        // 覆盖配置
        let dataField: string[] = [];
        if (Array.isArray(mainMetaData?.parentFields)) {
          const dataFieldInfo = mainMetaData.parentFields.find(
            (field: AppEntityField) => field.fieldName === fieldName
          );

          if (dataFieldInfo && _record[dataFieldInfo.fieldName]) {
            dataField = [mainMetaData.tableName, `${mainMetaData.tableName}.${index}.${dataFieldInfo.fieldName}`];
          }
        }

        const componentConfig = {
          ...currentComponentSchemas,
          config: {
            ...currentComponentSchemas.config,
            dataField:
              dataField?.length > 0
                ? dataField
                : [mainMetaData.tableName, `${mainMetaData.tableName}.${index}.${fieldName}`],
            label: {
              display: !isTitle,
              text: column?.title || currentComponentSchemas.config?.label?.text
            },
            layout: layout,
            status: STATUS_VALUES[STATUS_OPTIONS.DEFAULT],
            verify: { required: false },
            tooltip: ''
          }
        };

        console.log('componentConfig', componentConfig);

        return (
          <PreviewRender
            cpId={fieldName}
            cpType={cpType}
            detailMode={true}
            pageComponentSchema={componentConfig}
            runtime={true}
            recordId={_record.id}
          />
        );
      }

      // 系统字段 表单配置里没有就根据字段类型获取默认配置
      if (mainMetaData?.parentFields?.length) {
        const dataFieldInfo = mainMetaData.parentFields.find((field: AppEntityField) => field.fieldName === fieldName);
        const cpType = dataFieldInfo?.fieldType ? COMPONENT_MAP[dataFieldInfo.fieldType] : null;
        if (cpType) {
          const basicConfig = getComponentSchema(cpType as any);
          const componentConfig = {
            ...basicConfig,
            config: {
              ...basicConfig.config,
              dataField: [mainMetaData.tableName, `${mainMetaData.tableName}.${index}.${dataFieldInfo.fieldName}`],
              label: {
                display: !isTitle,
                text: fieldName
              },
              verify: { required: false },
              tooltip: '',
              layout: layout
            }
          };
          return (
            <PreviewRender
              cpId={fieldName}
              cpType={cpType}
              detailMode={true}
              pageComponentSchema={componentConfig}
              runtime={true}
              recordId={_record.id}
            />
          );
        }
      }

      return <span>{_record[fieldName]}</span>;
    };

    return (
      <div
        style={{
          opacity: status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN] ? 0.4 : 1,
          display: runtime && status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN] ? 'none' : 'unset'
        }}
      >
        <div className="cardHeader">
          {searchItems?.length ? (
            <div className="searchGroup">
              <Form form={form} layout="vertical" className="searchItems" labelAlign="left">
                <CardSearch
                  searchItems={searchItems}
                  labelColSpan={100}
                  runtime={runtime}
                  onSearch={handleSearch}
                  onReset={handleReset}
                  pageSetType={pageSetType}
                />
              </Form>
            </div>
          ) : null}
          <div className="headerActions">
            <div className="addButton">
              {showAddBtn && canCreate.value && (
                <Button type="primary" onClick={handleCreate} icon={<IconPlus />}>
                  添加数据
                </Button>
              )}

              {/* todo 草稿 */}
            </div>
            <Button type="text" onClick={() => handlePage()} icon={<IconRefresh />}></Button>
          </div>
        </div>
        <div className="cardContent">
          {/* 滚动加载 */}
          <Form
            form={cardForm}
            labelCol={layout === 'horizontal' ? { span: 10 } : {}}
            wrapperCol={layout === 'horizontal' ? { span: 14 } : {}}
          >
            <List
              bordered={false}
              dataSource={cardData}
              grid={{ span: getSpan(), gutter: [20, 20] }}
              render={(item, index) => (
                <Card
                  className="card"
                  bordered={false}
                  cover={
                    coverField ? (
                      <img
                        style={{ width: '100%', height: '128px', objectFit: imageFill || 'fill' }}
                        src={getFileUrlById(item[coverField]?.[0]?.id)}
                        alt=""
                      />
                    ) : undefined
                  }
                >
                  <Card.Meta
                    title={titleField ? renderItem(item, titleField, index, true) : undefined}
                    description={
                      showFields ? (
                        <>
                          {columns?.map((ele, i) => (
                            <div key={`${index}-${i}`}>{renderItem(item, ele.dataIndex, index, false, ele)}</div>
                          ))}
                        </>
                      ) : undefined
                    }
                  />
                </Card>
              )}
            ></List>
          </Form>
        </div>
      </div>
    );
  }
);

export default XCard;
