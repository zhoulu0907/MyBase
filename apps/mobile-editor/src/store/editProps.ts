import type { EditorProps } from '@/common/props';
import { signal } from '@preact/signals-react';

const eidtProps = signal<EditorProps>({} as EditorProps);

export { eidtProps };
