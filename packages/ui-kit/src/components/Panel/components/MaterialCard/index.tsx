import React from 'react';
import { ICON_Map } from './icons';
import './index.css';

interface MaterialCardProps {
  displayName: string;
  type: string;
  icon: string;
  id: string;
}

const MaterialCard: React.FC<MaterialCardProps> = ({ displayName, icon, type, id }) => {
  return (
    <div className="materialCard" data-cp-type={type} data-cp-displayname={displayName} data-cp-id={id}>
      <div className="icon">{ICON_Map[icon]}</div>
      <div className="text">{displayName}</div>
    </div>
  );
};

export default MaterialCard;
