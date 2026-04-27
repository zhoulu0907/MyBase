import type { PageView } from '@onebase/app';
import type { EditMode } from '@onebase/common';
import type { EditConfig } from '@onebase/ui-kit';

interface EditorProps {
  editMode: EditMode;
  setEditMode: (mode: EditMode) => void;

  curComponentID: string;
  setCurComponentID: (cp_id: string) => void;
  clearCurComponentID: () => void;
  setCurComponentSchema: (config: EditConfig) => void;

  pageComponentSchemas: {
    [key: string]: any;
  };
  setPageComponentSchemas: (cp_id: string, config: EditConfig) => void;
  delPageComponentSchemas: (cp_id: string) => void;

  components: any[];
  setComponents: (newComponents: any[]) => void;
  addComponents: (component: any) => void;
  delComponents: (cp_id: string) => void;

  showDeleteButton: boolean;
  setShowDeleteButton: (show: boolean) => void;

  layoutSubComponents: {
    [key: string]: any[][];
  };
  setLayoutSubComponents: (cp_id: string, newColumns: any[][]) => void;
  delLayoutSubComponents: (cp_id: string) => void;

  pageViews: { [key: string]: PageView };
  curViewId: string;
  setCurViewId: (id: string) => void;
  updatePageViewName: (id: string, name: string) => void;
}

export type { EditorProps };
