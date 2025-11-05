import { Editor } from './editor';
import { GlobalConfigProvider } from './components/globalConfig/components/globalConfigProvider';
export const FreeLayout = () => {

  return (
    <div>
      <GlobalConfigProvider>
        <Editor />
      </GlobalConfigProvider>
    </div>
  );
};
