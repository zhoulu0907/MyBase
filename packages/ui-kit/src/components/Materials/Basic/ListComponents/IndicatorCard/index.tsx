import { Button, Form, List, Card, Empty } from '@arco-design/web-react';
import { IconPlus, IconRefresh } from '@arco-design/web-react/icon';
import { memo, useEffect, useState } from 'react';
import {
  STATUS_OPTIONS,
  STATUS_VALUES,
  WIDTH_OPTIONS,
  WIDTH_VALUES,
  INDICATOR_CARD_STYLE_TYPE,
  INDICATOR_CALCULATE_TYPE,
  INDICATOR_TIME_DEMENSION,
  INDICATOR_COMPARE_CALCULATE_METHOD,
  INDICATOR_COMPARE_CALCULATE_TYPE
} from '../../../constants';
import type { XIndicatorCardConfig } from './schema';
import './index.css';

const XIndicatorCard = memo((props: XIndicatorCardConfig & { runtime?: boolean }) => {
  const { label, runtime = true, styleType, indicatorList, status, width } = props;
  return (
    <div
      style={{
        opacity: status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN] ? 0.4 : 1,
        display: runtime && status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN] ? 'none' : 'unset'
      }}
    >
      12123
    </div>
  );
});

export default XIndicatorCard;
