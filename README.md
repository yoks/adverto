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

Path | Method | Description | Returns
------- | ------ | ------- | -----
/advert/[uuid] | GET | Gets advert by UUID | Advert if found
/advert/[uuid] | DELETE | Deletes advert by UUID | 204
/advert/[uuid] | PUT | Updated Existing Advert | 204
/advert | GET | Lists all adverts | Adverts collection
/advert?sort=[sorting] | GET | Lists all adverts, with sort by field | Adverts collection
/advert | POST | Lists all adverts | 201
