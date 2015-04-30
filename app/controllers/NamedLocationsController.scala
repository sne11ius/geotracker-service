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

class NamedLocationsController @Inject() (implicit val env: Environment[User, SessionAuthenticator], val namedLocationsService: NamedLocationService, geoCoordService: GeoCoordService) extends Silhouette[User, SessionAuthenticator] {

  def listLocations = SecuredAction.async { implicit request =>
    val locations = namedLocationsService.loadLocations(request.identity)
    Future.successful(Ok(views.html.listLocations(request.identity, locations)))
  }

  def details(locationId: Long) = SecuredAction.async { implicit request =>
    namedLocationsService.find(request.identity, locationId) match {
      case None => Future.successful(Forbidden)
      case Some(l) => {
        val coords = geoCoordService.findMatchingCoordinates(request.identity, l, new Interval(0, Long.MaxValue))
        Future.successful(Ok(views.html.locationDetails(request.identity, l, coords)))
      }
    }
  }

  def create = SecuredAction.async { implicit request =>
    Future.successful(Ok(views.html.createLocation(request.identity, CreateLocationForm.form)))
  }

  def created = SecuredAction.async { implicit request =>
    CreateLocationForm.form.bindFromRequest.fold(
        withErrors => {
          Logger.debug(s"withErrors")
        },
        form => {
          val newNamedLocation = NamedLocation(
            None,
            form.name,
            form.latitude.toDouble,
            form.longitude.toDouble,
            form.radius.toFloat
          )
          namedLocationsService.addLocation(newNamedLocation, request.identity)
        }
    )
    Future.successful(Ok(views.html.createLocation(request.identity, CreateLocationForm.form)))
  }

}
