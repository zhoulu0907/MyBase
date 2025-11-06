import { useState, useMemo } from 'react';
import type { AppItem } from '../types/appItem';

interface UseTableDataReturn<T> {
  tableData: T[];
  displayData: T[];
  searchValue: string;
  currentPage: number;
  setSearchValue: (value: string) => void;
  setCurrentPage: (page: number) => void;
  getEditItem: (key: string | number) => T | undefined;
  removeItem: (key: string | number) => void;
  addItem: (newItem: T) => void;
}

export const useTableData = <T>(initialData : T[]): UseTableDataReturn<T> => {
  const [tableData, setTableData] = useState<T[]>(initialData);
  const [searchValue, setSearchValue] = useState('');
  const [currentPage, setCurrentPage] = useState(1);

  // 过滤逻辑
  const displayData = useMemo(() => {
    if (!searchValue.trim()) return tableData;
    const lowerSearch = searchValue.toLowerCase();
    return tableData.filter(item => 
      item.appName.toLowerCase().includes(lowerSearch) || 
      item.appId.toLowerCase().includes(lowerSearch)
    );
  }, [tableData, searchValue]);

  // 内部新增（生成唯一key）
  const addItem = (newItem: AppItem) => {
    setTableData(prev => [...prev, newItem]);
    setCurrentPage(1); // 新增后回到第一页
  };

  // 获取要编辑的项
  const getEditItem = (key: string | number) => {
    return tableData.find(item => item.key === key);
  };

  // 内部删除
  const removeItem = (key: string | number) => {
    setTableData(prev => prev.filter(item => item.key !== key));
  };

  return {
    tableData,
    displayData,
    searchValue,
    currentPage,
    setSearchValue,
    setCurrentPage,
    getEditItem,
    removeItem,
    addItem
  };
};