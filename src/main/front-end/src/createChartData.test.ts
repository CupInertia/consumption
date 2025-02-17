import { ConsumptionReport } from "./ConsumptionReport";
import { createChartData } from "./createChartData";

test("returns chart data based on the report", () => {
	// given
	const consumptionReport: ConsumptionReport = {
		meterID: "An ID",
		meterAddress: "An address",
		costs: [
			{
				cost: 10,
				kilowattHoursConsumed: 100,
				timestamp: new Date("2000-01-01T01:01:00"),
			},
		],
	};

	// when
	const data = createChartData(consumptionReport);

	// then
	expect(data.labels).toEqual(["January 2000"]);

	const { datasets } = data;
	expect(datasets.length).toBe(2);

	const firstDataset = datasets[0];
	expect(firstDataset.data).toEqual([10]);
	expect(firstDataset.backgroundColor).toBe("turquoise");
	expect(firstDataset.label).toBe("Cost in Euro");

	const secondDataset = datasets[1];
	expect(secondDataset.data).toEqual([100]);
	expect(secondDataset.backgroundColor).toBe("pink");
	expect(secondDataset.label).toBe("Consumption in kilowatt-hours");
});
