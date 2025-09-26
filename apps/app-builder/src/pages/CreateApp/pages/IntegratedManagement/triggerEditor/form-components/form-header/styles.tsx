import styled from 'styled-components';

export const Header = styled.div`
  box-sizing: border-box;
  width: 100%;
  border-radius: 8px 8px 0 0;
  background: linear-gradient(#f2f2ff 0%, rgb(251, 251, 251) 100%);
  padding: 8px 8px 0;
`;
export const Content = styled.div`
  box-sizing: border-box;
  display: flex;
  justify-content: flex-start;
  align-items: center;
  width: 100%;
  column-gap: 8px;
  overflow: hidden;
`;
export const Footer = styled.div`
  padding-left: 4px;
  width: 100%;
  column-gap: 8px;
  font-weight: 400;
  font-size: 12px;
  line-height: 20px;
  color: #6b7785;
`;

export const Title = styled.div`
  font-size: 20px;
  flex: 1;
  width: 0;
`;

export const Icon = styled.img`
  width: 24px;
  height: 24px;
  scale: 0.8;
  border-radius: 4px;
`;

export const Operators = styled.div`
  display: flex;
  align-items: center;
  column-gap: 4px;
`;
