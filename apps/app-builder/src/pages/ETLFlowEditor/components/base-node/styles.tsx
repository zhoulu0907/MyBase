import { IconInfoCircle } from '@douyinfe/semi-icons';
import styled from 'styled-components';

export const NodeWrapperStyle = styled.div`
  align-items: flex-start;
  background-color: #fff;
  display: flex;
  flex-direction: column;
  justify-content: center;
  position: relative;
  width: 100%;
  height: auto;
  border: 2px solid #e5e6eb;
  border-radius: 8px;

  &.selected {
    border: 2px solid #009e9e;
    border-radius: 8px;
  }

  &.error {
    border: 1px solid red;
  }
`;

export const ErrorIcon = () => (
  <IconInfoCircle
    style={{
      position: 'absolute',
      color: 'red',
      left: -6,
      top: -6,
      zIndex: 1,
      background: 'white',
      borderRadius: 8
    }}
  />
);
