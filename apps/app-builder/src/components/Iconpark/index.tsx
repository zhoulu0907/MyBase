
import bike from "@/assets/icon_application/bike.svg";
import bulletMap from "@/assets/icon_application/bullet-map.svg";
import churchOne from "@/assets/icon_application/church-one.svg";
import dataDisplay from "@/assets/icon_application/data-display.svg";
import fingerPrintTwo from "@/assets/icon_application/fingerprint-two.svg";
import platte from "@/assets/icon_application/platte.svg";

interface IconParkProps {
    iconName: string
}

const IconPark:React.FC<IconParkProps> = ({iconName}) => {
    const getIconName = (type: string) => {
        switch(type) {
            case "bike":
                return bike
            case "bullet-map":
                return bulletMap
            case "church-one":
                return churchOne;
            case "data-display":
                return dataDisplay;
            case "fingerprint-two":
                return fingerPrintTwo;
            case "platte":
                return platte
            default:
                return ""
        }
    }

    return <img src={getIconName(iconName)} />
}

export default IconPark;