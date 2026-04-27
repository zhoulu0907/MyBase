import { isTiangongPlatform } from '@/products';
import LingjiApplicationPage from './lingji';
import TiangongApplicationPage from './tiangong';

const ApplicationPage: React.FC = () => {
  const shouldUseTiangong = isTiangongPlatform();

  return shouldUseTiangong ? <TiangongApplicationPage /> : <LingjiApplicationPage />;
};

export default ApplicationPage;
