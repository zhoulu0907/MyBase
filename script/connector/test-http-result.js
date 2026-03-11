const inputData = input
const BASE_URI = "http://s25029301301.dev.internal.virtueit.net:81/v1-snapshot/onebaseserverruntime";
const TIANGONG_HAZARD_URI = "/runtime/tiangong/dashboard/device-hazards";

const logResp = await fetch(BASE_URI + TIANGONG_HAZARD_URI, {
    method: "GET",
    headers: { appid: "260705906803245056" }
});

var hazardResult = await logResp.json();
var firstData = hazardResult.data[0]

result = {
    id: firstData.id,
    province: firstData.province,
    deviceName: firstData.deviceName,
    predictTime: firstData.predictTime
};