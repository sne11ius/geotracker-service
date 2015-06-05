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

class CalendarController @Inject() (implicit val env: Environment[User, SessionAuthenticator], val namedLocationsService: NamedLocationService, geoCoordService: GeoCoordService) extends Silhouette[User, SessionAuthenticator] {

  def calendar() = SecuredAction.async { implicit request =>
    Future.successful(Ok(views.html.calendar()))
  }

}
