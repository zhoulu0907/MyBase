import { signal, computed } from '@preact/signals-react';

export interface ChatMessage {
  id: string;
  role: 'user' | 'assistant' | 'system';
  content: string;
  timestamp: number;
}

export const messagesSignal = signal<ChatMessage[]>([]);
export const inputValueSignal = signal<string>('');
export const isLoadingSignal = signal<boolean>(false);

export const chatMessages = computed(() =>
  messagesSignal.value.filter(m => m.role !== 'system')
);

export function setInputValue(value: string) {
  inputValueSignal.value = value;
}

export function addMessage(msg: Omit<ChatMessage, 'id' | 'timestamp'>): string {
  const newMsg: ChatMessage = {
    ...msg,
    id: crypto.randomUUID(),
    timestamp: Date.now(),
  };
  messagesSignal.value = [...messagesSignal.value, newMsg];
  return newMsg.id;
}

export function updateMessage(id: string, content: string) {
  messagesSignal.value = messagesSignal.value.map(msg =>
    msg.id === id ? { ...msg, content } : msg
  );
}

export function appendToMessage(id: string, delta: string) {
  messagesSignal.value = messagesSignal.value.map(msg =>
    msg.id === id ? { ...msg, content: msg.content + delta } : msg
  );
}

export function clearMessages() {
  messagesSignal.value = [];
}