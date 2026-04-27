import type { FieldError, FieldState, FieldWarning } from '@flowgram.ai/fixed-layout-editor';
import styled from 'styled-components';

interface StatePanelProps {
  errors?: FieldState['errors'];
  warnings?: FieldState['warnings'];
}

const ErrorStyle = styled.span`
  font-size: 12px;
  color: red;
`;

const WarningStyle = styled.span`
  font-size: 12px;
  color: orange;
`;

export const Feedback = ({ errors, warnings }: StatePanelProps) => {
  const renderFeedbacks = (fs: FieldError[] | FieldWarning[] | undefined) => {
    if (!fs) return null;
    return fs.map((f) => <span key={f.name}>{f.message}</span>);
  };
  return (
    <div>
      <div>
        <ErrorStyle>{renderFeedbacks(errors)}</ErrorStyle>
      </div>
      <div>
        <WarningStyle>{renderFeedbacks(warnings)}</WarningStyle>
      </div>
    </div>
  );
};
