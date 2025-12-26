import { NotFoundPage } from '@onebase/common';
import { Navigate, Route, HashRouter as Router, Routes } from 'react-router-dom';
import Login from './pages/Login';
import ThirdLogin from './pages/ThirdLogin';
import Runtime from './pages/Runtime';
import RuntimeHome from './pages/RuntimeHome';
import RuntimeHomeProtocol from './pages/RuntimeHome/components/Protocol';
import RuntimeHomePrivacy from './pages/RuntimeHome/components/Privacy';
import RuntimeHomeAbout from './pages/RuntimeHome/components/About';
import '@icon-park/react/styles/index.css';
// import Runtime from './pages/Runtime';

function AppContent() {
  return (
    <Routes>
      {/* 登录页面不需要认证 */}
      <Route path="/login" element={<Login />} />
      <Route path='/third/login' element={<ThirdLogin />} />
      
      <Route path="/onebase/runtime/" element={<Runtime />} />

      <Route path="/onebase/:appId/:tenantId/runtime-home/" element={<RuntimeHome />} />
      <Route path="/onebase/runtime-home/protocol" element={<RuntimeHomeProtocol />} />
      <Route path="/onebase/runtime-home/privacy" element={<RuntimeHomePrivacy />} />
      <Route path="/onebase/runtime-home/about" element={<RuntimeHomeAbout />} />

      <Route path="/onebase/:appId/:tenantId/runtime" element={<Runtime />} />

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
