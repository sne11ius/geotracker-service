package controllers

import com.mohiva.play.silhouette.api.{ Environment, LogoutEvent, Silhouette }
import com.mohiva.play.silhouette.impl.authenticators.SessionAuthenticator
import javax.inject.Inject
import services.NamedLocationService
import play.api.mvc.Controller
import scala.concurrent.Future
import models.User
import forms.CreateLocationForm
import play.api.Logger
import models.NamedLocation
import services.GeoCoordService
import org.joda.time.Interval
import play.api.libs.json._
import play.api.libs.json.Json
import models.NamedLocation
import argonaut._
import org.joda.time.DateTime

class NamedLocationsController @Inject() (implicit val env: Environment[User, SessionAuthenticator], val namedLocationsService: NamedLocationService, geoCoordService: GeoCoordService) extends Silhouette[User, SessionAuthenticator] {

  implicit val coordWrites = new Writes[NamedLocation] {
    def writes(n: NamedLocation): JsValue = {
      Json.obj(
        "id" -> n.id,
        "name" -> n.name,
        "latitude" -> n.latitude,
        "longitude" -> n.longitude,
        "radius" -> n.radius
      )
    }
  }

  // Using argonaut
  implicit def DecodeGeoCoord: DecodeJson[NamedLocation] =
    DecodeJson(c => for {
      id <- (c --\ "id").as[Option[Long]]
      name <- (c --\ "name").as[String]
      latitude <- (c --\ "latitude").as[Double]
      longitude <- (c --\ "longitude").as[Double]
      radius <- (c --\ "radius").as[Float]
    } yield NamedLocation(
      id, name, latitude, longitude, radius
    ))

  def listLocations = SecuredAction.async { implicit request =>
    val locations = namedLocationsService.loadLocations(request.identity)
    Future.successful(Ok(Json.toJson(locations)))
  }

  def addLocation = SecuredAction.async { implicit request =>
    request.body.asJson match {
      case Some(text) => {
        val option: Option[NamedLocation] = Parse.decodeOption[NamedLocation](text.toString())
        Parse.decodeOption[NamedLocation](text.toString()) match {
          case Some(location) => {
            Logger.debug(s"location: $location")
            namedLocationsService.addLocation(location, request.identity);
            Future.successful(Ok)
          }
          case _ => {
            Logger.debug("Could not parse shit D:")
            Future.successful(BadRequest)
          }
        }
      }
      case _ => {
        Logger.debug("Could not body as text D:")
        Future.successful(BadRequest)
      }
    }
  }

  def update(id: Long) = SecuredAction.async { implicit request =>
    Logger.debug(s"Update by id: $id")
    namedLocationsService.find(request.identity, id) match {
      case Some(location) => {
        request.body.asJson match {
          case Some(text) => {
            val option: Option[NamedLocation] = Parse.decodeOption[NamedLocation](text.toString())
            Parse.decodeOption[NamedLocation](text.toString()) match {
              case Some(location) => {
                Logger.debug(s"location: $location")
                namedLocationsService.updateLocation(location, request.identity);
                Future.successful(Ok)
              }
              case _ => {
                Logger.debug("Could not parse shit D:")
                Future.successful(BadRequest)
              }
            }
          }
          case _ => {
            Logger.debug("Could not body as text D:")
            Future.successful(BadRequest)
          }
        }
      }
      case _ => {
        Logger.warn(s"User has no location with id $id")
        Future.successful(NotFound)
      }
    }
  }

  def delete(id: Long) = SecuredAction.async { implicit request =>
    Logger.debug(s"Delete by id: $id")
    namedLocationsService.find(request.identity, id) match {
      case Some(location) => {
        namedLocationsService.delete(location)
      }
      case _ => {}
    }
    Future.successful(NoContent)
  }

  def fullCalendarEvents(tsBegin: Long, tsEnd: Long) = SecuredAction.async { implicit request =>
    val begin = new DateTime(tsBegin)
    val end = new DateTime(tsEnd)
    val events = namedLocationsService.loadLocations(request.identity).flatMap { l =>
      geoCoordService.findMatchingIntervals(request.identity, l, new Interval(begin, end)).map { i =>
        Json.obj(
          "title" -> JsString(l.name),
          "allDay" -> JsBoolean(false),
          "editable" -> JsBoolean(false),
          "start" -> JsString(i.getStart.toString),
          "end" -> JsString(i.getEnd.toString)
        )
      }
    }
    Future.successful(Ok(Json.toJson(events)))
  }

  //def details(locationId: Long) = SecuredAction.async { implicit request =>
  //  namedLocationsService.find(request.identity, locationId) match {
  //    case None => Future.successful(Forbidden)
  //    case Some(l) => {
  //      val coords = geoCoordService.findMatchingCoordinates(request.identity, l, new Interval(0, Long.MaxValue))
  //      val intervals = geoCoordService.findMatchingIntervals(request.identity, l, new Interval(0, Long.MaxValue))
  //      Future.successful(Ok(views.html.locationDetails(request.identity, l, coords, intervals)))
  //    }
  //  }
  //}
  //
  //def create = SecuredAction.async { implicit request =>
  //  Future.successful(Ok(views.html.createLocation(request.identity, CreateLocationForm.form)))
  //}
  //
  //def created = SecuredAction.async { implicit request =>
  //  CreateLocationForm.form.bindFromRequest.fold(
  //      withErrors => {
  //        Logger.debug(s"withErrors")
  //      },
  //      form => {
  //        val newNamedLocation = NamedLocation(
  //          None,
  //          form.name,
  //          form.latitude.toDouble,
  //          form.longitude.toDouble,
  //          form.radius.toFloat
  //        )
  //        namedLocationsService.addLocation(newNamedLocation, request.identity)
  //      }
  //  )
  //  Future.successful(Ok(views.html.createLocation(request.identity, CreateLocationForm.form)))
  //}

}
