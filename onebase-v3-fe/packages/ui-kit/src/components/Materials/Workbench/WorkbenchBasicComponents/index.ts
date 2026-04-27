import type { ComponentType } from 'react';

import type { WorkbenchComponentType } from '../core/componentTypes';
import XQuickEntry from './QuickEntry';
import XTodoCenter from './TodoCenter';
import XRichTextEditorWorkbench from './RichTextEditorWorkbench';
import XInformationList from './InformationList';
import XTodoList from './TodoList';
import XWelcomeCard from './WelcomeCard';
import XButtonWorkbench from './ButtonWorkbench';
import XImageWorkbench from './ImageWorkbench';
import XChatbot from './Chatbot';
import XChatbotAgent from './ChatbotAgent';

export const WorkbenchBasicComp = {
  XQuickEntry,
  XTodoCenter,
  XRichTextEditorWorkbench,
  XInformationList,
  XTodoList,
  XWelcomeCard,
  XButtonWorkbench,
  XImageWorkbench,
  XChatbot,
  XChatbotAgent
} satisfies Record<WorkbenchComponentType, ComponentType<any>>;

