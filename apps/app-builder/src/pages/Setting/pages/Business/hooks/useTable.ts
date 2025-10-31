import { useState, useMemo } from 'react';
import type { AppItem } from '../types/appItem';

interface UseTableDataReturn {
  tableData: AppItem[];
  displayData: AppItem[];
  searchValue: string;
  currentPage: number;
  // 内部搜索逻辑（仅处理表格过滤）
  setSearchValue: (value: string) => void;
  // 内部分页逻辑
  setCurrentPage: (page: number) => void;
  // 内部编辑（返回要编辑的项）
  getEditItem: (key: string | number) => AppItem | undefined;
  // 内部删除（直接修改表格数据）
  removeItem: (key: string | number) => void;
  // 内部新增（仅在表格数据中添加，如需外部交互可调用此方法后触发回调）
  addItem: (newItem: AppItem) => void;
}

export const useTableData = (initialData: AppItem[] = []): UseTableDataReturn => {
  const [tableData, setTableData] = useState<AppItem[]>(initialData);
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