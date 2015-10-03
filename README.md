# Adverto
Cars Adverts Application.

Pure Akka application. Uses Akka HTTP and Akka Persistence. JSON Marshalling/Unmarshalling done using Spray-JSON.

## Build
```sbt dist```
## Run
1. Unzip application in /target/universal folder
2. Run application sh/bat script. ```bin/adverto```

## API
REST API will be available at ```localhost:5090```

Port and host can be changed with config file at ```/conf/adverto.conf```

Path | Method | Request Body | Description | Returns
------- | ------ | ------ | ------ | ------
/advert/[uuid] | GET | None | Gets advert by UUID | Advert if found
/advert/[uuid] | DELETE | None | Deletes advert by UUID | 204
/advert/[uuid] | PUT | Advert(with id) | Updated Existing Advert | 204
/advert | GET | None | Lists all adverts | Adverts collection
/advert?sort=[sorting] | GET | None | Lists all adverts, with sort by field | Adverts collection
/advert | POST | Advert(without id) | Lists all adverts | 201 / new car id

Example Data

Advert (new car)
```
{
  "price": 15000, 
  "fuel": "Gasoline", 
  "id": "fc4a7e15-9499-4f8b-b80b-9bf38429c1ba", 
  "new": true, 
  "title": "Volvo S60"
}
```

Advert (old car)
```
{
  "mileage": 120000,
  "price": 5000,
  "fuel": "Gasoline",
  "id": "fc4a7e15-9499-4f8b-b80b-9bf38429c1ba",
  "new": false,
  "firstRegistration": "2003-08-10T00:00Z",
  "title": "Mercedes E320"
}
```

New advert body (new car)
```
{
  "title": "Volvo S60",
  "fuel": "Gasoline",
  "price": 15000,
  "new": true
}
```

New advert body (old car)
```
{
  "mileage": 120000,
  "price": 5000,
  "fuel": "Gasoline",
  "new": false,
  "firstRegistration": "2003-08-10T00:00Z",
  "title": "Mercedes E320"
}
```
