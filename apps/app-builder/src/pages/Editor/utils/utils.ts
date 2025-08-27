import { useLocation } from 'react-router-dom';
import { EDITOR_TYPES } from "./const";

export function isListEditorPage(): boolean {
  const path = useLocation().pathname;
  return path.endsWith(`/${EDITOR_TYPES.LIST_EDITOR}`);
}