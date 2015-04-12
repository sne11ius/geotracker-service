package controllers

import javax.inject.Inject
import com.mohiva.play.silhouette.api.{ Environment, LogoutEvent, Silhouette }
import com.mohiva.play.silhouette.impl.authenticators.SessionAuthenticator
import forms._
import models.User
import scala.concurrent.Future
import play.api.mvc.Controller
import play.api.mvc.Action
import play.api.data._
import play.api.data.Forms._
import services.GeoCoordService
import play.api.Logger
import org.joda.time.DateTime
import models.GeoCoord
import java.util.UUID

/**
 * The basic application controller.
 *
 * @param env The Silhouette environment.
 */
class GeoCoordController @Inject() (val geoCoordService: GeoCoordService) extends Controller {

  def postCoords = Action { implicit request =>
    //Logger.debug("Data:")
    //Logger.debug(request.body.asFormUrlEncoded.get.toString)
    val apiKey = Form("apiKey" -> text).bindFromRequest.fold( hasErrors => { "" }, value => { value } )
    if ("" == apiKey) {
      Logger.debug("No api key")
      Unauthorized
    } else {
      val latitude = Form("latitude" -> text).bindFromRequest.get.toDouble
      val longitude = Form("longitude" -> text).bindFromRequest.get.toDouble
      val altitude = Form("altitude" -> text).bindFromRequest.get.toDouble
      val accuracy = Form("accuracy" -> text).bindFromRequest.get.toFloat
      val speed = Form("speed" -> text).bindFromRequest.get.toFloat
      val time = new DateTime(Form("time" -> text).bindFromRequest.get.toLong)
      //Logger.debug(s"Api key: ${apiKey}")
      //Logger.debug(s"Latitude: ${latitude}")
      //Logger.debug(s"Longitude: ${longitude}")
      //Logger.debug(s"Altitude: ${altitude}")
      //Logger.debug(s"Accuracy: ${accuracy}")
      //Logger.debug(s"Speed: ${speed}")
      //Logger.debug(s"Time: ${time}")
      val geoCoord = GeoCoord(
          None,
          UUID.randomUUID(),
          latitude,
          longitude,
          altitude,
          accuracy,
          speed,
          time
      )
      geoCoordService.save(geoCoord, UUID.fromString(apiKey))
      Ok
    }
  }

}
