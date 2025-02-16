import { ChartData } from "chart.js";
import { Chart } from "primereact/chart";
import { Dropdown } from "primereact/dropdown";
import { useEffect, useState } from "react";
import { Toolbar } from "primereact/toolbar";
import { Message } from "primereact/message";
import { ConsumptionReport } from "./ConsumptionReport";
import { fetchConsumptionReports } from "./fetchConsumptionReports";
import "./App.css";

function App() {
	var [fetching, setFetching] = useState(true);
	var [consumptionReports, setConsumptionReports] = useState<
		ConsumptionReport[]
	>([]);
	var [selectedMeterID, setSelectedMeterID] = useState<String | null>(null);

	useEffect(() => {
		if (consumptionReports.length === 0) {
			(async () => {
				const reports = await fetchConsumptionReports();

				setConsumptionReports(reports);
				if (reports.length > 0) {
					setSelectedMeterID(reports[0].meterID);
				}

				setFetching(false);
			})();
		}
	});

	var costChartData: ChartData | undefined = undefined;
	if (selectedMeterID !== null) {
		const reportForMeter = consumptionReports.find(
			(e) => e.meterID === selectedMeterID,
		);
		const labels = reportForMeter!.costs.map((e) =>
			Intl.DateTimeFormat("en-GB", {
				year: "numeric",
				month: "long",
			}).format(e.timestamp),
		);

		costChartData = {
			labels,
			datasets: [
				{
					backgroundColor: "turquoise",
					label: "Cost in Euro",
					data: reportForMeter!.costs.map((e) => e.cost),
				},
				{
					backgroundColor: "pink",
					label: "Consumption in kilowatt-hours",
					data: reportForMeter!.costs.map((e) => e.kilowattHoursConsumed),
				},
			],
		};
	}

	return (
		<>
			<Toolbar
				start={fetching && <Message text={"Tallying costs..."} />}
				center={
					consumptionReports.length > 0 && (
						<>
							<Dropdown
								optionValue="meterID"
								value={selectedMeterID}
								options={consumptionReports}
								optionLabel="meterAddress"
								onChange={(e) => setSelectedMeterID(e.value)}
							/>
						</>
					)
				}
				end={<a href="/logout">Log out</a>}
			/>

			{costChartData && (
				<div className="Content">
					<Chart type="bar" data={costChartData} />
				</div>
			)}
		</>
	);
}

export default App;
