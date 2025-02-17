import { ConsumptionCost } from "./ConsumptionCost";

export type ConsumptionReport = {
	meterID: string;
	meterAddress: string;
	costs: ConsumptionCost[];
};
