package models.daos.slick

import java.util.UUID
import models.daos.UserDao
import models.User
import models.NamedLocation
import play.api.Play.current
import play.api.db.slick._
import play.api.db.slick.Config.driver.simple._
import play.api.Logger
import javax.inject.Inject
import models.GeoCoord
import models.daos.GeoCoordDao
import models.daos.slick.GeoCoordSlickDB.DBGeoCoord
import exceptions.NotFoundException
import org.joda.time.DateTime
import org.joda.time.Interval
import models.GeoCoord

class GeoCoordDaoSlick @Inject() (userDao: UserDao) extends GeoCoordDao {

  val slickGeoCoords = GeoCoordSlickDB.slickGeoCoords

  def save(geoCoord: GeoCoord, apiKey: UUID): GeoCoord = {
    DB withSession { implicit session =>
      userDao.findByApiKey(apiKey) match {
        case Some(user) => {
          slickGeoCoords += DBGeoCoord(
              None,
              user.userID.toString,
              geoCoord.latitude,
              geoCoord.longitude,
              geoCoord.altitude,
              geoCoord.accuracy,
              geoCoord.speed,
              geoCoord.time.getMillis
          )
          geoCoord.copy(userId = user.userID)
        }
        case None => {
          throw new NotFoundException
        }
      }
    }
  }

  def load(user: User): List[GeoCoord] = {
    DB withSession { implicit session =>
      slickGeoCoords.filter(_.userId === user.userID.toString).sortBy(_.time.desc).list.map { c =>
        GeoCoord(
          c.id,
          user.userID,
          c.latitude,
          c.longitude,
          c.altitude,
          c.accuracy,
          c.speed,
          new DateTime(c.time)
        )
      }
    }
  }

  def loadLatest(apiKey: UUID): Option[GeoCoord] = {
    DB withSession { implicit session =>
      userDao.findByApiKey(apiKey) match {
        case Some(user) => {
          slickGeoCoords.filter(_.userId === user.userID.toString).sortBy(_.time.desc).firstOption match {
            case Some(c) => {
              Some(GeoCoord(
                c.id,
                user.userID,
                c.latitude,
                c.longitude,
                c.altitude,
                c.accuracy,
                c.speed,
                new DateTime(c.time)
              ))
            }
            case None => None
          }
        }
        case None => {
          throw new NotFoundException
        }
      }
    }
  }

  // http://stackoverflow.com/questions/120283/how-can-i-measure-distance-and-create-a-bounding-box-based-on-two-latitudelongi
  def dist(location: NamedLocation, coord: GeoCoord): Double = {
    val earthRadius = 6371.0
    val diffBetweenLatitudeRadians = Math.toRadians(coord.latitude - location.latitude)
    val diffBetweenLongitudeRadians = Math.toRadians(coord.longitude - location.longitude)
    val latitudeOneInRadians = Math.toRadians(location.latitude)
    val latitudeTwoInRadians = Math.toRadians(coord.latitude)
    val a = Math.sin(diffBetweenLatitudeRadians / 2) * Math.sin(diffBetweenLatitudeRadians / 2) + Math.cos(latitudeOneInRadians) * Math.cos(latitudeTwoInRadians) * Math.sin(diffBetweenLongitudeRadians / 2) * Math.sin(diffBetweenLongitudeRadians / 2)
    val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
    (earthRadius * c) * 1000
  }

  override def findMatchingCoordinates(user: User, location: NamedLocation, interval: Interval): List[GeoCoord] = {
    DB withSession { implicit session =>
      val begin = interval.getStart.getMillis
      val end = interval.getEnd.getMillis
      val userId = user.userID.toString
      val coordsInInterval = slickGeoCoords.filter(
          c => c.userId === userId && c.time >= begin && c.time < end
      ).list.map { c =>
        GeoCoord(
          c.id,
          user.userID,
          c.latitude,
          c.longitude,
          c.altitude,
          c.accuracy,
          c.speed,
          new DateTime(c.time)
        )
      }
      coordsInInterval.filter{
        c => {
          dist(location, c) < location.radius
        }
      }
    }
  }

}
