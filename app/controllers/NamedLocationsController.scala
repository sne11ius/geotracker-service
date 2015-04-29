package controllers

import com.mohiva.play.silhouette.api.{ Environment, LogoutEvent, Silhouette }
import com.mohiva.play.silhouette.impl.authenticators.SessionAuthenticator
import javax.inject.Inject
import services.NamedLocationService
import play.api.mvc.Controller
import scala.concurrent.Future
import models.User
import forms.CreateLocationForm

class NamedLocationsController @Inject() (implicit val env: Environment[User, SessionAuthenticator], val namedLocationsService: NamedLocationService) extends Silhouette[User, SessionAuthenticator] {

  def listLocations = SecuredAction.async { implicit request =>
    val locations = namedLocationsService.loadLocations(request.identity)
    Future.successful(Ok(views.html.listLocations(request.identity, locations)))
  }

  def createLocation = SecuredAction.async { implicit request =>
    Future.successful(Ok(views.html.createLocation(request.identity, CreateLocationForm.form)))
  }

}
