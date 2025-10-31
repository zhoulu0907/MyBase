import { StrictMode } from 'react';
import { createRoot, type Root } from 'react-dom/client';
import { HashRouter } from 'react-router-dom';
import { qiankunWindow, renderWithQiankun } from 'vite-plugin-qiankun/dist/helper';
import App from './App.tsx';
import './index.css';

const containerMap = new WeakMap<HTMLElement, Root>();

async function bootstrap() {
  console.log('sub-app bootstrap');
}

async function mount(props: any) {
  console.log('sub-app mount', props);

  const container = props?.container ? props.container : document.getElementById('root')!;

  const containerElement = container.querySelector('#root') || container;

  const root = createRoot(containerElement);

  root.render(
    <StrictMode>
      <HashRouter>
        <App props={props} />
      </HashRouter>
    </StrictMode>
  );

  containerMap.set(container as HTMLElement, root);
}

async function unmount(props: any) {
  console.log('sub-app unmount');

  const container = props?.container ? props.container : document.getElementById('root')!;

  const root = containerMap.get(container as HTMLElement);
  root?.unmount();
}

renderWithQiankun({
  bootstrap,
  mount,
  unmount,
  update(props: any) {
    console.log('sub-app update', props);
    // 重新渲染以更新 props
    // const container = props?.container ? props.container : document.getElementById('root')!;
    // const root = containerMap.get(container as HTMLElement);
    // if (root) {
    //   root.render(
    //     <StrictMode>
    //       <App props={props} />
    //     </StrictMode>
    //   );
    // }
  }
});

if (!qiankunWindow.__POWERED_BY_QIANKUN__) {
  bootstrap().then(() => mount({}));
}
