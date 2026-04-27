import React from 'react';
import { ICON_Map } from './icons';
import './index.css';

interface MaterialCardProps {
  displayName: string;
  type: string;
  icon: string;
  id: string;
  layout?: 'column' | 'row';
  disabled?: boolean;
}

const MaterialCard: React.FC<MaterialCardProps> = ({ displayName, icon, type, id, layout, disabled = false }) => {
  return (
    <div 
      className={`materialCard ${layout} ${disabled && 'disabled-drag'}`} 
      data-cp-type={type} 
      data-cp-displayname={displayName} 
      data-cp-id={id}
      style={{ cursor: disabled ? 'not-allowed' : 'pointer' }}
    >
      <div className="icon">{ICON_Map[icon]}</div>
      <div className="text">{displayName}</div>
    </div>
  );
};

export default MaterialCard;
