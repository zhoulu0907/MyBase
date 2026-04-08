import { isArtifexDomain } from '../../utils';
import LingjiApplicationPage from './lingji';
import TiangongApplicationPage from './tiangong';

const ApplicationPage: React.FC = () => {
  const shouldUseTiangong = isArtifexDomain();

  return shouldUseTiangong ? <TiangongApplicationPage /> : <LingjiApplicationPage />;
};

export default ApplicationPage;
