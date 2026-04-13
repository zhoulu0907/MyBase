import { Message } from '@arco-design/web-react';
import '@icon-park/react/styles/index.css';
import { NotFoundPage, TokenManager } from '@onebase/common';
import { useEffect, useState } from 'react';
import { Navigate, Route, HashRouter as Router, Routes, useLocation, useMatch, useNavigate } from 'react-router-dom';
import { EditorPage } from './pages/Editor';
import { ETLFlowEditorPage } from './pages/ETLFlowEditor';
import Home from './pages/Home';
import Login from './pages/Login';
import SettingPage from './pages/Setting';
import { getPlatformExports, getPlatform, type PlatformExports } from './products';

// 定义 Callback 组件的 Props 类型
interface CallbackProps {
  navigate?: (path: string, options?: { replace?: boolean }) => void;
}

// 延迟加载的平台组件 - 使用 wrapper 传递 navigate
const LingjiCallback = () => {
  const navigate = useNavigate();
  const [Component, setComponent] = useState<React.ComponentType<CallbackProps> | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    getPlatformExports().then(exports => {
      setLoading(false);
      if (exports.LingjiCallback) {
        setComponent(() => exports.LingjiCallback!);
      } else {
        console.warn('[App] LingjiCallback 组件未找到，当前平台:', exports?.config?.platform);
      }
    });
  }, []);

  if (Component) return <Component navigate={navigate} />;
  if (loading) return <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', minHeight: '100vh' }}>加载中...</div>;
  return <NotFoundPage />;
};

const OAuthCallback = () => {
  const navigate = useNavigate();
  const [Component, setComponent] = useState<React.ComponentType<CallbackProps> | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    getPlatformExports().then(exports => {
      setLoading(false);
      if (exports.TiangongOAuthCallback) {
        setComponent(() => exports.TiangongOAuthCallback!);
      } else {
        console.warn('[App] TiangongOAuthCallback 组件未找到，当前平台:', exports?.config?.platform);
      }
    });
  }, []);

  if (Component) return <Component navigate={navigate} />;
  if (loading) return <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', minHeight: '100vh' }}>加载中...</div>;
  return <NotFoundPage />;
};

function AppContent() {
  Message.config({
    duration: 3000,
    maxCount: 1,
    getContainer: () => document.body
  });

  const location = useLocation();
  const match = useMatch('/onebase/:tenantId/*');
  const tenantId = match?.params.tenantId;
  const [platformExports, setPlatformExports] = useState<PlatformExports | null>(null);

  // 获取当前平台
  const currentPlatform = getPlatform();
  const isLingji = currentPlatform === 'lingji';

  // 加载平台包
  useEffect(() => {
    getPlatformExports().then(exports => {
      console.log('[App] 平台加载完成:', getPlatform());
      setPlatformExports(exports);
    });
  }, []);

  // 应用平台初始化
  useEffect(() => {
    if (!platformExports) return;

    // 灵畿平台初始化监督插件
    if (isLingji) {
      import('@onebase/product-lingji').then(({ initLingjiPlatform }) => {
        initLingjiPlatform();
      });
    }
  }, [platformExports, isLingji]);

  useEffect(() => {
    if (tenantId) {
      TokenManager.setCurIdentifyId(tenantId);

      const existingTenantId = TokenManager.getTenantInfo()?.tenantId;
      if (!existingTenantId) {
        TokenManager.setTenantId(tenantId);
      }
    }
  }, [tenantId, location.pathname]);

  // 灵畿平台的监督插件埋点
  useEffect(() => {
    if (!isLingji || !platformExports) return;

    const { pathname } = location;
    const hidePages = ['/login', '/lingji-callback', '/oauth', '/tenant'];
    const shouldHide = hidePages.some(page => pathname.startsWith(page));

    // 动态导入监督插件工具函数
    import('@onebase/product-lingji').then(({ showPlugin, hidePlugin, isPluginInitialized, updatePageInfo }) => {
      if (shouldHide) {
        hidePlugin();
      } else if (isPluginInitialized()) {
        showPlugin();
        const pathWithoutTenant = pathname.replace(/^\/onebase\/[^/]+/, '');
        const parts = pathWithoutTenant.split('/').filter(Boolean);
        const moduleCode = parts[0] || 'home';
        const menuCode = parts.length >= 2 ? parts.slice(1).join('-') : moduleCode;
        updatePageInfo(moduleCode, menuCode);
      }
    });
  }, [location.pathname, isLingji, platformExports]);

  return (
    <Routes>
      <Route path="/login" element={<Login />} />
      <Route path="/lingji-callback" element={<LingjiCallback />} />
      <Route path="/oauth/obbuilder/:appName" element={<OAuthCallback />} />
      <Route path="/aigen/chat" element={null} />
      <Route
        path="/onebase/:tenantId/home/*"
        element={<Home />}
      />
      <Route
        path="/onebase/:tenantId/setting/*"
        element={<SettingPage />}
      />
      <Route path="/onebase/:tenantId/editor/*" element={<EditorPage />} />
      <Route path="/onebase/:tenantId/etl_editor/*" element={<ETLFlowEditorPage />} />
      <Route path="/" element={<Navigate to="/login" replace />} />
      <Route path="/tenant/:tenantId/*" element={<Login />} />
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