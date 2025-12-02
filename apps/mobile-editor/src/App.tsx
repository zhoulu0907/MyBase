import { useEffect, useState } from 'react';
import { Route, Routes } from 'react-router-dom';
import type { EditorProps } from './common/props';
import { FormEditor } from './pages/FormEditor';
import { eidtProps } from './store/editProps';

interface AppProps {
  props: {
    onGlobalStateChange: (state: any, prev: any) => void;
    setGlobalState: (state: any) => void;
    offGlobalStateChange: () => void;
  };
}

const App: React.FC<AppProps> = ({ props }) => {
  //   useSignals();

  const [editProps, setEditProps] = useState<EditorProps>({} as EditorProps);

  useEffect(() => {
    if (props?.onGlobalStateChange) {
      props.onGlobalStateChange((state: any, prev: any) => {
        console.log('子应用接收的参数', state, prev);
        // eidtProps.value = state;
        setEditProps(state);
      }, true);
    }
  }, [props?.onGlobalStateChange]);

  useEffect(() => {
    console.log('eidtProps change: ', eidtProps.value);
  }, [eidtProps.value]);

  return (
    <>
      <Routes>
        <Route path="/onebase/editor/form_editor" element={<FormEditor props={editProps} />} />
      </Routes>
    </>
  );
};

export default App;
