package controllers

import javax.inject.Inject
import com.mohiva.play.silhouette.api.{ Environment, LogoutEvent, Silhouette }
import com.mohiva.play.silhouette.impl.authenticators.SessionAuthenticator
import forms._
import models.User
import scala.concurrent.Future
import services.GeoCoordService
import play.api.mvc.Action
import com.jcabi.manifests.Manifests
import play.api.libs.json.Json

import models.ManifestInfo

/**
 * The basic application controller.
 *
 * @param env The Silhouette environment.
 */
class ApplicationController @Inject() (implicit val env: Environment[User, SessionAuthenticator], geoCoordService: GeoCoordService)
  extends Silhouette[User, SessionAuthenticator] {

  def user = UserAwareAction.async { implicit request =>
    request.identity match {
      case Some(user) => {
        Future.successful(Ok(Json.toJson(request.identity)))
      }
      case _ => {
        Future.successful(Unauthorized(Json.toJson("{\"error\": \"You are not logged in.\"}")))
      }
    }
  }

  def index = UserAwareAction.async { implicit request =>
    request.identity match {
      case Some(user) => {
        Future.successful(Redirect("/mit/geotracker-service/ui"))
      }
      case _ => {
        Future.successful(Ok(views.html.signIn(SignInForm.form)))
      }
    }
    //val latest = geoCoordService.loadLatest(request.identity.apiKey)
    //Future.successful(Ok(views.html.home(request.identity, latest)))
  }
//
//  def about = UserAwareAction { implicit request =>
//    var manifestInfo = ManifestInfo("branch", "date", "rev")
//    try {
//      manifestInfo = ManifestInfo(
//        Manifests.read("Git-Branch"),
//        Manifests.read("Git-Build-Date"),
//        Manifests.read("Git-Head-Rev")
//      )
//    } catch {
//      case e: Exception => {}
//    }
//    Ok(views.html.about(manifestInfo, request.identity))
//  }
//
    def signIn = UserAwareAction.async { implicit request =>
      request.identity match {
        case Some(user) => Future.successful(Redirect(routes.ApplicationController.index()))
        case None => Future.successful(Ok(views.html.signIn(SignInForm.form)))
      }
    }
//
//  /**
//   * Handles the Sign Up action.
//   *
//   * @return The result to display.
//   */
//  def signUp = UserAwareAction.async { implicit request =>
//    request.identity match {
//      case Some(user) => Future.successful(Redirect(routes.ApplicationController.index()))
//      case None => Future.successful(Ok(views.html.signUp(SignUpForm.form)))
//    }
//  }
//
//  /**
//   * Handles the Sign Out action.
//   *
//   * @return The result to display.
//   */
//  def signOut = SecuredAction.async { implicit request =>
//    val result = Future.successful(Redirect(routes.ApplicationController.index()))
//    env.eventBus.publish(LogoutEvent(request.identity, request, request2lang))
//
//    request.authenticator.discard(result)
//  }
  def view(template: String) = UserAwareAction { implicit request =>
    //template match {
    //  case "home" => Ok(views.html.home())
    //  case "signUp" => Ok(views.html.signUp())
    //  case "signIn" => Ok(views.html.signIn())
    //  case "navigation" => Ok(views.html.navigation.render())
    //  case _ => NotFound
    //}
    Ok
  }
}
