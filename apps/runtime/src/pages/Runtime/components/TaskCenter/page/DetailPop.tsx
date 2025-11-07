import { useRef, useEffect, useState, type FC } from 'react';
import { Drawer, Grid, Tag, Button, Popconfirm, Tooltip } from '@arco-design/web-react';
import { IconFullscreen, IconLink, IconDoubleRight, IconFullscreenExit } from '@arco-design/web-react/icon';
import ExpendSp from '@/assets/images/task_center/expend-sp.svg';
import ProPreviewImg from '@/assets/images/task_center/process-preview.svg';
import { LISTTYPE, FlowStatusMap, BPMConfigButtonType } from '@onebase/app';
import DetailTable from './DetailTable';
import DetailStep from './DetailStep';
import DetailOKConfirm from './DetailOKConfirm';
import { getFormDetail, getOperatorRecord, fetchExecTask } from '@onebase/app/src/services/app_runtime';
import PreviewContainer from './DetailForm';
const Row = Grid.Row;
const Col = Grid.Col;

interface PageProps {
  detailPopVisible: boolean;
  setPopVisible: Function;
  onBack?: Function;
  taskId?: string;
  rowData?: any;
  listType?: string;
}

const DetailPage: FC<PageProps> = ({ detailPopVisible = false, setPopVisible, onBack, taskId, rowData, listType }) => {
  let [drawWidth, setDrawWidth] = useState<string>('66.66%');
  let [isShowRight, setIsShowRight] = useState(true);
  const [stepData, setStepData] = useState();
  const [detailData, setDetailData] = useState<any>();
  let confirmRef = useRef<any>(null);
  const formRef = useRef<any>(null);

  const [popupVisibleMap, setPopupVisibleMap] = useState({});
  const setPopupVisibleByIndex = (index, visible) => {
    setPopupVisibleMap((prev) => ({
      ...prev,
      [index]: visible
    }));
  };
  function toggleFullScreen(type: string) {
    if (type === 'FULLSCREEN') {
      setDrawWidth('100%');
    } else {
      setDrawWidth('66.66%');
    }
  }
  function renderTitle() {
    return (
      <>
        <span>{rowData?.processTitle} </span>
        <div>
          {drawWidth !== '100%' ? (
            <IconFullscreen onClick={() => toggleFullScreen('FULLSCREEN')} />
          ) : (
            <IconFullscreenExit onClick={() => toggleFullScreen('INITSCREEN')} />
          )}
          <IconLink />
        </div>
      </>
    );
  }

  const fetchExec = async (value: any) => {
    const buttonType = value?.buttonType;
    const entityData = await formRef.current.getFormData();
    try {
      const req = {
        buttonType,
        taskId: rowData?.taskId,
        instanceId: rowData?.instanceId,
        entity: entityData
      };
      await fetchExecTask(req);
      onBack && onBack();
    } catch (error) {}
  };

  const handleConfirmOK = async (value: any) => {
    const entityData = await formRef.current.getFormData();

    confirmRef.current.childMethod({ value, entityData });
  };

  function handlePreview() {
    console.log('handle Preview ...');
  }
  function renderDrawerFooter() {
    return (
      <>
        <Button type="text" onClick={handlePreview}>
          <img src={ProPreviewImg} style={{ marginRight: '3px' }} />
          流程预览
        </Button>
        {detailData?.buttonConfigs &&
          detailData?.buttonConfigs?.map((item, index) => {
            if (!item?.approvalCommentRequired) {
              return (
                <Button
                  type={item?.buttonType === BPMConfigButtonType.APPROVE ? 'primary' : 'outline'}
                  onClick={() => fetchExec(item)}
                >
                  {item?.buttonName}
                </Button>
              );
            } else {
              return (
                <Popconfirm
                  title=""
                  key={index}
                  style={{ maxWidth: '420px', width: '420px' }}
                  className="dt-ok-confirm"
                  content={
                    <DetailOKConfirm
                      ref={confirmRef}
                      onSetPopupVisible={(visible) => setPopupVisibleByIndex(index, visible)}
                      onBack={onBack}
                      taskId={taskId}
                      instanceId={rowData?.instanceId}
                      itemData={item}
                    />
                  }
                  onOk={() => {
                    handleConfirmOK(item);
                  }}
                  popupVisible={!!popupVisibleMap[index]}
                  onCancel={() => setPopupVisibleByIndex(index, false)}
                >
                  <Button
                    type={item?.buttonType === BPMConfigButtonType.APPROVE ? 'primary' : 'outline'}
                    onClick={() => setPopupVisibleByIndex(index, true)}
                  >
                    {item?.buttonName}
                  </Button>
                </Popconfirm>
              );
            }
          })}
      </>
    );
  }

  const fetchStepData = async () => {
    const res = await getOperatorRecord({ instanceId: rowData?.instanceId });
    setStepData(res);
  };
  const fetchDetailData = async () => {
    const res = await getFormDetail({ instanceId: rowData?.instanceId, taskId: rowData?.taskId });
    setDetailData(res);
  };

  useEffect(() => {
    if (listType === LISTTYPE.WILLDO || listType === LISTTYPE.IDONE || listType === LISTTYPE.ICREATED) {
      fetchStepData();
      fetchDetailData();
    } else {
      //根据列表类型请求对应的详情
    }
  }, [listType]);

  return (
    <section>
      <Drawer
        className="draw-detail-pop"
        width={drawWidth}
        title={renderTitle()}
        visible={detailPopVisible}
        footer={listType === LISTTYPE.WILLDO ? renderDrawerFooter() : null}
        onOk={() => {
          setPopVisible(false);
        }}
        onCancel={() => {
          setPopVisible(false);
        }}
      >
        <div className="draw-wrap-box">
          <Row className="header-row" style={{ marginBottom: 16 }}>
            <Col span={6}>
              <p className="gray-color">当前状态</p>
              <div style={{ padding: '4px 0' }}>
                <Tag color="arcoblue" defaultChecked checkable={false}>
                  {detailData?.currentStatus && FlowStatusMap[detailData?.currentStatus]}
                </Tag>
              </div>
            </Col>
            <Col span={6}>
              <p className="gray-color">发起人</p>
              <div className="photo-box">
                <p className="photo-img"></p>
                {detailData?.initiatorName}
              </div>
            </Col>
            <Col span={6}>
              <p className="gray-color">发起部门</p>
              <div className="photo-box">{detailData?.initiatorDeptName}</div>
            </Col>
            <Col span={6}>
              <p className="gray-color">流程版本号</p>
              <div className="photo-box">{detailData?.bpmVersion}</div>
            </Col>
          </Row>
          <div className="draw-content">
            <div className="draw-left">
              <PreviewContainer ref={formRef} pageSetId={rowData?.businessId} detailData={detailData} />
            </div>
            {isShowRight ? (
              <div className="draw-right">
                <div className="arco-drawer-header">
                  <div className="arco-drawer-header-title">
                    <span>审批记录</span>
                    <IconDoubleRight onClick={() => setIsShowRight(false)} />
                  </div>
                </div>
                <DetailStep stepData={stepData} />
              </div>
            ) : (
              <Tooltip position="lt" trigger="hover" content="展开审批记录">
                <div className="expend-sp-box" onClick={() => setIsShowRight(true)}>
                  <img src={ExpendSp} alt="" />
                </div>
              </Tooltip>
            )}
          </div>
        </div>
      </Drawer>
    </section>
  );
};

export default DetailPage;
