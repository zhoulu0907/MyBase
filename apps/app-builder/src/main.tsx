import '@/assets/icon_application/iconfont.css';
import '@/assets/icon_menu/iconfont.css';
import { StrictMode } from 'react';
import { createRoot } from 'react-dom/client';
import { ErrorBoundary } from 'react-error-boundary';
import App from './App.tsx';
import './i18n';
import './index.css';
import ErrorPage from './pages/ErrorPage/index.tsx';

createRoot(document.getElementById('root')!).render(
  <StrictMode>
    <ErrorBoundary FallbackComponent={ErrorPage}>
      <App />
    </ErrorBoundary>
  </StrictMode>
);
