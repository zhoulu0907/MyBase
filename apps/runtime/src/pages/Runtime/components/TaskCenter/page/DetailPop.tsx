import { useRef, useEffect, useState, type FC } from 'react';
import { Drawer, Grid, Tag, Button, Popconfirm, Tooltip } from '@arco-design/web-react';
import { IconFullscreen, IconLink, IconDoubleRight, IconFullscreenExit } from '@arco-design/web-react/icon';
import ExpendSp from '@/assets/images/task_center/expend-sp.svg';
import ProPreviewImg from '@/assets/images/task_center/process-preview.svg';
import { LISTTYPE, FlowStatusMap } from '@onebase/app';
import DetailTable from './DetailTable';
import DetailStep from './DetailStep';
import DetailOKConfirm from './DetailOKConfirm';
import { getFormDetail, getOperatorRecord } from '@onebase/app/src/services/app_runtime';

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
  const [popupVisible, setPopupVisible] = useState(false);
  const [stepData, setStepData] = useState();
  const [detailData, setDetailData] = useState();
  let confirmRef = useRef<any>(null);

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
  function handleConfirmOK() {
    confirmRef.current.childMethod();
  }
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
        <Popconfirm
          title=""
          style={{ maxWidth: '420px', width: '420px' }}
          className="dt-ok-confirm"
          content={
            <DetailOKConfirm
              ref={confirmRef}
              setPopupVisible={setPopupVisible}
              onBack={onBack}
              taskId={taskId}
              instanceId={rowData?.instanceId}
            />
          }
          onOk={() => {
            handleConfirmOK();
          }}
          popupVisible={popupVisible}
          onCancel={() => setPopupVisible(false)}
        >
          <Button type="primary" onClick={() => setPopupVisible(true)}>
            同意
          </Button>
        </Popconfirm>
        <Button type="outline" onClick={() => setPopVisible(false)}>
          拒绝
        </Button>
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
              <Row className="" style={{ marginBottom: 16 }}>
                <Col span={12}>
                  <p className="gray-color">申请原因</p>
                  <div className="photo-box">
                    需要采购一批办公用品
                  </div>
                </Col>
              </Row>
              <p className="gray-color photo-box">申请明细</p>
              <DetailTable />
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
