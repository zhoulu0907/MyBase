import { useRef, useEffect, useState, type FC } from 'react';
import { Drawer, Grid, Tag, Button, Popconfirm, Tooltip } from '@arco-design/web-react';
import { IconFullscreen, IconLink, IconDoubleRight, IconFullscreenExit } from '@arco-design/web-react/icon';
import ExpendSp from '@/assets/images/task_center/expend-sp.svg';
import ProPreviewImg from '@/assets/images/task_center/process-preview.svg';
import { LISTTYPE, FlowStatusMap, BPMConfigButtonType, FLOWSTATUS_TYPE } from '@onebase/app';
import DetailStep from './DetailStep';
import DetailOKConfirm from './DetailOKConfirm';
import { getFormDetail, getOperatorRecord, fetchExecTask } from '@onebase/app/src/services/app_runtime';
import PreviewContainer from './DetailForm';
import FlowView from '../../../../../../../app-builder/src/pages/Editor/components/flowView';
import { type FetchExecTaskReq } from '@onebase/app';
const Row = Grid.Row;
const Col = Grid.Col;

enum PageTypeMap {
  willdo = 'todo',
  idone = 'done',
  icreated = 'created',
  icopied = 'cc',
  list = 'list'
}

interface PageProps {
  detailPopVisible: boolean;
  setPopVisible: (visible: boolean) => void;
  onBack?: () => void;
  rowData?: any;
  listType?: string;
}

const DetailPage: React.FC<PageProps> = ({ detailPopVisible = false, setPopVisible, onBack, rowData, listType }) => {
  let [drawWidth, setDrawWidth] = useState<string>('66.66%');
  let [isShowRight, setIsShowRight] = useState(true);
  const [stepData, setStepData] = useState();
  const [detailData, setDetailData] = useState<any>();
  const [flowViewVisible, setFlowViewVisible] = useState(false);
  let confirmRef = useRef<any>(null);
  const formRef = useRef<any>(null);

  const [popupVisibleMap, setPopupVisibleMap] = useState<any>({});
  const setPopupVisibleByIndex = (index: number, visible: boolean) => {
    setPopupVisibleMap((prev: any) => {
      if (visible) {
        const newState: any = {};
        Object.keys(prev).forEach((key) => {
          newState[key] = false;
        });
        newState[index] = true;
        return newState;
      } else {
        return {
          ...prev,
          [index]: false
        };
      }
    });
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
        <span>{detailData?.processTitle}</span>
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
    const fieldData = await formRef.current.getFormData();
    try {
      const req: FetchExecTaskReq = {
        buttonType,
        taskId: detailData?.taskId,
        instanceId: rowData?.instanceId,
        entity: {
          tableName: detailData?.formData?.tableName,
          data: fieldData
        }
      };
      await fetchExecTask(req);
      onBack && onBack();
    } catch (error) {}
  };

  const handleConfirmOK = async (value: any) => {
    const fieldData = await formRef.current.getFormData();
    const entityData = {
      tableName: detailData?.formData?.tableName,
      data: fieldData
    };
    if (confirmRef?.current?.childMethod) {
      confirmRef.current.childMethod({ value, entityData });
    }
  };

  function handlePreview() {
    setFlowViewVisible(true);
  }
  function renderDrawerFooter() {
    return (
      <>
        <Button type="text" onClick={handlePreview}>
          <img src={ProPreviewImg} style={{ marginRight: '3px' }} />
          流程预览
        </Button>
        {detailData?.buttonConfigs &&
          detailData?.buttonConfigs?.map((item: any, index: number) => {
            if (
              item?.buttonType === BPMConfigButtonType.SAVE ||
              item?.buttonType === BPMConfigButtonType.SUBMIT ||
              item?.buttonType === BPMConfigButtonType.WITHDRAW
            ) {
              return (
                <Button
                  key={index}
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
                      onSetPopupVisible={(visible: any) => setPopupVisibleByIndex(index, visible)}
                      onBack={onBack}
                      taskId={detailData?.taskId}
                      instanceId={rowData?.instanceId}
                      itemData={item}
                      isRequired={item?.approvalCommentRequired}
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
    const res = await getFormDetail({
      instanceId: rowData?.instanceId,
      taskId: rowData?.taskId,
      from: PageTypeMap[listType as keyof typeof PageTypeMap]
    });
    setDetailData(res);
  };

  const getColor = (status: string) => {
    switch (status) {
      case FLOWSTATUS_TYPE.IN_APPROVAL:
        return 'blue';
      case FLOWSTATUS_TYPE.APPROVED:
        return 'green';
      case FLOWSTATUS_TYPE.DRAFT:
        return 'gray';
      case FLOWSTATUS_TYPE.REJECTED:
      case FLOWSTATUS_TYPE.WITHDRAWN:
      case FLOWSTATUS_TYPE.TERMINATED:
        return 'red';
      default:
        return 'gray';
    }
  };

  useEffect(() => {
    if (
      listType === LISTTYPE.WILLDO ||
      listType === LISTTYPE.IDONE ||
      listType === LISTTYPE.ICREATED ||
      listType === LISTTYPE.ICOPIED||
       listType === LISTTYPE.LIST
    ) {
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
        footer={renderDrawerFooter()}
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
                <Tag color={getColor(detailData?.currentStatus)} defaultChecked checkable={false}>
                  {detailData?.currentStatus && FlowStatusMap[detailData?.currentStatus]}
                </Tag>
              </div>
            </Col>
            <Col span={6}>
              <p className="gray-color">发起人</p>
              <div className="photo-box">
                <p className="photo-img">
                  {detailData?.initiator?.avatar ? (
                    <img src={detailData?.initiator?.avatar} alt="" />
                  ) : (
                    detailData?.initiatorName?.charAt(0)
                  )}
                </p>
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
              <PreviewContainer ref={formRef} pageSetId={rowData?.pageSetId} detailData={detailData} />
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
      <FlowView
        visible={flowViewVisible}
        setVisible={setFlowViewVisible}
        instanceId={rowData?.instanceId}
        businessUuid={rowData?.businessUuid}
      />
    </section>
  );
};

export default DetailPage;
