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
import play.api.libs.json._
import play.api.libs.json.Json
import argonaut._
import play.api.mvc.Request
import play.api.mvc.AnyContent
import play.api.mvc.Results
import com.mohiva.play.silhouette.api.{ Environment, LogoutEvent, Silhouette }
import com.mohiva.play.silhouette.impl.authenticators.SessionAuthenticator
import org.joda.time.Interval

class GeoCoordController @Inject() (implicit val env: Environment[User, SessionAuthenticator], val geoCoordService: GeoCoordService)
  extends Silhouette[User, SessionAuthenticator] {

  // Using argonaut
  implicit def DecodeGeoCoord: DecodeJson[GeoCoord] =
    DecodeJson(c => for {
      latitude <- (c --\ "latitude").as[Double]
      longitude <- (c --\ "longitude").as[Double]
      altitude <- (c --\ "altitude").as[Double]
      accuracy <- (c --\ "accuracy").as[Float]
      speed <- (c --\ "speed").as[Float]
      time <- (c --\ "time").as[Long]
    } yield GeoCoord(
      None,
      UUID.randomUUID(),
      latitude,
      longitude,
      altitude,
      accuracy,
      speed,
      new DateTime(time)
    ))

  def postCoords = Action { implicit request =>
    request.mediaType match {
      case Some(someType) => {
        someType.mediaSubType match {
          case "json" => {
            request.headers.get("Api-Key") match {
              case Some(apiKey) => {
                request.body.asJson match {
                  case Some(text) => {
                    val option: Option[List[GeoCoord]] = Parse.decodeOption[List[GeoCoord]](text.toString())
                    Parse.decodeOption[List[GeoCoord]](text.toString()) match {
                      case Some(coords) => {
                        //Logger.debug(s"Coords: $coords")
                        try {
                          geoCoordService.save(coords, UUID.fromString(apiKey))
                          Ok
                        } catch {
                          // apiKey might not be a uuid
                          case e: IllegalArgumentException => {
                            Logger.debug(s"Malformed api key: $apiKey")
                            BadRequest
                          }
                        }
                      }
                      case _ => {
                        Logger.debug("Could not parse shit D:")
                        BadRequest
                      }
                    }
                  }
                  case _ => {
                    Logger.debug("Could not body as text D:")
                    BadRequest
                  }
                }
              }
              case _ => {
                Logger.debug("No Api-Key header found")
                Unauthorized
              }
            }
          }
          case _ => {
            val apiKey = Form("apiKey" -> text).bindFromRequest.fold( hasErrors => { "" }, value => { value } ).trim
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
              Logger.debug(s"Api key: '$apiKey'.")
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
      }
      case _ => {
        BadRequest
      }
    }
  }

  implicit val coordWrites = new Writes[GeoCoord] {
    def writes(c: GeoCoord): JsValue = {
      Json.obj(
        "latitude" -> c.latitude,
        "longitude" -> c.longitude,
        "altitude" -> c.altitude,
        "speed" -> c.speed,
        "accuracy" -> c.accuracy,
        "time" -> c.time.getMillis
      )
    }
  }

  def loadCoords(begin: Option[Long], end: Option[Long]) = SecuredAction { implicit request =>
    Logger.debug("loadCoords")
    Logger.debug(s"begin: $begin")
    Logger.debug(s"end: $end")
    val finalBegin = begin match {
      case Some(ts) => new DateTime(ts)
      case _ => new DateTime().withMillisOfDay(0)
    }
    val finalEnd = end match {
      case Some(ts) => new DateTime(ts)
      case _ => new DateTime()
                      .withMillisOfSecond(999)
                      .withSecondOfMinute(59)
                      .withMinuteOfHour(59)
                      .withHourOfDay(23)
    }
    if (!finalEnd.isAfter(finalBegin)) {
      BadRequest
    } else {
      val coords = geoCoordService.load(request.identity, new Interval(finalBegin, finalEnd))
      Ok(Json.toJson(coords))
    }
  }

  def loadLatest = UserAwareAction { implicit request =>
    var apiKey = Form("apiKey" -> text).bindFromRequest.fold( hasErrors => { "" }, value => { value } )
    if ("" == apiKey) {
      apiKey = request.identity match {
        case Some(user) => {
          user.apiKey.toString
        }
        case _ => ""
      }
    }
    if ("" == apiKey) {
      Logger.debug("No api key")
      Unauthorized
    } else {
      val coord = geoCoordService.loadLatest(UUID.fromString(apiKey))
      Logger.debug(s"Api key: $apiKey")
      Logger.debug(s"Latest: $coord")
      Ok(Json.toJson(coord))
    }
  }

}
