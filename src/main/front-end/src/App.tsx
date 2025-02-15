import { useEffect, useState } from "react";
import "./App.css";

type ConsumptionCost = {
	meteringPoint: {
		id: string;
		address: string;
	};
	timestamt: Date;
	kiloWattHoursConsumed: number;
	cost: number;
};

function App() {
	var [fetching, setFetching] = useState(true);
	var [consumptionsCosts, setConsumptionCosts] = useState<ConsumptionCost[]>(
		[],
	);

	useEffect(() => {
		fetch(
			"http://localhost:8080/customer/89617b9c-91c3-4c5f-9e9f-24d7adb964c0/consumption",
		).then(async (response: any) => {
			setConsumptionCosts(await response.json());
			setFetching(false);
		});
	});

	return (
		<>
			{fetching && "Talling the costs..."}
			<a href="/logout">Log out</a>

			{consumptionsCosts.length}
		</>
	);
}

export default App;
