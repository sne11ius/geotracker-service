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

  def listLocations = SecuredAction.async { implicit request =>
    val locations = namedLocationsService.loadLocations(request.identity)
    Future.successful(Ok(Json.toJson(locations)))
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
