import { fetchConsumptionReports } from "./fetchConsumptionReports";
import fetchMock from "jest-fetch-mock";

beforeEach(() => {
	fetchMock.resetMocks();
});

test("fetchesConsumptionReports", async () => {
	// given
	const meterAddress = "Address A";
	const meterID = "73f4d93f-49a5-49e0-a5ff-59da2967ac1a";

	const cost = {
		timestamp: "2024-01-01T23:59:00+02:00",
		kilowattHoursConsumed: 61.94,
		cost: 4.05,
	};

	fetchMock.mockResponseOnce(
		JSON.stringify([
			{
				meterAddress,
				meterID,
				costs: [cost],
			},
		]),
	);

	// when
	const reports = await fetchConsumptionReports();

	// then
	expect(reports.length).toBe(1);

	const firstReport = reports[0];
	expect(firstReport.meterID).toBe(meterID);
	expect(firstReport.meterAddress).toBe(meterAddress);

	const costs = firstReport.costs;
	expect(costs.length).toBe(1);

	const firstCost = costs[0];
	expect(firstCost.cost).toBe(cost.cost);
	expect(firstCost.kilowattHoursConsumed).toBe(cost.kilowattHoursConsumed);
	expect(firstCost.timestamp).toEqual(new Date(cost.timestamp));
});
