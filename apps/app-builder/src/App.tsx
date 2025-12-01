import { Message } from '@arco-design/web-react';
import '@icon-park/react/styles/index.css';
import { NotFoundPage, TokenManager } from '@onebase/common';
import { useEffect } from 'react';
import { Navigate, Route, HashRouter as Router, Routes, useLocation, useMatch } from 'react-router-dom';
import { EditorPage } from './pages/Editor';
import { ETLFlowEditorPage } from './pages/ETLFlowEditor';
import Home from './pages/Home';
import Login from './pages/Login';
import SettingPage from './pages/Setting';

function AppContent() {
  //   // 启用token自动刷新
  //   useTokenRefresh();

  //   // 检查登录状态
  //   const { isChecking } = useAuthCheck();

  //   // 如果正在检查登录状态，显示加载屏幕
  //   if (isChecking) {
  //     return <LoadingScreen />;
  //   }

  Message.config({
    duration: 3000,
    maxCount: 1,
    getContainer: () => document.body
  });

  const location = useLocation();
  // 使用 useMatch 匹配包含 tenantId 的路由模式
  const match = useMatch('/onebase/:tenantId/*');
  const tenantId = match?.params.tenantId;

  useEffect(() => {
    if (tenantId) {
      console.log('set curIdentifyId: ', tenantId);
      TokenManager.setCurIdentifyId(tenantId);
    }
  }, [tenantId, location.pathname]);

  return (
    <Routes>
      {/* 登录页面不需要认证 */}
      <Route path="/login" element={<Login />} />

      {/* 需要认证的路由 */}
      <Route
        path="/onebase/:tenantId/home/*"
        element={
          // <AuthGuard>
          <Home />
          // </AuthGuard>
        }
      />
      <Route
        path="/onebase/:tenantId/setting/*"
        element={
          // <AuthGuard>
          <SettingPage />
          // </AuthGuard>
        }
      />

      <Route path="/onebase/:tenantId/editor/*" element={<EditorPage />} />
      <Route path="/onebase/:tenantId/etl_editor/*" element={<ETLFlowEditorPage />} />

      {/* 默认重定向到登录页 */}
      <Route path="/" element={<Navigate to="/login" replace />} />

      <Route path="/tenant/:tenantId/*" element={<Login />} />

      {/* 404页面 */}
      <Route path="*" element={<NotFoundPage />} />
    </Routes>
  );
}

function App() {
  return (
    <Router>
      <AppContent />
    </Router>
  );
}

export default App;
