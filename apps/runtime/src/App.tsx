import '@icon-park/react/styles/index.css';
import { NotFoundPage } from '@onebase/common';
import { Navigate, Route, HashRouter as Router, Routes } from 'react-router-dom';
import Login from './pages/Login';
import MyAppPage from './pages/MyApp';
import Runtime from './pages/Runtime';
import SettingPage from './pages/Setting';
// import Runtime from './pages/Runtime';

function AppContent() {
  return (
    <Routes>
      {/* 登录页面不需要认证 */}
      <Route path="/login" element={<Login />} />
      <Route path="/:appId/:tenantId/login" element={<Login />} />

      <Route path="/onebase/runtime/" element={<Runtime />} />
      <Route path="/onebase/runtime/:appId/" element={<Runtime />} />
      <Route path="/onebase/runtime/:appId/:tenantId" element={<Runtime />} />
      <Route path="/onebase/runtime/my-app" element={<MyAppPage />} />
      <Route path="/onebase/setting/*" element={<SettingPage />} />
      {/* 默认重定向到登录页 */}
      <Route path="/" element={<Navigate to="/login" replace />} />

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
