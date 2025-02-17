## Running the application
From the project's root directory, please run `./gradlew bootRun`

## Loging in
There are two users available:
- john, password: doe 
- billy, passoword: bonanza

## Important notes
### 1
While Vite is used to build the front-end, its development server isn't, i.e. `npm run dev`. 
With a bit more time I would have completely separated the front-end and backend, 
and used OAuth for authentication between the two, giving either full freedom.

### 2
The generated consumption data is based on hourly measurements, not a continuous readouts from meters, with timestamps. It is convenient for the application, but potentially not realistic.

### 3
The solution doesn't account for consumption on different years at the moment, per meter that is.  

