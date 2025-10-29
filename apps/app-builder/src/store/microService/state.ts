import { initGlobalState } from 'qiankun';
const initialState = {
  myName: '',
  subAppCount: 0
};
const globalState = initGlobalState(initialState);

export default globalState;
