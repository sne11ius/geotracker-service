# Geo tracker serivce

A service to store and display geo coordinates

## Example

This service is running @ [https://wasis.nu/mit/geotracker-service](https://wasis.nu/mit/geotracker-service)

## Client

See [Android GeoTracker](https://github.com/sne11ius/GeoTracker)

## How to run

1. Install [activator](https://www.playframework.com/documentation/2.3.x/Installing)
2. Hit `activator run`
3. ...
4. You want to use some DB with this
5. Create an account to get an api key

## Urls

* You can `POST` coordinates to `/coordinates` if you provide an api key.
* You can `POST` `apiKey=[your key]` to `/coordinates/latest` to get
the latest location as json.

## License

Yep, it's GPL v3 - get over with it ;)

Also: see the [`LICENSE`](https://raw.githubusercontent.com/sne11ius/geotracker-service/master/LICENSE) file
