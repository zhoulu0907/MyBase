import { Route, Routes } from 'react-router-dom';
import { FormEditor } from './pages/FormEditor';

interface AppProps {
  props: any;
  //   myName: string;
  //   onCountChange?: (count: number) => void;
}

const App: React.FC<AppProps> = ({ props }) => {
  return (
    <>
      <Routes>
        <Route path="/onebase/editor/form_editor" element={<FormEditor />} />
      </Routes>
    </>
  );
};

export default App;
