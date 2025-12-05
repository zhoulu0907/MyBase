import { useEffect, useState } from 'react';
import { Route, Routes } from 'react-router-dom';
import type { EditorProps } from './common/props';
import { FormEditor } from './pages/FormEditor';
import { ListEditor } from './pages/ListEditor';

interface AppProps {
  props: {
    onGlobalStateChange: (state: any, prev: any) => void;
    setGlobalState: (state: any) => void;
    offGlobalStateChange: () => void;
  };
}

const App: React.FC<AppProps & { instanceId: string }> = ({ instanceId, props }) => {
  //   useSignals();

  const [previewProps, setPreviewProps] = useState<EditorProps>({} as EditorProps);
  const [dragProps, setDragProps] = useState<EditorProps & { drag: boolean }>({ drag: true } as EditorProps & { drag: boolean });

  useEffect(() => {
    if (props?.onGlobalStateChange) {
      props.onGlobalStateChange((state: any, prev: any) => {
        console.log('子应用接收的参数', state, prev, instanceId);
        if (state.drag) {
          setDragProps(state);
        } else {
          setPreviewProps(state);
        }
        // eidtProps.value = state;
        // setEditProps(state);
      }, true);
    }
  }, [props?.onGlobalStateChange]);

  useEffect(() => {
    console.log('previewProps change: ', previewProps);
  }, [previewProps]);

  useEffect(() => {
    console.log('dragProps change: ', dragProps);
  }, [dragProps]);

  const propsData = (instanceId.indexOf('preview') !== -1 ? previewProps : dragProps) || {};
  return (
    <>
      <Routes>
        <Route path="/onebase/:tenantId/editor/form_editor" element={<FormEditor instanceId={instanceId} props={propsData} />} />
        <Route path="/onebase/:tenantId/editor/list_editor" element={<ListEditor instanceId={instanceId} props={propsData} />} />
      </Routes>
    </>
  );
};

export default App;
