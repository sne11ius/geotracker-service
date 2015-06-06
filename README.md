[![Build Status](http://wasis.nu:8090/buildStatus/icon?job=geotracker-service dist)](http://wasis.nu:8090/job/geotracker-service%20dist/)

# Geo tracker service

A service to store and display geo coordinates

## Example

This service is running @ [https://wasis.nu/mit/geotracker-service](https://wasis.nu/mit/geotracker-service)

## Client

See [Android GeoTracker](https://github.com/sne11ius/GeoTracker)

## How to

### Prerequisites

Make sure you got these available

- nodejs
- grunt
- bower
- ruby & sass

### Run

0. Clone [https://github.com/sne11ius/play-yeoman](https://github.com/sne11ius/play-yeoman), and
   `sbt publishLocal` the projects in `./play-yeoman` and `./sbt-yeoman`.
1. Run `npm install`
2. Run `bower install`
3. Install [activator](https://www.playframework.com/documentation/2.3.x/Installing)
4. Hit `activator run`
5. ...
6. You want to use some DB with this
7. Create an account to get an api key

## Urls

* You can `POST` coordinates to `/coordinates` if you provide an api key.
* You can `POST` `apiKey=[your key]` to `/coordinates/latest` to get
the latest location as json.

## License

Yep, it's GPL v3 - get over with it ;)

Also: see the [`LICENSE`](https://raw.githubusercontent.com/sne11ius/geotracker-service/master/LICENSE) file
