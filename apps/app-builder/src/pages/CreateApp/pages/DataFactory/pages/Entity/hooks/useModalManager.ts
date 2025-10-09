import { useState, useCallback } from 'react';

export const MODAL_TYPE = {
  CREATE_ENTITY: 'createEntity',
  EDIT_ENTITY: 'editEntity',
  CONFIG_FIELD: 'configField',
  CREATE_RELATION: 'createRelation',
  CREATE_MASTER_DETAIL: 'createMasterDetail',
  EDIT_RELATION: 'editRelation',
  EDIT_FIELD: 'editField',
  DELETE_CONFIRM: 'deleteConfirm',
  CREATE_FIELD: 'createField',
  FIELD_DETAIL: 'fieldDetail'
};

// 定义所有弹窗/抽屉类型
export type ModalType = (typeof MODAL_TYPE)[keyof typeof MODAL_TYPE] | null;

// 弹窗/抽屉管理器Hook
export const useModalManager = () => {
  const [activeModal, setActiveModal] = useState<ModalType>(null);
  const [modalData, setModalData] = useState<Record<string, unknown>>({});

  // 打开指定的弹窗/抽屉，自动关闭其他所有弹窗/抽屉
  const openModal = useCallback((type: ModalType, data?: Record<string, unknown>) => {
    setActiveModal(type);
    if (data) {
      setModalData(data);
    } else {
      setModalData({});
    }
  }, []);

  // 关闭当前弹窗/抽屉
  const closeModal = useCallback(() => {
    setActiveModal(null);
    setModalData({});
  }, []);

  // 关闭所有弹窗/抽屉
  const closeAllModals = useCallback(() => {
    setActiveModal(null);
    setModalData({});
  }, []);

  // 检查指定弹窗/抽屉是否打开
  const isModalOpen = useCallback(
    (type: ModalType) => {
      return activeModal === type;
    },
    [activeModal]
  );

  // 获取当前弹窗/抽屉的数据
  const getModalData = useCallback(
    (key: string) => {
      return modalData[key];
    },
    [modalData]
  );

  // 设置弹窗/抽屉数据
  const setModalDataValue = useCallback((key: string, value: unknown) => {
    setModalData((prev) => ({
      ...prev,
      [key]: value
    }));
  }, []);

  return {
    activeModal,
    modalData,
    openModal,
    closeModal,
    closeAllModals,
    isModalOpen,
    getModalData,
    setModalDataValue
  };
};
