import { memo } from 'react';
import { InputNumber, Form } from '@arco-design/web-react';
import type { XInputNumberConfig } from './schema';
import {
	STATUS_VALUES,
	STATUS_OPTIONS,
} from '@/components/Materials/constants';

const XInputNumber = memo((props: XInputNumberConfig) => {
	const {
		label,
		placeholder,
		tooltip,
		status,
		defaultValue,
		required,
		align,
		min,
		max,
		step,
		precision,
		layout,
		labelColSpan = 0,
	} = props;

	return status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN] ? null : (
		<Form.Item
			label={label}
			layout={layout}
			labelCol={{
				span: labelColSpan,
			}}
			tooltip={tooltip}
			wrapperCol={{ span: 24 - labelColSpan }}
			rules={[
				{
					required,
					type: 'number',
					min,
					max,
				},
			]}
			style={{
				pointerEvents:
					status === STATUS_VALUES[STATUS_OPTIONS.READONLY] ? 'none' : 'unset',
			}}
		>
			<InputNumber
				readOnly={status === STATUS_VALUES[STATUS_OPTIONS.READONLY]}
				defaultValue={defaultValue}
				placeholder={placeholder}
				step={step}
				min={min}
				max={max}
				precision={precision}
				style={{
					width: '100%',
					textAlignLast: align,
				}}
			/>
		</Form.Item>
	);
});

export default XInputNumber;
