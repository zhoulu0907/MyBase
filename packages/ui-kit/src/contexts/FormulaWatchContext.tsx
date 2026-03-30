import { createContext, useContext } from 'react';

const FormulaWatchContext = createContext<any>(null);

export const FormulaWatchProvider = FormulaWatchContext.Provider;

export const useFormulaWatchContext = () => {
  return useContext(FormulaWatchContext);
};
