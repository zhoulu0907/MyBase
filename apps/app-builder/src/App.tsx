import "@arco-design/web-react/dist/css/arco.css";
import "@arco-themes/react-onebase/index.less";
import { Navigate, Route, BrowserRouter as Router, Routes } from 'react-router-dom';
import { EditorPage } from "./pages/Editor";
import Home from './pages/Home';
import Login from './pages/Login';
import NotFound from './pages/NotFound';
import SettingPage from "./pages/Setting";


function AppContent() {
//   // 启用token自动刷新
//   useTokenRefresh();

//   // 检查登录状态
//   const { isChecking } = useAuthCheck();

//   // 如果正在检查登录状态，显示加载屏幕
//   if (isChecking) {
//     return <LoadingScreen />;
//   }

  return (
    <Routes>
      {/* 登录页面不需要认证 */}
      <Route path="/login" element={<Login />} />

      {/* 需要认证的路由 */}
      <Route path="/onebase/*" element={
        // <AuthGuard>
          <Home/>
        // </AuthGuard>
      } />
      <Route path="/onebase/setting/*" element={
        // <AuthGuard>
        <SettingPage />
        // </AuthGuard>
      } />

      <Route path="/onebase/editor/*" element={
        <EditorPage />
      } />

      {/* 默认重定向到登录页 */}
      <Route path="/" element={<Navigate to="/login" replace />} />

      {/* 404页面 */}
      <Route path="*" element={<NotFound />} />
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
