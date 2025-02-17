import { ChartData } from "chart.js";
import { ConsumptionReport } from "./ConsumptionReport";

export function createChartData(report: ConsumptionReport): ChartData {
	const labels = report!.costs.map((e) =>
		Intl.DateTimeFormat("en-GB", {
			year: "numeric",
			month: "long",
		}).format(e.timestamp),
	);

	return {
		labels,
		datasets: [
			{
				backgroundColor: "turquoise",
				label: "Cost in Euro",
				data: report!.costs.map((e) => e.cost),
			},
			{
				backgroundColor: "pink",
				label: "Consumption in kilowatt-hours",
				data: report!.costs.map((e) => e.kilowattHoursConsumed),
			},
		],
	};
}
