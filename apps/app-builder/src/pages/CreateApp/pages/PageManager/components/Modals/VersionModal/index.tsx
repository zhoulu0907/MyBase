import React, { useEffect, useState } from 'react';
import { Modal, Table, Select, Input, Message } from '@arco-design/web-react';
import { getVersionMgmt } from '@onebase/app';
import { getVersionColumns } from './tableColumn';
import { VersionStatus, SortType } from './indexType';
import type { VersionData, VersionModalProps } from './indexType';
import { versionMgmtDelete } from '@onebase/app/src/services';
import EditRemarkModal from '../EditRemarkModal';
import styles from './index.module.less';

export default function VersionModal({
  visible,
  setVisible,
  changeCurrentFlow,
  currentFlowId,
  getVersonList,
  businessUuid
}: VersionModalProps) {
  const [activeStatus, setActiveStatus] = useState<VersionStatus>(VersionStatus.ALL);
  const [sortType, setSortType] = useState<SortType>(SortType.UPDATE_TIME);
  const [versionList, setVersionList] = useState<VersionData[]>([]);
  const [searchKeyword, setSearchKeyword] = useState('');
  const [currentItem, setCurrentItem] = useState<VersionData>({} as VersionData);
  const [editRemarkVisible, setEditRemarkVisible] = useState(false);

  const handleCloseModal = () => {
    setVisible(false);
  };
  const handleView = (record: VersionData) => {
    changeCurrentFlow(record.id);
    handleCloseModal();
  };

  const handleEditRemark = (record: VersionData) => {
    setCurrentItem(record);
    setEditRemarkVisible(true);
  };

  const handleDelete = (record: VersionData) => {
    versionMgmtDelete({ id: record.id }).then(() => {
      Message.success('删除成功');
      getVersionMgmtData(record.id);
      getVersonList();
    });
  };
  const columns = getVersionColumns(handleView, handleEditRemark, handleDelete);
  const getVersionMgmtData = async (deleteId?: string) => {
    if (!businessUuid) {
      return;
    }
    const params = {
      businessUuid,
      sortType,
      bpmVersionStatus: activeStatus === VersionStatus.ALL ? undefined : activeStatus,
      bpmVersionAlias: searchKeyword || undefined
    };
    const { list } = await getVersionMgmt(params);
    if (deleteId === currentFlowId) {
      list[0]?.id && changeCurrentFlow(list[0].id);
    }
    setVersionList(list);
  };
  useEffect(() => {
    visible && getVersionMgmtData();
  }, [visible, activeStatus, sortType]);

  return (
    <Modal
      className={styles.versionModal}
      onCancel={handleCloseModal}
      visible={visible}
      onOk={handleCloseModal}
      footer={null}
      closable={false}
    >
      <div className={styles.searchHeader}>
        <div className={styles.left}>
          {[
            { label: '全部', value: VersionStatus.ALL },
            { label: '已发布', value: VersionStatus.PUBLISHED },
            { label: '设计中', value: VersionStatus.DESIGNING },
            { label: '历史', value: VersionStatus.HISTORY }
          ].map(({ label, value }, index) => (
            <React.Fragment key={value}>
              <span className={activeStatus === value ? styles.active : ''} onClick={() => setActiveStatus(value)}>
                {label}
              </span>
              {index < 3 && <span className={styles.divider}>|</span>}
            </React.Fragment>
          ))}
        </div>
        <div className={styles.right}>
          <Select
            style={{ width: 140, marginRight: 16 }}
            value={sortType}
            onChange={(value) => setSortType(value as SortType)}
            bordered={false}
          >
            <Select.Option value={SortType.UPDATE_TIME}>按更新时间排序</Select.Option>
            <Select.Option value={SortType.CREATE_TIME}>按创建时间排序</Select.Option>
          </Select>
          <Input.Search
            style={{ width: 240 }}
            placeholder="搜索版本备注/版本号"
            onChange={setSearchKeyword}
            onSearch={() => getVersionMgmtData()}
          />
        </div>
      </div>
      <Table columns={columns} data={versionList} rowKey="id" />
      <EditRemarkModal
        visible={editRemarkVisible}
        setVisible={setEditRemarkVisible}
        currentItem={currentItem}
        getVersionMgmtData={getVersionMgmtData}
        getVersonList={getVersonList}
      />
    </Modal>
  );
}
