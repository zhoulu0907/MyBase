import ClearIcon from '@/assets/images/clear_icon.svg';
import draftIcon from '@/assets/images/draft_icon.svg';
import CustomNav from '@/components/Nav';
import {
  Badge,
  Button,
  Dialog,
  Ellipsis,
  LoadMore,
  NoticeBar,
  PopupSwiper,
  Sticky,
  Toast
} from '@arco-design/mobile-react';
import { useForm } from '@arco-design/mobile-react/esm/form';
import { IconWarnCircle } from '@arco-design/mobile-react/esm/icon';
import {
  AppEntityField,
  deleteDraft,
  deleteDraftTable,
  getDraftPage,
  getEntityFieldsWithChildren,
  menuSignal
} from '@onebase/app';
import { isRuntimeEnv } from '@onebase/common';
import { ENTITY_FIELD_TYPE } from '@onebase/ui-kit';
import dayjs from 'dayjs';
import { cloneDeep } from 'lodash-es';
import { useEffect, useRef, useState } from 'react';
import './index.css';

interface IProps {
  onlyIcon?: boolean;
  showFromPageData?: Function;
  metaData: any;
  tableName: string;
  refresh?: number;
  tableColumns?: any[];
}

/**
 * 草稿箱组件
 */
export default function DraftBox(props: IProps) {
  const { metaData, tableName, tableColumns, refresh } = props;

  const [draftForm] = useForm();

  const { curMenu } = menuSignal;

  const [draftData, setDraftData] = useState<any[]>([]);
  const [originDraftData, setOriginDraftData] = useState<any[]>([]);
  const [draftPageNo, setDraftPageNo] = useState(1);
  const [draftTotal, setDraftTotal] = useState(0);
  const [pageSize, _setPageSize] = useState(5);
  const [loading, setLoading] = useState(false);
  const [visible, setVisible] = useState(false);
  const [localMainMetaData, setLocalMainMetaData] = useState<AppEntityField[]>();

  const popupContentRef = useRef(null);

  const onReachBottom = (cb: Function) => {
    if (!draftData.length) return;
    setDraftPageNo((prevPageNo) => prevPageNo + 1);
    cb('prepare');
  };

  useEffect(() => {
    handleGetDrafts();
  }, [tableName, curMenu.value?.id, refresh, draftPageNo]);

  const getMainMetaData = async () => {
    if (localMainMetaData) {
      return localMainMetaData;
    }

    const result = await getEntityFieldsWithChildren(metaData);
    setLocalMainMetaData(result);
    return result;
  };

  // 打开草稿箱
  const handleGetDrafts = async () => {
    try {
      if (!tableName || !curMenu.value?.id || !isRuntimeEnv()) return;

      if (loading) return;

      setLoading(true);

      const res = await getDraftPage(tableName, curMenu.value?.id, { pageNo: draftPageNo, pageSize });
      const mainMetaData = await getMainMetaData();

      const { list = [], total = 0 } = res || {};

      const cloneList = cloneDeep(list);

      const newTableData = await Promise.all(
        (cloneList || []).map(async (item: any) => {
          const newItem = item;
          for (const [key, value] of Object.entries(newItem)) {
            // 优化：减少重复查找，提升可读性和性能
            if (Array.isArray(mainMetaData?.parentFields)) {
              const dataField = mainMetaData.parentFields.find(
                (field: AppEntityField) => field.fieldName === key && field.fieldType === ENTITY_FIELD_TYPE.DATE.VALUE
              );
              if (dataField && newItem[key]) {
                // 仅当字段类型为日期且有值时格式化
                const dateValue = new Date(newItem[key]);
                if (!isNaN(dateValue.getTime())) {
                  newItem[key] = dayjs(dateValue).format('YYYY-MM-DD');
                }
              }

              const datatimeField = mainMetaData.parentFields.find(
                (field: AppEntityField) =>
                  field.fieldName === key && field.fieldType === ENTITY_FIELD_TYPE.DATETIME.VALUE
              );
              if (datatimeField && newItem[key]) {
                // 仅当字段类型为日期且有值时格式化
                const dateValue = new Date(newItem[key]);
                if (!isNaN(dateValue.getTime())) {
                  newItem[key] = dayjs(dateValue).format('YYYY-MM-DD HH:mm:ss');
                }
              }

              // 多选字段回显 逗号分割
              const multiSelectField = mainMetaData.parentFields.find(
                (field: AppEntityField) =>
                  field.fieldName === key && field.fieldType === ENTITY_FIELD_TYPE.MULTI_SELECT.VALUE
              );
              if (multiSelectField && newItem[key] && Array.isArray(newItem[key])) {
                newItem[key] = newItem[key]
                  .map((v) => v.name)
                  .filter(Boolean)
                  .join('，');
              }

              // 人员选择单选
              const userSelectField = mainMetaData.parentFields.find(
                (field: AppEntityField) => field.fieldName === key && field.fieldType === ENTITY_FIELD_TYPE.USER.VALUE
              );
              if (userSelectField && newItem[key]) {
                if (newItem[key]) {
                  newItem[key] = newItem[key].name;
                }
              }

              // 部门
              const departmentField = mainMetaData.parentFields.find(
                (field: AppEntityField) =>
                  field.fieldName === key && field.fieldType === ENTITY_FIELD_TYPE.DEPARTMENT.VALUE
              );
              if (departmentField && newItem[key]) {
                newItem[key] = newItem[key].name || '-';
              }

              // 开关
              const switchField = mainMetaData.parentFields.find(
                (field: AppEntityField) =>
                  field.fieldName === key && field.fieldType === ENTITY_FIELD_TYPE.BOOLEAN.VALUE
              );
              if (switchField && typeof newItem[key] === 'boolean') {
                newItem[key] = newItem[key] ? '是' : '否';
              }

              // 单选列表 - 根据id返回对应label
              const selectField = mainMetaData.parentFields.find(
                (field: AppEntityField) => field.fieldName === key && field.fieldType === ENTITY_FIELD_TYPE.SELECT.VALUE
              );

              if (selectField) {
                newItem[key] = newItem[key]?.name || '-';
              }

              // 数据选择
              const dateField = mainMetaData.parentFields.find(
                (field: AppEntityField) =>
                  field.fieldName === key && field.fieldType === ENTITY_FIELD_TYPE.DATA_SELECTION.VALUE
              );
              if (dateField) {
                newItem[key] = newItem[key].name || '-';
              }

              // 文件上传
              const fileField = mainMetaData.parentFields.find(
                (field: AppEntityField) => field.fieldName === key && field.fieldType === ENTITY_FIELD_TYPE.FILE.VALUE
              );
              if (fileField) {
                newItem[key] = newItem[key] || [];
              }
            }
          }

          return {
            ...newItem,
            key: item.id
          };
        })
      );

      setLoading(false);
      setOriginDraftData(draftPageNo === 1 ? list : [...originDraftData, ...list]);
      setDraftData(draftPageNo === 1 ? newTableData : [...draftData, ...newTableData]);
      draftForm.setFieldsValue({ [tableName]: newTableData });
      setDraftTotal(+total);
    } catch (error) {}
  };

  // 删除草稿 or 一键清空草稿箱
  const handleDelete = async (id: string) => {
    const deleteAll = id === 'all';
    (globalThis as any).modalInstance = Dialog.confirm({
      title: `确认${deleteAll ? '清空所有' : '删除'}草稿？`,
      children: '此操作不可撤销',
      okText: <div style={{ color: '#F53F3F' }}>确定</div>,
      cancelText: '取消',
      maskStyle: {
        zIndex: 1001
      },
      getContainer: () => document.body,
      onOk: () =>
        new Promise(async (resolve) => {
          if (deleteAll) {
            await deleteDraftTable(tableName, curMenu.value?.id);
          } else {
            await deleteDraft(tableName, curMenu.value?.id, { id });
          }
          setDraftPageNo(1);
          Toast.success('删除成功');
          resolve(void 0);
        }) as any
    });
  };

  // 载入草稿
  const handleLoadDraft = (draft: any) => {
    const curOriginDraftData = originDraftData.find((data) => data.id === draft.id);
    localStorage.setItem('draftData', JSON.stringify(curOriginDraftData || {}));

    // 打开编辑弹窗（新增模式）
    props?.showFromPageData?.('', true);
  };

  const getItemBtns = (item: any) => {
    return (
      <div className="list-body-item-btns">
        <Button
          color="#1D2129"
          borderColor="#86909C"
          type="ghost"
          size="mini"
          className="list-body-item-btn"
          onClick={() => handleDelete(item.id)}
        >
          删除
        </Button>
        <Button type="primary" size="mini" className="list-body-item-btn" onClick={() => handleLoadDraft(item)}>
          继续编辑
        </Button>
      </div>
    );
  };

  const getBottomBar = () => {
    if (!loading && !draftData.length && draftTotal === 0) {
      return <div className="no-data">暂无数据</div>;
    }

    if (loading || draftPageNo * pageSize >= (draftTotal || Number.MAX_SAFE_INTEGER)) {
      return draftTotal ? <div className="total-data">共{draftTotal}条数据</div> : null;
    }

    return (
      <LoadMore
        getData={onReachBottom}
        getDataAtFirst={false}
        threshold={200}
        blockWhenLoading={false}
        throttle={300}
        getScrollContainer={() => popupContentRef.current}
      />
    );
  };

  const pageTitle = `草稿箱${draftTotal ? `（${draftTotal}）` : ''}`;

  return (
    <>
      <div
        className="draft"
        onClick={() => {
          setVisible(true);
        }}
      >
        <Badge absolute text={draftTotal} style={{ backgroundColor: 'rgb(var(--primary-6))' }} />
        <img className="draft-icon" src={draftIcon} alt="" />
      </div>
      <PopupSwiper visible={visible} close={() => setVisible(false)} direction="bottom">
        <div style={{ height: '100dvh' }}>
          <div className="loadmore-list-wrapper-OBMobile draft-box">
            <Sticky topOffset={0}>
              <CustomNav title={pageTitle} style={{ background: '#fff' }} toBack={() => setVisible(false)} />
              <div className="draft-noticebar">
                <NoticeBar
                  closeable={false}
                  leftContent={<IconWarnCircle />}
                  style={{
                    width: '100%',
                    marginRight: '0.14rem',
                    color: '#4E5969',
                    borderRadius: '0.04rem',
                    backgroundColor: 'rgb(var(--primary-1))'
                  }}
                >
                  90天内未更新的草稿将被自动删除
                </NoticeBar>
                <img className="draft-icon" src={ClearIcon} alt="" onClick={() => handleDelete('all')} />
              </div>
            </Sticky>
            <div className="list-body-wrapper" ref={popupContentRef}>
              {draftData.map((item, index) => (
                <div key={index} className="list-body-item-wrapper" onClick={() => {}}>
                  {(tableColumns?.length ? tableColumns : [{}, {}])?.map((col: any, index: number) => {
                    return (
                      <div className="list-body-item-element" key={index}>
                        <Ellipsis className="list-body-item-title" text={(col.title || '') + '：'} />
                        <Ellipsis className="list-body-item-content" text={col.render?.(item, index)} />
                      </div>
                    );
                  })}
                  {getItemBtns(item)}
                </div>
              ))}
              {getBottomBar()}
            </div>
          </div>
        </div>
      </PopupSwiper>
    </>
  );
}
