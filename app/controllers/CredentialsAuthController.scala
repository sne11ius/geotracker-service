package controllers

import javax.inject.Inject
import com.mohiva.play.silhouette.api.Silhouette
import com.mohiva.play.silhouette.api.Environment
import com.mohiva.play.silhouette.api.LoginEvent
import com.mohiva.play.silhouette.api.exceptions.{ConfigurationException, ProviderException}
import com.mohiva.play.silhouette.api.services.AuthInfoService
import com.mohiva.play.silhouette.impl.authenticators.SessionAuthenticator
import com.mohiva.play.silhouette.impl.exceptions.IdentityNotFoundException
import com.mohiva.play.silhouette.impl.providers._
import forms.SignInForm
import models.User
import services.UserService
import play.api.i18n.Messages
import play.api.libs.concurrent.Execution.Implicits._
import play.api.mvc.Action
import scala.concurrent.Future
import play.api.Logger

import com.jcabi.manifests.Manifests
import models.ManifestInfo

import play.api.data.Form
import play.api.data.Forms._

/**
 * The credentials auth controller.
 *
 * @param env The Silhouette environment.
 */
class CredentialsAuthController @Inject() (
  implicit val env: Environment[User, SessionAuthenticator],
  val userService: UserService,
  val authInfoService: AuthInfoService)
  extends Silhouette[User, SessionAuthenticator] {

  var manifestInfo = ManifestInfo("branch", "date", "rev")
  try {
    manifestInfo = ManifestInfo(
      Manifests.read("Git-Branch"),
      Manifests.read("Git-Build-Date"),
      Manifests.read("Git-Head-Rev")
    )
  } catch {
    case e: Exception => {}
  }

  /**
   * Authenticates a user against the credentials provider.
   *
   * @return The result to display.
   */
  def authenticate = Action.async { implicit request =>
    SignInForm.form.bindFromRequest.fold(
      form => {
        Future.successful(BadRequest(views.html.signIn(form, manifestInfo)))
      },
      credentials => (env.providers.get(CredentialsProvider.ID) match {
        case Some(p: CredentialsProvider) => p.authenticate(credentials)
        case _ => Future.failed(new ConfigurationException(s"Cannot find credentials provider"))
      }).flatMap { loginInfo =>
        val result = Future.successful(Redirect(routes.ApplicationController.index()))
        userService.retrieve(loginInfo).flatMap {
          case Some(user) => env.authenticatorService.create(loginInfo).flatMap { authenticator =>
            env.eventBus.publish(LoginEvent(user, request, request2lang))
            env.authenticatorService.init(authenticator).flatMap(v => env.authenticatorService.embed(v, result))
          }
          case None => Future.failed(new IdentityNotFoundException("Couldn't find user"))
        }
      }.recover {
        case e: ProviderException =>
          Logger.debug("Error: " + Messages("invalid.credentials"))
          Redirect(routes.ApplicationController.signIn()).flashing("error" -> Messages("invalid.credentials"))
      }
    )
  }
}
