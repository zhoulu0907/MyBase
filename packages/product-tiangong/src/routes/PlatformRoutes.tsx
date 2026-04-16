import React from 'react';
import { Route, Routes } from 'react-router-dom';
import { TiangongOAuthCallback } from '../components/OAuthCallback';

/**
 * 天工平台路由
 * 包含天工特有的路由配置
 */
export function PlatformRoutes() {
  return (
    <Routes>
      <Route path="/oauth/obbuilder/:appName" element={<TiangongOAuthCallback />} />
    </Routes>
  );
}