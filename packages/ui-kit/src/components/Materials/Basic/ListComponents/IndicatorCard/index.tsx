import riseSvg01 from '@/assets/images/indicatorCard/rise01.svg';
import riseSvg02 from '@/assets/images/indicatorCard/rise02.svg';
import riseSvg03 from '@/assets/images/indicatorCard/rise03.svg';
import declineSvg01 from '@/assets/images/indicatorCard/decline01.svg';
import declineSvg02 from '@/assets/images/indicatorCard/decline02.svg';
import declineSvg03 from '@/assets/images/indicatorCard/decline03.svg';
import { Grid, Typography, Divider } from '@arco-design/web-react';
import { IconPlus, IconRefresh } from '@arco-design/web-react/icon';
import { memo, useEffect, useState } from 'react';
import { webMenuIcons } from '@/utils/menuIcons';
import { ReactSVG } from 'react-svg';
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
  const allWebMenuIcons = webMenuIcons.map((ele) => ele.children).reduce((acc, current) => acc.concat(current), []);

  const [data, setData] = useState<any[]>([]);

  useEffect(() => {
    getData();
  }, []);

  const getData = async () => {
    // todo 接口获取数据
    setData([
      { value: '1256', comparePercent: '3%', type: 'rise' },
      { value: '1256', comparePercent: '3%', type: 'rise' },
      { value: '1256', comparePercent: '3%', type: 'decline' },
      { value: '1256', comparePercent: '3%', type: 'decline' }
    ]);
  };

  const getSpan = (width: string) => {
    if (width === WIDTH_VALUES[WIDTH_OPTIONS.THIRD]) {
      return 8;
    }
    if (width === WIDTH_VALUES[WIDTH_OPTIONS.HALF]) {
      return 12;
    }
    if (width === WIDTH_VALUES[WIDTH_OPTIONS.FULL]) {
      return 24;
    }
    return 6;
  };

  return (
    <div
      style={{
        opacity: status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN] ? 0.4 : 1,
        display: runtime && status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN] ? 'none' : 'block'
      }}
      className="indicatorCard"
    >
      {label.display && <Typography.Ellipsis showTooltip={true}>{label.text}</Typography.Ellipsis>}

      {styleType === INDICATOR_CARD_STYLE_TYPE.ONE && (
        <Grid.Row className="cardOne">
          {indicatorList?.map((ele: any, index) => (
            <Grid.Col key={index} span={getSpan(ele.width)}>
              <div className="cardOneItem">
                <div className="cardOneContent">
                  {ele.label?.display && <div className="cardOneTitle">{ele.label.text}</div>}
                  <div className="cardOneValue">
                    {ele.thousandsSeparator
                      ? `${data?.[index]?.value || 0}`.replace(/\B(?=(\d{3})+(?!\d))/g, ',')
                      : `${data?.[index]?.value || 0}`}
                  </div>
                  {ele.compareLimit && (
                    <div className="cardOneCompare">
                      <span>同比</span>
                      {data?.[index]?.type === 'rise' ? (
                        <span className="cardOneCompareType" style={{ color: 'red' }}>
                          {data?.[index]?.comparePercent || '0%'}
                          <img src={riseSvg01} alt="" />
                        </span>
                      ) : (
                        <span className="cardOneCompareType" style={{ color: '#00B42A' }}>
                          {data?.[index]?.comparePercent || '0%'}
                          <img src={declineSvg01} alt="" />
                        </span>
                      )}
                    </div>
                  )}

                  <div
                    className="cardOneIcon"
                    style={{ backgroundColor: ele.backgroundColor || 'rgba(var(--primary-6),0.1)' }}
                  >
                    <ReactSVG
                      style={{ height: '18px' }}
                      src={allWebMenuIcons.find((e) => e.code === ele.icon?.name)?.icon || ''}
                      beforeInjection={(svg) => {
                        const fillColor = ele.icon?.color || 'rgb(var(--primary-6))';
                        svg.querySelectorAll('*').forEach((el) => {
                          if (el.getAttribute('fill') === 'black') {
                            el.setAttribute('fill', fillColor);
                          }
                        });
                        svg.setAttribute('width', '18px');
                        svg.setAttribute('height', '18px');
                      }}
                    />
                  </div>
                </div>
                {index < indicatorList.length - 1 && <Divider className="cardOneDivider" type="vertical" />}
              </div>
            </Grid.Col>
          ))}
        </Grid.Row>
      )}

      {styleType === INDICATOR_CARD_STYLE_TYPE.TWO && (
        <Grid.Row className="cardTwo">
          {indicatorList?.map((ele: any, index) => (
            <Grid.Col key={index} span={getSpan(ele.width)}></Grid.Col>
          ))}
        </Grid.Row>
      )}

      {styleType === INDICATOR_CARD_STYLE_TYPE.THREE && (
        <Grid.Row className="cardThree">
          {indicatorList?.map((ele: any, index) => (
            <Grid.Col key={index} span={getSpan(ele.width)}></Grid.Col>
          ))}
        </Grid.Row>
      )}

      {styleType === INDICATOR_CARD_STYLE_TYPE.FOUR && (
        <Grid.Row className="cardFour">
          {indicatorList?.map((ele: any, index) => (
            <Grid.Col key={index} span={getSpan(ele.width)}></Grid.Col>
          ))}
        </Grid.Row>
      )}

      {styleType === INDICATOR_CARD_STYLE_TYPE.FIVE && (
        <Grid.Row className="cardFive">
          {indicatorList?.map((ele: any, index) => (
            <Grid.Col key={index} span={getSpan(ele.width)} className="card"></Grid.Col>
          ))}
        </Grid.Row>
      )}
    </div>
  );
});

export default XIndicatorCard;
