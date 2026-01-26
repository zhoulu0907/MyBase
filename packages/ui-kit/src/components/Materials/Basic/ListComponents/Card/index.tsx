import { memo, useEffect, useState } from 'react';
import type { XCardConfig } from './schema';


const XCard = memo((props:XCardConfig & {runtime?: boolean;})=>{

    return <>card</>
})

export default XCard;