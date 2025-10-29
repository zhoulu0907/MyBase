import { initGlobalState } from 'qiankun';
const initialState = {
  myName: ''
};
const globalState = initGlobalState(initialState);

export default globalState;
