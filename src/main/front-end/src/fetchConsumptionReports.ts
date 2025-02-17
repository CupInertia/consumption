import { ConsumptionReport } from "./ConsumptionReport";

export async function fetchConsumptionReports() {
	const response = await fetch("http://localhost:8080/customer/consumption");
	return parseResponse(await response.json());
}

function parseResponse(response: any): ConsumptionReport[] {
	return response.map((responseReport: any) => {
		return {
			...responseReport,
			costs: responseReport.costs.map((e: any) => {
				return {
					...e,
					timestamp: new Date(e.timestamp),
				};
			}),
		};
	});
}
