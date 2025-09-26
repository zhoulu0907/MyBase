import { useState, useCallback } from 'react';

// 定义所有弹窗/抽屉类型
export type ModalType =
  | 'createEntity'
  | 'editEntity'
  | 'configField'
  | 'createRelation'
  | 'createMasterDetail'
  | 'editRelation'
  | 'editField'
  | 'deleteConfirm'
  | 'createField'
  | 'fieldDetail'
  | null;

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
