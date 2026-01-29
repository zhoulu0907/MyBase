import React from 'react';
import CodeMirrorEditor from '@/components/CodeMirrorEditor';

interface MonacoWrapperProps {
    value?: string;
    onChange?: (value: string) => void;
    language?: string;
    readOnly?: boolean;
    height?: string | number;
}

const MonacoWrapper: React.FC<MonacoWrapperProps> = ({
    value = '',
    onChange,
    // language = 'javascript',
    readOnly = false,
    height = 400
}) => {
    // Adapter to match CodeMirrorEditor props
    // CodeMirrorEditor has: value, onChange, theme, readOnly, placeholder, className, etc.

    // Note: The existing CodeMirrorEditor doesn't seem to accept 'language' prop explicitly in the visible interface 
    // but might use extensions internally. For now we just pass value/change.
    // If language support is needed, we might need to extend CodeMirrorEditor or pass extensions.

    return (
        <div style={{ border: '1px solid var(--color-border)', borderRadius: 4, height: height, overflow: 'hidden' }}>
            <CodeMirrorEditor
                value={value}
                onChange={onChange || (() => { })}
                readOnly={readOnly}
            // TODO: Pass language extension if supported by CodeMirrorEditor
            />
        </div>
    );
};

export default MonacoWrapper;
