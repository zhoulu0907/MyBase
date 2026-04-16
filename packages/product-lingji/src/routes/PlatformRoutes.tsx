import React from 'react';
import { Route, Routes } from 'react-router-dom';
import { LingjiCallback } from '../components/Callback';

/**
 * 灵畿平台路由
 * 包含灵畿特有的路由配置
 */
export function PlatformRoutes() {
  return (
    <Routes>
      <Route path="/lingji-callback" element={<LingjiCallback />} />
    </Routes>
  );
}