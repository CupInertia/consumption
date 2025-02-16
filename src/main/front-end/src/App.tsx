import { ChartData } from "chart.js";
import { Chart } from "primereact/chart";
import { Dropdown } from "primereact/dropdown";
import { useEffect, useState } from "react";
import { Toolbar } from "primereact/toolbar";
import { Message } from 'primereact/message';
import './App.css'

type ConsumptionCost = {
	timestamp: Date;
	kilowattHoursConsumed: number;
	cost: number;
};

type ConsumptionReport = {
	meterID: string;
	meterAddress: string;
	costs: ConsumptionCost[];
};

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

function App() {
	var [fetching, setFetching] = useState(true);
	var [consumptionReports, setConsumptionReports] = useState<
		ConsumptionReport[]
	>([]);
	var [selectedMeterID, setSelectedMeterID] = useState<String | null>(null);

	useEffect(() => {
		if (consumptionReports.length === 0) {
			fetch("http://localhost:8080/customer/consumption").then(
				async (response: any) => {
					const reports = parseResponse(await response.json());
					setConsumptionReports(reports);
					if (reports.length > 0) {
						setSelectedMeterID(reports[0].meterID);
					}

					setFetching(false);
				},
			);
		}
	});

	var costChartData: ChartData | undefined = undefined;
	var consumptionChartData: ChartData | undefined = undefined;
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
			],
		};

		consumptionChartData = {
			labels,
			datasets: [
				{
					label: "Consumption in kilowatt-hours",
					data: reportForMeter!.costs.map((e) => e.kilowattHoursConsumed),
				},
			],
		};
	}

	return (
		<>
			<Toolbar
				start={fetching && <Message text={"Talling the costs..."}/>}
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

			{consumptionReports.length > 0 && (
				<div className="Content">
					<Chart type="bar" data={costChartData} />
					<Chart type="bar" data={consumptionChartData} />
				</div>
			)}
		</>
	);
}

export default App;
