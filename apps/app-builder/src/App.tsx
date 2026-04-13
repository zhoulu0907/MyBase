import { Message } from '@arco-design/web-react';
import '@icon-park/react/styles/index.css';
import { NotFoundPage, TokenManager } from '@onebase/common';
import { useEffect, useState } from 'react';
import { Navigate, Route, HashRouter as Router, Routes, useLocation, useMatch, useSearchParams, useNavigate } from 'react-router-dom';
import { initPlugins } from './plugin';
import { EditorPage } from './pages/Editor';
import { ETLFlowEditorPage } from './pages/ETLFlowEditor';
import Home from './pages/Home';
import Login from './pages/Login';
import SettingPage from './pages/Setting';
import { getPlatformExports, getPlatform, type PlatformExports } from './products';

// 延迟加载的平台组件
const LingjiCallback = () => {
  const [Component, setComponent] = useState<React.ComponentType<any> | null>(null);

  useEffect(() => {
    getPlatformExports().then(exports => {
      if (exports.LingjiCallback) {
        setComponent(() => exports.LingjiCallback!);
      }
    });
  }, []);

  if (!Component) return null;
  return <Component />;
};

const OAuthCallback = () => {
  const [Component, setComponent] = useState<React.ComponentType<any> | null>(null);
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();

  useEffect(() => {
    getPlatformExports().then(exports => {
      if (exports.TiangongOAuthCallback) {
        setComponent(() => exports.TiangongOAuthCallback!);
      }
    });
  }, []);

  if (!Component) return null;
  return <Component searchParams={searchParams} navigate={navigate} />;
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
      console.log('[App] 平台包加载完成:', exports?.config?.platform);
      setPlatformExports(exports);
    });
  }, []);

  // 应用平台初始化
  useEffect(() => {
    if (!platformExports) return;

    console.log('[App] 平台包加载完成:', platformExports.config?.platform);

    // 灵畿平台初始化监督插件
    if (platformExports.config?.platform === 'lingji') {
      import('@onebase/product-lingji').then(({ initLingjiPlatform }) => {
        console.log('[App] 初始化灵畿平台...');
        initLingjiPlatform();
      });
    }
  }, [platformExports]);

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