import { memo } from 'react';
import { Input, Tooltip, Form } from '@arco-design/web-react';
import type { XInputTextAreaConfig } from './schema';
import {
	STATUS_VALUES,
	STATUS_OPTIONS,
} from '@/components/Materials/constants';

const TextArea = Input.TextArea;

const XInputTextArea = memo((props: XInputTextAreaConfig) => {
	const {
		label,
		placeholder,
		tooltip,
		status,
		defaultValue,
		required,
		align,
		layout,
		color,
		bgColor,
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
			rules={[{ required }]}
			style={{
				pointerEvents:
					status === STATUS_VALUES[STATUS_OPTIONS.READONLY] ? 'none' : 'unset',
			}}
		>
			<TextArea
				readOnly={status === STATUS_VALUES[STATUS_OPTIONS.READONLY]}
				defaultValue={defaultValue}
				placeholder={placeholder}
				style={{
					width: '100%',
					textAlign: align,
					color,
					backgroundColor: bgColor,
				}}
			/>
		</Form.Item>
	);
});

export default XInputTextArea;
