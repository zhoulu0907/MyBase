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
import { queryRuntimeDataCards } from '@onebase/app/src/services/app_runtime';
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
    if (!runtime || !indicatorList?.length) return;
    const params = JSON.stringify(indicatorList);
    try {
      const res = await queryRuntimeDataCards(params);
      // 将响应结果映射为组件内部 data 格式
      const mapped = (res || []).map((item: any) => ({
        value: item.value ?? 0,
        displayValue: item.displayValue ?? '',
        comparePercent: item.compareDisplay ?? '0%',
        type: item.compareType === 'up' ? 'rise' : item.compareType === 'down' ? 'decline' : 'equal',
        compareAvailable: item.compareAvailable
      }));
      setData(mapped);
    } catch (e) {
      console.error('数据卡片查询失败', e);
    }
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

  const renderCardValue = (ele: any, index: number) => {
    let value = `${data?.[index]?.value || 0}`;
    // 显示为绝对值
    if (ele.absoluteValue) {
      value = `${Math.abs(Number(value))}`;
    }
    // 保留小数点
    if (ele.precisionLimit) {
      value = Number(value).toFixed(ele.precision || 0);
    }
    // 使用千分位分隔符
    if (ele.thousandsSeparator) {
      const decimalIndex = value.indexOf('.');
      if (decimalIndex !== -1) {
        const decimalNum = value.slice(decimalIndex);
        const intNum = value.slice(0, decimalIndex);
        const intValue = intNum.replace(/\B(?=(\d{3})+(?!\d))/g, ',');
        value = `${intValue}${decimalNum}`;
      } else {
        value = value.replace(/\B(?=(\d{3})+(?!\d))/g, ',');
      }
    }
    // 显示为百分比
    if (ele.percent) {
      value = `${value}%`;
    }
    // 显示单位
    if (ele.unitLimit) {
      value = `${value}${ele.unit}`;
    }
    return value;
  };

  const renderCard = () => {
    switch (styleType) {
      case INDICATOR_CARD_STYLE_TYPE.ONE: {
        return (
          <Grid.Row>
            {indicatorList?.map((ele: any, index) => (
              <Grid.Col key={index} span={getSpan(ele.width)}>
                <div className="card">
                  <div className="cardContent" style={{ paddingRight: '26px', paddingTop: '20px' }}>
                    <div className="cardMain">
                      {ele.label?.display && (
                        <Typography.Ellipsis
                          showTooltip={true}
                          className="cardTitle"
                          style={{ width: 'calc(100% - 32px)' }}
                        >
                          {ele.label.text}
                        </Typography.Ellipsis>
                      )}

                      <Typography.Ellipsis showTooltip={true} className="cardValue">
                        {renderCardValue(ele, index)}
                      </Typography.Ellipsis>

                      {ele.compareLimit && (
                        <div className="cardCompare">
                          <span>{ele.compareDescribe || '同比'}</span>
                          <span
                            className="cardCompareValue"
                            style={{ color: data?.[index]?.type === 'rise' ? 'red' : '#00B42A' }}
                          >
                            {data?.[index]?.comparePercent || '0%'}
                          </span>
                          <img
                            className="cardCompareImg"
                            src={data?.[index]?.type === 'rise' ? riseSvg01 : declineSvg01}
                            alt=""
                          />
                        </div>
                      )}
                    </div>

                    {ele.icon?.display && (
                      <div
                        className="cardIcon1"
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
                    )}
                  </div>
                  {index < indicatorList.length - 1 && <Divider className="cardDivider" type="vertical" />}
                </div>
              </Grid.Col>
            ))}
          </Grid.Row>
        );
      }
      case INDICATOR_CARD_STYLE_TYPE.TWO: {
        return (
          <Grid.Row>
            {indicatorList?.map((ele: any, index) => (
              <Grid.Col key={index} span={getSpan(ele.width)}>
                <div className="card">
                  <div className="cardContent">
                    <div className="cardIcon2" style={{ backgroundColor: ele.icon?.color || 'rgb(var(--primary-6))' }}>
                      <ReactSVG
                        style={{ height: '24px' }}
                        src={allWebMenuIcons.find((e) => e.code === ele.icon?.name)?.icon || ''}
                        beforeInjection={(svg) => {
                          const fillColor = '#ffffff';
                          svg.querySelectorAll('*').forEach((el) => {
                            if (el.getAttribute('fill') === 'black') {
                              el.setAttribute('fill', fillColor);
                            }
                          });
                          svg.setAttribute('width', '24px');
                          svg.setAttribute('height', '24px');
                        }}
                      />
                    </div>
                    <div className="cardMain">
                      {ele.label?.display && (
                        <Typography.Ellipsis showTooltip={true} className="cardTitle">
                          {ele.label.text}
                        </Typography.Ellipsis>
                      )}

                      <Typography.Ellipsis showTooltip={true} className="cardValue">
                        {renderCardValue(ele, index)}
                      </Typography.Ellipsis>

                      {ele.compareLimit && (
                        <div className="cardCompare">
                          <span>{ele.compareDescribe || '同比'}</span>
                          <span
                            className="cardCompareValue"
                            style={{ color: data?.[index]?.type === 'rise' ? 'red' : '#00B42A' }}
                          >
                            {data?.[index]?.comparePercent || '0%'}
                          </span>
                          <img
                            className="cardCompareImg"
                            src={data?.[index]?.type === 'rise' ? riseSvg01 : declineSvg01}
                            alt=""
                          />
                        </div>
                      )}
                    </div>
                  </div>
                  {index < indicatorList.length - 1 && <Divider className="cardDivider" type="vertical" />}
                </div>
              </Grid.Col>
            ))}
          </Grid.Row>
        );
      }
      case INDICATOR_CARD_STYLE_TYPE.THREE: {
        return (
          <Grid.Row gutter={20}>
            {indicatorList?.map((ele: any, index) => (
              <Grid.Col key={index} span={getSpan(ele.width)}>
                <div className="card" style={{ backgroundColor: ele.backgroundColor || 'rgba(var(--primary-6),0.1)' }}>
                  <div className="cardContent">
                    <div className="cardMain">
                      {ele.label?.display && (
                        <Typography.Ellipsis showTooltip={true} className="cardTitle">
                          {ele.label.text}
                        </Typography.Ellipsis>
                      )}

                      <Typography.Ellipsis
                        showTooltip={true}
                        className="cardValue"
                        style={{ width: 'calc(100% - 50px)' }}
                      >
                        {renderCardValue(ele, index)}
                      </Typography.Ellipsis>

                      {ele.compareLimit && (
                        <div className="cardCompare">
                          <span>{ele.compareDescribe || '同比'}</span>
                          <span
                            className="cardCompareValue"
                            style={{ color: data?.[index]?.type === 'rise' ? 'red' : '#00B42A' }}
                          >
                            {data?.[index]?.comparePercent || '0%'}
                          </span>
                          <img
                            className="cardCompareImg"
                            src={data?.[index]?.type === 'rise' ? riseSvg01 : declineSvg01}
                            alt=""
                          />
                        </div>
                      )}
                    </div>
                    <div className="cardIcon3">
                      <ReactSVG
                        style={{ height: '42px' }}
                        src={allWebMenuIcons.find((e) => e.code === ele.icon?.name)?.icon || ''}
                        beforeInjection={(svg) => {
                          const fillColor = ele.icon?.color || 'rgb(var(--primary-6))';
                          svg.querySelectorAll('*').forEach((el) => {
                            if (el.getAttribute('fill') === 'black') {
                              el.setAttribute('fill', fillColor);
                            }
                          });
                          svg.setAttribute('width', '42px');
                          svg.setAttribute('height', '42px');
                        }}
                      />
                    </div>
                  </div>
                </div>
              </Grid.Col>
            ))}
          </Grid.Row>
        );
      }
      case INDICATOR_CARD_STYLE_TYPE.FOUR: {
        return (
          <Grid.Row>
            {indicatorList?.map((ele: any, index) => (
              <Grid.Col key={index} span={getSpan(ele.width)}>
                <div className="card">
                  <div className="cardContent">
                    <div className="cardMain">
                      {ele.label?.display && (
                        <Typography.Ellipsis showTooltip={true} className="cardTitle">
                          {ele.label.text}
                        </Typography.Ellipsis>
                      )}

                      <Typography.Ellipsis showTooltip={true} className="cardValue">
                        {renderCardValue(ele, index)}
                      </Typography.Ellipsis>
                    </div>
                    {ele.compareLimit && (
                      <div className="cardRight">
                        <div
                          className="cardCompareValue4"
                          style={{ color: data?.[index]?.type === 'rise' ? 'red' : '#00B42A' }}
                        >
                          <img
                            className="cardCompareImg4"
                            src={data?.[index]?.type === 'rise' ? riseSvg02 : declineSvg02}
                            alt=""
                          />
                          <span>{data?.[index]?.comparePercent || '0%'}</span>
                        </div>
                        <div className="cardCompare4">{ele.compareDescribe || '同比'}</div>
                      </div>
                    )}
                  </div>
                  {index < indicatorList.length - 1 && <Divider className="cardDivider" type="vertical" />}
                </div>
              </Grid.Col>
            ))}
          </Grid.Row>
        );
      }
      case INDICATOR_CARD_STYLE_TYPE.FIVE: {
        return (
          <Grid.Row>
            {indicatorList?.map((ele: any, index) => (
              <Grid.Col key={index} span={getSpan(ele.width)}>
                <div className="card">
                  <div className="cardContent" style={{ paddingRight: '26px' }}>
                    <div className="cardMain">
                      {ele.label?.display && (
                        <Typography.Ellipsis showTooltip={true} className="cardTitle">
                          {ele.label.text}
                        </Typography.Ellipsis>
                      )}

                      <Typography.Ellipsis showTooltip={true} className="cardValue">
                        {renderCardValue(ele, index)}
                      </Typography.Ellipsis>

                      {ele.compareLimit && (
                        <div className="cardCompare">
                          <span>{ele.compareDescribe || '同比'}</span>
                          <span
                            className="cardCompareValue"
                            style={{ color: data?.[index]?.type === 'rise' ? 'red' : '#00B42A' }}
                          >
                            {data?.[index]?.comparePercent || '0%'}
                          </span>
                          <img
                            className="cardCompareImg"
                            src={data?.[index]?.type === 'rise' ? riseSvg01 : declineSvg01}
                            alt=""
                          />
                        </div>
                      )}
                    </div>
                    <img
                      className="cardCompareImg5"
                      src={data?.[index]?.type === 'rise' ? riseSvg03 : declineSvg03}
                      alt=""
                    />
                  </div>
                  {index < indicatorList.length - 1 && <Divider className="cardDivider" type="vertical" />}
                </div>
              </Grid.Col>
            ))}
          </Grid.Row>
        );
      }
      default: {
        return <></>;
      }
    }
  };

  return (
    <div
      style={{
        opacity: status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN] ? 0.4 : 1,
        display: runtime && status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN] ? 'none' : 'block'
      }}
      className="indicatorCard"
    >
      {label.display && (
        <Typography.Ellipsis showTooltip={true} style={{ marginBottom: '12px' }}>
          {label.text}
        </Typography.Ellipsis>
      )}

      {renderCard()}
    </div>
  );
});

export default XIndicatorCard;
