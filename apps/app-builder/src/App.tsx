import { Message } from '@arco-design/web-react';
import '@icon-park/react/styles/index.css';
import { NotFoundPage, TokenManager } from '@onebase/common';
import { useEffect } from 'react';
import { Navigate, Route, HashRouter as Router, Routes, useLocation, useMatch } from 'react-router-dom';
import { initPlugins } from './plugin';
import { EditorPage } from './pages/Editor';
import { ETLFlowEditorPage } from './pages/ETLFlowEditor';
import Home from './pages/Home';
import Login from './pages/Login';
import LingjiCallback from './pages/LingjiCallback';
import OAuthCallback from './pages/OAuthCallback';
import SettingPage from './pages/Setting';
import { initSupervisionPlugin, updatePageInfo, showPlugin, hidePlugin, extractRouteInfo, isPluginInitialized } from './utils/supervisionPlugin';

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

  // useEffect(() => {
  //   initPlugins();
  // }, []);

  useEffect(() => {
    if (tenantId) {
      TokenManager.setCurIdentifyId(tenantId);

      // 如果 session 中没有 tenant_id，从 URL 获取并存储
      const existingTenantId = TokenManager.getTenantInfo()?.tenantId;
      if (!existingTenantId) {
        TokenManager.setTenantId(tenantId);
      }
      // initPlugins(); // 已在 main.tsx 中提前初始化
    }
  }, [tenantId, location.pathname]);

  // 监督插件初始化 - 在用户登录后初始化
  useEffect(() => {
    const tokenInfo = TokenManager.getToken();
    if (tokenInfo && !isPluginInitialized()) {
      // 尝试初始化，如果用户信息还未加载则重试
      const tryInit = (retries: number) => {
        if (retries <= 0) {
          console.log('[SupervisionPlugin] 初始化重试次数用尽');
          return;
        }

        // 检查配置是否加载
        const config = (window as any).supervision_config;
        if (!config) {
          console.log('[SupervisionPlugin] 配置未加载，等待重试...');
          setTimeout(() => tryInit(retries - 1), 500);
          return;
        }

        initSupervisionPlugin();
      };

      tryInit(5);
    }
  }, [location.pathname]);

  // 监督插件路由埋点监听
  useEffect(() => {
    const { pathname } = location;

    // 判断是否为需要隐藏插件的页面
    const hidePages = ['/login', '/lingji-callback', '/oauth', '/tenant'];
    const shouldHide = hidePages.some(page => pathname.startsWith(page));

    if (shouldHide) {
      hidePlugin();
    } else if (isPluginInitialized()) {
      // 插件已初始化且不在隐藏页面，更新埋点信息
      showPlugin();
      const { moduleCode, menuCode } = extractRouteInfo(pathname);
      updatePageInfo(moduleCode, menuCode);
    }
  }, [location.pathname]);

  return (
    <Routes>
      {/* 登录页面不需要认证 */}
      <Route path="/login" element={<Login />} />
      <Route path="/lingji-callback" element={<LingjiCallback />} />
      <Route path="/oauth/obbuilder/:appName" element={<OAuthCallback />} />
      <Route path="/aigen/chat" element={null} />
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
